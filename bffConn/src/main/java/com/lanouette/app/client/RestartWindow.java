package com.lanouette.app.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;

public class RestartWindow extends Window {
	
	
	
	public RestartWindow() {
		
		setWidth(360);  
		//setHeight(115);
		setAutoSize(true);
		setTitle("Conflit de versions");  
		setShowMinimizeButton(false);  
		setIsModal(true);  
		setShowModalMask(true);  
		centerInPage();  
		setAlign(Alignment.CENTER);
		
		IButton closeButton = new IButton("Fermer");
		closeButton.setAlign(Alignment.CENTER);
		
		Label textLabel = new Label("La version de l'application que vous utilisez est antérieure à celle du serveur. " +
									"La nouvelle version sera chargée automatiquement lorsque vous fermerez cette fenêtre.");
		textLabel.setWidth100();
		textLabel.setPadding(6);
		addItem(textLabel);
		addItem(closeButton);
//		VStack vStack = new VStack();
//		vStack.setWidth100();
//		vStack.setAlign(Alignment.CENTER);
//		vStack.addMember(textLabel);
//		vStack.addMember(closeButton);
//		addItem(vStack);
		
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
			
		});

		addCloseClickHandler(new CloseClickHandler() {  
			public void onCloseClick(CloseClickEvent event) {  
				destroy();  
			}  
		});  
	}

}
