package com.bbs.bbsadmin.ws;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.stereotype.Component;

/**
 * 端点配置器 (空实现, 保留扩展点)
 */
@Component
public class HandshakeInterceptor extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);
    }
}
