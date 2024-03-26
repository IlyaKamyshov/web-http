package org.example;

public class Main {

    final static int POOL_SIZE = 64;
    final static int PORT = 9999;

    public static void main(String[] args) {
        Server server = new Server(POOL_SIZE);
        server.start(PORT);
    }

}

