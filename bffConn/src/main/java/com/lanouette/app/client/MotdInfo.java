package com.lanouette.app.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lanouette.app.client.MotdPopup.MotdPopup;

public class MotdInfo extends FocusPanel {
    private MotdData myMotd;
    private HTML myMotdInfo = new HTML();
    private Image myStarIcon = new Image("images/stargray.png");
    private Image myStarOverIcon = new Image("images/stargray_Over.png");
    private Image myDeleteIcon = new Image("images/deletegray.png");
    private Image myDeleteOverIcon = new Image("images/deletegray_Over.png");
    private Label myStarLabel = new Label("Étoiler");
    private Label myDeleteLabel = new Label("Effacer");
    private HorizontalPanel myStarStack = new HorizontalPanel();
    private HorizontalPanel myDeleteStack = new HorizontalPanel();
    private HorizontalPanel myIconPane = new HorizontalPanel();
    private VerticalPanel myMainPane = new VerticalPanel();
    private UserCallbackInterface myUserCallbackInterface;
    private boolean starred = false;
    private boolean myIconBarHovered = false;

    public MotdInfo() {

    }

    void initialize(UserCallbackInterface cb) {
        final boolean isMobile = RuntimeData.getInstance().isMobile();
        myUserCallbackInterface = cb;

        buildInterface();

        if (!isMobile) {
            ClickHandler starClickHandler = new ClickHandler() {
                public void onClick(ClickEvent event) {
                    setFocus(false);
                    myUserCallbackInterface.motdStarClicked();
                }
            };
            myStarStack.addDomHandler(starClickHandler, ClickEvent.getType());

            ClickHandler deleteClickHandler = new ClickHandler() {
                public void onClick(ClickEvent event) {
                    setFocus(false);
                    myUserCallbackInterface.motdDeleteClicked();
                }
            };
            myDeleteStack.addDomHandler(deleteClickHandler, ClickEvent.getType());

            MouseOverHandler starMouseOverHandler = new MouseOverHandler() {
                public void onMouseOver(MouseOverEvent event) {
                    if (!starred) {
                        myStarOverIcon.setVisible(true);
                        myStarIcon.setVisible(false);
                    }
                }
            };
            myStarStack.addDomHandler(starMouseOverHandler, MouseOverEvent.getType());

            MouseOutHandler starMouseOutHandler = new MouseOutHandler() {
                public void onMouseOut(MouseOutEvent event) {
                    if (!starred) {
                        myStarOverIcon.setVisible(false);
                        myStarIcon.setVisible(true);
                    }
                }
            };
            myStarStack.addDomHandler(starMouseOutHandler, MouseOutEvent.getType());

            MouseOverHandler deleteMouseOverHandler = new MouseOverHandler() {
                public void onMouseOver(MouseOverEvent event) {
                    myDeleteOverIcon.setVisible(true);
                    myDeleteIcon.setVisible(false);
                }
            };
            myDeleteStack.addDomHandler(deleteMouseOverHandler, MouseOverEvent.getType());

            MouseOutHandler deleteMouseOutHandler = new MouseOutHandler() {
                public void onMouseOut(MouseOutEvent event) {
                    myDeleteOverIcon.setVisible(false);
                    myDeleteIcon.setVisible(true);
                }
            };

            myDeleteStack.addDomHandler(deleteMouseOutHandler, MouseOutEvent.getType());

            MouseOverHandler messageMouseOverHandler = new MouseOverHandler() {
                public void onMouseOver(MouseOverEvent event) {
                    myIconBarHovered = true;
                    myStarStack.setVisible(true);
                    myDeleteStack.setVisible(true);
                }
            };
            addDomHandler(messageMouseOverHandler, MouseOverEvent.getType());

            MouseOutHandler messageMouseOutHandler = new MouseOutHandler() {
                public void onMouseOut(MouseOutEvent event) {
                    myIconBarHovered = false;
                    if (!starred)
                        myStarStack.setVisible(false);
                    myDeleteStack.setVisible(false);
                }
            };
            addDomHandler(messageMouseOutHandler, MouseOutEvent.getType());
        } else {
            addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    openPopup();
                }
            });
        }
    }

    void openPopup() {
        final MotdPopup popup = new MotdPopup(myMotd, myUserCallbackInterface);

        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int right = getAbsoluteLeft() + getOffsetWidth();
                int top = getAbsoluteTop() + getOffsetHeight();
                popup.setPopupPosition(right - offsetWidth, top);
            }
        });
    }

    public void update(MotdData motd, String motd_user) {
        myMotd = motd;

        if (motd.deleted == 0) {
            myMotdInfo.setHTML("Par " + motd_user + " le " + myMotd.date + " à " + myMotd.time);
        } else {
            myMotdInfo.setHTML("");
        }
        setupStarred(motd);
    }

    private void setupStarred(MotdData motd) {
        if (motd.deleted != 0) {
            myIconPane.setVisible(false);
            return;
        } else {
            myIconPane.setVisible(true);
        }

        if (motd.stars.length() > 0) {
            String prompt = "";
            ArrayList<String> nickList = UserManager.getInstance().idListToArray(motd.stars);
            for (String nick : nickList)
                prompt += nick + "\n";
            myStarStack.setTitle(prompt.substring(0, prompt.length() - 1)); // Crappy hack to remove trailing newline
            myStarStack.setVisible(true);
            myStarLabel.setText("x" + nickList.size());
            myStarIcon.setVisible(false);
            myStarOverIcon.setVisible(true);
            starred = true;
        } else {
            myStarStack.setTitle("");
            if (myIconBarHovered)
                myStarStack.setVisible(true);
            else
                myStarStack.setVisible(false);
            myStarLabel.setText("Étoiler");
            myStarIcon.setVisible(true);
            myStarOverIcon.setVisible(false);
            starred = false;
        }
    }

    private void buildInterface() {
        myMotdInfo.setStyleName("motdInfo");
        myIconPane.setStyleName("motdIconPane");

        myStarIcon.setStyleName("motdStarIcon");
        myStarOverIcon.setStyleName("motdStarIcon");
        myStarLabel.setStyleName("starLabel");
        if (!starred) {
            myStarStack.setVisible(false);
            myStarOverIcon.setVisible(false);
        } else {
            myStarOverIcon.setVisible(true);
            myStarIcon.setVisible(false);
        }

        myDeleteIcon.setStyleName("deleteIcon");
        myDeleteOverIcon.setStyleName("deleteIcon");
        myDeleteOverIcon.setVisible(false);
        myDeleteLabel.setStyleName("deleteLabel");
        myDeleteStack.setVisible(false);

        myStarStack.add(myStarIcon);
        myStarStack.add(myStarOverIcon);
        myStarStack.add(myStarLabel);

        myDeleteStack.add(myDeleteIcon);
        myDeleteStack.add(myDeleteOverIcon);
        myDeleteStack.add(myDeleteLabel);

        myIconPane.setStyleName("motdIconPane");
        myIconPane.add(myStarStack);
        myIconPane.add(myDeleteStack);

        myMainPane.add(myMotdInfo);
        myMainPane.add(myIconPane);

        add(myMainPane);
    }
    native void consoleLog(String message) /*-{
        console.log("BFF: " + message);
    }-*/;
}
