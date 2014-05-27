package org.undp.bd.survey.application.api;

import org.json.JSONObject;

public interface APIListener {
	void onSuccess(JSONObject data);
	void onError(APIError error);
}
