package org.catchmycode.automation.common;

import java.util.Map;

/**
 * Domain object to handle any/all HTTP responses
 */
public class HttpResponse {
    
    private String content;
    private HttpStatus httpStatus;
    private Map<String, String> httpHeaders;

    public HttpResponse withContent(String content) {
        this.content = content;
        return this;
    }

    public HttpResponse withHttpStatusCode(int statusCode) {
        this.httpStatus = new HttpStatus(statusCode);
        return this;
    }

    public HttpResponse withHttpHeaders(Map<String,String> httpHeaders) {
        this.httpHeaders = httpHeaders;
        return this;
    }

    public String getContent() {
        return content;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    /**
     * Enum wrapper class for HTTP status {@link org.apache.http.HttpStatus}
     */
    public static class HttpStatus implements org.apache.http.HttpStatus {

        private int statusCode;

        /**
         * Intentionally private
         */
        private HttpStatus () {
            //noop
        }

        /**
         * Default constructor
         * @param statusCode the status code
         */
        public HttpStatus(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
