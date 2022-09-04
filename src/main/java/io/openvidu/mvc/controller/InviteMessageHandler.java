package io.openvidu.mvc.controller;

import io.openvidu.dto.SessionUser;
import io.openvidu.entity.User;
import io.openvidu.mvc.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

import static io.openvidu.Const.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class InviteMessageHandler extends TextWebSocketHandler {

    private final UserService userService;

    // <email, session> map
    Map<String, WebSocketSession> userSessionsMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("afterConnectionEstablished 실행");
        Map<String, Object> httpSession = session.getAttributes();
        SessionUser sessionUser = (SessionUser) httpSession.get(LOGIN_USER);
        if(sessionUser != null){
            log.info("로그인 유저 이메일 : {}", sessionUser.getEmail());
            userSessionsMap.put(sessionUser.getEmail(), session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("메시지 payload : {}", payload);
        if(!payload.isEmpty()){
            String str[] = payload.split(",");

            if(str.length == 3){
                String receiverUsername = str[0];
                String senderUsername = str[1];
                String sessionName = str[2];

                User receiver = userService.findUserByUsername(receiverUsername);
                if(receiver != null){
                    WebSocketSession receiverSession = userSessionsMap.get(receiver.getEmail());
                    log.info("리시버 세션 : {}", receiverSession);

                    // 초대 받는 사람이 접속중!
                    if(receiverSession != null){
                        log.info("리시버가 접속 중이므로 초대메시지를 전송합니다.");
                        TextMessage textMessage = new TextMessage(
                                "<a href='/invite?session-name=" + sessionName + "'>" +
                                senderUsername + "님이 " +
                                sessionName + " 채팅방에 " +
                                "초대하셨습니다." + "</a>");
                        receiverSession.sendMessage(textMessage);
                    }
                } else {
                    log.info("{}은 존재하지 않는 유저입니다.", receiverUsername);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> httpSession = session.getAttributes();
        SessionUser sessionUser = (SessionUser) httpSession.get(LOGIN_USER);
        if(sessionUser != null){
            log.info("closed 세션 email : {}", sessionUser.getEmail());
            userSessionsMap.remove(sessionUser.getEmail());
        }
    }
}
