package com.lanouette.app.client.FunctionPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.LocalDialog.LocalDialog;
import com.lanouette.app.client.userCallbackInterface;

public class FunctionPopup extends PopupPanel {
    private userCallbackInterface myUserCallbackInterface;

    interface MyUiBinder extends UiBinder<Widget, FunctionPopup> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private LocalDialog localDialog;

    @UiField
    VerticalPanel mainPanel;
    @UiField
    FocusPanel modeNormalPanel;
    @UiField
    FocusPanel modeCompactPanel;
    @UiField
    FocusPanel infoPanel;
    @UiField
    FocusPanel localPanel;
    @UiField
    FocusPanel statsPanel;
    @UiField
    FocusPanel logoutPanel;
    @UiField
    FocusPanel closePanel;

    public FunctionPopup(userCallbackInterface callbackInterface) {
        super(false);

        myUserCallbackInterface = callbackInterface;

        doSetAnimationType(this);
        setAnimationEnabled(true);
        setStyleName("popupFrame");

        setWidget(uiBinder.createAndBindUi(this));

        localDialog = new LocalDialog(myUserCallbackInterface);

        closePanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        modeNormalPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myUserCallbackInterface.showBarClicked();
                setNormalView();
                hide();
            }
        });

        modeCompactPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myUserCallbackInterface.hideBarClicked();
                setCompactView();
                hide();
            }
        });

        infoPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myUserCallbackInterface.infoClicked();
                hide();
            }
        });

        localPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
                localDialog.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = Window.getClientWidth() / 2  - offsetWidth / 2;
                        int top = 80;
                        localDialog.setPopupPosition(left, top);
                    }
                });
            }
        });

        statsPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myUserCallbackInterface.statsClicked();
                hide();
            }
        });

        logoutPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myUserCallbackInterface.logoutClicked();
                hide();
            }
        });
    }

    public void setCompactView() {
        modeNormalPanel.setVisible(true);
        modeCompactPanel.setVisible(false);
    }

    public void setNormalView() {
        modeNormalPanel.setVisible(false);
        modeCompactPanel.setVisible(true);
    }

    private native void doSetAnimationType(PopupPanel popup) /*-{
        popup.@com.lanouette.app.client.FunctionPopup.FunctionPopup::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::ROLL_DOWN);
    }-*/;
}
