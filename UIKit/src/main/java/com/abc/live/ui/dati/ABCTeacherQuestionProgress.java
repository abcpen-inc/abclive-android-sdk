package com.abc.live.ui.dati;

import android.content.Context;
import android.view.View;

import com.abcpen.core.event.room.resp.AnswerQuestionNotify;

import static com.abc.live.ABCLiveUIConstants.TYPE_SINGLE_CHOICE;

/**
 * Created by shaoxiaoze on 2017/10/26.
 */

public class ABCTeacherQuestionProgress {

    ABCDatiTeacherController controller;

    public ABCTeacherQuestionProgress(View view, Context context) {
        controller = new ABCDatiTeacherController(view, context,
                TYPE_SINGLE_CHOICE,
                new boolean[]{false, false, false, false, false, false},
                new boolean[]{
                        false, false, false, false, false, false
                });
    }

    public void udpateParams(ABCTeacherDatiStatus teacherDatiStatus, AnswerQuestionNotify notify) {

        boolean[] answers = new boolean[]{false, false, false, false, false, false};
        for (int i = 0; i < teacherDatiStatus.mSelectCount; i++) {
            answers[i] = true;
        }
        final int answeredcount = (notify == null ? 0 : notify.answeredcount);
        final int totalcount = (notify == null ? 0 : notify.totalcount);
        final int correctcount = (notify == null ? 0 : notify.correctcount);
        if (controller != null) {
            controller.setAnswers(answers);
            controller.setRightAnswers(teacherDatiStatus.mCorrectAnswers);
            if (totalcount == 0) {
                controller.setProgress(0);
            } else
                controller.setProgress(answeredcount / (totalcount * 1.0f) * 100);
            if (totalcount == 0) {
                controller.setCorrectRate(0);
            } else {
                controller.setCorrectRate(correctcount / (answeredcount * 1.0f) * 100);
            }
            controller.setType(teacherDatiStatus.mType);
            if (notify != null) {
                controller.setNumberStrings(notify.counta, notify.countb,
                        notify.countc, notify.countd, notify.counte, notify.countf);
            } else {
                controller.setNumberStrings(0, 0, 0, 0, 0, 0);
            }
            controller.updateUI();
        }
    }
}
