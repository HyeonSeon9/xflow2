package com.nhnacademy.aiot.message;

public abstract class Message {
    static int count;
    final String id;
    long creationTime;

    Message() {
        count++;
        id = getClass().getSimpleName() + count;
        creationTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public static int getCount() {
        return count;
    }
}
