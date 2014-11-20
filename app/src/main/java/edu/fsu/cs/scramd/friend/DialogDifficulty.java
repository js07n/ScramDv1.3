package edu.fsu.cs.scramd.friend;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.game.GameScreen;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DialogDifficulty extends DialogFragment {

	int DiffSelected = 0;
	
	Bundle b;
	
	 @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
		 		 			
			//Get Game Type from caller
			Intent intent = getActivity().getIntent();	        	        
	        b = intent.getExtras();		 //??
	        b = getArguments();	 
	        		 	
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	       
	        builder.setTitle(R.string.dialog_diff)
	        	

	        	.setSingleChoiceItems(R.array.Difficulty, 0, // 0 is default selection
	        			new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DiffSelected = which;
								
							}
						})            
	        	
				// Play Button
	        	.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {        	        				        			
	        			
	        			// Create the text message with a string
	        			Intent sendIntent = new Intent(getActivity(), GameScreen.class);

	        			Bundle bundle = new Bundle();
	        			
       			
	        			
	        			String GameType; 
	        			if(b!= null)
	        			{
	        				GameType = b.getString("GameType");
	        				bundle.putString("friendName", b.getString("friendName"));
	        			}
	        			else
	        				GameType = "solo";
	        			
	        			bundle.putInt("Difficulty", DiffSelected);
	        			bundle.putString("GameType", GameType);	        			
	        			
	        			sendIntent.putExtras(bundle);
	        		
	        			
	        			startActivity(sendIntent);
	        			
	        			getActivity().finish();

	        			// reset value to default
	        			DiffSelected = 0;

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

