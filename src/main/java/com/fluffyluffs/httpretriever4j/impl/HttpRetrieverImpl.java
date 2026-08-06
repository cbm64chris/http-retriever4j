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

import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria.ContentType;
import com.fluffyluffs.httpretriever4j.impl.error.HttpRetrieverImplError;
import com.fluffyluffs.httpretriever4j.model.HttpRetrieverResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpRetrieverImpl {

  private static final Logger LOGGER = Logger.getLogger(HttpRetrieverImpl.class.getName());

  private static final String AUTH = "Authorization";
  private static final String USER_AGENT = "User-Agent";
  private static final String ACCEPT = "Accept";
  private static final String CACHE_CONTROL = "Cache-Control";
  private static final String CONTENT_TYPE = "Content-Type";

  private final HttpRetrieverCriteria httpRetrieverCriteria;
  private final long timeout;
  private final TimeUnit timeUnit;

  public HttpRetrieverImpl(HttpRetrieverCriteria httpRetrieverCriteria) {
    this.httpRetrieverCriteria = httpRetrieverCriteria;
    this.timeout = 5;
    this.timeUnit = TimeUnit.SECONDS;
  }

  public HttpRetrieverImpl(
      HttpRetrieverCriteria httpRetrieverCriteria, long timeout, TimeUnit timeUnit) {
    this.httpRetrieverCriteria = httpRetrieverCriteria;
    this.timeout = timeout;
    this.timeUnit = timeUnit;
  }

  public HttpRetrieverResponse retrieveHttpRetrieverResponse() throws HttpRetrieverImplError {
    HttpURLConnection connection = getHttpURLConnection();

    try {
      int responseCode = connection.getResponseCode();
      Response response =
          Response.of(responseCode).orElse(Response.HTTP_INTERNAL_ERROR);
      boolean success = response.hasStatus();
      InputStream responseStream = success ? connection.getInputStream() : connection.getErrorStream();

      log(response, success ? Level.INFO : Level.WARNING);

      if (responseStream == null) {
        return new HttpRetrieverResponse(
            responseCode, InputStream.nullInputStream(), responseHeaders(connection));
      }

      try (InputStream in = responseStream) {
        return new HttpRetrieverResponse(
            responseCode, new ByteArrayInputStream(in.readAllBytes()), responseHeaders(connection));
      }

    } catch (IOException ex) {
      throw new HttpRetrieverImplError(ex.getLocalizedMessage(), ex);
    } finally {
      connection.disconnect();
    }
  }

  private void log(Response response, Level level) {
    LOGGER.log(
        level, "Recieved {0}:{1}", new Object[] {response.name(), response.getReponseCode()});
  }

  private HttpURLConnection getHttpURLConnection() throws HttpRetrieverImplError {

    try {

      HttpURLConnection connection =
          (HttpURLConnection) httpRetrieverCriteria.getUrl().openConnection();
      Optional.ofNullable(httpRetrieverCriteria.getAuthorization())
          .ifPresent(auth -> connection.setRequestProperty(AUTH, String.valueOf(auth)));
      connection.setRequestProperty(USER_AGENT, httpRetrieverCriteria.getUserAgent());
      Optional.ofNullable(httpRetrieverCriteria.getAcceptContentType())
          .map(ContentType::getContentType)
          .ifPresent(contentType -> connection.setRequestProperty(ACCEPT, contentType));
      connection.setRequestProperty(CACHE_CONTROL, "no-cache");
      connection.setRequestMethod(httpRetrieverCriteria.gethTTPMethod().name());
      connection.setConnectTimeout((int) Duration.of(timeout, timeUnit.toChronoUnit()).toMillis());
      connection.setReadTimeout(timeoutInMilliseconds());
      connection.setUseCaches(false);

      Optional.ofNullable(httpRetrieverCriteria.getBodyContentType())
          .ifPresent(
              contentType ->
                  connection.setRequestProperty(CONTENT_TYPE, contentType.getContentType()));

      httpRetrieverCriteria
          .getHeaders()
          .forEach(
              header -> {
                String headerType =
                    Optional.ofNullable(header.getType())
                        .orElseThrow(
                            () -> new NoSuchElementException("Header type cannot be null"));
                String headerString =
                    Optional.ofNullable(header.getHeader())
                        .orElseThrow(() -> new NoSuchElementException("Header cannot be null"));

                connection.setRequestProperty(headerType, headerString);
              });

      String body = httpRetrieverCriteria.getBody();
      if (body != null) {
        writeBody(connection, body);
      }

      connection.connect();

      return connection;
    } catch (IOException ex) {
      throw new HttpRetrieverImplError(ex.getLocalizedMessage(), ex);
    }
  }

  private void writeBody(HttpURLConnection secureConnection, String body) throws IOException {
    byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
    secureConnection.setDoOutput(true);
    secureConnection.setFixedLengthStreamingMode(bodyBytes.length);
    try (OutputStream outputStream = secureConnection.getOutputStream()) {
      outputStream.write(bodyBytes);
    }
  }

  private int timeoutInMilliseconds() {
    return Math.toIntExact(Duration.of(timeout, timeUnit.toChronoUnit()).toMillis());
  }

  private Map<String, List<String>> responseHeaders(HttpURLConnection connection) {
    Map<String, List<String>> headers = connection.getHeaderFields();
    if (headers == null) {
      return Map.of();
    }
    return headers.entrySet().stream()
        .filter(entry -> entry.getKey() != null && entry.getValue() != null)
        .collect(
            java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
  }
}
