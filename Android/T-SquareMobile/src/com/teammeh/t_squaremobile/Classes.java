package com.teammeh.t_squaremobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teammeh.t_squaremobile.AnnouncementFragment.OnAnnouncementFragmentInteractionListener;
import com.teammeh.t_squaremobile.AssignmentFragment.OnAssignmentFragmentInteractionListener;

public class Classes extends FragmentActivity implements OnAssignmentFragmentInteractionListener, OnAnnouncementFragmentInteractionListener {
	ListView listview;
	private String setClassname = "";
	private String classId;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	String sessionName;
	String sessionId;

	//	Handler mHandler; // used for network io

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		this.listview = (ListView) findViewById(R.id.listView1);

		super.onCreate(savedInstanceState);
		//		Uri data = getIntent().getData();
		//		sessionName = data.getQueryParameter("sessionName");
		//	    sessionId = data.getQueryParameter("sessionId");
		setContentView(R.layout.activity_classes);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		//Dynamically change the name of the class based on NavDrawer Selection
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			setClassname = extras.getString("className");
			classId = extras.getString("classId");
		}
		setTitle(setClassname);
		sessionName = extras.getString("sessionName");
		sessionId = extras.getString("sessionId");
		//		getAssignments();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.classes, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			//			Fragment fragment = new AssignmentsSectionFragment();
			
			Fragment fragment;
			if(position == 0) {
				fragment = new AnnouncementFragment();
			} else fragment = new AssignmentFragment();
			Bundle args = new Bundle();
//			args.putInt(AssignmentsSectionFragment.ARG_SECTION_NUMBER, position + 1);
			args.putString("sessionName", sessionName);
			args.putString("sessionId", sessionId);
			args.putString("classId", classId);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}

	}

	@Override
	public void onAssignmentFragmentInteraction(Assignment assignment) {
		// TODO Auto-generated method stub
		Toast.makeText(this, assignment.getTitle() + " is due on " + assignment.getDueDate()
				, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAnnouncementFragmentInteraction(Announcement announcement) {
		// TODO Auto-generated method stub
		Toast.makeText(this, announcement.getTitle() + " was posted by " + announcement.getAuthor()
				, Toast.LENGTH_SHORT).show();

	}
}
