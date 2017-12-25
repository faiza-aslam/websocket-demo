package com.example.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.User;

@ApplicationScoped
@Startup
@Singleton
public class UserSessionHandler {

	private Logger logger = LoggerFactory.getLogger(UserSessionHandler.class); 	
	private Map<Session, User> sessions;
	
	public UserSessionHandler() {
		logger.info("### Initializing UserSessionHandler ###");
	}
	
	@PostConstruct
	public void init() {
		logger.info("### PostConstruct UserSessionHandler ###");
		sessions = new HashMap<>();
	}
	
	public void addSession(Session session, User user) {
		sessions.put(session, user);
	}
	
	public void removeSession(Session session) {
		sessions.remove(session);
	}
	
	public void sendToAllConnectedSessions(String message, String status) {
    	
		JsonObject obj = JsonProvider
    						.provider()
    						.createObjectBuilder()
    						.add("title", "Cnic Verification Request")
    						.add("message", message)
    						.add("icon", "/images/"+status+".jpg")
    						.build();
    	
    	sendToAllConnectedSessions(obj);
    }
	
    private void sendToAllConnectedSessions(JsonObject message) {
    	sessions.keySet().forEach(session -> { 
    		sendToSession(session, message);
    	});
    }

    private void sendToSession(Session session, JsonObject message) {
    	
    	try {
			session.getBasicRemote().sendText(message.toString());
			
		} catch (IOException e) {
			sessions.remove(session);
			e.printStackTrace();
		}
    }
}
