package com.example.chat;

import java.util.ArrayList;
import java.util.List;

import com.example.app.MyApplaction;
import com.example.utils.LockPatternUtils;
import com.example.view.LockPatternView;
import com.example.view.LockPatternView.Cell;
import com.example.view.LockPatternView.DisplayMode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LockActivity extends Activity implements OnClickListener {

	private LockPatternView lockPatternView;
	private Button mFooterLeftButton;
	private LockPatternView lpv_lock;
	private static final int ID_EMPTY_MESSAGE = -1;
	private Stage mUiStage = Stage.Introduction;
	private final List<LockPatternView.Cell> mAnimatePattern = new ArrayList<LockPatternView.Cell>();
	private static final String KEY_PATTERN_CHOICE = "chosenPattern";
	private TextView mHeaderText;
	private Button mFooterRightButton;
	protected List<LockPatternView.Cell> mChosenPattern = null;

	private static final String KEY_UI_STAGE = "uiStage";

	/**
	 * Keep track internally of where the user is in choosing a pattern.
	 */
	protected enum Stage {

		Introduction(R.string.lockpattern_recording_intro_header,
				LeftButtonMode.Cancel, RightButtonMode.ContinueDisabled,
				ID_EMPTY_MESSAGE, true), HelpScreen(
				R.string.lockpattern_settings_help_how_to_record,
				LeftButtonMode.Gone, RightButtonMode.Ok, ID_EMPTY_MESSAGE,
				false), ChoiceTooShort(
				R.string.lockpattern_recording_incorrect_too_short,
				LeftButtonMode.Retry, RightButtonMode.ContinueDisabled,
				ID_EMPTY_MESSAGE, true), FirstChoiceValid(
				R.string.lockpattern_pattern_entered_header,
				LeftButtonMode.Retry, RightButtonMode.Continue,
				ID_EMPTY_MESSAGE, false), NeedToConfirm(
				R.string.lockpattern_need_to_confirm, LeftButtonMode.Cancel,
				RightButtonMode.ConfirmDisabled, ID_EMPTY_MESSAGE, true), ConfirmWrong(
				R.string.lockpattern_need_to_unlock_wrong,
				LeftButtonMode.Cancel, RightButtonMode.ConfirmDisabled,
				ID_EMPTY_MESSAGE, true), ChoiceConfirmed(
				R.string.lockpattern_pattern_confirmed_header,
				LeftButtonMode.Cancel, RightButtonMode.Confirm,
				ID_EMPTY_MESSAGE, false);

		/**
		 * @param headerMessage
		 *            The message displayed at the top.
		 * @param leftMode
		 *            The mode of the left button.
		 * @param rightMode
		 *            The mode of the right button.
		 * @param footerMessage
		 *            The footer message.
		 * @param patternEnabled
		 *            Whether the pattern widget is enabled.
		 */
		Stage(int headerMessage, LeftButtonMode leftMode,
				RightButtonMode rightMode, int footerMessage,
				boolean patternEnabled) {
			this.headerMessage = headerMessage;
			this.leftMode = leftMode;
			this.rightMode = rightMode;
			this.footerMessage = footerMessage;
			this.patternEnabled = patternEnabled;
		}

		final int headerMessage;
		final LeftButtonMode leftMode;
		final RightButtonMode rightMode;
		final int footerMessage;
		final boolean patternEnabled;
	}

	/**
	 * The states of the left footer button.
	 */
	enum LeftButtonMode {
		Cancel(R.string.lockpattern_retry_button_text, true), CancelDisabled(
				R.string.lockpattern_retry_button_text, false), Retry(
				R.string.lockpattern_retry_button_text, true), RetryDisabled(
				R.string.lockpattern_retry_button_text, false), Gone(
				ID_EMPTY_MESSAGE, false);

		/**
		 * @param text
		 *            The displayed text for this mode.
		 * @param enabled
		 *            Whether the button should be enabled.
		 */
		LeftButtonMode(int text, boolean enabled) {
			this.text = text;
			this.enabled = enabled;
		}

		final int text;
		final boolean enabled;
	}

	enum RightButtonMode {
		Continue(R.string.lockpattern_continue_button_text, true), ContinueDisabled(
				R.string.lockpattern_continue_button_text, false), Confirm(
				R.string.lockpattern_confirm_button_text, true), ConfirmDisabled(
				R.string.lockpattern_confirm_button_text, false), Ok(
				R.string.ok, true);

		/**
		 * @param text
		 *            The displayed text for this mode.
		 * @param enabled
		 *            Whether the button should be enabled.
		 */
		RightButtonMode(int text, boolean enabled) {
			this.text = text;
			this.enabled = enabled;
		}

		final int text;
		final boolean enabled;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lock);
		initView();

		if (savedInstanceState == null) {
			// ��ʼ���ս���
			updateStage(Stage.Introduction);
		} else {
			//��һ�ν��������������
			final String patternString = savedInstanceState
					.getString(KEY_PATTERN_CHOICE);
			if (patternString != null) {
				// ���ܣ����ڱ���״̬����
				mChosenPattern = LockPatternUtils
						.stringToPattern(patternString);
			}
			updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
		}
	}

	private void initView() {
		lpv_lock = (LockPatternView) findViewById(R.id.lpv_lock);
		mFooterLeftButton = (Button) findViewById(R.id.bt_reDraw);
		mHeaderText = (TextView) findViewById(R.id.tv_gesturepwd_create_text);
		mFooterRightButton = (Button) findViewById(R.id.bt_drawHandPwd);

		lpv_lock.setOnPatternListener(mChooseNewLockPatternListener);
		mFooterLeftButton.setOnClickListener(this);
		mFooterRightButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_reDraw:
			if (mUiStage.leftMode == LeftButtonMode.Retry) {
				mChosenPattern = null;
				lpv_lock.clearPattern();
				updateStage(Stage.Introduction);
			} else if (mUiStage.leftMode == LeftButtonMode.Cancel) {
				// They are canceling the entire wizard
				// finish();
				mChosenPattern = null;
				lpv_lock.clearPattern();
				updateStage(Stage.Introduction);
			} else {
				throw new IllegalStateException(
						"left footer button pressed, but stage of " + mUiStage
								+ " doesn't make sense");
			}

			break;

		case R.id.bt_drawHandPwd:
			// ����ұߵİ�ťģʽΪ�����Ļ��������һ�εĻ��Ƶ�״̬
			if (mUiStage.rightMode == RightButtonMode.Continue) {
				mFooterRightButton.setVisibility(View.GONE);
				if (mUiStage != Stage.FirstChoiceValid) {
					throw new IllegalStateException("expected ui stage "
							+ Stage.FirstChoiceValid + " when button is "
							+ RightButtonMode.Continue);
				}
				updateStage(Stage.NeedToConfirm);
			} else if (mUiStage.rightMode == RightButtonMode.Confirm) {
				mFooterRightButton.setVisibility(View.GONE);
				if (mUiStage != Stage.ChoiceConfirmed) {
					throw new IllegalStateException("expected ui stage "
							+ Stage.ChoiceConfirmed + " when button is "
							+ RightButtonMode.Confirm);
				}
				saveChosenPatternAndFinish();

			} else if (mUiStage.rightMode == RightButtonMode.Ok) {
				if (mUiStage != Stage.HelpScreen) {
					throw new IllegalStateException(
							"Help screen is only mode with ok button, but "
									+ "stage is " + mUiStage);
				}
				// create_gesture.setVisibility(View.VISIBLE);

				lpv_lock.clearPattern();
				lpv_lock.setDisplayMode(DisplayMode.Correct);
				updateStage(Stage.Introduction);
				mFooterRightButton.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}

	}

	// ���ü����¼�
	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {
		// ��ʼ������������
		public void onPatternStart() {
			lpv_lock.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		// �������
		public void onPatternCleared() {
			lpv_lock.removeCallbacks(mClearPatternRunnable);
		}

		// ֹͣ����,��ָ�ɿ�
		public void onPatternDetected(List<LockPatternView.Cell> pattern) {

			if (pattern == null)
				return;
			// �ٴλ��ƽ���ͼ���������������ϴλ��ƵĲ�һ��
			if (mUiStage == Stage.NeedToConfirm
					|| mUiStage == Stage.ConfirmWrong) {
				if (mChosenPattern == null)
					throw new IllegalStateException(
							"null chosen pattern in stage 'need to confirm");
				if (mChosenPattern.equals(pattern)) {
					// �ڶ��λ�����ȷ����������״̬
					updateStage(Stage.ChoiceConfirmed);

					
				} else {
					updateStage(Stage.ConfirmWrong);
				}
				// �����ڵ�һ�λ�����
			} else if (mUiStage == Stage.Introduction
					|| mUiStage == Stage.ChoiceTooShort) {
				if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
					updateStage(Stage.ChoiceTooShort);
				} else {
					// ����һ�λ��Ƶ�ͼ����¼��������ȥ
					mChosenPattern = new ArrayList<LockPatternView.Cell>(
							pattern);

					updateStage(Stage.FirstChoiceValid);// ͼ���Ѽ�¼
				}
			} else {
				throw new IllegalStateException("Unexpected stage " + mUiStage
						+ " when " + "entering the pattern.");
			}

			// //////////////////////////////////////////////

			if (mUiStage.rightMode == RightButtonMode.Continue) {
				mFooterRightButton.setVisibility(View.GONE);
				if (mUiStage != Stage.FirstChoiceValid) {
					throw new IllegalStateException("expected ui stage "
							+ Stage.FirstChoiceValid + " when button is "
							+ RightButtonMode.Continue);
				}
				updateStage(Stage.NeedToConfirm);
			} else if (mUiStage.rightMode == RightButtonMode.Confirm) {
				mFooterRightButton.setVisibility(View.GONE);
				if (mUiStage != Stage.ChoiceConfirmed) {
					throw new IllegalStateException("expected ui stage "
							+ Stage.ChoiceConfirmed + " when button is "
							+ RightButtonMode.Confirm);
				}
				saveChosenPatternAndFinish();

			} else if (mUiStage.rightMode == RightButtonMode.Ok) {
				if (mUiStage != Stage.HelpScreen) {
					throw new IllegalStateException(
							"Help screen is only mode with ok button, but "
									+ "stage is " + mUiStage);
				}
				// create_gesture.setVisibility(View.VISIBLE);
				// mFooterRightButton.setVisibility(View.VISIBLE);
				lpv_lock.clearPattern();
				lpv_lock.setDisplayMode(DisplayMode.Correct);
				updateStage(Stage.Introduction);
			}

		}

		private void patternInProgress() {
			// �����������ڻ�����
			mHeaderText.setText(R.string.lockpattern_recording_inprogress);
			mFooterLeftButton.setEnabled(false);
			mFooterRightButton.setEnabled(false);
		}

		// ��ָ����һ��ԲȦ,��ִ��һ��,�����ʱ�����Ҫ����һ�λ��Ƶ�״̬
		public void onPatternCellAdded(List<Cell> pattern) {

		}
	};

	// �������״̬
	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			lpv_lock.clearPattern();
		}
	};

	// ����״̬
	private void updateStage(Stage stage) {
		mUiStage = stage;
		if (stage == Stage.ChoiceTooShort) {
			mHeaderText.setText(getResources().getString(stage.headerMessage,
					LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
		} else {
			mHeaderText.setText(stage.headerMessage);
		}

		if (stage.leftMode == LeftButtonMode.Gone) {
			mFooterLeftButton.setVisibility(View.GONE);
		} else {
			mFooterLeftButton.setVisibility(View.GONE);
			// mFooterLeftButton.setText(stage.leftMode.text);
			// mFooterLeftButton.setEnabled(stage.leftMode.enabled);
		}

		// mFooterRightButton.setText(stage.rightMode.text);
		// mFooterRightButton.setEnabled(stage.rightMode.enabled);

		// same for whether the patten is enabled
		if (stage.patternEnabled) {
			// �����Ƿ���Խ��л���
			lpv_lock.enableInput();
		} else {
			lpv_lock.disableInput();
		}
		// ����Ϊ����״̬�ʹ���״̬���Ա�
		lpv_lock.setDisplayMode(DisplayMode.Correct);

		switch (mUiStage) {
		case Introduction:
			// ���û���һ��������������
			lpv_lock.clearPattern();
			break;
		case HelpScreen:
			lpv_lock.setPattern(DisplayMode.Animate, mAnimatePattern);
			break;
		case ChoiceTooShort:
			lpv_lock.setDisplayMode(DisplayMode.Wrong);
			postClearPatternRunnable();
			break;
		case FirstChoiceValid:
			// ͼ���Ѽ�¼
			break;
		case NeedToConfirm:
			// ���û��ڶ���������������
			lpv_lock.clearPattern();

			break;
		case ConfirmWrong:
			lpv_lock.setDisplayMode(DisplayMode.Wrong);
			postClearPatternRunnable();
			break;
		case ChoiceConfirmed:
			// ȷ�ϱ�����ͼ��
			break;
		}

	}

	// clear the wrong pattern unless they have started a new one
	// already
	private void postClearPatternRunnable() {
		lpv_lock.removeCallbacks(mClearPatternRunnable);
		lpv_lock.postDelayed(mClearPatternRunnable, 2000);
	}

	// ������������״̬�ķ���
	private void saveChosenPatternAndFinish() {
		MyApplaction.getInstance().getLockPatternUtils()
				.saveLockPattern(mChosenPattern);
		System.out.println("������:"+mChosenPattern);
		startActivity(new Intent(LockActivity.this, TalkingActivity.class));
		// showToast("�������óɹ�");
		// if (goFlag == 0) {
		// startActivity(new Intent(this, RegisterSuccessActivity.class));
		// } else if (goFlag == 1) {
		// startActivity(new Intent(this, RegisterVipSuccessActivity.class));
		// } else if (goFlag == 2) {
		// SharedPreferences share_activity = getSharedPreferences(
		// "flag_activity", MODE_PRIVATE);
		// SharedPreferences.Editor edit = share_activity.edit();
		// edit.putInt("have", 1);
		// edit.commit();
		// Intent intent = new Intent(this, HomeActivity.class);
		// intent.putExtra("activity", isActivity);
		// startActivity(intent);
		// } else if (goFlag == 3) {
		// Intent intent = new Intent(CreateGesturePasswordActivity.this,
		// NewYearActivity.class);
		// // intent.putExtra("flag", 0);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// } else {
		// startActivity(new Intent(this, HomeActivity.class));
		// //
		// }
		 finish();
	}

}
