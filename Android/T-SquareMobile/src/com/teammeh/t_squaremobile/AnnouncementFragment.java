package com.teammeh.t_squaremobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teammeh.t_squaremobile.AssignmentFragment.GetAssignmentsTask;
import com.teammeh.t_squaremobile.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AnnouncementFragment extends ListFragment {

	String sessionName;
	String sessionId;
	String classId;

	private ArrayList<Announcement> announcements;

	private OnAnnouncementFragmentInteractionListener mListener;

	// TODO: Rename and change types of parameters
	public static AnnouncementFragment newInstance(String sessionName, String sessionId, String classId) {
		AnnouncementFragment fragment = new AnnouncementFragment();
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
	public AnnouncementFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sessionName = GlobalState.getSessionName();
		sessionId = GlobalState.getSessionId();

		if (getArguments() != null) {
			//			this.sessionName = getArguments().getString("sessionName");
			//			this.sessionId = getArguments().getString("sessionId");
			this.classId = getArguments().getString("classId");
			getAnnouncements();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnAnnouncementFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Announcement announcement = this.announcements.get(position);

		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			mListener
			.onAnnouncementFragmentInteraction(announcement);
		}
	}

	public void parseJson(JSONObject items) {
		ArrayList<Announcement> list = new ArrayList<Announcement>();
		JSONArray array;
		try {
			array = items.getJSONArray("announcement_collection");
//			System.out.println(array.length());
			int i = 0;
			JSONObject obj = array.optJSONObject(i);
			while(obj != null) {
				list.add(new Announcement(obj));
				i++;
				obj = array.optJSONObject(i);

			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(list.size() == 0) {
			list.add(new Announcement("0", "", "No announcements at this time", "", ""));
		}
		this.announcements = list;
		setListAdapter(new AnnouncementListAdapter(getActivity(),
				android.R.layout.simple_list_item_1, announcements));

	}

	protected void getAnnouncements() {
		String url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/getAnnouncementsByClass";
		HttpPost post = new HttpPost(url);
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("classId", classId));
		try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new GetAnnouncementsTask().execute(post);
	}

	public class GetAnnouncementsTask extends AsyncTask<HttpPost, String, JSONObject> {

		String mText;

		protected JSONObject extractJson(HttpEntity entity) {
			InputStream stream = null;
			BufferedReader reader;
			String line = "";
			String result = "";
			JSONObject jObject = null;

			try {
				stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null)
				{
					sb.append(line + "\n");
				}
				result = sb.toString();
//				System.out.println(result);
				jObject = new JSONObject(result);
//				System.out.println("Length of JSON: " + jObject.length());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try{if(stream != null)stream.close();}catch(Exception squish){}
			}
			return jObject;
		}

		@Override
		protected JSONObject doInBackground(HttpPost... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = params[0];
			post.setHeader("Cookie", sessionName+"="+sessionId);
			HttpEntity entity = null;
			HttpResponse response = null;
			JSONObject jObject = null;	    	    
			try {
				Header[] headers = post.getAllHeaders();
				for(Header header : headers) {
//					System.out.println(header.getName() + " " + header.getValue());
				}
				response = client.execute(post);
				entity = response.getEntity();
				jObject = extractJson(entity);
//				System.out.println(response.getStatusLine());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.getConnectionManager().shutdown();

			return jObject;

		}

		@Override
		protected void onPostExecute(JSONObject jObject) {
			parseJson(jObject);
			//			if(jArray != null && jArray.length() > 0) {
			//				TextView t = (TextView)findViewById(R.id.textView1);
			//				try {
			//					t.setText(jArray.getString(0));
			//				} catch (JSONException e) {
			//					// TODO Auto-generated catch block
			//					e.printStackTrace();
			//				}
			//			}
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnAnnouncementFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onAnnouncementFragmentInteraction(Announcement announcement);
	}

}
