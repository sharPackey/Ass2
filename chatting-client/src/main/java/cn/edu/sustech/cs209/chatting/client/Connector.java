package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;
import cn.edu.sustech.cs209.chatting.common.UserList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Connector implements Runnable {
    public String username;
    private Socket socket;
    private static ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final Controller controller;

    @Override
    public void run() {
        try{
            connect();
        } catch (IOException e){
            e.printStackTrace();
        }
        try{
            while(socket.isConnected()) {
                Message message = (Message) inputStream.readObject();
                if(message.getType() == MessageType.NOTIFICATION){
                    UserList.setUserList(Arrays.asList(message.getData().split(", ")));
                    this.controller.setCurrentOnlineCnt();
                }
                else if(message.getType() == MessageType.PRIVATE){
                    this.controller.handleReceive(message);
                }
                else if(message.getType() == MessageType.GROUP){
                    this.controller.handleReceive(message);
                }
            }
        } catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
    }

    public Connector(String username, Controller controller){
        this.username = username;
        this.controller = controller;
        try{
            this.socket = new Socket("localhost", 8080);
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            inputStream = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void connect() throws IOException {
        Message msg = new Message(MessageType.CONNECTED, this.username, "server", "hello");
        outputStream.writeObject(msg);
    }

    public static void send(Message message){
        try {
            outputStream.writeObject(message);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
