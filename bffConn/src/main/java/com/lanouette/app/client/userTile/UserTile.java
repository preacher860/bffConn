package com.lanouette.app.client.userTile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UserTile implements IsWidget{
    interface MyUiBinder extends UiBinder<Widget, UserTile> {
    }

    private final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private final TileEvent tileEvent;

    @UiField
    FocusPanel panel;
    @UiField
    Image userPicture;
    @UiField
    Label userName;

    public UserTile(String url, String text, TileEvent tileEvent) {
        uiBinder.createAndBindUi(this);

        userPicture.setUrl(url);
        userName.setText(text);

        this.tileEvent = tileEvent;

        panel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                clickCallback();
            }
        });

    }

    public void clickCallback() {
        tileEvent.execute(getName());
    }

    public void setName(String name) {
        userName.setText(name);
    }

    public void setImageUrl(String url) {
        userPicture.setUrl(url);
    }

    public String getName() {
        return userName.getText();
    }

    public String getImageUrl() {
        return userPicture.getUrl();
    }

    public Widget asWidget() {
        return panel;
    }
}
