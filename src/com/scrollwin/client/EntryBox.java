package com.scrollwin.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;

public class EntryBox extends HorizontalPanel {

	private TextAreaItem messageItem = new TextAreaItem();
	private HTML infoItem = new HTML();
	private VerticalPanel imageStack = new VerticalPanel();
	private VerticalPanel editStack = new VerticalPanel();
	private DynamicForm form = new DynamicForm(); 
    private ioCallbackInterface myCallbackInterface;
    private userCallbackInterface myUserCallbackInterface;
    private Timer focusTimer;
    private boolean myIsShiftDown = false;
    private boolean myIsEditing = false;
    private int myEditingMessageSeq = 0;
    
    
    public EntryBox(ioCallbackInterface callbackInterface, userCallbackInterface userCB) {
    	
    	myCallbackInterface = callbackInterface;
    	myUserCallbackInterface = userCB;
    	
    	setStyleName("entryBox");
        
    	imageStack.setStyleName("entryBoxPicBox");
    	
    	infoItem.setHTML("Editing message 222222");
    	infoItem.setStyleName("entryBoxInfoItem");
    	infoItem.setVisible(false);
    	messageItem.setShowTitle(false);  
        messageItem.setLength(4000);  
        messageItem.setWidth("*");
        messageItem.setHeight(45);
        
        form.setNumCols(1);
        form.setCanDragResize(true);
        form.setAutoFocus(true);
        form.setWidth(744);
        form.setHeight(50);
        form.setFields(messageItem);
        form.focusInItem(messageItem);
        form.setStyleName("blueYellow");

        ClickHandler entryBoxClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.userEntry();
			}
		};
		addDomHandler(entryBoxClickHandler, ClickEvent.getType());
		
        messageItem.addKeyUpHandler(new KeyUpHandler() {
        	@Override
        	public void onKeyUp(KeyUpEvent event) {
        		if(event.getKeyName().compareTo("Shift") == 0)
					myIsShiftDown= false;
        	}
        });
        
        messageItem.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getKeyName().compareTo("Shift") == 0)
					myIsShiftDown = true;
				myUserCallbackInterface.userEntry();
				
				if (event.getKeyName().compareTo("Enter") == 0){
	        		if(!myIsShiftDown){
						event.cancel();
						messageItem.setSelectionRange(messageItem.getLength(), messageItem.getLength()); // Cursor at end
						myCallbackInterface.messageToSendCallback(filterMessage(messageItem.getValueAsString()), myIsEditing, myEditingMessageSeq);
						myIsEditing = false;
						infoItem.setVisible(false);
						myEditingMessageSeq = 0;
						messageItem.clearValue();
	        		}
				}
	        	
	        	if (event.getKeyName().compareTo("Escape") == 0){
	        		if(myIsEditing){
	        			myIsEditing = false;
						infoItem.setVisible(false);
						myEditingMessageSeq = 0;
						messageItem.clearValue();
	        		}
	        	}
			}
          });
        
        focusTimer = new Timer() {
			@Override
			public void run() {
				form.focusInItem(messageItem);
				messageItem.setSelectionRange(messageItem.getLength(), messageItem.getLength());
			}
        };
        
        editStack.add(infoItem);
        editStack.add(form);
        
		add(imageStack);
		add(editStack);
    }
    
    

	public void setUser(UserContainer user)
    {
    	Image userImage = new Image(user.getAvatarURL());
    	userImage.setStyleName("userAvatar");
    	imageStack.add(userImage);
    }
    
    public void addAddressee(String userNick)
    {
    	String Message;
    	
    	// Add a space before only if we're not beginning the message
    	if(messageItem.getValueAsString() != null)
    		Message = messageItem.getValueAsString() + " @" + userNick + " ";
    	else 
    		Message = "@" + userNick + " ";
    	
    	messageItem.setValue(Message);
		setFocus();
    }

    public void setFocus()
    {
    	focusTimer.schedule(100);
    }
    
    public String filterMessage(String Message)
    {
    	String outputMessage = "";
    	
    	// remove trailing crlf "submit", it's not part of the message
    	if(Message.endsWith("\n"))
    		Message = Message.substring(0, Message.length() - 1);
    	
    	// Convert CrLf to HTML linefeeds before the split because \n\r are considered 
    	// as whitespace by the regex.
    	outputMessage = convertCrLf(Message);
    	return escapeJson(outputMessage);
    }
    
    public void editMessage(MessageContainer message)
    {
    	String decodedMessage = URL.decode(message.getMessage());
    	messageItem.setValue(deconvertCrLf(decodedMessage));
    	myIsEditing = true;
    	myEditingMessageSeq = message.getMessageSeqId();
    	infoItem.setHTML("Édition du message <b>" + myEditingMessageSeq + "</b>. ESC pour annuler");
    	infoItem.setVisible(true);
    	setFocus();
    }
    
    public String escapeJson(String str) {
	    str = str.replace("\\", "\\\\");
	    str = str.replace("\"", "\\\"");
	    str = str.replace("/", "\\/");
	    str = str.replace("\b", "\\b");
	    str = str.replace("\f", "\\f");
	    str = str.replace("\t", "\\t");
	    return str;
	}
    
    public String convertCrLf(String str) {
    	// Add space before so the <br> is processed as a separate token
    	str = str.replace("\n", " <br /> "); 
	    str = str.replace("\r", " <br /> ");
	    return str;
    }
    
    public String deconvertCrLf(String str) {
    	// Put back the \n where <br> where inserted
    	str = str.replace(" <br /> ", "\n"); 
    	str = str.replace("<br />", "\n");
	    return str;
    }
}
