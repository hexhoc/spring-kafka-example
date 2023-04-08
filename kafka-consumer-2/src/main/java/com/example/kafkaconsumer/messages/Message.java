package com.example.kafkaconsumer.messages;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Message<T> {

    // Cloud Events compliant
    private String type;
    private String id = UUID.randomUUID().toString(); // unique id of this message
    private String source = "producer-1";
    private LocalDateTime time = LocalDateTime.now();
    private T data;
    private String datacontenttype = "application/json";
    private String specversion = "1.0";

    // Extension attributes
    private String traceid = UUID.randomUUID().toString(); // trace id, default: new unique
    private String correlationid; // id which can be used for correlation later if required

    public Message() {
    }

    public Message(String type, T payload) {
        this.type = type;
        this.data = payload;
    }

    public Message(String type, String traceid, T payload) {
        this.type = type;
        this.traceid = traceid;
        this.data = payload;
    }

    @Override
    public String toString() {
        return "Message [type=" + type + ", id=" + id + ", time=" + time + ", data=" + data + ", correlationid=" + correlationid + ", traceid=" + traceid + "]";
    }
}
