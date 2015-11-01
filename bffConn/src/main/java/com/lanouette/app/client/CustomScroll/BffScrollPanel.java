package com.lanouette.app.client.CustomScroll;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractNativeScrollbar;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.NativeHorizontalScrollbar;
import com.google.gwt.user.client.ui.NativeVerticalScrollbar;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.lanouette.app.client.CustomScroll.ScrollResourcesContainer.HorizontalResources;
import com.lanouette.app.client.CustomScroll.ScrollResourcesContainer.VerticalResources;

public class BffScrollPanel extends ScrollPanel {
        public BffScrollPanel() {
            super();

//            setHorizontalScrollbar(new NativeHorizontalScrollbar((HorizontalResources) GWT.create(HorizontalResources.class)),
//                    AbstractNativeScrollbar.getNativeScrollbarHeight());
//            setVerticalScrollbar(new NativeVerticalScrollbar((VerticalResources) GWT.create(VerticalResources.class)),
//                    AbstractNativeScrollbar.getNativeScrollbarWidth());
        }

        public void addContainerStyleName(String name) {
            getContainerElement().addClassName(name);
        }
    }
