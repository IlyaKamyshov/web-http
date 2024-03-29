package org.example;

import java.io.IOException;

public class Main {

    final static int POOL_SIZE = 64;
    final static int PORT = 9999;

    public static void main(String[] args) {
        Server server = new Server(POOL_SIZE);

        // добавление хендлеров (обработчиков) для // ("/index.html", "/spring.svg", "/spring.png", "/resources.html",
        // "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        server.addHandler("GET", "/classic.html", (request, out) -> {
            try{
                Handlers.classicGET(request, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("GET", "/index.html", (request, out) -> {
            try{
                Handlers.modernGET(request, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("PUT", "/resources.html", (request, out) -> {
            try{
                Handlers.modernGET(request, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.start(PORT);
    }

}

