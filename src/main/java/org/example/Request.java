package org.example;

import org.apache.hc.core5.http.NameValuePair;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    private final String body;
    private final List<String> headers;
    private final List<NameValuePair> queryParams;
    private final List<NameValuePair> postParams;

    public Request(String method, String path, String body, List<String> headers,
                   List<NameValuePair> queryParams, List<NameValuePair> postParams) throws URISyntaxException {
        this.method = method;
        this.path = path;
        this.body = body;
        this.headers = headers;
        this.queryParams = queryParams;
        this.postParams = postParams;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
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
                ", body='" + body + '\'' +
                '}';
    }
}
