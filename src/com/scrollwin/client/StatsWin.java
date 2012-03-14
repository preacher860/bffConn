package com.scrollwin.client;

import java.util.ArrayList;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
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
	private PieChart.PieOptions myPieOptions;
	private HStack hStack = new HStack();
	private HStack buttonStack = new HStack();
	private VStack chartStack = new VStack();
	private ImgButton myMessagesButton = new ImgButton();
	private ImgButton myDeletedButton = new ImgButton();
	private ImgButton myEditedButton = new ImgButton();
	private ImgButton myStarsSentButton = new ImgButton();
	private ImgButton myStarsRcvdButton = new ImgButton();
	private Label chartLabel = new Label();
	private ListGrid statsGrid;
	
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
	                return "font-size:14px;font-weight:bold";  
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
				
		ListGridField avatarField = new ListGridField("avatarURL", Canvas.imgHTML("people.png"), 50);  
        ListGridField messagesField = new ListGridField("messages", Canvas.imgHTML("msg.png"), 50); 
        ListGridField editedField = new ListGridField("edited", Canvas.imgHTML("edited.png"), 50);
        ListGridField deletedField = new ListGridField("deleted", Canvas.imgHTML("deleted.png"), 50);
        ListGridField starsSentField = new ListGridField("starssent", Canvas.imgHTML("star_up_20.png"), 50);
        ListGridField starsRcvdField = new ListGridField("starsrcvd", Canvas.imgHTML("star_down_20.png"), 50);
        messagesField.setAlign(Alignment.CENTER);
        editedField.setAlign(Alignment.CENTER);
        deletedField.setAlign(Alignment.CENTER);
        starsSentField.setAlign(Alignment.CENTER);
        starsRcvdField.setAlign(Alignment.CENTER);
        avatarField.setAlign(Alignment.CENTER);
        avatarField.setType(ListGridFieldType.IMAGE);  
        avatarField.setImageSize(30);  
        
        statsGrid.setFields(avatarField, messagesField, editedField, deletedField, starsSentField, starsRcvdField);
        statsGrid.setData(UserStatsData.getNewRecords(users));

        myMessagesButton.setWidth(32);
        myMessagesButton.setHeight(28);
        myMessagesButton.setMargin(4);
	    myMessagesButton.setShowRollOver(false);
	    myMessagesButton.setShowHover(true);
	    myMessagesButton.setShowDown(false);
	    myMessagesButton.setSrc("msg.png");
	    myMessagesButton.setPrompt("Messages envoyés");
	    myMessagesButton.setHoverStyle("tooltipStyle");
	    myMessagesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pie.draw(createTable(users), myPieOptions);
				chartLabel.setContents("<b>Messages envoyés par usager</b><br>Nombre total: " + computeTotalMessages(users));
			}
		});
	    
	    myDeletedButton.setWidth(28);
        myDeletedButton.setHeight(28);
        myDeletedButton.setMargin(4);
	    myDeletedButton.setShowRollOver(false);
	    myDeletedButton.setShowHover(true);
	    myDeletedButton.setShowDown(false);
	    myDeletedButton.setSrc("deleted.png");
	    myDeletedButton.setPrompt("Messages supprimés");
	    myDeletedButton.setHoverStyle("tooltipStyle");
	    myDeletedButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pie.draw(createDeletedTable(users), myPieOptions);
				chartLabel.setContents("<b>Messages supprimés par usager</b><br>Nombre total: " + computeTotalDeleted(users));
			}
		});
	    
	    myEditedButton.setWidth(28);
        myEditedButton.setHeight(28);
        myEditedButton.setMargin(4);
	    myEditedButton.setShowRollOver(false);
	    myEditedButton.setShowHover(true);
	    myEditedButton.setShowDown(false);
	    myEditedButton.setSrc("edited.png");
	    myEditedButton.setPrompt("Messages édités");
	    myEditedButton.setHoverStyle("tooltipStyle");
	    myEditedButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pie.draw(createEditedTable(users), myPieOptions);
				chartLabel.setContents("<b>Messages édités par usager</b><br>Nombre total: " + computeTotalEdited(users));
			}
		});
	    
	    myStarsSentButton.setWidth(28);
        myStarsSentButton.setHeight(28);
        myStarsSentButton.setMargin(4);
	    myStarsSentButton.setShowRollOver(false);
	    myStarsSentButton.setShowHover(true);
	    myStarsSentButton.setShowDown(false);
	    myStarsSentButton.setSrc("star_up_20.png");
	    myStarsSentButton.setPrompt("Étoiles envoyées");
	    myStarsSentButton.setHoverStyle("tooltipStyle");
	    myStarsSentButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pie.draw(createStarsSentTable(users), myPieOptions);
				chartLabel.setContents("<b>Étoiles envoyées par usager</b><br>Nombre total: " + computeTotalStarsSent(users));
			}
		});
	    
	    myStarsRcvdButton.setWidth(28);
        myStarsRcvdButton.setHeight(28);
        myStarsRcvdButton.setMargin(4);
	    myStarsRcvdButton.setShowRollOver(false);
	    myStarsRcvdButton.setShowHover(true);
	    myStarsRcvdButton.setShowDown(false);
	    myStarsRcvdButton.setSrc("star_down_20.png");
	    myStarsRcvdButton.setPrompt("Étoiles reçues");
	    myStarsRcvdButton.setHoverStyle("tooltipStyle");
	    myStarsRcvdButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pie.draw(createStarsRcvdTable(users), myPieOptions);
				chartLabel.setContents("<b>Étoiles reçues par usager</b><br>Nombre total: " + computeTotalStarsRcvd(users));
			}
		});
	    
	    chartLabel.setAutoHeight();
	    chartLabel.setWidth100();
	    chartLabel.setPadding(4);
	    chartLabel.setAlign(Alignment.CENTER);
	    chartLabel.setContents("<b>Messages envoyés par usager</b><br>Nombre total: " + computeTotalMessages(users));
	    
	    LayoutSpacer spacer = new LayoutSpacer();
	    spacer.setWidth(5);
	    buttonStack.addMember(myMessagesButton);
	    buttonStack.addMember(myEditedButton);
	    buttonStack.addMember(myDeletedButton);
	    buttonStack.addMember(myStarsSentButton);
	    buttonStack.addMember(myStarsRcvdButton);
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
		  chartArea.setLeft(12);
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
	
	private AbstractDataTable createDeletedTable(ArrayList<UserContainer> users) {
	    DataTable data = DataTable.create();
	    
	    data.addColumn(ColumnType.STRING, "Nick");
	    data.addColumn(ColumnType.NUMBER, "Messages");
	    data.addRows(users.size());
	    
	    for(int index = 0; index < users.size(); index++){
	    	data.setValue(index,0,users.get(index).getNick());
	    	data.setValue(index,1,users.get(index).getDeletedMessages());
	    }
	    return data;
	  }

	private AbstractDataTable createEditedTable(ArrayList<UserContainer> users) {
	    DataTable data = DataTable.create();
	    
	    data.addColumn(ColumnType.STRING, "Nick");
	    data.addColumn(ColumnType.NUMBER, "Messages");
	    data.addRows(users.size());
	    
	    for(int index = 0; index < users.size(); index++){
	    	data.setValue(index,0,users.get(index).getNick());
	    	data.setValue(index,1,users.get(index).getEditedMessages());
	    }
	    return data;
	  }

	
	private AbstractDataTable createStarsRcvdTable(ArrayList<UserContainer> users) {
	    DataTable data = DataTable.create();
	    
	    data.addColumn(ColumnType.STRING, "Nick");
	    data.addColumn(ColumnType.NUMBER, "Messages");
	    data.addRows(users.size());
	    
	    for(int index = 0; index < users.size(); index++){
	    	data.setValue(index,0,users.get(index).getNick());
	    	data.setValue(index,1,users.get(index).getStarsRcvd());
	    }
	    return data;
	  }
	
	private AbstractDataTable createStarsSentTable(ArrayList<UserContainer> users) {
	    DataTable data = DataTable.create();
	    
	    data.addColumn(ColumnType.STRING, "Nick");
	    data.addColumn(ColumnType.NUMBER, "Messages");
	    data.addRows(users.size());
	    
	    for(int index = 0; index < users.size(); index++){
	    	data.setValue(index,0,users.get(index).getNick());
	    	data.setValue(index,1,users.get(index).getStarsSent());
	    }
	    return data;
	  }
	
	private int computeTotalMessages(ArrayList<UserContainer> users) {
		int totalMsg = 0;
	    for(UserContainer user:users)
	    	totalMsg += user.getMessages();
	    return totalMsg;
    }
	
	private int computeTotalDeleted(ArrayList<UserContainer> users) {
		int totalMsg = 0;
	    for(UserContainer user:users)
	    	totalMsg += user.getDeletedMessages();
	    return totalMsg;
    }
	
	private int computeTotalEdited(ArrayList<UserContainer> users) {
		int totalMsg = 0;
	    for(UserContainer user:users)
	    	totalMsg += user.getEditedMessages();
	    return totalMsg;
    }
	
	private int computeTotalStarsSent(ArrayList<UserContainer> users) {
		int totalMsg = 0;
	    for(UserContainer user:users)
	    	totalMsg += user.getStarsSent();
	    return totalMsg;
    }
	
	private int computeTotalStarsRcvd(ArrayList<UserContainer> users) {
		int totalMsg = 0;
	    for(UserContainer user:users)
	    	totalMsg += user.getStarsRcvd();
	    return totalMsg;
    }
}

class UserStatsData {
	public static UserStatsRecord[] getNewRecords(ArrayList<UserContainer> users) {
		int recIndex = 0;

		UserStatsRecord [] records = new UserStatsRecord[users.size()];
		for(UserContainer currentUser:users) { 
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