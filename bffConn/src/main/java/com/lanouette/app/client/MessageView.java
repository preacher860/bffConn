package com.lanouette.app.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageView extends ScrollPanel {

    private static final int KEEP_AT_BOTTOM_RETRIES = 3;

    private VerticalPanel mainVPanel = new VerticalPanel();
    private int myOldestDisplayedSeq = 0;
    private int myNewestDisplayedSeq = 0;
    private int myNewestDisplayedDb = 0;
    private int myNumOfMessagesDisplayed = 0;
    private UserCallbackInterface myCallbackInterface;
    private Timer myScrollTimer;
    private Timer myPositionTimer;

    private boolean myAtBottom = true;
    private boolean myFetchingOld = false;
    private int myLastKnownOldest = 0;
    private int myUnreadLowest = 0;
    private int myUnreadHighest = 0;
    private int myKeepAtBottom = 0;
    private boolean myInvisibleMode = false;
    private MessageViewElement selectedMessage = null;
    private Boolean alertHasFired = false;

    public MessageView() {

    }

    public void initialize(UserCallbackInterface callbackInterface) {
        myCallbackInterface = callbackInterface;

        mainVPanel.setStyleName("messageViewVpanel");
        add(mainVPanel);

        setStyleName("messageView");
        getElement().setId("messageView");

        // This scroll handler sets the flag used to determine if we're at bottom or not.
        // Only if were at bottom do we kick the autoscroll on new messages
        addScrollHandler(new ScrollHandler() {
            public void onScroll(ScrollEvent event) {
                if (getVerticalScrollPosition() == getMaximumVerticalScrollPosition()) {
                    myAtBottom = true;
                } else {
                    myAtBottom = false;
                }

                if (getVerticalScrollPosition() == 0) {
                    // Autofetch old message when scrolling to top
                    myLastKnownOldest = myOldestDisplayedSeq;
                    myFetchingOld = true;
                    myCallbackInterface.scrollTop(myOldestDisplayedSeq);
                }
            }
        });

        myScrollTimer = new Timer() {
            @Override
            public void run() {
                scrollToBottom();
                if (myKeepAtBottom < KEEP_AT_BOTTOM_RETRIES) {
                    System.out.println("Forcing at bottom ret: " + myKeepAtBottom);
                    myScrollTimer.schedule(1000);
                    myKeepAtBottom++;
                }
            }
        };

        myPositionTimer = new Timer() {
            @Override
            public void run() {
                MessageViewElement element;
                if ((element = locateElement(myLastKnownOldest)) != null) {
                    System.out.println("Setting scrollpos to " + element.getElement().getOffsetTop());
                    setVerticalScrollPosition(element.getElement().getOffsetTop());
                }
            }
        };
    }

    public void cancelKeepAtBottom() {
        myKeepAtBottom = 0;
        myScrollTimer.cancel();
    }

    public void newMessages(ArrayList<MessageContainer> messages, Boolean jumping) {
        boolean listWasEmtpy;
        boolean messagesNotOwn = false;
        boolean myNewestUpdated = false;

        if (myNumOfMessagesDisplayed > 0) {
            myNewestDisplayedSeq = ((MessageViewElement) mainVPanel.getWidget(myNumOfMessagesDisplayed - 1)).getMessage().getMessageSeqId();
            myOldestDisplayedSeq = ((MessageViewElement) mainVPanel.getWidget(0)).getMessage().getMessageSeqId();
            listWasEmtpy = false;
        } else
            listWasEmtpy = true;

        for (int msgIndex = 0; msgIndex < messages.size(); msgIndex++) {

            MessageContainer currentMessage = messages.get(msgIndex);
            if (currentMessage.getMessageUserId() != RuntimeData.getInstance().getUserId())
                messagesNotOwn = true;

            MessageViewElement element = new MessageViewElement(currentMessage,
                    UserManager.getInstance().getUser(currentMessage.getMessageUserId()),
                    UserManager.getInstance().getUser(RuntimeData.getInstance().getUserId()),
                    myCallbackInterface);

            // When in invisible mode, all new messages are flagged unread
            if (myInvisibleMode) {
                element.setUnread(true);
                if ((myUnreadLowest == 0) || (myUnreadLowest > currentMessage.getMessageSeqId()))
                    myUnreadLowest = currentMessage.getMessageSeqId();
                if ((myUnreadHighest == 0) || (myUnreadHighest < currentMessage.getMessageSeqId()))
                    myUnreadHighest = currentMessage.getMessageSeqId();
            }

            // Locate the position we want to insert this new element at (will refine this later)
            //System.out.println(" Newest: " + myNewestDisplayedSeq + " oldest: " + myOldestDisplayedSeq);
            if (currentMessage.getMessageSeqId() > myNewestDisplayedSeq) {
                //System.out.println(" inserting " + currentMessage.getMessageSeqId() + " at bottom");
                mainVPanel.add(element);
                myNewestDisplayedSeq = currentMessage.getMessageSeqId();
                myNumOfMessagesDisplayed++;
                myNewestUpdated = true;
            } else if (currentMessage.getMessageSeqId() < myOldestDisplayedSeq) {
                //System.out.println(" inserting " + currentMessage.getMessageSeqId() + " at top");
                mainVPanel.insert(element, 0);
                myOldestDisplayedSeq = currentMessage.getMessageSeqId();
                myNumOfMessagesDisplayed++;
            } else {  //somewhere in-between.  Find appropriate insertion point
                for (int elementIndex = myNumOfMessagesDisplayed - 1; elementIndex > 0; elementIndex--) {
                    int currentSeq = ((MessageViewElement) mainVPanel.getWidget(elementIndex)).getMessage().getMessageSeqId();
                    int previousSeq = ((MessageViewElement) mainVPanel.getWidget(elementIndex - 1)).getMessage().getMessageSeqId();
                    //System.out.println("current: " + currentSeq + " previous: " + previousSeq);

                    if (currentSeq == currentMessage.getMessageSeqId()) {
                        ((MessageViewElement) mainVPanel.getWidget(elementIndex)).updateMessage(currentMessage);
                        break;
                    }
                    if ((currentMessage.getMessageSeqId() < currentSeq) && (currentMessage.getMessageSeqId() > previousSeq)) {
                        //System.out.println(" inserting " + currentMessage.getMessageSeqId() + " before " + currentSeq);
                        mainVPanel.insert(element, elementIndex);
                        myNumOfMessagesDisplayed++;
                        break;
                    }
                }
            }
            //System.out.println("  In queue: " + messageList.size());
            if (currentMessage.getMessageDbVersion() > myNewestDisplayedDb)
                myNewestDisplayedDb = currentMessage.getMessageDbVersion();
        }
        myNewestDisplayedSeq = ((MessageViewElement) mainVPanel.getWidget(myNumOfMessagesDisplayed - 1)).getMessage().getMessageSeqId();
        myOldestDisplayedSeq = ((MessageViewElement) mainVPanel.getWidget(0)).getMessage().getMessageSeqId();

        RuntimeData.getInstance().setNewestSeqId(myNewestDisplayedSeq);
        RuntimeData.getInstance().setDbVersion(myNewestDisplayedDb);

        if (myAtBottom && !jumping) {
            myKeepAtBottom = 0;
            myScrollTimer.schedule(200);
        }

        if (myFetchingOld) {
            myFetchingOld = false;
            myPositionTimer.schedule(200);
        }

        if (myNewestUpdated && !listWasEmtpy && messagesNotOwn) {
            myCallbackInterface.newestUpdated();

            if (myInvisibleMode) {
                if ((CookieData.getInstance().getAudioMode().equals("once") && !alertHasFired) ||
                        CookieData.getInstance().getAudioMode().equals("every") ||
                        CookieData.getInstance().getAudioMode().equals("always")) {
                    Alerts.getInstance().newMessageAlert();
                    alertHasFired = true;
                }
            } else if (CookieData.getInstance().getAudioMode().equals("always")) {
                Alerts.getInstance().newMessageAlert();
            }
        }

        myCallbackInterface.messageDisplayComplete();
    }

    public MessageViewElement locateElement(int seqId) {
        for (int elementIndex = myNumOfMessagesDisplayed - 1; elementIndex > 0; elementIndex--) {
            if (((MessageViewElement) mainVPanel.getWidget(elementIndex)).getMessage().getMessageSeqId() == seqId) {
                return ((MessageViewElement) mainVPanel.getWidget(elementIndex));
            }
        }

        return null;
    }

    public void setUnreadRange(int low, int high) {
        myUnreadLowest = low;
        myUnreadHighest = high;

        for (int messageId = low; messageId <= high; messageId++) {
            MessageViewElement currentElement = locateElement(messageId);
            if (currentElement != null)
                currentElement.setUnread(true);
        }
    }

    public void ClearUnreadAll() {
        for (int messageId = myUnreadLowest; messageId <= myUnreadHighest; messageId++) {
            MessageViewElement currentElement = locateElement(messageId);
            if (currentElement != null)
                currentElement.setUnread(false);
        }
        myUnreadLowest = 0;
        myUnreadHighest = 0;
    }

    public void setSelectedMessage(MessageViewElement message) {
        clearSelectedMessage();

        selectedMessage = message;
        message.messageSelect();
    }

    public void clearSelectedMessage() {
        if (selectedMessage != null) {
            selectedMessage.messageUnselect();
        }
    }

    public int getOldestDisplayedSeq() {
        return myOldestDisplayedSeq;
    }

    public int getNewestDisplayedSeq() {
        return myNewestDisplayedSeq;
    }

    public void setInvisibleMode(boolean mode) {
        myInvisibleMode = mode;

        if (!mode) {
            alertHasFired = false;
        }
        System.out.println("Invisible mode set to " + myInvisibleMode);
    }

    public void toBottom(boolean forced) {
        if (myAtBottom || forced)
            scrollToBottom();
    }

    public Boolean isMessageLoaded(Integer seqId) {
        if ((seqId > myNewestDisplayedSeq) || (seqId < myOldestDisplayedSeq)) {
            return false;
        } else {
            return true;
        }
    }
}
