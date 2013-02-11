package com.scrollwin.client;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.HTML;

public class motd extends HTML  {

	private String myMotd;
	private String myStars;
	
	public motd() {
		setStyleName("motd");
		myMotd = "";
		myStars = "";
	}
	
	public void update(motdData motd)
	{
		String displayedMotd = "";
		if(motd.deleted == 0) {
			myMotd = motd.text;
			myStars = motd.stars;
	
			// Split the message in tokens (separator is space) an try to locate URLs
	    	String [] parts = myMotd.split("\\s+");
	    	
	    	for(int tok = 0; tok < parts.length; tok++)
	    	{
	    		String item = parts[tok];
	    		if ((item.startsWith("http://")) || (item.startsWith("https://")) ){
	    			// It's a link to some random site
	    			item = encapsulateLink(item);
	    		}
	    		displayedMotd += item + " ";
	    	}
		} 
    	
		setHTML(displayedMotd);
	}
	
	public boolean hasChanged(motdData motd)
	{
		return !(motd.text.equals(myMotd)) || !(motd.stars.equals(myStars));
	}
	
	// Shameless duplication from MessageViewElementNative, tsk
	private String encapsulateLink(String link)
	{
		String encapsulatedLink;
	    
		RegExp regExp = RegExp.compile("https?://([a-zA-Z0-9.]+)");
		MatchResult matcher = regExp.exec(link);
		boolean matchFound = (matcher != null);

		// Matched regex in group0, subex in group one is the hostname
		if(matchFound && matcher.getGroupCount() == 2) {
			encapsulatedLink = "<a href=\"" + link + "\" target=\"_blank\">" + matcher.getGroup(1) + "</a>";
		} else
			encapsulatedLink = "<a href=\"" + link + "\" target=\"_blank\">lien</a>";
	    return encapsulatedLink;
	}
}
