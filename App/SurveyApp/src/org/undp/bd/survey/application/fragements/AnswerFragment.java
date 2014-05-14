package org.undp.bd.survey.application.fragements;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.Answer;
import org.undp.bd.survey.application.fragements.QuestionFragment.QuestionFragmentCallBacks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AnswerFragment extends Fragment {

	private Answer answer;

	public AnswerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (answer.question.option_type.equals("char")) {
			View rootView = inflater.inflate(R.layout.fragment_answer_char, container, false);
			return rootView;
		} else {
			throw new RuntimeException("unknown question type: " + answer.question.option_type);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("AnswerFragment", "Attached: " + answer);
		answer = ((QuestionFragmentCallBacks)activity).getAnswer();
	}
}
