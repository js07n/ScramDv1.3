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

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.friend.FriendScreen;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends Activity {

	//**************************************************************
	// Declaration.
	//**************************************************************
	String userEmail;
	String userPassword;
	EditText uMail;
	EditText uPass;
	Button signUp;
	Button logIn;
	Intent suIntent;
	Intent menuIntent;
	
	
	
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
		//menuIntent = new Intent(this, FriendScreen.class);
		
		//ParseUser currentUser;

		
		
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
				
				Toast.makeText(getApplicationContext(), "username pass " + userEmail + " " + userPassword, Toast.LENGTH_SHORT).show();
				//For remembering who's logged in
				//doesn't remember if you completely exit the app.
				//Needs work
				//should be in a service instead. whatever. thug life.
				
				//JS COMMENT OUT
				
								
				ParseUser currentUser = ParseUser.getCurrentUser();
				
				
				Toast.makeText(LogIn.this, "Current user is " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();
				
				if (currentUser != null && currentUser.getUsername() != null) {
					Toast.makeText(LogIn.this, "Current user is found! " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();
					menuIntent.putExtra("currUser", currentUser.getObjectId());
					startActivity(menuIntent);
				} else {

//if(true){				
				  ////////////////////////////////////
					// log in proccess 
				///////////////////////////////////
					
					
					
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
									    	menuIntent.putExtra("currUser", ParseUser.getCurrentUser().getObjectId());
								    		startActivity(menuIntent);
										}else
											Toast.makeText(LogIn.this, "cant' save installations obj", Toast.LENGTH_SHORT).show();
										
									}
								});
								//JS END
								
						   // 	menuIntent.putExtra("currUser", user.getObjectId());
						    //	startActivity(menuIntent);
						    } else {
						    	Toast.makeText(LogIn.this, "Can't log on", Toast.LENGTH_SHORT).show();
						    }
						  }
						});	
			////////////////////////////////////
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}

	
	public void logOut(View v)
	{
		
		ParseUser.logOut();
		
		// Associate the device with a user
    	//ParseInstallation installation = new ParseInstallation();
    	ParseInstallation installation = ParseInstallation.getCurrentInstallation();
    	//installation.put("objectId", ParseInstallation.getCurrentInstallation().getObjectId().toString());
    	installation.put("user","");
		installation.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e == null)
					Toast.makeText(getApplicationContext(), ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
				
			}
		});
	}
	
}
