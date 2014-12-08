package edu.upenn.med.researchaide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;

/**
 * Serves as the menu for all actions/activities in ResearchAide application.
 *
 */
public class IndexActivity extends Activity {

	private String record_id;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);

		// get the extra values passed from last activity (record_id)
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			record_id = extras.getString("record_id");
		}
	}
	
	/** Opens the activity to display the visitation schedule. */
	public void onScheduleButtonClick(View view) {
		Intent i = new Intent(this, ScheduleActivity.class);
		i.putExtra("record_id", record_id);
		startActivity(i);
	}

	/** Opens the activity to display directions to visitation locations. */
	public void onDirectionButtonClick(View view) {
		Intent i = new Intent(this, DirectionActivity.class);
		startActivity(i);
	}
	
	/** Opens the activity to display the compensation status of user's visits. */
	public void onCompensationButtonClick(View view) {
		Intent i = new Intent(this, CompensationActivity.class);
		i.putExtra("record_id", record_id);
		startActivity(i);
	}
	
	/** Opens the activity for user to contact the study's director. */
	public void onContactUsButtonClick(View view) {
		Intent i = new Intent(this, ContactActivity.class);
		i.putExtra("record_id", record_id);
		startActivity(i);
	}
	
	/** Opens the activity for a user to e-mail a friend about the study. */
	public void onTellFriendButtonClick(View view){
		Intent i = new Intent(this, TellfriendActivity.class);
		startActivity(i);
	}
	
	/** Opens the activity for a user to view ITMAP's website. */
	public void onLearnMoreButtonClick(View view) {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.itmat.upenn.edu"));
		startActivity(i);
	}

}
