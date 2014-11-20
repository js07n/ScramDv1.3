package edu.fsu.cs.scramd.main;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.fsu.cs.scramd.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class PushActivity extends Activity {

	ImageView ima;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // this may not be right	   
	    setContentView(R.layout.activity_log_in);
	    
	    
	   // ima = (ImageView)findViewById(R.id.image);
	    
//	    final Bitmap bitmap;
	    
	    //retrieve objects from server
	    ParseQuery<ParseObject> query = ParseQuery.getQuery("challenge");
	    query.whereContains("to_user", "Derp");
	    query.getFirstInBackground(new GetCallback<ParseObject>() {
	    	  public void done(ParseObject object, ParseException e) {
	    	    if (object == null) {
//	    	      Log.d("score", "The getFirst request failed.");

	    	    	
	    	    	
	    	    } else {
//	    	      Log.d("score", "Retrieved the object.");
	    	    	Toast.makeText(getApplicationContext(), "found obj", Toast.LENGTH_SHORT).show();
	    	    	
	    	    	ParseFile newFile = (ParseFile) object.get("image");	    	    	
	    	    	newFile.getDataInBackground(new GetDataCallback() {
	    	    	  public void done(byte[] data, ParseException e) {
	    	    	    if (e == null) {
	    	    	      // data has the bytes for the resume
	    	    	    	
	    	    	    	//not sure if i neeed  !!!!!
//	    	    	    	Bitmap bitmap = BitmapFactory.decodeFile("/path/images/image.jpg");
//	    	    	    	ByteArrayOutputStream blob = new ByteArrayOutputStream();
//	    	    	    	bitmap.compress(CompressFormat.PNG, 
// 							0, //ignored for PNG
// 							blob);
//	    	    	    	byte[] bitmapdata = blob.toByteArray();
	    	    	    	
	    	    	    	Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
	    	    	    	
	    	    	    	ima.setImageBitmap(bitmap);
	    	    	    	
	    	    	    } else {
	    	    	      // something went wrong	    	    	    	
	    	    	    }
	    	    
	    	    	  }
	    	    	});
	    	    	
	    	    }
	    	  }
	    	});
	    
/*	    
	    ParseFile applicantResume = (ParseFile)anotherApplication.get("applicantResumeFile");
	    applicantResume.getDataInBackground(new GetDataCallback() {
	      public void done(byte[] data, ParseException e) {
	        if (e == null) {
	          // data has the bytes for the resume
	        } else {
	          // something went wrong
	        }
	      }
	    });
*/	    
	    

	    
	    
	    
	    
	    
	}

	
	
	
	
}
