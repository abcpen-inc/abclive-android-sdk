package com.abc.live.widget.common;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.abc.live.R;

/**
 * Created by zhaocheng on 2017/6/2.
 */

public class ABCSendMsgView extends LinearLayout {

    private EditText etLiveMsg;
    private Button btnSendMsg;
    private OnABCSendMsgListener onABCSendMsgListener;

    public void setOnABCSendMsgListener(OnABCSendMsgListener onABCSendMsgListener) {
        this.onABCSendMsgListener = onABCSendMsgListener;
    }


    public ABCSendMsgView(Context context) {
        this(context, null);
    }

    public ABCSendMsgView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCSendMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void showKeyBoard() {
        etLiveMsg.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getVisibility() == VISIBLE) {
                    etLiveMsg.requestFocus();
                    imm.showSoftInput(etLiveMsg, 0);
                }
            }
        }, 300);

    }


    public interface OnABCSendMsgListener {
        void onSendMsg(String msg);
    }

    private void init() {
        setOrientation(VERTICAL);
        setBackgroundResource(R.color.abc_new_b2);
        inflate(getContext(), R.layout.abc_send_msg_view, this);
        etLiveMsg = (EditText) findViewById(R.id.et_live_msg);
        btnSendMsg = (Button) findViewById(R.id.btn_send_msg);
        findViewById(R.id.ll_send_parent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/6/2  do what?
            }
        });
        etLiveMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    btnSendMsg.setEnabled(true);
                } else {
                    btnSendMsg.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnSendMsg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onABCSendMsgListener != null) {
                    onABCSendMsgListener.onSendMsg(etLiveMsg.getText().toString().trim());
                    etLiveMsg.setText("");
                }
            }
        });


    }


}
