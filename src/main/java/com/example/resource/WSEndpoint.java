package com.example.resource;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@ServerEndpoint("/example")
public class WSEndpoint {

	private Logger logger = LoggerFactory.getLogger(WSEndpoint.class);
	
	@Resource
	ManagedExecutorService mes;
	
	@OnMessage
	public String receiveMessage(String message, Session session) {
		logger.info("Message received: {}, for session id: {}", message, session.getId());
		return "Message received by Server";
	}
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		logger.info("Open session: {}", session.getId());
		final Session s = session;
		logger.info("mes: {}", mes);
		mes.execute(new Runnable() {
			public void run() {
				try {
					Thread.sleep(5000);
					s.getBasicRemote().sendText("Hello from Server");
					Thread.sleep(10000);
					s.getBasicRemote().sendText("Hello again from Server");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("Closing session: {}", session.getId());
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.error("Error Occurred for session: {}", session.getId());
		throw new IllegalStateException("Error ...... ", throwable);
	}
	
}