package com.jkhl.entrance.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/webSocket")
@Component
@Slf4j
public class MyWebSocket {

    //concurrent 包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;


    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        log.info("有新连接加入！当前连接人数" + webSocketSet.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        log.info("关系连接,当前连接人数" + webSocketSet.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);
        String resultStr = message.replace("你", "我")
                .replace("不", "")
                .replace("吗", "")
                .replace("?", "!")
                .replace("？", "!");
        sendMessage("服务器端：" + resultStr);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
    }

    /**
     * 发送消息
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("webSocket失败原因, {}", e.getMessage());
        }
    }
    public void sendInfo(String message) {
        for (MyWebSocket item : webSocketSet) {
            item.sendMessage(message);
        }
    }

    /**
     * springboot内置tomcat的话，需要配一下这个。。如果没有这个对象，无法连接到websocket
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}