package com.lanouette.app.client.MessagePopup;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.MessageContainer;
import com.lanouette.app.client.MessageViewElementCallback;
import com.lanouette.app.client.UserCallbackInterface;
import com.lanouette.app.client.UserManager;

public class MessagePopup extends PopupPanel {
    interface MyUiBinder extends UiBinder<Widget, MessagePopup> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private UserCallbackInterface myCallbackInterface;
    private MessageViewElementCallback myMessageCallback;
    private MessageContainer myMessage;

    @UiField
    Image closeImage;
    @UiField
    FocusPanel closePanel;
    @UiField
    Image deleteImage;
    @UiField
    Image editImage;
    @UiField
    FocusPanel starPanel;
    @UiField
    FocusPanel editPanel;
    @UiField
    FocusPanel deletePanel;
    @UiField
    HTML starersBox;
    @UiField
    Label seqIdLabel;
    @UiField
    Label starNum;

    public MessagePopup(MessageContainer message,
                        boolean isMine,
                        UserCallbackInterface callbackInterface,
                        MessageViewElementCallback messageCallback) {
        super(true);

        myCallbackInterface = callbackInterface;
        myMessageCallback = messageCallback;
        myMessage = message;

        setAnimationEnabled(true);
        setStyleName("popupFrame");

        setWidget(uiBinder.createAndBindUi(this));

        if (myMessage.getMessageStars().length() > 0) {
            String prompt = "";
            ArrayList<String> nickList = UserManager.getInstance().idListToArray(myMessage.getMessageStars());
            for (String nick : nickList)
                prompt += nick + "<br>";
            starersBox.setHTML(prompt.substring(0, prompt.length() - 1)); // Crappy hack to remove trailing newline
            starNum.setText("x" + nickList.size());
        }

        seqIdLabel.setText("Message " + myMessage.getMessageSeqId());

        closePanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
                myMessageCallback.messageUnselect();
            }
        });

        if(!isMine) {
            deleteImage.setUrl("images/erasergray.png");
            editImage.setUrl("images/editgray.png");
        } else {
            editPanel.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    hide();
                    myMessageCallback.messageUnselect();
                    myCallbackInterface.editMessageClicked(myMessage);
                }
            });

            deletePanel.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    hide();
                    myMessageCallback.messageUnselect();
                    myCallbackInterface.deleteClicked(myMessage.getMessageSeqId());
                }
            });
        }

        starPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
                myMessageCallback.messageUnselect();
                myCallbackInterface.starClicked(myMessage.getMessageSeqId());
            }
        });

        addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                myMessageCallback.messageUnselect();
            }
        });
    }
}
