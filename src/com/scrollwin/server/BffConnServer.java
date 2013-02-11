package com.scrollwin.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

//import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.scrollwin.client.VersionInfo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BffConnServer extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static int ACTIVITY_TIMEOUT = 60;
	private final static int SESSION_TIMEOUT  = 7 * 24 * 3600; // 7 days for demanding users
	private final static int MOTD_TIMEOUT  = 24 * 3600; 	   // 24h timeout on MOTD
	
	private Integer myVersion = VersionInfo.CURRENT_VERSION;
	private ArrayList<srvUserContainer> userList = new ArrayList<srvUserContainer>();
	private Timer myRefreshTimer;
	private ServerConfig myServerConfig = new ServerConfig();
  
	 
	public void init(ServletConfig config) throws ServletException { 
		System.out.println("** BffConnServer servlet started");
		//System.out.println("JB: " + BCrypt.hashpw("jb1234", BCrypt.gensalt()));
		try {
			loadConfig();
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadUserInfo();
		updateUserMessageCount();
		myRefreshTimer = new Timer();
		myRefreshTimer.scheduleAtFixedRate(new userCheckTask(), 0 ,5000);
	}
  private void loadConfig() throws InvalidFileFormatException, IOException {
	  Ini prefs = new Ini(new File("/etc/bffconn.ini"));
	  myServerConfig.setDatabaseName(prefs.get("dbconfig", "database"));
	  myServerConfig.setDatabaseUser(prefs.get("dbconfig", "database_user"));
	  myServerConfig.setDatabasePwd(prefs.get("dbconfig", "database_pwd"));
	  myServerConfig.setSqlServerIp(prefs.get("sqlserver", "server_ip"));
	  myServerConfig.setSqlServerPort(prefs.get("sqlserver", "server_port"));
  }
public BffConnServer(){

  }

   
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
	    
	  resp.setContentType("application/json;charset=UTF-8");
	  PrintWriter out = resp.getWriter();
	  
	  String requestMode = req.getParameter("request_mode");
	  String userMessage = req.getParameter("message_text");
	  String firstNewMessage = req.getParameter("start_point");
	  String lastNewMessage = req.getParameter("end_point");
	  String userId = req.getParameter("user_id");
	  String sessionId = req.getParameter("session_id");
	  String login = req.getParameter("login");
	  String password = req.getParameter("password");
	  String local = req.getParameter("local");
	  String message_id = req.getParameter("message_id");
	  String db_version = req.getParameter("db_version");

	  // Login can be performed without active session ID
	  if((requestMode != null) && requestMode.contentEquals("perform_login"))
	  {
		  userLogin(req, out, login, password, local);
	  }
	  
	  // Session ID validity check can be performed without userId
	  if((requestMode != null) && (sessionId != null) && requestMode.contentEquals("get_server_session_valid"))
	  {
		  int foundUserId = getSessionAssociatedUser(sessionId);
		  if(foundUserId < 0)
			  sendSessionInvalid(out);
		  else
			  sendSessionValid(out, sessionId, foundUserId, getSessionAssociatedLocal(sessionId));
	  }
	  
	  // All commands other than login require a valid userId / sessionId combo
	  if ((requestMode != null) && (userId != null) && (sessionId != null))
	  {
		  int sessionUserId = getSessionAssociatedUser(sessionId);
		  if( ((sessionUserId < 0) || (sessionUserId != Integer.valueOf(userId))) 
				  && sessionId.compareTo("debugSessionId") != 0) // TBD: Remove this hack when protocol is debugged
		  {
			  System.out.println("Session " + sessionId + "didn't match user " + userId + ".  Returned id: " + sessionUserId);
			  resp.sendError(403);
		  } else {
			  // userId / sessionId match ok, fulfill request
			  if(requestMode.contentEquals("get_messages")) {
				  if(userMessage != null)
					  processNewMessage(userMessage, Integer.parseInt(userId), sessionId);

				  if(firstNewMessage != null){
					  Integer lastMsg;
					  if(lastNewMessage != null)
						  lastMsg = Integer.parseInt(lastNewMessage);
					  else
						  lastMsg = 0;
					  getNewMessages(Integer.parseInt(firstNewMessage), lastMsg, out);
				  }
			  }

			  if(requestMode.contentEquals("get_messages_by_db")) {
				  if(db_version != null)
					  getMessagesByDb(out, Integer.parseInt(db_version));
			  }
			  
			  if(requestMode.contentEquals("get_runtime_data")) {
				  getRuntimeData(out);
				  
				  // Mark this user as active: reset its online timeout and refresh its session
				  resetUserTimeout(Integer.parseInt(userId));
				  touchSession(sessionId);
			  }

			  if(requestMode.contentEquals("get_user_info")) {
				  getUserInfo(out);
			  }

			  if(requestMode.contentEquals("get_server_version")) {
				  getServerVersion(out);
			  }
			  
			  if(requestMode.contentEquals("perform_logout")) {
				  deleteSession(sessionId);
			  }
			  
			  if (requestMode.contentEquals("set_local")) {
				  updateSessionLocal(sessionId, local);
			  }
			  
			  if (requestMode.contentEquals("delete_message"))
			  {
				  if(message_id != null)
					  deleteMessage(out, message_id);
			  }
			  
			  if (requestMode.contentEquals("star_message"))
			  {
				  if(message_id != null)
					  starMessage(out, message_id, userId);
			  }
			  
			  if (requestMode.contentEquals("edit_message"))
			  {
				  if((message_id != null) && (userMessage != null))
					  editMessage(out, message_id, userMessage);
			  }
			  
			  if(requestMode.contentEquals("get_motd")) {
				  getMotd(out);
			  }
			  
			  if(requestMode.contentEquals("set_motd")) {
				  setMotd(userMessage, userId);
				  getMotd(out);
			  }
			  
			  if(requestMode.contentEquals("delete_motd")) {
				  deleteMotd();
				  getMotd(out);
			  }
			  
			  if (requestMode.contentEquals("star_motd"))
			  {
				  starMotd(out, userId);
				  getMotd(out);
			  }
		  }
	  } else
	  {
		  // Invalid parameter set
		  resp.sendError(400);
	  }
  }

  private void userLogin(HttpServletRequest req, PrintWriter out, String login, String password, String local) {
	  
	  String sessionId = req.getSession().getId();
	  srvUserContainer foundUser = null;
	  
	  if((login != null) && (password != null)) {
		  for(srvUserContainer currentUser:userList) {
			  if((currentUser.getNick().compareTo(login) == 0) &&
			     (BCrypt.checkpw(password, currentUser.getPasswordHash())) )
			  {
				  foundUser = currentUser;
				  break;
			  }
		  }
	  }

	  if(foundUser != null)
		  saveSession(sessionId, foundUser.getId(), local);
	  
	  if(local == null)
		  local = "";
	  System.out.println("Userlogin: " + foundUser.getId());
	  out.println('[');
	  out.println("  {");
	  if(foundUser != null)
	  {
		  out.println("     \"id\":\"" + foundUser.getId() + "\",");
		  out.println("     \"nick\":\"" + foundUser.getNick() + "\",");
		  out.println("     \"sessionId\":\"" + sessionId + "\",");
		  out.println("     \"local\":\"" + local + "\""); 
	  } else
		  out.println("     \"sessionId\":\"0\"");
	  out.println("  }");
	  out.println(']');
	  out.flush();
	  
	  incrementUserDbVersion();  // New login means updated user info
  }

private int getNewestSeq(Connection conn)
  {
	  String query = "SELECT seq FROM runtimedata";
	  try {
	      Statement select = conn.createStatement();
	      ResultSet result = select.executeQuery(query);
	      result.next();
          int id = result.getInt(1);
	      select.close();
	      result.close();
	      return id;
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
          return -1;
	  }
  }
  
  private void setNewestSeq(Connection conn, int seqId)
  {
	  String query = "UPDATE runtimedata SET seq=?";
	  try {
	      PreparedStatement update = conn.prepareStatement(query);
	      update.setInt(1, seqId);
	      update.executeUpdate();
	      update.close();
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
	  }
  }
  
  private int getDbVersion(Connection conn)
  {
	  String query = "SELECT dbversion FROM runtimedata";
	  try {
	      Statement select = conn.createStatement();
	      ResultSet result = select.executeQuery(query);
	      result.next();
          int id = result.getInt(1);
	      select.close();
	      result.close();
	      return id;
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
          return -1;
	  }
  }
  
  private void setDbVersion(Connection conn, int dbVersion)
  {
	  String query = "UPDATE runtimedata SET dbversion=?";
	  try {
	      PreparedStatement update = conn.prepareStatement(query);
	      update.setInt(1, dbVersion);
	      update.executeUpdate();
	      update.close();
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
	  }
  }
  
  private int getMotdDbVersion(Connection conn)
  {
	  String query = "SELECT motd_dbversion FROM runtimedata";
	  try {
	      Statement select = conn.createStatement();
	      ResultSet result = select.executeQuery(query);
	      result.next();
          int id = result.getInt(1);
	      select.close();
	      result.close();
	      return id;
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
          return -1;
	  }
  }
  
  private void setMotdDbVersion(Connection conn, int dbVersion)
  {
	  String query = "UPDATE runtimedata SET motd_dbversion=?";
	  try {
	      PreparedStatement update = conn.prepareStatement(query);
	      update.setInt(1, dbVersion);
	      update.executeUpdate();
	      update.close();
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
	  }
  }
  
  private int getUserDbVersion(Connection conn)
  {
	  String query = "SELECT userdbversion FROM runtimedata";
	  try {
	      Statement select = conn.createStatement();
	      ResultSet result = select.executeQuery(query);
	      result.next();
          int id = result.getInt(1);
	      select.close();
	      result.close();
	      return id;
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
          return -1;
	  }
  }
  
  private void setUserDbVersion(Connection conn, int dbVersion)
  {
	  String query = "UPDATE runtimedata SET userdbversion=?";
	  try {
	      PreparedStatement update = conn.prepareStatement(query);
	      update.setInt(1, dbVersion);
	      update.executeUpdate();
	      update.close();
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
	  }
  }
  
  private void incrementUserDbVersion()
  {
	  try {
		  Connection conn = this.getConn();
		  int userDbVersion = getUserDbVersion(conn);
		  setUserDbVersion(conn, userDbVersion + 1);
		  System.out.println("Incrementing user dbversion to " + (userDbVersion + 1));
	  } catch(Exception e) {
          System.err.println("Error in incrementUserDbVersion: " + e);
          e.printStackTrace();
	  }
  }
  
  private void setNewMessage(Connection conn, String Message, int seqId, int dbVersion, Integer userId, String sessionId)
  {
	  PreparedStatement sessionSelect;
	  String query = "INSERT into messagetable (seq,userid,text,local,dbversion) values(?, ?, ?, ?, ?);";
	  String sessionQuery = "SELECT * FROM sessions where id = ?";
	  String local;
	  
	  try {
		  sessionSelect = conn.prepareStatement(sessionQuery);
    	  sessionSelect.setString(1, sessionId);
    	  ResultSet sessionResult = sessionSelect.executeQuery();
    	  if(!sessionResult.next()) local = "";
    	  else if((local = sessionResult.getString(4)) == null) local = "";

    	  sessionSelect.close();
	      sessionResult.close();
	      
	      PreparedStatement update = conn.prepareStatement(query);
	      update.setInt(1, seqId);
	      update.setInt(2, userId);
	      update.setString(3, Message);
	      update.setString(4, local);
	      update.setInt(5, dbVersion);
	      update.executeUpdate();
	      update.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error: " + query);
	      e.printStackTrace();
	  }
  }

  private void getNewMessages(int seqId, int last, PrintWriter out)
  {
	  String query;
	  PreparedStatement select;
	  boolean firstEntry = true;
	  //System.out.println("Srv msg from " + seqId + " to " + last);
	  
	  out.println('[');
	  
	  try {
		  Connection conn = this.getConn();
	      
	      if(last != 0) {
	    	  query = "SELECT * FROM messagetable where seq >= ? and seq <= ? order by seq";
	    	  select = conn.prepareStatement(query);
	    	  select.setInt(1, seqId);
	    	  select.setInt(2, last);
	      } else {
	    	  query = "SELECT * FROM messagetable where seq >= ? order by seq";
	    	  select = conn.prepareStatement(query);
	    	  select.setInt(1, seqId);
	      }
	      
	      ResultSet result = select.executeQuery();
	      
	      while (result.next()) {
	    	  if(!firstEntry)  // Take care to leave no trailing comma on last entry, JSON is picky
	    		  out.println(",");
	    	  firstEntry = false;
	    	  
	    	  out.println("  {");
	    	  String seq = result.getString(1);
	    	  String user = result.getString(2);
	    	  String value = result.getString(3);
	    	  String dateStamp = result.getDate(4).toString();
	    	  String timeStamp = result.getTime(4).toString();
	    	  String local = result.getString(5);
	    	  String stars = result.getString(6);
	    	  boolean deleted = result.getBoolean(7);
	    	  int dbVersion = result.getInt(8);
	    	  
	    	  if(local == null)
	    		  local = "";
	    	  
	    	  out.println("     \"id\":\"" + seq + "\",");
	    	  out.println("     \"user\":\"" + user + "\",");
	    	  out.println("     \"value\":\"" + value + "\",");
	    	  out.println("     \"date\":\"" + dateStamp + "\",");
	    	  out.println("     \"time\":\"" + timeStamp + "\",");
	    	  out.println("     \"local\":\"" + local + "\",");
	    	  out.println("     \"deleted\":\"" + deleted + "\",");
	    	  out.println("     \"dbversion\":\"" + dbVersion + "\",");
	    	  out.println("     \"stars\":\"" + stars + "\"");
	          out.print("  }");
	      }
	      select.close();
	      result.close();
	      
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
	  
	  out.println("");
      out.println(']');
	  out.flush();

  }
  
  private void getMessagesByDb(PrintWriter out, int dbVersion)
  {
	  String query;
	  PreparedStatement select;
	  boolean firstEntry = true;
	  
	  out.println('[');
	  
	  try {
		  Connection conn = this.getConn();
	      
    	  query = "SELECT * FROM messagetable WHERE dbversion >= ? order by seq";
    	  select = conn.prepareStatement(query);
    	  select.setInt(1, dbVersion);
	      
	      ResultSet result = select.executeQuery();
	      
	      while (result.next()) {
	    	  if(!firstEntry)  // Take care to leave no trailing comma on last entry, JSON is picky
	    		  out.println(",");
	    	  firstEntry = false;
	    	  
	    	  out.println("  {");
	    	  String seq = result.getString(1);
	    	  String user = result.getString(2);
	    	  String value = result.getString(3);
	    	  String dateStamp = result.getDate(4).toString();
	    	  String timeStamp = result.getTime(4).toString();
	    	  String local = result.getString(5);
	    	  String stars = result.getString(6);
	    	  boolean deleted = result.getBoolean(7);
	    	  int version = result.getInt(8);
	    	  
	    	  if(local == null)
	    		  local = "";
	    	  
	    	  out.println("     \"id\":\"" + seq + "\",");
	    	  out.println("     \"user\":\"" + user + "\",");
	    	  out.println("     \"value\":\"" + value + "\",");
	    	  out.println("     \"date\":\"" + dateStamp + "\",");
	    	  out.println("     \"time\":\"" + timeStamp + "\",");
	    	  out.println("     \"local\":\"" + local + "\",");
	    	  out.println("     \"deleted\":\"" + deleted + "\",");
	    	  out.println("     \"dbversion\":\"" + version + "\",");
	    	  out.println("     \"stars\":\"" + stars + "\"");
	          out.print("  }");
	      }
	      select.close();
	      result.close();
	      
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
	  
	  out.println("");
      out.println(']');
	  out.flush();

  }
  
  private void getUserInfo(PrintWriter out)
  {
	  boolean firstEntry = true;

	  out.println('[');
	  
	  for(srvUserContainer currentUser:userList) {
		  if(!firstEntry)  // Take care to leave no trailing comma on last entry, JSON is picky
			  out.println(",");
		  firstEntry = false;
		  
		  out.println("  {");
		  out.println("     \"id\":\"" + currentUser.getId() + "\",");
		  out.println("     \"nick\":\"" + currentUser.getNick() + "\",");
		  out.println("     \"url\":\"" + currentUser.getAvatarURL() + "\",");
		  out.println("     \"online\":\"" + currentUser.getActiveStatus() + "\",");
		  out.println("     \"tmo\":\"" + currentUser.getActivityTimeout() + "\",");
		  out.println("     \"messages\":\"" + currentUser.getNumOfMessages() + "\",");
		  out.println("     \"deleted\":\"" + currentUser.getNumOfDeletedMessages() + "\",");
		  out.println("     \"edited\":\"" + currentUser.getNumOfEditedMessages() + "\",");
		  out.println("     \"starssent\":\"" + currentUser.getNumOfStarsSent() + "\",");
		  out.println("     \"starsreceived\":\"" + currentUser.getNumOfStarsReceived() + "\"");
		  out.print("  }");
	  }
	
	  out.println("");
      out.println(']');
	  out.flush();
  }
  
  private void processNewMessage(String Message, Integer userId, String sessionId)
  {
	  int seqId;
	  int dbVersion;
	  
	  // Enforce max msg len to avoid SQL errors
	  if (Message.length() > 4000 )
	  	Message = Message.substring(0, 4000);

	  Message = filterMessage(Message);
	  try {
		  
		  // Don't autocommit, we want to save message and update seqId/dbVersion in a single transaction
	      Connection conn = this.getConn();
	      conn.setAutoCommit(false);
	      
	      seqId = getNewestSeq(conn) + 1;
	      dbVersion = getDbVersion(conn) + 1;
	      setNewMessage(conn, Message, seqId, dbVersion, userId, sessionId);
	      setNewestSeq(conn, seqId);
	      setDbVersion(conn, dbVersion);
	      
	      conn.commit();
	      conn.close();
	      
	      incrementUserDbVersion();  // New msg means updated user stats
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error in ProcessNewMessage");
          e.printStackTrace();
	  }
  }
  
  private void deleteMessage(PrintWriter out, String message_id)
  {
	  int seqId = Integer.parseInt(message_id);
	  String query = "UPDATE messagetable SET deleted=?,dbversion=? where seq=?";
	  try {
		  Connection conn = this.getConn();
		  conn.setAutoCommit(false);
		  
		  int dbVersion = getDbVersion(conn) + 1;
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setBoolean(1, true);
    	  update.setInt(2, dbVersion);
    	  update.setInt(3, seqId);
    	  update.executeUpdate();
    	  setDbVersion(conn, dbVersion);
	      update.close();
	      
	      conn.commit();
	      conn.close();
	      
	      incrementUserDbVersion();  // Deleted msg means updated user stats
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
	  
	  // Send back the newly modified message so the client will refresh it
	  getNewMessages(seqId, seqId, out);
  }
  
  private void starMessage(PrintWriter out, String message_id, String user_id)
  {
	  int elementBitPos = (int) Math.pow(2,Integer.valueOf(user_id));
	  
	  int seqId = Integer.parseInt(message_id);
	  String query = "UPDATE messagetable SET stars=(stars ^ ?),dbversion=? where seq=?";
	  try {
		  Connection conn = this.getConn();
		  conn.setAutoCommit(false);
		  
		  int dbVersion = getDbVersion(conn) + 1;
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setInt(1, elementBitPos);
    	  update.setInt(2, dbVersion);
    	  update.setInt(3, seqId);
    	  update.executeUpdate();
    	  setDbVersion(conn, dbVersion);
	      update.close();
	      
	      conn.commit();
	      conn.close();
	      
	      incrementUserDbVersion();  // Starred msg means updated user stats
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
	  
	  // Send back the newly modified message so the client will refresh it
	  getNewMessages(seqId, seqId, out);
  }
  
  private void editMessage(PrintWriter out, String message_id, String message)
  {
	  int seqId = Integer.parseInt(message_id);
	  String query = "UPDATE messagetable SET text=(?),dbversion=?,edited=edited+1 where seq=?";
	  try {
		  Connection conn = this.getConn();
		  conn.setAutoCommit(false);
		  
		  int dbVersion = getDbVersion(conn) + 1;
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setString(1, filterMessage(message));
    	  update.setInt(2, dbVersion);
    	  update.setInt(3, seqId);
    	  update.executeUpdate();
    	  setDbVersion(conn, dbVersion);
	      update.close();
	      
	      conn.commit();
	      conn.close();
	      
	      incrementUserDbVersion();  // Edited msg means updated user stats
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
	  
	  // Send back the newly modified message so the client will refresh it
	  getNewMessages(seqId, seqId, out);
  }
  
  private String filterMessage(String message)
  {
	Whitelist localWhitelist = Whitelist.basicWithImages();
	
	localWhitelist.addTags("a", "img");
	localWhitelist.addAttributes("a", "href", "rel", "target");
	localWhitelist.addAttributes("img", "class");
	
	String unescapedMessage = unescapeJson(message);
  	String cleanedMessage = Jsoup.clean(unescapedMessage, localWhitelist);
  	String unescapedHTML = StringEscapeUtils.unescapeHtml3(cleanedMessage);
  	String escapedMessage = escapeJson(unescapedHTML);
  	
  	
//  	System.out.println("Evil: " + message);
  	System.out.println("Unesc: " + unescapedMessage);
  	System.out.println("Clean: " + cleanedMessage);
//  	System.out.println("Escaped: " + escapedMessage);
  	return escapedMessage;
  }
  
  public String escapeJson(String str) {
	    str = str.replace("\\", "\\\\");
	    str = str.replace("\"", "\\\"");
	    str = str.replace("/", "\\/");
	    str = str.replace("\b", "\\b");
	    str = str.replace("\f", "\\f");
	    str = str.replace("\n", ""); 
	    str = str.replace("\r", "");
	    str = str.replace("\t", "\\t");
	    return str;
  }
  
  public String unescapeJson(String str) {
	    str = str.replace("\\\\", "\\");
	    str = str.replace("\\\"", "\"");
	    str = str.replace("\\/", "/");
	    str = str.replace("\\b", "\b");
	    str = str.replace("\\f", "\f");
	    str = str.replace("\\t", "\t");
	    return str;
  }
  
  private void getRuntimeData(PrintWriter out)
  {
	  String query;
	  PreparedStatement select;
	  
	  out.println('[');
	  
	  try {
		  Connection conn = this.getConn();
	      
    	  query = "SELECT * FROM runtimedata";
    	  select = conn.prepareStatement(query);
	      
	      ResultSet result = select.executeQuery();
	      result.next();
    	  out.println("  {");
    	  String seq = result.getString(1);
    	  String dbVersion = result.getString(2);
    	  String dbVersionUsers = result.getString(3);
    	  String dbVersionMotd = result.getString(4);
    	  
    	  out.println("     \"serverVersion\":\"" + myVersion + "\",");
    	  out.println("     \"newestSeq\":\"" + seq + "\",");
    	  out.println("     \"dbVersion\":\"" + dbVersion + "\",");
    	  out.println("     \"dbVersionUsers\":\"" + dbVersionUsers + "\",");
    	  out.println("     \"dbVersionMotd\":\"" + dbVersionMotd + "\"");
          out.println("  }");

	      select.close();
	      result.close();
	      conn.close();
	  } catch(SQLException e) {
	          System.err.println("Mysql Statement Error");
	          e.printStackTrace();
	  }
	  
      out.println(']');
	  out.flush();

  }
  
  private int getSessionAssociatedUser(String sessionId) {
	  String query;
	  PreparedStatement select;
	  int userId = -1;
	  
	  try {
		  Connection conn = this.getConn();
	      
    	  query = "SELECT * FROM sessions where id = ?";
    	  select = conn.prepareStatement(query);
    	  select.setString(1, sessionId);
	      ResultSet result = select.executeQuery();
	      
	      if(result.next())
	    	  userId = result.getInt(2);
	      
	      select.close();
	      result.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
	  
	  return userId;
  }
  
  private String getSessionAssociatedLocal(String sessionId) {
	  String query;
	  PreparedStatement select;
	  String local = "";
	  
	  try {
		  Connection conn = this.getConn();
	      
    	  query = "SELECT * FROM sessions where id = ?";
    	  select = conn.prepareStatement(query);
    	  select.setString(1, sessionId);
	      ResultSet result = select.executeQuery();
	      
	      if(result.next())
	    	  local = result.getString(4);
	      if(local == null) local = "";
	      
	      select.close();
	      result.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
	  
	  return local;
  }
  
  private int saveSession(String sessionId, int userId, String local) {
	  
	  String query = "INSERT INTO sessions (id,user,local) values(?, ?, ?)";
	  try {
		  Connection conn = this.getConn();
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setString(1, sessionId);
    	  update.setInt(2, userId);
    	  update.setString(3, local);
    	  update.executeUpdate();
	      update.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
	  
	  return userId;
  }
  
  private void touchSession(String sessionId) {
	  
	  String query = "UPDATE sessions SET date=NOW() where id=?";
	  try {
		  Connection conn = this.getConn();
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setString(1, sessionId);
    	  update.executeUpdate();
	      update.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }
  private void updateSessionLocal(String sessionId, String local) {
	  
	  String query = "UPDATE sessions SET local=? where id=?";
	  local = filterMessage(local);
	  try {
		  Connection conn = this.getConn();
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setString(1, local);
    	  update.setString(2, sessionId);
    	  update.executeUpdate();
	      update.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }

  private void deleteSession(String sessionId) {
	  
	  String query = "DELETE FROM sessions where id=?";
	  try {
		  Connection conn = this.getConn();
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setString(1, sessionId);
    	  update.executeUpdate();
	      update.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }

  private void getServerVersion(PrintWriter out)
  {
	  out.println('[');
	  out.println("  {");
	  out.println("     \"serverVersion\":\"" + myVersion + "\"");
      out.println("  }");
      out.println(']');
	  out.flush();
  }

  private void sendSessionValid(PrintWriter out, String sessionId, Integer userId, String local)
  {
	  out.println('[');
	  out.println("  {");
	  out.println("     \"sessionId\":\"" + sessionId + "\",");
	  out.println("     \"userId\":\"" + userId + "\",");
	  out.println("     \"local\":\"" + local + "\"");
      out.println("  }");
      out.println(']');
	  out.flush();
  }
  
  private void sendSessionInvalid(PrintWriter out)
  {
	  out.println('[');
	  out.println("  {");
	  out.println("     \"sessionId\":\"0\"");
      out.println("  }");
      out.println(']');
	  out.flush();
  }
  private void loadUserInfo() {
	  String query;
	  PreparedStatement select;
	
	  try {
		  Connection conn = this.getConn();
	      
    	  query = "SELECT * FROM users ORDER BY id";
    	  select = conn.prepareStatement(query);
	      
	      ResultSet result = select.executeQuery();
	      while (result.next()) {
	    	  // test create of user 0
	    	  srvUserContainer user = new srvUserContainer();
	    	  userList.add(user);

	    	  user.setId(Integer.valueOf(result.getString(1)));
	    	  user.setNick(result.getString(2));
	    	  user.setName(result.getString(3));
	    	  user.setPasswordHash(result.getString(4));
	    	  user.setAvatarURL(result.getString(5));
	      }
	      select.close();
	      result.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }
  
  public srvUserContainer getUser(Integer userId){
		for(srvUserContainer currentUser:userList)
			if(currentUser.getId().intValue() == userId.intValue()) 
				return currentUser;

		return new srvUserContainer();
	}
  
  private void resetUserTimeout(int userId) {
	  
	for(srvUserContainer user:userList)
		if(user.getId() == userId) {
			if(user.getActivityTimeout() == 0)
				incrementUserDbVersion();
			user.setActivityTimeout(ACTIVITY_TIMEOUT);
			break;
		}
  }
  
  private void decrementUserTimeout() {
	  for(srvUserContainer user:userList)
		  if(user.getActivityTimeout() > 5) 
			  user.setActivityTimeout(user.getActivityTimeout() - 5);
		  else if(user.getActivityTimeout() > 0) {
			  user.setActivityTimeout(0);
			  incrementUserDbVersion();
		  }
}
  
  private void garbageCollectSessions()
  {
	  Date oldDate = new Date(System.currentTimeMillis() - SESSION_TIMEOUT * 1000);
	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	  String query = "DELETE from sessions where date < ?";
	  try {
		  Connection conn = this.getConn();
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setString(1, format.format(oldDate));
    	  update.executeUpdate();
	      update.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }
  
  private void cleanupMotd()
  {
	  Date currentDate = new Date(System.currentTimeMillis());
	  
	  String query = "SELECT date from motd WHERE dbversion = ? AND deleted = ?";
	  String update = "UPDATE motd SET deleted = ?, dbversion = ? WHERE dbversion = ?";
	  ResultSet result;
	  try {
		  Connection conn = this.getConn();
		  PreparedStatement select = conn.prepareStatement(query);
		  int motdDbVersion = getMotdDbVersion(conn); 
		  select.setInt(1, motdDbVersion);
		  select.setInt(2, 0);
    	  result = select.executeQuery();
    	  if(result.next()) {
    		  Date motdDate = new Date(result.getTimestamp(1).getTime());
    		  //System.out.println("Current: " + currentDate.getTime() + " motd: " + motdDate.getTime());
    		  if((currentDate.getTime() - motdDate.getTime()) / 1000 > MOTD_TIMEOUT)
    		  {
    			  PreparedStatement setDeleted = conn.prepareStatement(update);
    			  setDeleted.setInt(1, 1);
    			  setDeleted.setInt(2, motdDbVersion + 1);
    			  setDeleted.setInt(3, motdDbVersion);
    			  setDeleted.executeUpdate();
    			  setMotdDbVersion(conn, motdDbVersion + 1);
    		  }
    	  }
	      select.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }
  
  private void updateUserMessageCount()
  {
	  String query ="SELECT userid, COUNT(*) FROM messagetable GROUP BY userid ORDER BY userid";
	  String queryDel ="SELECT userid, COUNT(*) FROM messagetable where deleted=true GROUP BY userid ORDER BY userid";
	  String queryEdit ="SELECT userid,sum(edited) FROM messagetable WHERE deleted=false GROUP BY userid ORDER BY userid";
	  String queryStarsSent = "SELECT count(*) FROM messagetable WHERE find_in_set(?, stars) AND deleted=false";
	  String queryStarsRcvd = "SELECT SUM(BIT_COUNT(stars+0)) FROM messagetable WHERE userid=? AND deleted=false";
	  String queryMotdStarsSent = "SELECT count(*) FROM motd WHERE find_in_set(?, stars)";
	  String queryMotdStarsRcvd = "SELECT SUM(BIT_COUNT(stars+0)) FROM motd WHERE userid=?";
	  
	  PreparedStatement select;
	  ResultSet result;
	  int userTotalStarsSent = 0;
	  int userTotalStarsRcvd = 0;
	  
	  try {
		  Connection conn = this.getConn();
    	  select = conn.prepareStatement(query);
	      result = select.executeQuery();
	      while (result.next()) {
	    	  for(srvUserContainer user:userList)
	    		  if (user.getId().intValue() == Integer.valueOf(result.getString(1))) {
	    			  user.setNumOfMessages(Integer.valueOf(result.getString(2)));
	    			  break;
	    		  }
	      }
	      select.close();
	      result.close();
	      
	      select = conn.prepareStatement(queryDel);
	      result = select.executeQuery();
	      while (result.next()) {
	    	  for(srvUserContainer user:userList)
	    		  if (user.getId().intValue() == Integer.valueOf(result.getString(1))) {
	    			  user.setNumOfDeletedMessages(Integer.valueOf(result.getString(2)));
	    			  break;
	    		  }
	      }
	      select.close();
	      result.close();
	      
	      select = conn.prepareStatement(queryEdit);
	      result = select.executeQuery();
	      while (result.next()) {
	    	  for(srvUserContainer user:userList)
	    		  if (user.getId().intValue() == Integer.valueOf(result.getString(1))) {
	    			  user.setNumOfEditedMessages(Integer.valueOf(result.getString(2)));
	    			  break;
	    		  }
	      }
	      select.close();
	      result.close();
	      
	      String value = "";
	      for(int userIndex = 0; userIndex < userList.size(); userIndex++){
	    	  select = conn.prepareStatement(queryStarsSent);
	    	  select.setInt(1, userList.get(userIndex).getId()); 
		      result = select.executeQuery();
		      if(result.next() && ((value = result.getString(1)) != null ) )
		    	  userTotalStarsSent = Integer.valueOf(value);
		      
		      select = conn.prepareStatement(queryStarsRcvd);
	    	  select.setInt(1, userList.get(userIndex).getId()); 
		      result = select.executeQuery();
		      if(result.next() && ((value = result.getString(1)) != null ) )
		    	  userTotalStarsRcvd = Integer.valueOf(value);
		      
		      select = conn.prepareStatement(queryMotdStarsSent);
	    	  select.setInt(1, userList.get(userIndex).getId()); 
		      result = select.executeQuery();
		      if(result.next() && ((value = result.getString(1)) != null ) )
		    	  userTotalStarsSent += Integer.valueOf(value);
		      
		      select = conn.prepareStatement(queryMotdStarsRcvd);
	    	  select.setInt(1, userList.get(userIndex).getId()); 
		      result = select.executeQuery();
		      if(result.next() && ((value = result.getString(1)) != null ) )
		    	  userTotalStarsRcvd += Integer.valueOf(value);
		      
		      userList.get(userIndex).setNumOfStarsSent(userTotalStarsSent);
		      userList.get(userIndex).setNumOfStarsReceived(userTotalStarsRcvd);
		      //System.out.println("Stars for user " + userIndex + " sent: " + userTotalStarsSent + " rcvd: " + userTotalStarsRcvd);
	      }
	      select.close();
	      result.close();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }
 
  private void setMotd(String Motd, String userId)
  {
	  int motdDbVersion;
	  
	  Motd = Motd.substring(4, Motd.length()).trim();
	  if (Motd.length() > 255 )
		  	Motd = Motd.substring(0, 255);
	  
	  String query = "INSERT INTO motd (text, date, userid, dbversion) VALUES (?, CURRENT_TIMESTAMP, ?, ?)";
	  try {
		  Connection conn = this.getConn();
		  conn.setAutoCommit(false);
		  
		  motdDbVersion = getMotdDbVersion(conn) + 1;
	      PreparedStatement update = conn.prepareStatement(query);
	      update.setString(1, Motd);
	      update.setInt(2, Integer.valueOf(userId));
	      update.setInt(3, motdDbVersion);
	      update.executeUpdate();
	      update.close();
	      setMotdDbVersion(conn, motdDbVersion);
	      
	      conn.commit();
	      conn.close();
	  } catch(SQLException e) {
          System.err.println("Mysql Statement Error: " + query);
          e.printStackTrace();
	  }
  }
  
  private void getMotd(PrintWriter out)
  {
	  String query;
	  PreparedStatement select;
	  int motdNewestDbVersion;
	  
	  out.println('[');
	  
	  try {
		  Connection conn = this.getConn();
		  motdNewestDbVersion = getMotdDbVersion(conn);
		  
    	  query = "SELECT * FROM motd where dbversion=?";
    	  select = conn.prepareStatement(query);
	      select.setInt(1, motdNewestDbVersion);
	      ResultSet result = select.executeQuery();
	      result.next();
    	  out.println("  {");
    	  String userId = result.getString(1);
    	  String text = result.getString(2);
    	  String date = result.getDate(3).toString();
    	  String time = result.getTime(3).toString();
    	  String stars = result.getString(4);
    	  int deleted = result.getInt(5);
    	  
    	  out.println("     \"text\":\"" + text + "\",");
    	  out.println("     \"date\":\"" + date + "\",");
    	  out.println("     \"time\":\"" + time + "\",");
    	  out.println("     \"userid\":\"" + userId + "\",");
    	  out.println("     \"deleted\":\"" + deleted + "\",");
    	  out.println("     \"stars\":\"" + stars + "\",");
    	  out.println("     \"dbversion\":\"" + motdNewestDbVersion + "\"");
          out.println("  }");

	      select.close();
	      result.close();
	      conn.close();
	  } catch(SQLException e) {
	          System.err.println("Mysql Statement Error");
	          e.printStackTrace();
	  }
	  
      out.println(']');
	  out.flush();

  }
  
  private void deleteMotd()
  {
	  String query = "UPDATE motd SET deleted=?,dbversion=? where dbversion=?";
	  try {
		  Connection conn = this.getConn();
		  conn.setAutoCommit(false);
		  
		  int dbVersion = getMotdDbVersion(conn);
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setBoolean(1, true);
    	  update.setInt(2, dbVersion+1);
    	  update.setInt(3, dbVersion);
    	  update.executeUpdate();
    	  setMotdDbVersion(conn, dbVersion+1);
	      update.close();
	      
	      conn.commit();
	      conn.close();
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }
  
  private void starMotd(PrintWriter out, String user_id)
  {
	  int elementBitPos = (int) Math.pow(2,Integer.valueOf(user_id));
	  
	  String query = "UPDATE motd SET stars=(stars ^ ?),dbversion=? WHERE dbversion=?";
	  try {
		  Connection conn = this.getConn();
		  conn.setAutoCommit(false);
		  
		  int dbVersion = getMotdDbVersion(conn);
		  PreparedStatement update = conn.prepareStatement(query);
    	  update.setInt(1, elementBitPos);
    	  update.setInt(2, dbVersion + 1);
    	  update.setInt(3, dbVersion);
    	  update.executeUpdate();
    	  setMotdDbVersion(conn, dbVersion + 1);
	      update.close();
	      
	      conn.commit();
	      conn.close();
	      
	      incrementUserDbVersion();  // Starred msg means updated user stats
	  } catch(SQLException e) {
	      System.err.println("Mysql Statement Error");
	      e.printStackTrace();
	  }
  }
  
  protected Connection getConn() {

      Connection conn = null;

      // figure out what server this application is being hosted on
      String url = "jdbc:mysql://";
      url += myServerConfig.getSqlServerIp() + ":";
      url += myServerConfig.getSqlServerPort() + "/"; 
      url += myServerConfig.getDatabaseName();
      url += "?useUnicode=true&characterEncoding=utf8";
       
      String driver = "com.mysql.jdbc.Driver";
      String user   = myServerConfig.getDatabaseUser();
      String pass   = myServerConfig.getDatabasePwd();
      //System.out.println("** Opening SQL connection with URL " + url);
      try {
              Class.forName(driver).newInstance();
              conn = DriverManager.getConnection(url, user, pass);

      } catch (Exception e) {
              System.err.println("Mysql Connection Error: ");
              e.printStackTrace();
      }

      if (conn == null)  {
              System.out.println("~~~~~~~~~~ can't get a Mysql connection");
      }
      
      return conn;
  }
  
  class userCheckTask extends TimerTask {
	  private int loops = 0;
	  public void run() {
		  decrementUserTimeout();
		  updateUserMessageCount();

		  if(loops++ % 180 == 0) {
			  garbageCollectSessions();
			  cleanupMotd();
		  }
	  }
  }
}