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
	
	public IOModule(ioCallbackInterface theCallbackInterface) {
		myCallbackInterface = theCallbackInterface;
	}
	
	public void SendUserMessage(String MessageText, Integer seqId)
	{
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=get_messages";
		url += "&message_text=" + URL.encodePathSegment(MessageText);
		url += "&start_point=" + (seqId + 1);
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=delete_message";
		url += "&message_id=" + seqId;
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=star_message";
		url += "&message_id=" + seqId;
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=get_messages";
		url += "&start_point=" + seqId;
		if(num.intValue() > 0)
			url += "&end_point=" + seqId + num;
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=get_messages_by_db";
		url += "&db_version=" + dbVersion;
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=get_user_info";
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=get_runtime_data";
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
	
	public void GetServerVersion()
	{
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=get_server_version";
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) 
						handleServerVersion(response.getText());
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
		
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=get_server_session_valid";
		url += "&session_id=" + sessionId;
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=perform_login";
		url += "&login=" + URL.encodePathSegment(login);
		url += "&password=" + URL.encodePathSegment(password);
		url += "&local=" + URL.encodePathSegment(local);
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)     
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode())
						handleSessionReceived(response.getText());
					else if (302 == response.getStatusCode())
						System.out.println("Request response 302. Text: " + response.getText());
					else
						System.out.println("Request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
	
	public void SendLocal(String local)
	{
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=set_local";
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&local=" + URL.encodePathSegment(local);
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
		try
		{
			JSONObject obj;
		    JSONValue jsonValue = JSONParser.parseStrict(serverResponse);
		    JSONArray jsonArray = jsonValue.isArray();
		    ArrayList<MessageContainer> messageList = new ArrayList<MessageContainer>();
		    
		    for (int Index = 0; Index < jsonArray.size(); Index++)
		    {
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
		    }
		    if(messageList.size() > 0)
		    	myCallbackInterface.messagesReceivedCallback(messageList);
		} catch (Exception e) {
			System.out.println("JSON exception: " + e.toString());
		}
	}
	
	public void Logout()
	{
		String postData = "dummy=0";
		
		String url = urlPrefix + "jsontest?" + "request_mode=perform_logout";
		url += "&user_id=" + RuntimeData.getInstance().getUserId();
		url += "&session_id=" + RuntimeData.getInstance().getSessionId();
		url += "&rnd_value=" + Random.nextInt(400000000);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		try {
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			Request request = builder.sendRequest(postData, new RequestCallback() {
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
			JSONObject obj;
		    JSONValue jsonValue = JSONParser.parseStrict(serverResponse);
		    JSONArray jsonArray = jsonValue.isArray();
		    
		    obj = jsonArray.get(0).isObject();
		    Integer seqId = Integer.valueOf(obj.get("newestSeq").isString().stringValue());
		    Integer serverDbVersion = Integer.valueOf(obj.get("dbVersion").isString().stringValue());
		    Integer serverVersion = Integer.valueOf(obj.get("serverVersion").isString().stringValue());

		    RuntimeData.getInstance().setServerVersion(serverVersion);
		    RuntimeData.getInstance().setServerDbVersion(serverDbVersion);
		    RuntimeData.getInstance().setServerSeqId(seqId);
		    myCallbackInterface.runtimeDataReceivedCallback();
		} catch (Exception e) {
			System.out.println("JSON exception: " + e.toString());
		}
	}
	
	private void handleServerVersion(String serverResponse)
	{
		try
		{
			JSONObject obj;
		    JSONValue jsonValue = JSONParser.parseStrict(serverResponse);
		    JSONArray jsonArray = jsonValue.isArray();
		    
		    obj = jsonArray.get(0).isObject();
		    Integer version = Integer.valueOf(obj.get("serverVersion").isString().stringValue());
		    
		    //myCallbackInterface.serverVersionReceivedCallback(version);
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
		    	Integer starsSent = Integer.valueOf(obj.get("starssent").isString().stringValue());
		    	Integer starsRcvd = Integer.valueOf(obj.get("starsreceived").isString().stringValue());
		    	
		    	UserContainer user = new UserContainer(userId, userNick, "", avURL, online, messages, deleted, starsSent, starsRcvd);
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
		String userNick = "";
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
