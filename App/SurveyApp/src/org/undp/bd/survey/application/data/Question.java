package org.undp.bd.survey.application.data;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.api.DjangoObject;

import android.util.Log;

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
	public String question;
	
	@DatabaseField(canBeNull=false)
	public String help_text;
	
	@DatabaseField(canBeNull=false)
	public String label;
	
	@DatabaseField(canBeNull=false)
	public String option_type;
	
	@DatabaseField(canBeNull=false)
	public String options;
	
	@ForeignCollectionField
	public ForeignCollection<Answer> answers;

	public boolean hasPrevious() {
		if (survey != null)
			for (Question q : this.survey.questions)
				return q.id != this.id;
		return false;
	}
	
	public boolean hasNext() {
		if (survey != null) {
			Question last = null;
			for (Question q : this.survey.questions)
				last = q;
			if (last != null)
				return this.id != last.id;
		}
		return false;
		
	}
	
	@Override
	public void initialise(String model, int pk, JSONObject data, DatabaseHelper helper) {
		try
		{
			super.initialise(model, pk, data, helper);
			remote_id = pk;
			required = data.getBoolean("required");
			order = data.getInt("order");
			question = data.getString("question");
			help_text = data.getString("help_text");
			label = data.getString("label");
			option_type = data.getString("option_type");
			options = data.getString("options");
			Log.d("Question.initialise", "options: " + options);
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
	
	@Override
	public String toString() {
		return "<Question: id=" + id + ", remote_id=" + remote_id + ", field=" + label + ", option_type=" + option_type + ">";  
	}
}
