package com.teammeh.t_squaremobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	PreferenceManager prefs;
	String sessionName;
	String sessionId;
	String classId;

	int month;
	int day;
	int year;
	String assignDate;
	String myClassName;
	String myClassId;
	
	private String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};

	// Context context;
	private ContentValues values;
	private ExtendedCalendarView calendar;

	private ArrayList<Assignment> assignments;

	private OnAssignmentFragmentInteractionListener mListener;

	// TODO: Rename and change types of parameters
	public static AssignmentFragment newInstance(String sessionName,
			String sessionId, String classId) {
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

		sessionName = GlobalState.getSessionName();
		sessionId = GlobalState.getSessionId();

		if (getArguments() != null) {
			// this.sessionName = getArguments().getString("sessionName");
			// this.sessionId = getArguments().getString("sessionId");
			this.classId = getArguments().getString("classId");
			getAssignments();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnAssignmentFragmentInteractionListener) activity;
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
			mListener.onAssignmentFragmentInteraction(assignment);
		}
	}

	@SuppressWarnings("unchecked")
	public void parseJson(JSONArray items) {
		String months[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sept", "Oct", "Nov", "Dec" };
		ArrayList<Assignment> list = new ArrayList<Assignment>();

		for (int i = 0; i < items.length(); i++) {
			try {
				JSONObject obj = items.getJSONObject(i);
				Assignment assignment;
				if(obj.optString("class_id").equals("")) {
					assignment = new Assignment(obj);
				} else {
					String assignmentId = obj.optString("assignment_id");
					String title = obj.optString("title");
					String openDate = obj.optString("open_date");
					if(!openDate.equals("")) {
						Date date = new Date(Long.parseLong(openDate));
						DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm aaa");
						format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
						openDate = format.format(date);
					}
					String dueDate = obj.optString("due_date");
					if(!dueDate.equals("")) {
						Date date = new Date(Long.parseLong(dueDate));
						DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm aaa");
						format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
						dueDate = format.format(date);

					}
					String instructions = obj.optString("instructions");
					if(instructions != "") {
						Document doc = Jsoup.parseBodyFragment(instructions);
						Element bodyElement = doc.body();
						instructions = bodyElement.text();
					}
					assignment = new Assignment(assignmentId, title, openDate, dueDate, instructions);

				}
				list.add(assignment);
				// To Put Assignments In the Calendar
				if(assignment.getDueDate() != null){
				String date = assignment.getDueDate();
				String delims = "[ ]";
				String[] dateItems = date.split(delims);
				month = Arrays.asList(months).indexOf(dateItems[0]);
				day = Integer.parseInt(dateItems[1].replace(",", ""));
				year = Integer.parseInt(dateItems[2]);
				
				String assignName = assignment.getTitle();
				ArrayList<Course> myClass = GlobalState.getClasses();
				myClassId = classId;
				for (int k = 0; k < myClass.size(); k++) {
					if (myClassId.equals(myClass.get(k).getClassId())) {
						myClassName = myClass.get(k).getClassName();
					}
				}
				assignDate = "Due Date: " + months[month] + " " + day + ", " + year;
				boolean checkEvent = false;
				ArrayList<Items> eventList = AddAssignments
						.readEvents(getActivity());

				for (int k = 0; k < eventList.size(); k++) {
					checkEvent = checkEvent
							|| (Arrays.asList(eventList.get(k).getTitle())
									.contains(myClassName) & Arrays.asList(
									eventList.get(k).getDescription())
									.contains(assignName)
									& Arrays.asList(eventList.get(k).getDate())
									.contains(assignDate));
				}

				if (checkEvent == false) {
						values = AddAssignments.addToCal(myClassName,
								assignName, year, month, day);
						AddAssignments.addToGoogleCalendar(getActivity(), 
								myClassName, assignName, year, month, day);
						Uri uri = getActivity().getContentResolver().insert(
								CalendarProvider.CONTENT_URI, values);
						AddAssignments.setSingleNotification(getActivity(),
								myClassName, assignName, year, month, day);
				}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		this.assignments = list;
		if (assignments != null)
			setListAdapter(new AssignmentListAdapter(getActivity(),
					android.R.layout.simple_list_item_1, assignments));
			//TODO: FIX THIS! NullPointerException
	}
	
	

	protected void getAssignments() {
		String url;
		if(classId.equals("gtc-9371-3996-558c-9d8c-41046acd8ba4")) {
			url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/getDatabaseAssignmentsByClass";
		} else url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/getAssignmentsByClass";
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

	public class GetAssignmentsTask extends
			AsyncTask<HttpPost, String, JSONArray> {

		String mText;

		protected JSONArray extractJson(HttpEntity entity) {
			InputStream stream = null;
			BufferedReader reader;
			String line = "";
			String result = "";
			JSONArray jArray = null;

			try {
				stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream,
						"UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
				jArray = new JSONArray(result);
				// System.out.println("Length of JSON: " + jArray.length());
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
				try {
					if (stream != null)
						stream.close();
				} catch (Exception squish) {
				}
			}
			return jArray;
		}

		@Override
		protected JSONArray doInBackground(HttpPost... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = params[0];
			post.setHeader("Cookie", sessionName + "=" + sessionId);
			HttpEntity entity = null;
			HttpResponse response = null;
			JSONArray jArray = null;
			try {
				Header[] headers = post.getAllHeaders();
				for (Header header : headers) {
					// System.out.println(header.getName() + " " +
					// header.getValue());
				}
				response = client.execute(post);
				entity = response.getEntity();
				if (response.getStatusLine().toString().contains("504")) {
					jArray = null;
				} else {
					// System.out.println(response.getStatusLine());
					jArray = extractJson(entity);
				}
				// System.out.println(response.getStatusLine());
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
			if (jArray != null)
				parseJson(jArray);
			// if(jArray != null && jArray.length() > 0) {
			// TextView t = (TextView)findViewById(R.id.textView1);
			// try {
			// t.setText(jArray.getString(0));
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
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
	public interface OnAssignmentFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onAssignmentFragmentInteraction(Assignment assignment);
	}

}
