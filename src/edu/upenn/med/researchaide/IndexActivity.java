package edu.upenn.med.researchaide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import edu.upenn.med.researchaide.RedCapRecord;

public class IndexActivity extends Activity {

	private String verified_username;
	private String verified_password;
	private RedCapRecord userRecord;
	private String userInfo;
	private TextView userInfoTextView;
	private boolean gotInfo;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);

		// get the extra values passed from last activity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			verified_username = extras.getString("verified_username");
			verified_password = extras.getString("verified_password");
		}
	}

	public void onShowInfoButtonClick(View view) {
		Intent i = new Intent(this, UserInfoActivity.class);
		//i.putExtra("edu.upenn.med.reseachaide.username", verified_username);
		startActivity(i);
	}

	public void onChangePasswordButtonClick(View view) {
		Intent i = new Intent(this, ChangePasswordActivity.class);
		i.putExtra("old_password", verified_password);
		i.putExtra("username", verified_username);
		startActivity(i);
	}

	public void onDirectionButtonClick(View view) {
		Intent i = new Intent(this, DirectionActivity.class);
		startActivity(i);
	}
	
	public void onTellFriendButtonClick(View view){
		Intent i = new Intent(this, TellfriendActivity.class);
		startActivity(i);
	}
	
	public void onContactUsButtonClick(View view) {
		Intent i = new Intent(this, ContactActivity.class);
		startActivity(i);
	}

}
