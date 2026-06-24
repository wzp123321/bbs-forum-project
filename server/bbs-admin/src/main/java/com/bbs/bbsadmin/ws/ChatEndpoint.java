package com.bbs.bbsadmin.ws;

import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 公共聊天室端点
 * <p>
 * URL: ws://host/ws/chat?token=xxx
 * 鉴权: 握手时解析 token 写入 userId
 * 消息格式: 客户端只发纯文本,服务端包装为 ChatMessage 广播
 */
@Component
@ServerEndpoint(value = "/ws/chat", configurator = HandshakeInterceptor.class)
public class ChatEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ChatEndpoint.class);

    /** 在线 session: sessionId -> session */
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    /** sessionId -> userId */
    private static final Map<String, String> SESSION_USERS = new ConcurrentHashMap<>();
    /** sessionId -> userName */
    private static final Map<String, String> SESSION_NAMES = new ConcurrentHashMap<>();

    /** 最近 50 条消息, 新连接时回放 */
    private static final List<ChatMessage> HISTORY = new CopyOnWriteArrayList<>();
    private static final int MAX_HISTORY = 50;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** 静态注入, 解决 @ServerEndpoint 无法直接注入 Spring Bean 的问题 */
    private JwtUtil jwtUtil;
    private UserInfoMapper userInfoMapper;
    private ObjectMapper objectMapper;

    @Autowired
    public void setStatics(JwtUtil jwtUtil, UserInfoMapper userInfoMapper, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userInfoMapper = userInfoMapper;
        this.objectMapper = objectMapper;
    }

    @OnOpen
    public void onOpen(Session session) {
        // 解析 token (从 queryString)
        String token = extractToken(session);
        String userId = null;
        String userName = "匿名";
        if (StringUtils.hasText(token) && jwtUtil != null) {
            try {
                Map<String, Object> claims = jwtUtil.parse(token);
                userId = String.valueOf(claims.get("userId"));
                if (userInfoMapper != null) {
                    UserInfo u = userInfoMapper.selectById(userId);
                    if (u != null) userName = u.getUserName();
                }
            } catch (Exception ignored) {
                // 匿名进入
            }
        }

        SESSIONS.put(session.getId(), session);
        SESSION_USERS.put(session.getId(), userId == null ? "anon-" + session.getId().substring(0, 6) : userId);
        SESSION_NAMES.put(session.getId(), userName);
        broadcastSystem((userName == null ? "匿名" : userName) + " 加入了聊天室");
        sendHistory(session);
    }

    @OnMessage
    public void onMessage(String payload, Session session) {
        if (payload == null) return;
        String text = payload.trim();
        if (text.isEmpty() || text.length() > 500) return;
        // 简单敏感词
        if (containsBad(text)) {
            sendTo(session, ChatMessage.ofSystem("消息包含敏感内容, 已被拦截", now()));
            return;
        }
        ChatMessage m = ChatMessage.ofUser(
                SESSION_USERS.get(session.getId()),
                SESSION_NAMES.get(session.getId()),
                text,
                now()
        );
        pushHistory(m);
        broadcast(m);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session.getId());
        String name = SESSION_NAMES.remove(session.getId());
        String uid = SESSION_USERS.remove(session.getId());
        broadcastSystem((name == null ? "匿名" : name) + " 离开了聊天室");
        log.info("ws close userId={}", uid);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.warn("ws error", error);
    }

    // ---------- helpers ----------

    private String extractToken(Session session) {
        try {
            Map<String, List<String>> params = session.getRequestParameterMap();
            List<String> tokens = params.get("token");
            if (tokens != null && !tokens.isEmpty()) {
                return tokens.get(0);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void broadcast(ChatMessage m) {
        String json;
        try {
            json = objectMapper.writeValueAsString(m);
        } catch (Exception e) {
            log.warn("ws serialize fail", e);
            return;
        }
        for (Session s : SESSIONS.values()) {
            sendTo(s, json);
        }
    }

    private void broadcastSystem(String text) {
        ChatMessage m = ChatMessage.ofSystem(text, now());
        pushHistory(m);
        broadcast(m);
    }

    private void sendTo(Session s, Object payload) {
        try {
            String text = payload instanceof String ? (String) payload : objectMapper.writeValueAsString(payload);
            s.getBasicRemote().sendText(text);
        } catch (Exception e) {
            // 单点失败不影响其他
        }
    }

    private void sendHistory(Session s) {
        if (HISTORY.isEmpty()) return;
        try {
            String json = objectMapper.writeValueAsString(HISTORY);
            s.getBasicRemote().sendText(json);
        } catch (Exception ignored) {
        }
    }

    private void pushHistory(ChatMessage m) {
        HISTORY.add(m);
        while (HISTORY.size() > MAX_HISTORY) {
            HISTORY.remove(0);
        }
    }

    private static String now() {
        return LocalDateTime.now().format(TIME_FMT);
    }

    private static boolean containsBad(String s) {
        String lower = s.toLowerCase();
        return lower.contains("badword1") || lower.contains("badword2") || lower.contains("敏感词");
    }
}
