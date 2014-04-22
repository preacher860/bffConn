package com.lanouette.app.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class UserButtonBar extends HorizontalPanel{
    int userDbVersion = 0;
    private userCallbackInterface myUserCallbackInterface;

    UserButtonBar(userCallbackInterface callbackInterface) {
        myUserCallbackInterface = callbackInterface;
    }

    public void updateOnlineUsers(UserManager userManager) {
        if(userManager.getDbVersion() != userDbVersion)
        {
            clear();

            ArrayList<UserContainer> users = userManager.getOnlineUsers();
            if(users != null)
                for(UserContainer user: users) {
                    Image image = new Image();
                    image.setUrl(user.getAvatarURL());
                    image.setStyleName("headerAvatar");
                    image.setTitle(user.getNick());

                    image.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent clickEvent) {
                            myUserCallbackInterface.avatarClicked(((Image)clickEvent.getSource()).getTitle());
                        }
                    });

                    add(image);
                }
            userDbVersion = userManager.getDbVersion();
        }
    }
}
