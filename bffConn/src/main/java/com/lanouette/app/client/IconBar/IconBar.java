package com.lanouette.app.client.IconBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.FunctionPopup.FunctionPopup;
import com.lanouette.app.client.RuntimeData;
import com.lanouette.app.client.UserCallbackInterface;
import com.lanouette.app.client.VersionInfo;

public class IconBar implements IsWidget {
    interface MyUiBinder extends UiBinder<Widget, IconBar> {
    }

    private final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private UserCallbackInterface userCallbackInterface;
    private FunctionPopup popup;

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
    TextBox localBox;

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
    }

    public Widget asWidget() {
        return panel;
    }

    public void setCompactView() {
        normalButton.setVisible(true);
        compactButton.setVisible(false);
        if (RuntimeData.getInstance().isMobile()) {
            popup.setCompactView();
        }
    }

    public void setNormalView() {
        normalButton.setVisible(false);
        compactButton.setVisible(true);
        if (RuntimeData.getInstance().isMobile()) {
            popup.setNormalView();
        }
    }

    public void setLocal(String local) {
        localBox.setValue(local);
    }

    public void showLocationEntry() {
        localBox.setVisible(true);
        localBox.setFocus(true);
    }

    private void setTooltipText() {
        compactButton.setTitle("Mode compact\n(Ctrl-1)");
        normalButton.setTitle("Mode normal\n(Ctrl-2)");
        infoButton.setTitle("Info (Ctrl-I)\nVersion " + VersionInfo.CURRENT_MAJOR + "." + VersionInfo.CURRENT_VERSION);
        localButton.setTitle("Changer localisation\n(Ctrl-L)");
        statsButton.setTitle("Statistiques\n(Ctrl-S)");
        octoButton.setTitle("Octo\n(Ctrl-O)");
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
                userCallbackInterface.octopusClicked();
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
