package org.undp.bd.survey.application.fragements;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.Answer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QuestionFragment extends Fragment {
	
	public interface QuestionFragmentCallBacks {
		public Answer getAnswer();
	}

	private Answer answer;

	public QuestionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_question, container, false);
		
		String questionText = answer.question.question;
		if (answer.question.required) {
			TextView requiredHint = (TextView)rootView.findViewById(R.id.required_hint);
			requiredHint.setText("* " + getResources().getString(R.string.required));
			requiredHint.setVisibility(View.VISIBLE);
			questionText = "*" + questionText;
		}
		((TextView)rootView.findViewById(R.id.question)).setText(questionText);
		if (answer.question.help_text != null && !answer.question.help_text.trim().equals("")) {
			TextView helpText = (TextView)rootView.findViewById(R.id.help_text);
			helpText.setText(answer.question.help_text);
			helpText.setVisibility(View.VISIBLE);
		}
		
		getChildFragmentManager()
		.beginTransaction()
		.replace(R.id.answer_container, new AnswerFragment())
		.commit();
		
		rootView.findViewById(R.id.previous_button).setEnabled(answer.question.hasPrevious());
		rootView.findViewById(R.id.next_button).setEnabled(answer.question.hasNext());
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("QuestionFragment", "Attached");
		answer = ((QuestionFragmentCallBacks)activity).getAnswer();
	}
}
