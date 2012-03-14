package com.scrollwin.client;

import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

public class WaitWindow extends Window {

	VLayout layout = new VLayout();
	Img waitImg = new Img("wait2.gif");
	
	public WaitWindow() {
		
		centerInPage();
		setWidth(300);
		layout.setWidth100();
		layout.addMember(waitImg);
	    
	    addItem(layout);
	}

}
