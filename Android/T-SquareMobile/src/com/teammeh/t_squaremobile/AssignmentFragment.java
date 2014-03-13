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
import android.widget.ListView;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AssignmentFragment extends ListFragment {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	
	String sessionName;
	String sessionId;
	String classId;
	
	private ArrayList<Assignment> assignments;

	private OnFragmentInteractionListener mListener;

	// TODO: Rename and change types of parameters
	public static AssignmentFragment newInstance(String sessionName, String sessionId, String classId) {
		AssignmentFragment fragment = new AssignmentFragment();
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
	public AssignmentFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.sessionName = getArguments().getString("sessionName");
		this.sessionId = getArguments().getString("sessionId");
		this.classId = getArguments().getString("classId");

		
		if (getArguments() != null) {
//			assignments = parseJson((JSONArray)getArguments().get("json"));
			getAssignments();
		}
//		setListAdapter(new AssignmentListAdapter(getActivity(),
//				android.R.layout.simple_list_item_1, assignments));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
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

		Assignment assignment = this.assignments.get(position);
		
		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			mListener
					.onFragmentInteraction(assignment);
		}
	}
	
	public void parseJson(JSONArray items) {
		ArrayList<Assignment> list = new ArrayList<Assignment>();
		for(int i = 0; i < items.length(); i++) {
			try {
				JSONObject obj = items.getJSONObject(i);
				list.add(new Assignment(obj));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		this.assignments = list;
		setListAdapter(new AssignmentListAdapter(getActivity(),
				android.R.layout.simple_list_item_1, assignments));

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
	    new GetAssignmentsTask().execute(post);
	}

	public class GetAssignmentsTask extends AsyncTask<HttpPost, String, JSONArray> {

		String mText;
		
		protected JSONArray extractJson(HttpEntity entity) {
		    InputStream stream = null;
		    BufferedReader reader;
		    String line = "";
		    String result = "";
		    JSONArray jArray = null;
		   
			try {
				stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null)
				{
				    sb.append(line + "\n");
				}
				result = sb.toString();
				jArray = new JSONArray(result);
				System.out.println("Length of JSON: " + jArray.length());
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
			return jArray;
		}
		
		@Override
		protected JSONArray doInBackground(HttpPost... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = params[0];
			post.setHeader("Cookie", sessionName+"="+sessionId);
			HttpEntity entity = null;
		    HttpResponse response = null;
		    JSONArray jArray = null;	    	    
			try {
				Header[] headers = post.getAllHeaders();
				for(Header header : headers) {
					System.out.println(header.getName() + " " + header.getValue());
				}
				response = client.execute(post);
				entity = response.getEntity();
				jArray = extractJson(entity);
				System.out.println(response.getStatusLine());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.getConnectionManager().shutdown();
		    
			return jArray;
			
		}
		
		@Override
		protected void onPostExecute(JSONArray jArray) {
			parseJson(jArray);
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
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Assignment assignment);
	}
	
	
}
