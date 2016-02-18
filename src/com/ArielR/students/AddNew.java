package com.ArielR.students;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddNew extends Activity {
	
	Bitmap selectedImage;
	ParseObject story;
	EditText titleEdit;
	EditText descriptionEdit;
	TextView error;
	Boolean isNew = true;
	ParseObject prevStory;
	ImageView preview;
	Button submitButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new);
		titleEdit = (EditText)findViewById(R.id.addNewTitle);
		descriptionEdit = (EditText)findViewById(R.id.addNewDescription);
		error = (TextView)findViewById(R.id.addNewError);
		preview = (ImageView)findViewById(R.id.addNewImage);
		submitButton = (Button)findViewById(R.id.addNewPostButton);
		
		if((getIntent().getExtras().getString("storyid")!=null)&&!(getIntent().getExtras().getString("storyid").equals(""))){
			populate();
			isNew = false;
			submitButton.setClickable(false);
			submitButton.setText("Please Wait...");
			TextView topText = (TextView)findViewById(R.id.addNewTop);
			topText.setText("Edit");
		}
	}
	
	public void populate(){
		ParseQuery<ParseUser> query = ParseQuery.getQuery("Story");
	    query.whereEqualTo("objectId",getIntent().getExtras().getString("storyid"));
	    query.findInBackground(new FindCallback<ParseUser>() {
	        public void done(List<ParseUser> objects, ParseException e) {
	            if (e == null) {
	            	//Get All Data
	            	prevStory = objects.get(0);
	                //Display Data
	            	titleEdit.setText(prevStory.getString("title"));
	            	descriptionEdit.setText(prevStory.getString("description"));
	                //Display Image
	                ParseFile imageFile = (ParseFile)prevStory.get("storyImage");
	                imageFile.getDataInBackground(new GetDataCallback() {
	                  public void done(byte[] data, ParseException e) {
	                    if (e == null) {
	                    	selectedImage = BitmapFactory.decodeByteArray(data, 0, data.length);
	    	        	    preview.setImageBitmap(selectedImage);
	    	        	    submitButton.setClickable(true);
	    	    			submitButton.setText("Edit");
	                    } else {
	                      //TODO handle errors
	                    }
	                  }
	                }); 
	            } else {
	                //TODO handle errors
	            }
	        }
	    });
	}
	
	//Image Preview and Pick
	public void pickImage (View v){
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, 1);
		Button pickButton = (Button)findViewById(R.id.addNewImageButton);
		pickButton.setText("Pick A Different Image");
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
					preview.setImageBitmap(selectedImage);
				} 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		
			}
	
		}	
	}
	
	public void postNew(View v){
		error.setText("");
		String title = titleEdit.getText().toString().trim();
		String description = descriptionEdit.getText().toString().trim();
		//Basic Parsing -- Parse Handles All Other Parsing
		if(title.equals("")){
			error.setText("Please Enter a Value for Story Title!");
		}
		else if(description.equals("")){
			error.setText("Please Enter a Value for Story Description!");
		}
		else if(selectedImage==null){
			error.setText("Please Select a Display Image");
		}
		else{
			if(isNew){
				story = new ParseObject("Story");
			}
			else{
				story = prevStory;
			}
			story.put("title", title);
			story.put("description", description);
			story.put("schoolId", ParseUser.getCurrentUser().getObjectId());
		    story.saveInBackground(new SaveCallback() {
		    	public void done(ParseException e) {
		    		if (e == null) {
		    			//Save Image
		    			ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    		    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		    		    byte[] data = stream.toByteArray();
		    		    final ParseFile imageFile = new ParseFile(story.getObjectId()+".jpg", data);
		    		    imageFile.saveInBackground(new SaveCallback(){
			    		    	public void done(ParseException e){
			    		    		story.put("storyImage",imageFile);
			    		    		story.saveInBackground();
			    		    		Intent moveToStory = new Intent(AddNew.this, ViewStory.class);
					    			moveToStory.putExtra("storySchoolId", ParseUser.getCurrentUser().getObjectId());
					    			moveToStory.putExtra("storyId", story.getObjectId());
					    			startActivity(moveToStory);
					    			finish();
			    		    	}
		    		    	}
		    		    );	
		    	    } 
		    		else {
		    			Toast.makeText(AddNew.this, ""+e, Toast.LENGTH_SHORT).show();
		    	    }
		    	}
		    });
		}
	}
}
