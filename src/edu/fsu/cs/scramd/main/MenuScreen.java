package edu.fsu.cs.scramd.main;

import java.util.List;

import com.parse.ParseUser;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.data.DatabaseHandler;
import edu.fsu.cs.scramd.data.Friend;
import edu.fsu.cs.scramd.friend.*;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MenuScreen extends Activity implements OnClickListener {

	
	Button soloB;
	DialogDifficulty soloD;
	
	Button friendsB;
	Button settingsB;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_screen);
		
		soloB = (Button) findViewById(R.id.soloB);
		soloD = new DialogDifficulty();
		
		friendsB = (Button) findViewById(R.id.friendsB);
		settingsB = (Button) findViewById(R.id.settingsB);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// maybe use this for logout?
		//getMenuInflater().inflate(R.menu.menu_screen, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		if (v == soloB)
		{			
        	// Send Game Type to Dialog Fragment
        	Bundle dialogBundle = new Bundle();	        			        			
			dialogBundle.putString("GameType", "solo");
			dialogBundle.putBoolean("fromMenu", true);
        	soloD.setArguments(dialogBundle);
        	soloD.setCancelable(false);
			soloD.show(getFragmentManager(), "DialogDifficulty");			
		}
		else if(v == friendsB)
		{
			Intent friendIntent = new Intent(this, FriendScreen.class);
			//friendIntent.putExtra("currUser", ParseUser.getCurrentUser().getObjectId());
			startActivity(friendIntent);
			
		}else if(v == settingsB){
		
			Intent settingsIntent = new Intent(this, Settings.class);
			startActivity(settingsIntent);
		}
	}

}
