package com.scrollwin.client;

public class MessageContainer {
	private String Message;
	private Integer MessageUserId;
	private Integer MessageSeqId;
	private String MessageUserNick;
	private String MessageDate;
	private String MessageTime;
	private String MessageLocal;
	
	public MessageContainer(Integer seqId, Integer userId, String msg, String date, String time, String local)
	{
		MessageSeqId = seqId;
		MessageUserId = userId;
		Message = msg;
		//MessageUserNick = nick;
		MessageDate = date;
		MessageTime = time;
		MessageLocal = local;
	}
	
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public Integer getMessageUserId() {
		return MessageUserId;
	}
	public void setMessageUserId(Integer messageUserId) {
		MessageUserId = messageUserId;
	}
	public Integer getMessageSeqId() {
		return MessageSeqId;
	}
	public void setMessageSeqId(Integer messageSeqId) {
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
	
}
