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
import com.google.actions.api.response.helperintent.NewSurface;
import com.google.api.services.actions_fulfillment.v2.model.BasicCard;
import com.google.api.services.actions_fulfillment.v2.model.Surface;
import java.util.Collections;
import java.util.Map;

public class MyActionsApp extends DialogflowApp {

  @ForIntent("Current Capabilities")
  public ActionResponse currentCapabilities(ActionRequest request) {
    // [START df_java_has_capability]
    boolean hasScreen = request.hasCapability(Capability.SCREEN_OUTPUT.getValue());
    boolean hasAudio = request.hasCapability(Capability.AUDIO_OUTPUT.getValue());
    boolean hasMediaPlayback = request.hasCapability(Capability.MEDIA_RESPONSE_AUDIO.getValue());
    boolean hasWebBrowser = request.hasCapability(Capability.WEB_BROWSER.getValue());
    // Interactive Canvas must be enabled in your project to see this
    boolean hasInteractiveCanvas = request.hasCapability("INTERACTIVE_CANVAS");
    // [END df_java_has_capability]

    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(
        "Looks like your current device "
            + (hasScreen ? "has" : "does not have")
            + " the screen output capability, "
            + (hasAudio ? "has" : "does not have")
            + " the audio output capability, "
            + (hasMediaPlayback ? "has" : "does not have")
            + " the media capability, "
            + (hasWebBrowser ? "has" : "does not have")
            + " the browser capability, "
            + (hasInteractiveCanvas ? "has" : "does not have")
            + " the interactive canvas capability.");
    responseBuilder.add("What else would you like to try?");
    responseBuilder.addSuggestions(
        new String[] {
          "Transfer surface",
          "Check Audio Capability",
          "Check Screen Capability",
          "Check Media Capability",
          "Check Web Capability",
        });
    return responseBuilder.build();
  }

  @ForIntent("Transfer Surface")
  public ActionResponse transferSurface(ActionRequest request) {
    // [START df_java_screen_available]
    String screen = Capability.SCREEN_OUTPUT.getValue();
    boolean screenAvailable = false;
    for (Surface surface : request.getAvailableSurfaces()) {
      for (com.google.api.services.actions_fulfillment.v2.model.Capability capability :
          surface.getCapabilities()) {
        if (capability.getName().equals(screen)) {
          screenAvailable = true;
          break;
        }
      }
    }
    // [END df_java_screen_available]

    // [START df_java_transfer_reason]
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      responseBuilder.add("You're already on a screen device");
      responseBuilder.add("What else would you like to try?");
      responseBuilder.addSuggestions(
          new String[] {
            "Transfer surface",
            "Check Audio Capability",
            "Check Screen Capability",
            "Check Media Capability",
            "Check Web Capability",
          });
      return responseBuilder.build();
    } else if (screenAvailable) {
      responseBuilder.add(
          new NewSurface()
              .setContext("Let's move you to a screen device for cards and other visual responses")
              .setNotificationTitle("Try your Action here!")
              .setCapabilities(Collections.singletonList(screen)));
      return responseBuilder.build();
    } else {
      responseBuilder.add("It looks like there is no screen device associated with this user.");
      responseBuilder.add("What else would you like to try?");
      responseBuilder.addSuggestions(
          new String[] {
            "Transfer surface",
            "Check Audio Capability",
            "Check Screen Capability",
            "Check Media Capability",
            "Check Web Capability",
          });
      return responseBuilder.build();
    }
    // [END df_java_transfer_reason]
  }

  // [START df_java_transfer_accepted]
  @ForIntent("Transfer Surface - NEW_SURFACE")
  public ActionResponse newSurface(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    Map<String, Object> newSurfaceStatus = request.getArgument("NEW_SURFACE").getExtension();
    if (newSurfaceStatus.get("status").equals("OK")) {
      responseBuilder.add("Welcome to a screen device!");
      responseBuilder.add(
          new BasicCard()
              .setTitle("You're on a screened device!")
              .setFormattedText("Screen devices support basic cards and other visual responses!"));
    } else {
      responseBuilder.add("Ok, no problem.");
    }
    responseBuilder.add("What else would you like to try?");
    responseBuilder.addSuggestions(
        new String[] {
          "Transfer surface",
          "Check Audio Capability",
          "Check Screen Capability",
          "Check Media Capability",
          "Check Web Capability",
        });
    return responseBuilder.build();
  }
  // [END df_java_transfer_accepted]
}
