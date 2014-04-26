package org.undp.bd.survey.application.api;

import org.json.JSONObject;
import org.undp.bd.survey.application.data.DatabaseHelper;

public class DjangoObject {

	public String model;
	public int pk;
	public JSONObject data;

	public void initialise(String model, int pk, JSONObject data, DatabaseHelper helper) {
		this.model = model;
		this.pk = pk;
		this.data = data;
	}
}
