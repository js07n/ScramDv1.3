package edu.fsu.cs.scramd.main;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.data.DatabaseHandler;
import edu.fsu.cs.scramd.data.Friend;
import edu.fsu.cs.scramd.data.UserAccount;
import edu.fsu.cs.scramd.friend.*;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MenuScreen extends Activity implements OnClickListener {

	//**************************************************************
	// Declaration
	//**************************************************************
	DialogDifficulty soloD;
	Button soloB;
	Button friendsB;
	Button settingsB;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_screen);
		
		//**************************************************************
		// Initialization 
		//**************************************************************
		soloB = (Button) findViewById(R.id.soloB);
		friendsB = (Button) findViewById(R.id.friendsB);
		settingsB = (Button) findViewById(R.id.settingsB);
		soloD = new DialogDifficulty();
		
		
		addFriendsToDB();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// maybe use this for logout?
		//getMenuInflater().inflate(R.menu.menu_screen, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		if (v == soloB)
		{			
        	// Send Game Type to Dialog Fragment
        	Bundle dialogBundle = new Bundle();	        			        			
			dialogBundle.putString("GameType", "solo");
			dialogBundle.putBoolean("fromMenu", true);
        	soloD.setArguments(dialogBundle);
        	soloD.setCancelable(false);
			soloD.show(getFragmentManager(), "DialogDifficulty");			
		}
		else if(v == friendsB)
		{
			Intent friendIntent = new Intent(this, FriendScreen.class);
			startActivity(friendIntent);
			
		}else if(v == settingsB){
		
			Intent settingsIntent = new Intent(this, Settings.class);
			startActivity(settingsIntent);
		}
	}
	
	
	
	private void addFriendsToDB()
	{
		// *
		// * Retrieve objects from server
	    // *
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserAccount");
	    query.whereEqualTo("sendTo", ParseUser.getCurrentUser().getUsername());
	    query.findInBackground(new FindCallback<ParseObject>(){ //"find" retrieves all results, not just one.

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (objects == null || objects.size() == 0) 
				{
					System.out.println("Object is null");			    	    				    	    	
			    } 
				else 
				{	    	   
					UpdateChallenge updateChallenge = new UpdateChallenge(getApplicationContext());
					for(int i = 0; i < objects.size(); i++)
					{
						UserAccount challenge = (UserAccount) objects.get(i);
		    	    	
						System.out.println("Object is found");
				    	    	
				      	String status = challenge.getString("status");
				    	    	
				      	//testing 03.14.2014	    	    	    	    	
				      	System.out.println("UpdateChallenge created");
			  	    	if(challenge.getString("status") == null)
		    	    	{
			  	    		System.out.println("ChallengeStatus is null");
			  	    		System.out.println(1);
		    	    	}
			  	    	else if(status.equals("Sent"))
				     	{
			  	    		try {
								challenge.getSentBy().fetchIfNeeded();
							} catch (ParseException e1) {
								
								e1.printStackTrace();
							}
			  	    		//CHeck to see If this isn't a duplicated object on server.
			  	    		//(an error on the server. there should only be one game between 
			  	    		// 2 users)
			  	    		if(!UpdateChallenge.isUserAFriend(challenge.getSentBy().getUsername()))
			  	    		{
			  	    			updateChallenge.received(challenge);
			  	    			System.out.print(2.0);
			  	    		}
			  	    		System.out.println(2.1);
				     	}
				     	else if(status.equals("Update"))
				      	{
				     		//03.21.14				     		
				     		updateChallenge.update(challenge);
				     		System.out.println(3);
				    	    		
				    	}
				      	else if(status.equals("Done"))
				      	{
				      		updateChallenge.done(challenge);
				      		System.out.println(4);
				    	    		
				      	}
				      	else if(status.equals("Received"))
				     	{
				     		System.out.println("login activity - attempt to download received challenges");
				     		System.out.println(5);
				     		updateChallenge.received(challenge);
				     		
				     	}
				      	else if(status.equals(""))
				     	{				      					    	    												    
				      		try {
								challenge.getSentBy().fetchIfNeeded();
							} catch (ParseException e1) {
								
								e1.printStackTrace();
							}
					      	UpdateChallenge.addToFriendList(challenge.getSentBy().getUsername(), 
					      			"wait", 
					      			null, 
					      			challenge.getObjectId(), 
					      			challenge.getScore());
				     		System.out.println(5.5);
					      	
				     	}
				      	else
				      		;
					}// end for loop
			    	    	
				}// end if objects.size == 0
			}// end done
		});// end findInBG
	}

}
