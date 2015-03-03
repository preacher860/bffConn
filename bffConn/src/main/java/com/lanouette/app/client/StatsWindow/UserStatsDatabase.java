package com.lanouette.app.client.StatsWindow;

import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.lanouette.app.client.UserContainer;

public class UserStatsDatabase {
    private ListDataProvider<UserContainer> dataProvider = new ListDataProvider<UserContainer>();

    public void addDataDisplay(HasData<UserContainer> display) {
        dataProvider.addDataDisplay(display);
    }

    public void setList(List<UserContainer> entryList) {
        dataProvider.setList(entryList);
        dataProvider.refresh();
    }

    public List<UserContainer> getList() {
        return dataProvider.getList();
    }
}
