package com.lanouette.app.client.CustomScroll;

import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.NativeHorizontalScrollbar;
import com.google.gwt.user.client.ui.NativeVerticalScrollbar;

public class ScrollResourcesContainer {

    public interface ScrollPanelResources extends CustomScrollPanel.Resources
    {
        @Source( { "ScrollPanel.css", CustomScrollPanel.Style.DEFAULT_CSS } )
        CustomScrollPanel.Style customScrollPanelStyle();
    }

    public interface HorizontalResources extends NativeHorizontalScrollbar.Resources
    {
        @Source( { "com/lanouette/app/client/CustomScroll/HorizontalScrollbar.css"} )
        NativeHorizontalScrollbar.Style nativeHorizontalScrollbarStyle();
    }

    public interface VerticalResources extends NativeVerticalScrollbar.Resources
    {
        @Source( { "com/lanouette/app/client/CustomScroll/VerticalScrollbar.css" } )
        NativeVerticalScrollbar.Style nativeVerticalScrollbarStyle();
    }
}