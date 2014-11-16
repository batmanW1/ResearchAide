package edu.upenn.med.researchaide;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class UserInfoActivity extends Activity {

	static AmazonDynamoDBClient db = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAI5XSEH47DJ63FG2Q","ky7OiJmjNOMdnMaacXT3IFl8PdI4pl2duPdIQsIr"));
	private String userSchedule;
	private TextView tv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_userinfo);
		tv = (TextView)findViewById(R.id.userSchedule);
		new GetScheduleTask().execute();
		
		
	}
	
	/**
	 * This class runs on a separate thread as needed in order to make requests
	 * to the AWS DynamoDB database.
	 */
	public class GetScheduleTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Find all record_id in DynamoDB that match "1111." This will need to be updated once
			// we can pass the record_id from MainActivity to IndexActivity to this activity.
			Condition hashKeyCondition = new Condition()
			.withComparisonOperator(ComparisonOperator.EQ.toString())
			.withAttributeValueList(new AttributeValue().withS("1111"));

			// Since we cannot search DynamoDB using record_id alone, I've created this condition which will get
			// all matches with "1111" as hash key and anything where visit_date is greater than "111102014"
			Condition rangeKeyCondition = new Condition()
			.withComparisonOperator(ComparisonOperator.GT.toString())
			.withAttributeValueList(new AttributeValue().withS("11102014"));

			Map<String, Condition> keyConditions = new HashMap<String, Condition>();
			keyConditions.put("record_id", hashKeyCondition);
			keyConditions.put("visit_date", rangeKeyCondition);

			// Sets up the DynamoDB request to only retrieve visit_date and location values.
			QueryRequest queryRequest = new QueryRequest().withTableName("SCHEDULE")
					.withKeyConditions(keyConditions)
					.withAttributesToGet("visit_date", "location");

			// Adds each result to the userSchedule string to be put into the TextView.
			QueryResult result = db.query(queryRequest);
			for (Map<String, AttributeValue> item : result.getItems()) {
				userSchedule += item + "\n";
			}
			return userSchedule;
		}
		
		// Now we're back to the UI thread. userSchedule is now added to the textView.
		protected void onPostExecute(String userSchedule) {
			tv.setText(userSchedule);
		}
		
	}

	public void onBackButtonClick(View view) {
		Intent i = new Intent(this, IndexActivity.class);
		startActivity(i);
	}
}


