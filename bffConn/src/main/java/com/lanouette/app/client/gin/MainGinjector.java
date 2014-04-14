package com.lanouette.app.client.gin;

import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public interface MainGinjector extends Ginjector {
    DockLayoutPanel getMainPanel();
}
