package org.undp.bd.survey.application.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="answer")
public class Answer {
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField(canBeNull=false)
	public String value;
	
	@DatabaseField(foreign=true, canBeNull=false)
	public Question question;
	
	@DatabaseField(foreign=true, canBeNull=false)
	public Response reponse;

	public void delete(DatabaseHelper helper) {
		helper.getAnswers().delete(this);
	}
	
	@Override
	public String toString() {
		return "<Answer: id=" + id + ", response=" + reponse.id + ", survey=" + question.survey + ", question=" + question.field_name + ", value=" + value + ">";  
	}
}
