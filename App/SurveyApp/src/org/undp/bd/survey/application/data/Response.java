package org.undp.bd.survey.application.data;

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
	
	public void delete(DatabaseHelper helper) {
		for (Answer answer : answers)
			answer.delete(helper);
		helper.getResponses().delete(this);
	}
}
