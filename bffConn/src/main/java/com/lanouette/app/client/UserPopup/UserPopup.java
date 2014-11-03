package com.lanouette.app.client.UserPopup;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.FunctionPopup.FunctionPopup;
import com.lanouette.app.client.UserContainer;
import com.lanouette.app.client.userCallbackInterface;

public class UserPopup extends PopupPanel {
    interface MyUiBinder extends UiBinder<Widget, UserPopup> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private userCallbackInterface myUserCallbackInterface;

    @UiField
    VerticalPanel usersPanel;
    @UiField
    FocusPanel closePanel;

    public UserPopup(userCallbackInterface callbackInterface) {
        super(true);

        myUserCallbackInterface = callbackInterface;

        doSetAnimationType(this);
        setAnimationEnabled(true);
        setStyleName("popupFrame");

        setWidget(uiBinder.createAndBindUi(this));

        closePanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });
    }

    public void setUserList(ArrayList<UserContainer> users) {
        usersPanel.clear();

        for (UserContainer user : users) {
            Image image = new Image();
            image.setUrl(user.getAvatarURL());
            image.setStyleName("popupImage");

            Label label = new Label(user.getNick());
            label.addStyleName("popupLabel");

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

            usersPanel.add(panelWrapper);
        }
    }

    private native void doSetAnimationType(PopupPanel popup) /*-{
        popup.@com.lanouette.app.client.UserPopup.UserPopup::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::ROLL_DOWN);
    }-*/;

}

