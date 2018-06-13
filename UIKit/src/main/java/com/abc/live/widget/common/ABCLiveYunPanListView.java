package com.abc.live.widget.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.live.R;
import com.abcpen.open.api.callback.ABCCallBack;
import com.abcpen.open.api.model.YunPanFileMo;
import com.abcpen.open.api.resp.FileUpLoadUpyResp;
import com.abcpen.open.api.resp.YunPanGetFileListResp;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.util.ABCUtils;
import com.liveaa.livemeeting.sdk.util.YPDownLoadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaocheng on 16/10/12.
 */

public class ABCLiveYunPanListView extends LinearLayout implements YPDownLoadUtils.OnDownLoadCallBack, View.OnClickListener {
    public static final int REC_REQUESTCODE = 999;

    RecyclerView recyclerView;
    ImageView ivEmpty;
    TextView tvRight;
    TextView tvLeft;

    MyAdapter myAdapter;
    ABCCustomProgress loading;

    public void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvRight = (TextView) findViewById(R.id.tv_right);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        ivEmpty = (ImageView) findViewById(R.id.iv_empty);
        tvRight.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
    }

    ProgressDialog progressDialog;
    YunPanListener yunPanListener = null;
    YPDownLoadUtils downLoadUtils;
    private boolean downLoading = false;
    ABCCallBack<YunPanGetFileListResp> baseCallBack;

    @Override
    public void onDownLoadSuccess(String uri, String path) {
        downLoading = false;
        progressDialog.dismiss();
        yunPanListener.onSelectYunPanData(uri, path);
    }

    @Override
    public void onDownLoadProgress(long bytesRead, long contentLength) {
        int percent = (int) (bytesRead / (float) contentLength * 100);
        progressDialog.setProgress(percent);
    }

    @Override
    public void onDownLoadFail() {

    }


    public interface YunPanListener {

        void onClose();

        void onSelectYunPanData(String uri, String path);

    }


    public ABCLiveYunPanListView(Context context) {
        this(context, null);
    }

    public ABCLiveYunPanListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ABCLiveYunPanListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setYunPanListener(YunPanListener yunPanListener) {
        this.yunPanListener = yunPanListener;
    }

    public void refreshData() {
        loading = ABCCustomProgress.show(getContext(), "", true, null);
        if (baseCallBack != null)
            ABCLiveSDK.getInstance(getContext()).getApiServer().getCloudList(1,10,baseCallBack);
    }


    @Override
    public void onClick(View view) {
        if (yunPanListener == null) return;
        if (view.getId() == R.id.tv_right) {
            int position = (int) view.getTag();
            if (position == -1) {
                // go back
                yunPanListener.onClose();
            } else {
                YunPanFileMo item = myAdapter.getItem(position);
                if (downLoadUtils == null)
                    downLoadUtils = new YPDownLoadUtils(getContext(), this);
                String cacheDis = downLoadUtils.getCacheDis(item.url);
                if (!TextUtils.isEmpty(cacheDis)) {
                    yunPanListener.onSelectYunPanData(item.url, cacheDis);
                } else {
                    if (!downLoading) {
                        showLoading(0);
                        downLoading = true;
                        downLoadUtils.downLoadNet(item.url);
                    } else {
                        ABCUtils.showToast(getContext(), getContext().getString(R.string.abc_downloading_pdf));
                    }
                }
            }
        } else {
            openFileSystem();
        }

    }


    private void openFileSystem() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (getContext() instanceof Activity) {
            Activity act = (Activity) getContext();
            act.startActivityForResult(intent, REC_REQUESTCODE);
        }
    }


    private void showLoading(final int type) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getContext().getString(R.string.abc_wait));
        progressDialog.setMessage((type == 0) ? getContext().getString(R.string.abc_downloading) : getContext().getString(R.string.abc_uploading));
        progressDialog.setIndeterminate(false);//设置进度条是否为不明确
        progressDialog.setCancelable(false);//设置进度条是否可以按退回键取消
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }


    private void init() {
        inflate(getContext(), R.layout.abc_view_yunpan, this);
        initView();
        myAdapter = new MyAdapter();
        tvRight.setTag(-1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(myAdapter);

        baseCallBack = new ABCCallBack<YunPanGetFileListResp>() {
            @Override
            public void onSuccess(YunPanGetFileListResp yunPanGetFileListResp) {
                if (loading != null) loading.dismiss();
                if (yunPanGetFileListResp != null && yunPanGetFileListResp.data != null && yunPanGetFileListResp.data.content != null && yunPanGetFileListResp.data.content.size() > 0) {
                    hideEmpty();
                    myAdapter.clearItems();
                    myAdapter.addItemList(yunPanGetFileListResp.data.content);
                } else {
                    showEmpty();
                }
            }

            /**
             * @param code
             * @param msg
             */
            @Override
            public void onError(int code, String msg) {

            }

        };
    }

    private void hideEmpty() {
        ivEmpty.setVisibility(GONE);
    }

    private void showEmpty() {
        ivEmpty.setVisibility(VISIBLE);
    }

    public void uploadFile(File yunpanFile) {
        showLoading(1);
        ABCCallBack<FileUpLoadUpyResp> callback = new ABCCallBack<FileUpLoadUpyResp>() {
            @Override
            public void onSuccess(FileUpLoadUpyResp fileUpLoadUpyResp) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                refreshData();
                if (fileUpLoadUpyResp != null && fileUpLoadUpyResp.data != null) {
                    String url = fileUpLoadUpyResp.data.url;
                    ABCUtils.showToast(getContext(), getContext().getString(R.string.abc_upload_success) + url);
                }
            }

            /**
             * @param code
             * @param msg
             */
            @Override
            public void onError(int code, String msg) {

            }



            @Override
            public void onLoading(long totalBytesCount, long writtenBytesCount) {
                super.onLoading(totalBytesCount, writtenBytesCount);
                float percent = writtenBytesCount / (float) totalBytesCount;
                if (progressDialog != null) {
                    progressDialog.setProgress((int) (percent * 100));
                }
            }

        };
        ABCLiveSDK.getInstance(getContext()).getApiServer().uploadCloud(yunpanFile, callback);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPdfName;
        TextView tvPdfSize;
        TextView tvPdfTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPdfName = (TextView) itemView.findViewById(R.id.tv_pdf_name);
            tvPdfSize = (TextView) itemView.findViewById(R.id.tv_pdf_size);
            tvPdfTime = (TextView) itemView.findViewById(R.id.tv_pdf_time);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myAdapter.selectPosition != getLayoutPosition()) {
                        tvRight.setTag(getLayoutPosition());
                        tvRight.setText(getContext().getString(R.string.abc_share));
                        myAdapter.selectPosition = getLayoutPosition();
                    } else {
                        tvRight.setTag(-1);
                        tvRight.setText(getContext().getString(R.string.abc_cancel));
                        myAdapter.selectPosition = -1;
                    }
                    myAdapter.notifyDataSetChanged();
                }
            });
        }


        public void bindView(YunPanFileMo yunPanFile) {
            tvPdfName.setText(!TextUtils.isEmpty(yunPanFile.fileName) ? yunPanFile.fileName : "");
            tvPdfSize.setText(!TextUtils.isEmpty(yunPanFile.size) ? yunPanFile.size : "");
            tvPdfTime.setText(!TextUtils.isEmpty(yunPanFile.lastModifyTime) ? yunPanFile.lastModifyTime : "");

        }
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<YunPanFileMo> datas = new ArrayList<>();
        public int selectPosition = -1;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.abc_item_yunpan, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindView(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public void clearItems() {
            datas.clear();
            notifyDataSetChanged();
        }

        public void addItemList(@NonNull List<YunPanFileMo> list) {
            datas.addAll(list);
            notifyDataSetChanged();
        }

        public List<YunPanFileMo> getDatas() {
            return datas;
        }

        public YunPanFileMo getItem(int position) {
            return datas.get(position);
        }
    }


}
