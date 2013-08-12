package com.example.favtwitter;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

public class FavTwitter extends Activity {
	private SharedPreferences savedSearches;
	private TableLayout queryTableLayout;
	private EditText queryEditText;
	private EditText tagEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		savedSearches = getSharedPreferences("searches", MODE_PRIVATE);
		
		queryTableLayout = (TableLayout) findViewById(R.id.queryTableLayout);
		queryEditText = (EditText) findViewById(R.id.queryEditText);
		tagEditText = (EditText) findViewById(R.id.tagEditText);
		
		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(saveButtonListener);
		
		Button clearTagsButton = (Button) findViewById(R.id.clearTagsButton);
		clearTagsButton.setOnClickListener(clearTagsButtonListener);
		
		refreshButtons(null);
	}
	
	public OnClickListener saveButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//create tag if both queryEditText and tagEditText are not Empty
			if(queryEditText.getText().length() > 0 && tagEditText.getText().length() > 0){
				//maketag(String query, String tag)
				makeTag(queryEditText.getText().toString(), tagEditText.getText().toString());
				//clear
				queryEditText.setText("");
				tagEditText.setText("");
				//hide soft keyboard
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tagEditText.getWindowToken(), 0);
			}else{
				// Create a new AlertDialog Builder
				// Smt like a String Builder
				AlertDialog.Builder builder = new AlertDialog.Builder(FavTwitter.this);
				
				builder.setTitle(R.string.missingTitle);
				
				builder.setPositiveButton("OK", null);
				
				builder.setMessage(R.string.missingMessage);
				
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
			}
		}
	};
	
	public OnClickListener clearTagsButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(FavTwitter.this);
			
			builder.setTitle(R.string.confirmTitle);
			builder.setPositiveButton(R.string.erase,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int button) {
							// TODO Auto-generated method stub
							clearButtons();
							
							//get a SharedPreferences.Editor to clear searches
							SharedPreferences.Editor preEditor = savedSearches.edit();
							preEditor.clear();
							preEditor.apply();
						}
					});
			builder.setCancelable(true);
			builder.setNegativeButton(R.string.cancel, null);
			
			builder.setMessage(R.string.confirmMessage);
			
			AlertDialog confirmDialog = builder.create();
			confirmDialog.show();
		}
		
		
	};
	
	private void refreshButtons(String newTag){
		//WTF??????
		//store saved tags in the tags array
		String[] tags=savedSearches.getAll().keySet().toArray(new String[0]);
		//sort by tag
		Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);
		
		if(newTag!=null){
			//add a new tag button and corresponding edit button to the GUI
			makeTagGUI(newTag, Arrays.binarySearch(tags, newTag));
		}else{
			for(int index =0; index<tags.length; index++){
				makeTagGUI(tags[index], index);
			}
		}
	}
	
	private void makeTagGUI(String tag, int index){
		//get a reference to the LayoutInflater service
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		//inflate new_tag_view.xml to create new tag and edit Buttons
		View newTagView = inflater.inflate(R.layout.new_tag_view, null);
		
		//get newTagButton, set its text and register its listener
		Button newTagButton = (Button) newTagView.findViewById(R.id.newTagButton);
		newTagButton.setText(tag);
		newTagButton.setOnClickListener(queryButtonListener);
		
		Button newEditButton = (Button) newTagView.findViewById(R.id.newEditButton);
		newEditButton.setOnClickListener(editButtonListener);
		
		//add new tag and edit button to queryTableLayout
		queryTableLayout.addView(newTagView, index);
	}
	
	private void makeTag(String query, String tag){
		String originalQuery = savedSearches.getString(tag,null);
		//WTF2
		//get a SharedPreferences.Editor to store new tag/query pair
		SharedPreferences.Editor prefEditor = savedSearches.edit();
		prefEditor.putString(tag, query);
		prefEditor.apply();
		if(originalQuery == null)
			refreshButtons(tag);
	}
	
	private void clearButtons(){
		//remove all saved search buttons
		queryTableLayout.removeAllViews();
	}
	
	public OnClickListener queryButtonListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//get the query
			String buttonText=((Button)v).getText().toString();
			String query = savedSearches.getString(buttonText, null);
			
			//create the URL
			String urlString = getString(R.string.searchURL)+query;
			//INTENT!!!!!!!!
			Intent getURL = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
			
			startActivity(getURL);
		}
	};
	//edit selected search
	public OnClickListener editButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TableRow buttonTableRow = (TableRow) v.getParent();
			Button searchButton = (Button) buttonTableRow.findViewById(R.id.newTagButton);
			
			String tag = searchButton.getText().toString();
			
			tagEditText.setText(tag);
			queryEditText.setText(savedSearches.getString(tag,null));
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fav_twitter, menu);
		return true;
	}

}
