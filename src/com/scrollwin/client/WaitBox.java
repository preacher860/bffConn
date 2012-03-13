package com.scrollwin.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.BkgndRepeat;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VStack;

public class WaitBox extends VStack{

	private Label messageLabel = new Label();
	private Label titleLabel = new Label();
	
	 public WaitBox() {
		 setWidth(216);
		 setHeight(80);
		 setOverflow(Overflow.VISIBLE);
		 //setMargin(5);
		 setBackgroundColor("#FFFFFF");
		 setShowEdges(true);  
		 //setEdgeImage("borders/sharpframe_10.png");
	     //setEdgeSize(6);
		 setEdgeSize(3);
	     setShowShadow(true);
		 setShadowSoftness(3);
		 setShadowOffset(4);
	     
	     messageLabel.setHeight(10);
	     messageLabel.setWidth100();
	     messageLabel.setAlign(Alignment.CENTER);
	     
	     titleLabel.setWidth100();
	     titleLabel.setHeight(30);
	     titleLabel.setContents("<b>Info<b>");
	     titleLabel.setPadding(5);
	     titleLabel.setStyleName("myTitleBox");
	     titleLabel.setBackgroundImage("titleback30.png");
	     titleLabel.setBackgroundRepeat(BkgndRepeat.REPEAT_X);
	
	     addMember(titleLabel);
	     addMember(messageLabel);
	     hide();
	 }
	 
	 public void setMessage(String message)
	 {
		 messageLabel.setContents(message);
		 
	 }
}
