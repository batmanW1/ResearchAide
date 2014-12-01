package edu.upenn.med.researchaide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import edu.upenn.med.researchaide.RedCapRecord;

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
	
	public void onScheduleButtonClick(View view) {
		Intent i = new Intent(this, ScheduleActivity.class);
		startActivity(i);
	}

	public void onDirectionButtonClick(View view) {
		Intent i = new Intent(this, DirectionActivity.class);
		startActivity(i);
	}
	
	public void onCompensationButtonClick(View view) {
		Intent i = new Intent(this, CompensationActivity.class);
		startActivity(i);
	}

}
