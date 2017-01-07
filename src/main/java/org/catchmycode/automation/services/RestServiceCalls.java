package org.catchmycode.automation.services;

import org.catchmycode.automation.common.HttpResponse;
import org.catchmycode.automation.environment.PropertyLoader;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.*;

import static org.testng.Assert.fail;

public interface RestServiceCalls {

    String URL_DATA_DELIMITER = "&";
    String DEFAULT_DATA_DELIMITER = "\\|\\|";
    Logger LOGGER = LoggerFactory.getLogger(RestServiceCalls.class);

    //Request builder for the http connection
    int TIMEOUT_IN_SECONDS = 30;
    RequestConfig HTTP_REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(TIMEOUT_IN_SECONDS * 1000)
            .setConnectionRequestTimeout(TIMEOUT_IN_SECONDS * 1000).setSocketTimeout(TIMEOUT_IN_SECONDS * 1000).build();
    CloseableHttpClient httpClient = null;

    public enum Method {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE
    }

    /**
     * Allow each test case to pass in an application URI like "/doSomething"
     *
     * @return the path
     */
    public String getApplicationPath();

    /**
     * The HTTP protocol
     *
     * @return the value
     */
    default String getServiceProtocol() {
        return PropertyLoader.getInstance().getProperty("service.protocol");
    }

    /**
     * The HTTP host
     *
     * @return the value
     */
    default String getServiceHost() {
        return PropertyLoader.getInstance().getProperty("service.host.name");
    }

    /**
     * The HTTP port
     *
     * @return the value
     */
    default Integer getServicePort() {
        return PropertyLoader.getInstance().getPropertyAsInteger("service.port.number");
    }

    /**
     * The HTTP context path
     *
     * @return the value
     */
    default String getServiceContextPath() {
        return PropertyLoader.getInstance().getProperty("service.context");
    }

    /**
     * The service apiKey
     *
     * @return the value
     */
    default String getServiceApiKey() {
        return PropertyLoader.getInstance().getProperty("service.api.key");
    }

    /**
     * This is internal only - it is meant to support runtime and unit tests
     *
     * @return mock client
     */
    default CloseableHttpClient getHttpClient() {

        if (httpClient == null) {
            return HttpClientBuilder.create().setDefaultRequestConfig(HTTP_REQUEST_CONFIG).build();
        }
        return this.httpClient;
    }

    /**
     * The ingestion HTTP protocol
     *
     * @return the value
     */
    default String getServicePostProtocol() {
        return PropertyLoader.getInstance().getProperty("service.post.protocol");
    }

    /**
     * The ingestion HTTP host
     *
     * @return the value
     */
    default String getServicePostHost() {
        return PropertyLoader.getInstance().getProperty("service.post.host.name");
    }

    /**
     * The ingestion HTTP port
     *
     * @return the value
     */
    default Integer getServicePostPort() {
        return PropertyLoader.getInstance().getPropertyAsInteger("service.post.port.number");
    }

    /**
     * The ingestion HTTP context path
     *
     * @return the value
     */
    default String getServicePostPath() {
        try {
            return PropertyLoader.getInstance().getProperty("service.post.api.path");
        } catch (AssertionError ex) {
            return "";
        }
    }

    /**
     * The ingestion service apiKey
     *
     * @return the value
     */
    default String getServicePostApiKey() {
        try {
            return PropertyLoader.getInstance().getProperty("service.post.api.key");
        } catch (AssertionError ex) {
            return "";
        }
    }

    /**
     * Retrieve the response (content) from an org.apache.http.HttpResponse as a string
     * @param response the HTTP Response to extract the response text from
     * @return the response or a default "No Content"
     * @throws IOException thrown when there is an issue converting the entity to a byte array
     */
    default String getContent(org.apache.http.HttpResponse response) throws IOException {
        byte[] entityContent;
        if(response.getEntity() != null){
            entityContent = EntityUtils.toByteArray(response.getEntity());
        }else {
            entityContent = EntityUtils.toByteArray(new StringEntity("No Content"));
        }
        return new String(entityContent);
    }

    /**
     * Get the query string parameters from a string like the following...
     * <p>
     * 45.43,12.38||language=en-US,units=e||200
     *
     * @param data the content
     * @return the list of {@link org.apache.http.NameValuePair}
     */
    default Object[] getPathParameters(String data) {

        if (data == null || data.isEmpty()) {
            return null;
        }

        return data.split(DEFAULT_DATA_DELIMITER);
    }

    /**
     * Get the query string parameters from a string like the following...
     * <p>
     * 45.43,12.38||language=en-US,units=e||200
     *
     * @param data the content
     * @return the list of {@link org.apache.http.NameValuePair}
     */
    default List<NameValuePair> getQueryStringParameters(String data) {

        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> valuesToConvert = Arrays.asList(data.split(DEFAULT_DATA_DELIMITER));

        List<NameValuePair> pairs = new ArrayList<>();
        for (String toConvert : valuesToConvert) {
            pairs.add(new BasicNameValuePair(toConvert.split("=")[0], toConvert.split("=")[1]));
        }

        return pairs;
    }

    default List<NameValuePair> getQueryStringParameters(String data, String defaultDataDelimiter) {

        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> valuesToConvert = Arrays.asList(data.split(
                (defaultDataDelimiter != null) ? defaultDataDelimiter: URL_DATA_DELIMITER));

        List<NameValuePair> pairs = new ArrayList<>();
        for (String toConvert : valuesToConvert) {
            final String[] pair = toConvert.split("=");
            pairs.add(new BasicNameValuePair(pair[0], pair[1]));
        }

        return pairs;
    }

    /**
     * Constructs the URI from the test context
     *
     * @param pathParametersArray   is the parameters list for the URI
     * @param queryStringParameters is the query parameters for the URI
     * @return the URI to complete an API call
     */
    default URI buildUriForOutbound(Object[] pathParametersArray, List<NameValuePair> queryStringParameters) {
        if (queryStringParameters == null) {
            queryStringParameters = new ArrayList<>();
        }

        try {
            if (getServiceApiKey() == null || getServiceApiKey().isEmpty()) {
                return new URIBuilder()
                        .setScheme(getServiceProtocol())
                        .setHost(getServiceHost())
                        .setPort(getServicePort())
                        .setPath(MessageFormat.format(getServiceContextPath() + getApplicationPath(), pathParametersArray))
                        .setParameters(queryStringParameters)
                        .build();
            } else {
                return new URIBuilder()
                        .setScheme(getServiceProtocol())
                        .setHost(getServiceHost())
                        .setPort(getServicePort())
                        .setPath(MessageFormat.format(getServiceContextPath() + getApplicationPath(), pathParametersArray))
                        .setParameters(queryStringParameters)
                        .setParameter("apiKey", getServiceApiKey())
                        .build();
            }
        } catch (URISyntaxException ex) {
            LOGGER.error("Cannot build the uri to connect to the specified service {} {} {} {} {}", getServiceHost(),
                    getServicePort(), getServiceContextPath());
            Assert.fail();
            return null;
        }
    }

    /**
     * Constructs the URI from the test context
     *
     * @param pathParametersArray   is the parameters list for the URI
     * @param queryStringParameters is the query parameters for the URI
     * @return the URI to complete an API call
     */
    default URI buildUriForPost(Object[] pathParametersArray, List<NameValuePair> queryStringParameters) {
        try {

            if (getServicePostApiKey() == null || getServicePostApiKey().isEmpty()) {
                return new URIBuilder()
                        .setScheme(getServicePostProtocol())
                        .setHost(getServicePostHost())
                        .setPort(getServicePostPort())
                        .setPath(MessageFormat.format(getServicePostPath() + getApplicationPath(), pathParametersArray))
                        .setParameters(queryStringParameters)
                        .build();
            } else {
                return new URIBuilder()
                        .setScheme(getServicePostProtocol())
                        .setHost(getServicePostHost())
                        .setPort(getServicePostPort())
                        .setPath(MessageFormat.format(getServicePostPath() + getApplicationPath(), pathParametersArray))
                        .setParameters(queryStringParameters)
                        .setParameter("apiKey", getServicePostApiKey())
                        .build();
            }
        } catch (URISyntaxException ex) {
            LOGGER.error("Cannot build the uri to connect to the specified service {} {} {} {} {}", getServicePostHost(),
                    getServicePostPort(), getServicePostPath());
            Assert.fail();
            return null;
        }
    }

    /**
     * Gets the content from the any REST API
     *
     * @param pathParameters        are url parameters that are used to build the uri
     * @param queryStringParameters are the query parameters (parameters added after the '?') used to complete an API call
     * @param headers               are a map of header key-value pairs to append to a particular API call
     * @return the httpResponse {@link HttpResponse}
     */
    default HttpResponse getContentWithHttpResponse(String pathParameters, List<NameValuePair> queryStringParameters,
                                                    Map<String, String> headers) {

        Object[] pathParametersArray = getPathParameters(pathParameters);
        URI uri = buildUriForOutbound(pathParametersArray, queryStringParameters);
        CloseableHttpClient httpClient = getHttpClient();

        long now = System.currentTimeMillis();

        CloseableHttpResponse response = null;
        try {

            HttpGet httpGet = new HttpGet(uri);
            if (headers != null && !headers.isEmpty()) {
                for (String header : headers.keySet()) {
                    httpGet.addHeader(header, headers.get(header));
                }
            }
            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpResponse.HttpStatus.SC_OK
                    || response.getStatusLine().getStatusCode() == HttpResponse.HttpStatus.SC_NO_CONTENT
                    || response.getStatusLine().getStatusCode() == HttpResponse.HttpStatus.SC_BAD_REQUEST) {


                final Map<String, String> responseHeaders = new HashMap<>();
                if (response.getAllHeaders() != null) {
                    for (Header head : response.getAllHeaders()) {
                        responseHeaders.put(head.getName(), head.getValue());
                    }
                }
                return new HttpResponse().withContent(getContent(response))
                        .withHttpHeaders(responseHeaders)
                        .withHttpStatusCode(response.getStatusLine().getStatusCode());

            } else {
                LOGGER.warn("Received an invalid status code from the OAPI server: {}: {}",
                        response.getStatusLine().getStatusCode(),
                        new String((EntityUtils.toByteArray(response.getEntity()))));
                return null;
            }

        } catch (IOException ex) {
            LOGGER.error("Error communicating with the OAPI server: {}", uri, ex);
            return null;
        } finally {
            LOGGER.debug("Took {} ms to access the hourly forecast OAPI", (System.currentTimeMillis() - now));
            try {
                response.close();
            } catch (Exception ex) {
                //noop
            }
        }
    }

    /**
     * Base method for delivering content to the HTTP server (or delivering content to a web application)
     *
     * @param content               the content to push
     * @param method                the required http method, defaults to POST {@link Method}
     * @param pathParameters        the path parameters
     * @param queryStringParameters the query string parameters
     * @param headers               the headers
     * @param contentType           the {@link org.apache.http.entity.ContentType}
     * @return the {@link HttpResponse}
     */
    default HttpResponse deliverContentWithHttpResponse(String content, Method method, String pathParameters,
                                                        String queryStringParameters, Map<String, String> headers,
                                                        ContentType contentType) {


        if (method == null) {
            LOGGER.warn("No method type set, defaulting to POST");
            method = Method.POST;
        }

        if (!method.equals(Method.DELETE) && content == null) {
            fail("Content is required to ingest for POST, PUT, PATCH etc.");
        }

        List<NameValuePair> queryStringParametersList = getQueryStringParameters(queryStringParameters);
        Object[] pathParametersList = getPathParameters(pathParameters);

        URI uri = buildUriForPost(pathParametersList, queryStringParametersList);

        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {

            if (contentType == null) {
                contentType = ContentType.TEXT_PLAIN;
            }

            HttpRequestBase httpRequest = null;
            if (method.equals(Method.DELETE)) {
                httpRequest = new HttpDelete(uri);
            } else if (method.equals(Method.POST)) {
                httpRequest = new HttpPost(uri);
                ((HttpPost) httpRequest).setEntity(new StringEntity(content, contentType));
            } else if (method.equals(Method.PATCH)) {
                httpRequest = new HttpPatch(uri);
                ((HttpPatch) httpRequest).setEntity(new StringEntity(content, contentType));
            } else if (method.equals(Method.PUT)) {
                httpRequest = new HttpPut(uri);
                ((HttpPut) httpRequest).setEntity(new StringEntity(content, contentType));
            }

            if (headers != null && !headers.isEmpty()) {
                for (String header : headers.keySet()) {
                    httpRequest.addHeader(header, headers.get(header));
                }
            }

            response = httpClient.execute(httpRequest);

            LOGGER.debug("Publishing content to the ingestion API. {}:{}",
                    response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

            Map<String, String> responseHeaders = new HashMap<>();
            if (response.getAllHeaders() != null) {
                for (Header head : response.getAllHeaders()) {
                    responseHeaders.put(head.getName(), head.getValue());
                }
            }

            if(method.equals(Method.POST) && !(response.getEntity() == null)) {
                return new HttpResponse()
                        .withContent(new String(EntityUtils.toByteArray(response.getEntity())))
                        .withHttpHeaders(responseHeaders)
                        .withHttpStatusCode(response.getStatusLine().getStatusCode());
            }else {
                return new HttpResponse()
                        .withHttpHeaders(responseHeaders)
                        .withHttpStatusCode(response.getStatusLine().getStatusCode());
            }

        } catch (Exception ex) {
            LOGGER.error("Error communicating with the ingestion server: {}", uri, ex);
            fail("Error communicating with the ingestion server");
        } finally {
            try {
                response.close();
            } catch (Exception ex) {
            }
        }

        return null;
    }
}
