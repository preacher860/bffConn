package com.lanouette.app.client.StatsWindow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.lanouette.app.client.RuntimeData;
import com.lanouette.app.client.UserContainer;

public class StatsWin {
    interface MyUiBinder extends UiBinder<Widget, StatsWin> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private final UserStatsDatabase userStatsDatabase;
    private PieChart pie;
    private PieChart.PieOptions myPieOptions;

    //private Label myCloseLabel = new Label("Fermer");

    private int headerButtonSize = 28;
    private int gridColumnWidth = 55;


    @UiField
    DialogBox panel;
    @UiField
    DockLayoutPanel mainPanel;
    @UiField
    SimplePanel chartPanel;
    @UiField
    Image msgButton;
    @UiField
    Image editedButton;
    @UiField
    Image erasedButton;
    @UiField
    Image starTxButton;
    @UiField
    Image starRxButton;
    @UiField
    HTML chartLabel;
    @UiField
    SimplePanel buttonPanel;
    @UiField
    Button closeButton;
    @UiField
    FlowPanel controlPanel;

    @UiField(provided = true)
    CustomDataGrid<UserContainer> dataGrid;

    private Integer tableIconSize;
    public StatsWin(final ArrayList<UserContainer> users) {
        DataGrid.Resources tableRes;

        if (RuntimeData.getInstance().isMobile()) {
            tableRes = GWT.create(MobileStatsGridResource.class);
            tableIconSize = 36;
        } else {
            tableRes = GWT.create(DesktopStatsGridResource.class);
            tableIconSize = 24;
        }

        dataGrid = new CustomDataGrid<UserContainer>(32, tableRes);
        dataGrid.setAlwaysShowScrollBars(false);
        uiBinder.createAndBindUi(this);

        panel.setPopupPosition(200, 200);
        panel.getWidget().getParent().getElement().setAttribute("style", "box-shadow: 4px 4px 5px #888; border: 1px solid #455469;");
        userStatsDatabase = new UserStatsDatabase();
        userStatsDatabase.addDataDisplay(dataGrid);

        closeButton.addStyleName("closeButton");
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                panel.hide();
            }
        });

        if(RuntimeData.getInstance().isMobile()) {
            mainPanel.setWidgetSize(controlPanel, 38);
        }

        userStatsDatabase.setList(users);
        initTable(userStatsDatabase);

        msgButton.addStyleName("statsImageButton");
        editedButton.addStyleName("statsImageButton");
        erasedButton.addStyleName("statsImageButton");
        starTxButton.addStyleName("statsImageButton");
        starRxButton.addStyleName("statsImageButton");
        chartLabel.addStyleName("statsChartLabel");

        msgButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                pie.draw(createTable(users), myPieOptions);
                chartLabel.setHTML("<b>Messages envoyés par usager</b><br>Nombre total: " + computeTotalMessages(users));
            }
        });

        editedButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                pie.draw(createEditedTable(users), myPieOptions);
                chartLabel.setHTML("<b>Messages édités par usager</b><br>Nombre total: " + computeTotalEdited(users));
            }
        });

        erasedButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                pie.draw(createDeletedTable(users), myPieOptions);
                chartLabel.setHTML("<b>Messages supprimés par usager</b><br>Nombre total: " + computeTotalDeleted(users));
            }
        });

        starTxButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                pie.draw(createStarsSentTable(users), myPieOptions);
                chartLabel.setHTML("<b>Étoiles envoyées par usager</b><br>Nombre total: " + computeTotalStarsSent(users));
            }
        });

        starRxButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                pie.draw(createStarsRcvdTable(users), myPieOptions);
                chartLabel.setHTML("<b>Étoiles reçues par usager</b><br>Nombre total: " + computeTotalStarsRcvd(users));
            }
        });

        chartLabel.setHTML("<b>Messages envoyés par usager</b><br>Nombre total: " + computeTotalMessages(users));
        panel.show();

        Runnable onLoadCallback = new Runnable() {
            public void run() {

                // Create a pie chart visualization.
                myPieOptions = createPieOptions(250, 260);
                pie = new PieChart(createTable(users), myPieOptions);

                // chartStack.addMember(pie);
                chartPanel.add(pie);
                panel.show();
            }
        };

        VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);
    }

    private PieChart.PieOptions createPieOptions(int width, int height) {
        PieChart.PieOptions options = PieChart.PieOptions.create();
        options.setWidth(width);
        options.setHeight(height);
        options.set3D(true);

        TextStyle titleStyle = TextStyle.create();
        titleStyle.setFontSize(13);
        options.setTitleTextStyle(titleStyle);

        TextStyle style = TextStyle.create();
        style.setColor("black");
        if (RuntimeData.getInstance().isMobile()) {
            style.setFontSize(13);
        }
        options.setLegendTextStyle(style);

        ChartArea chartArea = ChartArea.create();
        chartArea.setWidth(width);
        chartArea.setLeft(12);
        options.setChartArea(chartArea);

        TextStyle pieStyle = TextStyle.create();
        pieStyle.setColor("black");
        if (RuntimeData.getInstance().isMobile()) {
            pieStyle.setFontSize(13);
        }
        options.setTooltipTextStyle(pieStyle);

        return options;
    }

    private AbstractDataTable createTable(ArrayList<UserContainer> users) {
        DataTable data = DataTable.create();

        data.addColumn(ColumnType.STRING, "Nick");
        data.addColumn(ColumnType.NUMBER, "Messages");
        data.addRows(users.size());

        for (int index = 0; index < users.size(); index++) {
            data.setValue(index, 0, users.get(index).getNick());
            data.setValue(index, 1, users.get(index).getMessages());
        }
        return data;
    }

    private AbstractDataTable createDeletedTable(ArrayList<UserContainer> users) {
        DataTable data = DataTable.create();

        data.addColumn(ColumnType.STRING, "Nick");
        data.addColumn(ColumnType.NUMBER, "Messages");
        data.addRows(users.size());

        for (int index = 0; index < users.size(); index++) {
            data.setValue(index, 0, users.get(index).getNick());
            data.setValue(index, 1, users.get(index).getDeletedMessages());
        }
        return data;
    }

    private AbstractDataTable createEditedTable(ArrayList<UserContainer> users) {
        DataTable data = DataTable.create();

        data.addColumn(ColumnType.STRING, "Nick");
        data.addColumn(ColumnType.NUMBER, "Messages");
        data.addRows(users.size());

        for (int index = 0; index < users.size(); index++) {
            data.setValue(index, 0, users.get(index).getNick());
            data.setValue(index, 1, users.get(index).getEditedMessages());
        }
        return data;
    }


    private AbstractDataTable createStarsRcvdTable(ArrayList<UserContainer> users) {
        DataTable data = DataTable.create();

        data.addColumn(ColumnType.STRING, "Nick");
        data.addColumn(ColumnType.NUMBER, "Messages");
        data.addRows(users.size());

        for (int index = 0; index < users.size(); index++) {
            data.setValue(index, 0, users.get(index).getNick());
            data.setValue(index, 1, users.get(index).getStarsRcvd());
        }
        return data;
    }

    private AbstractDataTable createStarsSentTable(ArrayList<UserContainer> users) {
        DataTable data = DataTable.create();

        data.addColumn(ColumnType.STRING, "Nick");
        data.addColumn(ColumnType.NUMBER, "Messages");
        data.addRows(users.size());

        for (int index = 0; index < users.size(); index++) {
            data.setValue(index, 0, users.get(index).getNick());
            data.setValue(index, 1, users.get(index).getStarsSent());
        }
        return data;
    }

    private int computeTotalMessages(ArrayList<UserContainer> users) {
        int totalMsg = 0;
        for (UserContainer user : users)
            totalMsg += user.getMessages();
        return totalMsg;
    }

    private int computeTotalDeleted(ArrayList<UserContainer> users) {
        int totalMsg = 0;
        for (UserContainer user : users)
            totalMsg += user.getDeletedMessages();
        return totalMsg;
    }

    private int computeTotalEdited(ArrayList<UserContainer> users) {
        int totalMsg = 0;
        for (UserContainer user : users)
            totalMsg += user.getEditedMessages();
        return totalMsg;
    }

    private int computeTotalStarsSent(ArrayList<UserContainer> users) {
        int totalMsg = 0;
        for (UserContainer user : users)
            totalMsg += user.getStarsSent();
        return totalMsg;
    }

    private int computeTotalStarsRcvd(ArrayList<UserContainer> users) {
        int totalMsg = 0;
        for (UserContainer user : users)
            totalMsg += user.getStarsRcvd();
        return totalMsg;
    }

    private void initTable(UserStatsDatabase userStatsDatabase) {
        dataGrid.setAlwaysShowScrollBars(false);
        initUsersColumn();
        initMessagesSentColumn();
        initMessagesEditedColumn();
        initMessagesErasedColumn();
        initMessagesStarsTxColumn();
        initMessagesStarsRxColumn();
        initDummyColumn();
    }

    private void initUsersColumn() {
        Column<UserContainer, SafeHtml> userColumn =
                new Column<UserContainer, SafeHtml>(new SafeHtmlCell()) {
                    @Override
                    public SafeHtml getValue(UserContainer object) {
                        return generateImageHtml(object.getHostAvatarURL(), 30, 30);
                    }
                };

        Header hdr = new Header(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue() {
                return generateImageHtml("images/people.png", tableIconSize, tableIconSize);
            }
        };

        dataGrid.addColumn(userColumn, hdr);
        dataGrid.setColumnWidth(userColumn, 40, Unit.PX);
    }

    private void initMessagesSentColumn() {
        Column<UserContainer, Number> messagesColumn =
                new Column<UserContainer, Number>(new NumberCell()) {
                    @Override
                    public Integer getValue(UserContainer object) {
                        return object.getMessages();
                    }
                };

        Header hdr = new Header(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue() {
                return generateImageHtml("images/msg.png", tableIconSize, tableIconSize);
            }
        };

        messagesColumn.setSortable(true);
        dataGrid.addColumn(messagesColumn, hdr);

        ColumnSortEvent.ListHandler sortHandler = new ColumnSortEvent.ListHandler(userStatsDatabase.getList());
        sortHandler.setComparator(messagesColumn, new Comparator() {
            public int compare(Object o1, Object o2) {
                return comparator(((UserContainer) o1).getMessages(), ((UserContainer) o2).getMessages());
            }
        });


        dataGrid.addColumnSortHandler(sortHandler);
        dataGrid.setColumnWidth(messagesColumn, 55, Unit.PX);
    }

    private void initMessagesEditedColumn() {
        Column<UserContainer, Number> column =
                new Column<UserContainer, Number>(new NumberCell()) {
                    @Override
                    public Integer getValue(UserContainer object) {
                        return object.getEditedMessages();
                    }
                };

        Header hdr = new Header(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue() {
                return generateImageHtml("images/edited.png", tableIconSize, tableIconSize);
            }
        };

        ColumnSortEvent.ListHandler sortHandler = new ColumnSortEvent.ListHandler(userStatsDatabase.getList());
        sortHandler.setComparator(column, new Comparator() {
            public int compare(Object o1, Object o2) {
                return comparator(((UserContainer) o1).getEditedMessages(), ((UserContainer) o2).getEditedMessages());
            }
        });


        column.setSortable(true);
        dataGrid.addColumnSortHandler(sortHandler);
        dataGrid.addColumn(column, hdr);
        dataGrid.setColumnWidth(column, 55, Unit.PX);
    }

    private void initMessagesErasedColumn() {
        Column<UserContainer, Number> column =
                new Column<UserContainer, Number>(new NumberCell()) {
                    @Override
                    public Integer getValue(UserContainer object) {
                        return object.getDeletedMessages();
                    }
                };

        Header hdr = new Header(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue() {
                return generateImageHtml("images/eraser_s.png", tableIconSize, tableIconSize);
            }
        };

        ColumnSortEvent.ListHandler sortHandler = new ColumnSortEvent.ListHandler(userStatsDatabase.getList());
        sortHandler.setComparator(column, new Comparator() {
            public int compare(Object o1, Object o2) {
                return comparator(((UserContainer) o1).getDeletedMessages(), ((UserContainer) o2).getDeletedMessages());
            }
        });
        column.setSortable(true);
        dataGrid.addColumnSortHandler(sortHandler);
        dataGrid.addColumn(column, hdr);
        dataGrid.setColumnWidth(column, 55, Unit.PX);
    }

    private void initMessagesStarsRxColumn() {
        Column<UserContainer, Number> column =
                new Column<UserContainer, Number>(new NumberCell()) {
                    @Override
                    public Integer getValue(UserContainer object) {
                        return object.getStarsRcvd();
                    }
                };

        Header hdr = new Header(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue() {
                return generateImageHtml("images/star_down_20.png", tableIconSize, tableIconSize);
            }
        };

        ColumnSortEvent.ListHandler sortHandler = new ColumnSortEvent.ListHandler(userStatsDatabase.getList());
        sortHandler.setComparator(column, new Comparator() {
            public int compare(Object o1, Object o2) {
                return comparator(((UserContainer) o1).getStarsRcvd(), ((UserContainer) o2).getStarsRcvd());
            }
        });

        column.setSortable(true);
        dataGrid.addColumnSortHandler(sortHandler);
        dataGrid.addColumn(column, hdr);
        dataGrid.setColumnWidth(column, 55, Unit.PX);
    }

    private void initMessagesStarsTxColumn() {
        Column<UserContainer, Number> column =
                new Column<UserContainer, Number>(new NumberCell()) {
                    @Override
                    public Integer getValue(UserContainer object) {
                        return object.getStarsSent();
                    }
                };

        Header hdr = new Header(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue() {
                return generateImageHtml("images/star_up_20.png", tableIconSize, tableIconSize);
            }
        };

        ColumnSortEvent.ListHandler sortHandler = new ColumnSortEvent.ListHandler(userStatsDatabase.getList());
        sortHandler.setComparator(column, new Comparator() {
            public int compare(Object o1, Object o2) {
                return comparator(((UserContainer) o1).getStarsSent(), ((UserContainer) o2).getStarsSent());
            }
        });

        column.setSortable(true);
        dataGrid.addColumnSortHandler(sortHandler);
        dataGrid.addColumn(column, hdr);
        dataGrid.setColumnWidth(column, 55, Unit.PX);
    }

    private void initDummyColumn() {
        Column<UserContainer, SafeHtml> column =
                new Column<UserContainer, SafeHtml>(new SafeHtmlCell()) {
                    @Override
                    public SafeHtml getValue(UserContainer object) {
                        SafeHtmlBuilder builder = new SafeHtmlBuilder();
                        return builder.toSafeHtml();
                    }
                };

        dataGrid.addColumn(column, "");
        //dataGrid.setColumnWidth(column, 20, Unit.PX);
    }

    private SafeHtml generateImageHtml(String url, Integer width, Integer height) {
        Image img = new Image();
        img.setUrl(url);
        img.setWidth(width + "px");
        img.setHeight(height + "px");

        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant(img.getElement().getString());

        return builder.toSafeHtml();
    }

    private Integer comparator(Integer a, Integer b) {
        if (a == b) {
            return 0;
        }

        if (a > b) {
            return 1;
        } else {
            return -1;
        }
    }

    native void consoleLog(String message) /*-{
        console.log("BFF: " + message);
    }-*/;
}
