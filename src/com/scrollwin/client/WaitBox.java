package com.scrollwin.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VStack;

public class WaitBox extends VStack{

	private Label messageLabel = new Label();
	
	 public WaitBox() {
		 setWidth(216);
		 setHeight(80);
		 setMargin(5);
		 setShowEdges(true);  
		 setEdgeImage("borders/sharpframe_10.png");
	     setEdgeSize(6);
	     
	     messageLabel.setHeight100();
	     messageLabel.setWidth100();
	     messageLabel.setAlign(Alignment.CENTER);
	     addMember(messageLabel);
	     hide();
	 }
	 
	 public void setMessage(String message)
	 {
		 messageLabel.setContents(message);
		 
	 }
}
