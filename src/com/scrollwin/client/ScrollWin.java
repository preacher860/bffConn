package com.scrollwin.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.Cookies;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ScrolledHandler;
import com.smartgwt.client.widgets.events.ScrolledEvent;

import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;


public class ScrollWin implements EntryPoint, ioCallbackInterface {
	
	public static final int MODE_INIT_S1  = 1;
	public static final int MODE_INIT_S2  = 2;
	public static final int MODE_RUNNING  = 3;
	public static final int MODE_SHUTDOWN = 4;
	
	public static final int MSG_INITIAL_RTRV = 20;
	
	private HStack hStack = new HStack();
	private VStack messageVStack = new VStack();
	private VStack chatvStack = new VStack();
	private VLayout mainvStack = new VLayout();
	private VStack menuBox = new VStack();
	
	private IOModule ioModule = new IOModule(this);
	private Integer myCurrentMode = MODE_INIT_S1;
	private RuntimeData myRuntimeData = new RuntimeData();
	private Timer myRefreshTimer;
	private Timer myCrappyTimer;
	private UserManager myUserManager = new UserManager();
	private boolean myRuntimeDataRcvd = false;
	private boolean myUserDataRcvd = false;
	private UserTileDisplay myUserTileDisplay = new UserTileDisplay();
	private EntryBox myEntryBox = new EntryBox(this);
	private Integer myUserId = 0;
	private String mySessionId = "0";
	private WaitWindow myWaitWindow = new WaitWindow();
	private Integer myVersion = VersionInfo.CURRENT_VERSION;
	private boolean myAtBottom = true;
	
	public ScrollWin(){
		
	}

	@Override
	public void onModuleLoad() {
		String sessionIdCookie = Cookies.getCookie("bffConnexionSID");
		ioModule.GetServerSessionValid(sessionIdCookie);
	}

	public void applicationStart() {

		Canvas canvas = new Canvas();
		canvas.setWidth100();
		canvas.setHeight100();
		canvas.setAlign(Alignment.CENTER);
	
		canvas.setLayoutAlign(Alignment.CENTER);
        mainvStack.setAlign(Alignment.CENTER);
        mainvStack.setHeight100();
        mainvStack.setWidth100();
        
        Img headerImage = new Img("http://srv.lanouette.ca/images/bffConnHead2.jpg", 1716, 76);
        headerImage.setOverflow(Overflow.HIDDEN);
        mainvStack.addMember(headerImage);
        
        messageVStack.setShowEdges(true);  
        messageVStack.setMargin(5);
        messageVStack.setWidth(800);  
        messageVStack.setHeight("80%");
        messageVStack.setCanDragResize(true);
        messageVStack.setOverflow(Overflow.AUTO);
        messageVStack.setLeaveScrollbarGap(true);
        messageVStack.setMembersMargin(3);  
        messageVStack.setLayoutMargin(4);
        
        chatvStack.addMember(messageVStack);
        chatvStack.addMember(myEntryBox);
        
        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setWidth(10);
        hStack.addMember(spacer);
        hStack.addMember(myUserTileDisplay);
        hStack.addMember(chatvStack);
        hStack.setWidth100();
        
        mainvStack.addMember(hStack);
        
        canvas.addChild(mainvStack); 
        //canvas.setBackgroundColor("#A0A0A0");
        canvas.draw();  
        
        // This scroll handler sets the flag user to determine if we're at bottom or not.
        // Only if were at bottom do we kick the autoscroll on new messages
        messageVStack.addScrolledHandler(new ScrolledHandler(){
			@Override
			public void onScrolled(ScrolledEvent event) {
				if(messageVStack.getScrollTop() == messageVStack.getScrollBottom())
					myAtBottom = true;
				else
					myAtBottom = false;
			}
        } );
        
        myRefreshTimer = new Timer() {
	      @Override
	      public void run() {
	      	if (myRuntimeDataRcvd && myUserDataRcvd)
	  		{
	      		if(myCurrentMode == MODE_INIT_S1){
	      			myEntryBox.setUser(myUserManager.getUser(myUserId));
	      			myCurrentMode = MODE_RUNNING;
	      		}

		  		ioModule.GetUserMessages(myRuntimeData.getNewestSeqId()+1, myUserId, mySessionId);
		  		
		  		// Refresh users to get their online status.  This will be gathered in a better way some day
		  		ioModule.GetUserInfo(myUserId, mySessionId);
	  			
	      		// Don't reschedule if shutting down, nothing good can come out of this
	  			if(myCurrentMode != MODE_SHUTDOWN)
	  				myRefreshTimer.schedule(3000);
	  		}
	      	// Check server version to quickly detect any mismatch
	      	ioModule.GetServerVersion(myUserId, mySessionId);
	      }
	    };
	    
	    myCrappyTimer = new Timer() {
		      @Override
		      public void run() {
		    	  messageVStack.scrollToBottom();
		      }
		    };
		    
		ioModule.GetServerVersion(myUserId, mySessionId);    
		ioModule.GetUserInfo(myUserId, mySessionId);
        ioModule.GetRuntimeData(myUserId, mySessionId);
        myRefreshTimer.schedule(1000);  //  Check if our init Gets are completed
	}
	@Override
	public void messagesReceivedCallback(final ArrayList<MessageContainer> messages) {
		for(int msgIndex = 0; msgIndex < messages.size(); msgIndex++){
			//  Add only messages newer than what we already got
			if(messages.get(msgIndex).getMessageSeqId() > myRuntimeData.getNewestSeqId())
			{
				ScrollWinElement bb = new ScrollWinElement(messages.get(msgIndex), myUserManager.getUser(messages.get(msgIndex).getMessageUserId()));
				messageVStack.addMember(bb);
				myRuntimeData.setNewestSeqId(messages.get(msgIndex).getMessageSeqId());
			}
		}
		
		if(myAtBottom)
			myCrappyTimer.schedule(200);
	} 
	
	@Override
	public void runtimeDataReceivedCallback(RuntimeData data) {
		// The seqId we want to start with for initial window fill
		if(data.newestSeqId > MSG_INITIAL_RTRV)
			myRuntimeData.setNewestSeqId(data.newestSeqId - MSG_INITIAL_RTRV);
		else
			myRuntimeData.setNewestSeqId(1);

		myRuntimeDataRcvd = true;
	}

	@Override
	public void usersReceivedCallback(ArrayList<UserContainer> users) {
		myUserManager.setUserList(users);
		myUserDataRcvd = true;
		myUserTileDisplay.UpdateOnlineUsers(myUserManager);
	}

	@Override
	public void serverVersionReceivedCallback(Integer version) {
		if(version.intValue() != myVersion.intValue()) {
			myCurrentMode = MODE_SHUTDOWN;
			SC.warn("La version de l'application que vous utilisez est antérieure à celle du serveur. " +
				   "La nouvelle version sera chargée automatiquement lorsque vous fermerez cette fenêtre.", 
				   new BooleanCallback() {
				   		@Override
						public void execute(Boolean value) {Window.Location.reload();}});
		}
	}

	@Override
	public void performLoginCallback(String login, String password) {
		
		ioModule.GetNewSession(login, password);
	}

	@Override
	public void sessionReceivedCallback(String sessionId, Integer userId, String userNick) {
		
		if(sessionId.compareTo("0") == 0)  // Login failed
		{
			LoginWin loginWin = new LoginWin(this);
			loginWin.show();
		}
		else
		{
			myUserId = userId;
			mySessionId = sessionId;
			
			// Save sessionId in a cookie so we don't have to re-logon each time we load the app
			long cookieLifespan = 1000 * 60 * 60 * 24 * 7; // one week
		    Date expires = new Date(System.currentTimeMillis() + cookieLifespan);
		    Cookies.setCookie("bffConnexionSID", sessionId, expires, null, "/", false);
		    
			applicationStart();
		}
	}

	@Override
	public void accessForbiddenCallback() {
		// Just reload for now, we'll handle graceful re-logins some day.  Riiight.
		Window.Location.reload();
	}

	@Override
	public void sessionValidReceivedCallback(String sessionId, int userId,
			boolean valid) {
		if(!valid){
			// Present the login screen until valid login is performed
			LoginWin loginWin = new LoginWin(this);
			loginWin.show();
		} else {
			myUserId = userId;
			mySessionId = sessionId;
			System.out.println("Session was still active: " + mySessionId);
			applicationStart();
		}
		
	}

	@Override
	public void messageToSendCallback(String message) {
		ioModule.SendUserMessage(message, myRuntimeData.getNewestSeqId(), myUserId, mySessionId);

		// Control max number of msg displayed - Disabled for DB debugging
  	  	//if(messageVStack.getMembers().length > 100)
		//  messageVStack.removeMember(messageVStack.getMember(0));

	};
	
}
