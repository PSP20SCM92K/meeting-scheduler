package com.code.test.meetingscheduler.model;

public class IndexNode {
    Object data;
    Object address;

    public IndexNode(Object data, Object address) {
        this.data = data;
        this.address = address;
    }

    public Object getData() {
        return data;
    }

    public Object getAddress() {
        return address;
    }
}
