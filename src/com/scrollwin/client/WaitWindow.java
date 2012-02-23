package com.scrollwin.client;

import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

public class WaitWindow extends Window{

	final Progressbar hBar1 = new Progressbar();  
	VLayout layout = new VLayout();
	
	public WaitWindow() {
		
		centerInPage();
		setWidth(300);
		layout.setWidth100();
		hBar1.setHeight(24);  
	    hBar1.setVertical(false);  
	    layout.addMember(hBar1);
	    
	    addItem(layout);
	}

	public void setBarPos(int pos)
	{
		hBar1.setPercentDone(pos);
		hBar1.redraw();
	}
}
