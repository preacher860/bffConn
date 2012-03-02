package com.scrollwin.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.layout.HStack;

public class HeaderButtonBar extends HStack{
	private userCallbackInterface myUserCallbackInterface;
	private ImgButton myLogoutButton = new ImgButton();
	private ImgButton myLocalButton = new ImgButton();
	private ImgButton myInfoButton = new ImgButton();
	private DynamicForm form = new DynamicForm();
	private TextItem localItem = new TextItem();
	
	public HeaderButtonBar(userCallbackInterface callbackInterface){
		myUserCallbackInterface = callbackInterface;
		
		setWidth100();
        setDefaultLayoutAlign(Alignment.CENTER);
        setDefaultLayoutAlign(VerticalAlignment.BOTTOM);
        setHeight(76);
        
		myLogoutButton.setSize(32);  
	    myLogoutButton.setShowRollOver(false);
	    myLogoutButton.setShowHover(true);
	    myLogoutButton.setShowDown(false);
	    myLogoutButton.setSrc("logout.png");
	    myLogoutButton.setPrompt("Déconnexion<br>(Ctrl-D)");
	    myLogoutButton.setHoverStyle("tooltipStyle");
	    myLogoutButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.logoutClicked();
			}
		});
	    
	    myInfoButton.setSize(32);  
	    myInfoButton.setShowRollOver(false);
	    myInfoButton.setShowHover(true);
	    myInfoButton.setShowDown(false);
	    myInfoButton.setSrc("info.png");
	    myInfoButton.setPrompt("Info");
	    myInfoButton.setHoverStyle("tooltipStyle");
	    myInfoButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.infoClicked();
			}
		});
	    
	    myLocalButton.setSize(32);  
	    myLocalButton.setShowRollOver(false);
	    myLocalButton.setShowHover(true);
	    myLocalButton.setShowDown(false);
	    myLocalButton.setSrc("local.png");
	    myLocalButton.setPrompt("Changer localisation<br>(Ctrl-L)");
	    myLocalButton.setHoverStyle("tooltipStyle");
	    myLocalButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				form.show();
			}
		});

	    localItem.setShowTitle(false);
	    localItem.setLength(30);
	    localItem.setSelectOnFocus(true);
	    localItem.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getKeyName().compareTo("Enter") == 0){
					event.cancel();
					String local = localItem.getValueAsString();
					if (local == null) local = "";
					form.hide();
					myUserCallbackInterface.localEntered(local);
				} else if (event.getKeyName().compareTo("Escape") == 0){
					event.cancel();
					form.hide();
					myUserCallbackInterface.localEntered(null);
				}
			}
		});
	    form.setHeight(30);
	    form.setAutoFocus(true);
	    form.setFields(localItem);
	    form.hide();
	    
	    //addMember(myInfoButton);
	    addMember(myLocalButton);
        addMember(form);
        addMember(myLogoutButton);
	}
	
	public void setLocal(String local){
		localItem.setValue(local);
	}
	
	public void showLocationEntry() {
		form.show();
	}
	
}
