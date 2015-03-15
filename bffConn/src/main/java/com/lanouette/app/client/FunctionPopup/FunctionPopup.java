package com.lanouette.app.client.FunctionPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.Alerts;
import com.lanouette.app.client.CookieData;
import com.lanouette.app.client.LocalDialog.LocalDialog;
import com.lanouette.app.client.UserCallbackInterface;

public class FunctionPopup extends PopupPanel {
    private UserCallbackInterface myUserCallbackInterface;

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
    FocusPanel alertPanel;
    @UiField
    FocusPanel logoutPanel;
    @UiField
    FocusPanel closePanel;
    @UiField
    Image audioButton;
    @UiField
    Label alertDescription;

    public FunctionPopup(UserCallbackInterface callbackInterface) {
        super(false);

        myUserCallbackInterface = callbackInterface;

        doSetAnimationType(this);
        setAnimationEnabled(true);
        setStyleName("popupFrame");

        setWidget(uiBinder.createAndBindUi(this));

        setAudioButtonStyle(CookieData.getInstance().getAudioMode());

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

    public void setCompactView() {
        modeNormalPanel.setVisible(true);
        modeCompactPanel.setVisible(false);
    }

    public void setNormalView() {
        modeNormalPanel.setVisible(false);
        modeCompactPanel.setVisible(true);
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
