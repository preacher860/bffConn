package com.lanouette.app.client;

public class RuntimeData {
	private int newestSeqId = 0;
	private int dbVersion = 0;
	private int dbVersionUsers = 0;
	private int dbVersionMotd = 0;
	private int requestedDbVersionUsers = 0;
	private int serverDbVersion = 0;
	private int serverDbVersionUsers = 0;
	private int serverDbVersionMotd = 0;
	private int serverVersion = 0;
	private int serverSeqId = 0;
	private int userId;
	private String sessionId;
	private String motd;
	private String motdDate;
	private String motdTime;
	private int motdUserId;
    private boolean isMobile;
    private boolean isIphone;
    private String locale;
    private boolean saveLocale;

	private static final RuntimeData instance = new RuntimeData();
	
	public static RuntimeData getInstance() {
      return instance;
   }
	public RuntimeData(){
		
	}
	
	public RuntimeData(int seqId, int dbVer, int serverVer)
	{
		newestSeqId = seqId;
		dbVersion = dbVer;
		serverVersion = serverVer;
		motd = "";
	}

	public String getMotd() {
		return motd;
	}
	public void setMotd(String motd) {
		this.motd = motd;
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
	public int getDbVersion() {
		return dbVersion;
	}
	public int getDbVersionUsers() {
		return dbVersionUsers;
	}
	public int getServerVersion() {
		return serverVersion;
	}
	public void setDbVersion(int dbVersion) {
		this.dbVersion = dbVersion;
	}
	public void setDbVersionUsers(int dbVersion) {
		this.dbVersionUsers = dbVersion;
	}
	public void setServerVersion(int serverVersion) {
		this.serverVersion = serverVersion;
	}
	public int getServerSeqId() {
		return serverSeqId;
	}
	public void setServerSeqId(int serverSeqId) {
		this.serverSeqId = serverSeqId;
	}
	public int getServerDbVersion() {
		return serverDbVersion;
	}
	public int getServerDbVersionUsers() {
		return serverDbVersionUsers;
	}
	public void setServerDbVersion(int serverDbVersion) {
		this.serverDbVersion = serverDbVersion;
	}
	public void setServerDbVersionUsers(int serverDbVersionUsers) {
		this.serverDbVersionUsers = serverDbVersionUsers;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getRequestedDbVersionUsers() {
		return requestedDbVersionUsers;
	}
	public void setRequestedDbVersionUsers(int requestedDbVersionUsers) {
		this.requestedDbVersionUsers = requestedDbVersionUsers;
	}
	public String getMotdDate() {
		return motdDate;
	}
	public String getMotdTime() {
		return motdTime;
	}
	public int getMotdUserId() {
		return motdUserId;
	}
	public void setNewestSeqId(int newestSeqId) {
		this.newestSeqId = newestSeqId;
	}
	public void setMotdDate(String motd_date) {
		this.motdDate = motd_date;
	}
	public void setMotdTime(String motd_time) {
		this.motdTime = motd_time;
	}
	public void setMotdUserId(int motd_userid) {
		this.motdUserId = motd_userid;
	}
	public int getDbVersionMotd() {
		return dbVersionMotd;
	}
	public void setDbVersionMotd(int dbVersionMotd) {
		this.dbVersionMotd = dbVersionMotd;
	}
	public int getServerDbVersionMotd() {
		return serverDbVersionMotd;
	}
	public void setServerDbVersionMotd(int serverDbVersionMotd) {
		this.serverDbVersionMotd = serverDbVersionMotd;
	}
    public boolean isMobile() {
        return isMobile;
    }
    public void setMobile(boolean isMobile) {
        this.isMobile = isMobile;
    }
    public boolean isIphone() {
        return isIphone;
    }
    public void setIphone(boolean isIphone) {
        this.isIphone = isIphone;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }
    public boolean isSaveLocale() {
        return saveLocale;
    }
    public void setSaveLocale(boolean saveLocale) {
        this.saveLocale = saveLocale;
    }
}
