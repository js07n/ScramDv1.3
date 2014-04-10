package edu.fsu.cs.scramd.push;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import edu.fsu.cs.scramd.data.UserAccount;
import edu.fsu.cs.scramd.friend.FriendScreen;
import edu.fsu.cs.scramd.friend.UpdateChallenge;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		//This method will update scores on App DB
		//It also lets other user know when opp scores have been updated
		
		final UpdateChallenge updateChallenge = new UpdateChallenge(context);
		
		//retrieve objects from server
	    ParseQuery<ParseObject> query = ParseQuery.getQuery("UserAccount");
	    query.whereEqualTo("sendTo", ParseUser.getCurrentUser().getUsername());
	    //query.whereEqualTo("status", "Update");
	    query.findInBackground(new FindCallback<ParseObject>(){ //"find" retrieves all results, not just one.

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (objects == null || objects.size() == 0) 
				{
								   	
					System.out.println("Push notification Receiver: object not found" +
							" OR error");			    	    				    	    	
			    } 
				else 
				{
					for(int i = 0; i < objects.size(); i++)
					{
						UserAccount challenge = (UserAccount) objects.get(i);
						String status = challenge.getString("status");
						
						if(status.equals("Update"))
				      	{
				     		//04.10.2014			     		
				     		updateChallenge.update(challenge);
				    	    		
				    	}
				      	else if(status.equals("Done"))
				      	{
				      		updateChallenge.done(challenge);
				    	    		
				      	}
				      	else
				      		;
					}
				}// end if objects.size == 0
			}// end done
		});// end findInBG

	}//end onReceive()
			

}//end Receiver UpdateReceiver
