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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fluffyluffs.httpretriever4j.HttpRetrieverAuthorization;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria.HTTPMethod;
import com.fluffyluffs.httpretriever4j.QueryParameter;
import com.fluffyluffs.httpretriever4j.model.HttpRetrieverResponse;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** TestHttpRetriever */
class TestHttpRetriever {

  @Test
  void httpMethod() {
    assertEquals(HTTPMethod.GET, HTTPMethod.valueOf("GET"));
    assertEquals(HTTPMethod.HEAD, HTTPMethod.valueOf("HEAD"));
    assertEquals(HTTPMethod.OPTIONS, HTTPMethod.valueOf("OPTIONS"));
    assertEquals(HTTPMethod.PATCH, HTTPMethod.valueOf("PATCH"));
    assertEquals(HTTPMethod.DELETE, HTTPMethod.valueOf("DELETE"));
    assertEquals(HTTPMethod.POST, HTTPMethod.valueOf("POST"));
    assertEquals(HTTPMethod.PUT, HTTPMethod.valueOf("PUT"));
    assertEquals(HTTPMethod.TRACE, HTTPMethod.valueOf("TRACE"));
  }

  @Test
  void contentType() {
    assertEquals("application/json; charset=UTF-8", contentType("JSON"));
    assertEquals("text/csv; charset=UTF-8", contentType("CSV"));
    assertEquals("image/png", contentType("PNG"));
    assertEquals("image/jpeg", contentType("JPEG"));
    assertEquals("image/gif", contentType("GIF"));
    assertEquals("image/svg+xml", contentType("SVG"));
    assertEquals("application/pdf", contentType("PDF"));
    assertEquals("text/plain; charset=UTF-8", contentType("TEXT"));
    assertEquals("text/html; charset=UTF-8", contentType("HTML"));
    assertEquals("text/css; charset=UTF-8", contentType("CSS"));
    assertEquals("text/javascript; charset=UTF-8", contentType("JAVASCRIPT"));
    assertEquals("application/xml; charset=UTF-8", contentType("XML"));
    assertEquals("video/mp4", contentType("MP4"));
    assertEquals("application/octet-stream", contentType("OCTET_STREAM"));
    assertEquals("application/zip", contentType("ZIP"));
    assertEquals("multipart/form-data", contentType("MULTIPART_FORM"));
    assertEquals("application/x-www-form-urlencoded", contentType("FORM"));
  }

  @Test
  void basicHttpRetrieverAuthorization() {
    String[] elements =
        String.valueOf(HttpRetrieverAuthorization.BASIC.getAuthorization("user:passwd"))
            .split("\\s+");
    assertEquals("Basic", elements[0]);
    assertTrue(
        Pattern.matches(
            "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$", elements[1]));
  }

  @Test
  void bearerHttpRetrieverAuthorization() {
    String bearer = "1234567890abcd";
    String[] elements =
        String.valueOf(HttpRetrieverAuthorization.BEARER.getAuthorization(bearer)).split("\\s+");
    assertEquals("Bearer", elements[0]);
    assertEquals(bearer, elements[1]);
  }

  @Test
  void emptyHttpRetrieverCriteria() {
    assertThrows(
        NoSuchElementException.class, () -> new HttpRetrieverCriteria.HttpRetrieverCriteriaBuilder().build());
  }

  @Test
  void validHttpRetrieverCriteria() throws MalformedURLException {
    HttpRetrieverCriteria httpRetrieverCriteria = criteria("http://cabbage.com/api/v1/");

    assertEquals("http://cabbage.com/api/v1/", httpRetrieverCriteria.getUrl().toExternalForm());
    assertFalse(httpRetrieverCriteria.getUserAgent().isEmpty());
    assertEquals("GET", httpRetrieverCriteria.gethTTPMethod().name());
  }

  @Test
  void queryParametersAreEncodedAndAppended() throws MalformedURLException {
    HttpRetrieverCriteria httpRetrieverCriteria =
        new HttpRetrieverCriteria.HttpRetrieverCriteriaBuilder()
            .setURL("https://example.com/reports?includeSchema=false")
            .setUserAgent("Mozilla/5.0")
            .setHTTPMethod(HTTPMethod.GET)
            .setQueryParameter(new QueryParameter("report name", "MARS & Friends"))
            .build();

    assertEquals(
        "https://example.com/reports?includeSchema=false&report+name=MARS+%26+Friends",
        httpRetrieverCriteria.getUrl().toExternalForm());
  }

  @Test
  void responseExposesHeaders() {
    HttpRetrieverResponse response =
        new HttpRetrieverResponse(
            404,
            new ByteArrayInputStream(new byte[0]),
            Map.of("Content-Type", List.of("application/problem+json")));

    assertEquals(404, response.getResponseCode());
    assertEquals(List.of("application/problem+json"), response.getHeaders().get("Content-Type"));
  }

  private String contentType(String name) {
    return HttpRetrieverCriteria.ContentType.valueOf(name).getContentType();
  }

  private HttpRetrieverCriteria criteria(String url) {
    return new HttpRetrieverCriteria.HttpRetrieverCriteriaBuilder()
        .setURL(url)
        .setUserAgent("Mozilla/5.0")
        .setHTTPMethod(HTTPMethod.GET)
        .build();
  }
}
