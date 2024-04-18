package ch.donkeycode.backendui.dto;

public class MessageDto<T> {
    Type type;
    T payload;


    public enum Type {
        UPDATE,
        FEEDBACK
    }
}
