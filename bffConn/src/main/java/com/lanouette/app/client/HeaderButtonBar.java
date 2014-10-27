package com.lanouette.app.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.lanouette.app.client.FunctionPopup.FunctionPopup;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

public class HeaderButtonBar extends HStack {
    private userCallbackInterface myUserCallbackInterface;
    private ImgButton myLogoutButton = new ImgButton();
    private ImgButton myLocalButton = new ImgButton();
    private ImgButton myStatsButton = new ImgButton();
    private ImgButton myOctopusButton = new ImgButton();
    private ImgButton myInfoButton = new ImgButton();
    private ImgButton myHideButton = new ImgButton();
    private ImgButton myShowButton = new ImgButton();
    private DynamicForm form = new DynamicForm();
    private TextItem localItem = new TextItem();
    private Img octopus = new Img("octopus.gif");
    private FunctionPopup popup;

    public HeaderButtonBar(userCallbackInterface callbackInterface) {
        myUserCallbackInterface = callbackInterface;
    }

    public void initialize() {
        boolean isMobile = RuntimeData.getInstance().isMobile();

        setWidth(200);
        setDefaultLayoutAlign(Alignment.CENTER);
        setDefaultLayoutAlign(VerticalAlignment.BOTTOM);
        setHeight(26);

        //octopus.setWidth(120);
        //octopus.setHeight(76);
        //octopus.hide();

        myLogoutButton.setSize(32);
        myLogoutButton.setSrc("logout.png");
        myLogoutButton.setShowDown(false);
        if (!isMobile) {
            myLogoutButton.setPrompt("Déconnexion<br>(Ctrl-D)");
            myLogoutButton.setHoverStyle("tooltipStyle");
            myLogoutButton.setShowRollOver(true);
            myLogoutButton.setShowHover(true);
            myLogoutButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    myUserCallbackInterface.logoutClicked();
                }
            });
        } else {
            myLogoutButton.setShowRollOver(false);
            myLogoutButton.setShowHover(false);
        }

        myStatsButton.setSize(32);
        myStatsButton.setSrc("stats.png");
        myStatsButton.setShowDown(false);
        if (!isMobile) {
            myStatsButton.setPrompt("Statistiques<br>(Ctrl-S)");
            myStatsButton.setHoverStyle("tooltipStyle");
            myStatsButton.setShowRollOver(true);
            myStatsButton.setShowHover(true);
            myStatsButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    myUserCallbackInterface.statsClicked();
                }
            });
        } else {
            myStatsButton.setShowRollOver(false);
            myStatsButton.setShowHover(false);
        }

        myInfoButton.setSize(32);
        myInfoButton.setSrc("info.png");
        myInfoButton.setShowDown(false);
        if (!isMobile) {
            myInfoButton.setPrompt("Info (Ctrl-I)<br>Version " + VersionInfo.CURRENT_MAJOR + "." + VersionInfo.CURRENT_VERSION);
            myInfoButton.setHoverStyle("tooltipStyle");
            myInfoButton.setShowRollOver(true);
            myInfoButton.setShowHover(true);
            myInfoButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    myUserCallbackInterface.infoClicked();
                }
            });
        } else {
            myInfoButton.setShowRollOver(false);
            myInfoButton.setShowHover(false);
        }

        myOctopusButton.setSize(32);
        myOctopusButton.setSrc("octopus.png");
        myOctopusButton.setShowDown(false);
        if (!isMobile) {
            myOctopusButton.setPrompt("Mode octo<br>(Ctrl-O)");
            myOctopusButton.setHoverStyle("tooltipStyle");
            myOctopusButton.setShowRollOver(true);
            myOctopusButton.setShowHover(true);
            myOctopusButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    myUserCallbackInterface.octopusClicked();
                }
            });
        } else {
            myOctopusButton.setShowRollOver(false);
            myOctopusButton.setShowHover(false);
        }

        myHideButton.setSize(32);
        myHideButton.setSrc("back2r.png");
        myHideButton.setShowDown(false);
        if (!isMobile) {
            myHideButton.setPrompt("Mode compact<br>(Ctrl-1)");
            myHideButton.setHoverStyle("tooltipStyle");
            myHideButton.setShowRollOver(true);
            myHideButton.setShowHover(true);
            myHideButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    myUserCallbackInterface.hideBarClicked();
                    setCompactView();
                }
            });
        } else {
            myHideButton.setShowRollOver(false);
            myHideButton.setShowHover(false);
        }

        myShowButton.setSize(32);
        myShowButton.setSrc("fwd2r.png");
        myShowButton.setShowDown(false);
        myShowButton.hide();
        if (!isMobile) {
            myShowButton.setPrompt("Mode normal<br>(Ctrl-2)");
            myShowButton.setHoverStyle("tooltipStyle");
            myShowButton.setShowRollOver(true);
            myShowButton.setShowHover(true);
            myShowButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    myUserCallbackInterface.showBarClicked();
                    setNormalView();
                }
            });
        } else {
            myShowButton.setShowRollOver(false);
            myShowButton.setShowHover(false);
        }

        myLocalButton.setSize(32);
        myLocalButton.setSrc("local.png");
        myLocalButton.setShowDown(false);
        if (!isMobile) {
            myLocalButton.setPrompt("Changer localisation<br>(Ctrl-L)");
            myLocalButton.setHoverStyle("tooltipStyle");
            myLocalButton.setShowHover(true);
            myLocalButton.setShowRollOver(true);
            myLocalButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    form.show();
                }
            });
        } else {
            myLocalButton.setShowHover(false);
            myLocalButton.setShowRollOver(false);
        }

        localItem.setShowTitle(false);
        localItem.setLength(30);
        localItem.setSelectOnFocus(true);
        localItem.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (event.getKeyName().compareTo("Enter") == 0) {
                    event.cancel();
                    String local = localItem.getValueAsString();
                    if (local == null) local = "";
                    form.hide();
                    myUserCallbackInterface.localEntered(local);
                } else if (event.getKeyName().compareTo("Escape") == 0) {
                    event.cancel();
                    form.hide();
                    myUserCallbackInterface.localEntered(null);
                }
            }
        });
        form.setHeight(30);
        form.setAutoFocus(true);
        form.setFields(localItem);
        form.hide();

        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setWidth(50);

        addMember(myHideButton);
        addMember(myShowButton);
        addMember(myInfoButton);
        addMember(myLocalButton);
        addMember(form);
        addMember(myStatsButton);
        addMember(myOctopusButton);
        addMember(myLogoutButton);
        addMember(spacer);
        //addMember(octopus);

        // Popup menu instanciated only on mobile devices
        if (isMobile) {
            popup = new FunctionPopup(myUserCallbackInterface);
            addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = getAbsoluteLeft();
                            int top = getAbsoluteTop() + getOffsetHeight();
                            popup.setPopupPosition(left, top);
                        }
                    });
                }
            });
        }
    }

    public void setLocal(String local) {
        localItem.setValue(local);
    }

    public void showLocationEntry() {
        form.show();
    }

    public void showOctopus() {
        octopus.show();
    }

    public void hideOctopus() {
        octopus.hide();
    }

    public void setCompactView() {
        myShowButton.show();
        myHideButton.hide();
        if (RuntimeData.getInstance().isMobile()) {
            popup.setCompactView();
        }
    }

    public void setNormalView() {
        myShowButton.hide();
        myHideButton.show();
        if (RuntimeData.getInstance().isMobile()) {
            popup.setNormalView();
        }
    }
}
