package org.undp.bd.survey.application.api;

import org.apache.http.client.methods.HttpGet;
import org.undp.bd.survey.application.data.ApplicationData;

import android.content.Context;

public class API {
	public static String API_BASE = "http://10.0.2.2:8000"; 

	private static HttpGet get(String url) {
		HttpGet request = new HttpGet(API_BASE + url);
		request.addHeader("Cookie", "sessionid=" + ApplicationData.instance().getUser().session_key);
		return request;
	}
	
	public static void login(Context context, String username, String password, APIListener listener) {
		HttpGet request = new HttpGet("/login/?username=" + username + "&password=" + password);
		new APITask(context, request, listener).execute();
	}

	public static void synchroniseSurveys(Context context, APIListener listener) {
		HttpGet request = get("/api/surveys/sync/");
		new APITask(context, request, listener).execute();
	}
}
