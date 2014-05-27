package org.undp.bd.survey.application.actions;

import org.json.JSONObject;
import org.undp.bd.survey.application.api.APIError;
import org.undp.bd.survey.application.api.APIListener;

class APIListenerProxy implements APIListener {
	private APIError error = null;
	private JSONObject data = null;
	
	public void onSuccess(JSONObject data) {
		this.data = data;
	}
	
	public void onError(APIError error) {
		this.error = error; 
	}
	
	public void dispatch(APIListener listener) {
		if (error != null)
			listener.onError(error);
		else if (data != null)
			listener.onSuccess(data);
		else
			throw new RuntimeException("failed or success was not called!");
	}
}