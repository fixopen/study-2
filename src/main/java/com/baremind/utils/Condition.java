package com.baremind.utils;

/**
 * Created by fixopen on 28/10/2016.
 */
public class Condition {
    private String op;
    private Object value;

    public Condition(String op, Object value) {
        this.op = op;
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
