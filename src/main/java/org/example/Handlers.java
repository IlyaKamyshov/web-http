package org.example;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Handlers {

    static void classicGET(Request request, BufferedOutputStream out) throws IOException {

        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);
        final var template = Files.readString(filePath);
        final var content = template.replace("{time}", LocalDateTime.now().toString()).getBytes();

        Status.StatusCode200(out, mimeType, content.length);
        out.write(content);
        out.flush();

    }

    static void modernGET(Request request, BufferedOutputStream out) throws IOException {

        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);

        Status.StatusCode200(out, mimeType, length);
        Files.copy(filePath, out);
        out.flush();

    }

    static void formHandler(Request request, BufferedOutputStream out) throws IOException {

        final var filePath = Path.of(".", "static", request.getPath());
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);

        Status.StatusCode200(out, mimeType, length);
        Files.copy(filePath, out);
        out.flush();

    }


}
