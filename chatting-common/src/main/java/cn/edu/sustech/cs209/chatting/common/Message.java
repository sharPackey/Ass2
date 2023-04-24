package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType msgType;
    private String sentBy;

    private String sendTo;

    private final String data;

    public Message(MessageType messageType, String sentBy, String sendTo, String data) {
        this.msgType = messageType;
        this.sentBy = sentBy;
        this.sendTo = sendTo;
        this.data = data;
    }

    public MessageType getType() {
        return msgType;
    }

    public String getSentBy() {
        return sentBy;
    }

    public String getSendTo() {
        return sendTo;
    }

    public String getData() {
        return data;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

}
