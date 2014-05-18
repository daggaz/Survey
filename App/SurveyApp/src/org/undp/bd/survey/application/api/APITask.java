package org.undp.bd.survey.application.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.http.HTTPTask;

import android.content.Context;
import android.util.Log;

public abstract class APITask<Progress> extends HTTPTask<Progress> {

	public APITask(Context context) {
		super(context);
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.d("APITask", "result: " + result);
		if (result != null) {
			try {
				JSONObject data = new JSONObject(result);
    			String status = data.getString("status");
				if (status.equals("success")) {
					success(data);
				} else if (status.equals("failed")) {
					failed(data.getString("reason")); // TODO I18n response from server
				} else {
					failed(context.getResources().getString(R.string.unknown_reason));
				}
			} catch (JSONException e) {
				failed(context.getResources().getString(R.string.error_parsing_response));
			}
		} else {
			failed(context.getResources().getString(R.string.no_response));
		}
	}
	
	protected abstract void failed(String reason);
	protected abstract void success(JSONObject data);
}
