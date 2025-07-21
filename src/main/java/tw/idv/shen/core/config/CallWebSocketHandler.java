package tw.idv.shen.core.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import tw.idv.shen.core.util.TcpSender;

public class CallWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LogManager.getLogger(CallWebSocketHandler.class);
    private Set<WebSocketSession> connectedSessionSet = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        connectedSessionSet.add(session);
    }

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        for (WebSocketSession connectedSession : connectedSessionSet) {
//            if (connectedSession.isOpen()) {
//                connectedSession.sendMessage(message);
//            } else {
//                connectedSessionSet.remove(connectedSession);
//            }
//        }
//    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String number = message.getPayload();

        TcpSender.sendToDevice(number);

        for (WebSocketSession connectedSession : connectedSessionSet) {
            if (connectedSession.isOpen()) {
                connectedSession.sendMessage(new TextMessage("已呼叫：" + number));
            } else {
                connectedSessionSet.remove(connectedSession);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        connectedSessionSet.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error(exception.getMessage(), exception);
        connectedSessionSet.remove(session);
    }
}


