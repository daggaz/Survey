package org.undp.bd.survey.application.fragements;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.Answer;
import org.undp.bd.survey.application.fragements.QuestionFragment.QuestionFragmentCallBacks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AnswerFragment extends Fragment {

	private Answer answer;

	public AnswerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("AnswerFragment", "Type: " + answer.question.option_type);
		if (answer.question.option_type.equals("char")) {
			View rootView = inflater.inflate(R.layout.fragment_answer_char, container, false);
			EditText text = (EditText) rootView.findViewById(R.id.char_answer);
			if (answer.value != null)
				text.setText(answer.value);
			text.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					answer.value = s.toString();
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			return rootView;
		} else if (answer.question.option_type.equals("choice")) {
			// TODO clear button non-required
			View rootView = inflater.inflate(R.layout.fragment_answer_radio, container, false);
			RadioGroup group = (RadioGroup) rootView.findViewById(R.id.radio_answer);
			int i = 0;
			for (String option : answer.question.options.split("\n")) {
				RadioButton rb = new RadioButton(getActivity());
				rb.setText(option);
				rb.setId(i);
				if (option.equals(answer.value))
					rb.setChecked(true);
				group.addView(rb, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				i++;
			}
			group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					answer.value = ((RadioButton) group.findViewById(checkedId)).getText().toString();
				}
			});
			return rootView;
		} else {
			throw new RuntimeException("unknown question type: " + answer.question.option_type);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		answer = ((QuestionFragmentCallBacks)activity).getAnswer();
		Log.d("AnswerFragment", "Attached: " + answer);
	}
}
