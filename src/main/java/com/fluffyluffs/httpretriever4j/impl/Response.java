/*
 * Copyright 2021 HTTPRetriever4J.
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
package com.fluffyluffs.httpretriever4j.impl;

import java.util.Arrays;
import java.util.Optional;

/**
 * Response
 *
 * @author Chris Luff
 */
public enum Response {
  HTTP_OK(200, true),
  HTTP_CREATED(201, true),
  HTTP_NO_CONTENT(204, true),
  HTTP_UNAUTHORIZED(401, false),
  HTTP_FORBIDDEN(403, false),
  HTTP_NOT_FOUND(404, false),
  HTTP_INTERNAL_ERROR(500, false);

  private final int reponseCode;
  private final boolean status;

  private Response(int reponseCode, boolean status) {
    this.reponseCode = reponseCode;
    this.status = status;
  }

  public int getReponseCode() {
    return reponseCode;
  }

  public boolean hasStatus() {
    return status;
  }

  public static Optional<Response> of(int value) {
    return Arrays.stream(values()).filter(v -> value == v.getReponseCode()).findFirst();
  }
  
  
}
