package org.undp.bd.survey.application.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.api.APITask;
import org.undp.bd.survey.application.api.DjangoObjectDecoder;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.DatabaseMixin;
import org.undp.bd.survey.application.data.Question;
import org.undp.bd.survey.application.data.Survey;
import org.undp.bd.survey.application.data.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

public class Home extends ActionBarActivity {
	
	private DatabaseMixin db = new DatabaseMixin(this);

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    db.destroy();
	}

	private DatabaseHelper getHelper() {
		return db.getHelper();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
	}
	
	public void surveys(View view) {
		Intent intent = new Intent(this, Surveys.class);	
		startActivity(intent);
	}
	
	public void synchronise(View view) {
		final User user = ApplicationData.instance().getUser();
		if (user.session_key != null) {
			APITask<Integer> getSurveys = new APITask<Integer>(Home.this) {
				
				@Override
				protected String getMessage() {
					return getResources().getString(R.string.synchronising_message);
				}
				
				@Override
				protected void failed(String reason) {
					Toast.makeText(getApplicationContext(), R.string.synchronising_error, Toast.LENGTH_LONG).show();
				}
				
				@Override
				protected void success(JSONObject data) {
					try {
						processSurveys(data);
						processQuestions(data);
						Toast.makeText(getApplicationContext(), R.string.synchronising_success, Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						Toast.makeText(getApplicationContext(), R.string.synchronising_error, Toast.LENGTH_LONG).show();
					}
				}

				private void processQuestions(JSONObject data) throws JSONException {
					RuntimeExceptionDao<Question, Integer> questions = getHelper().getQuestions();
					List<Question> allImportedQuestions = DjangoObjectDecoder.decode(Question.class, getHelper(), data.getJSONArray("questions"));
					
					for (Survey survey : user.surveys) {
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
								questions.update(importedQuestion);
								Log.d("Survey.Home", "Updated question " + importedQuestion);
							} else {
								questions.create(importedQuestion);
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
								existingQuestion.delete(getHelper());
								
							}
						}
					}
				}

				private void processSurveys(JSONObject data) throws JSONException {
					RuntimeExceptionDao<Survey, Integer> surveys = getHelper().getSurveys();
					List<Survey> importedSurveys = DjangoObjectDecoder.decode(Survey.class, getHelper(), data.getJSONArray("surveys"));
					List<Survey> existingSurveys = new ArrayList<Survey>(user.surveys);
					
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
							surveys.update(importedSurvey);
							Log.d("Survey.Home", "Updated survey " + importedSurvey);
						} else {
							importedSurvey.user = user;
							surveys.create(importedSurvey);
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
							existingSurvey.delete(getHelper());
							
						}
					}
				}
			};
			HttpGet request = new HttpGet("http://10.0.2.2:8000/api/surveys/sync/");
			request.addHeader("Cookie", "sessionid=" + user.session_key);
			getSurveys.execute(request);
		} else {
			Toast.makeText(getApplicationContext(), R.string.synchronising_login, Toast.LENGTH_LONG).show();
		}
	}
	
	public void logout(View view) {
		logout();
	}
	
	@Override
	public void onBackPressed() {
		logout();
	}
	
	private void logout() {
    	User user = ApplicationData.instance().getUser();
		user.session_key = null;
		getHelper().getUsers().update(user);
	    new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(getResources().getString(R.string.logout))
	        .setMessage(getResources().getString(R.string.confirm_logout))
	        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            finish();    
		        }
		})
	    .setNegativeButton(getResources().getString(R.string.no), null)
	    .show();
	}
}
