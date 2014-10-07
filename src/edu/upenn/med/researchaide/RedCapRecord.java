package edu.upenn.med.researchaide;
import java.util.HashMap;


/**
 * Object to hold user records.
 * 	Required Data Fields 
 *	Last name				lastname
 *	First name				firstname
 *	e-mail					emailid
 *	password				password
 *	age						age
 *	gender					gender
 *	race					race
 *	unique ID 				userName
 *	(that will allow linking to the AWS database that stores the majority of their de-identified/anonymous data)
 *	health care provider	healthCareProvider
 * 
 * @author Sunil
 */

public class RedCapRecord {
	public HashMap<String,String> recordAttributes;
	
	public RedCapRecord(String userName, String firstName, String lastName,
			String emailID, String password, int age, String gender,
			String race, String hcg) {
		recordAttributes = new HashMap<String, String>();
		recordAttributes.put("record_id", userName);
		recordAttributes.put("username", userName);
		recordAttributes.put("firstname", firstName);
		recordAttributes.put("lastname", lastName);
		recordAttributes.put("emailid", emailID);
		recordAttributes.put("password", password);
		recordAttributes.put("age", new Integer(age).toString());
		recordAttributes.put("gender", gender);
		recordAttributes.put("race", race);
		recordAttributes.put("hcg", hcg);

	}
	
	public RedCapRecord() {
		recordAttributes = new HashMap<String,String>();
	}
	
	/**
	 * Convert details to CSV format
	 *
	 * @param  null  
	 * @return String	 Contains user record and headers as a CSV formatted String
	 * 
	 */
	public String getRecords() {
		StringBuilder data = new StringBuilder();
		StringBuilder keySB = new StringBuilder();
		
		
		String[] records = {"record_id","username","firstname","lastname","emailid","password","age", "gender", "race", "hcg" };
		
		if(recordAttributes.containsKey("record_id")) {
			data.append(recordAttributes.get(records[0])  + "1,");
			keySB.append("record_id" + ",");
			
		}
		
		for(int i=1;i<records.length;i++) {
			data.append(recordAttributes.get(records[i]) + ",");
			keySB.append(records[i] + ",");
		}
	
		
		data.replace(data.length() - 1, data.length() , "\n");
		keySB.replace(keySB.length() - 1, keySB.length(), "\n");
		
		return keySB.toString() + data.toString();
		
	}
	
	public static void main(String[] args) {
			String s = "test1";
			RedCapRecord rcb = new RedCapRecord(s, s, s, s, s, 10, s, s, s);
			System.out.println(rcb.getRecords());
		
	}

}
