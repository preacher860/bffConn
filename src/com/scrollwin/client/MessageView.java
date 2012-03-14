package com.scrollwin.client;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.ScrolledEvent;
import com.smartgwt.client.widgets.events.ScrolledHandler;
import com.smartgwt.client.widgets.layout.VStack;

public class MessageView extends VStack {
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
	
	public MessageView(userCallbackInterface callbackInterface){
		//myMessageVStack = messageStack;
		myCallbackInterface = callbackInterface;
		setShowEdges(true);  
        //setMargin(5);
        setWidth(800);  
        setHeight("80%");
        setCanDragResize(true);
        setOverflow(Overflow.AUTO);
        setLeaveScrollbarGap(true);
        setMembersMargin(3);  
        setLayoutMargin(4);
        setBackgroundColor("#ffffff");
        //setEdgeImage("borders/sharpframe_10.png");
        //setEdgeSize(6);
        setEdgeSize(3);
        setShowShadow(true);
		setShadowSoftness(3);
		setShadowOffset(4);
        
        // This scroll handler sets the flag used to determine if we're at bottom or not.
        // Only if were at bottom do we kick the autoscroll on new messages
        addScrolledHandler(new ScrolledHandler(){
			@Override
			public void onScrolled(ScrolledEvent event) {
				if(getScrollTop() == getScrollBottom())
					myAtBottom = true;
				else
					myAtBottom = false;
				
				if(getScrollTop() == 0)	{
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
		      }
		    };
		    
	    myPositionTimer = new Timer() {
			@Override
			public void run() {
				MessageViewElement element;
				if ( (element = locateElement(myLastKnownOldest)) != null)
					scrollTo(0, element.getTop());
			}
	    };
	}
	
	public void newMessages(ArrayList<MessageContainer> messages) {
		boolean listWasEmtpy;
		boolean messagesNotOwn = false;
		boolean myNewestUpdated = false;
		
		if(myNumOfMessagesDisplayed > 0){
			myNewestDisplayedSeq = ((MessageViewElement)getMember(myNumOfMessagesDisplayed - 1)).getMessage().getMessageSeqId();
			myOldestDisplayedSeq = ((MessageViewElement)getMember(0)).getMessage().getMessageSeqId();
			listWasEmtpy = false;
		} else
			listWasEmtpy = true;
	
		for(int msgIndex = 0; msgIndex < messages.size(); msgIndex++) {

			MessageContainer currentMessage = messages.get(msgIndex);
			checkOcto(currentMessage);
			if(currentMessage.getMessageUserId() != RuntimeData.getInstance().getUserId())
				messagesNotOwn = true;

			MessageViewElement element = new MessageViewElement(currentMessage, 
					UserManager.getInstance().getUser(currentMessage.getMessageUserId()),
					UserManager.getInstance().getUser(RuntimeData.getInstance().getUserId()),
					myCallbackInterface);

			// Locate the position we want to insert this new element at (will refine this later)
			//System.out.println(" Newest: " + myNewestDisplayedSeq + " oldest: " + myOldestDisplayedSeq);
			if(currentMessage.getMessageSeqId() > myNewestDisplayedSeq){
				//System.out.println(" inserting " + currentMessage.getMessageSeqId() + " at bottom");
				addMember(element);
				myNewestDisplayedSeq = currentMessage.getMessageSeqId();
				myNumOfMessagesDisplayed++;
				myNewestUpdated = true;
			}
			else if (currentMessage.getMessageSeqId() < myOldestDisplayedSeq) {
				//System.out.println(" inserting " + currentMessage.getMessageSeqId() + " at top");
				addMember(element, 0);
				myOldestDisplayedSeq = currentMessage.getMessageSeqId();
				myNumOfMessagesDisplayed++;
			}
			else {  //somewhere in-between.  Find appropriate insertion point
				for(int elementIndex = myNumOfMessagesDisplayed - 1; elementIndex > 0; elementIndex--) {
					int currentSeq = ((MessageViewElement)getMember(elementIndex)).getMessage().getMessageSeqId();
					int previousSeq = ((MessageViewElement)getMember(elementIndex - 1)).getMessage().getMessageSeqId();
					//System.out.println("current: " + currentSeq + " previous: " + previousSeq);

					if (currentSeq == currentMessage.getMessageSeqId()){
						((MessageViewElement)getMember(elementIndex)).updateMessage(currentMessage);
						break;
					}
					if((currentMessage.getMessageSeqId() < currentSeq) && (currentMessage.getMessageSeqId() > previousSeq)){
						//System.out.println(" inserting " + currentMessage.getMessageSeqId() + " before " + currentSeq);
						addMember(element, elementIndex);
						myNumOfMessagesDisplayed++;
						break;
					}
				}
			}
			//System.out.println("  In queue: " + messageList.size());
			if(currentMessage.getMessageDbVersion() > myNewestDisplayedDb)
				myNewestDisplayedDb = currentMessage.getMessageDbVersion();
		}
		myNewestDisplayedSeq = ((MessageViewElement)getMember(myNumOfMessagesDisplayed - 1)).getMessage().getMessageSeqId();
		myOldestDisplayedSeq = ((MessageViewElement)getMember(0)).getMessage().getMessageSeqId();
		
		RuntimeData.getInstance().setNewestSeqId(myNewestDisplayedSeq);
		RuntimeData.getInstance().setDbVersion(myNewestDisplayedDb);
		
		if(myAtBottom)
			myScrollTimer.schedule(200);
		
		if(myFetchingOld){
			myFetchingOld = false;
			myPositionTimer.schedule(200);
		}
		
		if (myNewestUpdated && !listWasEmtpy && messagesNotOwn){
			myCallbackInterface.newestUpdated();
		}
			
		
		myCallbackInterface.messageDisplayComplete();
	}
	
	public MessageViewElement locateElement(int seqId)
	{
		for(int elementIndex = myNumOfMessagesDisplayed - 1; elementIndex > 0; elementIndex--)
			if (((MessageViewElement)getMember(elementIndex)).getMessage().getMessageSeqId() == seqId)
				return ((MessageViewElement)getMember(elementIndex));
		return null;
	}
	
	public void checkOcto(MessageContainer message)
	{
		if(message.getMessage().contains("octo!!!"))
	    	myCallbackInterface.octopusOnTyped();
    	if(message.getMessage().contains("!!!octo"))
	    	myCallbackInterface.octopusOffTyped();
	}
}
