package org.example;

import java.io.IOException;

public class Main {

    final static int POOL_SIZE = 64;
    final static int PORT = 9999;
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static void main(String[] args) {
        Server server = new Server(POOL_SIZE);

        // добавление хендлеров (обработчиков)

        server.addHandler(GET, "/default-get.html", (request, out) -> {
            try{
                Handlers.formHandler(request, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler(POST, "/default-get.html", (request, out) -> {
            try{
                Handlers.formHandler(request, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.start(PORT);
    }

}

