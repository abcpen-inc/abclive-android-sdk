package com.abc.live.widget.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.live.R;
import com.abc.live.widget.img.mask.PorterShapeImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.abcpen.open.api.model.ABCUserMo;
import com.bumptech.glide.Glide;
import com.liveaa.livemeeting.sdk.biz.core.ABCInteractiveCloudVideo;

/**
 * Created by zhaocheng on 2017/6/13.
 */

public class ABCInteractiveItemView extends RelativeLayout {
    private PorterShapeImageView userIcon;
    private TextView tvUserName, tvUserNameVideo;
    private FrameLayout fmVideo;
    private RelativeLayout rlUserParent;
    private FrameLayout fmVideoParent;
    private ImageView ivHandUp;
    private ABCUserMo mUserMo;
    private boolean isPlay = false;

    public ABCInteractiveItemView(Context context) {
        this(context, null);
    }

    public ABCInteractiveItemView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCInteractiveItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.abc_interactive_item_user_view, this);
        userIcon = (PorterShapeImageView) findViewById(R.id.user_icon);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserNameVideo = (TextView) findViewById(R.id.tv_user_name_video);
        fmVideo = (FrameLayout) findViewById(R.id.fm_video);
        rlUserParent = (RelativeLayout) findViewById(R.id.rl_user_detail);
        ivHandUp = (ImageView) findViewById(R.id.iv_hand_up);
        fmVideoParent = (FrameLayout) findViewById(R.id.fm_video_parent);
    }

    public void hideVideoView() {
        isPlay = false;
        setVideoViewVisibility(GONE);
        setUserDetailVisibility(VISIBLE);
        showUserDetail();
    }

    public boolean getIsPlay() {
        return isPlay;
    }

    public SurfaceView getSurfaceView() {
        if (isPlay && fmVideo.getChildCount() > 0) {
            return (SurfaceView) fmVideo.getChildAt(0);
        }
        return null;
    }


    public SurfaceView getSurfaceView(boolean isPlay) {
        SurfaceView videoView;
        this.isPlay = isPlay;
        if (fmVideo.getChildCount() == 0) {
            videoView = createVideoView();
        } else {
            videoView = (SurfaceView) fmVideo.getChildAt(0);
        }
        if (isPlay) {
            setVideoViewVisibility(VISIBLE);
            setUserDetailVisibility(GONE);
        }

        return videoView;
    }

    public void setUserMo(ABCUserMo socketUserMo) {
        mUserMo = socketUserMo;
        tvUserNameVideo.setText(mUserMo.uname);
        if (!isPlay)
            showUserDetail();
    }

    public ABCUserMo getUserMo() {
        return mUserMo;
    }

    public SurfaceView createVideoView() {
        SurfaceView rendererView = ABCInteractiveCloudVideo.createRendererView(getContext());
        rendererView.setZOrderMediaOverlay(true);
        fmVideo.removeAllViews();
        fmVideo.addView(rendererView);
        return rendererView;
    }

    private void showUserDetail() {
        setVideoViewVisibility(GONE);
        tvUserName.setText(mUserMo.uname);
        Glide.with(getContext())
                .load(mUserMo.avatar)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.abc_ic_interactive_default)
                        .error(R.drawable.abc_ic_interactive_default)
                ).into(userIcon);
        if (mUserMo.ustatus == ABCUserMo.HAND_UP) {
            ivHandUp.setVisibility(VISIBLE);
            ivHandUp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            ivHandUp.setVisibility(GONE);
            ivHandUp.setOnClickListener(null);
        }
    }

    private void setVideoViewVisibility(int videoViewVisibility) {
        if (fmVideo.getChildCount() > 0) {
            fmVideo.getChildAt(0).setVisibility(videoViewVisibility);
        }
        fmVideoParent.setVisibility(videoViewVisibility);
    }

    public void setUserDetailVisibility(int userDetailVisibility) {
        rlUserParent.setVisibility(userDetailVisibility);
    }


}
