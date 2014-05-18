package org.undp.bd.survey.application.activities;

import java.util.ArrayList;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.Answer;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.DatabaseMixin;
import org.undp.bd.survey.application.data.Question;
import org.undp.bd.survey.application.data.Response;
import org.undp.bd.survey.application.fragements.NavigationDrawerFragment;
import org.undp.bd.survey.application.fragements.QuestionFragment;
import org.undp.bd.survey.application.fragements.QuestionFragment.QuestionFragmentCallBacks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EditResponse extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, QuestionFragmentCallBacks {

	private CharSequence mTitle;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private Response response;
	private Question currentQuestion;
	private Answer currentAnswer;
	private int currentIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int responseId = getIntent().getIntExtra("response_id", 0);
		if (responseId > 0) {
			response = getHelper().getResponses().queryForId(responseId);
			getHelper().getSurveys().refresh(response.survey);
		} else {
			response = new Response();
			response.survey = getHelper().getSurveys().queryForId(getIntent().getIntExtra("survey_id", 0));
		}
		Log.d("EditResponse", "Editing response " + response);

		setContentView(R.layout.edit_response);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("EditResponse", "onCreateOptionsMenu");
		ActionBar actionBar = getSupportActionBar();
		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mTitle = actionBar.getTitle();
			actionBar.setTitle(R.string.questions);
		} else {
			actionBar.setTitle(mTitle);
		}

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_response, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.question_list:
			mNavigationDrawerFragment.openDrawer();
			return true;
		case R.id.save_draft:
			saveDraft();
			return true;
		case R.id.submit:
			submit();
			return true;
		default:
            return super.onOptionsItemSelected(item);
		}
	}
	
	private void submit() {
		if (response.isComplete()) {
			new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_info)
	        .setTitle(getResources().getString(R.string.confirm_submission))
	        .setMessage(getResources().getString(R.string.confirm_submission_message))
	        .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	response.saveComplete(getHelper());
					finish();
					Toast.makeText(EditResponse.this, R.string.response_submitted, Toast.LENGTH_LONG).show();
		        }
			})
			.setCancelable(true)
		    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			})
		    .show();
		} else {
			Toast.makeText(this, R.string.not_complete, Toast.LENGTH_LONG).show();
		}
	}

	public void saveDraft() {
		response.save(getHelper());
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("EditResponse", "Resume");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("EditResponse", "Stopped");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("EditResponse", "Destroyed");
		db.destroy();
	}
	
	@Override
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.closeDrawer();
		} else {
			new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(getResources().getString(R.string.discard_response))
		        .setMessage(getResources().getString(R.string.save_draft_query))
		        .setPositiveButton(getResources().getString(R.string.save_draft), new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			            saveDraft();
			            finish();
			        }
	
				})
				.setCancelable(true)
			    .setNegativeButton(getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
			    .show();
		}
	}

	public void previousQuestion(View view) {
		if (currentQuestion.hasPrevious())
			showQuestion(currentIndex-1);
	}
	
	public void nextQuestion(View view) {
		if (currentQuestion.hasNext())
			showQuestion(currentIndex+1);
	}

	@Override
	public void onNavigationDrawerItemSelected(int index) {
		showQuestion(index);
	}

	public void showQuestion(final int index) {
		if (index != currentIndex) {
			if (currentQuestion != null && currentQuestion.required && currentAnswer != null && (currentAnswer.value == null || currentAnswer.value.trim().equals(""))) {
				new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(getResources().getString(R.string.answer_required))
		        .setMessage(getResources().getString(R.string.required_question_message))
		        .setPositiveButton(getResources().getString(R.string.provide_answer), new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {}
				})
				.setCancelable(true)
			    .setNegativeButton(getResources().getString(R.string.later), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doShowQuestion(index);
					}
				})
			    .show();
			} else {
				doShowQuestion(index);
			}
		}
	}

	public void doShowQuestion(int index) {
		currentIndex = index;
		currentQuestion = response.survey.questions.toArray(new Question[] {})[index];
		Log.d("EditResponse", "question: " + currentQuestion);
		currentAnswer = response.getOrCreateAnswer(currentQuestion);
		Log.d("EditResponse", "answer: " + currentAnswer);
		
		mTitle = getResources().getText(R.string.question) + ": " + currentQuestion.label;
		getSupportActionBar().setTitle(mTitle);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.container, new QuestionFragment())
		.commit();
	}

	@Override
	public ListAdapter getNavigationDrawerListAdapter() {
		Log.d("EditResponse", "Setting up question drawer");
		return new MyAdapter<Question>(this, new ArrayList<Question>(response.survey.questions), R.layout.question_list_item) {
			@Override
			public void initialiseView(Context context, View view, Question question) {
				((TextView)view.findViewById(R.id.title)).setText(question.label);
				Integer image = null;
				Answer answer = response.getOrCreateAnswer(question);
				if (question.required) {
					if (answer.isComplete())
						image = R.drawable.required_checked;
					else
						image = R.drawable.required;
				} else if (answer.value != null && !answer.value.trim().equals("")) {
					image = R.drawable.checked;
				}
				ImageView required_icon = (ImageView)view.findViewById(R.id.required_icon);
				if (image != null) {
					view.setBackgroundResource(image.intValue());
					required_icon.setImageDrawable(getResources().getDrawable(image));
					required_icon.setVisibility(View.VISIBLE);
				} else {
					required_icon.setVisibility(View.GONE);
				}
			}
		};
	}
	
	@Override
	public Answer getAnswer() {
		return currentAnswer;
	}
	
	private DatabaseMixin db = new DatabaseMixin(this);
	public DatabaseHelper getHelper() {
		return db.getHelper();
	}
}
