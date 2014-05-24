package org.undp.bd.survey.application.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.api.DjangoObject;
import org.undp.bd.survey.application.data.DatabaseHelper;

class DjangoObjectDecoder {

	public static <T extends DjangoObject> List<T> decode(Class<T> cls, DatabaseHelper helper, JSONArray jsonArray) throws JSONException {
		List<T> objects = new ArrayList<T>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			T object = decode(cls, helper, jsonObject);
			objects.add(object);
		}
		return objects;
	}
	
	public static <T extends DjangoObject> T decode(Class<T> cls, DatabaseHelper helper, JSONObject jsonObject) throws JSONException {
		String model = jsonObject.getString("model");
		T object;
		try {
			object = cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException();
		}
		object.initialise(
				model,
				jsonObject.getInt("pk"),
				jsonObject.getJSONObject("fields"),
				helper
				);
		return object;
	}
}
