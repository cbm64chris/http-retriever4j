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

package com.fluffyluffs.httpretriever4j;

import com.fluffyluffs.httpretriever4j.impl.HttpRetrieverImpl;
import com.fluffyluffs.httpretriever4j.impl.error.HttpRetrieverImplError;
import java.io.InputStream;
import java.util.function.Function;

public class HttpRetriever {

  private final HttpRetrieverCriteria httpRetrieverCriteria;

  /**
   * HttpRetriever
   *
   * @param httpRetrieverCriteria extends {@link HttpRetrieverCriteria}
   */
  public HttpRetriever(HttpRetrieverCriteria httpRetrieverCriteria) {
    this.httpRetrieverCriteria = httpRetrieverCriteria;
  }

  /**
   * Retrieve
   *
   * @return InputStream
   */
  public InputStream retrieve() throws HttpRetrieverImplError {

    return new HttpRetrieverImpl(httpRetrieverCriteria).retrieveHttpRetrieverResponse().getIn();
  }

  /**
   * Retrieve
   *
   * @param <T> response type
   * @param responseFunction response function
   * @return T
   */
  public <T> T retrieve(Function<InputStream, T> responseFunction) throws HttpRetrieverImplError {

    return responseFunction.apply(
        new HttpRetrieverImpl(httpRetrieverCriteria).retrieveHttpRetrieverResponse().getIn());
  }

  public int status() throws HttpRetrieverImplError {

    return new HttpRetrieverImpl(httpRetrieverCriteria)
        .retrieveHttpRetrieverResponse()
        .getResponseCode();
  }
}
