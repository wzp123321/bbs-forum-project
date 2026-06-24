package com.bbs.bbsadmin.ws;

import lombok.Data;

/**
 * 聊天消息 DTO
 * <p>
 * type:
 *  - 1 文本
 *  - 2 系统提示
 */
@Data
public class ChatMessage {
    /** 类型: 1用户消息 2系统提示 */
    private Integer type;
    /** 发送者 userId (系统消息为 null) */
    private String userId;
    /** 发送者昵称 */
    private String userName;
    /** 消息内容 */
    private String content;
    /** 服务端时间 (ISO 字符串) */
    private String time;

    public static ChatMessage ofUser(String userId, String userName, String content, String time) {
        ChatMessage m = new ChatMessage();
        m.type = 1;
        m.userId = userId;
        m.userName = userName;
        m.content = content;
        m.time = time;
        return m;
    }

    public static ChatMessage ofSystem(String content, String time) {
        ChatMessage m = new ChatMessage();
        m.type = 2;
        m.content = content;
        m.time = time;
        return m;
    }
}
