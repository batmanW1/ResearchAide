package edu.upenn.med.researchaide;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import edu.upenn.med.researchaide.UserInfoActivity.GetScheduleTask;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ScheduleActivity extends ActionBarActivity {

//	static AmazonDynamoDBClient db = new AmazonDynamoDBClient(
//			new BasicAWSCredentials("AKIAIZLADNMN2PLDBZNA",
//					"fCjuyDhYVK28gIKtoVSCcwRI5suNatC3oJgy4vi"));
	static AmazonDynamoDBClient db = new AmazonDynamoDBClient(
			new BasicAWSCredentials("AKIAI5XSEH47DJ63FG2Q",
					"ky7OiJmjNOMdnMaacXT3IFl8PdI4pl2duPdIQsIr"));
	private String compensationStatus;
	private TextView tv;
	private String record_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		
		// get the extra values passed from last activity (record_id)
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			record_id = extras.getString("record_id");
		}
		
		tv = (TextView) findViewById(R.id.userSchedule);
		new GetCompensationTask().execute();
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule, menu);
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
	 * This class runs on a separate thread as needed in order to make requests
	 * to the AWS DynamoDB database.
	 */
	public class GetCompensationTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Find all record_id in DynamoDB that match record_id. For now,
			// I've hardcoded 1111 as the record ID.
			// This will need to be updated once
			// we can pass the record_id from MainActivity to IndexActivity to
			// this activity.
			Condition hashKeyCondition = new Condition()
					.withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withS("1111"));

			// Since we cannot search DynamoDB using record_id alone, I've
			// created this condition which will get
			// all matches with "1111" as hash key and anything where visit_date
			// is greater than "111102014"
			Condition rangeKeyCondition = new Condition()
					.withComparisonOperator(ComparisonOperator.GT.toString())
					.withAttributeValueList(
							new AttributeValue().withS("11102014"));

			Map<String, Condition> keyConditions = new HashMap<String, Condition>();
			keyConditions.put("record_id", hashKeyCondition);
			keyConditions.put("visit_date", rangeKeyCondition);

			// Sets up the DynamoDB request to only retrieve visit_date and
			// compensation values.
			QueryRequest queryRequest = new QueryRequest()
					.withTableName("SCHEDULE").withKeyConditions(keyConditions)
					.withAttributesToGet("visit_date", "compensation");

			// Adds each result to the compensationStatus string to be put into
			// the TextView.
			QueryResult result = db.query(queryRequest);
			compensationStatus = "Visit Date \t Compensation Status \n"; 
			for (Map<String, AttributeValue> item : result.getItems()) {
				compensationStatus += item.get("visit_date").getS() + "\t"
						+ item.get("compensation").getS() + "\n";
			}
			return compensationStatus;
		}

		// Now we're back to the UI thread. compensationStatus is now added to
		// the textView.
		protected void onPostExecute(String userSchedule) {
			tv.setText(compensationStatus);
		}
	}

	public void onBackButtonClick(View view) {
		Intent i = new Intent(this, IndexActivity.class);
		startActivity(i);
	}
	
}
