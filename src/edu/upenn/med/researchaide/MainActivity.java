package edu.upenn.med.researchaide;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	public static final int IndexActivity_ID = 1;
	EditText mUsernameEditText;
	String username;
	EditText mPasswordEditText;
	String password;
	boolean isUser;
	RedCapRecord user;
	boolean gotUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mUsernameEditText = (EditText) findViewById(R.id.userId);
		mPasswordEditText = (EditText) findViewById(R.id.password);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	public void onLogInButtonClick(View view) {
		gotUser = false;
		new Thread(runnable).start();
		// we don't need the while loop here
		while (true) {
			if (gotUser) {
//				if (isUser == false) {
				if (user == null) {
					Toast.makeText(
							MainActivity.this,
							"Incorrect username or password. Please try again.",
							Toast.LENGTH_LONG).show();
					break;
				} else {
					Toast.makeText(
							MainActivity.this,
							"Login Successful!",
							Toast.LENGTH_LONG).show();
					Intent i = new Intent(MainActivity.this, IndexActivity.class);
					// passing the verified information to new activity
					i.putExtra("verified_username", username);
					i.putExtra("verified_password", password);
					startActivityForResult(i, IndexActivity_ID);
					finish();
					break;
				}
			}
		}
	}

	Runnable runnable = new Runnable() {
		public void run() {
			username = mUsernameEditText.getText().toString();
			password = mPasswordEditText.getText().toString();
//			isUser = RedCap.verifyUser(username, password);
			user = RedCap.exportUser(username);
			gotUser = true;
		}
	};

}
