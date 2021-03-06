package com.lanouette.app.client.FunctionPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.Alerts;
import com.lanouette.app.client.ConsoleLogger;
import com.lanouette.app.client.CookieData;
import com.lanouette.app.client.JumpDialog.JumpDialog;
import com.lanouette.app.client.LocalDialog.LocalDialog;
import com.lanouette.app.client.RuntimeData;
import com.lanouette.app.client.UserCallbackInterface;
import com.lanouette.app.client.VersionInfo;

public class FunctionPopup extends PopupPanel {
    private UserCallbackInterface myUserCallbackInterface;

    interface MyUiBinder extends UiBinder<Widget, FunctionPopup> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private LocalDialog localDialog;
    private JumpDialog jumpDialog;

    @UiField
    VerticalPanel mainPanel;
    @UiField
    FocusPanel fontSizePanel;
    @UiField
    FocusPanel infoPanel;
    @UiField
    FocusPanel localPanel;
    @UiField
    FocusPanel statsPanel;
    @UiField
    FocusPanel alertPanel;
    @UiField
    FocusPanel octoPanel;
    @UiField
    FocusPanel logoutPanel;
    @UiField
    Image audioButton;
    @UiField
    Label alertDescription;
    @UiField
    Label fontSizeLabel;
    @UiField
    Label infoLabel;
    @UiField
    Label localisation;

    public FunctionPopup(UserCallbackInterface callbackInterface) {
        super(false);

        myUserCallbackInterface = callbackInterface;

        doSetAnimationType(this);
        setAnimationEnabled(true);
        setStyleName("popupFrame");

        setWidget(uiBinder.createAndBindUi(this));

        setAudioButtonStyle(CookieData.getInstance().getAudioMode());
        setViewMode(CookieData.getInstance().getViewMode());

        localDialog = new LocalDialog(myUserCallbackInterface);
        jumpDialog = new JumpDialog(myUserCallbackInterface);

        infoLabel.setText("Info (v" + VersionInfo.CURRENT_MAJOR + "." + VersionInfo.CURRENT_VERSION + ")");

        fontSizePanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                toggleViewMode();
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

        octoPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
                jumpDialog.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = Window.getClientWidth() / 2  - offsetWidth / 2;
                        int top = 80;
                        jumpDialog.setPopupPosition(left, top);
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

        alertPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                audioButtonToggle();
                myUserCallbackInterface.alertModeChanged(CookieData.getInstance().getAudioMode());
            }
        });

        logoutPanel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myUserCallbackInterface.logoutClicked();
                hide();
            }
        });
    }

    @Override
    public void show() {
        super.show();

       localisation.setText(RuntimeData.getInstance().getLocale());
    }

    public void toggleViewMode() {
        if(CookieData.getInstance().getViewMode().equals("normal")) {
            CookieData.getInstance().setViewMode("emps");
            setViewMode("emps");
        } else {
            CookieData.getInstance().setViewMode("normal");
            setViewMode("normal");
        }
    }

    public void setViewMode(String mode) {
        Element ele = DOM.getElementById("empsStyle");
        ConsoleLogger.getInstance().log("CSS element: " + ele.toString());

        if(mode.equals("emps")) {
            ele.setAttribute("href", "bffConnMobileEmps.css");
            fontSizeLabel.setText("Mode EMPS");
        } else {
            ele.setAttribute("href", "");
            fontSizeLabel.setText("Mode normal");
        }
    }

    public void audioButtonToggle() {
        if(CookieData.getInstance().getAudioMode().equals("disabled")) {
            CookieData.getInstance().setAudioMode("once");
            setAudioButtonStyle("once");
            Alerts.getInstance().newMessageAlert();
        } else if (CookieData.getInstance().getAudioMode().equals("once")){
            CookieData.getInstance().setAudioMode("every");
            setAudioButtonStyle("every");
        } else if (CookieData.getInstance().getAudioMode().equals("every")){
            CookieData.getInstance().setAudioMode("always");
            setAudioButtonStyle("always");
        } else if (CookieData.getInstance().getAudioMode().equals("always")){
            CookieData.getInstance().setAudioMode("disabled");
            setAudioButtonStyle("disabled");
        }
    }

    private void setAudioButtonStyle(String mode) {
        if(mode.equals("disabled")) {
            audioButton.addStyleName("speakerIconDisabled");
            audioButton.setUrl("images/speaker.png");
            alertDescription.setText("Aucune alerte");
        } else if(mode.equals("once")) {
            audioButton.removeStyleName("speakerIconDisabled");
            audioButton.setUrl("images/speaker.png");
            alertDescription.setText("Alerte unique hors visibilité");
        } else if(mode.equals("every")) {
            audioButton.removeStyleName("speakerIconDisabled");
            audioButton.setUrl("images/speaker_green.png");
            alertDescription.setText("Alertes multiples hors visibilité");
        } else if(mode.equals("always")) {
            audioButton.removeStyleName("speakerIconDisabled");
            audioButton.setUrl("images/speaker_red.png");
            alertDescription.setText("Alertes multiples");
        }
    }
    private native void doSetAnimationType(PopupPanel popup) /*-{
        popup.@com.lanouette.app.client.FunctionPopup.FunctionPopup::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::ROLL_DOWN);
    }-*/;
}
