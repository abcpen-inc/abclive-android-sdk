package com.abc.live.widget.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.live.ABCPDFPreviewActivity;
import com.abc.live.R;
import com.abcpen.open.api.callback.ABCCallBack;
import com.abcpen.open.api.model.YunPanFileMo;
import com.abcpen.open.api.resp.YunPanGetFileListResp;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.util.YPDownLoadUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaocheng on 16/10/12.
 */

public class ABCYunPanListView extends ABCBaseRightAnimLayout implements YPDownLoadUtils.OnDownLoadCallBack, View.OnClickListener {
    public static final int REC_REQUESTCODE = 999;

    private RecyclerView recyclerView;
    private ImageView ivEmpty;
    private TextView tvRight;
    private ImageView ivBack;

    private MyAdapter myAdapter;
    private ABCCustomProgress loading;

    private ABCLansDialog abcLansUploadDialog, abcLansEditDialog;

    private ContentLoadingProgressBar progressbar;

    public void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivEmpty = (ImageView) findViewById(R.id.iv_empty);
        progressbar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);
        tvRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private ProgressDialog progressDialog;
    private YunPanListener yunPanListener = null;
    private YPDownLoadUtils downLoadUtils;
    private ABCCallBack<YunPanGetFileListResp> baseCallBack;
    private boolean downLoading = false;

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
        downLoading = false;
    }


    public interface YunPanListener {

        void onClose();

        void onSelectYunPanData(String uri, String path);

    }


    public ABCYunPanListView(Context context) {
        this(context, null);
    }

    public ABCYunPanListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ABCYunPanListView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        int i = view.getId();
        if (i == R.id.iv_back) {
            yunPanListener.onClose();
        } else if (i == R.id.tv_right) {
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


    private void showProgressDialog() {
        downLoading = true;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getContext().getString(R.string.abc_wait));
        progressDialog.setMessage(getContext().getString(R.string.abc_downloading));
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


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPdfName;
        TextView tvPdfSize;
        TextView tvPdfTime;
        ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPdfName = (TextView) itemView.findViewById(R.id.tv_pdf_name);
            tvPdfSize = (TextView) itemView.findViewById(R.id.tv_pdf_size);
            tvPdfTime = (TextView) itemView.findViewById(R.id.tv_pdf_time);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(getLayoutPosition());
                }
            });
        }


        public void bindView(YunPanFileMo yunPanFile) {

            if (yunPanFile.fileName.endsWith(".pdf")) {
                ivIcon.setImageResource(R.drawable.abc_ic_ls_pdf_default);
            } else if (yunPanFile.fileName.endsWith(".doc") || yunPanFile.fileName.endsWith(".docx")) {
                ivIcon.setImageResource(R.drawable.abc_ic_ls_word_default);
            } else if (yunPanFile.fileName.endsWith(".xls") || yunPanFile.fileName.endsWith(".xlsx")) {
                ivIcon.setImageResource(R.drawable.abc_ic_ls_excel_default);
            } else if (yunPanFile.fileName.endsWith(".ppt") || yunPanFile.fileName.endsWith(".pptx")) {
                ivIcon.setImageResource(R.drawable.abc_ic_ls_ppt_default);
            }

            tvPdfName.setText(!TextUtils.isEmpty(yunPanFile.fileName) ? yunPanFile.fileName : "");
            tvPdfSize.setText(!TextUtils.isEmpty(yunPanFile.size) ? yunPanFile.size : "");
            tvPdfTime.setText(!TextUtils.isEmpty(yunPanFile.lastModifyTime) ? yunPanFile.lastModifyTime : "");

        }
    }

    private void showEditDialog(final int dataPosition) {
        if (abcLansEditDialog == null) {
            abcLansEditDialog = new ABCLansDialog(getContext());
        }
        abcLansEditDialog.setOnItemClickListener(new ABCLansDialog.OnItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                switch (position) {
                    case 0:
                        // TODO: 2017/6/24 share
                        doShare(dataPosition);
                        break;
                    case 1:
                        // TODO: 2017/6/24 预览
                        ABCPDFPreviewActivity.startABCPDFPreviewActivity(getContext(), myAdapter.getItem(dataPosition).url, myAdapter.getItem(dataPosition).fileName, true);
                        break;
                    case 2:
                        final ABCEditDialog editDialog = new ABCEditDialog(getContext());
                        editDialog.setOnItemClickListener(new ABCEditDialog.OnItemClickListener() {
                            @Override
                            public void onConfirm(String data) {
                                doReName(dataPosition, data);
                                editDialog.dismiss();
                            }
                        });
                        editDialog.show();
                        break;
                    case 3:
                        new AlertDialog.Builder(getContext())
                                .setMessage(getContext().getString(R.string.abc_del_msg))
                                .setTitle(getContext().getString(R.string.abc_dialog_hint))
                                .setPositiveButton(R.string.abc_confirm_str, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doDel(dataPosition);
                                    }
                                }).setNegativeButton(R.string.abc_cancel_str, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                        break;
                }
                abcLansEditDialog.dismiss();
            }

            @Override
            public void onCreate() {
                abcLansEditDialog.addDataText(R.string.abc_share, R.string.abc_preview_str);
            }

            @Override
            public void onCancel() {

            }
        });
        abcLansEditDialog.dismiss();
        abcLansEditDialog.show();
    }


    private void showLoading() {
        progressbar.setVisibility(VISIBLE);
        progressbar.show();
    }

    private void hideLoading() {
        progressbar.setVisibility(GONE);
        progressbar.hide();
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<YunPanFileMo> datas = new ArrayList<>();

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


    private void doDel(int dataPosition) {
        showLoading();
//        ABCLiveSDK.getInstance(getContext()).delYunPan(myAdapter.getDatas().get(dataPosition).fileName, new ABCCallBack<BaseResponse>() {
//            @Override
//            public void onSuccess(BaseResponse baseResponse) {
//                hideLoading();
//                ABCUtils.showToast(getContext(), getContext().getString(R.string.abc_del_success));
//                refreshData();
//            }
//
//            @Override
//            public void onUserListError(int code) {
//                hideLoading();
//                ABCUtils.showToast(getContext(), getContext().getString(R.string.abc_del_fail));
//            }
//        });
    }

    private void doShare(int dataPosition) {
        if (downLoading) return;
        showProgressDialog();
        if (downLoadUtils == null) {
            downLoadUtils = new YPDownLoadUtils(getContext(), ABCYunPanListView.this);
        }
        downLoadUtils.downLoadCheckCache(myAdapter.getItem(dataPosition).url);
    }

    private void doReName(int dataPosition, String data) {
//        showLoading();
//        String fileName = myAdapter.getDatas().get(dataPosition).fileName;
//        int i = fileName.lastIndexOf(".");
//        if (i > -1) {
//            String substring = myAdapter.getDatas().get(dataPosition).fileName.substring(i, fileName.length());
//            data += substring;
//        }
//        ABCLiveSDK.getInstance(getContext()).reNameYunPan(myAdapter.getDatas().get(dataPosition).fileName, data, new ABCCallBack<BaseResponse>() {
//            @Override
//            public void onSuccess(BaseResponse baseResponse) {
//                hideLoading();
//                ABCUtils.showToast(getContext(), getContext().getString(R.string.abc_update_success));
//                refreshData();
//            }
//
//            @Override
//            public void onUserListError(int code) {
//                hideLoading();
//                ABCUtils.showToast(getContext(), getContext().getString(R.string.abc_update_error));
//            }
//        });
    }

}
