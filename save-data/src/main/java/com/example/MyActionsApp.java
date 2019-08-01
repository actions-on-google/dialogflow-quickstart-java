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
import java.util.Map;

public class MyActionsApp extends DialogflowApp {

  @ForIntent("Default Welcome Intent")
  public ActionResponse welcome(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    Map<String, Object> userStorage = request.getUserStorage();
    if (userStorage.containsKey("sum")) {
      responseBuilder.add("Hi! Your last result was " + userStorage.get("sum") + ".");
    } else {
      responseBuilder.add("Hi! Let's add two numbers.");
    }
    responseBuilder.add("What's the first number?");
    return responseBuilder.build();
  }

  // [START save_data_between_turns_df]
  @ForIntent("Get First Number")
  public ActionResponse getFirstNumber(ActionRequest request) {
    Integer firstNum = ((Double) request.getParameter("firstNum")).intValue();

    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.getConversationData().put("firstNum", firstNum);
    responseBuilder.add("Got it, the first number is " + firstNum + ".");
    responseBuilder.add("What's the second number?");
    return responseBuilder.build();
  }
  // [END save_data_between_turns_df]

  @ForIntent("Get Second Number")
  public ActionResponse getSecondNumber(ActionRequest request) {
    Integer firstNum = ((Double) request.getConversationData().get("firstNum")).intValue();
    Integer secondNum = ((Double) request.getParameter("secondNum")).intValue();
    Integer sum = firstNum + secondNum;

    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.getConversationData().put("sum", sum);
    responseBuilder.add(
        "Got it, the second number is " + secondNum + ". " + "The sum of both numbers is " + sum);
    responseBuilder.add("Should I remember that for next time?");
    return responseBuilder.build();
  }

  // [START save_data_across_convs_df]
  @ForIntent("Save Sum")
  public ActionResponse saveSum(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    Integer sum = ((Double) request.getConversationData().get("sum")).intValue();
    responseBuilder.getUserStorage().put("sum", sum);
    responseBuilder.add("Alright, I'll store that for next time. See you then.");
    responseBuilder.endConversation();
    return responseBuilder.build();
  }
  // [END save_data_across_convs_df]

  // [START clear_user_storage_df]
  @ForIntent("Forget Number")
  public ActionResponse forgetNumber(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.getUserStorage().clear();
    responseBuilder.add("Alright, I forgot your last result.");
    responseBuilder.add("Let's add two new numbers. What is the first number?");
    return responseBuilder.build();
  }
  // [END clear_user_storage_df]
}
