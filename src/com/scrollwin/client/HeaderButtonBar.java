package com.scrollwin.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

public class HeaderButtonBar extends HStack{
	private userCallbackInterface myUserCallbackInterface;
	private ImgButton myLogoutButton = new ImgButton();
	private ImgButton myLocalButton = new ImgButton();
	private ImgButton myStatsButton = new ImgButton();
	private ImgButton myOctopusButton = new ImgButton();
	private ImgButton myInfoButton = new ImgButton();
	private ImgButton myHideButton = new ImgButton();
	private ImgButton myShowButton = new ImgButton();
	private DynamicForm form = new DynamicForm();
	private TextItem localItem = new TextItem();
	private Img octopus = new Img("octopus.gif");
	private boolean allowCompactMode = true;
	
	public boolean isAllowCompactMode() {
		return allowCompactMode;
	}

	public void setAllowCompactMode(boolean allowCompactMode) {
		this.allowCompactMode = allowCompactMode;
	}

	public HeaderButtonBar(userCallbackInterface callbackInterface){
		myUserCallbackInterface = callbackInterface;
		
		setWidth(200);
        setDefaultLayoutAlign(Alignment.CENTER);
        setDefaultLayoutAlign(VerticalAlignment.BOTTOM);
        setHeight(26);
        
        //octopus.setWidth(120);
        //octopus.setHeight(76);
        //octopus.hide();
        
		myLogoutButton.setSize(32);  
	    myLogoutButton.setShowRollOver(true);
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
	    
	    myStatsButton.setSize(32);  
	    myStatsButton.setShowRollOver(true);
	    myStatsButton.setShowHover(true);
	    myStatsButton.setShowDown(false);
	    myStatsButton.setSrc("stats.png");
	    myStatsButton.setPrompt("Statistiques<br>(Ctrl-S)");
	    myStatsButton.setHoverStyle("tooltipStyle");
	    myStatsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.statsClicked();
			}
		});
	    
	    myInfoButton.setSize(32);  
	    myInfoButton.setShowRollOver(true);
	    myInfoButton.setShowHover(true);
	    myInfoButton.setShowDown(false);
	    myInfoButton.setSrc("info.png");
	    myInfoButton.setPrompt("Info (Ctrl-I)<br>Version " + VersionInfo.CURRENT_MAJOR + "." + VersionInfo.CURRENT_VERSION);
	    myInfoButton.setHoverStyle("tooltipStyle");
	    myInfoButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.infoClicked();
			}
		});
	    
	    myOctopusButton.setSize(32);  
	    myOctopusButton.setShowRollOver(true);
	    myOctopusButton.setShowHover(true);
	    myOctopusButton.setShowDown(false);
	    myOctopusButton.setSrc("octopus.png");
	    myOctopusButton.setPrompt("Mode octo<br>(Ctrl-O)");
	    myOctopusButton.setHoverStyle("tooltipStyle");
	    myOctopusButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.octopusClicked();
			}
		});
	    
	    myHideButton.setSize(32);  
	    myHideButton.setShowRollOver(true);
	    myHideButton.setShowHover(true);
	    myHideButton.setShowDown(false);
	    myHideButton.setSrc("back2r.png");
	    myHideButton.setPrompt("Mode compact<br>(Ctrl-1)");
	    myHideButton.setHoverStyle("tooltipStyle");
	    myHideButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.hideBarClicked();
				setCompactView();
			}
		});
	    
	    myShowButton.setSize(32);  
	    myShowButton.setShowRollOver(true);
	    myShowButton.setShowHover(true);
	    myShowButton.setShowDown(false);
	    myShowButton.setSrc("fwd2r.png");
	    myShowButton.setPrompt("Mode normal<br>(Ctrl-2)");
	    myShowButton.setHoverStyle("tooltipStyle");
	    myShowButton.hide();
	    myShowButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.showBarClicked();
				setNormalView();
			}
		});
	    
	    myLocalButton.setSize(32);  
	    myLocalButton.setShowRollOver(true);
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
	    
	    LayoutSpacer spacer = new LayoutSpacer();
	    spacer.setWidth(50);
	    
	    addMember(myHideButton);
        addMember(myShowButton);
	    addMember(myInfoButton);
	    addMember(myLocalButton);
        addMember(form);
        addMember(myStatsButton);
        addMember(myOctopusButton);
        addMember(myLogoutButton);
        addMember(spacer);
        //addMember(octopus);
	}
	
	public void setLocal(String local){
		localItem.setValue(local);
	}
	
	public void showLocationEntry() {
		form.show();
	}
	
	public void showOctopus() {
		octopus.show();
	}
	
	public void hideOctopus() {
		octopus.hide();
	}
	
	public void setCompactView () {
		if(allowCompactMode) {
			myShowButton.show();
			myHideButton.hide();
		}
	}
	
	public void setNormalView () {
		myShowButton.hide();
		myHideButton.show();
	}
}
