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
	    
	    userInfoTextView = (TextView) findViewById(R.id.userInformationTextView);
	    
	    // get the extra values passed from last activity
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	    	verified_username = extras.getString("verified_username");
	    	System.out.println(verified_username);
	    	verified_password = extras.getString("verified_password");
	    }
	}
	
	public void onShowInfoButtonClick(View view) {
		gotInfo = false;
		new Thread(runnable).start();
		while (true) {
			if (gotInfo) {
				userInfoTextView.setText(userInfo);
				break;
			}
		}
	}
	
	public void onChangePasswordButtonClick(View view) {
		Intent i = new Intent(this, ChangePasswordActivity.class);
		i.putExtra("old_password", verified_password);
		startActivity(i);
	}
	
	Runnable runnable = new Runnable() {
		
		public void run() {
			userRecord = RedCap.exportUser(verified_username); //This may no longer work. Please test and verify.
			userInfo = userRecord.toString();
			gotInfo = true;
		}
	};
}
