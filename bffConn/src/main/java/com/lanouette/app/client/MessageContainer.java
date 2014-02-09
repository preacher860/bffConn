package com.lanouette.app.client;

public class MessageContainer {
	private String Message;
	private int MessageUserId;
	private int MessageSeqId;
	private String MessageUserNick;
	private String MessageDate;
	private String MessageTime;
	private String MessageLocal;
	private boolean MessageDeleted;
	private int MessageDbVersion;
	private String MessageStars;
	
	public MessageContainer(Integer seqId, Integer userId, String msg, String date, String time, String local, boolean deleted, int dbVersion, String stars)
	{
		MessageSeqId = seqId;
		MessageUserId = userId;
		Message = msg;
		MessageDate = date;
		MessageTime = time;
		MessageLocal = local;
		MessageDeleted = deleted;
		MessageDbVersion = dbVersion;
		MessageStars = stars;
	}
	
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public int getMessageUserId() {
		return MessageUserId;
	}
	public void setMessageUserId(int messageUserId) {
		MessageUserId = messageUserId;
	}
	public int getMessageSeqId() {
		return MessageSeqId;
	}
	public void setMessageSeqId(int messageSeqId) {
		MessageSeqId = messageSeqId;
	}

	public String getMessageUserNick() {
		return MessageUserNick;
	}

	public void setMessageUserNick(String messageUserNick) {
		MessageUserNick = messageUserNick;
	}

	public String getMessageDate() {
		return MessageDate;
	}

	public void setMessageDate(String messageDate) {
		MessageDate = messageDate;
	}

	public String getMessageTime() {
		return MessageTime;
	}

	public void setMessageTime(String messageTime) {
		MessageTime = messageTime;
	}

	public String getMessageLocal() {
		return MessageLocal;
	}

	public void setMessageLocal(String messageLocal) {
		MessageLocal = messageLocal;
	}

	public boolean isMessageDeleted() {
		return MessageDeleted;
	}

	public void setMessageDeleted(boolean messageDeleted) {
		MessageDeleted = messageDeleted;
	}

	public int getMessageDbVersion() {
		return MessageDbVersion;
	}

	public void setMessageDbVersion(int messageDbVersion) {
		MessageDbVersion = messageDbVersion;
	}

	public String getMessageStars() {
		return MessageStars;
	}

	public void setMessageStars(String messageStars) {
		MessageStars = messageStars;
	}
	
}
