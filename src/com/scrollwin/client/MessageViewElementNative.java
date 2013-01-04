package com.scrollwin.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
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
	private boolean myIconBarHovered = false; 
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
		String myEnhancedMessage = "";
		
		// If message hidden (deleted), no need to perform all the stuff, return immediately
		if (myMessage.isMessageDeleted()){
			setVisible(false);
			return;
		}
		
		setupStarred(myMessage);
		forMe = isMessageForLoggedUser(message, myself);
		myEnhancedMessage = enhanceMessage(myMessage.getMessage());
		
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

		userMessagePane.setHTML(myEnhancedMessage);
		
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
				myIconBarHovered = true;
				starStack.setVisible(true);
				deleteStack.setVisible(true);
				editStack.setVisible(true);
			}
		};

		addDomHandler(messageMouseOverHandler, MouseOverEvent.getType());
		
		MouseOutHandler messageMouseOutHandler = new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				myIconBarHovered = false;
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
		
		if (message.isMessageDeleted())
			setVisible(false);
		
		userMessagePane.setHTML(enhanceMessage(message.getMessage()));
		forMe = isMessageForLoggedUser(message, myUser);
		setUserPaneColor();
		setupStarred(message);
	}
	
	private void setupStarred(MessageContainer message)
	{
		if(message.getMessageStars().length() > 0){
			String prompt = "";
			ArrayList<String> nickList= UserManager.getInstance().idListToArray(message.getMessageStars()); 
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
			if (myIconBarHovered)
				starStack.setVisible(true);
			else
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
	
	private String enhanceMessage(String Message)
    {
    	String outputMessage = "";
    	int token = 0;
    	
    	// Split the message in tokens (separator is space) an try to locate URLs
    	String [] parts = Message.split("\\s+");
    	
    	// Check if the message is targeted at someone (
    	for(int tok = 0; tok < parts.length; tok++) {
    		if(parts[token].startsWith("@")){
    			if(token > 0)
    				outputMessage +=", ";

    			outputMessage += "<b>" + parts[token] + "</b>";
    			token++;
    		} else {
    			if(token > 0)
    				outputMessage += "<b> > </b>";
    			break;
    		}
    	}
    	
    	// Look for URLs and encapsulate them to img or href
    	for(int tok = token; tok < parts.length; tok++)
    	{
    		String item = parts[tok];
    		if ((item.startsWith("http://")) || (item.startsWith("https://")) ){
    			if( (item.endsWith(".jpg")) || (item.endsWith(".gif")) || (item.endsWith(".png")) ||
    				(item.endsWith(".JPG")) || (item.endsWith(".GIF")) || (item.endsWith(".PNG")))
    				item = "<br><a href=\"" + item + "\" target=\"_blank\"><img class=\"embeddedimage\" src=\"" + item + "\" /></a><br>";
    			else if(item.contains("www.youtube.com")){
    				int paramIndex = item.indexOf("v=");
    				if(paramIndex >= 0) {
    					// Youtube video (has v=)
    					String videoId = item.substring(paramIndex + 2, item.length());
    					item = "<br><iframe class=\"youtube-player\" type=\"text/html\" width=\"384\" height=\"231\" " +
    						   "src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\"></iframe><br>";
    				} else {
    					// Link to youtube but not an embeddable video
    					item = encapsulateLink(item);
    				}
    			}
    			else {
    				// It's a link to some random site
    				//GetPage(item);
    				item = encapsulateLink(item);
    			}
    		}
    		outputMessage += item + " ";
    	}
   	
    	return outputMessage;
    }
	
	private String encapsulateLink(String link)
	{
		String encapsulatedLink;
		
		// Cool-looking but does not quite work cross-browser...
		//encapsulatedLink  = "<div class=\"linkdiv\">";
		//encapsulatedLink += "<iframe class=\"linkiframe\" scrolling=\"no\" src=\"" + link + "\"></iframe>";
	    //encapsulatedLink += "<a class=\"linkanchor\" href=\"http://www.src.ca\"></a>";
	    //encapsulatedLink += "</div>";
	    
		RegExp regExp = RegExp.compile("https?://([a-zA-Z0-9.]+)");
		MatchResult matcher = regExp.exec(link);
		boolean matchFound = (matcher != null);

		// Matched regex in group0, subex in group one (the hostname)
		if(matchFound && matcher.getGroupCount() == 2) {
			encapsulatedLink = "<a href=\"" + link + "\" target=\"_blank\">" + matcher.getGroup(1) + "</a>";
		} else
			encapsulatedLink = "<a href=\"" + link + "\" target=\"_blank\">lien</a>";
	    return encapsulatedLink;
	}
	
	private void GetPage(String pageUrl)
	{
		String postData = "dummy";
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, pageUrl);
		try {
			//builder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			builder.sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
							System.out.println("GetPage successful, data size: " + response.getText().length());
					} 
					else
						System.out.println("GetPage request response error: " + response.getStatusCode());
				}
			});
		} catch (RequestException e) {
			Window.alert("Server error: " + e);
			// Couldn't connect to server        
		}
	}
}
