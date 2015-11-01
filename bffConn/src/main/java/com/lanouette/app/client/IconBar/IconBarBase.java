package com.lanouette.app.client.IconBar;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.lanouette.app.client.UserCallbackInterface;

public abstract class IconBarBase implements IsWidget {
    protected UserCallbackInterface userCallbackInterface;

    @UiField
    FocusPanel panel;

    public Widget asWidget() {
        return panel;
    }

    public abstract void initialize(UserCallbackInterface userCallbackInterface);

    public void setLocal(String locale) {
    }

    public void setCompactView() {
    }

    public void setNormalView() {
    }

    public void setAudioButtonStyle(String style) {
    }

    public void showLocationEntry() {
    }

    public void showJumpEntry() {
    }

    public void audioButtonToggle() {
    }
}
