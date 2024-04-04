package org.example;

import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

import static org.example.Main.GET;


public class RequestParser {

    public RequestParser(BufferedInputStream in, BufferedOutputStream out, Request request) throws IOException, URISyntaxException {
        // лимит на request line + заголовки
        final var limit = 4096;

        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        // ищем request line
        final var requestLineDelimiter = new byte[]{'\r', '\n'};
        final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestLineEnd == -1) {
            Status.StatusCode400(out);
            return;
        }

        // читаем request line
        final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            Status.StatusCode400(out);
            return;
        }

        final var method = requestLine[0];
        request.setMethod(method);

        final var uri = new URI(requestLine[1]);
        final var path = uri.getPath();
        request.setPath(path);

        // ищем заголовки
        final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
            Status.StatusCode400(out);
            return;
        }

        // отматываем на начало буфера
        in.reset();
        // пропускаем requestLine
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(headersEnd - headersStart);
        final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));
        request.setHeaders(headers);

        if (method.equals(GET)) {
            final var queryParams = URLEncodedUtils.parse(uri, Charset.defaultCharset());
            request.setQueryParams(queryParams);
        }

        // для GET тела нет
        if (!method.equals(GET)) {
            var body = "";
            in.skip(headersDelimiter.length);
            // вычитываем Content-Length, чтобы прочитать body
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());

                final var bodyBytes = in.readNBytes(length);
                body = new String(bodyBytes);
                request.setBody(bodyBytes);
            }
            // получаем параметры, переданные в теле запроса
            final var contentType = extractHeader(headers, "Content-Type");
            if (contentType.isPresent()) {
                if (contentType.get().equals("application/x-www-form-urlencoded")) {
                    final var postParams = URLEncodedUtils.parse(body, Charset.defaultCharset());
                    request.setPostParams(postParams);
                }
                if (contentType.get().contains("multipart/form-data")) {
                    request.setContentType(contentType.get());
                    final var postParams = FileUploader.doPost(request);
                    request.setPostParams(postParams);
                }
            }
        }
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

}
