package edu.fsu.cs.scramd.data;
//THIS WILL REPLACE Account.java!!!


import java.sql.Blob;

public class Friend {
	//Private variables:
	String username;
	String status;
	byte[] accIMG;
	int tScore;
	int uScore;
	int oScore;
	String objectId;

	//Empty Constructor
	public Friend(){
	}
	
	public Friend(String un)
	{
		this.username = un;
		this.status = "fight";
		this.accIMG = null;
		this.tScore = -1;
		this.uScore = 0;
		this.oScore = 0;
		this.objectId = "";
	}
	
	public Friend(String un, String s, byte[] IMG, int ts)
	{
		this.username = un;
		this.status = s;
		this.accIMG = IMG;
		this.tScore = ts;
		this.uScore = 0;
		this.oScore = 0;
		this.objectId = "";
	}
	

	public Friend(String un, String s, byte[] IMG, int ts, int us, int os, String oi)
	{
		this.username = un;
		this.status = s;
		this.accIMG = IMG;
		this.tScore = ts;
		this.uScore = us;
		this.oScore = os;
		this.objectId = oi;
	}
	
	//Get USERNAME
	public String getUsername(){
		return this.username;
	}	
	
	
	//Get Status
	public String getStatus(){
		return this.status;
	}
	
	
	//Get IMG
	public byte[] getIMG(){
		return this.accIMG;
	}
	
	//Get Temp Score
	public int getTScore()
	{
		return this.tScore;
	}
	
	//Get User Score
	public int getUScore()
	{
		return this.tScore;
	}
	
	//Get Opponent Score
	public int getOScore()
	{
		return this.tScore;
	}
	
	//Get Object Id
	public String getObjectId()
	{
		return this.objectId;
	}
	
	
	
	
	
	//Set USERNAME
	public void setUsername(String un){
		this.username = un;
	}
	
	//Set Status
	public void setStatus(String s){
		this.status = s;
	}
	
	//Set IMG
	public void setIMG(byte[] b){
		this.accIMG = b;
	}
	
	//Set Temp score
	public void setTScore(int ts)
	{
		this.tScore = ts;
	}
	
	//Set User score
	public void setUScore(int us)
	{
		this.uScore = us;
	}
	
	//Set Opponent score
	public void setOScore(int os)
	{
		this.oScore = os;
	}
	
	//Set Object ID
	public void setObjectId(String oi)
	{
		this.objectId = oi;
	}
}
