package com.lanouette.app.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.IconBar.IconBar;
import com.lanouette.app.client.MessageBox.MessageBox;
import com.lanouette.app.client.OnlineUsersView.OnlineUsers;
import com.lanouette.app.client.StatsWindow.StatsWin;

//import org.fusesource.restygwt.client.Method;
//import org.fusesource.restygwt.client.MethodCallback;

public class bffConn implements EntryPoint, ioCallbackInterface, UserCallbackInterface {
    interface uiBinder extends UiBinder<Widget, bffConn> {
    }

    public static final int MODE_INIT_S1 = 1;
    public static final int MODE_INIT_S2 = 2;
    public static final int MODE_RUNNING = 3;
    public static final int MODE_SHUTDOWN = 4;
    private static final int MSG_INITIAL_RTRV = 400;
    private static final int MSG_OLD_FETCH_NUM = 200;
    private static final int MSG_INITIAL_RTRV_MOBILE = 50;
    private static final int MSG_OLD_FETCH_NUM_MOBILE = 100;
    private static final int POLL_FAST = 2000;
    private static final int POLL_SLOW = 10000;
    private static final int MAX_JUMPBACK_MESSAGES = 2000;
    private static final int MAX_JUMPBACK_MESSAGES_MOBILE = 500;
    private final uiBinder uiBinder = GWT.create(uiBinder.class);
    //private final BffProxy proxy;
    private final boolean isMobile = checkMobile();
    private final boolean isIphone = checkIphone();
    @UiField
    DockLayoutPanel mainDockPanel;
    @UiField
    HorizontalPanel headerStack;
    @UiField
    VerticalPanel topToolbarStack;
    @UiField
    HorizontalPanel topToolbarLowerStack;
    @UiField
    VerticalPanel leftToolbarStack;
    @UiField
    Image headerImage;
    @UiField
    Motd myMotd;
    @UiField
    IconBar iconBar;
    @UiField
    UserButtonBar userButtonBar;
    @UiField
    MotdInfo myMotdInfo;
    @UiField
    OnlineUsers onlineUsers;
    @UiField
    MessageBox messageBox;
    @UiField
    EntryBox myEntryBox;
    @UiField
    MessageView myMessageManager;
    private Integer jumpAfterLoad = 0;
    private IOModule ioModule = new IOModule(this);
    private Integer myCurrentMode = MODE_INIT_S1;
    private Timer myRefreshTimer;
    private Timer myFaviconTimer;
    private Timer myResizeTimer;
    private Timer myInitTimer;
    private boolean compactModeEnabled = false;
    private boolean myRuntimeDataRcvd = false;
    private boolean myUserDataRcvd = false;
    private boolean myMotdRcvd = false;
    private boolean faviconAlert = false;
    private int myPollSpeed = POLL_FAST;

    public bffConn() {
        installShortcuts();
        try {
            registerVisibilityChangeCallback();
        } catch (Exception e) {

        }
        //Resource resource = new Resource( GWT.getModuleBaseURL() + "pizza-service");

        //proxy = GWT.create(BffProxy.class);
        //((RestServiceProxy)service).setResource(resource);

    }

    public void onModuleLoad() {
        //MainGinjector ginjector = GWT.create(MainGinjector.class);
        //mainDockPanel = ginjector.getMainPanel();

        consoleLog("Logger in 'DEBUG' mode");
        String sessionIdCookie = Cookies.getCookie("bffConnexionSID");
        ioModule.GetServerSessionValid(sessionIdCookie);

        RuntimeData.getInstance().setMobile(isMobile);
        RuntimeData.getInstance().setIphone(isIphone);

        uiBinder.createAndBindUi(this);

        iconBar.initialize(this);
        userButtonBar.initialize(this);
        myMotdInfo.initialize(this);
        onlineUsers.initialize(this);
        myEntryBox.initialize(this, this);
        myMessageManager.initialize(this);

        UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
        ConsoleLogger.getInstance().log("Host: " + Window.Location.getHost());
        ConsoleLogger.getInstance().log("Hostname: " + Window.Location.getHostName());
        ConsoleLogger.getInstance().log("Href: " + Window.Location.getHref());
        ConsoleLogger.getInstance().log("Path: " + Window.Location.getPath());
        ConsoleLogger.getInstance().log("Port: " + Window.Location.getPort());
        ConsoleLogger.getInstance().log("Proto: " + Window.Location.getProtocol());
        ConsoleLogger.getInstance().log("Hash: " + Window.Location.getHash());
        ConsoleLogger.getInstance().log("App URL: " + Window.Location.getProtocol() + "//" +  Window.Location.getHost() );
    }

    public void applicationStart() {
        iconBar.setLocal(RuntimeData.getInstance().getLocale());

        RootLayoutPanel.get().add(mainDockPanel);

        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                consoleLog("Width: " + Window.getClientWidth());
                consoleLog("Height: " + Window.getClientHeight());
                //consoleLog("Ori: " + MGWT.getOrientation().toString());
                myResizeTimer.schedule(500);
            }
        });

        myResizeTimer = new Timer() {
            @Override
            public void run() {
                myMessageManager.toBottom(false);
            }
        };

        myFaviconTimer = new Timer() {
            @Override
            public void run() {
                Element element = DOM.getElementById("favicon");
                if (faviconAlert) {
                    consoleLog("Setting favicon to alert (red)");
                    element.setAttribute("href", "images/favicon_red.ico");
                } else {
                    consoleLog("Setting favicon to normal (blue)");
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
                            messageBox.setMessage(waitMsg);
                            messageBox.setVisible(true);
                        }

                        ioModule.GetUserMessages(start_point, numMsgToFetch);
                    } else if (myCurrentMode == MODE_RUNNING) {

                    }

                    // Don't reschedule if shutting down, nothing good can come out of this
                    if (myCurrentMode != MODE_SHUTDOWN)
                        myRefreshTimer.schedule(myPollSpeed);
                }
                // Check server version to quickly detect any mismatch
                ioModule.GetRuntimeData();
            }
        };

        // Compact mode is default on mobile
        if (isMobile) {
            hideBarClicked();
        }

        myInitTimer = new Timer() {
            Integer initLoops = 0;

            @Override
            public void run() {
                if (myRuntimeDataRcvd && myUserDataRcvd) {
                    myInitTimer.cancel();
                    myRefreshTimer.schedule(1);
                } else {
                    if ((initLoops % 2000) == 0) {
                        ioModule.GetUserInfo();
                        ioModule.GetRuntimeData();
                        ioModule.GetMotd();
                    }
                    initLoops += 100;
                    myInitTimer.schedule(100);
                }
            }
        };

        myInitTimer.schedule(1);
    }

    public void messagesReceivedCallback(final ArrayList<MessageContainer> messages) {
        myMessageManager.newMessages(messages, jumpAfterLoad > 0);

        if (jumpAfterLoad > 0) {
            jumpToMessage(jumpAfterLoad);
            jumpAfterLoad = 0;
        }
    }

    public void runtimeDataReceivedCallback() {
        myRuntimeDataRcvd = true;
        checkServerVersion();
        checkRuntimeVersions();
    }

    public void usersReceivedCallback(ArrayList<UserContainer> users) {
        UserManager.getInstance().setUserList(users);
        myUserDataRcvd = true;
        onlineUsers.UpdateOnlineUsers(UserManager.getInstance());
        userButtonBar.updateOnlineUsers();
        RuntimeData.getInstance().setDbVersionUsers(RuntimeData.getInstance().getRequestedDbVersionUsers());
    }

    public void checkServerVersion() {
        if (RuntimeData.getInstance().getServerVersion() != VersionInfo.CURRENT_VERSION) {
            myCurrentMode = MODE_SHUTDOWN;
            Window.alert("La version de l'application que vous utilisez est antérieure à celle du serveur. " +
                    "La nouvelle version sera chargée automatiquement lorsque vous fermerez ce message.");
            Window.Location.reload();
        }
    }

    public void checkRuntimeVersions() {
        if (myCurrentMode == MODE_RUNNING) {
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
    }

    public void performLoginCallback(String login, String password, String local) {
        ioModule.GetNewSession(login, password, local);
    }

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

    public void accessForbiddenCallback() {
        // Just reload for now, we'll handle graceful re-logins some day.  Riiight.
        Window.Location.reload();
    }

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
        // Note: it's actually faster not to...
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

    public void avatarClicked(String userNick) {
        myEntryBox.addAddressee(userNick);
    }

    public void performLogout() {
        Cookies.removeCookie("bffConnexionSID", "/");
        ioModule.Logout();
    }

    public void logoutComplete() {
        Window.Location.reload();
    }

    public void logoutClicked() {
        performLogout();
    }

    public void statsClicked() {
        new StatsWin(UserManager.getInstance().getUserList());
    }

    public void localEntered(String local) {
        if (local != null)
            ioModule.SendLocal(local);

        myEntryBox.setFocus();
    }

    public void octopusClicked() {
//        proxy.getMotd(12, 1234, 123456, new MethodCallback<List<String>>() {
//            public void onFailure(Method method, Throwable throwable) {
//                return;
//            }
//
//            public void onSuccess(Method method, List<String> strings) {
//                return;
//            }
//            //new OctopusWin();
//        });

    }

    public void jumpEntered(Integer jumpId) {
        // First check with message manager if this message is loaded
        if (myMessageManager.isMessageLoaded(jumpId)) {
            ConsoleLogger.getInstance().log("Message is loaded");
            jumpToMessage(jumpId);
        } else if (jumpId > myMessageManager.getNewestDisplayedSeq()){
            myMessageManager.scrollToBottom();
        } else {
            ConsoleLogger.getInstance().log("Message is NOT loaded");
            Integer jumpBack = RuntimeData.getInstance().isMobile()? MAX_JUMPBACK_MESSAGES_MOBILE : MAX_JUMPBACK_MESSAGES;
            if ((jumpId - 10 > myMessageManager.getOldestDisplayedSeq() - jumpBack)) {
                jumpAfterLoad = jumpId;
                loadMessages((jumpId - 10 < 1)? 1 : jumpId - 10, myMessageManager.getOldestDisplayedSeq() - jumpId + 10);
            }
        }
    }

    public void jumpToMessage(Integer jumpId) {
        // Element should usually be locatable, but take next available if it's not there (likely deleted)
        MessageViewElement element;

        for (Integer seqId = jumpId; seqId < myMessageManager.getNewestDisplayedSeq(); seqId++) {
            element = myMessageManager.locateElement(seqId);

            if ((element != null) && (element.isVisible())) {
                myMessageManager.cancelKeepAtBottom();
                myMessageManager.setVerticalScrollPosition(myMessageManager.getVerticalScrollPosition() -
                        (element.getAbsoluteTop() - 90) * -1);
                myMessageManager.setSelectedMessage(element);

                break;
            }
        }
    }

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

        loadMessages(firstMsgToFetch, numMsgToFetch);
    }

    public void loadMessages(Integer firstMsgToFetch, Integer numMsgToFetch) {
        String waitMsg = "Chargement des messages <b>" + firstMsgToFetch + " </b>à<b> ";
        waitMsg += (firstMsgToFetch + numMsgToFetch) + "</b>";
        waitMsg += ". Veuillez patienter.";

        if (compactModeEnabled) {
            myMotd.setText(waitMsg);
        } else {
            messageBox.setMessage(waitMsg);
            messageBox.setVisible(true);
        }
        ioModule.GetUserMessages(firstMsgToFetch, numMsgToFetch);
    }

    public void messageDisplayComplete() {
        if (compactModeEnabled) {
            ioModule.GetMotd();
        } else {
            messageBox.setVisible(false);
        }
    }

    public void starClicked(int seqId) {
        ioModule.SendStarMessage(seqId);
    }

    public void deleteClicked(int seqId) {
        ioModule.SendDeleteMessage(seqId);
    }

    public void newestUpdated() {
        try {
            consoleLog("newestUpdated called");
            if (myCurrentMode == MODE_RUNNING && !faviconAlert && isTabHidden()) {
                consoleLog("Starting favicon timer");
                faviconAlert = true;
                myFaviconTimer.schedule(300);
            }
        } catch (Exception e) {
            consoleLog("Exception in newestUpdated");
        }
    }

    public void userEntry() {
        myMessageManager.ClearUnreadAll();
        myMessageManager.clearSelectedMessage();
    }

    public void editMessageClicked(MessageContainer message) {
        myEntryBox.editMessage(message);
    }

    public void infoClicked() {
        Window.open("https://github.com/preacher860/bffConn/wiki/Historique-des-changements", "Historique", "");
    }

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
        iconBar.setCompactView();
        userButtonBar.setVisible(true);
    }

    public void showBarClicked() {
        compactModeEnabled = false;
        mainDockPanel.setWidgetSize(leftToolbarStack, 225);
        leftToolbarStack.setVisible(true);
        headerImage.setVisible(true);
        headerStack.setCellWidth(headerImage, "225px");
        iconBar.setNormalView();
        userButtonBar.setVisible(false);
    }

    public void motdReceivedCallback(MotdData motd) {
        myMotdRcvd = true;
        RuntimeData.getInstance().setDbVersionMotd(motd.dbVersion);
        if (myMotd.hasChanged(motd) || motd.deleted == 1) {
            myMotd.update(motd);
            myMotdInfo.update(motd, UserManager.getInstance().getUser(motd.userId).getNick());
        }
    }

    public void motdStarClicked() {
        ioModule.SendStarMOTD();
    }

    public void motdDeleteClicked() {
        ioModule.DeleteMOTD();
    }

    public void jumpLinkClicked(Integer seqId) {
        jumpEntered(seqId);
    }

    public boolean checkMobile() {
        if (Window.Navigator.getUserAgent().contains("Mobile") ||
                Window.Navigator.getUserAgent().contains("Android")) {
            return true;
        }

        return false;
    }

    public boolean checkIphone() {
        if (Window.Navigator.getUserAgent().contains("iPhone")) {
            return true;
        }
        return false;
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
                                iconBar.showLocationEntry();
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
                                iconBar.showJumpEntry();
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

    private void visibilityChanged() {
        // Reset new message indicator octo when switching to visible
        consoleLog("Visibility changed to " + !isTabHidden());
        try {
            if (!isTabHidden()) {
                consoleLog("Tab is VISIBLE");
                if (faviconAlert) {
                    consoleLog("Starting favicon timer");
                    faviconAlert = false;
                    myFaviconTimer.schedule(300);
                }
                consoleLog("Setting message manager mode to visible");
                myMessageManager.setInvisibleMode(false);
                myPollSpeed = POLL_FAST;
                myRefreshTimer.schedule(1);
            } else {
                consoleLog("Tab is HIDDEN");
                consoleLog("Setting message manager mode to invisible");
                myMessageManager.setInvisibleMode(true);
                if (RuntimeData.getInstance().isMobile()) {
                    myPollSpeed = POLL_SLOW;
                    myEntryBox.blurFocus();
                }
            }
        } catch (Exception e) {
            consoleLog("Exception in visibilityChanged");
        }
    }

    native void consoleLog(String message) /*-{
        console.log("BFF: " + message);
    }-*/;
}

