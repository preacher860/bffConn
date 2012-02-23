package com.scrollwin.client;

public class RuntimeData {
	Integer newestSeqId = 0;
	
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
	
}
