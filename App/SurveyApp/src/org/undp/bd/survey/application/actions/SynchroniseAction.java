package org.undp.bd.survey.application.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.api.API;
import org.undp.bd.survey.application.api.APIError;
import org.undp.bd.survey.application.api.APIListener;
import org.undp.bd.survey.application.data.Answer;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseContext;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.Question;
import org.undp.bd.survey.application.data.Response;
import org.undp.bd.survey.application.data.Survey;
import org.undp.bd.survey.application.data.User;

import android.content.Context;
import android.util.Log;

public abstract class SynchroniseAction {

	public enum FailureType {
		OFFLINE, API,
	}

	private Context context;
	private DatabaseHelper database;
	
	public SynchroniseAction(DatabaseContext db) {
		this.context = db.getContext();
		this.database = db.getHelper();
	}

	protected abstract void onSuccess();
	protected abstract void onFailure(FailureType offline, String reason);

	private class SynchroniseTask extends APITask {
		public SynchroniseTask() {
			super(context);
		}
		
		@Override
		protected void run(APIListener listener) {
			try {
				API.synchroniseSurveys(context, listener, database.getResponses().queryBuilder().where().eq("complete", true).query());
			} catch (SQLException e) {
				Log.d("SynchroniseTask", "Error getting complete responses", e);
			}
		}
		
		@Override
		public void onSuccess(JSONObject data) {
			try {
				processAcknowledgments(data);
				processSurveys(data);
				processQuestions(data);
				SynchroniseAction.this.onSuccess();
			} catch (JSONException e) {
				onFailure(FailureType.API, context.getResources().getString(R.string.synchronising_response_invalid));
			}
		}
		
		@Override
		public void onError(APIError error) {
			Log.d("SynchroniseTask", error.getDetail());
			switch (error.getReason()) {
			case PARSING_EXCEPTION:
				onFailure(FailureType.API, context.getResources().getString(R.string.error_parsing_response));
				break;
			default:
				onFailure(FailureType.API, context.getResources().getString(R.string.unknown_reason));
			}
		}
		
		@Override
		protected String getProgressMessage() {
			return context.getResources().getString(R.string.synchronising_message);
		}
	}
	
	public void execute() {
		if (getUser().session_key != null) {
			new SynchroniseTask().execute();
		} else {
			onFailure(FailureType.OFFLINE, null);
		}
	}
	
	private User getUser() {
		return ApplicationData.instance().getUser();
	}

	private void processAcknowledgments(JSONObject data) throws JSONException {
		JSONArray acknowlegments = data.getJSONArray("acknowledgments");
		List<Response> acknowledgedResponses = new ArrayList<Response>();
		for (int i = 0; i < acknowlegments.length(); i++) {
			try {
				acknowledgedResponses.addAll(
						database.getResponses().queryBuilder().where().eq("uuid", UUID.fromString(acknowlegments.getString(i))).query()
						);
			} catch (SQLException e) {
				Log.e("SynchroniseAction", "Error while deleting acknowledged responses", e);
			}
		}
		for (Response response : acknowledgedResponses) {
			Log.d("SynchroniseAction", "Deleting " + response);
			for (Answer answer : response.answers)
				database.getAnswers().delete(answer);
			database.getResponses().delete(response);
		}		
	}

	private void processQuestions(JSONObject data) throws JSONException {
		List<Question> allImportedQuestions = DjangoObjectDecoder.decode(Question.class, database, data.getJSONArray("questions"));
		
		for (Survey survey : getUser().surveys) {
			List<Question> existingQuestions = new ArrayList<Question>(survey.questions);
			List<Question> importedQuestions = new ArrayList<Question>();
			for (Question importedQuestion : allImportedQuestions)
				if (importedQuestion.survey.id == survey.id)
					importedQuestions.add(importedQuestion);

			// Create or update questions
			for (Question importedQuestion : importedQuestions) {
				Question existingQuestion = null;
				for (Question question : existingQuestions)
					if (question.remote_id == importedQuestion.remote_id) {
						existingQuestion = question;
						break;
					}
				if (existingQuestion != null) {
					importedQuestion.id = existingQuestion.id;
					database.getQuestions().update(importedQuestion);
					Log.d("Survey.Home", "Updated question " + importedQuestion);
				} else {
					database.getQuestions().create(importedQuestion);
					Log.d("Survey.Home", "Created question " + importedQuestion);
				}
			}
	
			// Delete missing surveys
			for (Question existingQuestion : existingQuestions) {
				boolean found = false;
				for (Question importedQuestion : importedQuestions)
					if (importedQuestion.remote_id == existingQuestion.remote_id) {
						found = true;
						break;
					}
				if (!found) {
					Log.d("Survey.Home", "Deleted question " + existingQuestion);
					existingQuestion.delete(database);
					
				}
			}
		}
	}

	private void processSurveys(JSONObject data) throws JSONException {
		List<Survey> importedSurveys = DjangoObjectDecoder.decode(Survey.class, database, data.getJSONArray("surveys"));
		List<Survey> existingSurveys = new ArrayList<Survey>(getUser().surveys);
		
		// Create or update surveys
		for (Survey importedSurvey : importedSurveys) {
			Survey existingSurvey = null;
			for (Survey survey : existingSurveys)
				if (survey.remote_id == importedSurvey.remote_id) {
					existingSurvey = survey;
					break;
				}
			if (existingSurvey != null) {
				importedSurvey.id = existingSurvey.id;
				database.getSurveys().update(importedSurvey);
				Log.d("Survey.Home", "Updated survey " + importedSurvey);
			} else {
				importedSurvey.user = getUser();
				database.getSurveys().create(importedSurvey);
				Log.d("Survey.Home", "Created survey " + importedSurvey);
			}
		}
		
		// Delete missing surveys
		for (Survey existingSurvey : existingSurveys) {
			boolean found = false;
			for (Survey importedSurvey : importedSurveys)
				if (importedSurvey.remote_id == existingSurvey.remote_id) {
					found = true;
					break;
				}
			if (!found) {
				Log.d("Survey.Home", "Deleted survey " + existingSurvey);
				existingSurvey.delete(database);
				
			}
		}
	}
}
