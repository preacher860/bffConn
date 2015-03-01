package com.lanouette.app.client.LocalDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class LocalDialog extends PopupPanel {
    interface MyUiBinder extends UiBinder<Widget, LocalDialog> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private UserCallbackInterface myUserCallbackInterface;

    @UiField
    Button cancelButton;
    @UiField
    Button acceptButton;
    @UiField
    TextBox localTextBox;

    public LocalDialog(UserCallbackInterface callbackInterface) {
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

        acceptButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                myUserCallbackInterface.localEntered(localTextBox.getText());
                hide();
            }
        });

        localTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if (keyUpEvent.getNativeEvent().getKeyCode() == 13){
                    keyUpEvent.stopPropagation();
                    myUserCallbackInterface.localEntered(localTextBox.getText());
                    hide();
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();

        localTextBox.setText(RuntimeData.getInstance().getLocale());
    }
}
