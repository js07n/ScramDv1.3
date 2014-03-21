package edu.fsu.cs.scramd.camera;


import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.data.DatabaseHandler;
import edu.fsu.cs.scramd.data.UserAccount;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CameraActivity extends Activity {

	private UserAccount myAccount;
	private DatabaseHandler db;
	
	@Override
	public void onCreate(Bundle savedInstanceState){

		db = new DatabaseHandler(this);
		myAccount = new UserAccount();
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
		
        setContentView(R.layout.frame_camera);
        
        // only  used if a previous UserAccount Object exists on Server
        String objectID = db.getFriend(getIntent().getExtras().getString("friendName")).getObjectId();

        
        
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);                 
        
        if (fragment == null) {
        	fragment = new CameraConfirmFragment();
        	
        	if(getIntent().getExtras().getString("friendName") != null)
        	{
        		String friendName = getIntent().getExtras().getString("friendName");
        		Bundle b = new Bundle();
        		b.putString("friendName", friendName);
        		fragment.setArguments(b);
        		
        	}
        	
            manager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit();
        }
        
        
        //get existing UserAccount from Server if there is one
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserAccount");		
	    query.getInBackground(objectID, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				if(e != null || object == null)
				{
					System.out.println("object is  NULL");
				//	myAccount = new UserAccount();
				}
		        else
		        {
		        	System.out.println("object is NOT null");
		        	myAccount = (UserAccount) object;
		        }	
				
			}//end done
	    }//end getFirstInBG
	    );
        
	}
	
	public UserAccount getCurrentAccount(){
				


		return myAccount;
	}
}
