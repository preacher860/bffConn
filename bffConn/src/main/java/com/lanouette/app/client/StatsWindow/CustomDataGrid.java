package com.lanouette.app.client.StatsWindow;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CustomDataGrid<T> extends DataGrid<T> {

    public CustomDataGrid(int rows, Resources resources) {
       super(rows, resources);

        HeaderPanel header = (HeaderPanel) getWidget();
        ScrollPanel panel = (ScrollPanel) header.getContentWidget();
        panel.setAlwaysShowScrollBars(false);
    }
}
