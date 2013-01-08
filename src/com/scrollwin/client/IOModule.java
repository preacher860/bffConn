package com.scrollwin.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;


public class IOModule {

	private ioCallbackInterface myCallbackInterface;
	private static final String urlPrefix = GWT.getModuleBaseURL();
	private static final String servletName = "bffconnserver";
	
	public IOModule(ioCallbackInterface theCallbackInterface) {
		myCallbackInterface = theCallbackInterface;
	}
	
	//private void postToServer()
	public void SendUserMessage(String MessageText, Integer seqId)
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=get_messages";
		postData += "&message_text=" + URL.encodePathSegment(MessageText);
		postData += "&start_point=" + (seqId + 1);
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode())
						handleNewMessages(response.getText());
					else if (403 == response.getStatusCode())
						handleAccessForbidden();
					else 
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void SendMessageEdit(String MessageText, Integer seqId)
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=edit_message";
		postData += "&message_text=" + URL.encodePathSegment(MessageText);
		postData += "&message_id=" + seqId;
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode())
						handleNewMessages(response.getText());
					else if (403 == response.getStatusCode())
						handleAccessForbidden();
					else 
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void SendDeleteMessage(Integer seqId)
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=delete_message";
		postData += "&message_id=" + seqId;
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode())
						handleNewMessages(response.getText());
					else if (403 == response.getStatusCode())
						handleAccessForbidden();
					else 
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void SendStarMessage(Integer seqId)
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=star_message";
		postData += "&message_id=" + seqId;
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode())
						handleNewMessages(response.getText());
					else if (403 == response.getStatusCode())
						handleAccessForbidden();
					else 
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void GetUserMessages(Integer seqId, Integer num)
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=get_messages";
		postData += "&start_point=" + seqId;
		if(num.intValue() > 0)
			postData += "&end_point=" + (seqId + num);
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) 
						handleNewMessages(response.getText());
					else if (403 == response.getStatusCode())
						handleAccessForbidden();
					else 
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}

	public void GetUserMessagesByVersion(int dbVersion)
	{
		String url = urlPrefix + servletName; 
		url += "?rnd_value=" + Random.nextInt(400000000);
	
		System.out.println("Requesting messages by DB version " + dbVersion);
		String postData = "request_mode=get_messages_by_db";
		postData += "&db_version=" + dbVersion;
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) 
						handleNewMessages(response.getText());
					else if (403 == response.getStatusCode())
						handleAccessForbidden();
					else 
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void GetUserInfo()
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);

		String postData = "request_mode=get_user_info";
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) 
						handleUserInfo(response.getText());
					else if (403 == response.getStatusCode()) 
						handleAccessForbidden();
					else {
						System.out.println("Request response error: " + response.getStatusCode());
					}
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}

	public void GetRuntimeData()
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=get_runtime_data";
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					System.out.println("Request error: inventoryconfig_send");
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode())
						handleRuntimeData(response.getText());
					else if (403 == response.getStatusCode())
						handleAccessForbidden();
					else
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void GetServerSessionValid(String sessionId)
	{
		
		// If the sessionId is null, the cookie probably didn't exist.  Handle this as a negative
		// response from the server as it ends up being the same to the application, that is
		// prompting the user for a l/p 
		if(sessionId == null){
			handleSessionValid(null);
			return;
		}
		
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=get_server_session_valid";
		postData += "&session_id=" + sessionId;
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) 
						handleSessionValid(response.getText());
					else if (403 == response.getStatusCode())
						handleSessionValid(null);
					else
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void GetNewSession(String login, String password, String local)
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=perform_login";
		postData += "&login=" + URL.encodePathSegment(login);
		postData += "&password=" + URL.encodePathSegment(password);
		postData += "&local=" + URL.encodePathSegment(local);
				
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode())
						handleSessionReceived(response.getText());
					else if (302 == response.getStatusCode())
						System.out.println("Request response 302. Text: " + response.getText());
					else {
						System.out.println("Request response error: " + response.getStatusCode());
						handleAccessForbidden();
					}
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void SendLocal(String local)
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=set_local";
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
		postData += "&local=" + URL.encodePathSegment(local);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) 
						{} 
					else if (403 == response.getStatusCode())
						handleAccessForbidden();
					else
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	private void handleNewMessages(String serverResponse)
	{
		int Index = 0;
		try
		{
			JSONObject obj;
		    JSONValue jsonValue = JSONParser.parseStrict(serverResponse);
		    JSONArray jsonArray = jsonValue.isArray();
		    ArrayList<MessageContainer> messageList = new ArrayList<MessageContainer>();
		    
		    for (Index = 0; Index < jsonArray.size(); Index++)
		    {
		    	try {
			    	obj = jsonArray.get(Index).isObject();
			    	Integer messageId = Integer.valueOf(obj.get("id").isString().stringValue());
			    	Integer userId = Integer.valueOf(obj.get("user").isString().stringValue());
			    	String messageText = obj.get("value").isString().stringValue();
			    	String dateStamp = obj.get("date").isString().stringValue();
			    	String timeStamp = obj.get("time").isString().stringValue();
			    	String local = obj.get("local").isString().stringValue();
			    	boolean deleted = Boolean.valueOf(obj.get("deleted").isString().stringValue());
			    	int dbVersion = Integer.valueOf(obj.get("dbversion").isString().stringValue());
			    	String stars = obj.get("stars").isString().stringValue();
			    	
			    	MessageContainer message = new MessageContainer(messageId, userId, messageText, 
			    													dateStamp, timeStamp, local,
			    													deleted, dbVersion, stars);
			    	messageList.add(message);
		    	} catch (Exception e) {
					System.out.println("JSON exception for message at index " + Index + ": "+ e.toString());
				}
		    }
		    if(messageList.size() > 0)
		    	myCallbackInterface.messagesReceivedCallback(messageList);
		} catch (Exception e) {
			System.out.println("JSON exception at index " + Index + ": " + e.toString());
		}
	}
	
	public void Logout()
	{
		String url = urlPrefix + servletName;
		url += "?rnd_value=" + Random.nextInt(400000000);
		
		String postData = "request_mode=perform_logout";
		postData += "&user_id=" + RuntimeData.getInstance().getUserId();
		postData += "&session_id=" + RuntimeData.getInstance().getSessionId();
			
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					myCallbackInterface.logoutComplete();
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	private void handleRuntimeData(String serverResponse)
	{
		try
		{
			//jsonRuntimeDataReader rd = jsonRuntimeDataReader.JSON.read(serverResponse);
			
			JSONObject obj;
		    JSONValue jsonValue = JSONParser.parseStrict(serverResponse);
		    JSONArray jsonArray = jsonValue.isArray();
		    
		    obj = jsonArray.get(0).isObject();
		    Integer seqId = Integer.valueOf(obj.get("newestSeq").isString().stringValue());
		    Integer serverDbVersion = Integer.valueOf(obj.get("dbVersion").isString().stringValue());
		    Integer serverDbVersionUsers = Integer.valueOf(obj.get("dbVersionUsers").isString().stringValue());
		    Integer serverVersion = Integer.valueOf(obj.get("serverVersion").isString().stringValue());
		    String  serverMotd = obj.get("motd").isString().stringValue();
		    String  serverMotd_date = obj.get("motd_date").isString().stringValue();
		    String  serverMotd_time = obj.get("motd_time").isString().stringValue();
		    int     serverMotd_usedid = Integer.valueOf(obj.get("motd_userid").isString().stringValue());
		    

		    RuntimeData.getInstance().setServerVersion(serverVersion);
		    RuntimeData.getInstance().setServerDbVersion(serverDbVersion);
		    RuntimeData.getInstance().setServerDbVersionUsers(serverDbVersionUsers);
		    RuntimeData.getInstance().setServerSeqId(seqId);
		    RuntimeData.getInstance().setMotd(serverMotd);
		    RuntimeData.getInstance().setMotdDate(serverMotd_date);
		    RuntimeData.getInstance().setMotdTime(serverMotd_time);
		    RuntimeData.getInstance().setMotdUserId(serverMotd_usedid);
		    myCallbackInterface.runtimeDataReceivedCallback();
		} catch (Exception e) {
			System.out.println("JSON exception: " + e.toString());
		}
	}
	
	private void handleUserInfo(String serverResponse)
	{
		try
		{
			JSONObject obj;
			
		    JSONValue jsonValue = JSONParser.parseStrict(serverResponse);
		    JSONArray jsonArray = jsonValue.isArray();
		    ArrayList<UserContainer> userList = new ArrayList<UserContainer>();
		    
		    for (int Index = 0; Index < jsonArray.size(); Index++)
		    {
		    	obj = jsonArray.get(Index).isObject();
		    	Integer userId = Integer.valueOf(obj.get("id").isString().stringValue());
		    	String userNick = obj.get("nick").isString().stringValue();
		    	String avURL = obj.get("url").isString().stringValue();
		    	boolean online = Boolean.valueOf(obj.get("online").isString().stringValue());
		    	Integer messages = Integer.valueOf(obj.get("messages").isString().stringValue());
		    	Integer deleted = Integer.valueOf(obj.get("deleted").isString().stringValue());
		    	Integer edited = Integer.valueOf(obj.get("edited").isString().stringValue());
		    	Integer starsSent = Integer.valueOf(obj.get("starssent").isString().stringValue());
		    	Integer starsRcvd = Integer.valueOf(obj.get("starsreceived").isString().stringValue());
		    	
		    	UserContainer user = new UserContainer(userId, userNick, "", avURL, online, messages, deleted, edited, starsSent, starsRcvd);
		    	userList.add(user);
		    }
		    if(userList.size() > 0)
		    	myCallbackInterface.usersReceivedCallback(userList);
		} catch (Exception e) {
			System.out.println("JSON exception: " + e.toString());
		}
	}
	
	private void handleSessionReceived(String serverResponse)
	{
		String sessionId = "";
		String userNick = "";
		String userLocal = "";
		Integer userId = 0;
		System.out.println("handleSessionReceived()");
		try
		{
			JSONObject obj;
		    JSONValue jsonValue = JSONParser.parseStrict(serverResponse);
		    JSONArray jsonArray = jsonValue.isArray();
		    
		    obj = jsonArray.get(0).isObject();
		    sessionId = obj.get("sessionId").isString().stringValue();
		    if(sessionId.compareTo("0") != 0) {
		    	userNick = obj.get("nick").isString().stringValue();
		    	userId = Integer.valueOf(obj.get("id").isString().stringValue());
		    	userLocal = obj.get("local").isString().stringValue();
		    }
		    
		    myCallbackInterface.sessionReceivedCallback(sessionId, userId, userNick, userLocal);
		} catch (Exception e) {
			System.out.println("JSON exception: " + e.toString());
		}
	}
	
	private void handleSessionValid(String serverResponse)
	{
		String sessionId = "";
		String local = "";
		
		Integer userId = 0;
		
		if (serverResponse == null)
			myCallbackInterface.sessionValidReceivedCallback(null, 0, null, false);
		
		try
		{
			JSONObject obj;
		    JSONValue jsonValue = JSONParser.parseStrict(serverResponse);
		    JSONArray jsonArray = jsonValue.isArray();
		    
		    obj = jsonArray.get(0).isObject();
	    	sessionId = obj.get("sessionId").isString().stringValue();
	    	if(sessionId.compareTo("0") == 0)
	    			myCallbackInterface.sessionValidReceivedCallback(null, 0, null, false);
	    	else {
	    		userId = Integer.valueOf(obj.get("userId").isString().stringValue());
	    		local  =  obj.get("local").isString().stringValue();
		        myCallbackInterface.sessionValidReceivedCallback(sessionId, userId, local, true);
	    	}
		} catch (Exception e) {
			System.out.println("JSON exception: " + e.toString());
		}
	}

	private void handleAccessForbidden()
	{
		myCallbackInterface.accessForbiddenCallback();	
	}
}

