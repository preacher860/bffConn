package com.scrollwin.client;

import com.smartgwt.client.types.BkgndRepeat;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VStack;

public class octoBox extends VStack{

	private Label titleLabel = new Label();
	private Img octoImage = new Img("octopus.gif",210,120);

	public octoBox() {
		setWidth(216);
		setHeight(150);
		setOverflow(Overflow.HIDDEN);
		setBackgroundColor("#FFFFFF");
		setShowEdges(true);  
		setEdgeSize(3);
		setShowShadow(true);
		setShadowSoftness(3);
		setShadowOffset(4);

		titleLabel.setWidth100();
		titleLabel.setHeight(30);
		titleLabel.setContents("<b>Notre amie l'octo<b>");
		titleLabel.setPadding(5);
		titleLabel.setStyleName("myTitleBox");
		titleLabel.setBackgroundImage("titleback30.png");
		titleLabel.setBackgroundRepeat(BkgndRepeat.REPEAT_X);

		addMember(titleLabel);
		addMember(octoImage);
		hide();
	}
}
