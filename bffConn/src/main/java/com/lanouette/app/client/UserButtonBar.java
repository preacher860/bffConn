package com.lanouette.app.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class UserButtonBar extends FocusPanel {
    int userDbVersion = 0;
    private userCallbackInterface myUserCallbackInterface;
    private HorizontalPanel panel = new HorizontalPanel();
    final UserPopup popup;

    UserButtonBar(userCallbackInterface callbackInterface) {
        myUserCallbackInterface = callbackInterface;
        popup  = new UserPopup(callbackInterface);

        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                setFocus(false);

                popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = getAbsoluteLeft();
                        int top = getAbsoluteTop() + getOffsetHeight();
                        popup.setPopupPosition(left, top);
                    }
                });
            }
        });

        add(panel);
    }

    public void updateOnlineUsers() {
        if (UserManager.getInstance().getDbVersion() != userDbVersion) {
            panel.clear();

            final ArrayList<UserContainer> users = UserManager.getInstance().getOnlineUsers();

            if (users != null) {
                for (UserContainer user : users) {
                    Image image = new Image();
                    image.setUrl(user.getAvatarURL());
                    image.setStyleName("headerAvatar");
                    image.setTitle(user.getNick());

                    image.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent clickEvent) {
                            //myUserCallbackInterface.avatarClicked(((Image)clickEvent.getSource()).getTitle());
                        }
                    });

                    panel.add(image);
                }

                popup.setUserList(users);
            }
            userDbVersion = UserManager.getInstance().getDbVersion();
        }
    }
}
