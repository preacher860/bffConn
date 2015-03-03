package com.lanouette.app.client.StatsWindow;

import com.google.gwt.user.cellview.client.DataGrid;

public interface DesktopStatsGridResource extends DataGrid.Resources {
    @Source({DataGrid.Style.DEFAULT_CSS, "com/lanouette/app/client/resource/styles/statsGrid.css"})
    TableStyle dataGridStyle();

    interface TableStyle extends DataGrid.Style {
    }
}
