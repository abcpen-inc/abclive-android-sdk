package com.abc.live.widget.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.abc.live.R;
import com.abcpen.core.define.ABCConstants;

import com.abcpen.open.api.model.UserMo;
import com.google.android.flexbox.FlexboxLayout;
import com.liveaa.livemeeting.sdk.biz.core.OnEventListener;
import com.abcpen.open.api.model.ABCUserMo;

import org.abcpen.common.util.util.ALog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaocheng on 2017/6/13.
 */

public class ABCInteractiveLiveView extends BaseLeftAnimLayout {

    private static final String TAG = "ABCInteractiveLiveView";

    public static final int MAX_SIZE = 7;

    private ABCFlexLayout flexboxLayout;

    private Map<String, ABCInteractiveItemView> itemViews = new HashMap<>();

    private List<ABCInteractiveItemView> cacheViews = new ArrayList<>();

    private int videoWidth = 0;
    private int videoHeight = 0;
    private int userWidth = 0;

    private List<String> videoDatas = new ArrayList<>();

    private int widthMatchSize = 0;
    OnEventListener onDoubleTapListener = null;

    private boolean isHost = false;
    private int minWidthUser = 0;


    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

    public ABCInteractiveLiveView(Context context) {
        this(context, null);
    }

    public ABCInteractiveLiveView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCInteractiveLiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();

    }

    private void initViews() {
        videoWidth = getResources().getDimensionPixelOffset(R.dimen.abc_video_width);
        videoHeight = getResources().getDimensionPixelOffset(R.dimen.abc_video_height);
        userWidth = getResources().getDimensionPixelOffset(R.dimen.abc_video_height);
        inflate(getContext(), R.layout.view_interactive_video, this);
        flexboxLayout = (ABCFlexLayout) findViewById(R.id.flex_box);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                widthMatchSize = getMeasuredWidth();
                minWidthUser = (int) ((widthMatchSize / (float) MAX_SIZE) - (getResources().getDimensionPixelSize(R.dimen.abc_dp5) * 7));
                if (minWidthUser > userWidth) {
                    minWidthUser = userWidth;
                }
                ALog.d(TAG, "onGlobalLayout: " + minWidthUser);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
    }

    public void setOnChildChangeListener(ABCFlexLayout.OnChildChangeListener onChildChangeListener) {
        flexboxLayout.setOnChildChangeListener(onChildChangeListener);
    }

    @Override
    public void setVisibility(int visibility) {
        for (int i = 0; i < flexboxLayout.getChildCount(); i++) {
            ABCInteractiveItemView itemView = (ABCInteractiveItemView) flexboxLayout.getChildAt(i);
            if (itemView.getIsPlay() && itemView.getSurfaceView() != null) {
                if (visibility == GONE) {
                    itemView.getSurfaceView().setVisibility(GONE);
                } else {
                    itemView.getSurfaceView().setVisibility(VISIBLE);
                }
            }
        }

    }


    public void setUserStatus(ABCUserMo socketUserMo) {

        if (socketUserMo.roleType == ABCConstants.HOST_TYPE) {
            addHostView(socketUserMo);
        } else {
            switch (socketUserMo.ustatus) {
                case ABCConstants.UP_MIC:
                    addUpMicUser(socketUserMo);
                    break;
                case ABCConstants.NOTHING:
                    removeUser(socketUserMo.uid);
                    break;
            }
        }
    }


    /**
     * 添加老师
     *
     * @param socketUserMo
     */
    private void addHostView(ABCUserMo socketUserMo) {
        ABCInteractiveItemView viewForParent = getItemViewForUid(socketUserMo.uid);
        boolean isUpdate = false;
        if (viewForParent != null) {
            isUpdate = true;
        } else {
            viewForParent = newItemView(socketUserMo);
        }

        if (isUpdate) {
            viewForParent.setUserMo(socketUserMo);
        } else {
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(userWidth, userWidth);
            params.setFlexShrink(1);
            params.width = userWidth;
            params.height = userWidth;
            params.setMinWidth(minWidthUser);
            flexboxLayout.addView(viewForParent, 0, params);
        }
        itemViews.put(socketUserMo.uid, viewForParent);
    }

    /**
     * 添加上麦学生
     *
     * @param socketUserMo
     */
    private void addUpMicUser(ABCUserMo socketUserMo) {
        int childCount = flexboxLayout.getChildCount();
        boolean isUpdate = false;
        ABCInteractiveItemView userView = getItemViewForUid(socketUserMo.uid);
        if (userView != null) {
            isUpdate = true;
        } else {
            userView = newItemView(socketUserMo);
        }
        if (!isUpdate) {
            int position;
            if (childCount > 0) {
                ABCInteractiveItemView itemView = (ABCInteractiveItemView) flexboxLayout.getChildAt(childCount - 1);
                if (itemView.getUserMo().roleType != ABCUserMo.HOST_TYPE && itemView.getUserMo().ustatus == ABCUserMo.HAND_UP) {
                    position = flexboxLayout.getChildCount() - 1;
                } else {
                    position = flexboxLayout.getChildCount();
                }
            } else {
                position = 0;
            }
            userView.setUserMo(socketUserMo);
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(userWidth, userWidth);
            params.setFlexShrink(1);
            params.setMinWidth(minWidthUser);
            params.width = userWidth;
            params.height = userWidth;
            flexboxLayout.addView(userView, position, params);
        } else {
            userView.setUserMo(socketUserMo);
        }
        itemViews.put(socketUserMo.uid, userView);
    }


    public void removeUser(String uid) {
        if (itemViews.containsKey(uid)) {
            ABCInteractiveItemView abcInteractiveItemView = itemViews.get(uid);
            abcInteractiveItemView.hideVideoView();
            itemViews.remove(uid);
            int i = flexboxLayout.indexOfChild(abcInteractiveItemView);
            if (i > -1) {
                flexboxLayout.removeView(abcInteractiveItemView);
                if (i == 0 && flexboxLayout.getChildCount() > 0) {
                    View childAt = flexboxLayout.getChildAt(0);
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    layoutParams.leftMargin = 0;
                    childAt.setLayoutParams(layoutParams);
                }

            }
            cacheViews.add(abcInteractiveItemView);
        }
    }

    public ABCInteractiveItemView getItemViewForUid(String uid) {
        if (itemViews.containsKey(uid)) {
            return itemViews.get(uid);
        }
        return null;
    }


    public ABCInteractiveItemView newItemView(ABCUserMo userMo) {
        if (cacheViews.size() > 0) {
            ABCInteractiveItemView abcInteractiveItemView = cacheViews.get(0);
            abcInteractiveItemView.setUserMo(userMo);
            cacheViews.remove(abcInteractiveItemView);
            return abcInteractiveItemView;
        }
        ABCInteractiveItemView itemview = new ABCInteractiveItemView(getContext());
        if (onDoubleTapListener == null) {
            onDoubleTapListener = new OnEventListener(getContext()) {

                @Override
                public void onSingleTapUp(View view) {
                    super.onSingleTapUp(view);
                    if (mOnABCInteractiveListener != null) {
                        mOnABCInteractiveListener.onVideoClick((ABCInteractiveItemView) view.getTag());
                    }
                }

                @Override
                public void onDoubleTap(View view, MotionEvent e) {
                    if (mOnABCInteractiveListener != null) {
                        mOnABCInteractiveListener.onVideoDoubleClick((SurfaceView) view);
                    }
                    super.onDoubleTap(view, e);
                }
            };
        }
        itemview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ABCInteractiveItemView view = (ABCInteractiveItemView) v;
                if (mOnABCInteractiveListener != null) {
                    mOnABCInteractiveListener.onInteractiveItemClick(view, view.getUserMo());
                }
            }
        });
        SurfaceView videoView = itemview.createVideoView();
        videoView.setTag(itemview);
        videoView.setOnTouchListener(onDoubleTapListener);
        itemview.setUserMo(userMo);
        return itemview;
    }


    public SurfaceView getPlayVideoView(ABCUserMo userMo) {
        if (itemViews.containsKey(userMo.uid)) {
            ABCInteractiveItemView itemView = itemViews.get(userMo.uid);
            FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) itemView.getLayoutParams();
            layoutParams.width = videoWidth;
            layoutParams.height = videoHeight;
            itemView.setLayoutParams(layoutParams);
            return itemView.getSurfaceView(true);
        } else {
            if (userMo.roleType == ABCConstants.HOST_TYPE || userMo.ustatus == ABCConstants.UP_MIC) {
                setUserStatus(userMo);
                return getPlayVideoView(userMo);
            }

        }
        return null;
    }


    public void hideItemVideo(String uid) {
        if (itemViews.containsKey(uid)) {
            ABCInteractiveItemView itemView = itemViews.get(uid);
            FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) itemView.getLayoutParams();
            layoutParams.setFlexShrink(1);
            layoutParams.width = userWidth;
            layoutParams.height = userWidth;
            itemView.setLayoutParams(layoutParams);
            itemView.hideVideoView();
            videoDatas.remove(uid);

        }
    }


    private OnABCInteractiveListener mOnABCInteractiveListener;

    public void setOnABCInteractiveListener(OnABCInteractiveListener onABCInteractiveListener) {
        this.mOnABCInteractiveListener = onABCInteractiveListener;
    }


    public boolean isCanUpMic() {
        return getChildCount() < 7;
    }


    public interface OnABCInteractiveListener {

        void onInteractiveItemClick(ABCInteractiveItemView interactiveItemView, ABCUserMo socketUserMo);

        void onVideoClick(ABCInteractiveItemView interactiveItemView);

        void onVideoDoubleClick(SurfaceView tag);
    }


}
