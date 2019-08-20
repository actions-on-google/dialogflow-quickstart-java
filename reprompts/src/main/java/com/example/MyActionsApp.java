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

// [START df_java_reprompt]
package com.example;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;

public class MyActionsApp extends DialogflowApp {

  @ForIntent("Reprompt")
  public ActionResponse reprompt(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    int repromptCount = request.getRepromptCount();
    String response;
    if (repromptCount == 0) {
      response = "What was that?";
    } else if (repromptCount == 1) {
      response = "Sorry, I didn't catch that. Could you repeat yourself?";
    } else {
      responseBuilder.endConversation();
      response = "Okay let's try this again later.";
    }
    return responseBuilder.add(response).build();
  }
}
// [END df_java_reprompt]
