package org.example;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
             final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            final var method = parts[0];
            final var path = parts[1];


            if (parts.length != 3) {
                // just close socket
                Status.StatusCode400(out);
                return;
            }

            // read next lines
            // find headers
            String line;
            final Map<String, String> headers = new HashMap<>();
            while (!(line= in.readLine()).equals("")){
                int indexOf = line.indexOf(":");
                String name = line.substring(0, indexOf);
                String value = line.substring(indexOf + 2);
                headers.put(name,value);
            }

            // Вы принимаете запрос, парсите его целиком, как мы сделали на лекции, и собираете объект, типа Request.
            final var request = new Request(method, path, socket.getInputStream(), headers);

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

            handler.handle(request, out);

        } catch (IOException e) {
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
