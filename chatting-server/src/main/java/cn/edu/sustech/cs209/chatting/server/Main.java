package cn.edu.sustech.cs209.chatting.server;

import java.io.*;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Starting server");
        ServerSocket serverSocket = new ServerSocket(8080);
        Server server = new Server(serverSocket);
        server.keepListen();
    }

}
