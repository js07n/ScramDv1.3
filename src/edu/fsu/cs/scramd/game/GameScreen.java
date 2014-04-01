package edu.fsu.cs.scramd.game;

import java.util.ArrayList;
import java.util.Random;

import edu.fsu.cs.scramd.R;
import edu.fsu.cs.scramd.data.DatabaseHandler;
import edu.fsu.cs.scramd.data.Friend;
import edu.fsu.cs.scramd.friend.DialogDifficulty;
import edu.fsu.cs.scramd.friend.UpdateChallenge;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameScreen extends Activity implements View.OnTouchListener{

	DatabaseHandler db;
	
	DialogDifficulty soloD;
	
	ViewGroup _root;
	
	ImageView mImage;
	
	ImageView display[];
	
	String GameType;
	int DIFFICULTY; //constant ?
	int NumOfPieces; //DIFFICULTY * DIFFICULTY
	
	// friend play variable
	String friendName;
	
    Bitmap bitmap;	
	ArrayList<Bitmap> bArray;
	
	//width (or height) of image piece
	int chunk;
	
    int TrackArray[];
	
	//This lock will be used to prevent more than one
	// piece from being moved at once.
    int lock;	
	
    
    // this will be used to position collection of game pieces
    // these are coordinates for top-left corner of pieces
    private static int xOFFSET;
    private static int yOFFSET;
	

    // record initial coordinates from where piece was picked up from.
    float xInit = 0;
    float yInit = 0;
    
    
	private int _xDelta;
	private int _yDelta;
    
	// these Rect objects will hold the coordinates that will specify each 
	// region of the image.
	// image pieces will be dragged onto these coordinates.
	Rect[] bounds;
	
    
	// sec*1000 = time (EX: 30sec*1000 = 30000)
	private static long time;//, timeTick; //timeTick is used for activity state resume,stop,etc
	MyCounter cdTimer;
	//CountDownTimer cdTimer;
	TextView timerTV;
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
              
        setContentView(R.layout.activity_game_screen);
        
        db = new DatabaseHandler(this);

        //create Dialog for win condition.
        soloD = new DialogDifficulty();
        
        //set up ViewGroup //start from a blank XML file
        //Image pieces will be attached to ViewGroup
        
        _root = (ViewGroup)findViewById(R.id.root);
        


        
        // ***************************************************************************************************
        //Fetch Difficulty
        
        Intent intent = getIntent();
        
        Bundle bundle = intent.getExtras();
        
      
        //TESTING !!!
        //GameType = "solo";
        
        //TESTING !!!
        DIFFICULTY = 5;  //not using! temporary filler
        // 3 = EASY, 4 = MEDIUM, 5 = HARD
        
        //TESTING SEE IF VARIABLE ARE IN BUNDLE
        
        if(bundle == null)
        {
        	Toast.makeText(getApplicationContext(), "bundle is empty", Toast.LENGTH_SHORT).show();
        	
        }
        else
        {
        	Toast.makeText(getApplicationContext(), "bundle is NOT empty", Toast.LENGTH_SHORT).show();
        	Toast.makeText(getApplicationContext(), bundle.getString("friendName"), Toast.LENGTH_SHORT).show();
        }
        
        // check to see if there is anything in the bundle
        if(bundle != null)
        {
        	GameType = bundle.getString("GameType");
        	//GameType = "solo";
        	
        	// + 3 is an OFFSET added to make easy = 3 instead of 0, etc.
        	// the importance of this is so that the image is cut up properly.
        	DIFFICULTY = bundle.getInt("Difficulty") + 3;
        	
        	if(bundle.getString("friendName") != null)
        		friendName = bundle.getString("friendName");
        }
        
        
        //TESTING !!!
        Toast.makeText(getApplicationContext(), GameType, Toast.LENGTH_SHORT).show();
        
        NumOfPieces = DIFFICULTY * DIFFICULTY;
        
        
        // change these offsets to determine where the moving pieces will be on the screen.
        // offsets are 3*(paddingValue in XML file)
        xOFFSET = 90;
        yOFFSET = 300 + 75;  // 75 will be added to the value bc of the header. 
        // Header = the black strip that tells the time, battery life, etc.
        // fixed!! changed theme in Manifest file.  set it to no title bar.
        
        //JS - TESTING - 03.30.2014
        
        Display screenDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        screenDisplay.getSize(size);

        	xOFFSET = (size.x / 10) / 2;
        	yOFFSET = ((size.y / 4) / 2 )+ 75;
	
        //TESTING END
        		
        // ***********************************************************************************************
        
    	//Fetch Image  
    	//Convert to bitmap?
        
        Bitmap b;
        
        if(GameType.equals("solo"))
        {
        	Random rand = new Random();
                        
        	int pic = getPic(rand.nextInt(4)+1);        
        
        	//Call Garbage Collector to clean memory.
        	// If GC doesn't do this, then you will get java.lang.OutOfMemory Error
        	//System.gc();  //THIS DOESN'T WORK!!! !!!

        	b = BitmapFactory.decodeResource(getResources(), pic); // not using! TEMP FILLER // maybe using for solo
       	
        	 
        }
        else //friend play
        {
        	DatabaseHandler db = new DatabaseHandler(this);
        	Friend friend = db.getFriend(bundle.getString("friendName"));
        	b = BitmapFactory.decodeByteArray(friend.getIMG(), 
        			0, 
        			friend.getIMG().length);
        	
        }
        
        bArray = new ArrayList<Bitmap>();
        b = Bitmap.createBitmap(b);
        Bitmap temp = null;  // used for setting up bitmap Array
        
        // width and height of image pieces
        // Since pieces are squares, width = height
        //chunk = b.getWidth()/DIFFICULTY; //only works if image is 900x900
        int iSize = (size.x/10) * 9;

        
        
        chunk = iSize / DIFFICULTY; //TEMPORARY SOLUTION?
        
        
        //Recalibrate iSize so that it prevents further mathematical precision errors
        iSize  = (iSize / DIFFICULTY) * DIFFICULTY;
        
        Log.v("CHUNK", Integer.toString(chunk));
        Log.v("real width", Integer.toString(b.getWidth()));
        Log.v("real heigt", Integer.toString(b.getHeight()));
  
        // Break image(bitmap) into pieces and store into array (bitmap)
        for(int y = 0; y < DIFFICULTY; y++)
        {
        	for(int x = 0; x < DIFFICULTY; x++)
        	{
        		
        		temp = Bitmap.createBitmap(b,x*(b.getWidth()/DIFFICULTY), y*(b.getHeight()/DIFFICULTY), 
        			b.getWidth()/DIFFICULTY, b.getHeight()/DIFFICULTY); 
        		bArray.add(temp);
        	}
        }

        Log.v("SIZE OF ARRAY", Integer.toString(bArray.size()));   
        
        //create array of displays for image pieces
        display = new ImageView[NumOfPieces];    

        
        // Create and Randomize tracking array
        // Array that keeps track of position of pieces
        // create displays
        TrackArray = new int[NumOfPieces];
        for (int i = 0; i < NumOfPieces; i++)
        {
        	// Assigning TrackArray
        	TrackArray[i] = i; 
        	
        	//Initializing Displays
        	display[i] = new ImageView(this);
        	
        }
        
        
     
        //Fisher–Yates shuffle
        //Randomize tracking array
        Random rnd = new Random();
        for (int i = TrackArray.length - 1; i > 0; i--)
        {
        	int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = TrackArray[index];
            TrackArray[index] = TrackArray[i];
            TrackArray[i] = a;
        }
       
        
        
      
 //       Log.v("NumOfPieces", Integer.toString(NumOfPieces));

        //set parameters for each display
        // TrackArray is used to determine which display goes where on screen
        int z = 0;
        
        for(int y = 0; y < iSize; y += chunk)
        {
        	for(int x = 0; x < iSize; x += chunk)
        	{
        		
    			RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(iSize, iSize); 
    			lparams.leftMargin = x;
    			lparams.topMargin = y;
    			lparams.width = chunk;
    			lparams.height = chunk;
    			display[TrackArray[z++]].setLayoutParams(lparams);

    			Log.v("x  y", Integer.toString(x) + "  " + Integer.toString(y));
    			//!!! this is temp patch up to fix math issues
        	}      	        	        	
        }
        
        
        //TESTING 03.31.2014 - JS
        _root.setPadding(xOFFSET, yOFFSET-75, 0, 0);
        
        System.out.println("Padding complete");
        
        for (int i = 0; i < NumOfPieces; i++)
        {
        	//attach bitmap pieces to displays
        	display[i].setImageBitmap(bArray.get(i)); 
        	
        	// Make displays(image pieces) available to touch gesture
        	display[i].setOnTouchListener(this);
        	
        	// Attach displays(image pieces) to ViewGroup
        	_root.addView(display[i]);
        }



        
        lock = 1;
        
        
        //Create boundary coordinates for each region that images can be placed on.
        bounds = new Rect[NumOfPieces];
        
        z = 0;
        
    	for(int y = (0 + yOFFSET); y < (iSize + yOFFSET); y += chunk)
    	{
    		for(int x = (0 + xOFFSET); x < (iSize + xOFFSET); x += chunk)
    		{
		
    			bounds[z++] = new Rect (x, y, x+chunk, y+chunk);

    			Log.e("x  y", Integer.toString(x) + "  " + Integer.toString(y));
    			
    		}
    		
    	}


    	
    	
    	
    	//if(GameType.equals("solo"))
    	//{
    		// TIMER !!!
        	//timerTV = (TextView) findViewById(R.id.timerTV);
    	
    		timerTV = new TextView(this);
    		timerTV.setTextSize(size.x/30);
    		timerTV.setGravity(1);
    		
    		
    		//timerTV.settext
    		timerTV.setBackgroundColor(Color.BLACK);
    		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
    				LayoutParams.WRAP_CONTENT, 
    				LayoutParams.WRAP_CONTENT);
    		//LayoutParams params = new LayoutParams(size.x, size.y);
    		
    		params.width = size.x / 3;
    		
    		params.height = LayoutParams.WRAP_CONTENT;
    		
    		params.setMargins((size.x/2) - (params.width/2) - xOFFSET, iSize + (size.y/8), 0, 0);
    		//params.setMargins(100, 0, 0, 0);
    		//timerTV.setLayoutParams(params);
    		_root.addView(timerTV, params);
    		
    		//this.addContentView(timerTV, params);

    		
    		time = 30000;
    		//startCDTimer();
    		final MyCounter timer = new MyCounter(time,1000);
    	//} 
    	
    	
     
    }



    public class MyCounter extends CountDownTimer{

        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
			Toast.makeText(getApplicationContext(), "LOST!", Toast.LENGTH_SHORT).show();
			
			timerTV.setText("Done!");
			time = 0;
			
			if(GameType.equals("solo"))
			{
				// Send Game Type to Dialog Fragment
				Bundle countBundle = new Bundle();	        			        			
				countBundle.putString("GameType", GameType);
				soloD.setArguments(countBundle);
				soloD.setCancelable(false);
				// Launch Dialog Fragment
				soloD.show(getFragmentManager(), "DialogDifficulty");
			}
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timerTV.setText((millisUntilFinished/1000)+"");
            System.out.println("Timer  : " + (millisUntilFinished/1000));
            time = millisUntilFinished;
        }
    }
    
    
    @Override
    public void onPause()
    {
    	super.onPause();
//  	   if(cdTimer != null)    	
    	//if(GameType.equals("solo"))
 		   cdTimer.cancel();
 		   //cdTimer = null;
 		   //time = timeTick;
  	   
 	   	   
    }
    
 
    
    
    @Override
    public void onResume()
    {
    	super.onResume();
  	//   if(cdTimer != null)
 		   //startCDTimer();  //this doesn't work
//    	if(GameType.equals("solo"))
  //  	{
    		if(time >= 1)
    		{
    			Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();
    			cdTimer = new MyCounter(time, 1000);
    			cdTimer.start();  
    		}
    //	}
  	   
  		 	   
    }
    
    
    
    
    private int getPic(int i) {
		
    	switch(i)
    	{
    		case 1:
    			return R.drawable.solo1;   
    		case 2:
    			return R.drawable.solo2;
    		case 3:
    			return R.drawable.solo3;
    		default:
    			return R.drawable.solo4;    			
    	}
		
	}




     
    


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
 //       getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }





    
   

    
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
    	final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
       	
            	
            	// IF lock hasn't been placed 
            	// (one piece is currently being touched & dragged)
            	// the grab lock and record parameters
            	// ELSE fail (cancel touch event)
            	if(lock == 1)
            	{            	
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    _root.bringChildToFront(v);
                
                    xInit = lParams.leftMargin;
                    yInit = lParams.topMargin;
                
                    lock = 0;
            	}
            	else
            		return false;
            	
            	
            	               
                break;
                
            case MotionEvent.ACTION_UP:  // this code executes when user "releases" image piece
            	//SNAP IMAGES INTO SPECIFIC PLACES AND SWITCH THEM OUT WITH OTHER PIECE

                RelativeLayout.LayoutParams layParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                
                //?????             
                layParams.leftMargin =   (int) xInit;
                layParams.topMargin =   (int) yInit;

                //Array Position
                int AP = 0;


                // This records the coordinates for the region where the pieces is released.
                for (int i = 0; i < bounds.length; i++)
                {
                	Log.v("DERP", "ippers " + Integer.toString(i));
                	//The bounds for the layout have to be different because it
                	//is relative to the viewgroup NOT the screen.
                	// Therefore, they must start at (0,0) despite what offset they have on 
                	// the screen.  Hence ( - OFFSET)
                	if(bounds[i].contains(X, Y))
                	{
                		layParams.leftMargin = bounds[i].left - xOFFSET; 
                		layParams.topMargin = bounds[i].top - yOFFSET;
                		AP = i;
                		Log.v("DERP1", "left top  " + Integer.toString(layParams.leftMargin) + " " + 
                				Integer.toString(layParams.topMargin));
                		break;  //exit for loop
                	       
                	}
                	

                	//exit touch event if image piece released out of bounds.
                	if(i == (bounds.length-1))
                	{
                  	   Toast.makeText(getApplicationContext(), "out of bounds", Toast.LENGTH_SHORT).show();
                 	   lock = 1;
                 	   v.setLayoutParams(layParams);
                 	   //break;
                 	   return false;
                	}
                		
                }      
                
                
                Log.v("test", "index=" + 1);
                v.setLayoutParams(layParams);
                
                // scan through image pieces to find 
                int count =  _root.getChildCount();
                for (int i = 0; i < count; i++)
                {
                
                   View view = _root.getChildAt(i);
                   
                   int[] location = {0,0}; //{(int) view.getX(), (int) view.getY()};  

                   view.getLocationOnScreen(location);
                   int right = location[0] + view.getWidth();
                   int bottom = location[1] + view.getHeight();  //this is weird !! i have to add height myself
                
                   //This shows the actual coordinates of the display on the screen.
                 //Toast.makeText(getApplicationContext(), count + " " + Integer.toString(location[0]) + " " + Integer.toString(location[1])
               		//                                         + " " + Integer.toString(right) + " " + Integer.toString(bottom), Toast.LENGTH_LONG).show();
                   
                   Rect boundz = new Rect(location[0], location[1], right, bottom);
                  
                   if(boundz.contains(X, Y)) 
                   { 
                	  
                	   // if image piece trying to swap with itself, then cancel event.
                	   if (view == v)
                		   break;
                   
                      RelativeLayout.LayoutParams layParams1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                      
                      //give coordinates of piece "picked up" to piece "replaced"
                      layParams1.leftMargin = (int) xInit;
                      layParams1.topMargin = (int) yInit;
                      view.setLayoutParams(layParams1);            
                      

                      // Maintain TrackArray
                      //starting array position  
                      int SAP = 0;      
 //           		  Toast.makeText(getApplicationContext(), "x " + Integer.toString((int) xInit)
 //           				  + " y " + Integer.toString((int) yInit ), Toast.LENGTH_SHORT).show();
                      for(int zsap = 0; zsap < NumOfPieces; zsap++)
                      {
                    	  if(bounds[zsap].contains((int)xInit + xOFFSET, (int)yInit + yOFFSET)) 
                    	  {
                    		  SAP = zsap;
                    		  break;
                    	  }
                      }
                     
                      // dc = display counter
                      // Maintain TrackArray
                      for (int dc = 0; dc < NumOfPieces; dc++)
                      {
                    	  if(v == display[dc])
                    	  {
//                    		  Toast.makeText(getApplicationContext(), "moving display " +Integer.toString(dc) + 
//                    				  " from position: " + Integer.toString(SAP) +
//                    				  " to position " + Integer.toString(AP), Toast.LENGTH_SHORT).show();
                    		  if(AP != SAP)
                    		  {
                                 int temp;
                                 temp = TrackArray[SAP];
                                 TrackArray[SAP] = TrackArray[AP];
                                 TrackArray[AP] = temp;
                    		  }
                    	  }                    	  
                      }  
                      break;
                   }                                                               
                }
                
                
                v.setLayoutParams(layParams);

                //release LOCK
                lock = 1;

                //CHECK FOR WIN!
                boolean win = true;
                for(int ac = 0; ac < NumOfPieces; ac++)
                {
//                	Toast.makeText(getApplicationContext(), Integer.toString(TrackArray[ac]), Toast.LENGTH_SHORT).show();
                	
                	// If Track array numbers are not in order
                	// {0, 1, 2, ...}, then pieces aren't in correct order.
              	  	if(TrackArray[ac] != ac)
              	  	{  
//              		  Toast.makeText(getApplicationContext(), Integer.toString(ac) + "  LOST", Toast.LENGTH_SHORT).show();
              	  		win = false;
              	  		break; //
              	  	}
                }
                
                
                // ******************************************************************************************
                // This code segment will execute once WIN has been confirmed.                
                if(win)
                {

                	cdTimer.cancel();
                    Toast.makeText(getApplicationContext(), "WIN!", Toast.LENGTH_SHORT).show();
                    	
                    
                    
                    if(GameType.equals("solo"))
                    {  	
                    	// Send Game Type to Dialog Fragment
                    	Bundle dialogBundle = new Bundle();	        			        			
            			dialogBundle.putString("GameType", GameType);
                    	soloD.setArguments(dialogBundle);
                    	soloD.setCancelable(false); // prevents back button from canceling dialog
                    	// Launch Dialog Fragment
                    	soloD.show(getFragmentManager(), "DialogDifficulty");
                    }
                    else //GameType == friendplay
                    {
                    	//03.19.2014
                    	//save temp Score on DB
                    	//change status on DB
                    	if(friendName != null)
                    	{
                    		System.out.println(friendName);
                    		Friend friend = db.getFriend(friendName);
                    		
                    		
                    		//-1 means that there was no previous score and this 
                    		// is the first score that will be recorded for the challenge.
                    		if(friend.getTScore() == -1)
                    		{
                    			friend.setTScore(DIFFICULTY-2);
                    			friend.setStatus("fight"); //So that user will be allowed to send back a challenge
                    		}
                    		else //This game contains the second score and a winner should be awarded points.
                    		{
                    			//THIS MUST BE CHANGED!!!!!
                    			UpdateChallenge.calculateWinner(friendName, DIFFICULTY-2);
                    			friend.setStatus("wait");
                    		}
                    		
                    		db.updateFriend(friend);
                    		
                    		//!!!
                    		// CODE NEEDED HERE TO DISPLAY WINNER AND TO END ACTIVITY.
                    	}
                    	
                    }
                }
 
                
                break;

            case MotionEvent.ACTION_MOVE:
 //           	Toast.makeText(getApplicationContext(), "MOVE", Toast.LENGTH_SHORT).show();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -300;
                layoutParams.bottomMargin = -300;
                v.setLayoutParams(layoutParams);
                
                break;
        }

        _root.invalidate();
                     
        return true;
	}
	
    public void onBackPressed() {
    	//Timer has to be stopped,
    	// if not it will still try to execute code once if finishes.
    	// that would result in error.
    	//if(GameType.equals("solo"))
    		cdTimer.cancel();
    	finish();
    }   
    
}

