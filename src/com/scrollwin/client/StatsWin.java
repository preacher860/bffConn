package com.scrollwin.client;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.corechart.BarChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tile.TileRecord;

public class StatsWin extends Window {
	
	private PieChart pie;
	private BarChart bar;
	private PieChart.PieOptions myPieOptions;
	private HStack hStack = new HStack();
	private HStack buttonStack = new HStack();
	private VStack chartStack = new VStack();
	private ImgButton myMessagesButton = new ImgButton();
	private Label chartLabel = new Label();
	private ListGrid statsGrid;
	private int totalMessages = 0;
	
	public StatsWin(final ArrayList<UserContainer> users) {
		setTitle("Statistiques");
		setAutoSize(true);
		setCanDragResize(false);
		setShowMinimizeButton(false);
		setTop(200);
		setLeft(200);
		setOpacity(100);
	
		statsGrid = new ListGrid() {
			@Override  
	        protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {  
	            if (getFieldName(colNum).equals("messages")) {  
	                return "font-size:14px;font-weight:bold";  
	            } else {  
	                return super.getCellCSSText(record, rowNum, colNum);  
	            }  
	        }
		};
		
		statsGrid.setWidth100();
		statsGrid.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH); 
		statsGrid.setOverflow(Overflow.AUTO);
		statsGrid.setCanResizeFields(false);
		statsGrid.setAutoFitData(Autofit.BOTH);
		statsGrid.setShowAllRecords(true);
		statsGrid.setShowHeaderContextMenu(false);
		statsGrid.setHeaderHeight(28);
		statsGrid.setWrapCells(true);
		statsGrid.setCellHeight(34);
		statsGrid.setScrollbarSize(0); // There has to be a better way...
				
		ListGridField avatarField = new ListGridField("avatarURL", Canvas.imgHTML("people.png"), 80);  
        ListGridField messagesField = new ListGridField("messages", Canvas.imgHTML("msg.png"), 80);  
        messagesField.setAlign(Alignment.CENTER);
        avatarField.setAlign(Alignment.CENTER);
        avatarField.setType(ListGridFieldType.IMAGE);  
        avatarField.setImageSize(30);  
        
        statsGrid.setFields(avatarField, messagesField);
        statsGrid.setData(UserStatsData.getNewRecords(users));

        myMessagesButton.setWidth(32);
        myMessagesButton.setHeight(28);
        myMessagesButton.setMargin(4);
	    myMessagesButton.setShowRollOver(false);
	    myMessagesButton.setShowHover(true);
	    myMessagesButton.setShowDown(false);
	    myMessagesButton.setSrc("msg.png");
	    //myMessagesButton.setPrompt("Messages");
	    //myMessagesButton.setHoverStyle("tooltipStyle");
	    myMessagesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
	    
	    
	    chartLabel.setAutoHeight();
	    chartLabel.setWidth100();
	    chartLabel.setAlign(Alignment.CENTER);
	    chartLabel.setContents("<b>Messages envoyés par usager</b><br>Nombre total: " + computeTotalMessages(users));
	    
	    LayoutSpacer spacer = new LayoutSpacer();
	    spacer.setWidth(5);
	    buttonStack.addMember(myMessagesButton);
	    buttonStack.setHeight(32);
	    chartStack.setWidth(260);
	    chartStack.setAlign(Alignment.CENTER);
	    chartStack.addMember(buttonStack);
	    chartStack.addMember(chartLabel);
	    hStack.addMember(statsGrid);
	    hStack.addMember(spacer);
        hStack.addMember(chartStack);
        

//    	JsArray<Selection> selections = pie.getSelections();
//        Selection selection = selections.get(0);
//        selections.set(0, selection.createRowSelection(1));
//        System.out.println("Selecting row: " + selections.get(0).getRow());
//        pie.setSelections(selections);
        Runnable onLoadCallback = new Runnable() {
		      public void run() {
		    	
		        // Create a pie chart visualization.
		    	myPieOptions = createPieOptions(250, 260);
		        pie = new PieChart(createTable(users), myPieOptions);
		        
		        chartStack.addMember(pie);
		        addItem(hStack);
		        show();
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
		  options.setLegendTextStyle(style);
		  
		  ChartArea chartArea = ChartArea.create();
		  chartArea.setWidth(width);
		  chartArea.setLeft(10);
		  options.setChartArea(chartArea);
	      return options;
	}
	
	private AbstractDataTable createTable(ArrayList<UserContainer> users) {
	    DataTable data = DataTable.create();
	    
	    data.addColumn(ColumnType.STRING, "Nick");
	    data.addColumn(ColumnType.NUMBER, "Messages");
	    data.addRows(users.size());
	    
	    for(int index = 0; index < users.size(); index++){
	    	data.setValue(index,0,users.get(index).getNick());
	    	data.setValue(index,1,users.get(index).getMessages());
	    }
	    return data;
	  }
	
	private int computeTotalMessages(ArrayList<UserContainer> users) {
		int totalMsg = 0;
	    for(UserContainer user:users)
	    	totalMsg += user.getMessages();
	    return totalMsg;
    }
}

class UserStatsData {
	public static UserStatsRecord[] getNewRecords(ArrayList<UserContainer> users) {
		int recIndex = 0;

		UserStatsRecord [] records = new UserStatsRecord[users.size()];
		for(UserContainer currentUser:users) { 
			records[recIndex] = new UserStatsRecord(currentUser.getAvatarURL(), currentUser.getMessages());
			recIndex++;
		}
		
		return records;
	}
}

class UserStatsRecord extends TileRecord {

	public UserStatsRecord() {
	}

	public UserStatsRecord(String avatarURL, int messages) {
		setAvatarURL(avatarURL);
		setMessages(messages);
	}

	public void setAvatarURL(String avatarURL) {
		setAttribute("avatarURL", avatarURL);
	}
	
	public void setMessages(int messages) {
		setAttribute("messages", messages);
	}
}