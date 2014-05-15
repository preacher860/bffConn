package com.lanouette.app.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EntryBox extends HorizontalPanel {

    private TextArea messageItem = new TextArea();
    private HTML infoItem = new HTML();
    private VerticalPanel imageStack = new VerticalPanel();
    private VerticalPanel editStack = new VerticalPanel();
    private ioCallbackInterface myCallbackInterface;
    private userCallbackInterface myUserCallbackInterface;
    private boolean myIsShiftDown = false;
    private boolean myIsEditing = false;
    private int myEditingMessageSeq = 0;


    public EntryBox(ioCallbackInterface callbackInterface, userCallbackInterface userCB, boolean mobile, boolean iphone) {
        myCallbackInterface = callbackInterface;
        myUserCallbackInterface = userCB;

        setStyleName("entryBox");
        if (mobile) {
            addStyleName("entryBoxMobile");
        } else {
            addStyleName("entryBoxDesktop");
        }
        imageStack.setStyleName("entryBoxPicBox");
        infoItem.setHTML("Editing message 222222");
        infoItem.setStyleName("entryBoxInfoItem");
        infoItem.setVisible(false);

        editStack.setStyleName("editBoxTable");
        editStack.getElement().setAttribute("cellpadding", "1");
        messageItem.setCharacterWidth(80);
        messageItem.addStyleName("messageEditBox");
        if (mobile) {
            if (iphone) {
                messageItem.addStyleName("messageEditBoxIPhone");
            } else {
                messageItem.addStyleName("messageEditBoxMobile");
            }
        }

        ClickHandler entryBoxClickHandler = new ClickHandler() {
            public void onClick(ClickEvent event) {
                myUserCallbackInterface.userEntry();
            }
        };
        addDomHandler(entryBoxClickHandler, ClickEvent.getType());

        messageItem.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent keyDownEvent) {
                if (keyDownEvent.isShiftKeyDown()) {
                    myIsShiftDown = true;
                } else {
                    myIsShiftDown = false;
                }

                myUserCallbackInterface.userEntry();

                if (keyDownEvent.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (!myIsShiftDown) {
                        keyDownEvent.stopPropagation();
                        keyDownEvent.preventDefault();
                        messageItem.setCursorPos(messageItem.getValue().length()); // Cursor at end
                        myCallbackInterface.messageToSendCallback(filterMessage(messageItem.getValue()), myIsEditing, myEditingMessageSeq);
                        myIsEditing = false;
                        infoItem.setVisible(false);
                        myEditingMessageSeq = 0;
                        messageItem.setValue("");
                    }
                }

                if (keyDownEvent.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                    if (myIsEditing) {
                        myIsEditing = false;
                        infoItem.setVisible(false);
                        myEditingMessageSeq = 0;
                        messageItem.setValue("");
                    }
                }
            }
        });

        editStack.add(infoItem);
        editStack.add(messageItem);

        add(imageStack);
        add(editStack);
        setCellWidth(imageStack, "50px");
        setCellWidth(editStack, "100%");
    }

    public void setFocus() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            public void execute() {
                messageItem.setFocus(true);
            }
        });
    }

    public void setUser(UserContainer user) {
        Image userImage = new Image(user.getAvatarURL());
        userImage.setStyleName("userAvatarEntryBox");
        imageStack.add(userImage);
    }

    public void addAddressee(String userNick) {
        String Message;

        int currentPos = messageItem.getCursorPos();
        String insertedNick;
        String currentMsg = messageItem.getValue();

        Message = currentMsg.substring(0, currentPos);
        if (currentPos != 0) {
            insertedNick = " @" + userNick + " ";
        } else {
            insertedNick = "@" + userNick + " ";
        }
        Message += insertedNick;
        Message += currentMsg.substring(currentPos, currentMsg.length());

        messageItem.setValue(Message);
        messageItem.setCursorPos(currentPos + insertedNick.length());

        setFocus();
    }

    public String filterMessage(String Message) {
        String outputMessage = "";

        // remove trailing crlf "submit", it's not part of the message
        if (Message.endsWith("\n"))
            Message = Message.substring(0, Message.length() - 1);

        // Convert CrLf to HTML linefeeds before the split because \n\r are considered
        // as whitespace by the regex.
        outputMessage = convertCrLf(Message);
        return escapeJson(outputMessage);
    }

    public void editMessage(MessageContainer message) {
        String decodedMessage = URL.decode(message.getMessage());
        messageItem.setValue(deconvertCrLf(decodedMessage));
        myIsEditing = true;
        myEditingMessageSeq = message.getMessageSeqId();
        infoItem.setHTML("Édition du message <b>" + myEditingMessageSeq + "</b>. ESC pour annuler");
        infoItem.setVisible(true);
        setFocus();
    }

    public String escapeJson(String str) {
        str = str.replace("\\", "\\\\");
        str = str.replace("\"", "\\\"");
        str = str.replace("/", "\\/");
        str = str.replace("\b", "\\b");
        str = str.replace("\f", "\\f");
        str = str.replace("\t", "\\t");
        return str;
    }

    public String convertCrLf(String str) {
        // Add space before so the <br> is processed as a separate token
        str = str.replace("\n", " <br /> ");
        str = str.replace("\r", " <br /> ");
        return str;
    }

    public String deconvertCrLf(String str) {
        // Put back the \n where <br> where inserted
        str = str.replace(" <br /> ", "\n");
        str = str.replace("<br />", "\n");
        return str;
    }
}
