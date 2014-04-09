package edu.fsu.cs.scramd.main;
//********************************************************************************
//*
//* LogIn Activity
//*
//* Description:
//* LogIn screen. Username is the email.
//*
//*	LogIn Button:
//*		Check if valid account in database. Redirect to friend screen if true.
//*	SignUp Button:
//*		Redirect to Sign up Page.
//*
//* TODO:
//* 	Error check. EVERYTHING.
//********************************************************************************


import java.util.List;

import android.os.Bundle;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.data.UserAccount;
import edu.fsu.cs.scramd.friend.FriendScreen;
import edu.fsu.cs.scramd.friend.UpdateChallenge;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LogIn extends Activity {

	//**************************************************************
	// Declaration.
	//**************************************************************
	String userEmail;
	String userPassword;
	String idkPassMail;
	EditText uMail;
	EditText uPass;
	Button signUp;
	Button logIn;
	Intent suIntent;
	Intent menuIntent;
	TextView whatPass;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		Parse.initialize(this, "UjzhrbcURnpbJppWdLLrFzpV3tVJLUfyMi8GhCY2", "ITxk2VtfXhJHTTrvlArAvIjz4Ut9fmhlqMs74RCn");
		
		//*********************************************************
		// Initialization
		//*********************************************************
		uMail = (EditText)findViewById(R.id.mailEt);
		uPass = (EditText)findViewById(R.id.passEt);
		signUp = (Button) findViewById(R.id.suBtn);
		logIn = (Button) findViewById(R.id.logInBtn);
		suIntent = new Intent(this, SignUp.class);
		menuIntent = new Intent(this, MenuScreen.class);
		whatPass = (TextView) findViewById(R.id.whatPassTv);

		//JS - 04.03.2014
		final ParseUser currentUser = ParseUser.getCurrentUser();

		if (currentUser != null && currentUser.getUsername() != null) {
			menuIntent.putExtra("currUser", currentUser.getObjectId());
			startActivity(menuIntent);
			this.finish();
		}
		// JS - END
		
		
		//*********************************************************
		// Sign Up On Click Listener
		//*********************************************************
		signUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(suIntent);
			}
		});
	
		//*********************************************************
		// Log In On Click Listener
		//*********************************************************
		logIn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				
				//Change EditText to String
				userEmail = uMail.getText().toString();
				userPassword = uPass.getText().toString();
				
				if (currentUser != null && currentUser.getUsername() != null) {
					menuIntent.putExtra("currUser", currentUser.getObjectId());
					startActivity(menuIntent);
				} else {


					//*********************************************************
					// LogIn Process
					//*********************************************************
					ParseUser.logInInBackground(userEmail, userPassword, new LogInCallback() {						
						  public void done(ParseUser user, com.parse.ParseException e) {
						    if (user != null) {

						    	// Associate the device with a user						    	
						    	ParseInstallation installation = ParseInstallation.getCurrentInstallation();						 
						    	installation.put("user",ParseUser.getCurrentUser().getUsername());
								installation.saveInBackground(new SaveCallback() {
								
									@Override
									public void done(ParseException e) {
										if(e == null)
										{
											addFriendsToDB();	
									    	menuIntent.putExtra("currUser", ParseUser.getCurrentUser().getObjectId());
								    		startActivity(menuIntent);
								    		finish();

										}
										else
											System.out.println("Can't save installation object");

									}
								});
								//JS END
						    } else {
						    	Toast.makeText(LogIn.this, "Can't log on", Toast.LENGTH_SHORT).show();
						    }
						  }
						});	
				}
			}
		});
		
		//*********************************************************
		// Pass Reset On Click Listener
		//*********************************************************
		whatPass.setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				final EditText passIn = new EditText(LogIn.this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		                  LinearLayout.LayoutParams.MATCH_PARENT,
		                  LinearLayout.LayoutParams.MATCH_PARENT);
				passIn.setLayoutParams(lp); 
				
				// *
				// * Create Dialog to retrieve password.
				// *
		        AlertDialog getPass = new AlertDialog.Builder(LogIn.this).create();
		        getPass.setTitle("Enter Email Address");
		        getPass.setView(passIn);
		        
		        getPass.setButton("OK", new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int which) {
		        		idkPassMail = passIn.getText().toString();
		                        
		        		ParseUser.requestPasswordResetInBackground(idkPassMail,
		        			new RequestPasswordResetCallback() {
		        				public void done(ParseException e) {
		        					if (e == null) {
		        						//Good to Go.
		        					} else {
		        						Toast.makeText(getApplicationContext(), "Error: Unable to retrieve password at this time.", 
		        								Toast.LENGTH_LONG).show();
		        					}
		        				}
		            		});   
		        	}
		         });
		        getPass.show();
			}
		});
		
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
		
	    
	    
	    //This adds friends to App DB if the user has sent challenges to them.
	    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserAccount");
	    query1.whereEqualTo("sentBy", ParseUser.getCurrentUser());
	    query1.findInBackground(new FindCallback<ParseObject>(){ //"find" retrieves all results, not just one.

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
						System.out.println(6);
				    	    	
				      	//String status = challenge.getString("status");
				    	
				      	if(!UpdateChallenge.isUserAFriend(challenge.getSendTo()))
				      	{
				      		
				      		String status = "wait";
				      		if(challenge.getStatus().equals(""))
				      			status = "fight";
				      		
				      		UpdateChallenge.addToFriendList(challenge.getSendTo(), 
				      				status, 
				      				null, 
				      				challenge.getObjectId(), 
				      				challenge.getScore());
				      	}

					}// end for loop
			    	    	
				}// end if objects.size == 0
			}// end done
		});// end findInBG
	    
	}// end AddFriendsToDB MEthod
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}
	
}
