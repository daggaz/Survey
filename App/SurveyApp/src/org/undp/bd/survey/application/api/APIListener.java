package org.undp.bd.survey.application.api;

import org.json.JSONObject;

public interface APIListener {
	void failed(APIError error);
	void success(JSONObject data);
}
