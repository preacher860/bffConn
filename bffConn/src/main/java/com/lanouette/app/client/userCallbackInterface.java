package com.lanouette.app.client;

public interface userCallbackInterface {
	public void avatarClicked(String userNick);
	public void logoutClicked();
	public void statsClicked();
	public void infoClicked();
	public void octopusClicked();
	public void hideBarClicked();
	public void showBarClicked();
	public void localEntered(String local);
	public void scrollTop(int oldest);
	public void messageDisplayComplete();
	public void octopusOnTyped();
	public void octopusOffTyped();
	public void starClicked(int seqId);
	public void deleteClicked(int seqId);
	public void newestUpdated();
	public void userEntry();
	public void editMessageClicked(MessageContainer message);
	public void motdStarClicked();
	public void motdDeleteClicked();
}
