package edu.fsu.cs.scramd.friend;

import com.parse.ParseUser;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.data.DatabaseHandler;
import edu.fsu.cs.scramd.camera.CameraActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class DialogCamera extends DialogFragment {
	
	Bundle b;
	
	DatabaseHandler db;
	
	 @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

			
	        
	        db = new DatabaseHandler(getActivity());
		 

	        
		 	
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	       
	        builder.setTitle(R.string.send_challenge)

	        	.setPositiveButton(R.string.take_photo, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        	        				        			
	        			
	        			// Create the text message with a string
	        			Intent sendIntent = new Intent(getActivity(), CameraActivity.class);//camera activity

	        			Bundle bundle = new Bundle();

	        			//Get Friend Name From caller
	        			Intent intent = getActivity().getIntent();	        	        
	        	        b = intent.getExtras(); //??
	        	        b = getArguments();	 

	        			String friendName; 
	        			if(b!= null)
	        			{
	        				friendName = b.getString("friendName");
	        				bundle.putString("friendName", friendName);	 
	       
	        			}
	        			else
	        			{	     
	        				getActivity().finish();
	        			}
	        			       			
	        			bundle.putString("currUser", ParseUser.getCurrentUser().getObjectId());
	        			sendIntent.putExtras(bundle);	        		
	        			
	        			startActivity(sendIntent);

	        		}
	        	}) 
	        	
	        	// Cancel Button
	        	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        		
	        			
	        		}
	        	});
	        	
	        	
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }


}
