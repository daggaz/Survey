package org.undp.bd.survey.application.api;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.data.Answer;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.Response;

import android.content.Context;
import android.util.Log;

public class API {
	public static String API_BASE = "http://undp.grantanamo.com/api"; 
//	public static String API_BASE = "http://10.0.2.2:8000/api";
	
	@SuppressWarnings("unused")
	private static HttpGet get(String url) {
		HttpGet request = new HttpGet(API_BASE + url);
		addHeaders(request);
		return request;
	}
	
	private static HttpPost post(String url, Map<String, String> formdata) {
		HttpPost request = new HttpPost(API_BASE + url);
		addHeaders(request);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Entry<String, String> param : formdata.entrySet())
			params.add(new BasicNameValuePair(param.getKey(), param.getValue()));
		try {
			request.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			Log.e("API", "Error creating post request", e);
		}
		return request;
	}

	public static void addHeaders(HttpUriRequest request) {
		request.addHeader("Cookie", "sessionid=" + ApplicationData.instance().getUser().session_key);
	}
	
	public static void login(Context context, APIListener listener, String username, String password) {
		HttpGet request = new HttpGet(API_BASE + "/login/?username=" + username + "&password=" + password);
		new APIRequest(context, request, listener).execute();
	}

	public static void synchroniseSurveys(Context context, APIListener listener, List<Response> responses) {
		JSONArray jsonResponses = new JSONArray();
		for (Response response : responses) {
			JSONObject jsonResponse = new JSONObject();
			try {
				jsonResponse.put("id", response.id);
				jsonResponse.put("uuid", response.uuid.toString());
				jsonResponse.put("survey_id", response.survey.remote_id);
				Log.d("API", "");
				JSONArray jsonAnswers = new JSONArray();
				for (Answer answer : response.answers) {
					JSONObject jsonAnswer = new JSONObject();
					jsonAnswer.put("id", answer.id);
					jsonAnswer.put("question_id", answer.question.remote_id);
					jsonAnswer.put("value", answer.value);
					jsonAnswers.put(jsonAnswer);
				}
				jsonResponse.put("answers", jsonAnswers);
				jsonResponses.put(jsonResponse);
			} catch (JSONException e) {
				Log.e("API", "Error creating response JSON", e);
			}
		}
		
		Map<String, String> formdata = new HashMap<String, String>();
		formdata.put("responses", jsonResponses.toString());
		HttpPost request = post("/surveys/sync/", formdata);
		new APIRequest(context, request, listener).execute();
	}
}
