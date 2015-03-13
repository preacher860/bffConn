package com.lanouette.app.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
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

        String myEnhancedMessage = "";

        // If message hidden (deleted), no need to perform all the stuff, return immediately
        if (myMessage.isMessageDeleted()) {
            setVisible(false);
            return;
        }

        setupStarred(myMessage);
        forMe = isMessageForLoggedUser(message, myself);
        myEnhancedMessage = enhanceMessage(myMessage);

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
//        Anchor testAnchor = new Anchor();

        if (message.getMessageSeqId() == 1345) {
            Timer timer = new Timer() {
                @Override
                public void run() {

                    ClickHandler handler = new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            ConsoleLogger.getInstance().log("Anchor clicked");
                        }
                    };


                    Element element = DOM.getElementById("myLink");
                    ConsoleLogger.getInstance().log(element.toString());

                    Anchor.wrap(element).addClickHandler(handler);
                    //Anchor anchor = new Anchor(element.getInnerHTML());
                    //anchor.addClickHandler(handler);
                }
            };

            //timer.schedule(4000);
        }

        userMessagePane.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Element trgt = Element.as(event.getNativeEvent().getEventTarget());
                ConsoleLogger.getInstance().log("Click target: " + trgt.getInnerHTML());
            }
        });
        // Anchor.wrap(DOM.getElementById("myLink")).addClickHandler(handler);

        //NodeList<Element> anchors = userMessagePane.getElement().getElementsByTagName("a");
        //ConsoleLogger.getInstance().log("Found anchors: " + anchors.getLength());
//        for ( int i = 0 ; i < anchors.getLength() ; i++ ) {
//            Element a = anchors.getItem(i);
//            Anchor link = new Anchor(a.getInnerHTML());
//            link.addClickHandler(new ClickHandler() {
//                public void onClick(ClickEvent event) {
//                    ConsoleLogger.getInstance().log("Anchor clicked");
//                }
//            });
//            //HTMLPanel panel  = new HTMLPanel();
//            //panel.
//            //userMessagePane.addAndReplaceElement(link, a);
//        }

        //Element element = DOM.getElementById("myLink");
        //ConsoleLogger.getInstance().log("Element HTML: " + element.toString());

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
                            myUser.equals(messageUser),
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

        if (message.isMessageDeleted())
            setVisible(false);

        userMessagePane.setHTML(enhanceMessage(message));
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

    private String enhanceMessage(MessageContainer message) {
        String outputMessage = "";
        int token = 0;

        // Split the message in tokens (separator is space) an try to locate URLs
        String[] parts = message.getMessage().split("\\s+");

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
                    item = "<br><a href=\"" + item + "\" target=\"_blank\"><img class=\"embeddedimage\" src=\"" + item + "\" /></a><br>";
                else if (item.contains("www.youtube.com/user")) {
                    // Handle this as a normal link, it's not a video
                    // (except that we'll miss the http://www.youtube.com/user/UserName#p/u/1/1p3vcRhsYGo format)
                    item = encapsulateLink(item);
                } else if (item.contains("www.youtube.com") || item.contains("youtu.be")) {
                    item = encapsulateYoutube(item);
                } else {
                    // It's a link to some random site
                    item = encapsulateLink(item);
                }
            }

            outputMessage += item + " ";
        }

        if (message.getMessageSeqId() > 1300) {
            String finalMessage = "<a id='myLink'>TestLink</a>" + outputMessage;
            return finalMessage;

        } else {
            return outputMessage;
        }
    }

    private String encapsulateLink(String link) {
        String encapsulatedLink;

        RegExp regExp = RegExp.compile("https?://([a-zA-Z0-9.]+)");
        MatchResult matcher = regExp.exec(link);
        boolean matchFound = (matcher != null);

        // Matched regex in group0, subex in group one (the hostname)
        if (matchFound && matcher.getGroupCount() == 2) {
            encapsulatedLink = "<a href=\"" + link + "\" target=\"_blank\">" + matcher.getGroup(1) + "</a>";
        } else
            encapsulatedLink = "<a href=\"" + link + "\" target=\"_blank\">lien</a>";
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
}
