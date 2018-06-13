package com.abc.live.ui.dati;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.abc.live.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shaoxiaoze on 2017/10/24.
 */

public class ABCDatiDetailDialog extends Dialog {

    private static final int HEAD_VIEW = 0x001;
    private static final int CONTENT_VIEW = 0x002;

    ABCChartView mChartView;
    RecyclerView mListView;
    ImageView mClose;
    Context ctx;
    ABCDatiDetailDialog.DialogListner mListener;
    int mTotalCount = 0;

    /**
     * 答题选项
     */
    private ArrayList<String> mChartOptionList = new ArrayList<>();
    /**
     * 答题选项对应的人数
     */
    private ArrayList<Integer> mChartOptionNumList = new ArrayList<>();

    /**
     * 回答正确的选项
     */
    private ArrayList<String> mChartCorrectList = new ArrayList<>();

    /**
     * 图表部分
     */
    MyAdapter mAdapter;
    /**
     * 姓名列表
     */
    private ArrayList<String> mNameList = new ArrayList<>();
    /**
     * 选项列表
     */
    private ArrayList<String> mOptionsList = new ArrayList<>();
    /**
     * 时长列表
     */
    private ArrayList<String> mDurationList = new ArrayList<>();
    /**
     * A B C D E F 映射表
     */
    final HashMap alphabet = new HashMap<>();


    public interface DialogListner {
        public void onConfirm();

        public void onCancel();
    }

    /**
     * @param context
     * @param listener
     */
    public ABCDatiDetailDialog(Context context,
                               ArrayList<String> chartOptionList,
                               ArrayList<Integer> chartOptionNumList,
                               ArrayList<String> chatCorrectList,
                               ArrayList<String> namelist,
                               ArrayList<String> optionlist,
                               ArrayList<String> durationList,
                               int totalCount,
                               ABCDatiDetailDialog.DialogListner listener) {
        super(context, R.style.abc_class_dialog);
        ctx = context;
        mChartOptionList = chartOptionList;
        mChartOptionNumList = chartOptionNumList;
        mChartCorrectList = chatCorrectList;
        mListener = listener;
        mNameList = namelist;
        mOptionsList = optionlist;
        mDurationList = durationList;
        mTotalCount = totalCount;
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
        setContentView(R.layout.abc_dati_result_detail);
        mListView = (RecyclerView) findViewById(R.id.user_detail);

        mAdapter = new MyAdapter();
        mListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mListView.setAdapter(mAdapter);

        mClose = (ImageView) findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCancel();
                }
                dismiss();
            }
        });
    }


    class HeadViewTag extends RecyclerView.ViewHolder {
        ABCChartView mChartView;

        public HeadViewTag(View itemView) {
            super(itemView);
            mChartView = (ABCChartView) itemView.findViewById(R.id.barChat);
        }

        public void bindData() {

            mChartView.setOptionPeople(mChartOptionNumList);
            mChartView.setOptionsName(mChartOptionList);
            mChartView.setTotalCount(mTotalCount);
            mChartView.setCorrectName(mChartCorrectList);

        }


    }

    class ItemViewTag extends RecyclerView.ViewHolder {
        protected TextView mOptions;
        protected TextView mName;
        protected TextView mDuration;

        public ItemViewTag(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mOptions = (TextView) itemView.findViewById(R.id.options);
            mDuration = (TextView) itemView.findViewById(R.id.duration);
        }

        public void bindData(String name, String options, String duration) {
            mName.setText(!TextUtils.isEmpty(name) ? name : "");
            mOptions.setText(!TextUtils.isEmpty(options) ? options : "");
            mDuration.setText(!TextUtils.isEmpty(duration) ? duration : "");
        }

    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            if (viewType == HEAD_VIEW) {
                View inflate = LayoutInflater.from(getContext()).inflate(R.layout.abc_view_as_head_view, parent, false);
                holder = new HeadViewTag(inflate);
            } else {
                View inflate = LayoutInflater.from(getContext()).inflate(R.layout.abc_dati_grid_item, parent, false);
                holder = new ItemViewTag(inflate);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == HEAD_VIEW)
                ((HeadViewTag) holder).bindData();
            else
                ((ItemViewTag) holder).bindData(mNameList.get(position - 1), mOptionsList.get(position - 1), mDurationList.get(position - 1));
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEAD_VIEW;
            } else {
                return CONTENT_VIEW;
            }
        }

        @Override
        public int getItemCount() {
            return mNameList.size() + 1;
        }


    }

}
