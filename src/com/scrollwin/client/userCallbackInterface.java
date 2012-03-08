package com.scrollwin.client;

public interface userCallbackInterface {
	public void avatarClicked(String userNick);
	public void logoutClicked();
	public void statsClicked();
	public void octopusClicked();
	public void localEntered(String local);
	public void scrollTop(int oldest);
	public void messageDisplayComplete();
	public void octopusOnTyped();
	public void octopusOffTyped();
	public void starClicked(int seqId);
	public void deleteClicked(int seqId);
}
