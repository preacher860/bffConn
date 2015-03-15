package com.lanouette.app.client;

public interface UserCallbackInterface {
	public void avatarClicked(String userNick);
	public void logoutClicked();
	public void statsClicked();
	public void infoClicked();
	public void octopusClicked();
	public void hideBarClicked();
	public void showBarClicked();
	public void localEntered(String local);
    public void jumpEntered(Integer jumpId);
	public void scrollTop(int oldest);
	public void messageDisplayComplete();
	public void starClicked(int seqId);
	public void deleteClicked(int seqId);
	public void newestUpdated();
	public void userEntry();
	public void editMessageClicked(MessageContainer message);
	public void motdStarClicked();
	public void motdDeleteClicked();
    public void jumpLinkClicked(Integer seqId);
    public void alertModeChanged(String mode);
}
