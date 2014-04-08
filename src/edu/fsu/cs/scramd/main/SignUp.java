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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	EditText emailText;
	EditText userText;
	EditText pass2Text;
	EditText pass3Text;
	String username, email, password;
	Button submitButton;
	Button cancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		//**************************************************************
		// Initialization
		//**************************************************************
		userText = (EditText) findViewById(R.id.userEt);
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
		
			// *********************************************************
			// * Sign Up error checks.
			// *	Email validated through isEmailValid()
			// * 	Password
			// *	 Error Displayed when: String == null or == " "
			// * 						   String < 2 or pass2 != pass3
			// *********************************************************
			if(!isEmailValid(emailText.getText().toString()))
				emailText.setError("Email is Invalid. Please Re-Enter.");
			
			else if(pass2Text.getText().toString() == null || pass2Text.getText().toString() == " "
					|| pass2Text.getText().toString().length() < 2)
				pass2Text.setError("Password is invalid. Please choose another password.");
			
			else if(!pass2Text.getText().toString().equals(pass3Text.getText().toString()))
			{
				pass3Text.setError("Password does not match.");
			
				//Toast.makeText(getApplicationContext(), pass2Text.getText().toString(), Toast.LENGTH_SHORT).show();
				//Toast.makeText(getApplicationContext(), pass3Text.getText().toString(), Toast.LENGTH_SHORT).show();
			}
			else{	
				//Clears errors once entries are valid.
				emailText.setError(null);
				pass2Text.setError(null);
				
				username = userText.getText().toString();
				email = emailText.getText().toString();
				password = pass2Text.getText().toString();
				
				//*********************************************************
				// Save new user data into Parse.com Data Storage
				//*********************************************************
				ParseUser user = new ParseUser();
				user.setUsername(username);
				user.setEmail(email);
				user.setPassword(password);

				
				user.signUpInBackground(new SignUpCallback(){
					public void done(com.parse.ParseException e){
						if(e==null){
							finish();
						}else{
							//It Didn't Work
							Toast.makeText(getApplicationContext(), "Sign Up Error.", Toast.LENGTH_SHORT).show();
						}
					}
					});
				};
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
	
	// **********************************
	// * Checks if the string contains
	// * only characters.
	// **********************************
	public boolean isAlpha(String name) {
	    char[] chars = name.toCharArray();

	    for (char c : chars) {
	        if(!Character.isLetter(c)) {
	            return false;
	        }
	    }

	    return true;
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
}

