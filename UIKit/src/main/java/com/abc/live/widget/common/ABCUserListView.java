package com.abc.live.widget.common;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abc.live.R;
import com.abcpen.open.api.model.ABCUserMo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaocheng on 2017/6/1.
 */

public class ABCUserListView extends ABCBaseRightAnimLayout {

    private RecyclerView mRecyclerLiveUser;
    private TextView mTvOnlineNum;
    private ClassicsFooter classicsFooter;
    private List<ABCUserMo> mUserMos = new ArrayList<>();
    private MyAdapter myAdapter;
    private SmartRefreshLayout swipeToLoadLayout;
    private boolean isInteractive = false;


    private @DrawableRes
    int defaultRes = R.drawable.abc_default_icon;

    public void setOnItemClickListener(OnUserListListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnUserListListener onItemClickListener;

    public ABCUserListView(Context context) {
        this(context, null);
    }

    public ABCUserListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCUserListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setPadding(getResources().getDimensionPixelOffset(R.dimen.abc_dp10), 0, 0, 0);
        setBackgroundResource(R.color.abc_new_b2_80);
        inflate(getContext(), R.layout.abc_view_user_live_list, this);
        mRecyclerLiveUser = (RecyclerView) findViewById(R.id.swipe_target);
        swipeToLoadLayout = (SmartRefreshLayout) findViewById(R.id.swipe_load_layout);
        ClassicsHeader classicsHeader = new ClassicsHeader(getContext());
        classicsHeader.setTextSizeTitle(11);
        classicsHeader.setTextSizeTime(11);
        swipeToLoadLayout.setRefreshHeader(classicsHeader);


        classicsFooter = new ClassicsFooter(getContext());
        classicsFooter.setTextSizeTitle(11);
        swipeToLoadLayout.setRefreshFooter(classicsFooter);

        mTvOnlineNum = (TextView) findViewById(R.id.tv_on_line_num);
        mRecyclerLiveUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        myAdapter = new MyAdapter();
        mRecyclerLiveUser.setAdapter(myAdapter);


        swipeToLoadLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (onItemClickListener != null) {
                    onItemClickListener.onLoadMore();
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (onItemClickListener != null) {
                    onItemClickListener.onRefresh();
                }
            }
        });


    }

    public void setUserCount(int count) {
        mTvOnlineNum.setText(getContext().getString(R.string.abc_online_num, count));
    }


    public void loadComplete(boolean isError) {
        swipeToLoadLayout.finishRefresh(!isError);
        swipeToLoadLayout.finishLoadMore(!isError);
    }

    public void updateUserItem(ABCUserMo abcUserMo) {
        if (abcUserMo != null) {
            int position = getUserPosition(abcUserMo.uid);
            if (position != -1) {
                mUserMos.set(position, abcUserMo);
                myAdapter.notifyItemChanged(position);
            }
        }

    }

    public void removeUserItem(String id) {
        int position = getUserPosition(id);
        if (position != -1 && mUserMos.size() > 1) {
            myAdapter.notifyItemRemoved(position);
        }
    }

    private int getUserPosition(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return -1;
        }
        for (int i = 0; i < mUserMos.size(); i++) {
            if (TextUtils.equals(mUserMos.get(i).uid, uid)) {
                return i;
            }
        }
        return -1;
    }

    public void setUserList(int pageNo, List<ABCUserMo> userList) {
        loadComplete(false);
        classicsFooter.setNoMoreData(false);
        if (pageNo == 1) {
            mUserMos.clear();
            swipeToLoadLayout.finishRefresh();
        } else {
            swipeToLoadLayout.finishLoadMore();
        }
        mUserMos.addAll(userList);
        myAdapter.notifyDataSetChanged();

    }

    public void notifyDataSetChanged() {
        myAdapter.notifyDataSetChanged();
    }


    public void setIsInteractive(boolean isInteractive) {
        this.isInteractive = isInteractive;
    }

    public MyAdapter getMyAdapter() {
        return myAdapter;
    }


    public void setUserDefaultIcon(@DrawableRes int defaultIcon) {
        this.defaultRes = defaultIcon;
    }

    public void loadEmpty() {
        loadComplete(false);
        classicsFooter.setNoMoreData(true);
    }

    public interface OnUserListListener {
        void onClickUser(ABCUserMo socketUserMo);

        void onLoadMore();

        void onRefresh();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ABCCircleImageView ivIcon;
        private ABCUserItemLayout tvData;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvData = (ABCUserItemLayout) itemView.findViewById(R.id.ll_content);
            ivIcon = (ABCCircleImageView) itemView.findViewById(R.id.iv_icon);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClickUser(mUserMos.get(getLayoutPosition()));
                    }
                }
            });
            tvData.setInteractive(isInteractive);
        }

        public void bindData(ABCUserMo socketUserMo) {
            tvData.setUserData(socketUserMo);
            Glide.with(getContext()).load(socketUserMo.avatar).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(defaultRes)
                    .error(defaultRes)
            ).into(ivIcon);
        }
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (onItemClickListener != null && visibility == VISIBLE) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeToLoadLayout.autoRefresh();
                }
            }, 500);

        }

    }


    public class MyAdapter extends RecyclerView.Adapter<UserViewHolder> {


        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.abc_item_user_list, parent, false));
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            holder.bindData(mUserMos.get(position));
        }

        @Override
        public int getItemCount() {
            return mUserMos != null ? mUserMos.size() : 0;
        }
    }

}
