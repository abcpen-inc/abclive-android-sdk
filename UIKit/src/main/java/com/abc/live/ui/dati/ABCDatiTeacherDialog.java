package com.abc.live.ui.dati;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.abc.live.R;

import java.util.HashMap;

import static com.abc.live.ABCLiveUIConstants.TYPE_MULTI_CHOICE;
import static com.abc.live.ABCLiveUIConstants.TYPE_SINGLE_CHOICE;
import static com.abc.live.ABCLiveUIConstants.TYPE_YESNO_CHOICE;

/**
 * Created by shaoxiaoze on 2017/10/24.
 */

public class ABCDatiTeacherDialog extends Dialog {

    private Context ctx;
    ABCDatiTeacherDialog.DialogListner mListener;

    Button mSingleChoice, mMultiChoice, mYesNoChoice;
    Button mA, mB, mC, mD, mE, mF, mAdd, mRemove;
    Button[] buttons = new Button[6];
    int[] buttonResNormal = {R.drawable.ic_a_1, R.drawable.ic_b_1, R.drawable.ic_c_1, R.drawable.ic_d_1, R.drawable.ic_e_1,
            R.drawable.ic_f_1, R.drawable.ic_dui_1, R.drawable.ic_cuo_1};
    int[] buttonResSelect = {R.drawable.ic_a_2, R.drawable.ic_b_2, R.drawable.ic_c_2, R.drawable.ic_d_2, R.drawable.ic_e_2,
            R.drawable.ic_f_2, R.drawable.ic_dui_2, R.drawable.ic_cuo_2};
    boolean[] choices = new boolean[6];

    final HashMap<Integer, String> alphabet = new HashMap<>();
    //默认4个选项
    int currentIndex = 3;
    //
    private int mType = TYPE_SINGLE_CHOICE;
    Button left, right;

    public interface DialogListner {
        public void onConfirm(final int type, final int selectCount, final boolean[] correctAnswers, final String result);

        public void onCancel();
    }

    public ABCDatiTeacherDialog(Context context, int theme,
                                ABCDatiTeacherDialog.DialogListner listener) {
        super(context, theme);
        ctx = context;
        mListener = listener;
        init();
    }

    /**
     * @param context
     * @param listener
     */
    public ABCDatiTeacherDialog(Context context,
                                ABCDatiTeacherDialog.DialogListner listener) {
        super(context, R.style.abc_class_dialog);
        mListener = listener;
        init();
    }

    private void init() {
        alphabet.put(0, "A");
        alphabet.put(1, "B");
        alphabet.put(2, "C");
        alphabet.put(3, "D");
        alphabet.put(4, "E");
        alphabet.put(5, "F");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.abc_dati_teacher_dialog);
        mSingleChoice = (Button) findViewById(R.id.single_choice);
        mSingleChoice.setTag(TYPE_SINGLE_CHOICE);
        mSingleChoice.setSelected(true);
        mMultiChoice = (Button) findViewById(R.id.multi_choice);
        mMultiChoice.setTag(TYPE_MULTI_CHOICE);
        mMultiChoice.setSelected(false);
        mYesNoChoice = (Button) findViewById(R.id.yesno_choice);
        mYesNoChoice.setTag(TYPE_YESNO_CHOICE);
        mYesNoChoice.setSelected(false);

        mA = (Button) findViewById(R.id.A);
        mB = (Button) findViewById(R.id.B);
        mC = (Button) findViewById(R.id.C);
        mD = (Button) findViewById(R.id.D);
        mE = (Button) findViewById(R.id.E);
        mF = (Button) findViewById(R.id.F);
        mAdd = (Button) findViewById(R.id.dati_add);
        mRemove = (Button) findViewById(R.id.dati_remove);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < choices.length; i++) {
                        if (choices[i]) {
                            if (!TextUtils.isEmpty(sb.toString())) {
                                sb.append(",");
                            }
                            sb.append(i + 1);
                        }
                    }
                    final String result = sb.toString();
//                    ALog.e("result", " result " + result);
                    if (TextUtils.isEmpty(result)) {
                        mListener.onCancel();
                    } else {
                        mListener.onConfirm(mType, currentIndex + 1, choices, sb.toString());
                        dismiss();
                    }
                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttons[0] = mA;
        buttons[1] = mB;
        buttons[2] = mC;
        buttons[3] = mD;
        buttons[4] = mE;
        buttons[5] = mF;

        for (int i = 0; i < 6; i++) {
            choices[i] = false;
        }

        mRemove = (Button) findViewById(R.id.dati_remove);

        mSingleChoice.setOnClickListener(mTypeOnClickListener);
        mMultiChoice.setOnClickListener(mTypeOnClickListener);
        mYesNoChoice.setOnClickListener(mTypeOnClickListener);

        mA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == TYPE_SINGLE_CHOICE || mType == TYPE_YESNO_CHOICE) {
                    updateSingleChoice(0);
                } else if (mType == TYPE_MULTI_CHOICE) {
                    updateMultiChoice(0);
                }
                updateUI();
            }
        });

        mB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == TYPE_SINGLE_CHOICE || mType == TYPE_YESNO_CHOICE) {
                    updateSingleChoice(1);
                } else if (mType == TYPE_MULTI_CHOICE) {
                    updateMultiChoice(1);
                }
                updateUI();
            }
        });

        mC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == TYPE_SINGLE_CHOICE) {
                    updateSingleChoice(2);
                } else if (mType == TYPE_MULTI_CHOICE) {
                    updateMultiChoice(2);
                }
                updateUI();
            }
        });

        mD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == TYPE_SINGLE_CHOICE) {
                    updateSingleChoice(3);
                } else if (mType == TYPE_MULTI_CHOICE) {
                    updateMultiChoice(3);
                }
                updateUI();
            }
        });

        mE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == TYPE_SINGLE_CHOICE) {
                    updateSingleChoice(4);
                } else if (mType == TYPE_MULTI_CHOICE) {
                    updateMultiChoice(4);
                }
                updateUI();
            }
        });

        mF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == TYPE_SINGLE_CHOICE) {
                    updateSingleChoice(5);
                } else if (mType == TYPE_MULTI_CHOICE) {
                    updateMultiChoice(5);
                }
                updateUI();
            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex++;
                updateUI();
            }
        });

        mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choices[currentIndex] = false;
                currentIndex--;
                updateUI();
            }
        });
    }

    private void updateSingleChoice(final int index) {
        for (int i = 0; i < 6; i++) {
            if (i == index)
                choices[i] = true;
            else
                choices[i] = false;
        }
    }

    private void updateUI() {
        for (int i = 0; i < 6; i++) {
            if (i <= currentIndex)
                buttons[i].setVisibility(View.VISIBLE);
            else
                buttons[i].setVisibility(View.GONE);
        }

        if (currentIndex >= 5) {
            mAdd.setVisibility(View.GONE);
            mRemove.setVisibility(View.VISIBLE);
            currentIndex = 5;
        } else if (currentIndex <= 1) {
            if (mType == TYPE_YESNO_CHOICE) {
                mAdd.setVisibility(View.GONE);
                mRemove.setVisibility(View.GONE);
            } else {
                mAdd.setVisibility(View.VISIBLE);
                mRemove.setVisibility(View.GONE);
            }
            currentIndex = 1;
        } else {
            mAdd.setVisibility(View.VISIBLE);
            mRemove.setVisibility(View.VISIBLE);
        }


        if (mType == TYPE_YESNO_CHOICE) {
            for (int i = 0; i < 2; i++) {
                buttons[i].setBackgroundResource(choices[i] ? buttonResSelect[6 + i] : buttonResNormal[6 + i]);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                buttons[i].setBackgroundResource(choices[i] ? buttonResSelect[i] : buttonResNormal[i]);
            }
        }
    }

    private void updateMultiChoice(final int index) {
        choices[index] = !choices[index];
    }


    View.OnClickListener mTypeOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTypeClick((Integer) view.getTag());
                }
            };


    private void onTypeClick(int type) {
        mType = type;
        if (mType == TYPE_SINGLE_CHOICE) {
            currentIndex = 3;
        } else if (mType == TYPE_MULTI_CHOICE) {
            currentIndex = 3;
        } else if (mType == TYPE_YESNO_CHOICE) {
            currentIndex = 1;
        }
        updateSelectionUI(mType);
        clearChoices();
        updateUI();
    }

    private void clearChoices() {
        for (int i = 0; i < choices.length; i++) {
            choices[i] = false;
        }
    }

    private void updateSelectionUI(int type) {
        if (type == TYPE_SINGLE_CHOICE) {
            mSingleChoice.setSelected(true);
            setButtonTextColor(mSingleChoice, true);

            mMultiChoice.setSelected(false);
            setButtonTextColor(mMultiChoice, false);

            mYesNoChoice.setSelected(false);
            setButtonTextColor(mYesNoChoice, false);

        } else if (type == TYPE_MULTI_CHOICE) {
            mSingleChoice.setSelected(false);
            setButtonTextColor(mSingleChoice, false);

            mMultiChoice.setSelected(true);
            setButtonTextColor(mMultiChoice, true);

            mYesNoChoice.setSelected(false);
            setButtonTextColor(mYesNoChoice, false);

        } else if (type == TYPE_YESNO_CHOICE) {
            mSingleChoice.setSelected(false);
            setButtonTextColor(mSingleChoice, false);

            mMultiChoice.setSelected(false);
            setButtonTextColor(mMultiChoice, false);

            mYesNoChoice.setSelected(true);
            setButtonTextColor(mYesNoChoice, true);

        }
    }


    private void setButtonTextColor(Button button, boolean selected) {
        if (selected) {
            button.setTextColor(getContext().getResources().getColor(R.color.ios_blue));
        } else
            button.setTextColor(getContext().getResources().getColor(R.color.abc_g3));
    }
}
