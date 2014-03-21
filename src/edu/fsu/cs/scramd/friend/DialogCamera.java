package edu.fsu.cs.scramd.friend;

import java.io.ByteArrayOutputStream;

import com.parse.ParseUser;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.data.DatabaseHandler;
import edu.fsu.cs.scramd.camera.CameraActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

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
	        		//	Toast.makeText(getActivity(), Integer.toString(DiffSelected), Toast.LENGTH_SHORT).show();	        	        				        			
	        			
	        			// Create the text message with a string
	        			Intent sendIntent = new Intent(getActivity(), CameraActivity.class);//camera activity

	        			Bundle bundle = new Bundle();

	        			//Get Friend Name From caller
	        			Intent intent = getActivity().getIntent();	        	        
	        	        b = intent.getExtras();
	        			//if(b == null)
	        			//{
	        				Toast.makeText(getActivity(), "getArguments()", Toast.LENGTH_SHORT).show();
	        	        	b = getArguments();	 
	        			//}
	        			String friendName; 
	        			if(b!= null)
	        			{
	        				friendName = b.getString("friendName");
	        				bundle.putString("friendName", friendName);	 
	        				Toast.makeText(getActivity(), "bundle is not null", Toast.LENGTH_SHORT).show();
	        			}
	        			else
	        			{
	        				// ???
	        				getActivity().finish();
	        			//	Toast.makeText(getActivity(), "bundel is NULL", Toast.LENGTH_SHORT).show();	        				
	        				//return;
	        			}
	        			       			
	        			bundle.putString("currUser", ParseUser.getCurrentUser().getObjectId());
	        			sendIntent.putExtras(bundle);	        		
	        			
	        			startActivity(sendIntent);

	        			
	        			//TESTING TEMP CODE - DOESN'T BELONG HERE
	        			/*
	        			//convert image to byte[]
	        			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.solo4);
	        		    ByteArrayOutputStream stream=new ByteArrayOutputStream();
	        		    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
	        		    byte[] image=stream.toByteArray();
	        			
	        		    //add image to DB & update status
	        			int rowsAffected = db.updateFriend(new Friend(b.getString("friendName"), "play", image));
	        			
	        			Toast.makeText(getActivity(), Integer.toString(rowsAffected), Toast.LENGTH_SHORT).show();
	        			Toast.makeText(getActivity(), b.getString("friendName"), Toast.LENGTH_SHORT).show();
	        			*/
	        			
	        			
	        			//update status on server
	        			
	        			
	        			//TESTING END



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
