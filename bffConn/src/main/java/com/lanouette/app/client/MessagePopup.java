package com.lanouette.app.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class MessagePopup extends PopupPanel {
    interface MyUiBinder extends UiBinder<Widget, MessagePopup> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private userCallbackInterface myCallbackInterface;
    private MessageContainer myMessage;

    @UiField
    Button closeButton;
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
    Label starersBox;

    public MessagePopup(MessageContainer message,
                        boolean isMine,
                        userCallbackInterface callbackInterface) {
        super(false);

        myCallbackInterface = callbackInterface;
        myMessage = message;

        setAnimationEnabled(true);
        setStyleName("popupFrame");

        setWidget(uiBinder.createAndBindUi(this));

        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        if(!isMine) {
            deleteImage.setUrl("images/deletegray.png");
            editImage.setUrl("images/editgray.png");
        } else {
            editPanel.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    hide();
                    myCallbackInterface.editMessageClicked(myMessage);
                }
            });

            deletePanel.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    hide();
                    myCallbackInterface.deleteClicked(myMessage.getMessageSeqId());
                }
            });
        }

        starPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                if (myMessage.getMessageStars().length() > 0) {
                    String prompt = "";
                    ArrayList<String> nickList = UserManager.getInstance().idListToArray(myMessage.getMessageStars());
                    for (String nick : nickList)
                        prompt += nick + "\n";
                    starersBox.setText(prompt.substring(0, prompt.length() - 1)); // Crappy hack to remove trailing newline
                    starersBox.setVisible(true);
                }
            }
        });
    }
}
