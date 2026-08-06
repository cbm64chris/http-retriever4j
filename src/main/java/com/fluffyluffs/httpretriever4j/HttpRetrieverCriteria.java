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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/** HTTP Retriever Criteria */
public class HttpRetrieverCriteria {

  private final String url;
  private final HTTPMethod hTTPMethod;
  private final String body;
  private final ContentType bodyContentType;
  private final ContentType acceptContentType;
  private final List<Header> headers;
  private final List<QueryParameter> queryParameters;
  private final String userAgent;
  private final char[] authorization;

  private HttpRetrieverCriteria(HttpRetrieverCriteriaBuilder httpRetrieverCriteriaBuilder) {
    this.authorization = httpRetrieverCriteriaBuilder.authorization;
    this.hTTPMethod = httpRetrieverCriteriaBuilder.hTTPMethod;
    this.body = httpRetrieverCriteriaBuilder.body;
    this.bodyContentType = httpRetrieverCriteriaBuilder.bodyContentType;
    this.acceptContentType = httpRetrieverCriteriaBuilder.acceptContentType;
    this.userAgent = httpRetrieverCriteriaBuilder.userAgent;
    this.headers = List.copyOf(httpRetrieverCriteriaBuilder.headers);
    this.queryParameters = List.copyOf(httpRetrieverCriteriaBuilder.queryParameters);

    String parameters =
        queryParameters.stream()
            .map(queryParameter -> urlEncoding(queryParameter.getField()) + "=" + urlEncoding(queryParameter.getValue()))
            .collect(java.util.stream.Collectors.joining("&"));
    this.url =
        httpRetrieverCriteriaBuilder.url
            + queryParameterSeparator(httpRetrieverCriteriaBuilder.url, parameters);
  }

  private String urlEncoding(String arg) {
    return URLEncoder.encode(arg, StandardCharsets.UTF_8);
  }

  private String queryParameterSeparator(String baseUrl, String parameters) {
    if (parameters.isBlank()) {
      return "";
    }
    if (!baseUrl.contains("?")) {
      return "?" + parameters;
    }
    return (baseUrl.endsWith("?") || baseUrl.endsWith("&") ? "" : "&") + parameters;
  }

  /**
   * Get Authorisation
   *
   * @return {@link char[]}
   */
  public char[] getAuthorization() {
    return authorization;
  }

  /**
   * Get URL
   *
   * @return {@link URL}
   * @throws java.net.MalformedURLException
   */
  public URL getUrl() throws MalformedURLException {
    return new URL(url);
  }

  /**
   * Get HTTP Method
   *
   * @return {@link HTTPMethod}
   */
  public HTTPMethod gethTTPMethod() {
    return hTTPMethod;
  }

  /**
   * Get Body
   *
   * @return {@link String}
   */
  public String getBody() {
    return body;
  }

  /**
   * Get Body Content Type
   *
   * @return {@link ContentType}
   */
  public ContentType getBodyContentType() {
    return bodyContentType;
  }

  /**
   * Get Accepted Content Type
   *
   * @return {@link ContentType}
   */
  public ContentType getAcceptContentType() {
    return acceptContentType;
  }

  /**
   * Get User Agent
   *
   * @return {@link String}
   */
  public String getUserAgent() {
    return userAgent;
  }

  /**
   * Get the Headers to apply
   *
   * @return List of {@link Header}
   */
  public List<Header> getHeaders() {
    return headers;
  }

  /**
   * Get the Query Parameters to apply
   *
   * @return List of {@link QueryParameter}
   */
  public List<QueryParameter> getQueryParameters() {
    return queryParameters;
  }

  /** HTTP Retriever Criteria Builder */
  public static class HttpRetrieverCriteriaBuilder {

    private char[] authorization;
    private String url;
    private HTTPMethod hTTPMethod;
    private String body;
    private ContentType bodyContentType;
    private ContentType acceptContentType;
    private String userAgent;
    private List<Header> headers = new ArrayList<>();
    private List<QueryParameter> queryParameters = new ArrayList<>();

    /**
     * Set authorization in UTF-8 Base64. Using {@link HttpRetrieverAuthorization}
     *
     * <pre>
     *     DataRetrieverAuthorization.BASIC.setAuthorization(String.format("%s:%s", "user", "passwd"))
     * </pre>
     *
     * @param authorization {@link char[]}
     * @return {@link HttpRetrieverCriteriaBuilder}
     */
    public HttpRetrieverCriteriaBuilder setAuthorization(char[] authorization) {
      this.authorization = authorization;
      return this;
    }

    /**
     * Set URL
     *
     * @param url {@link String}
     * @return {@link HttpRetrieverCriteriaBuilder}
     */
    public HttpRetrieverCriteriaBuilder setURL(String url) {
      this.url = url;
      return this;
    }

    /**
     * Set HTTP Method
     *
     * @param hTTPMethod {@link HTTPMethod}
     * @return {@link HttpRetrieverCriteriaBuilder}
     */
    public HttpRetrieverCriteriaBuilder setHTTPMethod(HTTPMethod hTTPMethod) {
      this.hTTPMethod = hTTPMethod;
      return this;
    }

    /**
     * Set Body
     *
     * @param body {@link String}
     * @return {@link HttpRetrieverCriteriaBuilder}
     */
    public HttpRetrieverCriteriaBuilder setBody(String body) {
      this.body = body;
      return this;
    }

    public HttpRetrieverCriteriaBuilder setHeader(Header header) {
      setHeaders(List.of(header));
      return this;
    }

    public HttpRetrieverCriteriaBuilder setHeaders(Collection<Header> headers) {
      this.headers.addAll(headers);
      return this;
    }

    public HttpRetrieverCriteriaBuilder setQueryParameter(QueryParameter queryParameter) {
      queryParameters.add(queryParameter);
      return this;
    }

    /**
     * Set Accepted Content Type
     *
     * @param acceptContentType {@link ContentType}
     * @return {@link HttpRetrieverCriteriaBuilder}
     */
    public HttpRetrieverCriteriaBuilder setAcceptContentType(ContentType acceptContentType) {
      this.acceptContentType = acceptContentType;
      return this;
    }

    /**
     * Set Body Content Type
     *
     * @param bodyContentType {@link ContentType}
     * @return {@link HttpRetrieverCriteriaBuilder}
     */
    public HttpRetrieverCriteriaBuilder setBodyContentType(ContentType bodyContentType) {
      this.bodyContentType = bodyContentType;
      return this;
    }

    /**
     * Set User Agent: https://developers.whatismybrowser.com/useragents/explore
     *
     * @param userAgent {@link String}
     * @return {@link HttpRetrieverCriteriaBuilder}
     */
    public HttpRetrieverCriteriaBuilder setUserAgent(String userAgent) {
      this.userAgent = userAgent;
      return this;
    }

    /**
     * Build {@link HttpRetrieverCriteria}. May throw {@link NoSuchElementException} where a
     * required element is missing.
     *
     * @return
     */
    public HttpRetrieverCriteria build() {
      validate(url, "URL");
      validate(hTTPMethod, "HTTP Method GET, POST etc");
      validate(userAgent, "Mozilla/5.0 etc");
      queryParameters.stream()
          .forEach(
              queryParameter -> validate(queryParameter.getValue(), queryParameter.getField()));

      return new HttpRetrieverCriteria(this);
    }

    private <T> T validate(T criterionValue, String field) {
      return Optional.ofNullable(criterionValue)
          .orElseThrow(
              () -> new NoSuchElementException(String.format("Missing required %s.", field)));
    }
  }

  public enum ContentType {
    JSON("application/json; charset=UTF-8"),
    CSV("text/csv; charset=UTF-8"),
    PNG("image/png"),
    JPEG("image/jpeg"),
    GIF("image/gif"),
    SVG("image/svg+xml"),
    PDF("application/pdf"),
    TEXT("text/plain; charset=UTF-8"),
    HTML("text/html; charset=UTF-8"),
    CSS("text/css; charset=UTF-8"),
    JAVASCRIPT("text/javascript; charset=UTF-8"),
    XML("application/xml; charset=UTF-8"),
    MP4("video/mp4"),
    OCTET_STREAM("application/octet-stream"),
    ZIP("application/zip"),
    MULTIPART_FORM("multipart/form-data"),
    FORM("application/x-www-form-urlencoded");

    // TODO add additional content types

    private final String contentType;

    private ContentType(String contentType) {
      this.contentType = contentType;
    }

    /**
     * Get Content Type
     *
     * @return {@link String}
     */
    public String getContentType() {
      return contentType;
    }
  }

  public enum HTTPMethod {
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    PUT,
    DELETE,
    TRACE,
    POST;
  }
}
