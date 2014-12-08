package edu.upenn.med.researchaide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class ContactActivity extends ActionBarActivity {

	private EditText subject;
	private EditText message;
	private String subjectText;
	private String messageText;
	private String record_id;
	private String coordEmail;
	
	static AmazonDynamoDBClient db = new AmazonDynamoDBClient(
			new BasicAWSCredentials("AKIAI5XSEH47DJ63FG2Q",
					"ky7OiJmjNOMdnMaacXT3IFl8PdI4pl2duPdIQsIr"));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		subject = (EditText) findViewById(R.id.subject);
		message = (EditText) findViewById(R.id.message);
		
		// get the extra values passed from last activity (record_id)
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			record_id = extras.getString("record_id");
		}
		
		new GetEmailTask().execute();

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
		String email = "mailto:" + coordEmail + "?subject=";

		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(email
				+ Uri.encode(subjectText) + "&body=" + Uri.encode(messageText)));
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
	
	/**
	 * This class runs on a separate thread as needed in order to make requests
	 * to the AWS DynamoDB database.
	 */
	public class GetEmailTask extends AsyncTask<Void, Void, String> {
	    
	    @Override
		protected String doInBackground(Void...params) {
		
		// Finds the record_id in DynamoDB that match this record_id. For now, I've hardcoded 1111 as the record_id.
		//This will need to be updated once we can pass the record_id from MainActivity to IndexActivity to this activity.
	    Condition hashKeyCondition = new Condition()
		.withComparisonOperator(ComparisonOperator.EQ.toString())
		.withAttributeValueList(new AttributeValue().withS(record_id));
        
        // Again, hardcoded study_id. 
		Condition rangeKeyCondition = new Condition()
		.withComparisonOperator(ComparisonOperator.EQ.toString())
		.withAttributeValueList(new AttributeValue().withS("555"));
 
		Map<String, Condition> keyConditions = new HashMap<String, Condition>();
		keyConditions.put("record_id", hashKeyCondition);
		keyConditions.put("study_id", rangeKeyCondition);
 
        // This requests that ONLY the coordinator e-mail is returned.
		QueryRequest queryRequest = new QueryRequest().withTableName("STUDY")
				.withKeyConditions(keyConditions)
				.withAttributesToGet("coord_email");
 
        // This makes the actual request to the database. It returns the coordinator e-mail in a clean format to use.
		QueryResult result = db.query(queryRequest);
		coordEmail = "";
		for (Map<String, AttributeValue> item : result.getItems()) {
			coordEmail = item.get("coord_email").getS();
		}
 
		return coordEmail;
	    }
	    
	    // Now we're back to the UI thread. This is where you can use the coordinator e-mail.
		protected void onPostExecute(String userSchedule) {
			// Put your code here to open an email client with the coordinator e-mail filled in.
		}
	    
	}
		
}