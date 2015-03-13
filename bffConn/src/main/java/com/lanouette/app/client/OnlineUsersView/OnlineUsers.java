package com.lanouette.app.client.OnlineUsersView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.UserCallbackInterface;
import com.lanouette.app.client.UserContainer;
import com.lanouette.app.client.UserManager;
import com.lanouette.app.client.userTile.TileEvent;
import com.lanouette.app.client.userTile.UserTile;

public class OnlineUsers implements IsWidget {
    interface MyUiBinder extends UiBinder<Widget, OnlineUsers> {
    }

    private final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private UserCallbackInterface userCallbackInterface;
    private TileEvent tileEvent;
    private int userDbVersion = 0;

    @UiField
    HTMLPanel mainPanel;
    @UiField
    SimplePanel header;
    @UiField
    FlowPanel usersView;

    public OnlineUsers() {
        uiBinder.createAndBindUi(this);
    }

    public void initialize(UserCallbackInterface userCallbackInterface) {
        this.userCallbackInterface = userCallbackInterface;
        createHandlers();
    }

    public void UpdateOnlineUsers(UserManager userManager) {
        if (userManager.getDbVersion() != userDbVersion) {
            usersView.clear();

            if (userManager.getOnlineUsers() != null) {
                for (UserContainer user : userManager.getOnlineUsers()) {
                    UserTile tile = new UserTile(user.getHostAvatarURL(), user.getNick(), tileEvent);
                    usersView.add(tile);
                }
            }
            userDbVersion = userManager.getDbVersion();
        }
    }

    public Widget asWidget() {
        return mainPanel;
    }

    private void createHandlers(){
        tileEvent = new TileEvent() {
            public void execute(String name) {
                userCallbackInterface.avatarClicked(name);
            }
        };
    }
}
