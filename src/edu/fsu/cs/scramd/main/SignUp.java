package edu.fsu.cs.scramd.main;
//********************************************************************************
//*
//* SignUp Activity
//*
//* Description:
//*	SignUp Page. Name, Email, Password is REQUIRED.
//*	Must check if user exists. If not, grant perm for new account.
//*			   if user exists -> deny permission. Warn user.
//*	Submit Button: Redirect to Login if new user granted.
//*				   Display Error if new user denied.
//*	Cancel Button: Redirect to LogIn Screen.
//*
//* TODO:
//* 	Error Check.
//*	No repeating Users.
//*	All fields must be valid.
//*	Pass must be the same.
//********************************************************************************


import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import edu.fsu.cs.scramd.R;

import android.R.string;
import android.app.Activity;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends Activity{
	
	//**************************************************************
	// Declaration
	//**************************************************************
	EditText firstText;
	EditText lastText;
	EditText emailText;
	EditText pass2Text;
	EditText pass3Text;
	String firstName, lastName, email, password;
	Button submitButton;
	Button cancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		//**************************************************************
		// Initialization
		//**************************************************************
		firstText = (EditText) findViewById(R.id.firstEt);
		lastText = (EditText) findViewById(R.id.lastEt);
		emailText = (EditText) findViewById(R.id.emailEt);
		pass2Text = (EditText) findViewById(R.id.pass2Et);
		pass3Text = (EditText) findViewById(R.id.pass3Et);
		submitButton = (Button) findViewById(R.id.subBtn);
		cancelButton = (Button) findViewById(R.id.cancelBtn);
		
		//**************************************************************
		// Submit Button Click Listener
		//**************************************************************
		submitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
			firstName = firstText.getText().toString();
			lastName = lastText.getText().toString();
			email = emailText.getText().toString();
			password = pass2Text.getText().toString();
			
			//*********************************************************
			// Save new user data into Parse.com Data Storage
			//*********************************************************
			ParseUser user = new ParseUser();
			user.setUsername(email);
			//user.setEmail(email);
			user.setPassword(password);
			//user.put("FirstName", firstName);
			//user.put("LastName", lastName);
			
			user.signUpInBackground(new SignUpCallback(){
				public void done(com.parse.ParseException e){
					if(e==null){
						firstText.setText("");
						finish();
					}else{
						//itdidntwork
						Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
					}
				}
			});
			}
		});
		
		//************************************************************
		// Cancel Button Click Listener
		//************************************************************
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}
}

