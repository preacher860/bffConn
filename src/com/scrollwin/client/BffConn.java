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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

import com.smartgwt.client.widgets.layout.LayoutSpacer;
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
	
	private static final int MSG_INITIAL_RTRV = 40;
	private static final int MSG_OLD_FETCH_NUM = 200;
	
	DockLayoutPanel mainDockPanel = new DockLayoutPanel(Unit.PX);
	private HorizontalPanel headerStack = new HorizontalPanel();
	private VStack leftToolbarStack = new VStack();
	private VerticalPanel topToolbarStack = new VerticalPanel();
	private DockLayoutPanel chatDockPanel = new DockLayoutPanel(Unit.EM);
	
	private IOModule ioModule = new IOModule(this);
	private Integer myCurrentMode = MODE_INIT_S1;
	private Timer myRefreshTimer;
	private Timer myFaviconTimer;
	private Timer myResizeTimer;
	private boolean myRuntimeDataRcvd = false;
	private boolean myUserDataRcvd = false;
	private UserTileDisplay myUserTileDisplay = new UserTileDisplay(this);
	private WaitBox myWaitBox = new WaitBox();
	private octoBox myOctoBox = new octoBox();
	private EntryBox myEntryBox = new EntryBox(this, this);
	private HeaderButtonBar myHeaderButtonBar = new HeaderButtonBar(this);
	private MessageView myMessageManager = new MessageView(this);
	private Image headerImage = new Image();
	private motd myMotd = new motd();
	private boolean faviconAlert = false;
	private int newestDisplayedWhenLostVisibility = 0;
	private String mySessionLocal = "";
	private boolean wideView = true;
	private Label motdLabel= new Label("Message of the day");
	private Timer myOctoTimer;
	private OctoObject OctoArray[] = new OctoObject[5];
	private boolean toolbarHideable = true;
	
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

		mainDockPanel.setStyleName("mainPanel");
		
		headerImage.setUrl("images/bffConnLogo4.png");
        
        LayoutSpacer toolbarSpacer = new LayoutSpacer();
        LayoutSpacer toolbarSpacer2 = new LayoutSpacer();
        toolbarSpacer.setHeight(15);
        toolbarSpacer2.setHeight(15);
        leftToolbarStack.setStyleName("leftToolbar");
        leftToolbarStack.addMember(myUserTileDisplay);
        leftToolbarStack.addMember(toolbarSpacer);
        leftToolbarStack.addMember(myWaitBox);
        leftToolbarStack.addMember(toolbarSpacer2);
        leftToolbarStack.addMember(myOctoBox);

        motdLabel.setHeight(50);
        motdLabel.setWidth(150);
        topToolbarStack.add(myMotd);
        topToolbarStack.add(myHeaderButtonBar);
        topToolbarStack.setCellHeight(myMotd, "40px");
        topToolbarStack.setCellHeight(myHeaderButtonBar, "32px");
        topToolbarStack.setCellVerticalAlignment(myMotd, HasVerticalAlignment.ALIGN_BOTTOM);
        
        headerStack.setStyleName("headerStack");
        headerStack.add(headerImage);
        headerStack.add(topToolbarStack);
        headerStack.setCellWidth(headerImage, "240px");
        myHeaderButtonBar.setLocal(mySessionLocal);
        
        mainDockPanel.addNorth(headerStack, 76);
        mainDockPanel.addWest(leftToolbarStack, 240);
        mainDockPanel.addSouth(myEntryBox,110);
        mainDockPanel.add(myMessageManager);
        
        RootLayoutPanel.get().add(mainDockPanel);
        RootLayoutPanel.get().setStyleName("mainPanel");
        
        //Window.enableScrolling(false);
        
        Window.addResizeHandler(new ResizeHandler() {
			 public void onResize(ResizeEvent event) {
				 myResizeTimer.schedule(500);
			 }
		});
        
        myResizeTimer = new Timer() {
        	@Override
			public void run() {
        		myMessageManager.toBottom(false);
        	}
        };
        	
        myOctoTimer = new Timer() {
        	@Override
			public void run() {
        		//for (int octoIndex = 0; octoIndex < OctoArray.length; octoIndex++) {
        		//	OctoArray[octoIndex].MoveOcto(canvas.getWidth(), canvas.getHeight());
        		//}
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
	      			//System.out.println("Db version: " + RuntimeData.getInstance().getDbVersion() + "  Srv Db version: " + RuntimeData.getInstance().getServerDbVersion());
	      			if(RuntimeData.getInstance().getDbVersion() < RuntimeData.getInstance().getServerDbVersion()){
	      				System.out.println("DB version behind, updating to " + RuntimeData.getInstance().getServerDbVersion());
	      				ioModule.GetUserMessagesByVersion(RuntimeData.getInstance().getServerDbVersion());
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
	
	@Override
	public void messagesReceivedCallback(final ArrayList<MessageContainer> messages) {
		myMessageManager.newMessages(messages);
	} 
	
	@Override
	public void runtimeDataReceivedCallback() {
		myRuntimeDataRcvd = true;
		checkServerVersion();
		myMotd.updateMotd(RuntimeData.getInstance().getMotd());
	}

	@Override
	public void usersReceivedCallback(ArrayList<UserContainer> users) {
		UserManager.getInstance().setUserList(users);
		myUserDataRcvd = true;
		myUserTileDisplay.UpdateOnlineUsers(UserManager.getInstance());
		RuntimeData.getInstance().setDbVersionUsers(RuntimeData.getInstance().getRequestedDbVersionUsers());
		//System.out.println("Users updated up to db version " + RuntimeData.getInstance().getDbVersionUsers());
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
						else if(ne.getKeyCode()=='1')
						{
							hideBarClicked();
							event.consume();
							ne.preventDefault();
							ne.stopPropagation();
						}
						else if(ne.getKeyCode()=='2')
						{
							showBarClicked();
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
		myOctoBox.show();
		toolbarHideable = false;
		myHeaderButtonBar.setAllowCompactMode(false);
		showBarClicked();
	}

	@Override
	public void octopusOffTyped() {
		myOctoBox.hide();
		myHeaderButtonBar.setAllowCompactMode(true);
		toolbarHideable = true;
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
		//for(OctoObject octo:OctoArray){
		//	octo.showOcto();
		//}
		//myOctoTimer.scheduleRepeating(50);
	}

	@Override
	public void superOctopusOffTyped() {
		//for(OctoObject octo:OctoArray){
		//	octo.hideOcto();
		//}
		//myOctoTimer.cancel();

		
	}

	@Override
	public void hideBarClicked() {
		if(toolbarHideable) {
			mainDockPanel.setWidgetSize(leftToolbarStack, 35);
			leftToolbarStack.setVisible(false);
			headerImage.setVisible(false);
			headerStack.setCellWidth(headerImage, "40px");
			myHeaderButtonBar.setCompactView();
		}
	}

	@Override
	public void showBarClicked() {
		mainDockPanel.setWidgetSize(leftToolbarStack, 240);
		leftToolbarStack.setVisible(true);
		headerImage.setVisible(true);
		headerStack.setCellWidth(headerImage, "240px");
		myHeaderButtonBar.setNormalView();
	}
}
