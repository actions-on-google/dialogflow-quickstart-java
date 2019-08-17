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

import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class MyActionsAppTest {

  private static final Logger LOGGER = Logger.getLogger(MyActionsAppTest.class.getName());

  private static String getReqJson(String directory) throws IOException {
    Path reqPath = Paths.get("src", "test", "resources", directory, "req.json");
    return new String(Files.readAllBytes(reqPath));
  }

  private static String getResJson(String directory) throws IOException {
    Path reqPath = Paths.get("src", "test", "resources", directory, "res.json");
    return new String(Files.readAllBytes(reqPath));
  }

  private static String[] getAllDirs() throws IOException {
    Path resPath = Paths.get("src", "test", "resources");
    File resFile = new File(resPath.toString());
    String[] directories =
        resFile.list(
            new FilenameFilter() {
              @Override
              public boolean accept(File resource, String fileName) {
                return resource.isDirectory();
              }
            });
    return directories;
  }

  @TestFactory
  public Collection<DynamicTest> webhookDynamicTests() throws IOException {
    Collection<DynamicTest> dynamicTests = new ArrayList<>();
    for (String intent : getAllDirs()) {
      DynamicTest test =
          DynamicTest.dynamicTest(
              intent,
              () -> {
                LOGGER.info("Testing intent: " + intent);
                MyActionsApp app = new MyActionsApp();
                String req = getReqJson(intent);
                String expectedRes = getResJson(intent);
                CompletableFuture<String> future = app.handleRequest(req, null /* headers */);
                String actualRes = future.get();

                JsonObject actualResObj = new Gson().fromJson(actualRes, JsonObject.class);
                JsonObject expResObj = new Gson().fromJson(expectedRes, JsonObject.class);

                LOGGER.info("Comparing actual response: " + actualResObj.toString());
                LOGGER.info("With expected response: " + expResObj.toString());

                assertTrue(Objects.deepEquals(actualResObj, expResObj));
              });
      dynamicTests.add(test);
    }
    return dynamicTests;
  }
}