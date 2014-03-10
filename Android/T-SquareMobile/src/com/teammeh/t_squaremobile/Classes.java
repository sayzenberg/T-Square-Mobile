package com.teammeh.t_squaremobile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import android.widget.TextView;

public class Classes extends FragmentActivity {
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
		getDummy();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.classes, menu);
		return true;
	}

	
	protected void getDummy() {
		String url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/dummy";
		HttpGet get = new HttpGet(url);
		new GetCallTask().execute(get);
	}
	
	protected void getAssignments() {
		String url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/getAssignmentsByClass";
		HttpPost post = new HttpPost(url);
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	    postParameters.add(new BasicNameValuePair("classId", classId));
	    try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    new PostCallTask().execute(post);
	}
	
//	protected void pushCallToUi(String text) {
//		TextView t = (TextView)findViewById(R.id.textView1);
//		t.setText(text);
//	}
		
	public class GetCallTask extends AsyncTask<HttpGet, String, String> {

		String mText;
		
		@Override
		protected String doInBackground(HttpGet... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = params[0];
			get.setHeader("Cookie", sessionName+"="+sessionId);
		    HttpResponse response = null;
			try {
				response = client.execute(get);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.getConnectionManager().shutdown();
//			String text = response.getAllHeaders()[0].getName() + ": " + response.getAllHeaders()[0].getValue();
			HttpEntity entity = response.getEntity();
			String responseString = "";
			try {
				responseString = EntityUtils.toString(entity, "UTF-8");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return responseString;

		}
		
		@Override
		protected void onPostExecute(String text) {
			TextView t = (TextView)findViewById(R.id.textView1);
			t.setText(text);
		}
		
	}
	
	public class PostCallTask extends AsyncTask<HttpPost, String, String> {

		String mText;
		
		@Override
		protected String doInBackground(HttpPost... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = params[0];
			post.setHeader("Cookie", sessionName+"="+sessionId);
		    HttpResponse response = null;
			try {
				response = client.execute(post);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.getConnectionManager().shutdown();
//			String text = response.getAllHeaders()[0].getName() + ": " + response.getAllHeaders()[0].getValue();
			HttpEntity entity = response.getEntity();
			String responseString = "";
			try {
				responseString = EntityUtils.toString(entity, "UTF-8");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return responseString;
			
		}
		
		@Override
		protected void onPostExecute(String text) {
			TextView t = (TextView)findViewById(R.id.textView1);
			t.setText(text);
		}
		
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
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
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

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_classes_dummy,
					container, false);
			//TextView dummyTextView = (TextView) rootView
			//		.findViewById(R.id.section_label);
			//dummyTextView.setText(Integer.toString(getArguments().getInt(
			//		ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        onBackPressed();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

}