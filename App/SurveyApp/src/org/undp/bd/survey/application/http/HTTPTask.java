package org.undp.bd.survey.application.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

public abstract class HTTPTask<Progress> extends ProgressTask<HttpUriRequest, Progress, String> {
	
	public HTTPTask(Context context) {
		super(context);
	}
	
    @Override
    protected String doInBackground(HttpUriRequest... requests) {
    	HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        HttpUriRequest request = requests[0];
        try {
        	Log.d("HTTPTask", "loading: " + request.getURI().toString());
            response = httpclient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.d("HTTPTask", e.getMessage());
        } catch (IOException e) {
        	Log.d("HTTPTask", e.getMessage());
        }
        return responseString;
    }
}
