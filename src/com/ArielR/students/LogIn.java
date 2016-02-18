package com.ArielR.students;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class LogIn extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		//Make Sure Scrolling Works
		ScrollView scrollView = (ScrollView)findViewById(R.id.loginScrollView);
		RelativeLayout relativeScroll = (RelativeLayout)findViewById(R.id.loginRelativeScroll);
		LayoutParams params = relativeScroll.getLayoutParams();
		params.height = (scrollView.getChildAt(0).getHeight() + 1);
		relativeScroll.setLayoutParams(params);
	}
	
	//Open Splash
	@Override
	public void onBackPressed() {
		startActivity(new Intent(LogIn.this, Splash.class));
		finish();
	}
	
	public void logIn(View v){
		//Get All Data
		final TextView error = (TextView)findViewById(R.id.loginError);
		final EditText emailEdit = (EditText)findViewById(R.id.loginEmail);
		final EditText passwordEdit = (EditText)findViewById(R.id.loginPassword);
		String email = emailEdit.getText().toString().trim();
		String password = passwordEdit.getText().toString().trim();
		
		error.setText("");
		//Basic Parsing -- Parse Handles All Other Parsing
		if(email.equals("")){
			error.setText("Please Enter a Value for Email!");
		}
		else if(password.equals("")){
			error.setText("Please Enter a Value for Password!");
		}
		else{
			//Parse Login
			ParseUser.logInInBackground(email, password, new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					if (user != null) {
						if(user.getBoolean("isSchool")){
							if(user.getBoolean("isVerified")){
								Intent moveToSchool = new Intent(LogIn.this, SchoolView.class);
								moveToSchool.putExtra("schoolid", user.getObjectId());
								startActivity(moveToSchool);
								finish();
							}
							else{
								//School But Not Verified
								new AlertDialog.Builder(LogIn.this)
							    .setTitle("Sorry, You Haven't Been Verified Yet")
							    .setMessage("Your account will be verified in less than 48 hours. As soon as its verified, you'll be able to log in, start posting updates, and get fundraising!")
							    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
							        public void onClick(DialogInterface dialog, int which) { 
							          startActivity(new Intent(LogIn.this, Splash.class));
							          finish();
							        }
							     })
							    .show();
							}
						}
						else{
							//Regular User
							Intent moveToList = new Intent(LogIn.this, ViewList.class);
							moveToList.putExtra("isSchool", false);
							startActivity(moveToList);
							finish();
						}
					} 
					else {
						error.setText("Incorrect Username/Password");
					}
				}
			});
		}
	}
}
