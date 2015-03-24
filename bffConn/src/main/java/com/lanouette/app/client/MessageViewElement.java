package com.lanouette.app.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lanouette.app.client.MessagePopup.MessagePopup;

public class MessageViewElement extends HorizontalPanel implements MessageViewElementCallback {
    private final boolean isMobile;
    private final ioCommandCallback ogCommandCallback;
    private HorizontalPanel infoPane = new HorizontalPanel();
    private HorizontalPanel iconPane = new HorizontalPanel();
    private VerticalPanel messagePane = new VerticalPanel();
    private VerticalPanel imageStack = new VerticalPanel();
    private HTML userInfoLabel = new HTML();
    private HorizontalPanel starStack = new HorizontalPanel();
    private HorizontalPanel deleteStack = new HorizontalPanel();
    private HorizontalPanel editStack = new HorizontalPanel();
    private HTML userMessagePane = new HTML();
    private Image starIcon = new Image("images/stargray.png");
    private Image starOverIcon = new Image("images/stargray_Over.png");
    private Image deleteIcon = new Image("images/erasergray.png");
    private Image deleteOverIcon = new Image("images/erasergray_Over.png");
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
    private UserCallbackInterface myUserCallbackInterface;
    private String myMessageOriginatingUser;
    private MessageContainer myMessage = null;
    private UserContainer messageUser = null;
    private UserContainer myUser = null;
    private MessageViewElement mySelfRef;

    public MessageViewElement(MessageContainer message, UserContainer user,
                              UserContainer myself, UserCallbackInterface cb) {
        myUserCallbackInterface = cb;
        myMessageOriginatingUser = user.getNick();
        myMessage = message;
        messageUser = user;
        myUser = myself;
        mySelfRef = this;
        isMobile = RuntimeData.getInstance().isMobile();

        ogCommandCallback = new ioCommandCallback() {
            public void execute(String response) {
                processOgTags(response);
            }
        };

        String myEnhancedMessage = "";

        // If message hidden (deleted), no need to perform all the stuff, return immediately
        if (myMessage.isMessageDeleted()) {
            setVisible(false);
            return;
        }

        setupStarred(myMessage);
        forMe = isMessageForLoggedUser(message, myself);
        myEnhancedMessage = enhanceMessage(myMessage.getMessage());

        setStyleName("messageViewElement");

        Integer kittenSelect = 48 + message.getMessageUserId();
        if (user.getHostAvatarURL().isEmpty())
            userImage = new Image("http://placekitten.com/" + kittenSelect + "/" + kittenSelect);
        else
            userImage = new Image(user.getHostAvatarURL());

        userImage.setStyleName("userAvatar");
        userImage.setTitle(myMessageOriginatingUser);
        imageStack.setStyleName("messageViewPicBox");
        imageStack.add(userImage);

        starIcon.setStyleName("starIcon");
        starOverIcon.setStyleName("starIcon");
        starLabel.setStyleName("starLabel");

        starStack.setStyleName("starStack");
        starStack.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        starStack.add(starIcon);
        starStack.add(starOverIcon);
        starStack.add(starLabel);
        if (!starred) {
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
        deleteStack.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        deleteStack.setVisible(false);
        deleteStack.add(deleteIcon);
        deleteStack.add(deleteOverIcon);
        deleteStack.add(deleteLabel);

        editIcon.setStyleName("editIcon");
        editOverIcon.setStyleName("editIcon");
        editOverIcon.setVisible(false);
        editLabel.setStyleName("editLabel");
        editStack.setStyleName("editStack");
        editStack.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        editStack.setVisible(false);
        editStack.add(editIcon);
        editStack.add(editOverIcon);
        editStack.add(editLabel);

        iconPane.add(starStack);
        iconPane.add(deleteStack);
        iconPane.add(editStack);

        String infoLabelContents = "";
        if (isMobile) {
            infoLabelContents = message.getMessageSeqId() + " - " +
                    message.getMessageDate() +
                    " à " + message.getMessageTime();
        } else {
            infoLabelContents = "Message " + message.getMessageSeqId() + "   envoyé par " +
                    user.getNick() + "  le " + message.getMessageDate() +
                    " à " + message.getMessageTime();
        }

        if (message.getMessageLocal().contentEquals("") != true)
            infoLabelContents += " - " + message.getMessageLocal();
        userInfoLabel.setHTML(infoLabelContents);

        userMessagePane.setHTML(myEnhancedMessage);

        userMessagePane.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String elementText = Element.as(event.getNativeEvent().getEventTarget()).getInnerHTML();

                // Check for jump links
                if (elementText != null && elementText.startsWith("#")) {
                    try {
                        Integer value = Integer.valueOf(elementText.substring(1, elementText.length()));
                        event.stopPropagation();
                        myUserCallbackInterface.jumpLinkClicked(value);
                    } catch (Exception e) {
                        ConsoleLogger.getInstance().log("Jump doesn't appear to be a valid int");
                    }
                }

                // Check for links as not to pop the menu if following
                String elementHref = Element.as(event.getNativeEvent().getEventTarget()).getPropertyString("href");
                if ((elementHref != null) && ((elementHref.startsWith("http://") || elementHref.startsWith("https://")))) {
                    event.stopPropagation();
                }
            }
        });

        infoPane.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
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
        setCellWidth(imageStack, "44px");
        add(messagePane);

        setUserPaneColor();

        if (isMobile) {
            ClickHandler messagePaneClickHandler = new ClickHandler() {
                public void onClick(ClickEvent event) {
                    final MessagePopup popup = new MessagePopup(myMessage,
                            myUser.getNick().equals(messageUser.getNick()),
                            myUserCallbackInterface,
                            MessageViewElement.this);

                    popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = (int) (DOM.getElementById("messageView").getAbsoluteLeft() +
                                    DOM.getElementById("messageView").getClientWidth() -
                                    offsetWidth * 1.25);
                            int top;

                            // Popup always on top of message window, as long as this fits in message view.
                            // Bottom otherwise
                            if (getAbsoluteTop() - offsetHeight < DOM.getElementById("messageView").getAbsoluteTop()) {
                                if (getAbsoluteTop() + getOffsetHeight() + offsetHeight >
                                        Window.getClientHeight()) {
                                    top = Window.getClientHeight() - offsetHeight;
                                } else {
                                    top = getAbsoluteTop() + getOffsetHeight();
                                }
                            } else {
                                top = getAbsoluteTop() - offsetHeight;
                            }

                            popup.setPopupPosition(left, top);
                        }
                    });

                    messageSelect();
                }
            };
            messagePane.addDomHandler(messagePaneClickHandler, ClickEvent.getType());
        }

        ClickHandler avatarClickHandler = new ClickHandler() {
            public void onClick(ClickEvent event) {
                myUserCallbackInterface.avatarClicked(myMessageOriginatingUser);
            }
        };
        imageStack.addDomHandler(avatarClickHandler, ClickEvent.getType());

        if (!isMobile) {

            ClickHandler starClickHandler = new ClickHandler() {
                public void onClick(ClickEvent event) {
                    myUserCallbackInterface.starClicked(myMessage.getMessageSeqId());
                }
            };
            starStack.addDomHandler(starClickHandler, ClickEvent.getType());

            MouseOverHandler starMouseOverHandler = new MouseOverHandler() {
                public void onMouseOver(MouseOverEvent event) {
                    if (!starred) {
                        starOverIcon.setVisible(true);
                        starIcon.setVisible(false);
                    }
                }
            };
            starStack.addDomHandler(starMouseOverHandler, MouseOverEvent.getType());

            MouseOutHandler starMouseOutHandler = new MouseOutHandler() {
                public void onMouseOut(MouseOutEvent event) {
                    if (!starred) {
                        starOverIcon.setVisible(false);
                        starIcon.setVisible(true);
                    }
                }
            };
            starStack.addDomHandler(starMouseOutHandler, MouseOutEvent.getType());

            // May only delete/edit own messages
            if (myMessage.getMessageUserId() == RuntimeData.getInstance().getUserId()) {

                ClickHandler editClickHandler = new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        myUserCallbackInterface.editMessageClicked(myMessage);
                    }
                };

                editStack.addDomHandler(editClickHandler, ClickEvent.getType());

                MouseOverHandler editMouseOverHandler = new MouseOverHandler() {
                    public void onMouseOver(MouseOverEvent event) {
                        editOverIcon.setVisible(true);
                        editIcon.setVisible(false);
                    }
                };
                editStack.addDomHandler(editMouseOverHandler, MouseOverEvent.getType());

                MouseOutHandler editMouseOutHandler = new MouseOutHandler() {
                    public void onMouseOut(MouseOutEvent event) {
                        editOverIcon.setVisible(false);
                        editIcon.setVisible(true);
                    }
                };

                editStack.addDomHandler(editMouseOutHandler, MouseOutEvent.getType());

                ClickHandler deleteClickHandler = new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        myUserCallbackInterface.deleteClicked(myMessage.getMessageSeqId());
                    }
                };

                deleteStack.addDomHandler(deleteClickHandler, ClickEvent.getType());

                MouseOverHandler deleteMouseOverHandler = new MouseOverHandler() {
                    public void onMouseOver(MouseOverEvent event) {
                        deleteOverIcon.setVisible(true);
                        deleteIcon.setVisible(false);
                    }
                };
                deleteStack.addDomHandler(deleteMouseOverHandler, MouseOverEvent.getType());

                MouseOutHandler deleteMouseOutHandler = new MouseOutHandler() {
                    public void onMouseOut(MouseOutEvent event) {
                        deleteOverIcon.setVisible(false);
                        deleteIcon.setVisible(true);
                    }
                };

                deleteStack.addDomHandler(deleteMouseOutHandler, MouseOutEvent.getType());
            }

            MouseOverHandler messageMouseOverHandler = new MouseOverHandler() {
                public void onMouseOver(MouseOverEvent event) {
                    myIconBarHovered = true;
                    starStack.setVisible(true);
                    deleteStack.setVisible(true);
                    editStack.setVisible(true);
                }
            };

            addDomHandler(messageMouseOverHandler, MouseOverEvent.getType());

            MouseOutHandler messageMouseOutHandler = new MouseOutHandler() {
                public void onMouseOut(MouseOutEvent event) {
                    myIconBarHovered = false;
                    if (!starred)
                        starStack.setVisible(false);
                    deleteStack.setVisible(false);
                    editStack.setVisible(false);
                }
            };

            addDomHandler(messageMouseOutHandler, MouseOutEvent.getType());
        }
    }

    public boolean isMessageForLoggedUser(MessageContainer message, UserContainer myself) {
        String atUserNick = "@" + myself.getNick();

        return message.getMessage().contains(atUserNick);
    }

    public MessageContainer getMessage() {
        return myMessage;
    }

    public void updateMessage(MessageContainer message) {
        myMessage = message;

        if (message.isMessageDeleted()) {
            setVisible(false);
        }

        userMessagePane.setHTML(enhanceMessage(message.getMessage()));
        forMe = isMessageForLoggedUser(message, myUser);
        setUserPaneColor();
        setupStarred(message);
    }

    public void setUnread(boolean state) {
        myUnread = state;
        setUserPaneColor();
    }

    public void messageSelect() {
        addStyleName("messageViewElementSelect");
    }

    public void messageUnselect() {
        removeStyleName("messageViewElementSelect");
    }

    private void setUserPaneColor() {
        userMessagePane.setStyleName("messageViewElementBox");

        if (forMe && myUnread)
            userMessagePane.addStyleName("messageViewElementBoxAdresseeUnread"); // Addressee + unread purple
        else if (forMe)
            userMessagePane.addStyleName("messageViewElementBoxAdressee"); // Addressee green
        else if (myUnread)
            userMessagePane.addStyleName("messageViewElementBoxUnread"); // Unread orange
        else {
            userMessagePane.addStyleName("messageViewElementBoxNormal"); // Normal blue
        }
    }

    private void setupStarred(MessageContainer message) {
        if (message.getMessageStars().length() > 0) {
            String prompt = "";
            ArrayList<String> nickList = UserManager.getInstance().idListToArray(message.getMessageStars());
            for (String nick : nickList)
                prompt += nick + "\n";
            starStack.setTitle(prompt.substring(0, prompt.length() - 1)); // Crappy hack to remove trailing newline
            starStack.setVisible(true);
            starLabel.setText("x" + nickList.size());
            starIcon.setVisible(false);
            starOverIcon.setVisible(true);
            starred = true;
        } else {
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

    private String enhanceMessage(String message) {
        String outputMessage = "";
        int token = 0;

        // Split the message in tokens (separator is space) an try to locate URLs
        String[] parts = message.split("\\s+");

        // Check if the message is targeted at someone (
        for (int tok = 0; tok < parts.length; tok++) {
            if (parts[token].startsWith("@")) {
                if (token > 0)
                    outputMessage += ", ";

                outputMessage += "<b>" + parts[token] + "</b>";
                token++;
            } else {
                if (token > 0)
                    outputMessage += "<b> > </b>";
                break;
            }
        }

        // Look for URLs and encapsulate them to img or href
        for (int tok = token; tok < parts.length; tok++) {
            String item = parts[tok];
            if ((item.startsWith("http://")) || (item.startsWith("https://"))) {
                if ((item.endsWith(".jpg")) || (item.endsWith(".gif")) || (item.endsWith(".png")) ||
                        (item.endsWith(".JPG")) || (item.endsWith(".GIF")) || (item.endsWith(".PNG")))
                    item = encapsulateImage(item, item);
                else if (item.contains("www.youtube.com/user")) {
                    // Handle this as a normal link, it's not a video
                    // (except that we'll miss the http://www.youtube.com/user/UserName#p/u/1/1p3vcRhsYGo format)
                    item = encapsulateLink(item);
                } else if (item.contains("www.youtube.com") || item.contains("youtu.be")) {
                    item = encapsulateYoutube(item);
                } else {
                    // It's a link to some random site
                    IOModule.getInstance().SendOgDataRequest(item, ogCommandCallback);
                    item = encapsulateLink(item);
                }
            }

            if (item.startsWith("#")) {
                item = encapsulateJump(item);
            }

            outputMessage += item + " ";
        }

        return outputMessage;
    }

    private String encapsulateImage(String href, String imgSrc) {
        String encapsulated = "<br><a href=\"" + href + "\" target=\"_blank\">";
        encapsulated += "<img class=\"embeddedimage\" src=\"" + imgSrc + "\" /></a><br>";

        return encapsulated;
    }

    private String encapsulateThumbnail(String href, String imgSrc) {
        String encapsulated = "<a class=\"ogAnchorImage\" href=\"" + href + "\" target=\"_blank\">";
        encapsulated += "<img class=\"embeddedthumbnail\" src=\"" + imgSrc + "\" /></a>";

        return encapsulated;
    }

    private String encapsulateLink(String link) {
        String encapsulatedLink;

        RegExp regExp = RegExp.compile("https?://([a-zA-Z0-9.-]+)");
        MatchResult matcher = regExp.exec(link);
        boolean matchFound = (matcher != null);

        // Matched regex in group0, subex in group one (the hostname)
        if (matchFound && matcher.getGroupCount() == 2) {
            encapsulatedLink = "<a href=\"" + link + "\" target=\"_blank\">" + matcher.getGroup(1) + "</a>";
        } else
            encapsulatedLink = "<a href=\"" + link + "\" target=\"_blank\">lien</a>";
        return encapsulatedLink;
    }

    private String encapsulateAnchor(String href, String title) {
        String encapsulatedLink;

        encapsulatedLink = "<a class=\"ogAnchor\" href=\"" + href + "\" target=\"_blank\">" + title + "</a>";

        return encapsulatedLink;
    }

    private String encapsulateYoutube(String link) {
        String encapsulatedLink;

        RegExp regExp = RegExp.compile("(v=|\\/)([\\w-]+)(&.+)?$");
        MatchResult matcher = regExp.exec(link);
        boolean matchFound = (matcher != null);

        if (matchFound && matcher.getGroupCount() >= 3) {
            encapsulatedLink = "<br><iframe class=\"youtube-player\" type=\"text/html\" width=\"384\" height=\"231\" " +
                    "src=\"https://www.youtube.com/embed/" + matcher.getGroup(2) + "\" frameborder=\"0\"></iframe><br>";
        } else {
            // Link to youtube but not an embeddable video
            encapsulatedLink = encapsulateLink(link);
        }
        return encapsulatedLink;
    }

    private String encapsulateJump(String jump) {
        String encapsulated = jump;
        RegExp regExp = RegExp.compile("[#]\\d+");
        MatchResult matcher = regExp.exec(jump);

        // Matched regex in group0, subex in group one (the hostname)
        if (matcher != null && matcher.getGroupCount() == 1) {
            ConsoleLogger.getInstance().log("Found jump link " + matcher.getGroup(0));

            // First see if we can convert that to an int, otherwise leave it as text
            try {
                Integer value = Integer.valueOf(matcher.getGroup(0).substring(1, matcher.getGroup(0).length()));
                if (value < 1) {
                    return jump;
                }
            } catch (Exception e) {
                return jump;
            }

            encapsulated = "<a class=\"jumpAnchor\">" + matcher.getGroup(0) + "</a>";
            encapsulated += jump.substring(matcher.getGroup(0).length(), jump.length());
        }

        return encapsulated;
    }

    private void processOgTags(String responseText) {
        try {
            JSONValue jsonValue = JSONParser.parseStrict(responseText);
            String targetUrl = jsonValue.isObject().get("target_url").isString().stringValue();
            String ogTitle = jsonValue.isObject().get("ogtitle").isString().stringValue();
            String ogImage = jsonValue.isObject().get("ogimage").isString().stringValue();
            //ConsoleLogger.getInstance().log("Target URL: " + targetUrl);
            //ConsoleLogger.getInstance().log("OG Title: " + ogTitle);
            //ConsoleLogger.getInstance().log("OG Image: " + ogImage);

            if (!ogTitle.isEmpty()) {
                // Just keep the part before the first splitter in the title
                if (ogTitle.contains("|")) {
                    ogTitle = ogTitle.substring(0, ogTitle.indexOf('|'));
                }

                // Try to locate the anchor where this URL was encapsulated when the message was first
                // displayed. If found, replace the anchor with formatted OG data
                String displayedMessage = userMessagePane.getHTML();

                // Encapsulate target URL and match with the one likely present in DOM
                String anchorItem = encapsulateLink(targetUrl);
                Integer targetUrlPos = displayedMessage.indexOf(anchorItem);
                if (targetUrlPos >= 0) {
                    String replaced = "";

                    if (targetUrlPos > 0) {
                        replaced = displayedMessage.substring(0, targetUrlPos);
                        replaced += "<br>";
                    }

                    replaced += "<table><tr>";
                    if (!ogImage.isEmpty()) {
                        replaced += "<td class='ogImageContainer'>";
                        replaced += encapsulateThumbnail(targetUrl, ogImage);
                        replaced += "</td>";
                    }

                    replaced += "<td class='ogTitleContainer'>";
                    replaced += encapsulateAnchor(targetUrl, ogTitle) + "<br>";

                    RegExp regExp = RegExp.compile("https?://([a-zA-Z0-9.-]+)");
                    MatchResult matcher = regExp.exec(targetUrl);
                    if (matcher != null && matcher.getGroupCount() == 2) {
                        replaced += "<span class='ogTitleTarget'>";
                        replaced += matcher.getGroup(1) + "</span><br>";
                    }
                    replaced += "</td>";
                    replaced += "</tr></table>";

                    replaced += displayedMessage.substring(targetUrlPos + anchorItem.length(),
                            displayedMessage.length());

                    userMessagePane.setHTML(replaced);
                }
            }
        } catch (Exception e) {
            ConsoleLogger.getInstance().log("Error encapsulating OG tags: " + e.toString());
        }
    }
}
