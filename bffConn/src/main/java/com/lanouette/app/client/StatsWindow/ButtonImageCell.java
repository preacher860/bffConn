package com.lanouette.app.client.StatsWindow;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;

public class ButtonImageCell extends ButtonCell {
    private String tooltipText = "";

    public ButtonImageCell(String text) {
        super();

        tooltipText = text;
    }

    public ButtonImageCell() {
        super();
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context,
                       String value, SafeHtmlBuilder sb) {
        Image image = new Image(value);
        image.setHeight("32px");
        image.setWidth("32px");
        image.setTitle(tooltipText);
        sb.appendHtmlConstant(image.toString());
    }
}