package com.ArielR.students;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUpAsSchool extends Activity {
	
	Bitmap selectedImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_school);
	}
	
	//Pick Photo and Display Preview
	public void pickImage (View v){
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, 1);
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(SignUpAsSchool.this, SignUpAsSchoolInfo.class));
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
	
		switch(requestCode) { 
		case 1:
			if(resultCode == RESULT_OK){
				try {
					final Uri imageUri = imageReturnedIntent.getData();
					final InputStream imageStream = getContentResolver().openInputStream(imageUri);
					selectedImage = BitmapFactory.decodeStream(imageStream);
					ImageView preview = (ImageView)findViewById(R.id.signupSchoolImage);
					preview.setImageBitmap(selectedImage);
				} 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		
			}
	
		}	
	}
	
	//Regular Signup Flow
	public void signupSchool (View v){
		//Get All Data
		final TextView error = (TextView)findViewById(R.id.signupSchoolError);
		final EditText nameEdit = (EditText)findViewById(R.id.signupSchoolName);
		final EditText emailEdit = (EditText)findViewById(R.id.signupSchoolEmail);
		final EditText passwordEdit = (EditText)findViewById(R.id.signupSchoolPassword);
		final EditText confirmPasswordEdit = (EditText)findViewById(R.id.signupSchoolConfirmPassword);
		final EditText bioEdit = (EditText)findViewById(R.id.signupSchoolBio);
		String name = nameEdit.getText().toString().trim();
		String email = emailEdit.getText().toString().trim();
		String password = passwordEdit.getText().toString().trim();
		String confirmPassword = confirmPasswordEdit.getText().toString().trim();
		String bio = bioEdit.getText().toString().trim();
		
		error.setText("");
		//Basic Parsing -- Parse Handles All Other Parsing
		if(name.equals("")){
			error.setText("Please Enter a Value for School Name!");
		}
		else if(bio.equals("")){
			error.setText("Please Enter a Value for School Info!");
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
		else if(selectedImage==null){
			error.setText("Please Select a Display Image");
		}
		else{
			//Parse SignUp
			final ParseUser user = new ParseUser();
			user.setUsername(email);
			user.setPassword(password);
			user.setEmail(email);
			user.put("name",name);
			user.put("isSchool", true);
			user.put("isVerified", false);
			user.put("bio", bio);
			//Image Compression
			user.signUpInBackground(
				new SignUpCallback() {
				  public void done(ParseException e) {
				    if (e == null) {
				    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    		    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		    		    byte[] data = stream.toByteArray();
		    		    final ParseFile imageFile = new ParseFile(user.getObjectId()+".jpg", data);
		    		    imageFile.saveInBackground(new SaveCallback(){
			    		    	public void done(ParseException e){
			    		    		user.put("image",imageFile);
			    		    		user.saveInBackground();
							    	new AlertDialog.Builder(SignUpAsSchool.this)
								    .setTitle("Thank You For Signing Up!")
								    .setMessage("Your account will be verified in less than 48 hours. As soon as its verified, you'll be able to log in, start posting updates, and get fundraising!")
								    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
								        public void onClick(DialogInterface dialog, int which) { 
								          ParseUser.getCurrentUser().logOut();	 
								          startActivity(new Intent(SignUpAsSchool.this, Splash.class));
								          finish();
								        }
								     })
								    .show();
			    		    	}
		    		    	}
		    		    );	
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
