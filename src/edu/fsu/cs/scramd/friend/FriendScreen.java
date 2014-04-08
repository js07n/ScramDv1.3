package edu.fsu.cs.scramd.friend;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.data.DatabaseHandler;
import edu.fsu.cs.scramd.data.Friend;
import edu.fsu.cs.scramd.data.UserAccount;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class FriendScreen extends Activity {

	TabHost tabHost;
	
	ListView lv;
	
	EditText addET;
	EditText removeET;
	
	ParseUser user;
	
	DatabaseHandler db;
	
	DialogCamera cameraD;
	DialogDifficulty playD;
	
	UpdateChallenge updateChallenge;
	
	Timer refreshTimer;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	
	    setContentView(R.layout.activity_friend_screen);
	    
	    db = new DatabaseHandler(this);
	    
	    updateChallenge = new UpdateChallenge(getApplicationContext());
	    
	    user = ParseUser.getCurrentUser();
	    
	    tabHost = (TabHost) findViewById(R.id.tabhost); 
	    tabHost.setup();
	   	    	   
	    TabSpec spec1 = tabHost.newTabSpec("Friend List");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Friend List");

        TabSpec spec2 = tabHost.newTabSpec("Add Friends");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Add Friends");
        
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
             
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				if(tabId.equals("Friend List"))
				{
			        //delete ALL FRIENDS THIS IS FOR TESTING ONLY!!!
			        List<Friend> friends = db.getAllFriends();
			        //Toast.makeText(getApplicationContext(), "db size " + friends.size(), Toast.LENGTH_SHORT).show();
					for(int i = 0; i < friends.size(); i++)
					{
						//friends.get(i).setUsername(Integer.toString(i));
						//friends.get(i).setStatus(Integer.toString(i));
						
						//db.deleteFriend(new Friend(friends.get(i).getUsername()));
						
					//	addToFriendList(friends.get(i).getUsername(), "fight", null);
						//saveList(null);
						//removeFriend(friends.get(i).getUsername());
						
						
					}
					refreshList();	
					downloadChallenges();

				}
			}
		});
        
        // =====================================================================================
        //FRIEND LIST TAB
        
        cameraD = new DialogCamera();
        
        playD = new DialogDifficulty();
        
        lv = (ListView) findViewById(R.id.lv);
        
        //refreshList();
        downloadChallenges();
        //refreshList();

      
		
        // ============================================================================
        // ADD FRIEND TAB
        
        addET = (EditText) findViewById(R.id.addEt);
        removeET = (EditText) findViewById(R.id.removeEt);
        
        

	}
	
	

	public void refreshList()
	{		
		//Toast.makeText(getApplicationContext(), "Tab changed", Toast.LENGTH_SHORT).show();
		
		String[] freunde;
		
		
		
		
		//Check if friendList is null //will crash if it is
		if(user.getJSONArray("friendList") != null)
		{
			//System.out.println("Friendlist is NOT null");
			
			//Retrieve friendlist from user object
			JSONArray jarr = user.getJSONArray("friendList");

			//!!! Show length
			//Toast.makeText(getApplicationContext(), Integer.toString(jarr.length()), Toast.LENGTH_SHORT).show();
			
			// If friendlist is not empty then perform copy
			if(jarr.length() != 0)
			{
				freunde = new String[jarr.length()];
				
				//copy JSONArray elements to string array (freunde)
				for(int i = 0; i < jarr.length(); i++)		
					freunde[i] = jarr.optString(i);		
			}
			else
				freunde = new String[1];  //Prog may break w/o this.
		}	
		else
		{
			freunde = new String[1];						
		}

		
		

		SimpleAdapter adaptert = null;
		
		
		
		//TESTING - GET FRIENDS FROM APP DB		
		if(db.getFriendsCount() != 0)
		{
			//System.out.println("DB does NOT have ZERO friends");
			//Toast.makeText(getApplicationContext(), "DB " + Integer.toString(db.getFriendsCount()), Toast.LENGTH_SHORT).show();
			List<Friend> friends = db.getAllFriendsForAdapter();
			/*
			freunde = new String[db.getFriendsCount()];
			for(int i = 0; i < friends.size(); i++)
				freunde[i] = friends.get(i).getUsername();
			*/
			
			System.out.println("refreshList()");
			
			//Temp solution
			int uScore = 0;
			int oScore = 0;
			
			//Testing JS - 03.30.14
	        // create the grid item mapping
	        String[] from = new String[] {"username", "status", "userScore", "oppScore"};
	        int[] to = new int[] { R.id.lvtv, R.id.lvStatus, R.id.lvScoreMe, R.id.lvScoreFriend};
	 
	        // prepare the list of all records
	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	        for(int i = 0; i < friends.size(); i++){
	            HashMap<String, String> map = new HashMap<String, String>();
	            map.put("username", friends.get(i).getUsername());
	            map.put("status", friends.get(i).getStatus());
	            map.put("userScore", Integer.toString(friends.get(i).getUScore()));
	            map.put("oppScore", Integer.toString(friends.get(i).getOScore()));
	            
	            //TEMP SOLUTION
	            //uScore = db.getFriend(friends.get(i).getUsername()).getUScore();
	            //oScore = db.getFriend(friends.get(i).getUsername()).getOScore();
	            
	            //map.put("userScore", Integer.toString(uScore));
	            //map.put("oppScore", Integer.toString(oScore));
	            // END TEMP SOLUTION
	           //System.out.println(friends.get(i).getUsername() + " " + Integer.toString(friends.get(i).getUScore()));

	            
	            fillMaps.add(map);
	        }
	 
	        // fill in the grid_item layout
	        adaptert = new SimpleAdapter(this, fillMaps, R.layout.item_friend, from, to);
	        
			lv.setAdapter(adaptert);
	        
			//Testing END
			
			
			
			//TESTING 02.25.14
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					// JS - 03.30.2014
					String username = arg0.getAdapter().getItem(arg2).getClass().toString();
										
					@SuppressWarnings("unchecked")
					HashMap<String,String> hm = (HashMap<String, String>) arg0.getItemAtPosition(arg2);
					username = hm.get("username");
					//JS - END
					
					//Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();
					
					//fight Status
					//if(db.getFriend(arg0.getAdapter().getItem(arg2).toString()).getStatus().equals("fight"))
					if(db.getFriend(username).getStatus().equals("fight"))
					{
						//Toast.makeText(getApplicationContext(), 
								//db.getFriend(username).getStatus(), 
								//Toast.LENGTH_SHORT).show();
						
						// Send Friend Name to Dialog Bundle
						Bundle dialogBundle = new Bundle();	        			        			
						dialogBundle.putString("friendName", username);					
						cameraD.setArguments(dialogBundle);
						//cameraD.setCancelable(false);
						cameraD.show(getFragmentManager(), "DialogCamera");
					}
					//play Status
					else if(db.getFriend(username).getStatus().equals("play"))
					{
						//Toast.makeText(getApplicationContext(), 
								//db.getFriend(username).getStatus(), 
								//Toast.LENGTH_SHORT).show();
						
						//String friendName = arg0.getAdapter().getItem(arg2).toString();
						
			        	// Send Game Type to Dialog Fragment
			        	Bundle dialogBundle = new Bundle();	        			        			
						dialogBundle.putString("GameType", "friend");
						dialogBundle.putString("friendName", username);
						//dialogBundle.putBoolean("fromMenu", true);
			        	playD.setArguments(dialogBundle);
			        	playD.setCancelable(false);
						playD.show(getFragmentManager(), "DialogDifficulty");						
					}
					else
					{
						//Toast.makeText(getApplicationContext(), 
							//	db.getFriend(username).getStatus(), 
								//Toast.LENGTH_SHORT)
								//.show();
					}
					
					
				}
			});
			
		}//DB has zero friends
		else
		{
			//Toast.makeText(getApplicationContext(), "DB has ZERO entries", Toast.LENGTH_SHORT).show();
			//03.30.2014
			freunde[0] = new String("");
/*			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_friend, 
					R.id.lvtv, freunde);
			
			lv.setAdapter(adapter);
*/			
		//END TESTING
		}


		

		



		
	}
	

	
	
	private void downloadChallenges()
	{
		//retrieve objects from server
	    ParseQuery<ParseObject> query = ParseQuery.getQuery("UserAccount");
	    query.whereEqualTo("sendTo", ParseUser.getCurrentUser().getUsername());
	    query.findInBackground(new FindCallback<ParseObject>(){ //"find" retrieves all results, not just one.

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (objects == null || objects.size() == 0) 
				{
//			   		Log.d("score", "The getFirst request failed.");
					System.out.println(" downloadChallenges() - Object is null");			    	    				    	    	
			    } 
				else 
				{
//			   		Log.d("score", "Retrieved the object.");
					//Toast.makeText(getApplicationContext(), "found obj", Toast.LENGTH_SHORT).show();
			    	    	
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
		    	    	}	    	    	
				     	else if(status.equals("Sent"))
				     	{
				     		updateChallenge.received(challenge);
				     	}
				     	else if(status.equals("Update"))
				      	{
				     		//03.21.14				     		
				     		updateChallenge.update(challenge);
				    	    		
				    	}
				      	else if(status.equals("Done"))
				      	{
				      		updateChallenge.done(challenge);
				    	    		
				      	}
				      	else
				      		;
					}// end for loop
			    	    	
				}// end if objects.size == 0
			}// end done
		});// end findInBG
/*	    
	    query.getFirstInBackground(new GetCallback<ParseObject>() {
	    	  public void done(ParseObject object, ParseException e) {
	    	    if (object == null) {
//	    	      Log.d("score", "The getFirst request failed.");
	    	    	System.out.println("Object is null");
	    	    	
	    	    	
	    	    } else {
//	    	      Log.d("score", "Retrieved the object.");
	    	    	Toast.makeText(getApplicationContext(), "found obj", Toast.LENGTH_SHORT).show();
	    	    	
	    	    	UserAccount challenge = (UserAccount) object;
	    	    	
	    	    	System.out.println("Object is found");
	    	    	
	    	    	String status = challenge.getString("status");
	    	    	
	    	    	//testing 03.14.2014	    	    	    	    	
	    	    	System.out.println("UpdateChallenge created");
	    	    	if(challenge.getString("status") == null)
	    	    	{
	    	    		System.out.println("ChallengeStatus is null");
	    	    	}	    	    	
	    	    	else if(status.equals("Sent"))
	    	    	{
	    	    		updateChallenge.received(challenge);
	    	    	}
	    	    	else if(status.equals("Update"))
	    	    	{
	    	    		
	    	    		
	    	    	}
	    	    	else if(status.equals("Done"))
	    	    	{
	    	    		//change sentTo so that user stops downloading Challenge
	    	    		
	    	    	}
	    	    	
	    	    	
	    	       }
	    	  }
	    	  
	    	}); // end getFirstInBackground
*/		
		refreshList();
	}
	
	
	

	public void addFriend(View v)
	{		
		//TESTING !!
		//Toast.makeText(getApplicationContext(), user.getUsername(), Toast.LENGTH_SHORT).show();

		//1. Check to see if User doesn't ALREADY have friend in friendlist
		if(UpdateChallenge.isUserAFriend(addET.getText().toString()) || addET.getText().toString().equals(""))
			Toast.makeText(getApplicationContext(), "Invalid Request", Toast.LENGTH_SHORT).show();
		else
		{
			//2. Check Server info to see if username exists.				
			ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");		
			//query.whereContains("username", addET.getText().toString());
			query.whereEqualTo("username", addET.getText().toString());
		
			query.getFirstInBackground(new GetCallback<ParseUser>(){

				@Override
				public void done(ParseUser object, ParseException e) {
				
					// 	2.1 If username exists, add it to user's friend list
					if(e == null)
					{
						addToFriendList(object.getString("username"), "fight", null);
						saveList(addET);
								
					}				
					else // 2.2 If username doesn't exist, print out an error message.
					{
						Toast.makeText(getApplicationContext(), "User NOT FOUND", Toast.LENGTH_SHORT).show();
						addET.setText("");
					}
				}
			});
		}
				
	}


	
	
	private void addToFriendList(String un, String status, byte[] img)
	{
		Toast.makeText(getApplicationContext(), "User Found", Toast.LENGTH_SHORT).show();
		
		//Add to app DB
		db.addFriend(new Friend(un, status, img, -1)); //"-1" means that there is no previous score
		
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
								
	}
	
	
	
	public void removeFriend(View v)
	{		
		//Checks to see if the friend
		if(UpdateChallenge.isUserAFriend(removeET.getText().toString()))
		{
			//Cancel method if friendlist is null (empty)
			if(user.getJSONArray("friendList") == null)
				return;
			
			String remFriend = "";
			
			// This creates a new copy of JSONArray with the omission of the deleted friend		
			JSONArray jarr = new JSONArray();								
		
			for(int i = 0; i < user.getJSONArray("friendList").length(); i++)
			{
				// don't add "deleted" friend
				if(removeET.getText().toString().equals(user.getJSONArray("friendList").optString(i)))
				{
					//remove friend from ap DB
					remFriend = user.getJSONArray("friendList").optString(i);
				}
				else
					jarr.put(user.getJSONArray("friendList").optString(i));
			}

			//remove friend from app DB
			db.deleteFriend(new Friend(remFriend));
			
			//Update friendlist 
			user.put("friendList", jarr);
			
			//Save friendlist
			saveList(removeET);
		}
	}
	
	
	private void saveList(final EditText et)
	{
		user.saveInBackground(new SaveCallback() {			
			@Override
			public void done(ParseException e) {
				if(e == null)
				{
					if(et != null)
					{
						Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
						et.setText("");
					}
				}
				else
				{
					if(et != null)
					{
						Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
						et.setText("");
					}
				}
			}
		});
									
	}//end saveList()
	
	
	public void onResume()
	{
		super.onResume();
		downloadChallenges();
		//refreshList();
	}


	protected void onDestroy() {
	    super.onDestroy();
	    if (db != null) {
	        db.close();
	    }

	}
}
