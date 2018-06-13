package com.abc.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.live.pdf.PDFView;
import com.abc.live.pdf.listener.OnLoadCompleteListener;
import com.abc.live.pdf.listener.OnPageChangeListener;
import com.abc.live.pdf.scroll.DefaultScrollHandle;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.util.ABCUtils;
import com.liveaa.livemeeting.sdk.util.YPDownLoadUtils;

import java.io.File;

/**
 * Created by zhaocheng on 2017/6/24.
 */

public class ABCPDFPreviewActivity extends Activity implements YPDownLoadUtils.OnDownLoadCallBack, OnPageChangeListener, OnLoadCompleteListener {

    private final static String INTENT_URL = "INTENT_URL";
    private final static String INTENT_TITLE = "INTENT_TITLE";
    private final static String INTENT_ORIENTATION = "intent_orientation";
    PDFView pdfView;
    ContentLoadingProgressBar progressbar;
    ImageView ivBack;
    TextView tvPdfTitle;


    private YPDownLoadUtils loadUtils;

    public static void startABCPDFPreviewActivity(Context context, String url, String title, boolean isLandscape) {
        Intent intent = new Intent(context, ABCPDFPreviewActivity.class);
        intent.putExtra(INTENT_URL, url);
        intent.putExtra(INTENT_ORIENTATION, isLandscape);
        intent.putExtra(INTENT_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isLandscape = getIntent().getBooleanExtra(INTENT_ORIENTATION, false);
        if (isLandscape)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.abc_pdf_perview);
        pdfView = (PDFView) findViewById(R.id.pdf_view);
        progressbar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvPdfTitle = (TextView) findViewById(R.id.tv_pdf_title);
        tvPdfTitle.setText(getIntent().getStringExtra(INTENT_TITLE));
        loadUtils = new YPDownLoadUtils(this, this);
        String url = getIntent().getStringExtra(INTENT_URL);
        progressbar.setProgress(0);
        progressbar.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressbar.show();
        if (url != null) {
            loadUtils.downLoadCheckCache(url);
        }

    }

    @Override
    public void onDownLoadSuccess(String uri, String path) {
        progressbar.hide();
        progressbar.setVisibility(View.GONE);
        pdfView.fromFile(new File(path))
                .defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .load();
    }

    @Override
    public void onDownLoadProgress(long bytesRead, long contentLength) {
        int percent = (int) (bytesRead / (float) contentLength * 100);
        progressbar.setProgress(percent);
    }

    @Override
    public void onDownLoadFail() {
        ABCLiveSDK.showToast(getString(R.string.abc_download_fail));
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    public void onBackClick(View view) {
        onBackPressed();
    }
}
