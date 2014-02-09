package com.lanouette.app.client;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;

public class OctopusWin extends Window {
	
	public OctopusWin() {
		Img octopus = new Img("octopus.gif");
		Timer closeTimer = new Timer(){

			@Override
			public void run() {
				destroy();
			}
		};
		
		octopus.setWidth(339);
		octopus.setHeight(226);
		
		setTop(250);
		setLeft(250);
		setShowMinimizeButton(false);
		setShowCloseButton(false);
		setAutoSize(true);
		setTitle(Canvas.imgHTML("octopus_s.png")+ "Pour toi Anne...");
		
		addItem(octopus);
		closeTimer.schedule(15*1000);
		
		show();
	}
}
