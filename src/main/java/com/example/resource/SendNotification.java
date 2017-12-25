package com.example.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.example.websocket.UserSessionHandler;

@Path("send")
public class SendNotification {

	@Inject
	private UserSessionHandler sessionHandler;
	
	@GET
	@Path("notification/{status}")
	public String sendNotification(@PathParam("status") String status) {
		String message = "A new CNIC verification request of user: {0} has been created"+status;
		sessionHandler.sendToAllConnectedSessions(message, status);
		return "Notification sent";
	}
	
	@GET
	@Path("notification")
	public String sendNotificationToManager() {
		String message = "A new CNIC verification request of user: {0} has been created";
		sessionHandler.sendToAllConnectedSessions(message, null);
		return "Notification sent To manager";
	}
	
}
