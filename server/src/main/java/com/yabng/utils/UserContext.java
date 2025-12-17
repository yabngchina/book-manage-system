package com.yabng.utils;

public class UserContext {
    private static final ThreadLocal<Integer> userContext = new ThreadLocal<>();

    public static Integer get() {
        return userContext.get();
    }

    public static void set(Integer id) {
        userContext.set(id);
    }

    public static void remove() {
        userContext.remove();
    }
}
