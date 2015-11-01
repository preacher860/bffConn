package com.lanouette.app.client.IconBar;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.ConsoleLogger;
import com.lanouette.app.client.FunctionPopup.FunctionPopup;
import com.lanouette.app.client.UserCallbackInterface;

public class IconBarMobile extends IconBarBase {
    interface MyUiBinder extends UiBinder<Widget, IconBarMobile> {
    }

    private final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    FlowPanel iconPanel;
    @UiField
    Image menuButton;

    private FunctionPopup popup;

    public IconBarMobile() {
        uiBinder.createAndBindUi(this);
    }

    public void initialize(UserCallbackInterface userCallbackInterface) {
        this.userCallbackInterface = userCallbackInterface;

        installPopup();
    }

    private void installPopup() {
        popup = new FunctionPopup(userCallbackInterface);

        panel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                if (popup.isShowing()) {
                   popup.hide();
                } else {
                    popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = menuButton.getAbsoluteLeft();
                            int top = menuButton.getAbsoluteTop() + menuButton.getOffsetHeight();
                            popup.setPopupPosition(left, top);
                        }
                    });
                }
            }
        });
    }
}
