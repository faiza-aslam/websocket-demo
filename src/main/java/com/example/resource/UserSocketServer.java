package com.example.resource;

import java.io.StringReader;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
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

import com.example.model.User;
import com.example.websocket.UserSessionHandler;

@ServerEndpoint("/actions")
public class UserSocketServer {
	
	private Logger logger = LoggerFactory.getLogger(UserSocketServer.class);
	
	@Inject
	private UserSessionHandler sessionHandler;

	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		logger.info("Opening session: {}", session.getId());
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("Closing session: {}", session.getId());
		sessionHandler.removeSession(session);
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.error("Error Occurred for session: {}, message: {}", session.getId(), throwable.getMessage());
		throw new IllegalStateException("Error ...... ", throwable);
	} 
	
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.info("Message received: {}, from session id: {}", message, session.getId());
		
		try {
			JsonReader reader = Json.createReader(new StringReader(message));
			JsonObject jsonObj = reader.readObject();
			String action = jsonObj.getString("action");
			
			if ("add".equalsIgnoreCase(action)) {
				User user = new User(Integer.parseInt(jsonObj.getString("id")), jsonObj.getString("name"), jsonObj.getString("email"));
				sessionHandler.addSession(session, user);
				
			} else if ("remove".equalsIgnoreCase(action)) {
				sessionHandler.removeSession(session);
				
			} else {
				logger.error("No such action defined");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
