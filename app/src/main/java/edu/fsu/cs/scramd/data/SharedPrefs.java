package edu.fsu.cs.scramd.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SharedPrefs extends Activity {

	public static final String PREFS_NAME = "Login";

	@Override protected void onCreate(Bundle state){
	super.onCreate(state);

	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	boolean silent = settings.getBoolean("silentMode", false);
	//setSilent(silent);
	}

	@Override protected void onStop(){
	super.onStop();

	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	SharedPreferences.Editor editor = settings.edit();
	//editor.putBoolean("silentMode", mSilentMode);
	editor.commit();
	}


}
