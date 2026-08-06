/*
 * Copyright 2023 HTTPRetriever4J.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fluffyluffs.httpretriever4j.model;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class HttpRetrieverResponse {

  private final int responseCode;
  private final InputStream in;
  private final Map<String, List<String>> headers;

  public HttpRetrieverResponse(int responseCode, InputStream in) {
    this(responseCode, in, Map.of());
  }

  public HttpRetrieverResponse(
      int responseCode, InputStream in, Map<String, List<String>> headers) {
    this.responseCode = responseCode;
    this.in = in;
    this.headers = Map.copyOf(headers);
  }

  public int getResponseCode() {
    return responseCode;
  }

  public InputStream getIn() {
    return in;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }
}
