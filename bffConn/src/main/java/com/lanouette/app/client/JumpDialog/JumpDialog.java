package com.lanouette.app.client.JumpDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.RuntimeData;
import com.lanouette.app.client.UserCallbackInterface;

public class JumpDialog extends PopupPanel {
    interface MyUiBinder extends UiBinder<Widget, JumpDialog> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @UiField
    Button cancelButton;
    @UiField
    Button jumpButton;
    @UiField
    TextBox jumpTextBox;
    private UserCallbackInterface myUserCallbackInterface;

    public JumpDialog(UserCallbackInterface callbackInterface) {
        super(true);

        myUserCallbackInterface = callbackInterface;

        setAnimationEnabled(true);
        setWidget(uiBinder.createAndBindUi(this));
        setStyleName("popupFrame");

        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        jumpButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                processJump();
            }
        });

        jumpTextBox.addKeyDownHandler(new KeyDownHandler() {
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
        jumpTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if (keyUpEvent.getNativeEvent().getKeyCode() == 13) {
                    keyUpEvent.stopPropagation();
                    processJump();
                }
            }
        });
    }

    public void processJump() {
        try {
            myUserCallbackInterface.jumpEntered(Integer.valueOf(jumpTextBox.getText()));
        } catch (Exception e) {
        }
        hide();
    }

    @Override
    public void show() {
        super.show();

        jumpTextBox.setText("");

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            public void execute() {
                jumpTextBox.setFocus(true);
            }
        });
    }
}
