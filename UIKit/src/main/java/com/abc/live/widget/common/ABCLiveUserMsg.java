package com.abc.live.widget.common;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.live.R;
import com.liveaa.livemeeting.sdk.model.ImMsgMo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaocheng on 2017/6/1.
 */

public class ABCLiveUserMsg extends ABCBaseRightAnimLayout {

    public static final int SYSTEM_MSG = 1;

    private RecyclerView mRecyclerView;

    private List<ImMsgMo> msgMos;

    private boolean isChangeItemColor = false;

    private MyAdapter myAdapter;

    private OnChangeItemStatusListener mOnChangeItemStatusListener;

    private LinearLayoutManager layoutManager;

    private boolean isScorllTo = true;

    private boolean isTouch = false;

    private int lastVisibleItem = 0;
    private boolean isEdit = false;


    public void setOnChageItemStatusListener(OnChangeItemStatusListener OnChangeItemStatusListener) {
        this.mOnChangeItemStatusListener = OnChangeItemStatusListener;
    }

    public ObjectAnimator getPropertyValuesHolder(int size) {
        return ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, getTranslationY(), getTranslationY() + size, getTranslationY() + size);
    }


    public void changeItemColor() {
        if (!isTouch) {
            isChangeItemColor = !isChangeItemColor;
            myAdapter.notifyDataSetChanged();
        } else if (mOnChangeItemStatusListener != null) {
            mOnChangeItemStatusListener.onMsgShow();
        }


    }

    public ABCLiveUserMsg(Context context) {
        this(context, null);
    }

    public ABCLiveUserMsg(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCLiveUserMsg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void toEnd() {
        if (myAdapter.getItemCount() > 0 && isScorllTo) {
            mRecyclerView.scrollToPosition(myAdapter.getItemCount() - 1);
        }
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
        myAdapter.notifyDataSetChanged();
    }

    public interface OnChangeItemStatusListener {
        void onMsgShow();

        void onMsgHide();

        void onOutMsgSideClick();

        void onMsgClick(ImMsgMo msgMo);

    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView,
                                         int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == myAdapter.getItemCount() - 1) {
                isScorllTo = true;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = layoutManager.findLastVisibleItemPosition();

        }
    };

    private void init() {
        inflate(getContext(), R.layout.abc_view_live_user_msg, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        msgMos = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return !isEdit;
            }
        };
        mRecyclerView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.addOnScrollListener(onScrollListener);


        mRecyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        isScorllTo = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (lastVisibleItem == myAdapter.getItemCount() - 1) {
                            isScorllTo = true;
                        }
                        isTouch = false;
                        break;

                    case MotionEvent.ACTION_DOWN:
                        isTouch = true;
                        break;
                }

                return false;
            }
        });
    }

    public void addMsg(ImMsgMo msgMo) {
        myAdapter.addItem(msgMo);
        isChangeItemColor = false;
        myAdapter.notifyDataSetChanged();
        if (mOnChangeItemStatusListener != null)
            mOnChangeItemStatusListener.onMsgShow();
        toEnd();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvUserMsg;
        private LinearLayout llParent;

        public ViewHolder(View itemView) {
            super(itemView);
            if (!isEdit) {
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnChangeItemStatusListener != null) {
                            mOnChangeItemStatusListener.onOutMsgSideClick();
                        }
                    }
                });
            } else {
                itemView.setOnClickListener(null);
            }
            tvUserName = (TextView) itemView.findViewById(R.id.tv_msg_send_name);
            tvUserMsg = (TextView) itemView.findViewById(R.id.tv_user_msg);
            llParent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
            llParent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isChangeItemColor = false;
                    myAdapter.notifyDataSetChanged();
                    if (mOnChangeItemStatusListener != null) {
                        mOnChangeItemStatusListener.onMsgShow();
                        mOnChangeItemStatusListener.onMsgClick(msgMos.get(getLayoutPosition()));
                    }

                }
            });
        }

        public void bindData(ImMsgMo msgMo) {
            if (isChangeItemColor) {
                llParent.setBackgroundResource(R.drawable.abc_bg_round_b2_30);
                if (mOnChangeItemStatusListener != null)
                    mOnChangeItemStatusListener.onMsgHide();
            } else {
                llParent.setBackgroundResource(R.drawable.abc_bg_round_b2_60);
            }
            if (msgMo.type == SYSTEM_MSG) {
                tvUserName.setTextColor(getResources().getColor(R.color.abc_msg_sysyem_color));
                tvUserName.setText(R.string.abc_system_msg);
            } else {
                tvUserName.setTextColor(getResources().getColor(R.color.abc_new_c1));
                tvUserName.setText(getResources().getString(R.string.abc_msg_user_name, msgMo.name));
            }

            tvUserMsg.setText(msgMo.msgValue);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.abc_item_user_msg_view, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindData(msgMos.get(position));
        }

        @Override
        public int getItemCount() {
            return msgMos.size();
        }

        public void addItem(ImMsgMo msgMo) {
            msgMos.add(msgMo);
            notifyItemInserted(msgMos.size());

        }
    }

}
