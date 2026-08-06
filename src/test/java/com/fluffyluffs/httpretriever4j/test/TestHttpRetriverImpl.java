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
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fluffyluffs.httpretriever4j.HttpRetriever;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria.ContentType;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria.HTTPMethod;
import com.fluffyluffs.httpretriever4j.impl.error.HttpRetrieverImplError;
import com.fluffyluffs.httpretriever4j.model.HttpRetrieverResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** TestHttpRetrieverImpl */
class TestHttpRetrieverImpl {

  private static HttpServer server;
  private static String serverUrl;

  @BeforeAll
  static void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext("/ok", exchange -> respond(exchange, 200, "{\"name\":\"Cabbage\"}"));
    server.createContext("/no-content", exchange -> respond(exchange, 204, ""));
    server.createContext("/not-found", exchange -> respond(exchange, 404, "{\"error\":\"Not Found\"}"));
    server.createContext(
        "/echo",
        exchange ->
            respond(
                exchange,
                200,
                new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)));
    server.start();
    serverUrl = "http://" + server.getAddress().getHostString() + ":" + server.getAddress().getPort();
  }

  @AfterAll
  static void stopServer() {
    server.stop(0);
  }

  @Test
  void retrievesSuccessfulResponseAndHeaders() throws IOException, HttpRetrieverImplError {
    HttpRetrieverResponse response = new HttpRetriever(criteria("/ok", HTTPMethod.GET, null)).execute();

    assertEquals(200, response.getResponseCode());
    assertEquals("{\"name\":\"Cabbage\"}", read(response));
    assertEquals("request-id", response.getHeaders().get("X-request-id").get(0));
  }

  @Test
  void retrievesNoContentResponse() throws IOException, HttpRetrieverImplError {
    HttpRetrieverResponse response = new HttpRetriever(criteria("/no-content", HTTPMethod.GET, null)).execute();

    assertEquals(204, response.getResponseCode());
    assertEquals("", read(response));
  }

  @Test
  void retrievesErrorResponseBody() throws IOException, HttpRetrieverImplError {
    HttpRetrieverResponse response = new HttpRetriever(criteria("/not-found", HTTPMethod.GET, null)).execute();

    assertEquals(404, response.getResponseCode());
    assertEquals("{\"error\":\"Not Found\"}", read(response));
  }

  @Test
  void sendsExactUtf8RequestBody() throws IOException, HttpRetrieverImplError {
    String body = "{\"currency\":\"£\"}";

    HttpRetrieverResponse response =
        new HttpRetriever(criteria("/echo", HTTPMethod.PUT, body)).execute();

    assertEquals(body, read(response));
  }

  @Test
  void reportsConnectionFailures() {
    HttpRetriever retriever = new HttpRetriever(criteria("http://127.0.0.1:1", HTTPMethod.GET, null));

    assertThrows(HttpRetrieverImplError.class, retriever::execute);
  }

  private static HttpRetrieverCriteria criteria(String path, HTTPMethod method, String body) {
    HttpRetrieverCriteria.HttpRetrieverCriteriaBuilder builder =
        new HttpRetrieverCriteria.HttpRetrieverCriteriaBuilder()
            .setURL(path.startsWith("http") ? path : serverUrl + path)
            .setUserAgent("HTTPRetriever4J test")
            .setHTTPMethod(method);
    if (body != null) {
      builder.setBodyContentType(ContentType.JSON).setBody(body);
    }
    return builder.build();
  }

  private static String read(HttpRetrieverResponse response) throws IOException {
    return read(response.getIn());
  }

  private static String read(java.io.InputStream input) throws IOException {
    return new String(input.readAllBytes(), StandardCharsets.UTF_8);
  }

  private static void respond(HttpExchange exchange, int status, String body) throws IOException {
    exchange.getResponseHeaders().set("X-Request-Id", "request-id");
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(status, status == 204 ? -1 : bytes.length);
    if (status != 204) {
      exchange.getResponseBody().write(bytes);
    }
    exchange.close();
  }
}
