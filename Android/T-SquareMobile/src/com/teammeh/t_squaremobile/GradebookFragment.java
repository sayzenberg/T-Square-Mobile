package com.teammeh.t_squaremobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class GradebookFragment extends Fragment {

	String classId;

	public static GradebookFragment newInstance(String sessionName, String sessionId, String classId) {
		GradebookFragment fragment = new GradebookFragment();
		Bundle args = new Bundle();
		args.putString("sessionName", sessionName);
		args.putString("sessionId", sessionId);
		args.putString("classId", classId);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public GradebookFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//		sessionName = GlobalState.getSessionName();
		//		sessionId = GlobalState.getSessionId();

		if (getArguments() != null) {
			//			this.sessionName = getArguments().getString("sessionName");
			//			this.sessionId = getArguments().getString("sessionId");
			this.classId = getArguments().getString("classId");
		}

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_gradebook, container, false);
		Button b = (Button) v.findViewById(R.id.gradebook_button);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
						Uri.parse("https://t-square.gatech.edu/portal/site/" + classId));
				startActivity(browserIntent);

			}
		});
		return v;
	}

	public void onButtonPress(View view) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
				Uri.parse("https://t-square.gatech.edu/portal/site/" + classId));
		startActivity(browserIntent);
	}


}
