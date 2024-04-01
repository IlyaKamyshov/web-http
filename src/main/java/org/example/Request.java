package org.example;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    private final InputStream body;
    private final Map<String,String> headers;
    private final List<NameValuePair> queryParams;

    public Request(String method, String path, InputStream body, Map<String,String> headers) throws URISyntaxException {
        this.method = method;
        URI uri = new URI(path);
        this.path = uri.getPath();
        this.body = body;
        this.headers =headers;
        this.queryParams = URLEncodedUtils.parse(uri, Charset.defaultCharset());
    }

    public String getMethod(){
        return method;
    }

    public String getPath(){
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

    @Override
    public String toString(){
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", query=" + queryParams + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
