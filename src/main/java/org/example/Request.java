package org.example;

import org.apache.commons.fileupload.RequestContext;
import org.apache.hc.core5.http.NameValuePair;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request implements RequestContext {
    private String method;
    private String path;
    private byte[] body;
    private List<String> headers;
    private List<NameValuePair> queryParams;
    private List<NameValuePair> postParams;
    private String contentType;
    private int contentLength;

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setQueryParams(List<NameValuePair> queryParams) {
        this.queryParams = queryParams;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<String> getQueryParam(String name) {
        return queryParams.stream()
                .filter(param -> Objects.equals(param.getName(), name))
                .map(param -> param.getValue())
                .collect(Collectors.toList());
    }

    public void setPostParams(List<NameValuePair> postParams) {
        this.postParams = postParams;
    }

    public List<NameValuePair> getPostParams() {
        return postParams;
    }

    public List<String> getPostParam(String name) {
        return postParams.stream()
                .filter(param -> Objects.equals(param.getName(), name))
                .map(param -> param.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", query=" + queryParams + '\'' +
                ", post=" + postParams + '\'' +
                ", headers=" + headers +
                ", body='" + new String(body) + '\'' +
                '}';
    }

    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(body);
    }

}
