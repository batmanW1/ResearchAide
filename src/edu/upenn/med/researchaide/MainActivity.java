package edu.upenn.med.researchaide;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
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
		while (true) {
			if (gotUser) {
				if (isUser == false) {
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
					startActivityForResult(i, IndexActivity_ID);
					// How do you pass variables from one activity to another?
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
			isUser = RedCap.verifyUser(username, password);
			gotUser = true;
		}
	};

}
