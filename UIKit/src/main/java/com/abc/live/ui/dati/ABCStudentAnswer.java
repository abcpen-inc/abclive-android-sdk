package com.abc.live.ui.dati;

import android.content.Context;
import android.view.View;

import com.abcpen.core.event.room.resp.NewQuestionCard;

import static com.abc.live.ABCLiveUIConstants.TYPE_SINGLE_CHOICE;

/**
 * Created by shaoxiaoze on 2017/10/26.
 */

public class ABCStudentAnswer {

    ABCDatiStudentController controller;

    public ABCStudentAnswer(final View view, final Context context) {
        controller = new ABCDatiStudentController(view, context,
                TYPE_SINGLE_CHOICE, 4
        );
    }

    public void udpateParams(NewQuestionCard new_question_card, ABCDatiStudentController.OnSubmitListener listener) {

        if (new_question_card == null)
            return;
//        ALog.e("StudentAnswer", "udpateParams " + new_question_card.type + " selectCount "
//                + new_question_card.selectcount);
        if (controller != null) {
            controller.setType(new_question_card.type);
            controller.setSelectCount(new_question_card.selectcount);
            controller.setOnSubmitListener(listener);
            controller.updateOptions();
            controller.updateUI();
        }
    }

}
