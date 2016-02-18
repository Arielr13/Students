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
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SchoolView extends Activity {
	ParseUser school;
	ParseUser currentUser = ParseUser.getCurrentUser();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_view);
		String schoolId = getIntent().getExtras().getString("schoolid");
        Button donateButton = (Button)findViewById(R.id.schoolDonate);
        Button addNewButton = (Button)findViewById(R.id.schoolAddNew);
        final ImageButton settingsButton = (ImageButton)findViewById(R.id.schoolSettings);
        if(currentUser.getObjectId().equals(schoolId)){
        	donateButton.setVisibility(View.GONE);
        }
        else{
        	addNewButton.setVisibility(View.GONE);
        	settingsButton.setVisibility(View.INVISIBLE);
        }
		ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
	    query.whereEqualTo("objectId",schoolId);
	    query.findInBackground(new FindCallback<ParseUser>() {
	        public void done(List<ParseUser> objects, ParseException e) {
	            if (e == null) {
	            	//Get All Data
	                TextView name = (TextView)findViewById(R.id.schoolViewName);
	                TextView bio = (TextView)findViewById(R.id.schoolViewBio);
	                final ImageView image = (ImageView)findViewById(R.id.schoolViewImage);
	                school = objects.get(0);
	                //Display Data
	                name.setText(school.getString("name"));
	                bio.setText(school.getString("bio"));
	                //Display Image
	                ParseFile imageFile = (ParseFile)school.get("image");
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
	            }
	        }
	    });
	}
	
	public void addNew(View v){
		Intent moveToSchool = new Intent(SchoolView.this, AddNew.class);
		moveToSchool.putExtra("storyid", "");
		startActivity(moveToSchool);
	}
	public void donate(View v){
		Intent moveToSchool = new Intent(SchoolView.this, Donate.class);
		moveToSchool.putExtra("schoolid", school.getObjectId());
		startActivity(moveToSchool);
	}
	public void viewStories(View v){
		Intent moveToStories = new Intent(SchoolView.this, ViewList.class);
		moveToStories.putExtra("isSchool", true);
		moveToStories.putExtra("schoolid", school.getObjectId());
		startActivity(moveToStories);
	}
	public void settings(View v){
    	LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
    	View popupView = layoutInflater.inflate(R.layout.settings_popout, null);
    	float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    	
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0); 
       
        TextView popupusername=(TextView)popupView.findViewById(R.id.PopupUsername);
        popupusername.setText(ParseUser.getCurrentUser().getString("firstName")+"");
        
        TextView popupLogout=(TextView)popupView.findViewById(R.id.PopupLogout);
        popupLogout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	ParseUser.getCurrentUser().logOut();
            	popupWindow.dismiss();
            	startActivity(new Intent(SchoolView.this, Splash.class));
            }
        });

        TextView popupcancel=(TextView)popupView.findViewById(R.id.PopupCancel);
        popupcancel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	popupWindow.dismiss();
            }
        });
	}
}
