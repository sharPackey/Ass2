package cn.edu.sustech.cs209.chatting.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;
import cn.edu.sustech.cs209.chatting.common.UserList;


public class Server {
    private final Map<String, clientService> clientServiceMap;
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clientServiceMap = new HashMap<>();
    }

    public void keepListen() {
        System.out.println("The Server is listening for client...");
        while (true) {
            try {
                Socket socket = this.serverSocket.accept();
                clientService clientService = new clientService(socket);
                clientService.start();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }


    private class clientService extends Thread {
        private String username;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;

        public clientService(Socket socket) {
            try {
                this.inputStream = new ObjectInputStream(socket.getInputStream());
                this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Message clientMsg = (Message) inputStream.readObject();

                    if(clientMsg.getType() == MessageType.CONNECTED){
                        this.username = clientMsg.getSentBy();
                        clientServiceMap.put(this.username, this);
                        UserList.addUser(this.username);
                        clientServiceMap.forEach((s, clientService) -> clientService.sendUserList());

                    }
                    else if(clientMsg.getType() == MessageType.USER){
                        sendUserList();
                    }
                    else if(clientMsg.getType() == MessageType.PRIVATE){
                        sendTo(clientMsg.getSendTo(), clientMsg);
                    }
                    else if(clientMsg.getType() == MessageType.GROUP){
                        String members = clientMsg.getSendTo();
                        List<String> toSend = Arrays.asList(members.split(", "));
                        toSend.forEach(s -> {
                            if(!s.equals(this.username)){
                                try {
                                    sendTo(s, clientMsg);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                } catch (IOException | ClassNotFoundException e) {
                    clientServiceMap.remove(username);
                    UserList.removeUser(username);
                    clientServiceMap.forEach((s, clientService) -> clientService.sendUserList());
                    break;
                }
            }
        }

        public synchronized void sendTo(String username, Message message) throws IOException {
            clientService service;
            if(clientServiceMap.containsKey(username)){
                service = clientServiceMap.get(username);
                service.send(message);
            }
        }

        public synchronized void send(Message message) throws IOException{
            outputStream.writeObject(message);
        }

        public void sendUserList(){
            Message message = new Message(MessageType.NOTIFICATION,
                    "server", this.username, UserList.listString());
            try {
                outputStream.writeObject(message);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }
}
