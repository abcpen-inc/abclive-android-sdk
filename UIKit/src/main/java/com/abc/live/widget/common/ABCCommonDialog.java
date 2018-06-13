package com.abc.live.widget.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.live.R;


public class ABCCommonDialog extends Dialog {
	Button mConfirmBtn;
	Button mLeftBtn;
	Button mRightBtn;
	String mLeftStr;
	String mConfirmStr;
	TextView mTitleTv,mTitleContent;
	String mTitleStr,mContentStr;

	int mType;
	private Context ctx;
	private DialogListner mListner;
	private LinearLayout mLayoutYesOrNO;
	private LinearLayout mLayoutComfirm;

	public static final int NEED_BIND = 0;
	public static final int ALERT_DELETE = 1;
	public static final int RETRY = 2;
	public static final int ALERT_DELETE_LINE_BREAK = 3;

	public interface DialogListner {
		public void onConfirm();

		public void onCancel();
	}

	public ABCCommonDialog(Context context, int theme, int type, String leftStr,
						   String rightStr, String title, DialogListner listener) {
		super(context, theme);
		ctx = context;
		mLeftStr = leftStr;
		mConfirmStr = rightStr;
		mTitleStr = title;
		mType = type;
		mListner = listener;
	}

	/**
	 * @param context
	 * @param type
	 * @param title
	 * @param comfirmStr
	 *            表示只有一个按钮
	 * @param listener
	 */
	public ABCCommonDialog(Context context, int type, String title,
						   String comfirmStr, DialogListner listener) {
		super(context, R.style.abc_class_dialog);
		ctx = context;
		mConfirmStr = comfirmStr;
		mTitleStr = title;
		mType = type;
		mListner = listener;
	}

	/**
	 * @param context
	 * @param type
	 *            表示有两个按钮
	 * @param title
	 * @param leftStr
	 * @param rightStr
	 * @param listener
	 */
	public ABCCommonDialog(Context context, int type, String title, String leftStr,
						   String rightStr, DialogListner listener) {
		super(context, R.style.abc_class_dialog);
		ctx = context;
		mLeftStr = leftStr;
		mConfirmStr = rightStr;
		mTitleStr = title;
		mType = type;
		mListner = listener;
	}

	/**
	 * @param context
	 * @param type
	 *            表示有两个按钮
	 * @param title
	 * @param leftStr
	 * @param rightStr
	 * @param listener
	 */
	public ABCCommonDialog(Context context, int type, String title, String content, String leftStr,
						   String rightStr, DialogListner listener) {
		super(context, R.style.abc_class_dialog);
		ctx = context;
		mLeftStr = leftStr;
		mConfirmStr = rightStr;
		mContentStr = content;
		mTitleStr = title;
		mType = type;
		mListner = listener;
	}


	/**
	 * @param context
	 * @param type
	 *            表示有两个按钮
	 * @param title
	 * @param leftStr
	 * @param rightStr
	 * @param listener
	 */
	public ABCCommonDialog(Context context, int type, @StringRes int title,@StringRes int content, @StringRes int leftStr,
						   @StringRes int rightStr, DialogListner listener) {
		super(context, R.style.abc_class_dialog);
		ctx = context;
		mLeftStr =ctx.getString(leftStr);
		mConfirmStr = ctx.getString(rightStr);
		mContentStr = ctx.getString(content);
		mTitleStr = ctx.getString(title);
		mType = type;
		mListner = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.abc_common_dialog);
		mLayoutYesOrNO = (LinearLayout) findViewById(R.id.yes_no);
		mLayoutComfirm = (LinearLayout) findViewById(R.id.confirm_only);

		if (mType == 1) {
			mLayoutYesOrNO.setVisibility(View.VISIBLE);
			mLayoutComfirm.setVisibility(View.GONE);
			mTitleContent = (TextView) this.findViewById(R.id.content);
			mTitleTv = (TextView) this.findViewById(R.id.title);
			mLeftBtn = (Button) this.findViewById(R.id.left);
			mRightBtn = (Button) this.findViewById(R.id.right);

			mTitleTv.setText(Html.fromHtml(mTitleStr));

			mLeftBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mListner.onCancel();
					dismiss();
				}
			});
			mLeftBtn.setText(mLeftStr);

			mRightBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mListner.onConfirm();
					dismiss();
				}
			});
			mRightBtn.setText(mConfirmStr);
			if (!TextUtils.isEmpty(mContentStr)){
				mTitleContent.setVisibility(View.VISIBLE);
				mTitleContent.setText(mContentStr);
			}else{
				mTitleContent.setVisibility(View.GONE);
			}
		} else {
			mLayoutYesOrNO.setVisibility(View.GONE);
			mLayoutComfirm.setVisibility(View.VISIBLE);
			mTitleTv = (TextView) this.findViewById(R.id.title);
			mConfirmBtn = (Button) this.findViewById(R.id.right_confirm);
			mTitleTv.setText(mTitleStr);
			mConfirmBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mListner.onConfirm();
					dismiss();
				}
			});
			mConfirmBtn.setText(mConfirmStr);

		}

	}

}
