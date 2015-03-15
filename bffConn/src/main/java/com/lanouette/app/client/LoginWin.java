package com.lanouette.app.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginWin extends VerticalPanel {

    private Label topLabel = new Label("Ouverture de session");

    private HorizontalPanel userNamePanel = new HorizontalPanel();
    private TextBox userNameItem = new TextBox();
    private Label userNameLabel = new Label("Usager:");

    private HorizontalPanel passwordPanel = new HorizontalPanel();
    private PasswordTextBox passwordItem = new PasswordTextBox();
    private Label passwordLabel = new Label("Mot de passe:");

    private HorizontalPanel localPanel = new HorizontalPanel();
    private TextBox localItem = new TextBox();
    private Label localLabel = new Label("Localisation:");

    private CheckBox storeLocal = new CheckBox("Retenir nom et localisation");

    private Button submitButton = new Button("Soumettre");

	private ioCallbackInterface myCallbackInterface;

	public LoginWin(ioCallbackInterface theCallbackInterface) {
		myCallbackInterface = theCallbackInterface;

        setStyleName("loginBox");

        topLabel.addStyleName("loginHeader");
        userNameItem.addStyleName("loginEntryBox");
        userNameLabel.addStyleName("loginLabel");
        passwordItem.addStyleName("loginEntryBox");
        passwordLabel.addStyleName("loginLabel");
        localItem.addStyleName("loginEntryBox");
        localLabel.addStyleName("loginLabel");
        storeLocal.addStyleName("loginCheckbox");
        submitButton.addStyleName("loginButton");

		String remember = Cookies.getCookie("bffRememberNameLoc");
		if((remember != null) && (remember.contentEquals("true")))
		{
			String local = Cookies.getCookie("bffLastLocation");
			if(local != null)
				localItem.setText(local);

			String name = Cookies.getCookie("bffLastName");
			if(name != null)
				userNameItem.setText(name);

			storeLocal.setValue(true);
		} else
			storeLocal.setValue(false);

        passwordItem.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if (keyUpEvent.getNativeEvent().getKeyCode() == 13){
					keyUpEvent.stopPropagation();
					loginSubmit(storeLocal.getValue());
				}
            }
        });

        localItem.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if (keyUpEvent.getNativeEvent().getKeyCode() == 13){
                    keyUpEvent.stopPropagation();
                    loginSubmit(storeLocal.getValue());
                }
            }
        });

		submitButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				loginSubmit(storeLocal.getValue());
			}});

        userNamePanel.add(userNameLabel);
        userNamePanel.add(userNameItem);
        userNamePanel.setCellVerticalAlignment(userNameLabel, HasVerticalAlignment.ALIGN_MIDDLE);

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordItem);
        passwordPanel.setCellVerticalAlignment(passwordLabel, HasVerticalAlignment.ALIGN_MIDDLE);

        localPanel.add(localLabel);
        localPanel.add(localItem);
        localPanel.setCellVerticalAlignment(localLabel, HasVerticalAlignment.ALIGN_MIDDLE);

        add(topLabel);
        add(userNamePanel);
        add(passwordPanel);
        add(localPanel);
        add(storeLocal);
        add(submitButton);

        setCellHorizontalAlignment(storeLocal, HasHorizontalAlignment.ALIGN_CENTER);
        setCellHorizontalAlignment(submitButton, HasHorizontalAlignment.ALIGN_CENTER);

        RootPanel.get().add(this);
        RootPanel.get().setStyleName("mainPanel");
	}
	
	private void loginSubmit(boolean rememberValues)
	{
		// If either username or password empty, don't go any further
		if((userNameItem.getText() == null) || (passwordItem.getText() == null))
			return;
		String localisation = localItem.getText();
		if(localisation == null)
			localisation = "";
		
		if (rememberValues) {
			long cookieLifespan = 1000L * 60L * 60L * 24L * 365L; // one year
		    Date expires = new Date(System.currentTimeMillis() + cookieLifespan);
		    
		    Cookies.setCookie("bffRememberNameLoc", "true", expires, null, "/", false);
		    
			String username = userNameItem.getText();
			Cookies.setCookie("bffLastName", username, expires, null, "/", false);
			Cookies.setCookie("bffLastLocation", localisation, expires, null, "/", false);
		} else {
			Cookies.removeCookie("bffRememberNameLoc", "/");
			Cookies.removeCookie("bffLastName", "/");
			Cookies.removeCookie("bffLastLocation", "/");
		}
		
		myCallbackInterface.performLoginCallback(userNameItem.getText(),
												 passwordItem.getText(),
												 localisation);
		RootPanel.get().remove(this);
	}
	
	public void setLocal(String local) {
		
	}
}
