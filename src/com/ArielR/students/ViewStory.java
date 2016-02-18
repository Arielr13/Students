package com.ArielR.students;

import java.util.List;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewStory extends Activity {
	ParseObject story;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_story);
		final String storyId = getIntent().getExtras().getString("storyId");
		String storySchoolId = getIntent().getExtras().getString("storySchoolId");
	    Button donateButton = (Button)findViewById(R.id.viewStoryDonate);
	    Button editButton = (Button)findViewById(R.id.viewStoryEdit);
	    if(ParseUser.getCurrentUser().getObjectId().equals(storySchoolId)){
	    	donateButton.setVisibility(View.GONE);
	    }
	    else{
	    	editButton.setVisibility(View.GONE);
	    }
	    
	    ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
	    query.whereEqualTo("objectId",storyId);
	    query.findInBackground(new FindCallback<ParseObject>() {
	        public void done(List<ParseObject> objects, ParseException e) {
	            if (e == null&&objects.size()>0) {
	            	//Get All Data
	                TextView title = (TextView)findViewById(R.id.viewStoryTitle);
	                TextView description = (TextView)findViewById(R.id.viewStoryBio);
	                final ImageView image = (ImageView)findViewById(R.id.viewStoryImage);
	                story = objects.get(0);
	                //Display Data
	                title.setText(story.getString("title"));
	                description.setText(story.getString("description"));
	                //Display Image
	                ParseFile imageFile = (ParseFile)story.get("storyImage");
	                imageFile.getDataInBackground(new GetDataCallback() {
	                  public void done(byte[] data, ParseException e) {
	                    if (e == null) {
	                    	Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
	    	        	    image.setImageBitmap(bmp);
	                    } else {
	                      //TODO handle errors
	                    }
	                  }
	                });
	                
	            } else {
	                //TODO handle errors
	            	Toast.makeText(ViewStory.this, ""+storyId, Toast.LENGTH_SHORT).show();
	            }
	        }
	    });
	}
	
	public void viewSchool(View v){
		Intent moveToSchool = new Intent(ViewStory.this, SchoolView.class);
		moveToSchool.putExtra("schoolid", story.getString("schoolId"));
		startActivity(moveToSchool);
	}
	public void donate(View v){
		Intent moveToSchool = new Intent(ViewStory.this, Donate.class);
		moveToSchool.putExtra("schoolid", story.getString("schoolId"));
		startActivity(moveToSchool);
	}
	public void edit(View v){
		Intent moveToEdit = new Intent(ViewStory.this, AddNew.class);
		moveToEdit.putExtra("storyid", story.getObjectId());
		startActivity(moveToEdit);
	}
}


