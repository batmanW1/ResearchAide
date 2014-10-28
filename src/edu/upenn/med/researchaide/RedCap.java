package edu.upenn.med.researchaide;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
//import org.apache.log4j.Logger;

//Using XML
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import com.sleepDiary.backend.statusCodes.DBCodes;

/**
 * Import and Export records from RedCap
 * 
 * @author Sunil
 */

public class RedCap {

	// static Logger logger = Logger.getLogger("RedCap") ;
	static String tokenID = "BDB0B36E9A91F179FBA94EAB04D7275B";

	/**
	 * Write new user details into RedCap
	 * 
	 * @param userDetails
	 *            user information marshalled into RedCapRecord Object
	 * @return boolean true Addition Successful false If addition has failed
	 */

	public static boolean commitToRedCap(RedCapRecord userDetails) {

		if (userDetails == null || userDetails.recordAttributes == null
				|| userDetails.recordAttributes.get("record_id") == null) {
			return false;
		}

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://redcap.med.upenn.edu/api/");

		// Request parameters and other properties.
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(5);
		params.add(new BasicNameValuePair("token", tokenID));
		params.add(new BasicNameValuePair("content", "record"));
		params.add(new BasicNameValuePair("format", "csv"));
		params.add(new BasicNameValuePair("type", "flat"));

		String data = buildCSV(userDetails);
		// You can use XML format
		// (or)
		// String data = buildXMLString(userDetails);

		if (data == null) {
			return false;
		}

		//
		System.err.println(data);

		params.add(new BasicNameValuePair("data", data));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// logger.error(e1.getStackTrace());
			return false;
		}

		// Execute and get the response.
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			response = httpclient.execute(httppost);
			entity = response.getEntity();

			StatusLine responseStatus = response.getStatusLine();
			System.out.println("Response is " + responseStatus.getStatusCode());
			System.out.println(response.getStatusLine());
			if (!(responseStatus != null && responseStatus.getStatusCode() == 200)) {

				return false;
			}

			entity = response.getEntity();
			InputStream is = entity.getContent();
			byte[] dataByte = new byte[1024];
			is.read(dataByte);
			System.out.println(new String(dataByte));
			return true;
		} catch (IOException e1) {
			// logger.error(e1.getStackTrace());
			return false;
		}

	}

	/**
	 * Get list of all Users stored in RedCAp
	 * 
	 * @return HashMap<String,String> null - If there are no elements present
	 *         HashMap consists of Key,Value where Key = record_id Value =
	 *         userName
	 * 
	 * @see RedCapRecord
	 */
	public static HashMap<String, String> getUserNames() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://redcap.med.upenn.edu/api/");

		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(5);
		params.add(new BasicNameValuePair("token", tokenID));
		params.add(new BasicNameValuePair("content", "record"));
		params.add(new BasicNameValuePair("format", "csv"));
		params.add(new BasicNameValuePair("type", "flat"));
		// params.add(new BasicNameValuePair("fields", "userRecords"));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// logger.error(e1.getStackTrace());
			return null;
		}

		// Execute and get the response.
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			response = httpclient.execute(httppost);
			if (response == null)
				return null;
			entity = response.getEntity();

			StatusLine responseStatus = response.getStatusLine();

			if (responseStatus != null && responseStatus.getStatusCode() == 200) {
				entity = response.getEntity();
				InputStream is = entity.getContent();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br1 = new BufferedReader(isr);
				String data = br1.readLine();
				System.out.println("In RedCap getUserNames..." + data);
				HashMap<String, String> userNames = new HashMap<String, String>();
				while ((data = br1.readLine()) != null) {
					System.out.println("In RedCap getUserNames part 2..."
							+ data);

					String[] recordID_username = data.replaceAll("\"", "")
							.split(",");

					for (String info : recordID_username) {
						System.out.println("recordId_username contains: "
								+ info);
					}

					if (recordID_username.length == 2)
						userNames.put(recordID_username[0],
								recordID_username[1]);
				}
				return userNames;
			} else {
				// logger.error("GetUserNames: Recieved Status " +
				// response.getStatusLine());
				return null;

			}
		} catch (IOException e1) {
			// logger.error(e1.getStackTrace());
			return null;
		}

	}

	/**
	 * Verify if username and password are valid in RedCap.
	 */
	public static boolean verifyUser(String username, String password) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://redcap.med.upenn.edu/api/");

		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(5);
		params.add(new BasicNameValuePair("token", tokenID));
		params.add(new BasicNameValuePair("content", "record"));
		params.add(new BasicNameValuePair("format", "csv"));
		params.add(new BasicNameValuePair("type", "flat"));
		// params.add(new BasicNameValuePair("fields", "userRecords"));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			System.out.println("1");
			// logger.error(e1.getStackTrace());
			return false;
		}

		// Execute and get the response.
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			response = httpclient.execute(httppost);
			if (response == null) {
				System.out.println("2");
				return false;
			}
			entity = response.getEntity();

			StatusLine responseStatus = response.getStatusLine();

			if (responseStatus != null && responseStatus.getStatusCode() == 200) {
				entity = response.getEntity();
				InputStream is = entity.getContent();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br1 = new BufferedReader(isr);
				String data = br1.readLine();
				// System.out.println("In RedCap getUserNames..." + data);
				// HashMap<String,String> userNames = new
				// HashMap<String,String>();
				while ((data = br1.readLine()) != null) {
					// System.out.println("In RedCap getUserNames part 2..." +
					// data);

					String[] recordID_username = data.replaceAll("\"", "")
							.split(",");
					if (recordID_username[1].equals(username)) {
						if (recordID_username[5].equals(password)) {
							return true;
						}
						// for (String info : recordID_username) {
						// System.out.println("recordId_username contains: " +
						// info);
						// }

					}
				}
			}

			// logger.error("GetUserNames: Recieved Status " +
			// response.getStatusLine());

		} catch (IOException e1) {
			System.out.println("3");
			// logger.error(e1.getStackTrace());
			return false;
		}
		System.out.println("4");
		return false;
	}
	
	/**
	 * Retrieve user information from RedCap.
	 */
	
	public static RedCapRecord getUserInfo(String username) {
		RedCapRecord userInfo;
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://redcap.med.upenn.edu/api/");

		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(5);
		params.add(new BasicNameValuePair("token", tokenID));
		params.add(new BasicNameValuePair("content", "record"));
		params.add(new BasicNameValuePair("format", "csv"));
		params.add(new BasicNameValuePair("type", "flat"));
		// params.add(new BasicNameValuePair("fields", "userRecords"));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// logger.error(e1.getStackTrace());
			return null;
		}

		// Execute and get the response.
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			response = httpclient.execute(httppost);
			if (response == null)
				return null;
			entity = response.getEntity();

			StatusLine responseStatus = response.getStatusLine();

			if (responseStatus != null && responseStatus.getStatusCode() == 200) {
				entity = response.getEntity();
				InputStream is = entity.getContent();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br1 = new BufferedReader(isr);
				String data = br1.readLine();

				while ((data = br1.readLine()) != null) {
					String[] recordID_username = data.replaceAll("\"", "")
							.split(",");
					if (recordID_username[1].equals(username)) {
						String userName = recordID_username[1];
						String firstName = recordID_username[2];
						String lastName = recordID_username[3];
						String emailID = recordID_username[4];
						String password = recordID_username[5];
						int age = Integer.parseInt(recordID_username[6]);
						String gender = recordID_username[8];
						String race = recordID_username[7];
						String hcg = recordID_username[9];
						userInfo = new RedCapRecord(userName, firstName, lastName, emailID, password, age, gender, race, hcg);
						return userInfo;
						}

					}
				}

		} catch (IOException e1) {
			return null;
		}
		return null;
	}

	/**
	 * Get the details of a particular username
	 * 
	 * @param userName
	 *            unique id of the subject
	 * @return RedCapRecord Object populated with the details of the user
	 * @see RedCapRecord
	 */

	public static RedCapRecord exportUser(String userName) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://redcap.med.upenn.edu/api/");

		// HttpPost httppost = new
		// HttpPost("http://www.posttestserver.com/post.php");
		// Request parameters and other properties.
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(5);
		params.add(new BasicNameValuePair("token", tokenID));
		params.add(new BasicNameValuePair("content", "record"));
		params.add(new BasicNameValuePair("format", "csv"));
		params.add(new BasicNameValuePair("type", "flat"));
		params.add(new BasicNameValuePair("records", userName));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// logger.error(e1.getStackTrace());
			return null;
		}

		// Execute and get the response.
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			StatusLine responseStatus = response.getStatusLine();
			if (responseStatus != null && responseStatus.getStatusCode() == 200) {
				entity = response.getEntity();
				InputStream is = entity.getContent();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedInputStream br = new BufferedInputStream(is);
				BufferedReader br1 = new BufferedReader(isr);
				String headersString = br1.readLine();
				String userDetailsString = null;
				// TODO Need to handle EOF in this while loop.
				while (userDetailsString == null) {
					userDetailsString = br1.readLine();
				}

				// Headers is printing out: "record_id, recap_event_name, name,
				// email, password, my_first_instrument_complete, email_complete
				// UserDetailsString is always null.
				System.out.println("headersString: " + headersString);
				System.out.println("userDetailsString: " + userDetailsString);

				String[] headers = headersString.replaceAll("\"", "")
						.split(",");

				if (userDetailsString == null) {
					return null;
				}

				String[] usersDetail = userDetailsString.replaceAll("\"", "")
						.split(",");

				HashMap<String, String> userDetails = new HashMap<String, String>();

				for (int i = 0; i < headers.length; i++) {
					userDetails.put(headers[i], usersDetail[i]);

				}

				RedCapRecord userRecord = new RedCapRecord();
				userRecord.recordAttributes = userDetails;

				return userRecord;

			} else {
				// logger.error("ExportUsers: Recieved Status " +
				// response.getStatusLine());
				return null;
			}
		} catch (IOException e1) {
			// logger.error(e1.getStackTrace());
			return null;
		}
	}

	private static String buildCSV(RedCapRecord userDetails) {
		if (userDetails == null || userDetails.recordAttributes == null)
			return null;

		return userDetails.getRecords();

	}

	public static String buildXMLString(RedCapRecord userDetails) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("records");
			doc.appendChild(rootElement);

			// staff elements
			Element item = doc.createElement("item");
			rootElement.appendChild(item);

			if (userDetails.recordAttributes.keySet() == null)
				return null;

			Iterator<String> keys = userDetails.recordAttributes.keySet()
					.iterator();

			while (keys.hasNext()) {
				String keyName = keys.next();
				Element element = doc.createElement(keyName);
				element.appendChild(doc
						.createTextNode(userDetails.recordAttributes
								.get(keyName)));
				item.appendChild(element);
			}

			try {
				StringWriter sw = new StringWriter();
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
						"no");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

				transformer.transform(new DOMSource(doc), new StreamResult(sw));
				return sw.toString();
			} catch (Exception ex) {
				throw new RuntimeException("Error converting to String", ex);
			}
		} catch (ParserConfigurationException | RuntimeException ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public static void test() {
		int d = 1;
		RedCapRecord test = new RedCapRecord("userName" + d, "firstName" + d,
				"lastName" + d, "mail@mail.com" + d, "password" + d, 10, "M",
				"Asian", "Test");
		/*
		 * HashMap<String,String> users = getUserNames(); Iterator<String>
		 * itUsers = users.keySet().iterator(); while(itUsers.hasNext()) {
		 * String key = itUsers.next(); System.out.println("Looking for " +
		 * key); RedCapRecord userDetails = exportUser(key); if(userDetails !=
		 * null) { HashMap<String,String> userNames =
		 * userDetails.recordAttributes; Iterator<String> it =
		 * userNames.keySet().iterator();
		 * 
		 * while(it.hasNext()) { String keyName = it.next();
		 * System.out.println(keyName + " -> " + userNames.get(keyName)); }
		 * System.out.println("\n"); } }
		 */

		commitToRedCap(test);
	}

	// public static void main(String[] args) {
	// test();
	// }
	//
}

/*
 * Description
 * 
 * This function allows you to import a set of records for a project
 * 
 * NOTE: While this *does* work for Parent/Child projects, please note that it
 * will import the records only to the specific project you are accessing via
 * the API (i.e. the Parent or the Child project) and not to both. Additionally,
 * if importing new records into a Child project, those records must also
 * already exist in the Parent project, or else the API will return an error.
 * 
 * URL
 * 
 * https://redcap.med.upenn.edu/api/
 * 
 * Supported Request Methods
 * 
 * POST
 * 
 * Parameters (case sensitive)
 * 
 * Required
 * 
 * token the API token specific to your REDCap project and username (each token
 * is unique to each user for each project) - See the section above for
 * obtaining a token for a given project content record format csv, json, xml
 * [default] type flat - input as one record per row [default] eav - input as
 * one data point per row Non-longitudinal: Must have the fields - record*,
 * field_name, value Longitudinal: Must have the fields - record*, field_name,
 * value, redcap_event_name**
 * 
 * Record refers to the study id or whatever the primary key is for the project*
 * Event name is the unique name for an event, not the event label
 * overwriteBehavior normal - blank/empty values will be ignored [default]
 * overwrite - blank/empty values are valid and will overwrite data data the
 * formatted data to be imported NOTE: When importing data in EAV type format,
 * please be aware that checkbox fields must have their field_name listed as
 * variable+"___"+optionCode and its value as either "0" or "1" (unchecked or
 * checked, respectively). For example, for a checkbox field with variable name
 * "icecream", it would be imported as EAV with the field_name as "icecream___4"
 * having a value of "1" in order to set the option coded with "4" (which might
 * be "Chocolate") as "checked". EAV XML: <?xml version="1.0" encoding="UTF-8"
 * ?> <records> <item> <record></record> <field_name></field_name>
 * <value></value> <redcap_event_name></redcap_event_name> </item> </records>
 * 
 * Flat XML: <?xml version="1.0" encoding="UTF-8" ?> <records> <item> each data
 * point as an element ... </item> </records> Optional
 * 
 * returnContent ids - a list of all study IDs that were imported, count
 * [default] - the number of records imported, nothing - no text, just the HTTP
 * status code returnFormat csv, json, xml - specifies the format of returned
 * content or error messages. If you do not pass in this flag, it will select
 * the default format for you passed based on the "format" flag you passed in or
 * if no format flag was passed in, it will default to "xml". Returns:
 * 
 * the content specified by returnContent
 */