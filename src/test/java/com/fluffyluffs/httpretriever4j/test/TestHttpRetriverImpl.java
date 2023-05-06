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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import com.fluffyluffs.httpretriever4j.HttpRetriever;
import com.fluffyluffs.httpretriever4j.HttpRetrieverCriteria;
import com.fluffyluffs.httpretriever4j.Utils;
import com.fluffyluffs.httpretriever4j.impl.error.HttpRetrieverImplError;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

/**
 * TestHttpRetriverImpl
 *
 * <p>HttpURLConnection mocked with assistance of Stackoverflow
 * https://stackoverflow.com/a/40073597/1338769
 */
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class TestHttpRetriverImpl {

  @Test
  public void test_response_code_204_get() throws IOException, Exception {

    String responseString = "";

    URL mockURL = mock(URL.class);
    whenNew(URL.class).withArguments(anyString()).thenReturn(mockURL);
    HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
    when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);

    doNothing().when(mockHttpURLConnection).connect();
    when(mockHttpURLConnection.getResponseCode()).thenReturn(204);
    doReturn(new ByteArrayInputStream(responseString.getBytes("UTF-8")))
        .when(mockHttpURLConnection)
        .getInputStream();

    HttpRetrieverCriteria mockHttpRetrieverCriteria = mock(HttpRetrieverCriteria.class);
    whenNew(HttpRetrieverCriteria.class).withAnyArguments().thenReturn(mockHttpRetrieverCriteria);
    when(mockHttpRetrieverCriteria.getUrl()).thenReturn(mockURL);
    when(mockHttpRetrieverCriteria.getUserAgent()).thenReturn("Mozzila/5.0");
    when(mockHttpRetrieverCriteria.gethTTPMethod())
        .thenReturn(HttpRetrieverCriteria.HTTPMethod.GET);

    String repsonse = new HttpRetriever(mockHttpRetrieverCriteria).retrieve(Utils::convertToString);
    assertEquals(responseString, repsonse);
  }

  @Test
  public void test_response_code_200_get() throws IOException, Exception {

    String responseString = "{\"name\": \"Cabbage\"}";

    URL mockURL = mock(URL.class);
    whenNew(URL.class).withArguments(anyString()).thenReturn(mockURL);
    HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
    when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);

    doNothing().when(mockHttpURLConnection).connect();
    when(mockHttpURLConnection.getResponseCode()).thenReturn(200);
    doReturn(new ByteArrayInputStream(responseString.getBytes("UTF-8")))
        .when(mockHttpURLConnection)
        .getInputStream();

    HttpRetrieverCriteria mockHttpRetrieverCriteria = mock(HttpRetrieverCriteria.class);

    whenNew(HttpRetrieverCriteria.class).withAnyArguments().thenReturn(mockHttpRetrieverCriteria);
    when(mockHttpRetrieverCriteria.getUrl()).thenReturn(mockURL);
    when(mockHttpRetrieverCriteria.getUserAgent()).thenReturn("Mozzila/5.0");
    when(mockHttpRetrieverCriteria.gethTTPMethod())
        .thenReturn(HttpRetrieverCriteria.HTTPMethod.GET);

    String repsonse = new HttpRetriever(mockHttpRetrieverCriteria).retrieve(Utils::convertToString);
    assertEquals(responseString, repsonse);
  }

  @Test
  public void test_response_code_404_get() throws IOException, Exception {

    URL mockURL = mock(URL.class);
    whenNew(URL.class).withArguments(anyString()).thenReturn(mockURL);
    HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
    when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);

    doNothing().when(mockHttpURLConnection).connect();
    when(mockHttpURLConnection.getResponseCode()).thenReturn(404);
    when(mockHttpURLConnection.getResponseMessage()).thenReturn("Not Found");

    HttpRetrieverCriteria mockHttpRetrieverCriteria = mock(HttpRetrieverCriteria.class);

    whenNew(HttpRetrieverCriteria.class).withAnyArguments().thenReturn(mockHttpRetrieverCriteria);
    when(mockHttpRetrieverCriteria.getUrl()).thenReturn(mockURL);
    when(mockHttpRetrieverCriteria.getUserAgent()).thenReturn("Mozzila/5.0");
    when(mockHttpRetrieverCriteria.gethTTPMethod())
        .thenReturn(HttpRetrieverCriteria.HTTPMethod.GET);

    new HttpRetriever(mockHttpRetrieverCriteria).retrieve();
  }

  @Test
  public void test_response_code_200_put() throws IOException, Exception {

    String bodyContent = "{\"Version\": 1}";
    String responseString = "{\"name\": \"Cabbage\"}";

    URL mockURL = mock(URL.class);
    whenNew(URL.class).withArguments(anyString()).thenReturn(mockURL);
    HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
    when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);

    doNothing().when(mockHttpURLConnection).connect();
    doReturn(new ByteArrayInputStream(responseString.getBytes("UTF-8")))
        .when(mockHttpURLConnection)
        .getInputStream();
    when(mockHttpURLConnection.getResponseCode()).thenReturn(200);
    when(mockHttpURLConnection.getResponseMessage()).thenReturn("Success");

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    when(mockHttpURLConnection.getOutputStream()).thenReturn(byteArrayOutputStream);

    HttpRetrieverCriteria mockHttpRetrieverCriteria = mock(HttpRetrieverCriteria.class);

    whenNew(HttpRetrieverCriteria.class).withAnyArguments().thenReturn(mockHttpRetrieverCriteria);
    when(mockHttpRetrieverCriteria.getUrl()).thenReturn(mockURL);
    when(mockHttpRetrieverCriteria.getUserAgent()).thenReturn("Mozzila/5.0");
    when(mockHttpRetrieverCriteria.gethTTPMethod())
        .thenReturn(HttpRetrieverCriteria.HTTPMethod.PUT);
    when(mockHttpRetrieverCriteria.getBodyContentType())
        .thenReturn(HttpRetrieverCriteria.ContentType.JSON);
    when(mockHttpRetrieverCriteria.getBody()).thenReturn(bodyContent);

    String repsonse = new HttpRetriever(mockHttpRetrieverCriteria).retrieve(Utils::convertToString);
    assertEquals(responseString, repsonse);
  }

  @Test
  public void test_response_code_unexpected_get() throws IOException, Exception {

    URL mockURL = mock(URL.class);
    whenNew(URL.class).withArguments(anyString()).thenReturn(mockURL);
    HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
    when(mockURL.openConnection()).thenReturn(mockHttpURLConnection);

    doNothing().when(mockHttpURLConnection).connect();
    doReturn(new ByteArrayInputStream("ERROR".getBytes("UTF-8")))
        .when(mockHttpURLConnection)
        .getInputStream();
    when(mockHttpURLConnection.getResponseCode()).thenReturn(500);
    when(mockHttpURLConnection.getResponseMessage())
        .thenReturn("Well, that didn't go well did it!");

    HttpRetrieverCriteria mockHttpRetrieverCriteria = mock(HttpRetrieverCriteria.class);

    whenNew(HttpRetrieverCriteria.class).withAnyArguments().thenReturn(mockHttpRetrieverCriteria);
    when(mockHttpRetrieverCriteria.getUrl()).thenReturn(mockURL);
    when(mockHttpRetrieverCriteria.getUserAgent()).thenReturn("Mozzila/5.0");
    when(mockHttpRetrieverCriteria.gethTTPMethod())
        .thenReturn(HttpRetrieverCriteria.HTTPMethod.GET);

    new HttpRetriever(mockHttpRetrieverCriteria).retrieve();
  }

  @Test
  public void test_url_ioexception() throws IOException, Exception {

    URL mockURL = mock(URL.class);
    whenNew(URL.class).withArguments(anyString()).thenReturn(mockURL);
    HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
    when(mockURL.openConnection()).thenThrow(new IOException("Couldn't open that connection!"));

    HttpRetrieverCriteria mockHttpRetrieverCriteria = mock(HttpRetrieverCriteria.class);

    whenNew(HttpRetrieverCriteria.class).withAnyArguments().thenReturn(mockHttpRetrieverCriteria);
    when(mockHttpRetrieverCriteria.getUrl()).thenReturn(mockURL);
    when(mockHttpRetrieverCriteria.getUserAgent()).thenReturn("Mozzila/5.0");
    when(mockHttpRetrieverCriteria.gethTTPMethod())
        .thenReturn(HttpRetrieverCriteria.HTTPMethod.GET);

    HttpRetrieverImplError httpRetrieverImplError =
        Assert.assertThrows(
            HttpRetrieverImplError.class,
            () -> new HttpRetriever(mockHttpRetrieverCriteria).retrieve());

    assertEquals("Couldn't open that connection!", httpRetrieverImplError.getLocalizedMessage());
  }

  @Test
  public void test_url_unreachable() throws IOException, Exception {

    URL mockURL = mock(URL.class);
    whenNew(URL.class).withArguments(anyString()).thenReturn(mockURL);
    HttpURLConnection mockHttpURLConnection = Mockito.mock(HttpURLConnection.class);
    when(mockURL.openConnection())
        .thenThrow(new UnknownHostException("Couldn't open that connection!"));

    HttpRetrieverCriteria mockHttpRetrieverCriteria = mock(HttpRetrieverCriteria.class);

    whenNew(HttpRetrieverCriteria.class).withAnyArguments().thenReturn(mockHttpRetrieverCriteria);
    when(mockHttpRetrieverCriteria.getUrl()).thenReturn(mockURL);
    when(mockHttpRetrieverCriteria.getUserAgent()).thenReturn("Mozzila/5.0");
    when(mockHttpRetrieverCriteria.gethTTPMethod())
        .thenReturn(HttpRetrieverCriteria.HTTPMethod.GET);

    HttpRetrieverImplError httpRetrieverImplError =
        Assert.assertThrows(
            HttpRetrieverImplError.class,
            () -> new HttpRetriever(mockHttpRetrieverCriteria).retrieve());

    assertEquals("Couldn't open that connection!", httpRetrieverImplError.getLocalizedMessage());
  }
}