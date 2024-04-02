package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    // Самый простой способ хранить хендлеры — это использовать в качестве ключей метод и путь.
    // Можно сделать как Map внутри Map, так и отдельные Map на каждый метод.
    final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();
    final ExecutorService threadPool;

    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                threadPool.submit(() -> connection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connection(Socket socket) {
        try (socket;
             final var in = new BufferedInputStream(socket.getInputStream());
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            RequestParser parser = new RequestParser(in, out);

            // Вы принимаете запрос, парсите его целиком, как мы сделали на лекции, и собираете объект, типа Request.
            final var request = new Request(parser.getMethod(), parser.getPath(), parser.getBody(), parser.getHeaders(),
                    parser.getQueryParams(), parser.getPostParams());

            // На основании данных из Request вы выбираете хендлер (он может быть только один),
            // который и будет обрабатывать запрос.
            // Поиск хендлера заключается в том, что вы выбираете по нужному методу все зарегистрированные хендлеры,
            // а затем перебираете по пути. Используйте пока точное соответствие: считайте, что у вас все
            // запросы без Query String.
            // Найдя нужный хендлер, достаточно вызвать его метод handle, передав туда Request и BufferedOutputStream.
            var handler = handlers.get(request.getMethod()).get(request.getPath());

            if (handler == null) {
                Status.StatusCode404(out);
                return;
            }

            System.out.println("\n" + request);
//            System.out.println("\n" + request.getPostParam("value"));

            handler.handle(request, out);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(path, handler);
    }

}
