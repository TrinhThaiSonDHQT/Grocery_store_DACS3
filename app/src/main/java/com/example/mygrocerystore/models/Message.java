package com.example.mygrocerystore.models;

public class Message {
    String messageId,message,senderId,imageUrl;
    Long timeStamp;

    public Message() {
    }

    public Message(String messageId, String message, String senderId, String imageUrl, Long timeStamp) {
        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
