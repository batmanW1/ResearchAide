package com.sleepDiary.backend.data;

import java.util.ArrayList;

public class UnitData {
	
	int version;
	public String routine;
	
	// Modify this as per app requirement. 
	// Marshal the data into 
	
	public ArrayList<String> answers ;
	public ArrayList<String> questions ;
	
	public UnitData(int version) {
		version = version;
		answers = new ArrayList<String>();
		questions  = new ArrayList<String>();
	}
	
	public void addAnswers(String answer) {
		answers.add(answer);
	}

	public String getRoutine() {
		return routine;
	}

	public ArrayList<String> getAnswers() {
		
		return answers;
	}

	public void addQuestions(String string) {
		questions.add(string);
		
	}
	
	public ArrayList<String> getQuestions() {
		
		return questions;
	}
	
	
}
