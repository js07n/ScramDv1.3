package edu.fsu.cs.scramd.friend;

import org.json.JSONArray;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import edu.fsu.cs.scramd.data.DatabaseHandler;
import edu.fsu.cs.scramd.data.Friend;
import edu.fsu.cs.scramd.data.UserAccount;
import edu.fsu.cs.scramd.game.GameScreen;
import android.content.Context;



public class UpdateChallenge {
	
	public static DatabaseHandler db;
	public static ParseUser user;
	
	

	
	public UpdateChallenge(Context context)
	{
		db = new DatabaseHandler(context);
		user = ParseUser.getCurrentUser();
		
		
	}

	
	public static void sent(UserAccount challenge)
	{
		// may not need this method.
		// handled by camera and gameplay activites
		
	}

	
	public void received(UserAccount challenge)
	{
		//Download Image from Server and save to App DB
		System.out.println("Method 'received' called");
		
		//update challenge status on Server
    	ParseUser sb = ParseUser.getCurrentUser();
    
    	
    	
    	//get information on User that sent challenge
    	try 
    	{
			sb = challenge.getSentBy().fetchIfNeeded();
		} 
    	catch (ParseException e1) 
		{				
			e1.printStackTrace();
		}
    		
    		
    	System.out.println("sentby = " + sb.getUsername());
    	
    	// Store the challenge's sentBy User and Object ID
    	final String sentBy = sb.getUsername();
    	final String objectId = challenge.getObjectId();
    	final int tScore = challenge.getScore();
    	
    	//Null photo field has been sent
    	if(challenge.getPhotoFile() == null)
    		return;
    	
    	ParseFile newFile = (ParseFile) challenge.get("photo");

    	// Retrieve Photo from server
    	newFile.getDataInBackground(new GetDataCallback() {
    		public void done(byte[] data, ParseException e) {
    			if (e == null) { // if no errors
    	    	
    				if (data == null)
    				{
    					System.out.println("data field is null");
    					return;
    				}
    				
    				if(isUserAFriend(sentBy) && db.getFriendsCount() != 0)
    				{	    	    	    		
    					//save image to database
    					Friend friend = db.getFriend(sentBy);
    					friend.setIMG(data);
    	    		
    					//Save oppnent's score if available
    					if(tScore != -1)
    						friend.setTScore(tScore);
    					
    					
    					System.out.println("tScore " + tScore);
    					
    					//update status
    					friend.setStatus("play");
    					if(friend.getObjectId().equals(""))
    						friend.setObjectId(objectId);
    					db.updateFriend(friend);	
    					
    				}
    				else
    				{	    	    	    		
    					//if user that sent image isn't a friend
    					//then create an entry for them in the database
    					addToFriendList(sentBy, "play", data, objectId, tScore);	
    					
    				}
    				
    				updateChallengeOnServer(sentBy, objectId, "Received");
    				
    			} 
    			else // there are errors
    			{
    				// something went wrong	    	    	    	
    			}
    			
    		}// end done
    	});	// end getDataInBackground
    	
    	//updateChallengeOnServer(sentBy);

    	
	}// end received

	

	//Checks to see if Username entered in EditText is already the user's friend
	public static boolean isUserAFriend(String v)
	{			
		if(user == null)
		{
			user = ParseUser.getCurrentUser();
		}
		
		//This check prevents crashes from doing comparisons with NULL values
		// Usually this is the case when accounts are just created and have
		// zero friends in their friendList
		if(user.getJSONArray("friendList") == null)
			return false;	
		
		//Check to to see if user is trying to be-/de-friend himself
		if(v.equals(user.getUsername()))
			return true;
				
		JSONArray jarr = user.getJSONArray("friendList");
		
		//Check to see if User is friends with requested user
		for(int i = 0; i < jarr.length(); i++)
		{
			if(v.equals(jarr.optString(i)))
				return true;
		}		
		
		return false;
	
	}    		
	
	
	
	public static void addToFriendList(String un, String status, byte[] img, String objectId, int tScore)
	{		
		//Add to app DB
		db.addFriend(new Friend(un, status, img, tScore, 0, 0, objectId)); 
		//"-1" means that there is no previous score
		// 0, 0, = user and opponent scores, 
		// since there is has been no previous games played, they are zeros.
		
		JSONArray jarr;
		
		//if friendList is null create a new JSONArray
		if(user.getJSONArray("friendList") == null)
			jarr = new JSONArray();
		else
		{
		//	Copy current user's Friend list to variable
			jarr = user.getJSONArray("friendList");
		}
		
		//Add friend name to JSON
		jarr.put(un);

		//Attach updated friendList to user
		user.put("friendList", jarr);
		
		user.saveInBackground(new SaveCallback() {			
			@Override
			public void done(ParseException e) {
				if(e == null)
				{

				}
				else
				{
					System.out.println("friendlist save failed");
				}
			}// end done()
		});// end saveInBackground()							
	}// end addToFriendList()    	
	
	
	
	
	public static void updateChallengeOnServer(final String sentBy, String objectId, final String status)
	{
		// Get UserAccount object by using OBJECT ID
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserAccount");
		query.getInBackground(objectId, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				if(e == null)
				{
					System.out.println("No errors in UpdateChallenge");
					
					if(object != null)
					{						
						UserAccount ua = (UserAccount) object;

						//update UserAccount on Server				    	
						//ua.setPhotoFile(null); // this field CANNOT be NULL or it will crash
				    	ua.setStatus(status);
				    	
				    	if(status.equals("Done"))
				    	{
				    		ua.setSentBy(user);
				    		ua.setSendTo(sentBy);
				    		ua.setScore(-1);
				    	}
				    	ua.saveInBackground(new SaveCallback(){

							@Override
							public void done(ParseException e) {
								if(e == null){
									System.out.println("Challenge updated on server");
									
									
								}else{
									System.out.println("Challenge NOT updated on server : " + e.toString());
									
								}
								
							}// end challenge.saveInBackground.done

						});// end challenge.saveInBackground
					
					}// end if
					else
						System.out.println("Object is NULL");
				}// end if
				else
					System.out.println("Errors found when getting Challenge: " + e.toString());
			}// end getfirstinBG.done
		});// getFirstinBackground
		    		  
	}
	
	
	
	//03.26.14
	public void update(UserAccount challenge)
	{
		final int tScore = challenge.getInt("score");		
		ParseUser sentBy= challenge.getParseUser("sentBy");
		
		
		//fetch ParseUser object info.
		try {
			sentBy.fetch();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		if(!isUserAFriend(sentBy.getUsername()) || db.getFriendsCount() == 0)
		{	    	    	    		    	    	    		
			//if user isn't a friend,
			//then create an entry for them in the database
			addToFriendList(sentBy.getUsername(), "wait", null, challenge.getObjectId(), tScore);				
		}
		
		

		if(tScore == 0)
		{
			//No updates on App DB necessary
		}
		else
		{					
			//save image to database
			Friend friend = db.getFriend(sentBy.getUsername());
			

			//update status
			friend.setStatus("wait");
			if(friend.getObjectId().equals(""))
				friend.setObjectId(challenge.getObjectId());
			
			if (tScore < 10)
			{
				//user won
				friend.setUScore(friend.getUScore() + tScore);
			}
			else // (tScore >= 10)
			{
				//opp won
				friend.setOScore(friend.getOScore() + (tScore / 10));
			}
			
			db.updateFriend(friend);
		}
		

    	
		updateChallengeOnServer(sentBy.getUsername(), challenge.getObjectId(), "Done");
	}
	
	
	
	
	
	public static int calculateWinner(final String friendName, int uScore)
	{
		//opponent's score
		int oScore = db.getFriend(friendName).getTScore();
		int wScore = 0;
		int winner;
		
		Friend friend = db.getFriend(friendName);
		
		if(uScore == oScore)
		{
			//game ends in Draw,
			// no one is awarded points.
			System.out.println("Game ended in a Draw");
			winner = 0;
		}
		else if(uScore > oScore)
		{
			System.out.println("User wins " + uScore);
			//User wins
			//User is awarded points
			wScore = uScore * 10;			
			friend.setUScore((friend.getUScore() + uScore));// add game score to old score
			 // reset TScore

			winner = 1;
		}
		else //(uScore < oScore)
		{
			System.out.println("Opp wins " + oScore);
			//Opponent wins
			//Opponent is awarded points
			wScore = oScore;			
			friend.setOScore((friend.getOScore() + oScore));// add game score to old score

			winner = 2;
		}
		
		friend.setStatus("wait");
		friend.setTScore(-1); //reset TScore
		db.updateFriend(friend);
		
		final int winScore = wScore;
		
		//update Server UserAccount Object
		//retrieve objects from server
	    ParseQuery<ParseObject> query = ParseQuery.getQuery("UserAccount");	    
	    query.getInBackground(db.getFriend(friendName).getObjectId(), new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				if(object != null)
				{
					UserAccount challenge = (UserAccount) object;
					
					challenge.setStatus("Update");
					challenge.setSendTo(friendName);
					challenge.setSentBy(ParseUser.getCurrentUser());
					challenge.setScore(winScore);
					
					challenge.saveInBackground();
				}
				
			}//end Done
		}//end getFirstInBG
	    );
		
		return winner;
	}
	
	public void done(UserAccount challenge)
	{
		ParseUser sentBy= challenge.getParseUser("sentBy");
		
		
		//fetch ParseUser object info.
		try {
			sentBy.fetch();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		//If user is not a friend,
		// make user into a friend.
		if(!isUserAFriend(sentBy.getUsername()) || db.getFriendsCount() == 0)
		{	    	    	    		    	    	    		
			//if user isn't a friend,
			//then create an entry for them in the database
			addToFriendList(sentBy.getUsername(), "fight", null, challenge.getObjectId(), -1);				
		}
		else
		{					
			//save image to database
			Friend friend = db.getFriend(sentBy.getUsername());
			

			//update status
			friend.setStatus("fight");
			if(friend.getObjectId().equals(""))
				friend.setObjectId(challenge.getObjectId());			
			
			db.updateFriend(friend);
		}// end Else //friend is on App DB	
		
		//Should the status be "" or "Done"
		updateChallengeOnServer(sentBy.getUsername(), challenge.getObjectId(), "");
		
	}//end Done Method
	

	
	
}//end UpdateChallenge Class
