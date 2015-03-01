package com.lanouette.app.client.MessageBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.UserCallbackInterface;
import com.lanouette.app.client.UserContainer;
import com.lanouette.app.client.UserManager;
import com.lanouette.app.client.userTile.TileEvent;
import com.lanouette.app.client.userTile.UserTile;

public class MessageBox implements IsWidget {
    interface MyUiBinder extends UiBinder<Widget, MessageBox> {
    }

    private final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    HTMLPanel mainPanel;
    @UiField
    SimplePanel header;
    @UiField
    HTML infoText;

    public MessageBox() {
        uiBinder.createAndBindUi(this);

        setVisible(false);
    }

    public void setMessage(String message) {
        infoText.setHTML(message);
    }

    public Widget asWidget() {
        return mainPanel;
    }

    public void setVisible(Boolean value) {
        mainPanel.setVisible(value);
    }
}
