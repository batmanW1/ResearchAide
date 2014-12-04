package edu.upenn.med.researchaide;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ContactActivity extends ActionBarActivity {

	private EditText subject;
	private EditText message;
	private String subjectText;
	private String messageText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		subject = (EditText) findViewById(R.id.subject);
		message = (EditText) findViewById(R.id.message);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact, menu);
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

	public void onSendButtonClick(View view) {

		subjectText = subject.getText().toString();
		messageText = message.getText().toString();

		Intent intent = new Intent(Intent.ACTION_SENDTO,
				Uri.parse("mailto:zyhus1990@gmail.com?subject="
						+ Uri.encode(subjectText) + "&body="
						+ Uri.encode(messageText)));
		clearInfo();
		startActivity(intent);
	}

	/**
	 * Clear information in all text fields
	 */
	private void clearInfo() {
		subject.setText("");
		message.setText("");
	}

}
