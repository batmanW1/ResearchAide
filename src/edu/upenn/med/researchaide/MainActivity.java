package edu.upenn.med.researchaide;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Handles user login functionality
 *
 */
public class MainActivity extends ActionBarActivity {

	public static final int IndexActivity_ID = 1;
	private EditText mUsernameEditText, mPasswordEditText;
	private String username, password;
	private boolean isUser;
	private RedCapRecord user;
	private SharedPreferences saveUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mUsernameEditText = (EditText) findViewById(R.id.userId);
		mPasswordEditText = (EditText) findViewById(R.id.password);

		Context ctx = MainActivity.this;
		saveUser = ctx.getSharedPreferences("userInfo", MODE_PRIVATE);

		mUsernameEditText.setText(saveUser.getString("user_name", ""));
		mPasswordEditText.setText(saveUser.getString("password", ""));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** Calls inner class to verify email and password credentials with RedCap database. */
	public void onLogInButtonClick(View view) {
		new VerifyLoginInfoTask().execute();
	}
	
	/** Called to display a temporary message about whether login was successful or unsuccessful. */
	private void makeToast(String message) {
		Toast toast = Toast.makeText(MainActivity.this, message,
				Toast.LENGTH_LONG);

		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * This class runs on a separate thread as needed in order to make requests
	 * to the RedCap database.
	 */
	private class VerifyLoginInfoTask extends
			AsyncTask<Void, Void, RedCapRecord> {
		
		String message;

		/** Makes RedCap database call to get information for email(username) and password credentials. */
		@Override
		protected RedCapRecord doInBackground(Void... params) {
			username = mUsernameEditText.getText().toString();
			password = mPasswordEditText.getText().toString();
			user = RedCap.exportUser(username);
			return user;
		}

		/** Verifies email(username) and password credentials entered by user with returned data about user from RedCap */
		protected void onPostExecute(RedCapRecord user) {
						
			if (user != null) {
				if (user.recordAttributes.get("email").equals(username)
						&& user.recordAttributes.get("password").equals(
								password)) {
					isUser = true;
				}
			}
			if (isUser == false || user == null) {
				
				// show message to user
				message = "Incorrect username or password. Please try again.";
				makeToast(message);
				
				// clear text field
				mUsernameEditText.setText("");
				mPasswordEditText.setText("");
				
			} else {
				
				// save verified login information for later use
				Editor editor = saveUser.edit();
				editor.putString("user_name", username).commit();
				editor.putString("password", password).commit();
				
				// show message to user
				message = "Login Successfully!";
				makeToast(message);
				
				// passing the verified information to new activity
				Intent i = new Intent(MainActivity.this, IndexActivity.class);
				i.putExtra("record_id", user.recordAttributes.get("record_id"));
				startActivityForResult(i, IndexActivity_ID);
			}
		}
	}

}