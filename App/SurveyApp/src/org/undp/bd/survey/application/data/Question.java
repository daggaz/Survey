package org.undp.bd.survey.application.data;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.api.DjangoObject;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="question")
public class Question extends DjangoObject {
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField(canBeNull=false)
	public int remote_id;

	@DatabaseField(canBeNull=false, foreign=true)
	public Survey survey;
	
	@DatabaseField(canBeNull=false)
	public boolean required;
	
	@DatabaseField(canBeNull=false)
	public int order;
	
	@DatabaseField(canBeNull=false)
	public String field_name;
	
	@DatabaseField(canBeNull=false)
	public String question;
	
	@DatabaseField(canBeNull=false)
	public String help_text;
	
	@DatabaseField(canBeNull=false)
	public String label;
	
	@DatabaseField(canBeNull=false)
	public String option_type;
	
	@ForeignCollectionField
	public ForeignCollection<Answer> answers;

	@Override
	public void initialise(String model, int pk, JSONObject data, DatabaseHelper helper) {
		try
		{
			super.initialise(model, pk, data, helper);
			remote_id = pk;
			required = data.getBoolean("required");
			order = data.getInt("order");
			field_name = data.getString("field_name");
			question = data.getString("question");
			help_text = data.getString("help_text");
			label = data.getString("label");
			option_type = data.getString("option_type");
			survey = helper.getSurveys().queryBuilder().where().eq("remote_id", data.getInt("survey")).queryForFirst();
		} catch (JSONException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(DatabaseHelper helper) {
		for (Answer answer : answers)
			answer.delete(helper);
		helper.getQuestions().delete(this);
	}
}
