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
package com.fluffyluffs.httpretriever4j.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fluffyluffs.httpretriever4j.HttpRetrieverAuthorization;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria.HTTPMethod;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.junit.Test;

/** TestHttpRetriever */
public class TestHttpRetriever {

  @Test
  public void test_HttpMethod() {
    assertEquals(HTTPMethod.GET, HTTPMethod.valueOf("GET"));
    assertEquals(HTTPMethod.DELETE, HTTPMethod.valueOf("DELETE"));
    assertEquals(HTTPMethod.POST, HTTPMethod.valueOf("POST"));
    assertEquals(HTTPMethod.PUT, HTTPMethod.valueOf("PUT"));
    assertEquals(HTTPMethod.TRACE, HTTPMethod.valueOf("TRACE"));
  }

  @Test
  public void test_ContentType() {
    assertEquals(
        "application/json; charset=UTF-8", HttpRetrieverCriteria.ContentType.JSON.getContentType());
    assertEquals(
        "text/html; charset=UTF-8", HttpRetrieverCriteria.ContentType.TEXT.getContentType());
  }

  @Test
  public void test_basic_HttpRetrieverAuthorization() {
    String[] elements =
        String.valueOf(HttpRetrieverAuthorization.BASIC.getAuthorization("user:passwd"))
            .split("\\s+");
    assertEquals("Basic", elements[0]);
    assertTrue(
        Pattern.matches(
            "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$", elements[1]));
  }

  @Test
  public void test_bearer_HttpRetrieverAuthorization() {
    String bearer = "1234567890abcd";
    String[] elements =
        String.valueOf(HttpRetrieverAuthorization.BEARER.getAuthorization(bearer)).split("\\s+");
    assertEquals("Bearer", elements[0]);
    assertEquals(bearer, elements[1]);
  }

  @Test(expected = NoSuchElementException.class)
  public void test_empty_httpRetrieverCriteria() {
    HttpRetrieverCriteria httpRetrieverCriteria =
        new HttpRetrieverCriteria.HttpRetrieverCriteriaBuilder().build();

    fail("Expected NoSuchElementException");
  }

  @Test
  public void test_valid_httpRetrieverCriteria() throws MalformedURLException {
    HttpRetrieverCriteria httpRetrieverCriteria =
        new HttpRetrieverCriteria.HttpRetrieverCriteriaBuilder()
            .setURL("http://cabbage.com/api/v1/")
            .setUserAgent("Mozzila/5.0")
            .setHTTPMethod(HTTPMethod.GET)
            .build();

    assertEquals("http://cabbage.com/api/v1/", httpRetrieverCriteria.getUrl().toExternalForm());
    assertFalse(httpRetrieverCriteria.getUserAgent().isEmpty());
    assertEquals("GET", httpRetrieverCriteria.gethTTPMethod().name());
  }
}