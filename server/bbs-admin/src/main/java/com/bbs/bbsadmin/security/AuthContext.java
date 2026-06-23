package com.bbs.bbsadmin.security;

/**
 * 当前登录用户上下文 (基于 ThreadLocal)
 * 在 AuthInterceptor 解析 token 后写入,在请求结束清空
 */
public class AuthContext {

    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_NAME = new ThreadLocal<>();

    public static void set(String userId, String userName) {
        USER_ID.set(userId);
        USER_NAME.set(userName);
    }

    public static String userId() {
        return USER_ID.get();
    }

    public static String userName() {
        return USER_NAME.get();
    }

    public static void clear() {
        USER_ID.remove();
        USER_NAME.remove();
    }
}
