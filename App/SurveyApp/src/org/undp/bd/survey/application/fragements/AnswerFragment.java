package org.undp.bd.survey.application.fragements;

import java.util.ArrayList;
import java.util.List;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.Answer;
import org.undp.bd.survey.application.fragements.QuestionFragment.QuestionFragmentCallBacks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

public class AnswerFragment extends Fragment {

	private Answer answer;

	public AnswerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("AnswerFragment", "Type: " + answer.question.option_type);
		if (answer.question.option_type.equals("char")) {
			return createCharAnswer(inflater, container, InputType.TYPE_CLASS_TEXT);
		} else if (answer.question.option_type.equals("text")) {
			return createCharAnswer(inflater, container, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		} else if (answer.question.option_type.equals("email")) {
			return createCharAnswer(inflater, container, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		} else if (answer.question.option_type.equals("integer")) {
			return createCharAnswer(inflater, container, InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		} else if (answer.question.option_type.equals("float")) {
			return createCharAnswer(inflater, container, InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		} else if (answer.question.option_type.equals("select")) {
			return createSelectAnswer(inflater, container);
		} else if (answer.question.option_type.equals("choice")) {
			return createChoiceAnswer(inflater, container);
		} else if (answer.question.option_type.equals("bool_list")) {
			return createBoolListAnswer(inflater, container);
		} else {
			throw new RuntimeException("unknown question type: " + answer.question.option_type);
		}
	}

	public View createCharAnswer(LayoutInflater inflater, ViewGroup container, int inputType) {
		View rootView = inflater.inflate(R.layout.fragment_answer_char, container, false);
		
		EditText text = (EditText) rootView.findViewById(R.id.char_answer);
		if (answer.value != null)
			text.setText(answer.value);
		text.setInputType(inputType);
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
	}

	public View createSelectAnswer(LayoutInflater inflater, ViewGroup container) {
		// TODO clear button non-required
		View rootView = inflater.inflate(R.layout.fragment_answer_select, container, false);

		Spinner select = (Spinner) rootView.findViewById(R.id.select_answer);
		int i = 0;
		String[] options = answer.question.options.split("\\r|\\r\\n|\\n");
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_dropdown_item, options);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select.setAdapter(adapter);
		select.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				answer.value = parent.getItemAtPosition(pos).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				answer.value = null;
			}
		});
		return rootView;
	}

	public View createChoiceAnswer(LayoutInflater inflater, ViewGroup container) {
		// TODO clear button non-required
		View rootView = inflater.inflate(R.layout.fragment_answer_radio, container, false);

		RadioGroup group = (RadioGroup) rootView.findViewById(R.id.radio_answer);
		int i = 0;
		for (String option : answer.question.options.split("\\r|\\r\\n|\\n")) {
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
	}

	public View createBoolListAnswer(LayoutInflater inflater, ViewGroup container) {
		// TODO clear button non-required
		View rootView = inflater.inflate(R.layout.fragment_answer_bool_list, container, false);

		LinearLayout group = (LinearLayout) rootView.findViewById(R.id.bool_list_answer);
		int i = 0;
		final List<String> answers = new ArrayList<String>();
		if (answer.value != null)
			for (String choice : answer.value.split("\n"))
				answers.add(choice);
		for (final String option : answer.question.options.split("\\r|\\r\\n|\\n")) {
			CheckBox cb = new CheckBox(getActivity());
			cb.setText(option);
			if (answers.contains(option))
				cb.setChecked(true);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked && !answers.contains(option))
						answers.add(option);
					else if (!isChecked && answers.contains(option))
						answers.remove(option);
					
					if (answers.size() > 0)
						answer.value = TextUtils.join("\n", answers);
					else
						answer.value = null;
				}
			});
			group.addView(cb, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		answer = ((QuestionFragmentCallBacks)activity).getAnswer();
		Log.d("AnswerFragment", "Attached: " + answer);
	}
}
