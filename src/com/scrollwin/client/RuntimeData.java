package com.scrollwin.client;

public class RuntimeData {
	private Integer newestSeqId = 0;
	private int userId;
	
	
	private static final RuntimeData instance = new RuntimeData();
	
	public static RuntimeData getInstance() {
      return instance;
   }
	 
	public RuntimeData()
	{
		newestSeqId = 0;
	}
	
	public RuntimeData(Integer seqId)
	{
		newestSeqId = seqId;
	}

	public Integer getNewestSeqId() {
		return newestSeqId;
	}

	public void setNewestSeqId(Integer newestSeqId) {
		this.newestSeqId = newestSeqId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
