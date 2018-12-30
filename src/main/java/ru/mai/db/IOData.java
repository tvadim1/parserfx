package ru.mai.db;

import java.io.Serializable;

public final class IOData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String x;
    private String y;

    public IOData(final String x, final String y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}