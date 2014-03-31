package edu.fsu.cs.scramd.main;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import edu.fsu.cs.scramd.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity implements OnClickListener {

	TextView changePassTv, deleteAccTv, 
		aboutTv, ppTv, tosTv, loTv;
	
	EditText passAtv, passBtv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_screen);
		
		changePassTv = (TextView) findViewById(R.id.passEdit);
		deleteAccTv = (TextView) findViewById(R.id.deleteAcc);
		aboutTv = (TextView) findViewById(R.id.about);
		ppTv = (TextView) findViewById(R.id.pp);
		tosTv = (TextView) findViewById(R.id.tos);	
		loTv = (TextView) findViewById(R.id.lo);
		passAtv = (EditText) findViewById(R.id.newPassword1);
		passBtv = (EditText) findViewById(R.id.newPassword2);

		
	}

	@Override	
	public void onClick(View v) {
		if( v == changePassTv){
			showCustomDialog();
		}
		else if( v == deleteAccTv){
			showDeleteDialog();
		}
		else if(v == aboutTv){
			Intent i = new Intent(this, About.class);
			startActivity(i);
		}
		else if(v == ppTv){
			String url = "https://parse.com/about/privacy";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
		else if(v == tosTv){
			String url = "https://parse.com/about/terms";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
		else if(v == loTv){
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
						;
				}
			});
			
			//Clears Activities on stack before returning to Login screen.
			Intent intent = new Intent(this, LogIn.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}
	
	protected void showCustomDialog(){
		final Dialog dialog = new Dialog(Settings.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_change_pass);
		
		final EditText newPassA = (EditText)dialog.findViewById(R.id.newPassword1);
		final EditText newPassB = (EditText)dialog.findViewById(R.id.newPassword2);
		
		Button submitBtn = (Button)dialog.findViewById(R.id.goNewBtn);
		submitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ParseUser currUser = ParseUser.getCurrentUser();
				
				if(newPassA.getText().toString() == null || newPassA.getText().toString() == " "
						|| newPassA.getText().toString().length() < 2)
					newPassA.setError("Password is invalid. Please choose another password.");
				
				else if(!newPassA.getText().toString().equals(newPassB.getText().toString()))
					newPassB.setError("Password doesn't match.");
				else{
					newPassA.setError(null);
					newPassB.setError(null);
					
					currUser.setPassword(newPassA.getText().toString());
					currUser.saveInBackground();
					
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), newPassA.getText().toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		Button cancelBtn = (Button)dialog.findViewById(R.id.noNewBtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
			}
		});
		
		dialog.show();
		
	}
	
	protected void showDeleteDialog(){
		AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
		
		//Set Title
		alert.setTitle("Deleting Account")
		.setMessage("Are you sure you want to delete your account? All your information will be lost.")
		.setCancelable(false)
		.setPositiveButton("Yes, delete my account.", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), "w/e i don't like u anyway.", Toast.LENGTH_SHORT).show();
				
					
				ParseUser current = ParseUser.getCurrentUser();
				current.deleteInBackground();
				ParseUser.logOut();
				
				//Clears Activities on stack before returning to Login screen.
				Intent intent = new Intent(getApplicationContext(), LogIn.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				
				
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), "good ish pimp", Toast.LENGTH_SHORT).show();
				
			}
		});
		
		//Create it
		AlertDialog alertDialog = alert.create();
		//Show It
		alertDialog.show();
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

}
