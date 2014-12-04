package edu.upenn.med.researchaide;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends ActionBarActivity {
	
	private String userName;
	private String userPassword;
	private EditText oldPassword;
	private EditText newPassword1;
	private EditText newPassword2;
	private String oldPasswordText;
	private String newPasswordText1;
	private String newPasswordText2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		oldPassword = (EditText) findViewById(R.id.oldPassword);
		newPassword1 = (EditText) findViewById(R.id.newPassword1);
		newPassword2 = (EditText) findViewById(R.id.newPassword2);
		
		// get the extra values passed from last activity
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	    	userName = extras.getString("username");
	    	
	    	userPassword = extras.getString("old_password");
	    }
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_password, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onConfirmChangeButtonClick(View view) {
		
		oldPasswordText = oldPassword.getText().toString();
		newPasswordText1 = newPassword1.getText().toString();
		newPasswordText2 = newPassword2.getText().toString();
		
		if (!oldPasswordText.equals(userPassword)) {
			Toast toast = Toast.makeText(this, 
					"Old password does not match! Please try again!",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			clearInfo();
		} else if (newPasswordText1.equals(oldPasswordText)) {
			Toast toast = Toast.makeText(this, 
					"New Password must be different from the old one. Please try again!",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			clearInfo();
		} else if (!newPasswordText1.equals(newPasswordText2)) {
			Toast toast = Toast.makeText(this, 
					"New passwords do not match. Please try again.",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			clearInfo();
		} else {
			new Thread(runnable).start();
			Toast toast = Toast.makeText(this, 
					"Password has changed successfully!",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			clearInfo();
			finish();
		}
	}
	
	/**
	 * Clear information in all text fields
	 */
	private void clearInfo() {
		oldPassword.setText("");
		newPassword1.setText("");
		newPassword2.setText("");
	}
	
	// put changing password into a new thread
	Runnable runnable = new Runnable() {
		
		public void run() {
			
			RedCapRecord old_rc = RedCap.exportUser(userName);
			RedCapRecord new_rc = new RedCapRecord();
			new_rc.recordAttributes = old_rc.recordAttributes;
			new_rc.recordAttributes.put("password", newPasswordText1);
			RedCap.commitToRedCap(new_rc);
		}
	};
	
}
