package edu.fsu.cs.scramd.data;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserAccount")
public class UserAccount extends ParseObject {

	public UserAccount(){
		//Default Constructor
	}
	
	//SETTERS AND GETTERS
	
/*	
	public void setSendTo(ParseUser user){
		put("sendTo" , user);
	}
	
	public ParseUser getSendTo(){
		return getParseUser("sendTo");
	}
*/
	public void setSendTo(String user){
		put("sendTo" , user);
	}
	
	public String getSendTo(){
		return getString("sendTo");
	}
	
	
	public void setSentBy(ParseUser user){
		put("sentBy" , user);
	}
	
	public ParseUser getSentBy(){
		return getParseUser("sentBy");
	}
	
	
	
	public void setStatus(String status){
		put("status", status);
	}
	
	public String getStatus(){
		return getString("status");
	}
	
	
	
	public void setPhotoFile(ParseFile file){
		put("photo", file);
	}
	
	public ParseFile getPhotoFile(){
		return getParseFile("photo");
	}
	
	
	public void setScore(int score){
		put("score", score);
	}
	
	public int getScore(){
		return getInt("score");
	}
	
}
