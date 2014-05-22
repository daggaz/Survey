package org.undp.bd.survey.application.fragements;

import org.undp.bd.survey.application.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EditSurveyNavigationDrawerFooter extends Fragment {

	public EditSurveyNavigationDrawerFooter() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_navigation_drawer_footer, container, false);
	}
}
