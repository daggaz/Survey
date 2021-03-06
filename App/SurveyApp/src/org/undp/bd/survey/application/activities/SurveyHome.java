package org.undp.bd.survey.application.activities;

import java.util.ArrayList;
import java.util.List;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.DatabaseContext;
import org.undp.bd.survey.application.data.Response;
import org.undp.bd.survey.application.data.Survey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SurveyHome extends ActionBarActivity {

	private DatabaseContext db = new DatabaseContext(this);
	private Survey survey;
	private MyAdapter<Response> partialResponseData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.survey_home);
		
		survey = getHelper().getSurveys().queryForId(getIntent().getIntExtra("survey_id", 0));
		
		setTitle(survey.title);
		
		((TextView)findViewById(R.id.title)).setText(survey.title);
		
		((TextView)findViewById(R.id.description)).setText(survey.description);
		
	    partialResponseData = new MyAdapter<Response>(this, getPartialResponses(), R.layout.partial_submission_list_item) {
			@Override
			public void initialiseView(Context context, View view, final Response response) {
				ImageButton deleteButton = (ImageButton)view.findViewById(R.id.button);
				deleteButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						response.delete(getHelper());
						partialResponseData.setObjects(getPartialResponses());
					}
				});
			}
		};
		ListView partialResponselistView = (ListView) findViewById(R.id.partial_submission_list);
		partialResponselistView.setAdapter(partialResponseData);
		partialResponselistView.setEmptyView(findViewById(R.id.empty));
		partialResponselistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				editResponse(SurveyHome.this.partialResponseData.getObjects().get(position).id);
			}
		});

		updateSubmittedResponses();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateSubmittedResponses();
		partialResponseData.setObjects(getPartialResponses());
	}

	public void updateSubmittedResponses() {
		List<Response> submittedResponses = getSubmittedResponses();
		TextView submittedResponseMessage = (TextView)findViewById(R.id.submitted_response_message);
		if (submittedResponses.size() == 0) {
			submittedResponseMessage.setText(getResources().getString(R.string.no_submitted_responses));
		} else if (submittedResponses.size()== 1) {
			submittedResponseMessage.setText(getResources().getString(R.string.submitted_response_message));
		} else {
			String message = getResources().getString(R.string.submitted_response_message_plural).replace("{0}", Integer.toString(submittedResponses.size()));
			submittedResponseMessage.setText(message);
		}
	}
	

	public List<Response> getPartialResponses() {
		final List<Response> partialResponses = new ArrayList<Response>();
	    for (Response response : survey.responses)
	    	if (!response.complete)
	    		partialResponses.add(response);
	    return partialResponses;
	}

	public List<Response> getSubmittedResponses() {
		final List<Response> submittedResponses = new ArrayList<Response>();
	    for (Response response : survey.responses)
	    	if (response.complete)
	    		submittedResponses.add(response);
	    return submittedResponses;
	}
	
	public void createSubmission(View view) {
		Log.d("SurveyHome", "create submission");
	  	editResponse(0);
	}

	public void editResponse(int id) {
		Intent intent = new Intent(this, EditResponse.class);
		intent.putExtra("response_id", id);
		intent.putExtra("survey_id", survey.id);
	  	startActivity(intent);
	}

	private DatabaseHelper getHelper() {
		return db.getHelper();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.destroy();
	}
}
