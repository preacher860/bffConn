package com.scrollwin.client;

import com.smartgwt.client.widgets.Img;

public class OctoObject {
	private Img octopus = new Img("octopus.gif");
	private int myOctoXDir;
	private int myOctoYDir;
	
	OctoObject(int Width, int Height, int Left, int Top, int xDir, int yDir, int Opacity)
	{
		myOctoXDir = xDir;
		myOctoYDir = yDir;
		octopus.setWidth(Width);
		octopus.setHeight(Height);
		octopus.setLeft(Left);
		octopus.setTop(Top);
		octopus.setOpacity(Opacity);
		octopus.hide();
	}
	
	Img getImage() {
		return octopus;
	}

	public void MoveOcto(int canvasWidth, int canvasHeight){
		octopus.setLeft(octopus.getLeft()+myOctoXDir);
		octopus.setTop(octopus.getTop()+myOctoYDir);
		
		if((octopus.getLeft() + octopus.getWidth() > canvasWidth - 10) || (octopus.getLeft() < 10))	
			myOctoXDir = -myOctoXDir;
		if((octopus.getTop() + octopus.getHeight() > canvasHeight - 10) || (octopus.getTop() < 10))
			myOctoYDir = -myOctoYDir;
	}
	
	public void showOcto() {
		octopus.show();
	}
	
	public void hideOcto() {
		octopus.hide();
	}
}
