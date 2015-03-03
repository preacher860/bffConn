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
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
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
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tile.TileRecord;

public class StatsWin {
    interface MyUiBinder extends UiBinder<Widget, StatsWin> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private PieChart pie;
    private PieChart.PieOptions myPieOptions;
    private VStack vStack = new VStack();
    private HStack hStack = new HStack();
    private HLayout bottomStack = new HLayout();
    private HStack buttonStack = new HStack();
    private VStack chartStack = new VStack();
    private ImgButton myMessagesButton = new ImgButton();
    private ImgButton myDeletedButton = new ImgButton();
    private ImgButton myEditedButton = new ImgButton();
    private ImgButton myStarsSentButton = new ImgButton();
    private ImgButton myStarsRcvdButton = new ImgButton();
    private ImgButton myCloseButton = new ImgButton();
    private Label myCloseLabel = new Label("Fermer");
    private ListGrid statsGrid;
    private int headerButtonSize = 28;
    private int gridColumnWidth = 55;
    // private DataGrid dataGrid;

    @UiField
    PopupPanel panel;
    @UiField
    SimplePanel contentsPanel;
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

    @UiField(provided = true)
    CustomDataGrid<UserContainer> dataGrid;

    public StatsWin(final ArrayList<UserContainer> users) {
        DataGrid.Resources tableRes = GWT.create(DesktopStatsGridResource.class);
        dataGrid = new CustomDataGrid<UserContainer>(32, tableRes);
        dataGrid.setAlwaysShowScrollBars(false);
        uiBinder.createAndBindUi(this);

        //panel.setTitle("Statistiques");
        //setAutoSize(true);
        //setCanDragResize(false);
        //setShowMinimizeButton(false);
        //setTop(200);
        //setLeft(200);
        //setOpacity(100);
        panel.setPopupPosition(200, 200);

        UserStatsDatabase userStatsDatabase = new UserStatsDatabase();
        initTable(userStatsDatabase);
        userStatsDatabase.addDataDisplay(dataGrid);


        List<UserContainer> fakeUsers = new ArrayList<UserContainer>();
        for (int i = 0; i < 5; i++) {
            for (UserContainer user : users) {
                fakeUsers.add(user);
            }
        }
        userStatsDatabase.setList(fakeUsers);

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

        /*
        if (RuntimeData.getInstance().isMobile()) {
            headerButtonSize = 48;
            gridColumnWidth = 70;
        }

        statsGrid = new ListGrid() {
            @Override
            protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
                if (RuntimeData.getInstance().isMobile()) {
                    return "font-size:18px;font-weight:bold;vertical-align: middle;border-collapse:separate";
                } else {
                    return "font-size:14px;font-weight:bold;vertical-align: middle;border-collapse:separate";
                }
            }
        };




        myCloseButton.setWidth(headerButtonSize);
        myCloseButton.setHeight(headerButtonSize);
        myCloseButton.setMargin(4);
        myCloseButton.setShowRollOver(false);
        myCloseButton.setShowHover(true);
        myCloseButton.setShowDown(false);
        myCloseButton.setSrc("close.png");
        myCloseButton.setPrompt("Fermer");
        myCloseButton.setHoverStyle("tooltipStyle");


        myCloseLabel.setStyleName("statsCloseLabel");
        myCloseLabel.setAutoWidth();
        myCloseLabel.setTop(6);

          */
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
        initMessagesSentColumn(userStatsDatabase);
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
                        return generateImageHtml(object.getAvatarURL(), 30, 30);
                    }
                };

        Header hdr = new Header(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue() {
                return generateImageHtml("images/people.png", 24, 24);
            }
        };

        dataGrid.addColumn(userColumn, hdr);
        dataGrid.setColumnWidth(userColumn, 40, Unit.PX);
    }

    private void initMessagesSentColumn(UserStatsDatabase userStatsDatabase) {
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
                return generateImageHtml("images/msg.png", 24, 24);
            }
        };

        dataGrid.addColumn(messagesColumn, hdr);

        messagesColumn.setSortable(true);
        ListHandler<UserContainer> handler = new ListHandler<UserContainer>(userStatsDatabase.getList());
        handler.setComparator(messagesColumn, new Comparator<UserContainer>() {
            public int compare(UserContainer o1, UserContainer o2) {
                return 0;
            }
        });

        ColumnSortEvent.AsyncHandler sortHandler= new ColumnSortEvent.AsyncHandler(dataGrid) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                consoleLog("ColumnSort");
            }
        };
        dataGrid.addColumnSortHandler(sortHandler);
        ColumnSortList columnSortList = dataGrid.getColumnSortList();
        columnSortList.push(new ColumnSortList.ColumnSortInfo(messagesColumn, true));
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
                return generateImageHtml("images/edited.png", 24, 24);
            }
        };

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
                return generateImageHtml("images/eraser_s.png", 24, 24);
            }
        };

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
                return generateImageHtml("images/star_down_20.png", 24, 24);
            }
        };

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
                return generateImageHtml("images/star_up_20.png", 24, 24);
            }
        };

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

    native void consoleLog(String message) /*-{
        console.log("BFF: " + message);
    }-*/;
}

class UserStatsData {
    public static UserStatsRecord[] getNewRecords(ArrayList<UserContainer> users) {
        int recIndex = 0;

        UserStatsRecord[] records = new UserStatsRecord[users.size()];
        for (UserContainer currentUser : users) {
            records[recIndex] = new UserStatsRecord(currentUser.getAvatarURL(),
                    currentUser.getMessages(),
                    currentUser.getEditedMessages(),
                    currentUser.getDeletedMessages(),
                    currentUser.getStarsSent(),
                    currentUser.getStarsRcvd());
            recIndex++;
        }
        return records;
    }
}

class UserStatsRecord extends TileRecord {

    public UserStatsRecord() {
    }

    public UserStatsRecord(String avatarURL, int messages, int edited, int deleted, int starsSent, int starsRcvd) {
        setAvatarURL(avatarURL);
        setMessages(messages);
        setEdited(edited);
        setDeleted(deleted);
        setStarsSent(starsSent);
        setStarsRcvd(starsRcvd);
    }

    public void setAvatarURL(String avatarURL) {
        setAttribute("avatarURL", avatarURL);
    }

    public void setMessages(int messages) {
        setAttribute("messages", messages);
    }

    public void setEdited(int deleted) {
        setAttribute("edited", deleted);
    }

    public void setDeleted(int deleted) {
        setAttribute("deleted", deleted);
    }

    public void setStarsRcvd(int stars) {
        setAttribute("starsrcvd", stars);
    }

    public void setStarsSent(int stars) {
        setAttribute("starssent", stars);
    }


}