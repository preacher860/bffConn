package com.scrollwin.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageViewElementNative extends HorizontalPanel{

	private HorizontalPanel infoPane = new HorizontalPanel();
	private HorizontalPanel iconPane = new HorizontalPanel();
	private VerticalPanel	messagePane = new VerticalPanel();
	private VerticalPanel   imageStack = new VerticalPanel();
	private HTML 			userInfoLabel = new HTML();
	private HorizontalPanel starStack = new HorizontalPanel();
	private HorizontalPanel deleteStack = new HorizontalPanel();
	private HorizontalPanel editStack = new HorizontalPanel();

	private HTML userMessagePane = new HTML();
	private Image starIcon = new Image("images/stargray.png");
	private Image starOverIcon = new Image("images/stargray_Over.png");
	private Image deleteIcon = new Image("images/deletegray.png");
	private Image deleteOverIcon = new Image("images/deletegray_Over.png");
	private Image editIcon = new Image("images/editgray.png");
	private Image editOverIcon = new Image("images/edit.png");
	private Label starLabel = new Label("Étoiler");
	private Label deleteLabel = new Label("Effacer");
	private Label editLabel = new Label("Éditer");
	private Image userImage;

	private boolean starred = false;
	private boolean myUnread = false;
	private boolean forMe = false;
	private userCallbackInterface myUserCallbackInterface;
	private String myMessageOriginatingUser;
	private MessageContainer myMessage = null;
	private UserContainer myUser = null;
	
	public MessageViewElementNative(MessageContainer message, UserContainer user, UserContainer myself, userCallbackInterface cb)
	{
		myUserCallbackInterface = cb;
		myMessageOriginatingUser = user.getNick();
		myMessage = message;
		myUser = myself;
		
		// If message hidden (deleted), no need to perform all the stuff, return immediately
		if (myMessage.isMessageDeleted()){
			setVisible(false);
			return;
		}
		
		setupStarred(myMessage);
		forMe = isMessageForLoggedUser(message, myself);
		
		setStyleName("messageViewElement");

		Integer kittenSelect = 48 + message.getMessageUserId();
		if(user.getAvatarURL().isEmpty())
			userImage = new Image("http://placekitten.com/" + kittenSelect + "/" + kittenSelect);
		else
			userImage = new Image(user.getAvatarURL());
		
		userImage.setStyleName("userAvatar");
		userImage.setTitle(myMessageOriginatingUser);
		imageStack.setStyleName("messageViewPicBox");
		imageStack.add(userImage);

		starIcon.setStyleName("starIcon");
		starOverIcon.setStyleName("starIcon");
		starLabel.setStyleName("starLabel");
		
		starStack.setStyleName("starStack");
		starStack.add(starIcon);
		starStack.add(starOverIcon);
		starStack.add(starLabel);
		if(!starred){
			starStack.setVisible(false);
			starOverIcon.setVisible(false);
		} else {
			starOverIcon.setVisible(true);
			starIcon.setVisible(false);
		}

		deleteIcon.setStyleName("deleteIcon");
		deleteOverIcon.setStyleName("deleteIcon");
		deleteOverIcon.setVisible(false);
		deleteLabel.setStyleName("deleteLabel");
		deleteStack.setStyleName("deleteStack");
		deleteStack.setVisible(false);
		deleteStack.add(deleteIcon);
		deleteStack.add(deleteOverIcon);
		deleteStack.add(deleteLabel);

		editIcon.setStyleName("editIcon");
		editOverIcon.setStyleName("editIcon");
		editOverIcon.setVisible(false);
		editLabel.setStyleName("editLabel");
		editStack.setStyleName("editStack");
		editStack.setVisible(false);
		editStack.add(editIcon);
		editStack.add(editOverIcon);
		editStack.add(editLabel);

		iconPane.add(starStack);
		iconPane.add(deleteStack);
		iconPane.add(editStack);

		String infoLabelContents = "Message " + message.getMessageSeqId() + "   envoyé par " + 
					user.getNick() + "  le " + message.getMessageDate() + 
					" à " + message.getMessageTime();
		if(message.getMessageLocal().contentEquals("") != true)
			infoLabelContents += " - " + message.getMessageLocal();
		userInfoLabel.setHTML(infoLabelContents);

		userMessagePane.setHTML(message.getMessage());
		
		infoPane.add(userInfoLabel);
		infoPane.add(iconPane);

		setStyleName("messageViewElement");
		userMessagePane.setStyleName("messageViewElementBoxNormal");
		infoPane.setStyleName("messageStatusBar");
		userInfoLabel.setStyleName("userInfo");
		iconPane.setStyleName("iconPane");

		messagePane.setStyleName("messagePane");
		messagePane.add(userMessagePane);
		messagePane.add(infoPane);

		add(imageStack);
		setCellWidth(imageStack, "50px");
		add(messagePane);

		setUserPaneColor();
		
		ClickHandler avatarClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.avatarClicked(myMessageOriginatingUser);
			}
		};
		imageStack.addDomHandler(avatarClickHandler, ClickEvent.getType());
		
		ClickHandler starClickHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.starClicked(myMessage.getMessageSeqId());
			}
		};
		starStack.addDomHandler(starClickHandler, ClickEvent.getType());

		MouseOverHandler starMouseOverHandler = new MouseOverHandler(){ 
			@Override
			public void onMouseOver(MouseOverEvent event) {
			if(!starred){
				starOverIcon.setVisible(true);
				starIcon.setVisible(false);
			}
		} };
		starStack.addDomHandler(starMouseOverHandler, MouseOverEvent.getType());
		
		MouseOutHandler starMouseOutHandler = new MouseOutHandler(){ 
			@Override
			public void onMouseOut(MouseOutEvent event) {
			if(!starred){
				starOverIcon.setVisible(false);
				starIcon.setVisible(true);
			}
		} };
		starStack.addDomHandler(starMouseOutHandler, MouseOutEvent.getType());
		
		// May only delete/edit own messages
		if(myMessage.getMessageUserId() == RuntimeData.getInstance().getUserId()) {
			
			ClickHandler editClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					myUserCallbackInterface.editMessageClicked(myMessage);
				}
			};
			
			editStack.addDomHandler(editClickHandler, ClickEvent.getType());
			
			MouseOverHandler editMouseOverHandler = new MouseOverHandler(){ 
				@Override
				public void onMouseOver(MouseOverEvent event) {
					editOverIcon.setVisible(true);
					editIcon.setVisible(false);
				}
			};
			editStack.addDomHandler(editMouseOverHandler, MouseOverEvent.getType());
			
			MouseOutHandler editMouseOutHandler = new MouseOutHandler(){ 
				@Override
				public void onMouseOut(MouseOutEvent event) {
					editOverIcon.setVisible(false);
					editIcon.setVisible(true);
			} };
			
			editStack.addDomHandler(editMouseOutHandler, MouseOutEvent.getType());
			
			ClickHandler deleteClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					myUserCallbackInterface.deleteClicked(myMessage.getMessageSeqId());
				}
			};
			
			deleteStack.addDomHandler(deleteClickHandler, ClickEvent.getType());
			
			MouseOverHandler deleteMouseOverHandler = new MouseOverHandler(){ 
				@Override
				public void onMouseOver(MouseOverEvent event) {
					deleteOverIcon.setVisible(true);
					deleteIcon.setVisible(false);
				}
			};
			deleteStack.addDomHandler(deleteMouseOverHandler, MouseOverEvent.getType());
			
			MouseOutHandler deleteMouseOutHandler = new MouseOutHandler(){ 
				@Override
				public void onMouseOut(MouseOutEvent event) {
					deleteOverIcon.setVisible(false);
					deleteIcon.setVisible(true);
			} };
			
			deleteStack.addDomHandler(deleteMouseOutHandler, MouseOutEvent.getType());
		}

		MouseOverHandler messageMouseOverHandler = new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				starStack.setVisible(true);
				deleteStack.setVisible(true);
				editStack.setVisible(true);
			}
		};

		addDomHandler(messageMouseOverHandler, MouseOverEvent.getType());
		
		MouseOutHandler messageMouseOutHandler = new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if(!starred)
					starStack.setVisible(false);
				deleteStack.setVisible(false);
				editStack.setVisible(false);
			}
		};
		
		addDomHandler(messageMouseOutHandler, MouseOutEvent.getType());
	}
	
	private void setUserPaneColor() {
		
		if(forMe && myUnread)
			userMessagePane.setStyleName("messageViewElementBoxAdresseeUnread"); // Addressee + unread purple
		else if(forMe) 
        	userMessagePane.setStyleName("messageViewElementBoxAdressee"); // Addressee green
        else if(myUnread)
        	userMessagePane.setStyleName("messageViewElementBoxUnread"); // Unread orange
        else
        	userMessagePane.setStyleName("messageViewElementBoxNormal"); // Normal blue
	}
	
	public boolean isMessageForLoggedUser(MessageContainer message, UserContainer myself)
	{
		String atUserNick = "@" + myself.getNick();
		
		return message.getMessage().contains(atUserNick);
	}
	
	public MessageContainer getMessage(){
		return myMessage;
	}
	
	public void updateMessage(MessageContainer message){
		myMessage = message;
		
		userMessagePane.setHTML(message.getMessage());
		
		if (myMessage.isMessageDeleted())
			setVisible(false);
		
		forMe = isMessageForLoggedUser(message, myUser);
		setUserPaneColor();
		setupStarred(myMessage);
	}
	
	private void setupStarred(MessageContainer message)
	{
		if(myMessage.getMessageStars().length() > 0){
			String prompt = "";
			ArrayList<String> nickList= UserManager.getInstance().idListToArray(myMessage.getMessageStars()); 
			for(String nick:nickList)
				prompt += nick + "\n";
			starStack.setTitle(prompt.substring(0, prompt.length() - 1)); // Crappy hack to remove trailing newline
			starStack.setVisible(true);
			starLabel.setText("x" + nickList.size());
			starIcon.setVisible(false);
			starOverIcon.setVisible(true);
			starred = true;
		} else{
			starStack.setTitle("");
			starStack.setVisible(false);
			starLabel.setText("Étoiler");
			starIcon.setVisible(true);
			starOverIcon.setVisible(false);
			starred = false;
		}
	}
	
	public void setUnread(boolean state) {
		myUnread = state;
		setUserPaneColor();
	}
}
