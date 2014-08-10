package org.undp.bd.survey.application.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

class APIRequest {

	private HttpUriRequest request;
	private APIListener listener;
	
	public APIRequest(Context context, HttpUriRequest request, APIListener listener) {
		this.request = request;
		this.listener = listener;
	}
	
	public void execute() {
    	HttpClient httpclient = new DefaultHttpClient();
        try {
        	Log.d("APIRequest", "loading: " + request.getURI().toString());
        	HttpResponse response = httpclient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                processResponse(out.toString());
            } else {
                response.getEntity().getContent().close();
                listener.onError(new APIError(APIError.Reason.HTTP_ERROR, statusLine.getReasonPhrase()));
            }
        } catch (ClientProtocolException e) {
            listener.onError(new APIError(APIError.Reason.EXCEPTION, e.getMessage()));
        } catch (IOException e) {
            listener.onError(new APIError(APIError.Reason.EXCEPTION, e.getMessage()));
        }
	}
	
	private void processResponse(String response) {
		Log.d("APIRequest", "response: " + response);
		try {
			JSONObject data = new JSONObject(response);
			String status = data.getString("status");
			if (status.equals("success")) {
				listener.onSuccess(data);
			} else if (status.equals("failed")) {
				listener.onError(new APIError(APIError.Reason.FAILED_RESPONSE, data.getString("reason"))); // TODO I18n response from server
			} else {
				listener.onError(new APIError(APIError.Reason.STATUS_ERROR, status));
			}
		} catch (JSONException e) {
			listener.onError(new APIError(APIError.Reason.PARSING_EXCEPTION, e.getMessage()));
		}
	}
}
