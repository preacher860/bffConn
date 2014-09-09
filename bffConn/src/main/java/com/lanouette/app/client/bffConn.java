package com.lanouette.app.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lanouette.app.client.proxies.BffProxy;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VStack;
import com.googlecode.mgwt.ui.client.MGWT;

public class bffConn implements EntryPoint, ioCallbackInterface, userCallbackInterface {
    public static final int MODE_INIT_S1 = 1;
    public static final int MODE_INIT_S2 = 2;
    public static final int MODE_RUNNING = 3;
    public static final int MODE_SHUTDOWN = 4;

    private static final int MSG_INITIAL_RTRV = 400;
    private static final int MSG_OLD_FETCH_NUM = 200;
    private static final int MSG_INITIAL_RTRV_MOBILE = 80;
    private static final int MSG_OLD_FETCH_NUM_MOBILE = 100;

    private final BffProxy proxy;
    private final boolean isMobile = checkMobile();
    private final boolean isIphone = checkIphone();

    private boolean compactModeEnabled = false;

    DockLayoutPanel mainDockPanel = new DockLayoutPanel(Unit.PX);
    private HorizontalPanel headerStack = new HorizontalPanel();
    private HorizontalPanel topToolbarLowerStack = new HorizontalPanel();
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
    private boolean myMotdRcvd = false;
    private UserTileDisplay myUserTileDisplay = new UserTileDisplay(this);
    private WaitBox myWaitBox = new WaitBox();
    private octoBox myOctoBox = new octoBox();
    private EntryBox myEntryBox = new EntryBox(this, this);
    private HeaderButtonBar myHeaderButtonBar = new HeaderButtonBar(this);
    private MessageView myMessageManager = new MessageView(this, isMobile, isIphone);
    private Image headerImage = new Image();
    private motd myMotd = new motd();
    private MotdInfo myMotdInfo = new MotdInfo(this, isMobile);
    private UserButtonBar userButtonBar = new UserButtonBar(this);
    private boolean faviconAlert = false;
    private int newestDisplayedWhenLostVisibility = 0;
    private boolean wideView = true;
    private Timer myOctoTimer;
    private OctoObject OctoArray[] = new OctoObject[5];

    //@Override
    public void onModuleLoad() {
        //MainGinjector ginjector = GWT.create(MainGinjector.class);
        //mainDockPanel = ginjector.getMainPanel();

        //RootPanel.get().add(widget);

        Log.debug("Logger in 'DEBUG' mode");
        String sessionIdCookie = Cookies.getCookie("bffConnexionSID");
        ioModule.GetServerSessionValid(sessionIdCookie);

        RuntimeData.getInstance().setMobile(isMobile);
        RuntimeData.getInstance().setIphone(isIphone);

        myHeaderButtonBar.initialize();
    }

    public bffConn() {
        installShortcuts();
        try {
            registerVisibilityChangeCallback();
        } catch (Exception e) {

        }
        //Resource resource = new Resource( GWT.getModuleBaseURL() + "pizza-service");

        proxy = GWT.create(BffProxy.class);
        //((RestServiceProxy)service).setResource(resource);

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

        topToolbarLowerStack.add(myHeaderButtonBar);
        topToolbarLowerStack.add(userButtonBar);
        topToolbarLowerStack.add(myMotdInfo);
        topToolbarLowerStack.setCellHorizontalAlignment(myMotdInfo, HasHorizontalAlignment.ALIGN_RIGHT);
        topToolbarLowerStack.setCellWidth(myHeaderButtonBar, "245px");
        topToolbarLowerStack.setCellWidth(userButtonBar, "320px");
        topToolbarLowerStack.setCellWidth(myMotdInfo, "235px");
        userButtonBar.setVisible(false);

        topToolbarStack.add(myMotd);
        topToolbarStack.add(topToolbarLowerStack);
        topToolbarStack.setCellHeight(myMotd, "40px");
        topToolbarStack.setCellHeight(myHeaderButtonBar, "32px");
        topToolbarStack.setCellVerticalAlignment(myMotd, HasVerticalAlignment.ALIGN_BOTTOM);

        headerStack.setStyleName("headerStack");
        headerStack.add(headerImage);
        headerStack.add(topToolbarStack);
        headerStack.setCellWidth(headerImage, "240px");
        myHeaderButtonBar.setLocal(RuntimeData.getInstance().getLocale());

        mainDockPanel.addNorth(headerStack, 76);
        mainDockPanel.addWest(leftToolbarStack, 240);
        mainDockPanel.addSouth(myEntryBox, 110);
        mainDockPanel.add(myMessageManager);

        RootLayoutPanel.get().add(mainDockPanel);
        RootLayoutPanel.get().setStyleName("mainPanel");

        //Window.enableScrolling(false);

        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
		consoleLog("Width: " + Window.getClientWidth());
		consoleLog("Height: " + Window.getClientHeight());
		consoleLog("Ori: " + MGWT.getOrientation().toString());
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
                if (faviconAlert) {
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
                if (myRuntimeDataRcvd && myUserDataRcvd) {
                    int start_point = 1;
                    int numMsgToFetch;

                    if (isMobile) {
                        numMsgToFetch = MSG_INITIAL_RTRV_MOBILE;
                    } else {
                        numMsgToFetch = MSG_INITIAL_RTRV;
                    }

                    if (myCurrentMode == MODE_INIT_S1) {
                        myEntryBox.setUser(UserManager.getInstance().getUser(RuntimeData.getInstance().getUserId()));
                        myCurrentMode = MODE_RUNNING;

                        // Initial retrieve
                        if (RuntimeData.getInstance().getServerSeqId() > numMsgToFetch)
                            start_point = RuntimeData.getInstance().getServerSeqId() - numMsgToFetch;
                        else
                            start_point = 1;

                        // Consider DB versions aligned after first retrieve is performed
                        RuntimeData.getInstance().setDbVersion(RuntimeData.getInstance().getServerDbVersion());
                        RuntimeData.getInstance().setDbVersionUsers(RuntimeData.getInstance().getServerDbVersionUsers());

                        String waitMsg = "Chargement des messages <b>" + start_point + " </b>à<b> ";
                        waitMsg += (start_point + numMsgToFetch) + "</b>.";
                        waitMsg += " Veuillez patienter.";
                        if (compactModeEnabled) {
                            myMotd.setText(waitMsg);
                        } else {
                            myWaitBox.setMessage(waitMsg);
                            myWaitBox.show();
                        }

                        ioModule.GetUserMessages(start_point, numMsgToFetch);
                    } else if (myCurrentMode == MODE_RUNNING) {
                        //System.out.println("Db version: " + RuntimeData.getInstance().getDbVersion() + "  Srv Db version: " + RuntimeData.getInstance().getServerDbVersion());
                        if (RuntimeData.getInstance().getDbVersion() < RuntimeData.getInstance().getServerDbVersion()) {
                            System.out.println("DB version behind, updating to " + RuntimeData.getInstance().getDbVersion() + 1);
                            ioModule.GetUserMessagesByVersion(RuntimeData.getInstance().getDbVersion() + 1);
                        }

                        if (RuntimeData.getInstance().getDbVersionUsers() < RuntimeData.getInstance().getServerDbVersionUsers()) {
                            RuntimeData.getInstance().setRequestedDbVersionUsers(RuntimeData.getInstance().getServerDbVersionUsers());
                            ioModule.GetUserInfo();
                        }

                        if (RuntimeData.getInstance().getDbVersionMotd() < RuntimeData.getInstance().getServerDbVersionMotd()) {
                            System.out.println("MOTD version behind, updating to " + RuntimeData.getInstance().getServerDbVersionMotd());
                            ioModule.GetMotd();
                        }
                    }

                    // Don't reschedule if shutting down, nothing good can come out of this
                    if (myCurrentMode != MODE_SHUTDOWN)
                        myRefreshTimer.schedule(2000);
                }
                // Check server version to quickly detect any mismatch
                ioModule.GetRuntimeData();
            }
        };

        // Compact mode is default on mobile
        if (isMobile) {
            hideBarClicked();
        }

        ioModule.GetUserInfo();
        ioModule.GetRuntimeData();
        ioModule.GetMotd();
        myRefreshTimer.schedule(2000);  //  Check if our init Gets are completed
    }

    //@Override
    public void messagesReceivedCallback(final ArrayList<MessageContainer> messages) {
        myMessageManager.newMessages(messages);
    }

    //@Override
    public void runtimeDataReceivedCallback() {
        myRuntimeDataRcvd = true;
        checkServerVersion();
    }

    //@Override
    public void usersReceivedCallback(ArrayList<UserContainer> users) {
        UserManager.getInstance().setUserList(users);
        myUserDataRcvd = true;
        myUserTileDisplay.UpdateOnlineUsers(UserManager.getInstance());
        userButtonBar.updateOnlineUsers();
        RuntimeData.getInstance().setDbVersionUsers(RuntimeData.getInstance().getRequestedDbVersionUsers());
        //System.out.println("Users updated up to db version " + RuntimeData.getInstance().getDbVersionUsers());
    }

    public void checkServerVersion() {
        if (RuntimeData.getInstance().getServerVersion() != VersionInfo.CURRENT_VERSION) {
            myCurrentMode = MODE_SHUTDOWN;
            SC.warn("La version de l'application que vous utilisez est antérieure à celle du serveur. " +
                    "La nouvelle version sera chargée automatiquement lorsque vous fermerez cette fenêtre.",
                    new BooleanCallback() {
                        //@Override
                        public void execute(Boolean value) {
                            Window.Location.reload();
                        }
                    });
        }
    }

    //@Override
    public void performLoginCallback(String login, String password, String local) {

        ioModule.GetNewSession(login, password, local);
    }

    //@Override
    public void sessionReceivedCallback(String sessionId, Integer userId, String userNick, String userLocal) {

        if (sessionId.compareTo("0") == 0)  // Login failed
        {
            LoginWin loginWin = new LoginWin(this);
            loginWin.setVisible(true);
        } else {
            RuntimeData.getInstance().setUserId(userId);
            RuntimeData.getInstance().setSessionId(sessionId);
            RuntimeData.getInstance().setLocale(userLocal);

            // Save sessionId in a cookie so we don't have to re-logon each time we load the app
            long cookieLifespan = 1000 * 60 * 60 * 24 * 7; // one week
            Date expires = new Date(System.currentTimeMillis() + cookieLifespan);
            Cookies.setCookie("bffConnexionSID", sessionId, expires, null, "/", false);

            applicationStart();
        }
    }

    //@Override
    public void accessForbiddenCallback() {
        // Just reload for now, we'll handle graceful re-logins some day.  Riiight.
        Window.Location.reload();
    }

    //@Override
    public void sessionValidReceivedCallback(String sessionId, int userId, String local, boolean valid) {
        if (!valid) {
            // Present the login screen until valid login is performed
            LoginWin loginWin = new LoginWin(this);
            loginWin.setVisible(true);
        } else {
            RuntimeData.getInstance().setUserId(userId);
            RuntimeData.getInstance().setSessionId(sessionId);
            RuntimeData.getInstance().setLocale(local);
            System.out.println("Session was still active: " + RuntimeData.getInstance().getSessionId());
            applicationStart();
        }
    }

    //@Override
    public void messageToSendCallback(String message, boolean edit, int seqId) {
        if (message.startsWith("@mdj")) {
            if (message.equals("@mdj")) {
                ioModule.DeleteMOTD();
            } else {
                ioModule.SendMOTD(message);
            }
        } else {
            if (!edit)
                ioModule.SendUserMessage(message, RuntimeData.getInstance().getNewestSeqId());
            else
                ioModule.SendMessageEdit(message, seqId);
        }

        // Control max number of msg displayed - Disabled for DB debugging
        //if(messageVStack.getMembers().length > 100)
        //  messageVStack.removeMember(messageVStack.getMember(0));

    }

    private void installShortcuts() {
        Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
            //@Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent ne = event.getNativeEvent();
                switch (event.getTypeInt()) {
                    case Event.ONKEYDOWN:
                        if (ne.getCtrlKey() && !ne.getShiftKey()) {
                            if (ne.getKeyCode() == 'l' || ne.getKeyCode() == 'L') {
                                myHeaderButtonBar.showLocationEntry();
                                event.consume();
                                ne.preventDefault();
                                ne.stopPropagation();
                            } else if (ne.getKeyCode() == 'd' || ne.getKeyCode() == 'D') {
                                performLogout();
                                event.consume();
                                ne.preventDefault();
                                ne.stopPropagation();
                            } else if (ne.getKeyCode() == 's' || ne.getKeyCode() == 'S') {
                                statsClicked();
                                event.consume();
                                ne.preventDefault();
                                ne.stopPropagation();
                            } else if (ne.getKeyCode() == 'o' || ne.getKeyCode() == 'O') {
                                octopusClicked();
                                event.consume();
                                ne.preventDefault();
                                ne.stopPropagation();
                            } else if (ne.getKeyCode() == 'i' || ne.getKeyCode() == 'I') {
                                infoClicked();
                                event.consume();
                                ne.preventDefault();
                                ne.stopPropagation();
                            } else if (ne.getKeyCode() == '1') {
                                hideBarClicked();
                                event.consume();
                                ne.preventDefault();
                                ne.stopPropagation();
                            } else if (ne.getKeyCode() == '2') {
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
            function () {
                that.@com.lanouette.app.client.bffConn::visibilityChanged()();
            },
            false);
    }-*/;

    public final native boolean isTabHidden() /*-{
        return document.webkitHidden;
    }-*/;

    private void visibilityChanged() {
        // Reset new message indicator octo when switching to visible
        Log.debug("Visibility changed to " + !isTabHidden());
        try {
            if (!isTabHidden()) {
                Log.debug("Tab is VISIBLE");
                if (faviconAlert) {
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

    //@Override
    public void avatarClicked(String userNick) {
        myEntryBox.addAddressee(userNick);
    }

    ;

    public void performLogout() {
        Cookies.removeCookie("bffConnexionSID", "/");
        ioModule.Logout();
    }

    //@Override
    public void logoutComplete() {
        Window.Location.reload();
    }

    //@Override
    public void logoutClicked() {
        performLogout();
    }

    public void statsClicked() {
        new StatsWin(UserManager.getInstance().getUserList());
    }

    //@Override
    public void localEntered(String local) {
        if (local != null)
            ioModule.SendLocal(local);

        myEntryBox.setFocus();
    }

    //@Override
    public void octopusClicked() {
        proxy.getMotd(12, 1234, 123456, new MethodCallback<List<String>>() {
            public void onFailure(Method method, Throwable throwable) {
                return;
            }

            public void onSuccess(Method method, List<String> strings) {
                return;
            }
            //new OctopusWin();
        });
    }

    //@Override
    public void octopusOnTyped() {
        myOctoBox.show();
        //showBarClicked();
    }

    //@Override
    public void octopusOffTyped() {
        myOctoBox.hide();
    }

    //@Override
    public void scrollTop(int oldest) {
        int firstMsgToFetch;
        int numMsgToFetch;

        if (isMobile) {
            numMsgToFetch = MSG_OLD_FETCH_NUM_MOBILE;
        } else {
            numMsgToFetch = MSG_OLD_FETCH_NUM;
        }

        if (oldest > numMsgToFetch)
            firstMsgToFetch = oldest - numMsgToFetch;
        else
            firstMsgToFetch = 1;

        String waitMsg = "Chargement des messages <b>" + firstMsgToFetch + " </b>à<b> ";
        waitMsg += (firstMsgToFetch + numMsgToFetch) + "</b>";
        waitMsg += ". Veuillez patienter.";

        if (compactModeEnabled) {
            myMotd.setText(waitMsg);
        } else {
            myWaitBox.setMessage(waitMsg);
            myWaitBox.show();
        }
        ioModule.GetUserMessages(firstMsgToFetch, numMsgToFetch);
    }

    //@Override
    public void messageDisplayComplete() {
        if (compactModeEnabled) {
            ioModule.GetMotd();
        } else {
            myWaitBox.hide();
        }
    }

    //@Override
    public void starClicked(int seqId) {
        ioModule.SendStarMessage(seqId);
    }

    //@Override
    public void deleteClicked(int seqId) {
        ioModule.SendDeleteMessage(seqId);
    }

    //@Override
    public void newestUpdated() {
        try {
            Log.debug("newestUpdated called");
            if (myCurrentMode == MODE_RUNNING && !faviconAlert && isTabHidden()) {
                Log.debug("Starting favicon timer");
                faviconAlert = true;
                myFaviconTimer.schedule(300);
            }
        } catch (Exception e) {
            Log.debug("Exception in newestUpdated");
        }
    }

    //@Override
    public void userEntry() {
        myMessageManager.ClearUnreadAll();
    }

    //@Override
    public void editMessageClicked(MessageContainer message) {
        myEntryBox.editMessage(message);
    }

    //@Override
    public void infoClicked() {
        //SC.showConsole();
        Window.open("https://github.com/preacher860/bffConn/wiki/Historique-des-changements", "Historique", "");
    }

    //@Override
    public void superOctopusOnTyped() {
        //for(OctoObject octo:OctoArray){
        //	octo.showOcto();
        //}
        //myOctoTimer.scheduleRepeating(50);
    }

    //@Override
    public void superOctopusOffTyped() {
        //for(OctoObject octo:OctoArray){
        //	octo.hideOcto();
        //}
        //myOctoTimer.cancel();


    }

    //@Override
    public void hideBarClicked() {
        compactModeEnabled = true;
        if (isMobile) {
            mainDockPanel.setWidgetSize(leftToolbarStack, 10);
        } else {
            mainDockPanel.setWidgetSize(leftToolbarStack, 35);
        }
        leftToolbarStack.setVisible(false);
        headerImage.setVisible(false);
        headerStack.setCellWidth(headerImage, "40px");
        myHeaderButtonBar.setCompactView();
        userButtonBar.setVisible(true);
        }

    //@Override
    public void showBarClicked() {
        compactModeEnabled = false;
        mainDockPanel.setWidgetSize(leftToolbarStack, 240);
        leftToolbarStack.setVisible(true);
        headerImage.setVisible(true);
        headerStack.setCellWidth(headerImage, "240px");
        myHeaderButtonBar.setNormalView();
        userButtonBar.setVisible(false);
    }

    //@Override
    public void motdReceivedCallback(motdData motd) {
        myMotdRcvd = true;
        RuntimeData.getInstance().setDbVersionMotd(motd.dbVersion);
        if (myMotd.hasChanged(motd) || motd.deleted == 1) {
            myMotd.update(motd);
            myMotdInfo.update(motd, UserManager.getInstance().getUser(motd.userId).getNick());
        }
    }

    //@Override
    public void motdStarClicked() {
        ioModule.SendStarMOTD();
    }

    //@Override
    public void motdDeleteClicked() {
        ioModule.DeleteMOTD();
    }

    public boolean checkMobile() {
        if (Window.Navigator.getUserAgent().contains("Mobile") ||
                Window.Navigator.getUserAgent().contains("Android")) {
            return true;
        }

        return false;
    }

    public boolean checkIphone() {
	if(Window.Navigator.getUserAgent().contains("iPhone")) {
	    return true;
	}
	return false;
    }

    native void consoleLog(String message) /*-{
      console.log( "BFF: " + message );
    }-*/;
}

