package com.abc.live.ui.dati;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.abc.live.R;

import static com.abc.live.ABCLiveUIConstants.TYPE_MULTI_CHOICE;
import static com.abc.live.ABCLiveUIConstants.TYPE_SINGLE_CHOICE;
import static com.abc.live.ABCLiveUIConstants.TYPE_YESNO_CHOICE;

/**
 * Created by shaoxiaoze on 2017/10/26.
 */

public class ABCDatiStudentController {

    private final Context ctx;
    private int mType;
    private int mSelectCount;
    private Button A, B, C, D, E, F;
    private Button mSubmit;

    OnSubmitListener mOnSubmitListener;


    Button[] buttons = new Button[6];
    int[] buttonResNormal = {R.drawable.ic_a_1, R.drawable.ic_b_1, R.drawable.ic_c_1, R.drawable.ic_d_1, R.drawable.ic_e_1,
            R.drawable.ic_f_1, R.drawable.ic_dui_1, R.drawable.ic_cuo_1};
    int[] buttonResSelect = {R.drawable.ic_a_2, R.drawable.ic_b_2, R.drawable.ic_c_2, R.drawable.ic_d_2, R.drawable.ic_e_2,
            R.drawable.ic_f_2, R.drawable.ic_dui_2, R.drawable.ic_cuo_2};
    boolean[] choices = new boolean[6];


    public OnSubmitListener getOnSubmitListener() {
        return mOnSubmitListener;
    }

    public void setOnSubmitListener(OnSubmitListener mOnSubmitListener) {
        this.mOnSubmitListener = mOnSubmitListener;
    }

    public ABCDatiStudentController(final View view, Context ctx, final int type, final int selectCount) {
        this.ctx = ctx;
        this.mType = type;
        this.mSelectCount = selectCount;

        A = (Button) view.findViewById(R.id.A);
        B = (Button) view.findViewById(R.id.B);
        C = (Button) view.findViewById(R.id.C);
        D = (Button) view.findViewById(R.id.D);
        E = (Button) view.findViewById(R.id.E);
        F = (Button) view.findViewById(R.id.F);
        mSubmit = (Button) view.findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSubmitListener != null) {

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < choices.length; i++) {
                        if (choices[i]) {
                            if (!TextUtils.isEmpty(sb.toString())) {
                                sb.append(",");
                            }
                            sb.append(i + 1);
                        }
                    }
                    mOnSubmitListener.onSubmit(mType, sb.toString());
                }
            }
        });

        buttons[0] = A;
        buttons[1] = B;
        buttons[2] = C;
        buttons[3] = D;
        buttons[4] = E;
        buttons[5] = F;


        A.setOnClickListener(new View.OnClickListener() {
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

        B.setOnClickListener(new View.OnClickListener() {
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

        C.setOnClickListener(new View.OnClickListener() {
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

        D.setOnClickListener(new View.OnClickListener() {
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

        E.setOnClickListener(new View.OnClickListener() {
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

        F.setOnClickListener(new View.OnClickListener() {
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

        updateOptions();
    }

    public void updateUI() {
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

    private void updateSingleChoice(final int index) {
        for (int i = 0; i < 6; i++) {
            if (i == index)
                choices[i] = true;
            else
                choices[i] = false;
        }
    }


    private void updateMultiChoice(final int index) {
        choices[index] = !choices[index];
    }

    public void updateOptions() {
        clearChoices();
        if (mType == TYPE_YESNO_CHOICE) {
            mSelectCount = 2;
        }
        for (int i = 0; i < 6; i++) {
            if (i >= mSelectCount)
                buttons[i].setVisibility(View.GONE);
            else
                buttons[i].setVisibility(View.VISIBLE);
        }
    }

    public void setType(final int type) {
        this.mType = type;
    }

    public void setSelectCount(int selectCount) {
        this.mSelectCount = selectCount;
    }

    public interface OnSubmitListener {
        void onSubmit(final int type, final String result);
    }

    public void clearChoices() {
        for (int i = 0; i < choices.length; i++) {
            choices[i] = false;
        }
    }

}
