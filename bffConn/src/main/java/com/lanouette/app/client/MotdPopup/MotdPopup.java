package com.lanouette.app.client.MotdPopup;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.UserCallbackInterface;
import com.lanouette.app.client.UserManager;
import com.lanouette.app.client.motdData;

public class MotdPopup extends PopupPanel {
    interface MyUiBinder extends UiBinder<Widget, MotdPopup> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private UserCallbackInterface myCallbackInterface;
    private motdData myMotdData;

    @UiField
    Image closeImage;
    @UiField
    FocusPanel closePanel;
    @UiField
    Image deleteImage;
    @UiField
    FocusPanel starPanel;
    @UiField
    FocusPanel deletePanel;
    @UiField
    HTML starersBox;
    @UiField
    Label seqIdLabel;
    @UiField
    Label starNum;

    public MotdPopup(motdData motd,
                     UserCallbackInterface callbackInterface) {
        super(true);

        myCallbackInterface = callbackInterface;
        myMotdData = motd;

        setAnimationEnabled(true);
        setStyleName("popupFrame");

        setWidget(uiBinder.createAndBindUi(this));

        if (myMotdData.stars.length() > 0) {
            String prompt = "";
            ArrayList<String> nickList = UserManager.getInstance().idListToArray(myMotdData.stars);
            for (String nick : nickList)
                prompt += nick + "<br>";
            starersBox.setHTML(prompt.substring(0, prompt.length() - 1)); // Crappy hack to remove trailing newline
            starNum.setText("x" + nickList.size());
        }

        closePanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        deletePanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myCallbackInterface.motdDeleteClicked();
                hide();
            }
        });

        starPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myCallbackInterface.motdStarClicked();
                hide();
            }
        });
    }
}
