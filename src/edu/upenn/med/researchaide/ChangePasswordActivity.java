package edu.upenn.med.researchaide;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends ActionBarActivity {
	
	private String userPassword;
	private EditText oldPassword;
	private EditText newPassword1;
	private EditText newPassword2;

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
		
		
		String oldPasswordText = oldPassword.getText().toString();
		String newPasswordText1 = newPassword1.getText().toString();
		String newPasswordText2 = newPassword2.getText().toString();
		
		if (oldPasswordText.equals(userPassword)) {
			Toast.makeText(this, 
					"New Password can not be same with the old one, Please try again!",
					Toast.LENGTH_LONG).show();
			oldPassword.setText("");
			newPassword1.setText("");
			newPassword2.setText("");
		} 
		
		if (!newPassword1.equals(newPassword2)) {
			Toast.makeText(this, 
					"Retype password not match, Please try again!",
					Toast.LENGTH_LONG).show();
			oldPassword.setText("");
			newPassword1.setText("");
			newPassword2.setText("");
		}
		
		new Thread(runnable).start();
	}
	
	Runnable runnable = new Runnable() {
		
		public void run() {
			RedCapRecord rc = new RedCapRecord("3", "juntao", "Juntao", "Wang", "juntao@seas.upenn.edu", 
					"1234", 24, "Male", "Asian", "142");
			boolean isSuccess = RedCap.commitToRedCap(rc);
			System.out.println(isSuccess);
		}
	};
	
}
