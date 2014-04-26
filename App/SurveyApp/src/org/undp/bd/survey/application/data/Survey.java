package org.undp.bd.survey.application.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.api.DjangoObject;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="survey")
public class Survey extends DjangoObject {
	public Survey() {}
	
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField(canBeNull=false)
	public int remote_id;
	
	@DatabaseField(canBeNull=false, foreign=true)
	public User user;
	
	@DatabaseField(canBeNull=false)
	public String title;
	
	@DatabaseField(canBeNull=false)
	public String description;
	
	@DatabaseField(canBeNull=false)
	public String slug;
	
	@DatabaseField(canBeNull=false)
	public boolean is_live;
	
	@DatabaseField(canBeNull=false)
	public boolean is_open;

	@ForeignCollectionField(eager=true)
	public ForeignCollection<Question> questions;

	@ForeignCollectionField(eager=false)
	public ForeignCollection<Response> responses;
	
	public void delete(DatabaseHelper helper) {
		for (Response response : responses)
			response.delete(helper);
		for (Question question : questions)
			question.delete(helper);
		helper.getSurveys().delete(this);
	}
	
	@Override
	public void initialise(String model, int pk, JSONObject data, DatabaseHelper helper) {
		try
		{
			super.initialise(model, pk, data, helper);
			remote_id = pk;
			user = ApplicationData.instance().getUser();
			title = data.getString("title");
			description = data.getString("description");
			slug = data.getString("slug");
			is_live = data.getBoolean("is_live");
			is_open = data.getBoolean("is_open");
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return "<Survey: remote_id=" + remote_id + ", name=" + title + ">";
	}
}
