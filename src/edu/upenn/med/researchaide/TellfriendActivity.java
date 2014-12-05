package edu.upenn.med.researchaide;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

@SuppressWarnings("deprecation")

/**
 * Handles functionality for user to text message a friend about study participation.
 *
 */
public class TellfriendActivity extends ActionBarActivity {

	Button btnSendSMS;
	EditText txtPhoneNo;
	EditText txtMessage;

	private static final int CONTACT_PICKER_RESULT = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tellfriend);

		txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
		txtMessage = (EditText) findViewById(R.id.txtMessage);
		
		txtMessage.setText("ResearchAide is awesome!!!");

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

	/**
	 * Sends a text message to user's friend/contact.
	 * @param phoneNo The phone number to send the message to.
	 * @param mes The message to send.
	 */
	private void send(String phoneNo, String mes) {
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
				TellfriendActivity.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNo, null, mes, pi, null);

	}

	/**
	 * Retrieves phone number and message from user input and sends text message.
	 * @param view
	 */
	public void onTellfriendButtonClick(View view) {
		String phoneNo = txtPhoneNo.getText().toString();
		String message = txtMessage.getText().toString();
		if (phoneNo.length() > 0 && message.length() > 0)
			send(phoneNo, message);
		else
			Toast.makeText(getBaseContext(),
					"Please enter a number and message.", Toast.LENGTH_SHORT)
					.show();
	}

	/** Opens user's contacts so user can select a friend to send a message to. */
	public void onContactsButtonClick(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CONTACT_PICKER_RESULT:
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor cur = managedQuery(contactData, null, null, null, null);
				cur.moveToFirst();
				String num = getContactPhone(cur);
				txtPhoneNo.setText(num);
			}
		}
	}

	/** If user opens contacts, this method gets the phone number of the contact the user clicked on and passes it
	 * back to this activity.
	 * @return The contact's phone number.
	 */
	private String getContactPhone(Cursor cursor) {
		int phoneColumn = cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		int phoneNum = cursor.getInt(phoneColumn);
		String result = "";
		if (phoneNum > 0) {
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
			Cursor phone = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			if (phone.moveToFirst()) {
				for (; !phone.isAfterLast(); phone.moveToNext()) {
					int index = phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					//int typeindex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
					//int phone_type = phone.getInt(typeindex);
					String phoneNumber = phone.getString(index);
					result = phoneNumber;
				}
				if (!phone.isClosed()) {
					phone.close();
				}
			}
		}

		return result;
	}

}
