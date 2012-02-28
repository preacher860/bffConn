package com.scrollwin.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

public class EntryBox extends HLayout {

	private TextAreaItem messageItem = new TextAreaItem();
	private HStack imageStack = new HStack();
	private DynamicForm form = new DynamicForm(); 
    private ioCallbackInterface myCallbackInterface;
    private boolean myIsShiftDown = false;
    
    
    public EntryBox(ioCallbackInterface callbackInterface) {
    	myCallbackInterface = callbackInterface;
    	
    	setShowEdges(true);
    	setMargin(5);
    	setWidth(800);
    	setHeight(75);
    	
    	imageStack.setWidth(40);
    	imageStack.setAlign(Alignment.CENTER);
    	imageStack.setPadding(8);
    	imageStack.setBackgroundColor("#E0E0E0");
    	
    	messageItem.setShowTitle(false);  
        messageItem.setLength(1000);  
        messageItem.setWidth("*");
        messageItem.setHeight("*");

        form.setNumCols(1);
        form.setCanDragResize(true);
        form.setWidth100();
        form.setHeight100();
        form.setFields(messageItem);
        form.setStyleName("blueYellow");
        
        messageItem.addKeyUpHandler(new KeyUpHandler() {
        	@Override
        	public void onKeyUp(KeyUpEvent event) {
        		if(event.getKeyName().compareTo("Shift") == 0)
					myIsShiftDown= false;
        		
	        	if (event.getKeyName().compareTo("Enter") == 0){
	        		if(!myIsShiftDown){
						event.cancel();
						myCallbackInterface.messageToSendCallback(filterMessage(messageItem.getValueAsString()));
						messageItem.clearValue();
	        		}
				}
        	}
        });
        
        messageItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().compareTo("Shift") == 0)
					myIsShiftDown = true;
			}
          });
        
		addMember(imageStack);
		addMember(form);
    }
    
    public void setUser(UserContainer user)
    {
    	Img userImage = new Img(user.getAvatarURL(), 36, 36);
    	userImage.setBorder("2px groove #808080");
    	imageStack.addMember(userImage, 0);
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
    }

    public String filterMessage(String Message)
    {
    	String outputMessage = "";
    	
    	// Split the message in tokens (separator is space) an try to locate URLs
    	String [] parts = Message.split("\\s");
    	
    	// Check if the message is targeted at someone
    	if(parts[0].startsWith("@"))
    		parts[0] = "<b>" + parts[0] + " ></b>";
    	
    	// Look for URLs and encapsulate them to img or href
    	for(String item:parts)
    	{
    		if ((item.startsWith("http://")) || (item.startsWith("https://")) ){
    			if( (item.endsWith(".jpg")) || (item.endsWith(".gif")) || (item.endsWith(".png")) )
    				item = "<img src=\"" + item + "\" height=200/>";
    			else
    				item = "<a href=\"" + item + "\">lien</a>";
    		}
    		outputMessage += item + " ";
    	}
    	return escapeJson(outputMessage);
    }
    
    public String escapeJson(String str) {
	    str = str.replace("\\", "\\\\");
	    str = str.replace("\"", "\\\"");
	    str = str.replace("/", "\\/");
	    str = str.replace("\b", "\\b");
	    str = str.replace("\f", "\\f");
	    str = str.replace("\n", "<br>"); 
	    str = str.replace("\r", "<br>");
	    str = str.replace("\t", "\\t");
	    return str;
	}
}
