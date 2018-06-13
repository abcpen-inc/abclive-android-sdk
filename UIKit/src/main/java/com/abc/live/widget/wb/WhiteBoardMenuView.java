package com.abc.live.widget.wb;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.abc.live.R;
import com.abc.live.widget.common.ABCBaseRightAnimLayout;
import com.liveaa.livemeeting.sdk.Constants;
import com.liveaa.livemeeting.sdk.biz.core.ABCWhiteboardFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaocheng on 2017/6/6.
 */

public class WhiteBoardMenuView extends ABCBaseRightAnimLayout {

    private ABCWhiteboardFragment mAbcWhiteboardFragment;
    private int mColorIndex = 0;
    private int mWidthIndex = 0;

    private AllAngleExpandableButton alPen;
    private AllAngleExpandableButton alSize;
    private AllAngleExpandableButton alColor;
    private AllAngleExpandableButton alDel;
    private AllAngleExpandableButton alAdd;
    private AllAngleExpandableButton alEraser;

    private AllAngleExpandableButton mLastExpandableButton = null;

    private List<AllAngleExpandableButton> itemButtons;

    private OnItemClickListener mListener;

    private boolean isCanReset = false;
    private boolean isCanAddPage = false;

    private List<ButtonData> penButtonDatas, sizeButtonDatas, colorButtonDatas;
    private ArrayList<ButtonData> alEraserButtonDatas;

    public WhiteBoardMenuView(Context context) {
        this(context, null);
    }

    public WhiteBoardMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WhiteBoardMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onYunPanClick();

        void onAddImageClick();

        /**
         * 重置白板
         */
        void onResetClick();

        /**
         * clean白板
         */
        void onCleanClick();


        void onWbAddPageClick();
    }

    public void init(ABCWhiteboardFragment whiteboardFragment) {
        this.mAbcWhiteboardFragment = whiteboardFragment;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        inflate(getContext(), R.layout.abc_white_board_view, this);
        initView();
    }


    public void setCanReset(boolean isCanReset) {
        this.isCanReset = isCanReset;
        changeCanReset();
    }

    public void setCanAddPage(boolean isCanAddPage) {
        this.isCanAddPage = isCanAddPage;
        mAbcWhiteboardFragment.setCanChangePage(isCanAddPage);
        changeCanAddPage();
    }

    private void initView() {

        alPen = (AllAngleExpandableButton) findViewById(R.id.al_pen);
        penButtonDatas = new ArrayList<>();
        int[] penDrawable = {R.drawable.abc_wb_pen_bg};
        for (int i = 0; i < penDrawable.length; i++) {
            ButtonData buttonData = ButtonData.buildIconButton(getContext(), penDrawable[i], 0);
            buttonData.setSelect(true);
            penButtonDatas.add(buttonData);
        }
        alPen.setIsCanExpand(false);
        alPen.setButtonDatas(penButtonDatas);
        alPen.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(ButtonData buttonData, int index) {

            }

            @Override
            public void onExpand(AllAngleExpandableButton view) {
            }

            @Override
            public void onCollapse(AllAngleExpandableButton view) {
            }

            @Override
            public void onParentClicked(AllAngleExpandableButton view) {
                changeSelect(view);
            }
        });


        alSize = (AllAngleExpandableButton) findViewById(R.id.al_size);
        sizeButtonDatas = new ArrayList<>();
        int[] sizeDrawable = {R.drawable.abc_wb_size_bg, R.drawable.abc_wb_size_item_big, R.drawable.abc_wb_size_item_mid, R.drawable.abc_wb_size_item_small,};
        for (int i = 0; i < sizeDrawable.length; i++) {
            ButtonData buttonData = ButtonData.buildIconButton(getContext(), sizeDrawable[i], 0);
            if (i == 3) {
                buttonData.setSelect(true);
            }
            sizeButtonDatas.add(buttonData);
        }
        alSize.setButtonDatas(sizeButtonDatas);
        alSize.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(ButtonData buttonData, int index) {
                switch (index) {
                    case 0:
                        mWidthIndex = Constants.POS_THICK;
                        break;
                    case 1:
                        mWidthIndex = Constants.POS_MID;
                        break;
                    case 2:
                        mWidthIndex = Constants.POS_THIN;
                        break;
                }
                for (ButtonData btn : sizeButtonDatas) {
                    if (btn != buttonData)
                        btn.setSelect(false);
                    else
                        btn.setSelect(true);
                }

                mAbcWhiteboardFragment.setToolType(mColorIndex, mWidthIndex);
            }

            @Override
            public void onExpand(AllAngleExpandableButton view) {

                changeSelect(view);
            }

            @Override
            public void onCollapse(AllAngleExpandableButton view) {

            }

            @Override
            public void onParentClicked(AllAngleExpandableButton view) {

            }
        });


        alColor = (AllAngleExpandableButton) findViewById(R.id.al_color);
        colorButtonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.abc_wb_color_bg, R.drawable.abc_wb_color_item_black, R.drawable.abc_wb_color_item_blue, R.drawable.abc_wb_color_item_red};
        for (int i = 0; i < drawable.length; i++) {
            ButtonData buttonData = ButtonData.buildIconButton(getContext(), drawable[i], 0);
            if (i == 1) {
                buttonData.setSelect(true);
            }
            colorButtonDatas.add(buttonData);
        }
        alColor.setButtonDatas(colorButtonDatas);
        alColor.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(ButtonData buttonData, int index) {
                switch (index) {
                    case 0:
                        mColorIndex = Constants.POS_BLACK;
                        break;
                    case 1:
                        mColorIndex = Constants.POS_BLUE;
                        break;
                    case 2:
                        mColorIndex = Constants.POS_RED;
                        break;
                }
                for (ButtonData btn : colorButtonDatas) {
                    if (btn != buttonData)
                        btn.setSelect(false);
                    else
                        btn.setSelect(true);
                }
                mAbcWhiteboardFragment.setToolType(mColorIndex, mWidthIndex);
            }

            @Override
            public void onExpand(AllAngleExpandableButton view) {
                changeSelect(view);
            }

            @Override
            public void onCollapse(AllAngleExpandableButton view) {
            }

            @Override
            public void onParentClicked(AllAngleExpandableButton view) {

            }
        });

        alEraser = (AllAngleExpandableButton) findViewById(R.id.al_eraser);
        alEraserButtonDatas = new ArrayList<>();
        int[] alEraserDrawable = {R.drawable.abc_wb_eraser_bg};
        for (int i = 0; i < alEraserDrawable.length; i++) {
            ButtonData buttonData = ButtonData.buildIconButton(getContext(), alEraserDrawable[i], 0);
            alEraserButtonDatas.add(buttonData);
        }
        alEraser.setIsCanExpand(false);
        alEraser.setButtonDatas(alEraserButtonDatas);
        alEraser.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(ButtonData buttonData, int index) {

            }

            @Override
            public void onExpand(AllAngleExpandableButton view) {
//                mAbcWhiteboardFragment.setDrawMode(Constants.FreehandMode);
                mAbcWhiteboardFragment.setToolType(Constants.POS_CLEAR,
                        mWidthIndex);
                changeSelect(view);
            }

            @Override
            public void onCollapse(AllAngleExpandableButton view) {

            }

            @Override
            public void onParentClicked(AllAngleExpandableButton view) {
//                mAbcWhiteboardFragment.setDrawMode(Constants.FreehandMode);
                mAbcWhiteboardFragment.setToolType(Constants.POS_CLEAR,
                        mWidthIndex);
                changeSelect(view);
            }
        });


        newAdd();
        newDel();

        itemButtons = new ArrayList<>(5);
        itemButtons.add(alPen);
        itemButtons.add(alSize);
        itemButtons.add(alColor);
        itemButtons.add(alEraser);
        itemButtons.add(alDel);
        itemButtons.add(alAdd);

        changeSelect(alPen);

    }

    private void newDel() {


        alDel = (AllAngleExpandableButton) findViewById(R.id.al_del);
        changeCanReset();
        alDel.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(ButtonData buttonData, int index) {
                switch (index) {
                    case 0:
                        if (isCanReset)
                            mListener.onResetClick();
                        else
                            mListener.onCleanClick();
                        break;
                    case 1:
                        mListener.onCleanClick();
                        break;

                }
            }

            @Override
            public void onExpand(AllAngleExpandableButton view) {
                resetAllViews(view);
                view.setItemSelected(view.getMainButton(), true);
            }

            @Override
            public void onCollapse(AllAngleExpandableButton view) {
                view.setItemSelected(view.getMainButton(), false);
            }

            @Override
            public void onParentClicked(AllAngleExpandableButton view) {

            }
        });
    }

    private void changeCanReset() {
        final List<ButtonData> alDelButtonDatas = new ArrayList<>();
        int[] alDelDrawable;
        if (isCanReset) {
            alDelDrawable = new int[]{R.drawable.abc_wb_del_bg, R.drawable.abc_wb_del_item_reset, R.drawable.abc_wb_del_item_clean};
        } else {
            alDelDrawable = new int[]{R.drawable.abc_wb_del_bg, R.drawable.abc_wb_del_item_clean};
        }
        for (int i = 0; i < alDelDrawable.length; i++) {
            ButtonData buttonData = ButtonData.buildIconButton(getContext(), alDelDrawable[i], 0);
            alDelButtonDatas.add(buttonData);
        }
        alDel.setButtonDatas(alDelButtonDatas);
    }

    private void newAdd() {
        changeCanAddPage();
        alAdd.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(ButtonData buttonData, int index) {

                switch (index) {
                    case 0:
                        mListener.onYunPanClick();
                        break;
                    case 1:
                        mListener.onAddImageClick();
                        break;
                    case 2:
                        mListener.onWbAddPageClick();
                        break;
                }

            }

            @Override
            public void onExpand(AllAngleExpandableButton view) {
                resetAllViews(view);
                view.setItemSelected(view.getMainButton(), true);
            }

            @Override
            public void onCollapse(AllAngleExpandableButton view) {
                view.setItemSelected(view.getMainButton(), false);
            }

            @Override
            public void onParentClicked(AllAngleExpandableButton view) {

            }
        });
    }

    private void changeCanAddPage() {
        alAdd = (AllAngleExpandableButton) findViewById(R.id.al_add);
        final List<ButtonData> alAddButtonDatas = new ArrayList<>();
        int[] alAddDrawable;
        if (isCanAddPage) {
            alAddDrawable = new int[]{R.drawable.abc_wb_add_bg,
                    R.drawable.abc_wb_yunpan, R.drawable.abc_wb_add_pic, R.drawable.abc_ic_wb_add_default};
        } else {
            alAddDrawable = new int[]{R.drawable.abc_wb_add_bg,
                    R.drawable.abc_wb_yunpan, R.drawable.abc_wb_add_pic};
        }

        for (int i = 0; i < alAddDrawable.length; i++) {
            ButtonData buttonData = ButtonData.buildIconButton(getContext(), alAddDrawable[i], 0);
            alAddButtonDatas.add(buttonData);
        }
        alAdd.setButtonDatas(alAddButtonDatas);

    }


    private void resetAllViews(View view) {
        for (AllAngleExpandableButton item : itemButtons) {
            if (item != view) {
                item.collapse();
            }
        }
    }


    public void setCanDoPreviousPage(boolean isCanDo) {
        if (isLockAnim()) return;
        if (isCanDo) {
            show();
        } else {
            hide();
        }
        mAbcWhiteboardFragment.setEnabled(isCanDo);
    }


    public void changeSelect(AllAngleExpandableButton view) {

        if (view != alEraser) {
            mAbcWhiteboardFragment.setToolType(mColorIndex, mWidthIndex);
        }

        resetAllViews(view);
        if (mLastExpandableButton != null && mLastExpandableButton != view) {
            mLastExpandableButton.setItemSelected(mLastExpandableButton.getMainButton(), false);
        }
        mLastExpandableButton = view;
        mLastExpandableButton.setItemSelected(mLastExpandableButton.getMainButton(), true);
    }
}
