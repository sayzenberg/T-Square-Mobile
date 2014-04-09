package com.teammeh.t_squaremobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri data = intent.getData();
		if(data != null) {
			if(data.getQueryParameter("sessionName") != null && data.getQueryParameter("sessionId") != null) {
				GlobalState.setSessionName(data.getQueryParameter("sessionName"));
				GlobalState.setSessionId(data.getQueryParameter("sessionId"));
			}
		}
		Intent homeIntent = new Intent(this, HomeScreenActivity.class);
		startActivity(homeIntent);
	}

	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.login, menu);
	//	return true;
	//}

	public void onPress(View view) {
		
//		Intent intent = new Intent(this, HomeScreenActivity.class);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dev.m.gatech.edu/login/private?url=tsquaremobile://loggedin&sessionTransfer=window"));
//		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
}
