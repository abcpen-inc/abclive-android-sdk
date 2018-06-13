package com.abc.live.ui.dati;


import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abc.live.ABCLiveUIConstants;
import com.abc.live.R;

import static com.abc.live.ABCLiveUIConstants.TYPE_YESNO_CHOICE;

public class ABCDatiTeacherController {

    Context ctx;
    /**
     * 进度条
     */
    ProgressBar seekBar;
    TextView seekBarText;
    /**
     * 正确率
     */
    TextView mAccuracyTv;

    private int mType = ABCLiveUIConstants.TYPE_SINGLE_CHOICE;
    int[] buttonResNormal = {R.drawable.abc_option_a_1, R.drawable.abc_option_b_1,
            R.drawable.abc_option_c_1, R.drawable.abc_option_d_1, R.drawable.abc_option_e_1,
            R.drawable.abc_option_f_1, R.drawable.abc_option_dui_1, R.drawable.abc_option_cuo_1};
    int[] buttonResSelect = {R.drawable.abc_option_a_2, R.drawable.abc_option_b_2, R.drawable.abc_option_c_2,
            R.drawable.abc_option_d_2, R.drawable.abc_option_e_2,
            R.drawable.abc_option_f_2, R.drawable.abc_option_dui_2, R.drawable.abc_option_cuo_2};

    public TextView A, B, C, D, E, F;

    boolean[] rightAnswers = {false, false, false, false, false, false};
    private float mProgress;
    private float mCorrectRate;

    public boolean[] getRightAnswers() {
        return rightAnswers;
    }

    public void setRightAnswers(boolean[] rightAnswers) {
        this.rightAnswers = rightAnswers;
    }

    public boolean[] getAnswers() {
        return answers;
    }

    public void setAnswers(boolean[] answers) {
        this.answers = answers;
    }

    boolean[] answers = {false, false, false, false, false, false};
    TextView[] buttons = new TextView[6];


    public ABCDatiTeacherController(final View view, Context ctx, final int type,
                                    final boolean answers[], boolean[] rightAnswers) {
        this.ctx = ctx;
        this.answers = answers;
        this.rightAnswers = rightAnswers;
        this.mType = type;

        seekBar = (ProgressBar) view.findViewById(R.id.seekbar);
        seekBarText = (TextView) view.findViewById(R.id.seekbar_percet);
        mAccuracyTv = (TextView) view.findViewById(R.id.accuracy);
        A = (TextView) view.findViewById(R.id.A);
        B = (TextView) view.findViewById(R.id.B);
        C = (TextView) view.findViewById(R.id.C);
        D = (TextView) view.findViewById(R.id.D);
        E = (TextView) view.findViewById(R.id.E);
        F = (TextView) view.findViewById(R.id.F);

        buttons[0] = A;
        buttons[1] = B;
        buttons[2] = C;
        buttons[3] = D;
        buttons[4] = E;
        buttons[5] = F;

        updateUI();
    }

    public void updateUI() {
        if (mType == TYPE_YESNO_CHOICE) {
            buttons[0].setVisibility(View.VISIBLE);
            buttons[1].setVisibility(View.VISIBLE);
            buttons[2].setVisibility(View.GONE);
            buttons[3].setVisibility(View.GONE);
            buttons[4].setVisibility(View.GONE);
            buttons[5].setVisibility(View.GONE);

            if (rightAnswers[0]) {
                buttons[0].setBackgroundResource(buttonResSelect[6]);
                buttons[1].setBackgroundResource(buttonResNormal[7]);
            } else if (rightAnswers[1]) {
                buttons[0].setBackgroundResource(buttonResNormal[6]);
                buttons[1].setBackgroundResource(buttonResSelect[7]);
            } else {
                buttons[0].setBackgroundResource(buttonResNormal[6]);
                buttons[1].setBackgroundResource(buttonResNormal[7]);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                if (answers[i]) {
                    buttons[i].setVisibility(View.VISIBLE);
                    if (rightAnswers[i]) {
                        buttons[i].setBackgroundResource(buttonResSelect[i]);
                    } else {
                        buttons[i].setBackgroundResource(buttonResNormal[i]);
                    }
                } else {
                    buttons[i].setVisibility(View.GONE);
                }
            }
        }
    }


    private void updateSeekBar() {
        if (seekBar != null && seekBar.getWidth() != 0) {
            final int current = (int) mProgress;
            seekBar.setProgress(current);
            seekBarText.setText(current + "%");
            final int seekbarWidth = seekBar.getWidth();
            final int seekbarTextWidth = seekBarText.getWidth();
            final float moveWidth = current / (100 *  1.0f) * (seekbarWidth);
            if (moveWidth + seekbarTextWidth <= seekbarWidth) {
                seekBarText.setTranslationX(moveWidth);
            } else {
                seekBarText.setTranslationX(seekbarWidth - 1.5f * seekbarTextWidth);
            }

        }
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        updateSeekBar();
    }

    public void setCorrectRate(float correctRate) {
        this.mCorrectRate = correctRate;
        mAccuracyTv.setText((int) correctRate + "%");
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setNumberStrings(int s, int s1, int s2, int s3, int s4, int s5) {
        A.setText(String.valueOf(s));
        B.setText(String.valueOf(s1));
        C.setText(String.valueOf(s2));
        D.setText(String.valueOf(s3));
        E.setText(String.valueOf(s4));
        F.setText(String.valueOf(s5));
    }
}

