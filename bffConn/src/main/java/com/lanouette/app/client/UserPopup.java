package com.lanouette.app.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPopup extends PopupPanel {

    private VerticalPanel mainPanel = new VerticalPanel();
    private userCallbackInterface myUserCallbackInterface;

    public UserPopup(userCallbackInterface callbackInterface) {
        super(true);

        myUserCallbackInterface = callbackInterface;

        doSetAnimationType(this);
        setAnimationEnabled(true);
        setStyleName("userPopup");

        setWidget(mainPanel);
    }

    public void setUserList(ArrayList<UserContainer> users) {
        mainPanel.clear();

        for (UserContainer user : users) {
            Image image = new Image();
            image.setUrl(user.getAvatarURL());
            image.setStyleName("userPopupAvatar");

            Label label = new Label(user.getNick());
            label.addStyleName("userPopupLabel");

            HorizontalPanel userPanel = new HorizontalPanel();
            userPanel.add(image);
            userPanel.add(label);
            userPanel.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);

            FocusPanel panelWrapper = new FocusPanel();
            panelWrapper.add(userPanel);
            panelWrapper.setTitle(user.getNick());
            panelWrapper.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    myUserCallbackInterface.avatarClicked(((FocusPanel)clickEvent.getSource()).getTitle());
                    hide();
                }
            });

            mainPanel.add(panelWrapper);
        }

        Button closeButton = new Button("Fermer");
        closeButton.setStyleName("popupButton");
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        mainPanel.add(closeButton);
        mainPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_CENTER);

    }

    private native void doSetAnimationType(PopupPanel popup) /*-{
        popup.@com.lanouette.app.client.UserPopup::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::ROLL_DOWN);
    }-*/;

}

