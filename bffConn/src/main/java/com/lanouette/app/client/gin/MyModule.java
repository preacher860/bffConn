package com.lanouette.app.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.inject.Singleton;

public class MyModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DockLayoutPanel.class).in(Singleton.class);
    }
}
