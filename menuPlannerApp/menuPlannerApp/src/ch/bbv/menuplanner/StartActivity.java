package ch.bbv.menuplanner;

import java.io.IOException;
import java.net.InetAddress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class StartActivity extends Activity {

	public static final String DEFAULT_ADDRESS = "10.0.1.8";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private EditText serverAdrView;
	private View mConnectStatusView;
	private View mLoginFormView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_start);

		// Set up the login form.
		serverAdrView = (EditText) findViewById(R.id.serverIp);
		serverAdrView.setText(DEFAULT_ADDRESS);

		mLoginFormView = findViewById(R.id.login_form);

		mConnectStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

//		findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				attemptStart();
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptStart(View view) {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		serverAdrView.setError(null);

		// Store values at the time of the login attempt.
		String serverAdress = serverAdrView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid server address.
		if (TextUtils.isEmpty(serverAdress)) {
			serverAdrView.setError(getString(R.string.error_field_required));
			focusView = serverAdrView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; focus the first form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}
	
	public void startOffline(View view) {
		finish();
		Intent intent = new Intent(StartActivity.this, MainActivity.class);
		intent.removeExtra(MainActivity.SERVER_ADDRESS);
		startActivity(intent);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

		mConnectStatusView.setVisibility(View.VISIBLE);
		mConnectStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mConnectStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			}
		});

		mLoginFormView.setVisibility(View.VISIBLE);
		mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			}
		});
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				InetAddress.getByName(serverAdrView.getText().toString()).isReachable(2000);
			} catch (IOException e) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
				Intent intent = new Intent(StartActivity.this, MainActivity.class);
				intent.putExtra(MainActivity.SERVER_ADDRESS, serverAdrView.getText().toString());
				startActivity(intent);
			} else {
				serverAdrView.setText(getString(R.string.error_incorrect_serverAddress));
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
