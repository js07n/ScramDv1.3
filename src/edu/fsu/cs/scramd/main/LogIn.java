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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import edu.fsu.cs.scramd.R.color;
import edu.fsu.cs.scramd.data.UserAccount;
import edu.fsu.cs.scramd.friend.FriendScreen;
import edu.fsu.cs.scramd.friend.UpdateChallenge;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
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
		//Parse.initialize(this, "UjzhrbcURnpbJppWdLLrFzpV3tVJLUfyMi8GhCY2", "ITxk2VtfXhJHTTrvlArAvIjz4Ut9fmhlqMs74RCn");
		
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
						    	Toast toast = Toast.makeText(LogIn.this, "Invalid account or no connection", Toast.LENGTH_SHORT);
						    	TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
						    	view.setBackgroundColor(Color.TRANSPARENT);
						    	toast.show();
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
				passRetrieveDialog();
			}
		});
	}
	
	private void passRetrieveDialog(){
		final Dialog dialog = new Dialog(LogIn.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_whatpasstv);
		
		final EditText idkPass = (EditText)dialog.findViewById(R.id.idkpassMail);
		Button submitPass = (Button)dialog.findViewById(R.id.recovBtn);
		submitPass.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				idkPassMail = idkPass.getText().toString();
				
				if(isEmailValid(idkPassMail)){
	        		ParseUser.requestPasswordResetInBackground(idkPassMail,
		        			new RequestPasswordResetCallback() {
		        				public void done(ParseException e) {
		        					if (e == null) {
		        						Toast.makeText(getApplicationContext(), "Done! Please, check your email.", Toast.LENGTH_SHORT).show();
		        					} else {
		        						Toast.makeText(getApplicationContext(), "Unable to retrieve password.", 
		        								Toast.LENGTH_LONG).show();
		        					}
		        				}
		            		});
	        		dialog.dismiss();
				}else
					Toast.makeText(getApplicationContext(), "Invalid Email Address.", Toast.LENGTH_SHORT).show();
			}});
		
		dialog.show();
		
	}
	
	// ***************************************************
	// * method is used for checking valid email id format.
	// *
	// * @param email
	// * @return boolean true for valid false for invalid
	// ***************************************************
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
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
