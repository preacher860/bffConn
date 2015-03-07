package com.lanouette.app.client.StatsWindow;

import com.google.gwt.user.cellview.client.DataGrid;

public interface MobileStatsGridResource extends DataGrid.Resources {
    @Source({DataGrid.Style.DEFAULT_CSS, "com/lanouette/app/client/resource/styles/statsGridMobile.css"})
    TableStyle dataGridStyle();

    interface TableStyle extends DataGrid.Style {
    }
}
