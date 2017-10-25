package com.example.chat;


import java.util.List;

import com.example.app.MyApplaction;
import com.example.utils.ActivityUtil;
import com.example.utils.LockPatternUtils;
import com.example.view.LockPatternView;
import com.example.view.LockPatternView.Cell;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.test.ActivityUnitTestCase;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class UnLockActivity extends Activity {
	
	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;
	
	private Toast mToast;

	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_un_lock);
		
		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		findViewById(R.id.tv_unlock_forget).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						SharedPreferences sp = getSharedPreferences("config", 0);
						Editor editor = sp.edit();
						editor.putString("username", null);
						editor.putString("password", null);
						editor.commit();
						ActivityUtil.goToActivity(
  								UnLockActivity.this,
								LoginActivity.class);
						finish();

					}
				});
		findViewById(R.id.tv_unlock_another).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						SharedPreferences sp = getSharedPreferences("config", 0);
						Editor editor = sp.edit();
						editor.putString("username", null);
						editor.putString("password", null);
						editor.commit();
						SharedPreferences share_bank = getSharedPreferences(
								"bank_info", MODE_PRIVATE);
						Editor bank_edit = share_bank.edit();
						bank_edit.putString("bank_name", "");
						bank_edit.putString("bank_end", "");
						bank_edit.putString("account_city", "");
						bank_edit.putString("province", "");
						bank_edit.commit();
						ActivityUtil.goToActivity(
								UnLockActivity.this,
								LoginActivity.class);
						finish();

					}
				});
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();

		if (!MyApplaction.getInstance().getLockPatternUtils().savedPatternExists()) {
//			startActivity(new Intent(this, CreateGesturePasswordActivity.class));
			finish();
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (MyApplaction.getInstance().getLockPatternUtils().checkPattern(pattern)) {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Correct);
				Intent intent = new Intent(UnLockActivity.this,
						TalkingActivity.class);
//				 打开新的Activity
				startActivity(intent);
				 showToast("解锁成功");
				finish();
			} else {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							showToast("您已5次输错密码，请30秒后再试");
						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				} else {
					showToast("输入长度不够，请重试");
				}

				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 2000);
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}


		private void patternInProgress() {
		}

		@Override
		public void onPatternCellAdded(List<Cell> pattern) {
			// TODO Auto-generated method stub
			
		}
	};
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			mCountdownTimer = new CountDownTimer(
					LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					} else {
						mHeadTextView.setText("请绘制手势密码");
						mHeadTextView.setTextColor(Color.WHITE);
					}

				}

				@Override
				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};

}
