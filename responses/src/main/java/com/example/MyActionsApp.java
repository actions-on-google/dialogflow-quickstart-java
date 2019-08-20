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
import com.google.actions.api.Capability;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.actions.api.response.helperintent.SelectionList;
import com.google.api.services.actions_fulfillment.v2.model.BasicCard;
import com.google.api.services.actions_fulfillment.v2.model.Button;
import com.google.api.services.actions_fulfillment.v2.model.CarouselBrowse;
import com.google.api.services.actions_fulfillment.v2.model.CarouselBrowseItem;
import com.google.api.services.actions_fulfillment.v2.model.CarouselSelectCarouselItem;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.LinkOutSuggestion;
import com.google.api.services.actions_fulfillment.v2.model.ListSelectListItem;
import com.google.api.services.actions_fulfillment.v2.model.MediaObject;
import com.google.api.services.actions_fulfillment.v2.model.MediaResponse;
import com.google.api.services.actions_fulfillment.v2.model.OpenUrlAction;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;
import com.google.api.services.actions_fulfillment.v2.model.SimpleResponse;
import com.google.api.services.actions_fulfillment.v2.model.TableCard;
import com.google.api.services.actions_fulfillment.v2.model.TableCardCell;
import com.google.api.services.actions_fulfillment.v2.model.TableCardColumnProperties;
import com.google.api.services.actions_fulfillment.v2.model.TableCardRow;
import java.util.ArrayList;
import java.util.Arrays;

public class MyActionsApp extends DialogflowApp {

  // [START df_java_simple_response]
  @ForIntent("Simple Response")
  public ActionResponse welcome(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(
        new SimpleResponse()
            .setTextToSpeech(
                "Here's an example of a simple response. "
                    + "Which type of response would you like to see next?")
            .setDisplayText(
                "Here's a simple response. " + "Which response would you like to see next?"));
    return responseBuilder.build();
  }
  // [END df_java_simple_response]

  // [START df_java_ssml_demo]
  @ForIntent("SSML")
  public ActionResponse ssml(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(
        "<speak>"
            + "Here are <say-as interpet-as=\"characters\">SSML</say-as> examples."
            + "Here is a buzzing fly "
            + "<audio src=\"https://actions.google.com/sounds/v1/animals/buzzing_fly.ogg\"></audio>"
            + "and here's a short pause <break time=\"800ms\"/>"
            + "</speak>");
    return responseBuilder.build();
  }
  // [END df_java_ssml_demo]

  // [START df_java_basic_card]
  @ForIntent("Basic Card")
  public ActionResponse basicCard(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      return responseBuilder
          .add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
          .add("Which response would you like to see next?")
          .build();
    }

    // Prepare formatted text for card
    String text =
        "This is a basic card.  Text in a basic card can include \"quotes\" and\n"
            + "  most other unicode characters including emoji \uD83D\uDCF1. Basic cards also support\n"
            + "  some markdown formatting like *emphasis* or _italics_, **strong** or\n"
            + "  __bold__, and ***bold itallic*** or ___strong emphasis___ as well as other\n"
            + "  things like line  \\nbreaks"; // Note the two spaces before '\n' required for
    // a line break to be rendered in the card.
    responseBuilder
        .add("Here's an example of a basic card.")
        .add(
            new BasicCard()
                .setTitle("Title: this is a title")
                .setSubtitle("This is a subtitle")
                .setFormattedText(text)
                .setImage(
                    new Image()
                        .setUrl(
                            "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                        .setAccessibilityText("Image alternate text"))
                .setImageDisplayOptions("CROPPED")
                .setButtons(
                    new ArrayList<Button>(
                        Arrays.asList(
                            new Button()
                                .setTitle("This is a Button")
                                .setOpenUrlAction(
                                    new OpenUrlAction().setUrl("https://assistant.google.com"))))))
        .add("Which response would you like to see next?");

    return responseBuilder.build();
  }
  // [END df_java_basic_card]

  // [START df_java_browse_caro]
  @ForIntent("Browsing Carousel")
  public ActionResponse browseCarousel(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())
        || !request.hasCapability(Capability.WEB_BROWSER.getValue())) {
      return responseBuilder
          .add("Sorry, try this on a phone or select the phone surface in the simulator.")
          .add("Which response would you like to see next?")
          .build();
    }

    responseBuilder
        .add("Here's an example of a browsing carousel.")
        .add(
            new CarouselBrowse()
                .setItems(
                    new ArrayList<CarouselBrowseItem>(
                        Arrays.asList(
                            new CarouselBrowseItem()
                                .setTitle("Title of item 1")
                                .setDescription("Description of item 1")
                                .setOpenUrlAction(new OpenUrlAction().setUrl("https://example.com"))
                                .setImage(
                                    new Image()
                                        .setUrl(
                                            "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                                        .setAccessibilityText("Image alternate text"))
                                .setFooter("Item 1 footer"),
                            new CarouselBrowseItem()
                                .setTitle("Title of item 2")
                                .setDescription("Description of item 2")
                                .setOpenUrlAction(new OpenUrlAction().setUrl("https://example.com"))
                                .setImage(
                                    new Image()
                                        .setUrl(
                                            "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                                        .setAccessibilityText("Image alternate text"))
                                .setFooter("Item 2 footer")))));

    return responseBuilder.build();
  }
  // [END df_java_browse_caro]

  // [START df_java_suggestion_chips]
  @ForIntent("Suggestion Chips")
  public ActionResponse suggestionChips(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      return responseBuilder
          .add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
          .add("Which response would you like to see next?")
          .build();
    }

    responseBuilder
        .add("These are suggestion chips.")
        .addSuggestions(new String[] {"Suggestion 1", "Suggestion 2", "Suggestion 3"})
        .add(
            new LinkOutSuggestion()
                .setDestinationName("Suggestion Link")
                .setUrl("https://assistant.google.com/"))
        .add("Which type of response would you like to see next?");
    return responseBuilder.build();
  }
  // [END df_java_suggestion_chips]

  // [START df_java_media_response]
  @ForIntent("Media Response")
  public ActionResponse mediaResponse(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.MEDIA_RESPONSE_AUDIO.getValue())) {
      return responseBuilder
          .add("Sorry, this device does not support audio playback.")
          .add("Which response would you like to see next?")
          .build();
    }

    responseBuilder
        .add("This is a media response example.")
        .add(
            new MediaResponse()
                .setMediaObjects(
                    new ArrayList<MediaObject>(
                        Arrays.asList(
                            new MediaObject()
                                .setName("Jazz in Paris")
                                .setDescription("A funky Jazz tune")
                                .setContentUrl(
                                    "https://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3")
                                .setIcon(
                                    new Image()
                                        .setUrl(
                                            "https://storage.googleapis.com/automotive-media/album_art.jpg")
                                        .setAccessibilityText("Album cover of an ocean view")))))
                .setMediaType("AUDIO"))
        .addSuggestions(new String[] {"Basic Card", "List", "Carousel", "Browsing Carousel"});
    return responseBuilder.build();
  }
  // [END df_java_media_response]

  // [START df_java_media_status]
  @ForIntent("Media Status")
  public ActionResponse mediaStatus(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    String mediaStatus = request.getMediaStatus();
    String response = "Unknown media status received.";
    if (mediaStatus != null && mediaStatus.equals("FINISHED")) {
      response = "Hope you enjoyed the tune!";
    }
    responseBuilder.add(response);
    responseBuilder.add("Which response would you like to see next?");
    return responseBuilder.build();
  }
  // [END df_java_media_status]

  // [START df_java_table_simple]
  @ForIntent("Simple Table Card")
  public ActionResponse simpleTable(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      return responseBuilder
          .add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
          .add("Which response would you like to see next?")
          .build();
    }

    responseBuilder
        .add("This is a simple table example.")
        .add(
            new TableCard()
                .setColumnProperties(
                    Arrays.asList(
                        new TableCardColumnProperties().setHeader("header 1"),
                        new TableCardColumnProperties().setHeader("header 2"),
                        new TableCardColumnProperties().setHeader("header 3")))
                .setRows(
                    Arrays.asList(
                        new TableCardRow()
                            .setCells(
                                Arrays.asList(
                                    new TableCardCell().setText("row 1 item 1"),
                                    new TableCardCell().setText("row 1 item 2"),
                                    new TableCardCell().setText("row 1 item 3"))),
                        new TableCardRow()
                            .setCells(
                                Arrays.asList(
                                    new TableCardCell().setText("row 2 item 1"),
                                    new TableCardCell().setText("row 2 item 2"),
                                    new TableCardCell().setText("row 2 item 3"))))));
    return responseBuilder.build();
  }
  // [END df_java_table_simple]

  // [START df_java_table_complex]
  @ForIntent("Advanced Table Card")
  public ActionResponse advancedTable(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      return responseBuilder
          .add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
          .add("Which response would you like to see next?")
          .build();
    }

    responseBuilder
        .add("This is a table with all the possible fields.")
        .add(
            new TableCard()
                .setTitle("Table Title")
                .setSubtitle("Table Subtitle")
                .setImage(
                    new Image()
                        .setUrl(
                            "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                        .setAccessibilityText("Alt text"))
                .setButtons(
                    Arrays.asList(
                        new Button()
                            .setTitle("Button Text")
                            .setOpenUrlAction(
                                new OpenUrlAction().setUrl("https://assistant.google.com"))))
                .setColumnProperties(
                    Arrays.asList(
                        new TableCardColumnProperties()
                            .setHeader("header 1")
                            .setHorizontalAlignment("CENTER"),
                        new TableCardColumnProperties()
                            .setHeader("header 2")
                            .setHorizontalAlignment("LEADING"),
                        new TableCardColumnProperties()
                            .setHeader("header 3")
                            .setHorizontalAlignment("TRAILING")))
                .setRows(
                    Arrays.asList(
                        new TableCardRow()
                            .setCells(
                                Arrays.asList(
                                    new TableCardCell().setText("row 1 item 1"),
                                    new TableCardCell().setText("row 1 item 2"),
                                    new TableCardCell().setText("row 1 item 3")))
                            .setDividerAfter(false),
                        new TableCardRow()
                            .setCells(
                                Arrays.asList(
                                    new TableCardCell().setText("row 2 item 1"),
                                    new TableCardCell().setText("row 2 item 2"),
                                    new TableCardCell().setText("row 2 item 3")))
                            .setDividerAfter(true),
                        new TableCardRow()
                            .setCells(
                                Arrays.asList(
                                    new TableCardCell().setText("row 2 item 1"),
                                    new TableCardCell().setText("row 2 item 2"),
                                    new TableCardCell().setText("row 2 item 3"))))));
    return responseBuilder.build();
  }
  // [END df_java_table_complex]

  // [START df_java_list]
  @ForIntent("List")
  public ActionResponse list(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      return responseBuilder
          .add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
          .add("Which response would you like to see next?")
          .build();
    }

    responseBuilder
        .add("This is a list example.")
        .add(
            new SelectionList()
                .setTitle("List Title")
                .setItems(
                    Arrays.asList(
                        new ListSelectListItem()
                            .setTitle("Title of First List Item")
                            .setDescription("This is a description of a list item.")
                            .setImage(
                                new Image()
                                    .setUrl(
                                        "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                                    .setAccessibilityText("Image alternate text"))
                            .setOptionInfo(
                                new OptionInfo()
                                    .setSynonyms(
                                        Arrays.asList("synonym 1", "synonym 2", "synonym 3"))
                                    .setKey("SELECTION_KEY_ONE")),
                        new ListSelectListItem()
                            .setTitle("Google Home")
                            .setDescription(
                                "Google Home is a voice-activated speaker powered by the Google Assistant.")
                            .setImage(
                                new Image()
                                    .setUrl(
                                        "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                                    .setAccessibilityText("Google Home"))
                            .setOptionInfo(
                                new OptionInfo()
                                    .setSynonyms(
                                        Arrays.asList(
                                            "Google Home Assistant",
                                            "Assistant on the Google Home"))
                                    .setKey("SELECTION_KEY_GOOGLE_HOME")),
                        new ListSelectListItem()
                            .setTitle("Google Pixel")
                            .setDescription("Pixel. Phone by Google.")
                            .setImage(
                                new Image()
                                    .setUrl(
                                        "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                                    .setAccessibilityText("Google Pixel"))
                            .setOptionInfo(
                                new OptionInfo()
                                    .setSynonyms(
                                        Arrays.asList("Google Pixel XL", "Pixel", "Pixel XL"))
                                    .setKey("SELECTION_KEY_GOOGLE_PIXEL")))));
    return responseBuilder.build();
  }
  // [END df_java_list]

  // [START df_java_list_selected]
  @ForIntent("List - OPTION")
  public ActionResponse listSelected(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    String selectedItem = request.getSelectedOption();
    String response;

    if (selectedItem.equals("SELECTION_KEY_ONE")) {
      response = "You selected the first item";
    } else if (selectedItem.equals("SELECTION_KEY_GOOGLE_HOME")) {
      response = "You selected the Google Home!";
    } else if (selectedItem.equals("SELECTION_KEY_GOOGLE_PIXEL")) {
      response = "You selected the Google Pixel!";
    } else {
      response = "You did not select a valid item";
    }
    return responseBuilder.add(response).add("Which response would you like to see next?").build();
  }
  // [END df_java_list_selected]

  // [START df_java_caro]
  @ForIntent("Carousel")
  public ActionResponse carousel(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      return responseBuilder
          .add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
          .add("Which response would you like to see next?")
          .build();
    }

    responseBuilder
        .add("This is a carousel example.")
        .add(
            new SelectionCarousel()
                .setItems(
                    Arrays.asList(
                        new CarouselSelectCarouselItem()
                            .setTitle("Title of First List Item")
                            .setDescription("This is a description of a list item.")
                            .setImage(
                                new Image()
                                    .setUrl(
                                        "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                                    .setAccessibilityText("Image alternate text"))
                            .setOptionInfo(
                                new OptionInfo()
                                    .setSynonyms(
                                        Arrays.asList("synonym 1", "synonym 2", "synonym 3"))
                                    .setKey("SELECTION_KEY_ONE")),
                        new CarouselSelectCarouselItem()
                            .setTitle("Google Home")
                            .setDescription(
                                "Google Home is a voice-activated speaker powered by the Google Assistant.")
                            .setImage(
                                new Image()
                                    .setUrl(
                                        "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                                    .setAccessibilityText("Google Home"))
                            .setOptionInfo(
                                new OptionInfo()
                                    .setSynonyms(
                                        Arrays.asList(
                                            "Google Home Assistant",
                                            "Assistant on the Google Home"))
                                    .setKey("SELECTION_KEY_GOOGLE_HOME")),
                        new CarouselSelectCarouselItem()
                            .setTitle("Google Pixel")
                            .setDescription("Pixel. Phone by Google.")
                            .setImage(
                                new Image()
                                    .setUrl(
                                        "https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
                                    .setAccessibilityText("Google Pixel"))
                            .setOptionInfo(
                                new OptionInfo()
                                    .setSynonyms(
                                        Arrays.asList("Google Pixel XL", "Pixel", "Pixel XL"))
                                    .setKey("SELECTION_KEY_GOOGLE_PIXEL")))));
    return responseBuilder.build();
  }
  // [END df_java_caro]

  // [START df_java_caro_selected]
  @ForIntent("Carousel - OPTION")
  public ActionResponse carouselSelected(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    String selectedItem = request.getSelectedOption();
    String response;

    if (selectedItem.equals("SELECTION_KEY_ONE")) {
      response = "You selected the first item";
    } else if (selectedItem.equals("SELECTION_KEY_GOOGLE_HOME")) {
      response = "You selected the Google Home!";
    } else if (selectedItem.equals("SELECTION_KEY_GOOGLE_PIXEL")) {
      response = "You selected the Google Pixel!";
    } else {
      response = "You did not select a valid item";
    }
    return responseBuilder.add(response).add("Which response would you like to see next?").build();
  }
  // [END df_java_caro_selected]
}
