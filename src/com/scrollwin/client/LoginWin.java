package com.scrollwin.client;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.allen_sauer.gwt.log.client.Log;

public class LoginWin extends Window {
	
	private final DynamicForm form = new DynamicForm();
	private TextItem usernameItem = new TextItem();
	private PasswordItem passwordItem = new PasswordItem();
	private TextItem localItem = new TextItem();
	private ioCallbackInterface myCallbackInterface;
	private CheckboxItem rememberItem = new CheckboxItem("rememberItem");
	
	public LoginWin(ioCallbackInterface theCallbackInterface) {
		boolean rememberValues = false;
		myCallbackInterface = theCallbackInterface;
		
		setTitle("Connexion");  
        setShowMinimizeButton(false);  
        setIsModal(true);  
        setShowModalMask(true);  
        //centerInPage();  
        setWidth(360);  
        setHeight(200);  
        
        moveTo(Page.getWidth() / 2 - getWidth()/2, Page.getHeight() / 2 - getHeight()/2);
        
		VLayout layout = new VLayout(20);
		layout.setAlign(Alignment.CENTER);
		layout.setWidth100();
		layout.setHeight100();
		layout.setDefaultLayoutAlign(Alignment.CENTER);
		
		IButton submitButton = new IButton("Soumettre");
		  
		//form.setHeight100();  
        form.setWidth100();  
        form.setPadding(5);  
        form.setLayoutAlign(VerticalAlignment.BOTTOM);
        form.setAutoFocus(true);
		  
		usernameItem.setTitle("Usager");  
		usernameItem.setRequired(true);  
		  
		passwordItem.setTitle("Mot de passe");  
		passwordItem.setRequired(true);
		
		localItem.setTitle("Localisation");
		localItem.setRequired(false);
		localItem.setLength(16);
		localItem.setSelectOnFocus(true);
		
		rememberItem.setTitle("Retenir nom et localisation");
		
		String remember = Cookies.getCookie("bffRememberNameLoc");
		if((remember != null) && (remember.contentEquals("true")))
		{
			String local = Cookies.getCookie("bffLastLocation");
			if(local != null)
				localItem.setValue(local);
			
			String name = Cookies.getCookie("bffLastName");
			if(name != null)
				usernameItem.setValue(name);
			
			rememberItem.setValue(true);
		} else
			rememberItem.setValue(false);
		
		passwordItem.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getKeyName().compareTo("Enter") == 0){
					event.cancel();
					loginSubmit(rememberItem.getValueAsBoolean());
				}
			}
		});
		
		localItem.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getKeyName().compareTo("Enter") == 0){
					event.cancel();
					loginSubmit(rememberItem.getValueAsBoolean());
				}
			}
		});

		submitButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				loginSubmit(rememberItem.getValueAsBoolean());
			}});
		
		form.setFields(usernameItem, passwordItem, localItem, rememberItem);  

		layout.addMember(form);
		layout.addMember(submitButton);
		addItem(layout);
	}
	
	private void loginSubmit(boolean rememberValues)
	{
		// If either username or password empty, don't go any further
		if((usernameItem.getValueAsString() == null) || (passwordItem.getValueAsString() == null))
			return;
		String local = localItem.getValueAsString();
		if(local == null)
			local = "";
		
		if (rememberValues) {
			long cookieLifespan = 1000 * 60 * 60 * 24 * 365; // one year
		    Date expires = new Date(System.currentTimeMillis() + cookieLifespan);
		    
		    Cookies.setCookie("bffRememberNameLoc", "true", expires, null, "/", false);
		    
			String username = usernameItem.getValueAsString();
			Cookies.setCookie("bffLastName", username, expires, null, "/", false);
			Cookies.setCookie("bffLastLocation", local, expires, null, "/", false);
		} else {
			Cookies.removeCookie("bffRememberNameLoc", "/");
			Cookies.removeCookie("bffLastName", "/");
			Cookies.removeCookie("bffLastLocation", "/");
		}
		
		myCallbackInterface.performLoginCallback(usernameItem.getValueAsString(), 
												 passwordItem.getValueAsString(),
												 local);
		destroy();
	}
	
	public void setLocal(String local) {
		
	}
	
	private String removeCrLf(String str) {
	    str = str.replace("\n", "");
	    str = str.replace("\r", "");
	    return str;
	}
}
