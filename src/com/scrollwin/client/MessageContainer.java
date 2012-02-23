package com.scrollwin.client;

public class MessageContainer {
	private String Message;
	private Integer MessageUserId;
	private Integer MessageSeqId;
	private String MessageUserNick;
	private String MessageDate;
	
	public MessageContainer(Integer seqId, Integer userId, String msg, String nick, String date)
	{
		MessageSeqId = seqId;
		MessageUserId = userId;
		Message = msg;
		MessageUserNick = nick;
		MessageDate = date;
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
	
}
