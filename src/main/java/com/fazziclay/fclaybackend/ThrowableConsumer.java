package com.fazziclay.fclaybackend;

public interface ThrowableConsumer <E extends Throwable, T> {
    void accept(T o) throws E;
}
