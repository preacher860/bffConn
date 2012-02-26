package com.scrollwin.client;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;

public class LoginWin extends Window {
	
	private final DynamicForm form = new DynamicForm();
	private TextItem usernameItem = new TextItem();
	private PasswordItem passwordItem = new PasswordItem();
	private ioCallbackInterface myCallbackInterface;
	
	public LoginWin(ioCallbackInterface theCallbackInterface) {
		myCallbackInterface = theCallbackInterface;
		
		setTitle("Connexion");  
        setShowMinimizeButton(false);  
        setIsModal(true);  
        setShowModalMask(true);  
        //centerInPage();  
        setWidth(360);  
        setHeight(150);  
        
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
		
		passwordItem.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getKeyName().compareTo("Enter") == 0){
					event.cancel();
					loginSubmit();
				}
			}
			
		});
//		passwordItem.addKeyDownHandler(new KeyDownHandler() {
//			@Override
//			public void onKeyDown(KeyDownEvent event) {
//				System.out.println("Key:" + event.getKeyName());
//				test.setValue(event.getKeyName());
//				if (event.getKeyName().compareTo("Enter") == 0){
//					event.cancel();
//					loginSubmit();
//				}
//			}
//          });
		
		//passwordItem.addKeyPressHandler(new KeyPressHandler() {
			
         // });
		
		submitButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				loginSubmit();
			}});
		
		form.setFields(usernameItem, passwordItem);  

		layout.addMember(form);
		layout.addMember(submitButton);
		addItem(layout);
	}
	
	private void loginSubmit()
	{
		myCallbackInterface.performLoginCallback(usernameItem.getValueAsString(), passwordItem.getValueAsString());
		destroy();
	}
	
	private String removeCrLf(String str) {
	    str = str.replace("\n", "");
	    str = str.replace("\r", "");
	    return str;
	}
}
