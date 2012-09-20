package com.scrollwin.client;

import java.util.ArrayList;
import java.util.Date;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.DockLayoutPanel;


import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.BkgndRepeat;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;

import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;


public class BffConn implements EntryPoint, ioCallbackInterface, userCallbackInterface {
	
	static {
    //    init();
    }

    private static native void init()/*-{
        $wnd.isc.setAutoDraw(false);
    }-*/;
    
	public static final int MODE_INIT_S1  = 1;
	public static final int MODE_INIT_S2  = 2;
	public static final int MODE_RUNNING  = 3;
	public static final int MODE_SHUTDOWN = 4;
	
	private static final int MSG_INITIAL_RTRV = 200;
	private static final int MSG_OLD_FETCH_NUM = 100;
	
	private HStack hStack = new HStack();
	private VLayout chatvStack = new VLayout();
	private VLayout mainvStack = new VLayout();
	private HStack headerStack = new HStack();
	private VStack versionStack = new VStack();
	private VStack leftToolbarStack = new VStack();
	private VStack headerShadow = new VStack();
	private HTMLPane versionPane = new HTMLPane();
	private DockLayoutPanel chatDockPanel = new DockLayoutPanel(Unit.EM);
	
	private IOModule ioModule = new IOModule(this);
	private Integer myCurrentMode = MODE_INIT_S1;
	private Timer myRefreshTimer;
	private Timer myFaviconTimer;
	private boolean myRuntimeDataRcvd = false;
	private boolean myUserDataRcvd = false;
	private UserTileDisplay myUserTileDisplay = new UserTileDisplay(this);
	private WaitBox myWaitBox = new WaitBox();
	private EntryBox myEntryBox = new EntryBox(this, this);
	private HeaderButtonBar myHeaderButtonBar = new HeaderButtonBar(this);
	private MessageView myMessageManager = new MessageView(this);
	
	private boolean faviconAlert = false;
	private int newestDisplayedWhenLostVisibility = 0;
	private String mySessionLocal = "";
	
	private Timer myOctoTimer;
	private OctoObject OctoArray[] = new OctoObject[5];
	
	@Override
	public void onModuleLoad() {
		Log.debug("Logger in 'DEBUG' mode");
		String sessionIdCookie = Cookies.getCookie("bffConnexionSID");
		ioModule.GetServerSessionValid(sessionIdCookie);
	}

	public BffConn(){
		installShortcuts();
		try {
			registerVisibilityChangeCallback();
		} catch (Exception e) {
			
		}
	}
	
	public void applicationStart() {

		
		final Canvas canvas = new Canvas();
		canvas.setWidth100();
		canvas.setHeight100();
		canvas.setAlign(Alignment.CENTER);
		canvas.setLayoutAlign(Alignment.CENTER);
		canvas.setBackgroundImage("backgrounds/convore.jpg");
		canvas.setBackgroundRepeat(BkgndRepeat.REPEAT);
        mainvStack.setAlign(Alignment.CENTER);
        mainvStack.setHeight100();
        mainvStack.setWidth100();
        
        OctoArray[0] = (new OctoObject(240,152,100,100,2,-2,85));
    	OctoArray[1] = (new OctoObject(200,122,200,300,4,4,85));
    	OctoArray[2] = (new OctoObject(100,61,400,200,-3,3,75));
    	OctoArray[3] = (new OctoObject(300,170,50,400,1,-1,80));
    	OctoArray[4] = (new OctoObject(150,90,300,100,7,-2,70));
      
        Img headerImage = new Img("bffConnLogo4.png", 200, 76);
        headerShadow.setHeight(76);
        headerShadow.setWidth100();
        headerShadow.setBackgroundImage("top2.png");
        headerShadow.setBackgroundRepeat(BkgndRepeat.REPEAT_X);
        headerShadow.addMember(headerImage);
        
        LayoutSpacer chatvSpacer = new LayoutSpacer();
        chatvSpacer.setHeight(10);
        chatvStack.addMember(myMessageManager);
        chatvStack.addMember(chatvSpacer);
        chatvStack.addMember(myEntryBox);
        
        LayoutSpacer toolbarSpacer = new LayoutSpacer();
        toolbarSpacer.setHeight(15);
        leftToolbarStack.addMember(myUserTileDisplay);
        leftToolbarStack.addMember(toolbarSpacer);
        leftToolbarStack.addMember(myWaitBox);
        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setWidth(10);
        LayoutSpacer spacer2 = new LayoutSpacer();
        spacer2.setWidth(10);
        hStack.addMember(spacer);
        hStack.addMember(leftToolbarStack);
        hStack.addMember(spacer2);
        hStack.addMember(chatvStack);
        hStack.setWidth100();
        
        LayoutSpacer headerSpacer = new LayoutSpacer();
        headerSpacer.setWidth(240);
        headerStack.setWidth100();
        headerStack.setDefaultLayoutAlign(Alignment.CENTER);
        headerStack.setDefaultLayoutAlign(VerticalAlignment.BOTTOM);
        headerStack.setHeight(74);
        
        
        String versionLink ="";
        String versionString = "<font size=\"2\">Version ";
        versionString += VersionInfo.CURRENT_MAJOR + "." + VersionInfo.CURRENT_VERSION;
        versionLink += "<style type=\"text/css\">";
        versionLink += "a.one:link {color:#000000;text-decoration:none}";
        versionLink += "a.one:visited {color:#000000;text-decoration:none}";
        versionLink += "a.one:hover {color:#0000FF;text-decoration:none}";
        versionLink += "</style>";
        versionLink += "<a class=\"one\" href=\"https://github.com/preacher860/bffConn/wiki/Historique-des-changements\" target=\"_blank\"><b>";
        versionLink += versionString;
        versionLink += "</b></a></font>";
        versionPane.setContents(versionLink);
        versionPane.setHeight(15);
        versionPane.setOverflow(Overflow.HIDDEN);
	    
        LayoutSpacer versionSpacer = new LayoutSpacer();
        versionSpacer.setHeight(56);
        versionStack.setHeight(76);
        versionStack.addMember(versionSpacer);
        versionStack.addMember(versionPane);
                
        headerStack.addMember(headerSpacer);
        //headerStack.addMember(versionStack);
        headerStack.addMember(myHeaderButtonBar);
        
        LayoutSpacer vSpacer = new LayoutSpacer();
        vSpacer.setHeight(5);
        mainvStack.addMember(headerStack);
        mainvStack.addMember(vSpacer);
        mainvStack.addMember(hStack);
        mainvStack.setTop(0);
        
        myHeaderButtonBar.setLocal(mySessionLocal);
        
        canvas.addChild(OctoArray[0].getImage());
        canvas.addChild(OctoArray[3].getImage());
        canvas.addChild(headerShadow);
        canvas.addChild(mainvStack); 
        canvas.addChild(OctoArray[1].getImage());
        canvas.addChild(OctoArray[2].getImage());
        canvas.addChild(OctoArray[4].getImage());
        canvas.draw();  
        
        Window.enableScrolling(false);
        adjustComponentsSize(Window.getClientHeight());
        
        Window.addResizeHandler(new ResizeHandler() {

			 public void onResize(ResizeEvent event) {
			   int height = event.getHeight();
			   adjustComponentsSize(height);
			 }
			});
        
        myOctoTimer = new Timer() {
        	@Override
			public void run() {
        		for (int octoIndex = 0; octoIndex < OctoArray.length; octoIndex++) {
        			OctoArray[octoIndex].MoveOcto(canvas.getWidth(), canvas.getHeight());
        		}
        	}
        };
        //myOctoTimer.scheduleRepeating(50);
        
        myFaviconTimer = new Timer() {
			@Override
			public void run() {
				Element element = DOM.getElementById("favicon");
				if(faviconAlert) {
					Log.debug("Setting favicon to alert (red)");
					element.setAttribute("href", "images/favicon_red.ico");
				} else {
					Log.debug("Setting favicon to normal (blue)");
					element.setAttribute("href", "images/favicon.ico");
				}
			}
        };
        
        myRefreshTimer = new Timer() {
	      @Override
	      public void run() {
	      	if (myRuntimeDataRcvd && myUserDataRcvd)
	  		{
	      		int start_point = 1;
	      		if(myCurrentMode == MODE_INIT_S1){
	      			myEntryBox.setUser(UserManager.getInstance().getUser(RuntimeData.getInstance().getUserId()));
	      			myCurrentMode = MODE_RUNNING;
	      			
	      			// Initial retrieve
	      			if (RuntimeData.getInstance().getServerSeqId() > MSG_INITIAL_RTRV)
		      			start_point = RuntimeData.getInstance().getServerSeqId() - MSG_INITIAL_RTRV;
		      		else
		      			start_point = 1;
	      			
	      			// Consider DB versions aligned after first retrieve is performed
	      			RuntimeData.getInstance().setDbVersion(RuntimeData.getInstance().getServerDbVersion());
	      			RuntimeData.getInstance().setDbVersionUsers(RuntimeData.getInstance().getServerDbVersionUsers());
	      			
	      			String waitMsg = "Chargement des messages <b>" + start_point + " </b>à<b> ";
	      			waitMsg += (start_point + MSG_INITIAL_RTRV) + "</b>.";
	      			waitMsg += " Veuillez patienter.";
	      			myWaitBox.setMessage(waitMsg);
	      			myWaitBox.show();
	      			
	      			ioModule.GetUserMessages(start_point, MSG_INITIAL_RTRV);
	      		} 
	      		else if (myCurrentMode == MODE_RUNNING) {
	      			if(RuntimeData.getInstance().getDbVersion() < RuntimeData.getInstance().getServerDbVersion()){
	      				ioModule.GetUserMessagesByVersion(RuntimeData.getInstance().getDbVersion());
	      			}
	      			
	      			if(RuntimeData.getInstance().getDbVersionUsers() < RuntimeData.getInstance().getServerDbVersionUsers()) {
	      				RuntimeData.getInstance().setRequestedDbVersionUsers(RuntimeData.getInstance().getServerDbVersionUsers());
	      				ioModule.GetUserInfo();
	      			}
	      		}
	      		
		  		// Don't reschedule if shutting down, nothing good can come out of this
	  			if(myCurrentMode != MODE_SHUTDOWN)
	  				myRefreshTimer.schedule(3000);
	  		}
	      	// Check server version to quickly detect any mismatch
	      	ioModule.GetRuntimeData();
	      }
	    };
	    
		ioModule.GetUserInfo();
        ioModule.GetRuntimeData();
        myRefreshTimer.schedule(1000);  //  Check if our init Gets are completed
	}
	
	private void adjustComponentsSize(int height)
	{
	   int freeSpace = height - headerStack.getHeight() - myEntryBox.getHeight() - 40;
	   chatvStack.setHeight(height - headerStack.getHeight());
	   myMessageManager.setHeight(Integer.toString(freeSpace) + "px");
	   myEntryBox.setTop(height - myEntryBox.getHeight() - chatvStack.getAbsoluteTop() - 10);
	   
	   myMessageManager.toBottom(false);
	}
	
	@Override
	public void messagesReceivedCallback(final ArrayList<MessageContainer> messages) {
		myMessageManager.newMessages(messages);
	} 
	
	@Override
	public void runtimeDataReceivedCallback() {
		myRuntimeDataRcvd = true;
		checkServerVersion();
	}

	@Override
	public void usersReceivedCallback(ArrayList<UserContainer> users) {
		UserManager.getInstance().setUserList(users);
		myUserDataRcvd = true;
		myUserTileDisplay.UpdateOnlineUsers(UserManager.getInstance());
		RuntimeData.getInstance().setDbVersionUsers(RuntimeData.getInstance().getRequestedDbVersionUsers());
		System.out.println("Users updated up to db version " + RuntimeData.getInstance().getDbVersionUsers());
	}

	public void checkServerVersion() {
		if(RuntimeData.getInstance().getServerVersion() != VersionInfo.CURRENT_VERSION) {
			myCurrentMode = MODE_SHUTDOWN;
			SC.warn("La version de l'application que vous utilisez est antérieure à celle du serveur. " +
				   "La nouvelle version sera chargée automatiquement lorsque vous fermerez cette fenêtre.", 
				   new BooleanCallback() {
				   		@Override
						public void execute(Boolean value) {Window.Location.reload();}});
		}
	}

	@Override
	public void performLoginCallback(String login, String password, String local) {
			    
		ioModule.GetNewSession(login, password, local);
	}

	@Override
	public void sessionReceivedCallback(String sessionId, Integer userId, String userNick, String userLocal) {
		
		if(sessionId.compareTo("0") == 0)  // Login failed
		{
			LoginWin loginWin = new LoginWin(this);
			loginWin.show();
		}
		else
		{
			RuntimeData.getInstance().setUserId(userId);
			RuntimeData.getInstance().setSessionId(sessionId);
			mySessionLocal = userLocal;
			
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
	public void sessionValidReceivedCallback(String sessionId, int userId, String local, boolean valid) {
		if(!valid){
			// Present the login screen until valid login is performed
			LoginWin loginWin = new LoginWin(this);
			loginWin.show();
		} else {
			RuntimeData.getInstance().setUserId(userId);
			RuntimeData.getInstance().setSessionId(sessionId);
			mySessionLocal = local;
			System.out.println("Session was still active: " + RuntimeData.getInstance().getSessionId());
			applicationStart();
		}
	}

	@Override
	public void messageToSendCallback(String message, boolean edit, int seqId) {
		if(!edit)
			ioModule.SendUserMessage(message, RuntimeData.getInstance().getNewestSeqId());
		else
			ioModule.SendMessageEdit(message, seqId);

		// Control max number of msg displayed - Disabled for DB debugging
  	  	//if(messageVStack.getMembers().length > 100)
		//  messageVStack.removeMember(messageVStack.getMember(0));

	}

	private void installShortcuts()
	{
		Event.addNativePreviewHandler(new Event.NativePreviewHandler() { 
			@Override 
			public void onPreviewNativeEvent(NativePreviewEvent event) { 
				NativeEvent ne = event.getNativeEvent();
				switch (event.getTypeInt()) { 
				case Event.ONKEYDOWN:
					if(ne.getCtrlKey() && !ne.getShiftKey()){
						if(ne.getKeyCode()=='l' || ne.getKeyCode()=='L')
						{
							myHeaderButtonBar.showLocationEntry();
							event.consume();
							ne.preventDefault();
							ne.stopPropagation();
						} 
						else if(ne.getKeyCode()=='d' || ne.getKeyCode()=='D')
						{
							performLogout();
							event.consume();
							ne.preventDefault();
							ne.stopPropagation();
						}
						else if(ne.getKeyCode()=='s' || ne.getKeyCode()=='S')
						{
							statsClicked();
							event.consume();
							ne.preventDefault();
							ne.stopPropagation();
						}
						else if(ne.getKeyCode()=='o' || ne.getKeyCode()=='O')
						{
							octopusClicked();
							event.consume();
							ne.preventDefault();
							ne.stopPropagation();
						}
						else if(ne.getKeyCode()=='i' || ne.getKeyCode()=='I')
						{
							infoClicked();
							event.consume();
							ne.preventDefault();
							ne.stopPropagation();
						}
					}
				} 
			} 
		}); 
	}

	public native void registerVisibilityChangeCallback() /*-{ 
		var that = this;
		document.addEventListener("webkitvisibilitychange", 
								  function() {that.@com.scrollwin.client.BffConn::visibilityChanged()();},
		 						  false); 
	}-*/; 
	
	public final native boolean isTabHidden() /*-{
		return document.webkitHidden;
	}-*/;

	private void visibilityChanged() { 
		// Reset new message indicator octo when switching to visible
		Log.debug("Visibility changed to " + !isTabHidden());
		try {
			if(!isTabHidden()){
				Log.debug("Tab is VISIBLE");
				if(faviconAlert){
					Log.debug("Starting favicon timer");
					faviconAlert = false;
					myFaviconTimer.schedule(300);
				}
				Log.debug("Setting message manager mode to visible");
				myMessageManager.setInvisibleMode(false);
			} else {
				Log.debug("Tab is HIDDEN");
				newestDisplayedWhenLostVisibility = myMessageManager.getNewestDisplayedSeq();
				Log.debug("Setting message manager mode to invisible");
				myMessageManager.setInvisibleMode(true);
			}
		} catch (Exception e) {
			Log.debug("Exception in visibilityChanged");
		}
	}
	
	@Override
	public void avatarClicked(String userNick) {
		myEntryBox.addAddressee(userNick);
	};
	
	public void performLogout() {
		Cookies.removeCookie("bffConnexionSID", "/");
		ioModule.Logout();
	}

	@Override
	public void logoutComplete() {
		Window.Location.reload();
	}

	@Override
	public void logoutClicked() {
		performLogout();
	}
	
	public void statsClicked() {
		new StatsWin(UserManager.getInstance().getUserList());
	}

	@Override
	public void localEntered(String local) {
		if (local != null)
			ioModule.SendLocal(local);
		
		myEntryBox.setFocus();
	}

	@Override
	public void octopusClicked() {
		new OctopusWin();
	}

	@Override
	public void octopusOnTyped() {
		myHeaderButtonBar.showOctopus();
	}

	@Override
	public void octopusOffTyped() {
		myHeaderButtonBar.hideOctopus();
	}

	@Override
	public void scrollTop(int oldest) {
		int firstMsgToFetch;
		if (oldest > MSG_OLD_FETCH_NUM)
			firstMsgToFetch = oldest - MSG_OLD_FETCH_NUM;
		else
			firstMsgToFetch = 1;
		
		String waitMsg = "Chargement des messages <b>" + firstMsgToFetch + " </b>à<b> ";
		waitMsg += (firstMsgToFetch + MSG_OLD_FETCH_NUM) + "</b>";
		waitMsg += " Veuillez patienter.";
		myWaitBox.setMessage(waitMsg);
		myWaitBox.show();
		
		ioModule.GetUserMessages(firstMsgToFetch, MSG_OLD_FETCH_NUM);
	}

	@Override
	public void messageDisplayComplete() {
		myWaitBox.hide();
	}

	@Override
	public void starClicked(int seqId) {
		ioModule.SendStarMessage(seqId);
		//Window.open("http://www.youtube.com/watch?v=dQw4w9WgXcQ&t=0m43s", "Rick Rolled!", "");
	}

	@Override
	public void deleteClicked(int seqId) {
		ioModule.SendDeleteMessage(seqId);
	}

	@Override
	public void newestUpdated() {
		try {
			Log.debug("newestUpdated called");
			if(myCurrentMode == MODE_RUNNING && !faviconAlert && isTabHidden()) {
				Log.debug("Starting favicon timer");
				faviconAlert = true;
				myFaviconTimer.schedule(300);
			}
		} catch (Exception e) {
			Log.debug("Exception in newestUpdated");
		}
	}

	@Override
	public void userEntry() {
		myMessageManager.ClearUnreadAll();
	}

	@Override
	public void editMessageClicked(MessageContainer message) {
		myEntryBox.editMessage(message);
	}

	@Override
	public void infoClicked() {
		//SC.showConsole();
		Window.open("https://github.com/preacher860/bffConn/wiki/Historique-des-changements", "Historique", "");
	}

	@Override
	public void superOctopusOnTyped() {
		for(OctoObject octo:OctoArray){
			octo.showOcto();
		}
		myOctoTimer.scheduleRepeating(50);
	}

	@Override
	public void superOctopusOffTyped() {
		for(OctoObject octo:OctoArray){
			octo.hideOcto();
		}
		myOctoTimer.cancel();

		
	}
}
