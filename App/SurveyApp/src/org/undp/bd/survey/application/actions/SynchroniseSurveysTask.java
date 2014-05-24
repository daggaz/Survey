package org.undp.bd.survey.application.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.api.API;
import org.undp.bd.survey.application.api.APIError;
import org.undp.bd.survey.application.api.APIListener;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseContext;
import org.undp.bd.survey.application.data.Question;
import org.undp.bd.survey.application.data.Survey;
import org.undp.bd.survey.application.data.User;

import android.util.Log;

public abstract class SynchroniseSurveysTask extends AbstractTask {

	public enum FailureType {
		OFFLINE, API,
	}
	
	public SynchroniseSurveysTask(DatabaseContext db) {
		super(db);
	}

	protected abstract void onSuccess();
	protected abstract void onFailure(FailureType offline, String reason);

	@Override
	protected String getProgressMessage() {
		return getResources().getString(R.string.synchronising_message);
	}
	
	@Override
	protected void run() {
		if (getUser().session_key != null) {
			API.synchroniseSurveys(getContext(), new APIListener() {
				@Override
				public void success(JSONObject data) {
					try {
						processSurveys(data);
						processQuestions(data);
						onSuccess();
					} catch (JSONException e) {
						onFailure(FailureType.API, getResources().getString(R.string.synchronising_response_invalid));
					}
				}
				
				@Override
				public void failed(APIError error) {
					Log.d("SynchroniseSurveysTask", error.getDetail());
					switch (error.getReason())
					{
					case PARSING_EXCEPTION:
						onFailure(FailureType.API, getResources().getString(R.string.error_parsing_response));
						break;
					default:
						onFailure(FailureType.API, getResources().getString(R.string.unknown_reason));
					}
				}
			});
		} else {
			onFailure(FailureType.OFFLINE, null);
		}
	}
	
	private User getUser() {
		return ApplicationData.instance().getUser();
	}

	private void processQuestions(JSONObject data) throws JSONException {
		List<Question> allImportedQuestions = DjangoObjectDecoder.decode(Question.class, getDatabase(), data.getJSONArray("questions"));
		
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
					getDatabase().getQuestions().update(importedQuestion);
					Log.d("Survey.Home", "Updated question " + importedQuestion);
				} else {
					getDatabase().getQuestions().create(importedQuestion);
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
					existingQuestion.delete(getDatabase());
					
				}
			}
		}
	}

	private void processSurveys(JSONObject data) throws JSONException {
		List<Survey> importedSurveys = DjangoObjectDecoder.decode(Survey.class, getDatabase(), data.getJSONArray("surveys"));
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
				getDatabase().getSurveys().update(importedSurvey);
				Log.d("Survey.Home", "Updated survey " + importedSurvey);
			} else {
				importedSurvey.user = getUser();
				getDatabase().getSurveys().create(importedSurvey);
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
				existingSurvey.delete(getDatabase());
				
			}
		}
	}
}
