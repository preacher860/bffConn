package com.lanouette.app.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.lanouette.app.client.UserPopup.UserPopup;

public class UserButtonBar extends FocusPanel {
    int userDbVersion = 0;
    private UserCallbackInterface myUserCallbackInterface;
    private HorizontalPanel panel = new HorizontalPanel();
    private UserPopup popup;

    UserButtonBar() {
    }

    public void initialize(UserCallbackInterface callbackInterface) {
        myUserCallbackInterface = callbackInterface;
        popup = new UserPopup(myUserCallbackInterface);

        add(panel);
    }

    public void updateOnlineUsers() {
        if (UserManager.getInstance().getDbVersion() != userDbVersion) {
            panel.clear();

            final ArrayList<UserContainer> users = UserManager.getInstance().getOnlineUsers();

            if (users != null) {
                for (UserContainer user : users) {
                    Image image = new Image();
                    image.setUrl(user.getHostAvatarURL());
                    image.setStyleName("headerAvatar");
                    image.setTitle(user.getNick());

                    image.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent clickEvent) {
                            myUserCallbackInterface.avatarClicked(((Image) clickEvent.getSource()).getTitle());
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
