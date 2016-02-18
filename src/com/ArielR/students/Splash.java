package com.ArielR.students;

import java.io.Console;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		//Parse Analytics
		ParseAnalytics.trackAppOpenedInBackground(getIntent());
		if(ParseUser.getCurrentUser()!=null){
			if(ParseUser.getCurrentUser().getBoolean("isSchool")){
				Intent viewSchool = new Intent(Splash.this, SchoolView.class);
				viewSchool.putExtra("schoolid", ParseUser.getCurrentUser().getObjectId());
				startActivity(viewSchool);
			}
			else{
				Intent viewList = new Intent(Splash.this, ViewList.class);
				viewList.putExtra("isSchool", false);
				startActivity(viewList);
			}
			finish();
		}
	}
	
	public void logIn (View v){
		startActivity(new Intent(Splash.this, LogIn.class));
		finish();
	}
	public void signUp (View v){
		startActivity(new Intent(Splash.this, SignUp.class));
		finish();
	}
	public void signUpAsSchool (View v){
		startActivity(new Intent(Splash.this, SignUpAsSchoolInfo.class));
		finish();
	}
}
