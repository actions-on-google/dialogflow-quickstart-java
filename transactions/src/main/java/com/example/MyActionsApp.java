/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.DeliveryAddress;
import com.google.actions.api.response.helperintent.SignIn;
import com.google.actions.api.response.helperintent.transactions.v3.TransactionDecision;
import com.google.actions.api.response.helperintent.transactions.v3.TransactionRequirements;
import com.google.api.services.actions_fulfillment.v2.model.Action;
import com.google.api.services.actions_fulfillment.v2.model.Argument;
import com.google.api.services.actions_fulfillment.v2.model.DeliveryAddressValueSpecAddressOptions;
import com.google.api.services.actions_fulfillment.v2.model.GooglePaymentOption;
import com.google.api.services.actions_fulfillment.v2.model.LineItemV3;
import com.google.api.services.actions_fulfillment.v2.model.Location;
import com.google.api.services.actions_fulfillment.v2.model.MerchantPaymentMethod;
import com.google.api.services.actions_fulfillment.v2.model.MerchantPaymentOption;
import com.google.api.services.actions_fulfillment.v2.model.MerchantUnitMeasure;
import com.google.api.services.actions_fulfillment.v2.model.MerchantV3;
import com.google.api.services.actions_fulfillment.v2.model.MoneyV3;
import com.google.api.services.actions_fulfillment.v2.model.OpenUrlAction;
import com.google.api.services.actions_fulfillment.v2.model.OrderContents;
import com.google.api.services.actions_fulfillment.v2.model.OrderOptionsV3;
import com.google.api.services.actions_fulfillment.v2.model.OrderUpdateV3;
import com.google.api.services.actions_fulfillment.v2.model.OrderV3;
import com.google.api.services.actions_fulfillment.v2.model.PaymentMethodDisplayInfo;
import com.google.api.services.actions_fulfillment.v2.model.PaymentMethodStatus;
import com.google.api.services.actions_fulfillment.v2.model.PaymentParameters;
import com.google.api.services.actions_fulfillment.v2.model.PresentationOptionsV3;
import com.google.api.services.actions_fulfillment.v2.model.PriceAttribute;
import com.google.api.services.actions_fulfillment.v2.model.PromotionV3;
import com.google.api.services.actions_fulfillment.v2.model.PurchaseFulfillmentInfo;
import com.google.api.services.actions_fulfillment.v2.model.PurchaseItemExtension;
import com.google.api.services.actions_fulfillment.v2.model.PurchaseItemExtensionItemOption;
import com.google.api.services.actions_fulfillment.v2.model.PurchaseOrderExtension;
import com.google.api.services.actions_fulfillment.v2.model.PurchaseReturnsInfo;
import com.google.api.services.actions_fulfillment.v2.model.SimpleResponse;
import com.google.api.services.actions_fulfillment.v2.model.StructuredResponse;
import com.google.api.services.actions_fulfillment.v2.model.TimeV3;
import com.google.api.services.actions_fulfillment.v2.model.UserInfo;
import com.google.api.services.actions_fulfillment.v2.model.UserInfoOptions;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyActionsApp extends DialogflowApp {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyActionsApp.class);
  private static final String SERVICE_ACCOUNT_KEY_FILE_NAME = "service-account.json";
  private static final GsonBuilder GSON_BUILDER;
  private static final LocationDeserializer LOCATION_DESERIALIZER;
  static {
    LOCATION_DESERIALIZER = new LocationDeserializer();
    GSON_BUILDER = new GsonBuilder();
    GSON_BUILDER.registerTypeAdapter(Location.class, LOCATION_DESERIALIZER);
  }

  @ForIntent("Default Welcome Intent")
  public ActionResponse welcome(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    String welcome = "Hey there! I can help you go through a transaction " +
        "with Google Pay and Merchant-managed payments.";
    return responseBuilder
        .add(new SimpleResponse()
            .setTextToSpeech(welcome)
            .setDisplayText(welcome))
        .addSuggestions(new String[] {
            "Merchant Transaction",
            "Google Pay Transaction"
        }).build();
  }

  // [START sign_in_df]
  @ForIntent("Sign In")
  public ActionResponse signIn(ActionRequest request) {
    return getResponseBuilder(request).add(
        new SignIn()
            .setContext("To get your account details"))
        .build();
  }
  // [END sign_in_df]

  // [START get_sign_in_status_df]
  @ForIntent("Sign In Complete")
  public ActionResponse signInComplete(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (request.isSignInGranted()) {
      responseBuilder
          .add("You must meet all the requirements necessary to make a " +
              "transaction. Try saying \"check transaction requirements\".")
          .addSuggestions(new String[] {
              "check requirements"
          });
    } else {
      responseBuilder.add("You need to sign in before making a transaction.");
    }
    return responseBuilder.build();
  }
  // [END get_sign_in_status_df]

  @ForIntent("Transaction Check Merchant")
  public ActionResponse transactionCheckMerchant(ActionRequest request) {
    // [START transaction_check_df]
    return getResponseBuilder(request)
        .add(new TransactionRequirements())
        .build();
    // [END transaction_check_df]
  }

  @ForIntent("Transaction Check Google")
  public ActionResponse transactionCheckGoogle(ActionRequest request) {
    return getResponseBuilder(request)
        .add(new TransactionRequirements())
        .build();
  }

  // [START transaction_check_complete_df]
  @ForIntent("Transaction Check Complete")
  public ActionResponse transactionCheckComplete(ActionRequest request) {
    LOGGER.info("Checking Transaction Requirements Result.");

    // Check result of transaction requirements check
    Argument transactionCheckResult = request
        .getArgument("TRANSACTION_REQUIREMENTS_CHECK_RESULT");
    boolean result = false;
    if (transactionCheckResult != null) {
      Map<String, Object> map = transactionCheckResult.getExtension();
      if (map != null) {
        String resultType = (String) map.get("resultType");
        result = resultType != null && resultType.equals("CAN_TRANSACT");
      }
    }

    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (result) {
      // Normally take the user through cart building flow
      responseBuilder
          .add("Looks like you're good to go! Next " +
              "I'll need your delivery address. Try saying " +
              "\"get delivery address\".")
          .addSuggestions(new String[]{"get delivery address"});
    } else {
      // Exit conversation
      responseBuilder.add("Transaction failed.");
    }
    return responseBuilder.build();
  }
  // [END transaction_check_complete_df]

  // [START delivery_address_df]
  @ForIntent("Delivery Address")
  public ActionResponse deliveryAddress(ActionRequest request) {
    DeliveryAddressValueSpecAddressOptions addressOptions =
        new DeliveryAddressValueSpecAddressOptions()
            .setReason("To know where to send the order");
    return getResponseBuilder(request)
        .add(new DeliveryAddress()
            .setAddressOptions(addressOptions))
        .build();
  }
  // [END delivery_address_df]

  // [START delivery_address_complete_df]
  @ForIntent("Delivery Address Complete")
  public ActionResponse deliveryAddressComplete(ActionRequest request) {
    Argument deliveryAddressValue = request.getArgument("DELIVERY_ADDRESS_VALUE");
    Location deliveryAddress = null;
    if (deliveryAddressValue != null) {
      Map<String, Object> map = deliveryAddressValue.getExtension();
      if (map != null) {
        String userDecision = (String) map.get("userDecision");
        Location location = (Location) map.get("location");
        deliveryAddress = userDecision != null && userDecision.equals("ACCEPTED") ? location : null;
      }
    }
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (deliveryAddress != null) {
      // Cache delivery address in conversation data for later use
      Map<String, Object> conversationData = request.getConversationData();
      conversationData.put("location",
          GSON_BUILDER.create().toJson(deliveryAddress, Location.class));
      responseBuilder
          .add("Great, got your address! Now say \"confirm transaction\".")
          .addSuggestions(new String[] {
              "confirm transaction"
          });
    } else {
      responseBuilder.add("Transaction failed.").endConversation();
    }
    return responseBuilder.build();
  }
  // [END delivery_address_complete_df]

  @ForIntent("Transaction Decision")
  public ActionResponse transactionDecision(ActionRequest request) {
    LOGGER.info("Checking Transaction Decision.");

    String orderId = generateRandomOrderId();
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("latestOrderId", orderId);

    // Build the Order
    // [START build_order_df]
    // Transaction Merchant
    MerchantV3 transactionMerchant = new MerchantV3()
        .setId("http://www.example.com")
        .setName("Example Merchant");

    // Line Item
    PriceAttribute itemPrice = new PriceAttribute()
        .setType("REGULAR")
        .setName("Item Price")
        .setState("ACTUAL")
        .setAmount(new MoneyV3()
            .setCurrencyCode("USD")
            .setAmountInMicros(8990000L)
        )
        .setTaxIncluded(true);

    PriceAttribute totalItemPrice = new PriceAttribute()
        .setType("TOTAL")
        .setName("Total Price")
        .setState("ACTUAL")
        .setAmount(new MoneyV3()
            .setCurrencyCode("USD")
            .setAmountInMicros(9990000L)
        )
        .setTaxIncluded(true);

    // Purchase Item Extension
    PurchaseItemExtension purchaseItemExtension = new PurchaseItemExtension()
        .setQuantity(1)
        .setUnitMeasure(new MerchantUnitMeasure()
            .setMeasure(1.0)
            .setUnit("POUND"))
        .setItemOptions(Arrays.asList(new PurchaseItemExtensionItemOption()
            .setId("ITEM_OPTION_ID")
            .setName("Pepperoni")
            .setPrices(Arrays.asList(
                new PriceAttribute()
                  .setType("REGULAR")
                  .setState("ACTUAL")
                  .setName("Item Price")
                  .setAmount(new MoneyV3()
                      .setCurrencyCode("USD")
                      .setAmountInMicros(1000000L))
                  .setTaxIncluded(true),
                new PriceAttribute()
                    .setType("TOTAL")
                    .setState("ACTUAL")
                    .setName("Total Price")
                    .setAmount(new MoneyV3()
                        .setCurrencyCode("USD")
                        .setAmountInMicros(1000000L))
                    .setTaxIncluded(true)
                ))
            .setNote("Extra pepperoni")
            .setQuantity(1)));

    LineItemV3 lineItem = new LineItemV3()
        .setId("LINE_ITEM_ID")
        .setName("Pizza")
        .setDescription("A four cheese pizza.")
        .setPriceAttributes(Arrays.asList(itemPrice, totalItemPrice))
        .setNotes(Collections.singletonList("Extra cheese."))
        .setPurchase(purchaseItemExtension);

    // Order Contents
    OrderContents contents = new OrderContents()
        .setLineItems(Collections.singletonList(lineItem));

    // User Info
    UserInfo buyerInfo = new UserInfo()
        .setEmail("janedoe@gmail.com")
        .setFirstName("Jane")
        .setLastName("Doe")
        .setDisplayName("Jane Doe");

    // Price Attributes
    PriceAttribute subTotal = new PriceAttribute()
        .setType("SUBTOTAL")
        .setName("Subtotal")
        .setState("ESTIMATE")
        .setAmount(new MoneyV3()
            .setCurrencyCode("USD")
            .setAmountInMicros(9990000L)
        )
        .setTaxIncluded(true);
    PriceAttribute deliveryFee = new PriceAttribute()
        .setType("DELIVERY")
        .setName("Delivery")
        .setState("ACTUAL")
        .setAmount(new MoneyV3()
            .setCurrencyCode("USD")
            .setAmountInMicros(2000000L)
        )
        .setTaxIncluded(true);
    PriceAttribute tax = new PriceAttribute()
        .setType("TAX")
        .setName("Tax")
        .setState("ESTIMATE")
        .setAmount(new MoneyV3()
            .setCurrencyCode("USD")
            .setAmountInMicros(3780000L)
        )
        .setTaxIncluded(true);
    PriceAttribute totalPrice = new PriceAttribute()
        .setType("TOTAL")
        .setName("Total Price")
        .setState("ESTIMATE")
        .setAmount(new MoneyV3()
            .setCurrencyCode("USD")
            .setAmountInMicros(15770000L)
        )
        .setTaxIncluded(true);

    // Follow up actions
    Action viewDetails = new Action()
        .setType("VIEW_DETAILS")
        .setTitle("View details")
        .setOpenUrlAction(new OpenUrlAction()
            .setUrl("https://example.com"));
    Action call = new Action()
        .setType("CALL")
        .setTitle("Call us")
        .setOpenUrlAction(new OpenUrlAction()
            .setUrl("tel:+16501112222"));
    Action email = new Action()
        .setType("EMAIL")
        .setTitle("Email us")
        .setOpenUrlAction(new OpenUrlAction()
            .setUrl("mailto:person@example.com"));

    // Terms of service and order note
    String termsOfServiceUrl = "http://example.com";
    String orderNote = "Sale event";

    // Promotions
    PromotionV3 promotion = new PromotionV3()
        .setCoupon("COUPON_CODE");

    // Purchase Order Extension
    Location location = GSON_BUILDER.create().fromJson(
        (String) conversationData.get("location"), Location.class);

    PurchaseOrderExtension purchaseOrderExtension = new PurchaseOrderExtension()
        .setStatus("CREATED")
        .setUserVisibleStatusLabel("CREATED")
        .setType("FOOD")
        .setReturnsInfo(new PurchaseReturnsInfo()
            .setIsReturnable(false)
            .setDaysToReturn(1)
            .setPolicyUrl("https://example.com"))
        .setFulfillmentInfo(new PurchaseFulfillmentInfo()
            .setId("FULFILLMENT_SERVICE_ID")
            .setFulfillmentType("DELIVERY")
            .setExpectedFulfillmentTime(new TimeV3()
                .setTimeIso8601("2019-09-25T18:00:00.877Z"))
            .setLocation(location)
            .setPrice(new PriceAttribute()
                .setType("REGULAR")
                .setName("Delivery price")
                .setState("ACTUAL")
                .setAmount(new MoneyV3()
                    .setCurrencyCode("USD")
                    .setAmountInMicros(2000000L))
                .setTaxIncluded(true))
            .setFulfillmentContact(new UserInfo()
                .setEmail("johnjohnson@gmail.com")
                .setFirstName("John")
                .setLastName("Johnson")
                .setDisplayName("John Johnson")))
        .setPurchaseLocationType("ONLINE_PURCHASE");

    OrderV3 order = new OrderV3()
        .setCreateTime("2019-09-24T18:00:00.877Z")
        .setLastUpdateTime("2019-09-24T18:00:00.877Z")
        .setMerchantOrderId(orderId)
        .setUserVisibleOrderId(orderId)
        .setTransactionMerchant(transactionMerchant)
        .setContents(contents)
        .setBuyerInfo(buyerInfo)
        .setPriceAttributes(Arrays.asList(
            subTotal,
            deliveryFee,
            tax,
            totalPrice
        ))
        .setFollowUpActions(Arrays.asList(
            viewDetails,
            call,
            email
        ))
        .setTermsOfServiceUrl(termsOfServiceUrl)
        .setNote(orderNote)
        .setPromotions(Collections.singletonList(promotion))
        .setPurchase(purchaseOrderExtension);
    // [END build_order_df]

    // Create payment parameters
    PaymentParameters paymentParameters = new PaymentParameters();
    if (request.getContext("google_payment") != null) {
      // [START ask_for_transaction_decision_google_payment_df]
      // Create order options
      OrderOptionsV3 orderOptions = new OrderOptionsV3()
          .setUserInfoOptions(new UserInfoOptions()
              .setUserInfoProperties(Collections.singletonList("EMAIL")));

      // Create presentation options
      PresentationOptionsV3 presentationOptions = new PresentationOptionsV3()
          .setActionDisplayName("PLACE_ORDER");

      // Create payment parameters
      JSONObject merchantInfo = new JSONObject();
      merchantInfo.put("merchantName", "Example Merchant");

      JSONObject facilitationSpec = new JSONObject();
      facilitationSpec.put("apiVersion", 2);
      facilitationSpec.put("apiVersionMinor", 0);
      facilitationSpec.put("merchantInfo", merchantInfo);

      JSONObject allowedPaymentMethod = new JSONObject();
      allowedPaymentMethod.put("type", "CARD");

      JSONArray allowedAuthMethods = new JSONArray();
      allowedAuthMethods.addAll(Arrays.asList("PAN_ONLY", "CRYPTOGRAM_3DS"));
      JSONArray allowedCardNetworks = new JSONArray();
      allowedCardNetworks.addAll(Arrays.asList("AMEX", "DISCOVER", "JCB", "MASTERCARD", "VISA"));

      JSONObject allowedPaymentMethodParameters = new JSONObject();
      allowedPaymentMethodParameters.put("allowedAuthMethods", allowedAuthMethods);
      allowedPaymentMethodParameters.put("allowedCardNetworks", allowedCardNetworks);

      allowedPaymentMethod.put("parameters", allowedPaymentMethodParameters);

      JSONObject tokenizationSpecificationParameters = new JSONObject();
      tokenizationSpecificationParameters.put("gateway", "example");
      tokenizationSpecificationParameters.put("gatewayMerchantId", "exampleGatewayMerchantId");

      JSONObject tokenizationSpecification = new JSONObject();
      tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
      tokenizationSpecification.put("parameters", tokenizationSpecificationParameters);
      allowedPaymentMethod.put("tokenizationSpecification", tokenizationSpecification);

      JSONArray allowedPaymentMethods = new JSONArray();
      allowedPaymentMethods.add(allowedPaymentMethod);

      facilitationSpec.put("allowedPaymentMethods", allowedPaymentMethods);

      JSONObject transactionInfo = new JSONObject();
      transactionInfo.put("totalPriceStatus", "FINAL");
      transactionInfo.put("totalPrice", "15.77");
      transactionInfo.put("currencyCode", "USD");

      facilitationSpec.put("transactionInfo", transactionInfo);

      GooglePaymentOption googlePaymentOption = new GooglePaymentOption()
          .setFacilitationSpec(facilitationSpec.toJSONString());
      paymentParameters.setGooglePaymentOption(googlePaymentOption);

      return getResponseBuilder(request)
          .add(new TransactionDecision()
              .setOrder(order)
              .setOrderOptions(orderOptions)
              .setPresentationOptions(presentationOptions)
              .setPaymentParameters(paymentParameters)
          )
          .build();
      // [END ask_for_transaction_decision_google_payment_df]
    } else {
      // [START ask_for_transaction_decision_merchant_payment_df]
      // Create order options
      OrderOptionsV3 orderOptions = new OrderOptionsV3()
          .setUserInfoOptions(new UserInfoOptions()
              .setUserInfoProperties(Collections.singletonList("EMAIL")));

      // Create presentation options
      PresentationOptionsV3 presentationOptions = new PresentationOptionsV3()
          .setActionDisplayName("PLACE_ORDER");

      // Create payment parameters
      MerchantPaymentMethod merchantPaymentMethod = new MerchantPaymentMethod()
          .setPaymentMethodDisplayInfo(new PaymentMethodDisplayInfo()
              .setPaymentMethodDisplayName("VISA **** 1234")
              .setPaymentType("PAYMENT_CARD"))
          .setPaymentMethodGroup("Payment method group")
          .setPaymentMethodId("12345678")
          .setPaymentMethodStatus(new PaymentMethodStatus()
              .setStatus("STATUS_OK")
              .setStatusMessage("Status message"));

      MerchantPaymentOption merchantPaymentOption = new MerchantPaymentOption()
          .setDefaultMerchantPaymentMethodId("12345678")
          .setManagePaymentMethodUrl("https://example.com/managePayment")
          .setMerchantPaymentMethod(Collections.singletonList(merchantPaymentMethod));

      paymentParameters.setMerchantPaymentOption(merchantPaymentOption);

      return getResponseBuilder(request)
          .add(new TransactionDecision()
              .setOrder(order)
              .setOrderOptions(orderOptions)
              .setPresentationOptions(presentationOptions)
              .setPaymentParameters(paymentParameters)
          )
          .build();
      // [END ask_for_transaction_decision_merchant_payment_df]
    }
  }

  // Check result of asking to perform transaction / place order
  @ForIntent("Transaction Decision Complete")
  public ActionResponse transactionDecisionComplete(ActionRequest request) {

    // [START get_transaction_decision_df]
    // Check transaction decision value
    Argument transactionDecisionValue = request
        .getArgument("TRANSACTION_DECISION_VALUE");
    Map<String, Object> extension = null;
    if (transactionDecisionValue != null) {
      extension = transactionDecisionValue.getExtension();
    }

    String transactionDecision = null;
    if (extension != null) {
      transactionDecision = (String) extension.get("transactionDecision");
    }
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if ((transactionDecision != null && transactionDecision.equals("ORDER_ACCEPTED"))) {
      OrderV3 order = ((OrderV3) extension.get("order"));
    }
    // [END get_transaction_decision_df]
    if ((transactionDecision != null && transactionDecision.equals("ORDER_ACCEPTED"))) {
      // [START create_order_df]
      OrderV3 order = ((OrderV3) extension.get("order"));
      order.setLastUpdateTime("2019-09-24T19:00:00.877Z");

      // Update order status
      PurchaseOrderExtension purchaseOrderExtension = order.getPurchase();
      purchaseOrderExtension.setStatus("CONFIRMED");
      purchaseOrderExtension.setUserVisibleStatusLabel("Order confirmed");
      order.setPurchase(purchaseOrderExtension);

      // Order update
      OrderUpdateV3 orderUpdate = new OrderUpdateV3()
          .setType("SNAPSHOT")
          .setReason("Reason string")
          .setOrder(order);

      Map<String, Object> conversationData = request.getConversationData();
      String orderId = (String) conversationData.get("latestOrderId");
      responseBuilder
          .add("Transaction completed! Your order " + orderId + " is all set!")
          .addSuggestions(new String[] {"send order update"})
          .add(new StructuredResponse().setOrderUpdateV3(orderUpdate));
      // [END create_order_df]
    }
    else {
      responseBuilder.add("Transaction failed.").endConversation();
    }
    return responseBuilder.build();
  }

  @ForIntent("Send Order Update")
  public ActionResponse sendOrderUpdate(ActionRequest request)
      throws IOException {
    Map<String, Object> conversationData = request.getConversationData();
    String orderId = (String) conversationData.get("latestOrderId");
    // [START order_update]
    // Setup service account credentials
    String serviceAccountFile = MyActionsApp.class.getClassLoader()
        .getResource(SERVICE_ACCOUNT_KEY_FILE_NAME)
        .getFile();
    InputStream actionsApiServiceAccount = new FileInputStream(
        serviceAccountFile);
    ServiceAccountCredentials serviceAccountCredentials = (ServiceAccountCredentials)
        ServiceAccountCredentials.fromStream(actionsApiServiceAccount)
            .createScoped(Collections.singleton(
                "https://www.googleapis.com/auth/actions.order.developer"));
    AccessToken token = serviceAccountCredentials.refreshAccessToken();

    // Setup request with headers
    HttpPatch patchRequest = new HttpPatch(
        "https://actions.googleapis.com/v3/orders/" + orderId);
    patchRequest.setHeader("Content-type", "application/json");
    patchRequest.setHeader("Authorization", "Bearer " + token.getTokenValue());

    // Create order update
    FieldMask fieldMask = FieldMask.newBuilder().addAllPaths(Arrays.asList(
        "lastUpdateTime",
        "purchase.status",
        "purchase.userVisibleStatusLabel"))
        .build();

    OrderUpdateV3 orderUpdate = new OrderUpdateV3()
        .setOrder(new OrderV3()
            .setMerchantOrderId(orderId)
            .setLastUpdateTime(Instant.now().toString())
            .setPurchase(new PurchaseOrderExtension()
                .setStatus("DELIVERED")
                .setUserVisibleStatusLabel("Order delivered.")))
        .setUpdateMask(FieldMaskUtil.toString(fieldMask))
        .setReason("Order status was updated to delivered.");

    // Setup JSON body containing order update
    JsonParser parser = new JsonParser();
    JsonObject orderUpdateJson =
        parser.parse(new Gson().toJson(orderUpdate)).getAsJsonObject();
    JsonObject body = new JsonObject();
    body.add("orderUpdate", orderUpdateJson);
    JsonObject header = new JsonObject();
    header.addProperty("isInSandbox", true);
    body.add("header", header);
    StringEntity entity = new StringEntity(body.toString());
    entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    patchRequest.setEntity(entity);

    // Make request
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponse response = httpClient.execute(patchRequest);
    LOGGER.info(response.getStatusLine().getStatusCode() + " " + response
        .getStatusLine().getReasonPhrase());

    return getResponseBuilder(request)
        .add("The order has been updated.")
        .build();
    // [END order_update]
  }

  private static String generateRandomOrderId() {
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    for (int i = 0; i < 6; i++) {
      sb.append(validCharacters.charAt(random.nextInt(validCharacters.length())));
    }
    return sb.toString();
  }
}
