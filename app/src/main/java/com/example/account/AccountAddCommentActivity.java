package com.example.account;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.example.module.Account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class AccountAddCommentActivity extends ActionBarActivity implements OnClickListener
	{

	private EditText mContentEditText;
	private boolean mCreateNew;
	private Context mContext;
	private Intent  mIntent = null;

	private String mLastSaveContent = "";

	private boolean mTextChanged = false;

	private final String mBullet = " ? ";
	private final String mNewLine = "\n";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(Constants.TAG, "------AccountAddCommentActivity----onCreate -----");
		mContext = this;
		overridePendingTransition(R.anim.push_up, R.anim.push_down);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle(getString(R.string.add_comments_app_name));
		setContentView(R.layout.activity_account_add_comment);
		mContentEditText = (EditText) findViewById(R.id.content);
		
		mIntent = getIntent();
		Bundle bundle = getIntent().getExtras();
		
		if (bundle != null ) {
			String content = bundle.getString("content");
			
			mCreateNew = false;
			mLastSaveContent = content;
		}
		else
		{
			Log.i(Constants.TAG, "------AccountAddCommentActivity----onCreate -bundle==null----");
			return ;
		}

		if(mLastSaveContent != null)
		{
			mContentEditText.setText(mLastSaveContent);	
			mContentEditText.setSelection(mLastSaveContent.length());
		}
		
		if (mCreateNew) {
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			mContentEditText.requestFocus();
			//MobclickAgent.onEvent(mContext, "new_memo");
		} else {
			//MobclickAgent.onEvent(mContext, "edit_memo");
		}

		findViewById(R.id.edit_container).setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			saveCommentsAndLeave();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_container:
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInputFromWindow(
					findViewById(R.id.edit_container)
							.getApplicationWindowToken(),
					InputMethodManager.SHOW_FORCED, 0);
			break;
		default:
			break;
		}
	}

	private boolean clickEnter() {
		int currentPosition = mContentEditText.getSelectionStart();
		int newPosition = currentPosition;
		String currentText = mContentEditText.getText().toString();
		StringBuffer contentBuffer = new StringBuffer(currentText);
		int maxEnd = contentBuffer.length();
		int before3 = ((currentPosition - mBullet.length()) < 0) ? 0
				: (currentPosition - mBullet.length());
		int start = currentText.lastIndexOf(mNewLine, currentPosition - 1) + 1;
		start = (start == -1) ? 0 : start;
		int end = ((start + mBullet.length()) > maxEnd) ? maxEnd
				: (start + mBullet.length());

		if (contentBuffer.substring(start, end).equals(mBullet)) {
			if (maxEnd == end) {
				contentBuffer.replace(start, end, "\n");
				mContentEditText.setText(contentBuffer);
				mContentEditText.setSelection(contentBuffer.length());
				return true;
			} else if (contentBuffer.substring(before3, currentPosition)
					.equals(mBullet)) {
				contentBuffer.replace(start, end, "");
				newPosition = currentPosition - (end - start) + 1;
				mContentEditText.setText(contentBuffer);
				mContentEditText.setSelection(newPosition);
			} else {
				contentBuffer.insert(currentPosition, mNewLine + mBullet);
				mContentEditText.setText(contentBuffer);
				newPosition = ((currentPosition + mBullet.length() + mNewLine
						.length()) > contentBuffer.length()) ? contentBuffer
						.length()
						: (currentPosition + mBullet.length() + mNewLine
								.length());
				mContentEditText.setSelection(newPosition);
			}
			return true;
		}
		return false;

	}

	private Boolean saveComments() {
		if (mCreateNew
				&& mContentEditText.getText().toString().trim().length() == 0) {
			return false;
		}

		if (mLastSaveContent == null) {
			mLastSaveContent = new String(mContentEditText.getText().toString());
		} else {
			if (mLastSaveContent.equals(mContentEditText.getText().toString())) {
				return false;
			}
		}
		//m_CurrentAccount.Comments = mContentEditText.getText().toString();
		//m_CurrentAccount.Action = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP;
		//m_CurrentAccount.SyncTime = new Date().getTime();
		//m_CurrentAccount.save();
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		//MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//MobclickAgent.onResume(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.account_add_comment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			saveCommentsAndLeave();
			break;
		case R.id.comment_finish:
			saveCommentsAndLeave();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveCommentsAndLeave() {
		Boolean bResult = saveComments();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if(bResult)
		{
			mIntent.putExtra("content", mContentEditText.getText().toString());
			setResult(Activity.RESULT_OK, mIntent); 
		}
		
		finish();
		overridePendingTransition(R.anim.out_push_up, R.anim.out_push_down);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			findViewById(R.id.comment_finish).performClick();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}


}
