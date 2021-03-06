package com.lanouette.app.client.IconBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.Alerts;
import com.lanouette.app.client.ConsoleLogger;
import com.lanouette.app.client.CookieData;
import com.lanouette.app.client.FunctionPopup.FunctionPopup;
import com.lanouette.app.client.RuntimeData;
import com.lanouette.app.client.UserCallbackInterface;
import com.lanouette.app.client.VersionInfo;

public class IconBar extends IconBarBase {
    interface MyUiBinder extends UiBinder<Widget, IconBar> {
    }

    private final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @UiField
    FocusPanel panel;
    @UiField
    FlowPanel iconPanel;
    @UiField
    Image compactButton;
    @UiField
    Image normalButton;
    @UiField
    Image infoButton;
    @UiField
    Image localButton;
    @UiField
    Image statsButton;
    @UiField
    Image octoButton;
    @UiField
    Image logoutButton;
    @UiField
    Image audioButton;
    @UiField
    TextBox localBox;
    @UiField
    TextBox jumpBox;
    private FunctionPopup popup;

    public IconBar() {
        uiBinder.createAndBindUi(this);
    }

    public void initialize(UserCallbackInterface userCallbackInterface) {
        this.userCallbackInterface = userCallbackInterface;

        // Popup menu instanciated only on mobile devices
        if (RuntimeData.getInstance().isMobile()) {
            installPopup();
        } else {
            installHandlers();
            setTooltipText();
        }

        setAudioButtonStyle(CookieData.getInstance().getAudioMode());
    }

    public Widget asWidget() {
        return panel;
    }

    public void setCompactView() {
        normalButton.setVisible(true);
        compactButton.setVisible(false);
    }

    public void setNormalView() {
        normalButton.setVisible(false);
        compactButton.setVisible(true);
    }

    public void setLocal(String local) {
        localBox.setValue(local);
    }

    public void showLocationEntry() {
        localBox.setVisible(true);
        localBox.setFocus(true);
    }

    public void showJumpEntry() {
        jumpBox.setValue("");
        jumpBox.setVisible(true);
        jumpBox.setFocus(true);
    }

    private void setTooltipText() {
        compactButton.setTitle("Mode compact\n(Ctrl-1)");
        normalButton.setTitle("Mode normal\n(Ctrl-2)");
        infoButton.setTitle("Info (Ctrl-I)\nVersion " + VersionInfo.CURRENT_MAJOR + "." + VersionInfo.CURRENT_VERSION);
        localButton.setTitle("Changer localisation\n(Ctrl-L)");
        statsButton.setTitle("Statistiques\n(Ctrl-S)");
        octoButton.setTitle("Saut vers message\n(Ctrl-O)");
        logoutButton.setTitle("Déconnexion\n(Ctrl-D)");
    }

    private void installHandlers() {
        compactButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                userCallbackInterface.hideBarClicked();
                setCompactView();
            }
        });

        normalButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                userCallbackInterface.showBarClicked();
                setNormalView();
            }
        });

        infoButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                userCallbackInterface.infoClicked();
            }
        });

        localButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                localBox.setVisible(true);
                localBox.setFocus(true);
            }
        });

        statsButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                userCallbackInterface.statsClicked();
            }
        });

        octoButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                jumpBox.setValue("");
                jumpBox.setVisible(true);
                jumpBox.setFocus(true);
            }
        });

        audioButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                audioButtonToggle();
            }
        });

        logoutButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                userCallbackInterface.logoutClicked();
            }
        });

        localBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    event.stopPropagation();
                    String local = localBox.getValue();
                    if (local == null) local = "";
                    localBox.setVisible(false);
                    userCallbackInterface.localEntered(local);
                } else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                    event.stopPropagation();
                    localBox.setVisible(false);
                    userCallbackInterface.localEntered(null);
                }
            }
        });

        jumpBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    event.stopPropagation();
                    String jumpId = jumpBox.getValue();
                    if (jumpId == null) jumpId = "";
                    jumpBox.setVisible(false);

                    Integer jumpSeq = 0;
                    try {
                        jumpSeq = Integer.valueOf(jumpId);
                    } catch (Exception e) {
                        ConsoleLogger.getInstance().log("Invalid jump code " + jumpBox.getValue());
                    }
                    userCallbackInterface.jumpEntered(jumpSeq);
                } else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                    event.stopPropagation();
                    jumpBox.setVisible(false);
                }
            }
        });

        jumpBox.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                Integer keyCode = event.getNativeEvent().getKeyCode();
                if (event.isShiftKeyDown() || event.isAltKeyDown() || event.isControlKeyDown() ||
                        (!(keyCode >= KeyCodes.KEY_ZERO && keyCode <= KeyCodes.KEY_NINE) &&
                                !(keyCode >= 96 && keyCode <= 105) &&  // Keypad digits
                                (keyCode != KeyCodes.KEY_DELETE) &&
                                (keyCode != KeyCodes.KEY_BACKSPACE) &&
                                (keyCode != KeyCodes.KEY_HOME) &&
                                (keyCode != KeyCodes.KEY_END) &&
                                (keyCode != KeyCodes.KEY_LEFT) &&
                                (keyCode != KeyCodes.KEY_RIGHT) &&
                                (keyCode != KeyCodes.KEY_TAB))) {
                    event.getNativeEvent().preventDefault();
                }
            }
        });
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

    public void setAudioButtonStyle(String mode) {
        if(mode.equals("disabled")) {
            audioButton.addStyleName("speakerIconDisabled");
            audioButton.setUrl("images/speaker.png");
            audioButton.setTitle("Aucune alerte\nCTRL-A");
        } else if(mode.equals("once")) {
            audioButton.removeStyleName("speakerIconDisabled");
            audioButton.setUrl("images/speaker.png");
            audioButton.setTitle("Alerte unique hors visibilité\nCTRL-A");
        } else if(mode.equals("every")) {
            audioButton.removeStyleName("speakerIconDisabled");
            audioButton.setUrl("images/speaker_green.png");
            audioButton.setTitle("Alertes multiples hors visibilité\nCTRL-A");
        } else if(mode.equals("always")) {
            audioButton.removeStyleName("speakerIconDisabled");
            audioButton.setUrl("images/speaker_red.png");
            audioButton.setTitle("Alertes multiples\nCTRL-A");
        }
    }

    private void installPopup() {
        popup = new FunctionPopup(userCallbackInterface);

        panel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = panel.getAbsoluteLeft();
                        int top = panel.getAbsoluteTop() + panel.getOffsetHeight();
                        popup.setPopupPosition(left, top);
                    }
                });
            }
        });
    }
}
