package com.scrollwin.client;

import java.util.ArrayList;

public interface ioCallbackInterface {
		public void messagesReceivedCallback(ArrayList<MessageContainer> messages);
		public void runtimeDataReceivedCallback(RuntimeData data);
		public void usersReceivedCallback(ArrayList<UserContainer> messages);
		public void serverVersionReceivedCallback(Integer version);
		public void performLoginCallback(String login, String password, String local);
		public void sessionReceivedCallback(String sessionId, Integer userId, String userNick);
		public void sessionValidReceivedCallback(String sessionId, int userId, boolean valid);
		public void accessForbiddenCallback();
		public void messageToSendCallback(String Message);
		public void logoutComplete();
}
