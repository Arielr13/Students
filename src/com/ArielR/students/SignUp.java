package com.ArielR.students;

import java.io.ByteArrayOutputStream;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SignUp extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
		//Make Sure Scrolling Works
		ScrollView scrollView = (ScrollView)findViewById(R.id.signupScrollView);
		RelativeLayout relativeScroll = (RelativeLayout)findViewById(R.id.signupRelativeScroll);
		LayoutParams params = relativeScroll.getLayoutParams();
		params.height = (scrollView.getChildAt(0).getHeight() + 1);
		relativeScroll.setLayoutParams(params);
	}
	
	//Open Info
	@Override
	public void onBackPressed() {
		startActivity(new Intent(SignUp.this, Splash.class));
		finish();
	}
	
	public void signUp(View v){
		//Get All Data
		final TextView error = (TextView)findViewById(R.id.signupError);
		final EditText firstNameEdit = (EditText)findViewById(R.id.signupFirstName);
		final EditText lastNameEdit = (EditText)findViewById(R.id.signupLastName);
		final EditText emailEdit = (EditText)findViewById(R.id.signupEmail);
		final EditText passwordEdit = (EditText)findViewById(R.id.signupPassword);
		final EditText confirmPasswordEdit = (EditText)findViewById(R.id.signupConfirmPassword);
		String firstName = firstNameEdit.getText().toString().trim();
		String lastName = lastNameEdit.getText().toString().trim();
		String email = emailEdit.getText().toString().trim();
		String password = passwordEdit.getText().toString().trim();
		String confirmPassword = confirmPasswordEdit.getText().toString().trim();
		
		error.setText("");
		//Basic Parsing -- Parse Handles All Other Parsing
		if(firstName.equals("")){
			error.setText("Please Enter a Value for First Name!");
		}
		else if(lastName.equals("")){
			error.setText("Please Enter a Value for Last Name!");
		}
		else if(email.equals("")){
			error.setText("Please Enter a Value for Email!");
		}
		else if(password.equals("")){
			error.setText("Please Enter a Value for Password!");
		}
		else if(!(password.equals(confirmPassword))){
			error.setText("Passwords Do Not Match!");
		}
		else{
			//Parse SignUp
			ParseUser user = new ParseUser();
			user.setUsername(email);
			user.setPassword(password);
			user.setEmail(email);
			user.put("firstName",firstName);
			user.put("lastName", lastName);
			user.put("isSchool", false);
			user.signUpInBackground(
				new SignUpCallback() {
				  public void done(ParseException e) {
				    if (e == null) {
				    	Intent moveToSchool = new Intent(SignUp.this, Splash.class);
						moveToSchool.putExtra("isSchool", false);
						startActivity(moveToSchool);
						finish();
				    } 
				    else {
				    	String error1 = e.toString().replace("com.parse.ParseRequest$ParseRequestException: ","");
				    	String error2 = Character.toUpperCase(error1.charAt(0)) + error1.substring(1);
				    	error.setText(error2);
				    }
				  }
			});
		}
	}
}
