package org.undp.bd.survey.application.data;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="response")
public class Response {
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField(foreign=true, canBeNull=false)
	public Survey survey;
	
	@ForeignCollectionField(eager=true)
	public ForeignCollection<Answer> answers;
	
	@DatabaseField(canBeNull=false)
	public boolean complete;
	
	private List<Answer> unsavedAnswers = new ArrayList<Answer>();
	
	public void addAnswer(Answer answer) {
		unsavedAnswers.add(answer);
	}
	
	public boolean isComplete() {
		if (answers != null)
			for (Answer answer : answers)
				if (!answer.isComplete())
					return false;
		for (Answer answer : unsavedAnswers)
			if (!answer.isComplete())
				return false;
		return true;
	}

	public void saveComplete(DatabaseHelper helper) {
		complete = true;
		save(helper);
	}
	public void save(DatabaseHelper helper) {
		if (id == 0) {
			helper.getResponses().create(this);
			helper.getResponses().refresh(this);
		} else {
			helper.getResponses().update(this);
		}
		if (answers != null)
			for (Answer answer : answers)
				helper.getAnswers().update(answer);
		for (Answer answer : unsavedAnswers) {
			answer.reponse = this;
			answers.add(answer);
		}
		unsavedAnswers.clear();
	}
	
	public void delete(DatabaseHelper helper) {
		for (Answer answer : answers)
			answer.delete(helper);
		helper.getResponses().delete(this);
	}

	@Override
	public String toString() {
		return "<Response: id=" + id + ", survey=" + survey + ">";
	}

	public Answer getOrCreateAnswer(Question question) {
		if (answers != null)
			for (Answer answer : answers)
				if (answer.question.id == question.id) {
					answer.question = question;
					return answer;
				}
		
		for (Answer answer : unsavedAnswers)
			if (answer.question.id == question.id)
				return answer;
		
		Answer answer = new Answer();
		answer.question = question;
		answer.reponse = this;
		unsavedAnswers.add(answer);
		return answer;
	}
}
