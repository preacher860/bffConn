package com.scrollwin.client;

import com.google.gwt.user.client.ui.TextArea;
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

	//private final DynamicForm form = new DynamicForm();
	private TextAreaItem messageItem = new TextAreaItem();
	private TextAreaItem textEntry = new TextAreaItem();
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
						myCallbackInterface.messageToSendCallback(escapeJson(messageItem.getValueAsString()));
						messageItem.clearValue();
	        		}
				}
        	}
        });
        
        messageItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().compareTo("Shift") == 0)
					myIsShiftDown= true;
			}
          });
        
        //  KeyDown doesn't work anymore as of Chrome 16.0.9xx!! (works on 16.0.8xx and Firefox)
        //  Lucky us, KeyUp still does what it's supposed to
//        messageItem.addKeyDownHandler(new KeyDownHandler() {
//			@Override
//			public void onKeyDown(KeyDownEvent event) {
//				System.out.println("KeyDown: "+ event.getKeyName());
//				if (event.getKeyName().compareTo("Enter") == 0){
//					event.cancel();
//					myCallbackInterface.messageToSendCallback(escapeJson(messageItem.getValueAsString()));
//					messageItem.clearValue();
//				}
//			}
//          });
        
        
        
//    	textEntry.setWidth("400px");
//		textEntry.addKeyPressHandler(new KeyPressHandler() {
//
//            public void onKeyPress(KeyPressEvent event) {
//              if (event.getCharCode() == (char) 13){
//            	  myCallbackInterface.messageToSendCallback(escapeJson(textEntry.getText()));
//            	  event.preventDefault(); // So the "Enter" won't be sent to edit box
//            	  textEntry.setText("");
//              }
//            }
//          });
		addMember(imageStack);
		addMember(form);
    }
    
    public void setUser(UserContainer user)
    {
    	Img userImage = new Img(user.getAvatarURL(), 36, 36);
    	userImage.setBorder("2px groove #808080");
    	imageStack.addMember(userImage, 0);
    }
    
    public String escapeJson(String str) {
	    str = str.replace("\\", "\\\\");
	    str = str.replace("\"", "\\\"");
	    str = str.replace("/", "\\/");
	    str = str.replace("\b", "\\b");
	    str = str.replace("\f", "\\f");
	    //str = str.replace("\n", "\\n");
	    //str = str.replace("\r", "\\r");
	    str = str.replace("\n", "<br>"); 
	    str = str.replace("\r", "<br>");
	    str = str.replace("\t", "\\t");
	    return str;
	}
}
