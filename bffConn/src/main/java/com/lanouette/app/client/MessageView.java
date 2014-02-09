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
	private int myNewestDisplayedDb  = 0;
	private int myNumOfMessagesDisplayed = 0;
	private userCallbackInterface myCallbackInterface;
	private Timer myScrollTimer;
	private Timer myPositionTimer;
	
	private boolean myAtBottom = true;
	private boolean myFetchingOld = false;
	private int	myLastKnownOldest = 0; 
	private int myUnreadLowest = 0;
	private int myUnreadHighest = 0;
	private int myKeepAtBottom = 0;
	private boolean myInvisibleMode = false;
	
	public MessageView(userCallbackInterface callbackInterface){

		myCallbackInterface = callbackInterface;
		
		mainVPanel.setStyleName("messageViewVpanel");
		add(mainVPanel);

		setStyleName("messageView");
		
        // This scroll handler sets the flag used to determine if we're at bottom or not.
        // Only if were at bottom do we kick the autoscroll on new messages
        addScrollHandler(new ScrollHandler(){
			@Override
			public void onScroll(ScrollEvent event) {
				if(getVerticalScrollPosition() == getMaximumVerticalScrollPosition()){
					myAtBottom = true;
				}
				else {
					myAtBottom = false;
				}
				
				if(getVerticalScrollPosition() == 0)	{
					// Autofetch old message when scrolling to top
					myLastKnownOldest = myOldestDisplayedSeq;
					myFetchingOld = true;
					myCallbackInterface.scrollTop(myOldestDisplayedSeq);
				}
			}
        } );
        
        myScrollTimer = new Timer() {
		      @Override
		      public void run() {
		    	  scrollToBottom();
		    	  if(myKeepAtBottom < KEEP_AT_BOTTOM_RETRIES) {
		    		  System.out.println("Forcing at bottom ret: " + myKeepAtBottom);
		    		  myScrollTimer.schedule(1000);
		    		  myKeepAtBottom++;
		    	  }
		      }
		    };
		    
	    myPositionTimer = new Timer() {
			@Override
			public void run() {
				MessageViewElementNative element;				
				if ( (element = locateElement(myLastKnownOldest)) != null) {
					System.out.println("Setting scrollpos to " + element.getElement().getOffsetTop());
					setVerticalScrollPosition(element.getElement().getOffsetTop());
				}
			}
	    };
	}
	
	public void newMessages(ArrayList<MessageContainer> messages) {
		boolean listWasEmtpy;
		boolean messagesNotOwn = false;
		boolean myNewestUpdated = false;
		
		if(myNumOfMessagesDisplayed > 0){
			myNewestDisplayedSeq = ((MessageViewElementNative)mainVPanel.getWidget(myNumOfMessagesDisplayed - 1)).getMessage().getMessageSeqId();
			myOldestDisplayedSeq = ((MessageViewElementNative)mainVPanel.getWidget(0)).getMessage().getMessageSeqId();
			listWasEmtpy = false;
		} else
			listWasEmtpy = true;
	
		for(int msgIndex = 0; msgIndex < messages.size(); msgIndex++) {

			MessageContainer currentMessage = messages.get(msgIndex);
			checkOcto(currentMessage);
			if(currentMessage.getMessageUserId() != RuntimeData.getInstance().getUserId())
				messagesNotOwn = true;

			MessageViewElementNative element = new MessageViewElementNative(currentMessage, 
					UserManager.getInstance().getUser(currentMessage.getMessageUserId()),
					UserManager.getInstance().getUser(RuntimeData.getInstance().getUserId()),
					myCallbackInterface);

			// When in invisible mode, all new messages are flagged unread
			if (myInvisibleMode) {
				element.setUnread(true);
				if((myUnreadLowest == 0) || (myUnreadLowest > currentMessage.getMessageSeqId()))
					myUnreadLowest = currentMessage.getMessageSeqId();
				if((myUnreadHighest == 0) || (myUnreadHighest < currentMessage.getMessageSeqId()))
					myUnreadHighest = currentMessage.getMessageSeqId();
			}
			
			// Locate the position we want to insert this new element at (will refine this later)
			//System.out.println(" Newest: " + myNewestDisplayedSeq + " oldest: " + myOldestDisplayedSeq);
			if(currentMessage.getMessageSeqId() > myNewestDisplayedSeq){
				//System.out.println(" inserting " + currentMessage.getMessageSeqId() + " at bottom");
				mainVPanel.add(element);
				myNewestDisplayedSeq = currentMessage.getMessageSeqId();
				myNumOfMessagesDisplayed++;
				myNewestUpdated = true;
			}
			else if (currentMessage.getMessageSeqId() < myOldestDisplayedSeq) {
				//System.out.println(" inserting " + currentMessage.getMessageSeqId() + " at top");
				mainVPanel.insert(element, 0);
				myOldestDisplayedSeq = currentMessage.getMessageSeqId();
				myNumOfMessagesDisplayed++;
			}
			else {  //somewhere in-between.  Find appropriate insertion point
				for(int elementIndex = myNumOfMessagesDisplayed - 1; elementIndex > 0; elementIndex--) {
					int currentSeq = ((MessageViewElementNative)mainVPanel.getWidget(elementIndex)).getMessage().getMessageSeqId();
					int previousSeq = ((MessageViewElementNative)mainVPanel.getWidget(elementIndex - 1)).getMessage().getMessageSeqId();
					//System.out.println("current: " + currentSeq + " previous: " + previousSeq);

					if (currentSeq == currentMessage.getMessageSeqId()){
						((MessageViewElementNative)mainVPanel.getWidget(elementIndex)).updateMessage(currentMessage);
						break;
					}
					if((currentMessage.getMessageSeqId() < currentSeq) && (currentMessage.getMessageSeqId() > previousSeq)){
						//System.out.println(" inserting " + currentMessage.getMessageSeqId() + " before " + currentSeq);
						mainVPanel.insert(element, elementIndex);
						myNumOfMessagesDisplayed++;
						break;
					}
				}
			}
			//System.out.println("  In queue: " + messageList.size());
			if(currentMessage.getMessageDbVersion() > myNewestDisplayedDb)
				myNewestDisplayedDb = currentMessage.getMessageDbVersion();
		}
		myNewestDisplayedSeq = ((MessageViewElementNative)mainVPanel.getWidget(myNumOfMessagesDisplayed - 1)).getMessage().getMessageSeqId();
		myOldestDisplayedSeq = ((MessageViewElementNative)mainVPanel.getWidget(0)).getMessage().getMessageSeqId();
		
		RuntimeData.getInstance().setNewestSeqId(myNewestDisplayedSeq);
		RuntimeData.getInstance().setDbVersion(myNewestDisplayedDb);
		
		if(myAtBottom) {
			myKeepAtBottom = 0;
			myScrollTimer.schedule(200);
		}
		
		if(myFetchingOld){
			myFetchingOld = false;
			myPositionTimer.schedule(200);
		}
		
		if (myNewestUpdated && !listWasEmtpy && messagesNotOwn){
			myCallbackInterface.newestUpdated();
		}
			
		
		myCallbackInterface.messageDisplayComplete();
	}
	
	public MessageViewElementNative locateElement(int seqId)
	{
		for(int elementIndex = myNumOfMessagesDisplayed - 1; elementIndex > 0; elementIndex--)
			if (((MessageViewElementNative)mainVPanel.getWidget(elementIndex)).getMessage().getMessageSeqId() == seqId)
				return ((MessageViewElementNative)mainVPanel.getWidget(elementIndex));
		return null;
	}
	
	public void checkOcto(MessageContainer message)
	{
		if(message.getMessage().contains("octo!!!"))
	    	myCallbackInterface.octopusOnTyped();
    	if(message.getMessage().contains("!!!octo"))
	    	myCallbackInterface.octopusOffTyped();
    	if(message.getMessage().contains("superOcto!!!"))
	    	myCallbackInterface.superOctopusOnTyped();
    	if(message.getMessage().contains("Jérôme!!!"))
	    	myCallbackInterface.superOctopusOffTyped();
	}

	public void setUnreadRange(int low, int high) {
		myUnreadLowest = low;
		myUnreadHighest = high;
		
		for (int messageId = low; messageId <= high; messageId++)
		{
			MessageViewElementNative currentElement = locateElement(messageId);
			if (currentElement != null)
				currentElement.setUnread(true);
		}
	}
	
	public void ClearUnreadAll() {
		for (int messageId = myUnreadLowest; messageId <= myUnreadHighest; messageId++)
		{
			MessageViewElementNative currentElement = locateElement(messageId);
			if (currentElement != null)
				currentElement.setUnread(false);
		}
		myUnreadLowest = 0;
		myUnreadHighest = 0;
	}
	
	public int getOldestDisplayedSeq() {
		return myOldestDisplayedSeq;
	}

	public int getNewestDisplayedSeq() {
		return myNewestDisplayedSeq;
	}
	
	public void setInvisibleMode(boolean mode) {
		myInvisibleMode = mode;
		System.out.println("Invisible mode set to " + myInvisibleMode);
	}
	
	public void toBottom(boolean forced){
		if (myAtBottom || forced)
			scrollToBottom();
	}
}
