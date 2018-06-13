package com.abc.live.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.abc.live.ABCLiveUIConstants;
import com.abc.live.R;
import com.abc.live.ui.dati.ABCDatiDetailDialog;
import com.abc.live.ui.dati.ABCDatiStudentController;
import com.abc.live.ui.dati.ABCDatiTeacherDialog;
import com.abc.live.ui.dati.ABCStudentAnswer;
import com.abc.live.ui.dati.ABCTeacherDatiStatus;
import com.abc.live.ui.dati.ABCTeacherQuestionProgress;
import com.abc.live.ui.live.ABCPlayLiveActivity;
import com.abc.live.widget.common.ABCCommonDialog;
import com.abc.live.widget.common.ABCLivePopView;
import com.abc.live.widget.common.ABCYunPanListView;
import com.abcpen.core.event.room.resp.AnswerQuestionNotify;
import com.abcpen.core.event.room.resp.GetAnswerStatsRsp;
import com.abcpen.core.event.room.resp.NewQuestionCard;
import com.abcpen.sdk.pen.PenEventType;
import com.abcpen.sdk.pen.PenSDK;
import com.abcpen.sdk.pen.device.BluetoothLeDevice;
import com.abcpen.sdk.pen.equil.CalibrationPointActivity;
import com.abcpen.sdk.pen.equil.EquilSDK;
import com.abcpen.sdk.pen.listener.IPenDataListener;
import com.abcpen.sdk.pen.listener.IPenRegionListener;
import com.abcpen.sdk.pen.listener.IPenStateChange;
import com.abcpen.sdk.utils.PaperType;
import com.abcpen.sdk.utils.PenBleWindow;
import com.liveaa.livemeeting.sdk.Constants;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.biz.core.ABCWhiteboardFragment;
import org.abcpen.common.util.util.ALog;
import com.liveaa.livemeeting.sdk.util.ABCUtils;
import com.liveaa.livemeeting.sdk.wb.hub.WBInterface;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

;

/**
 * Created by zhaocheng on 2017/6/6.
 * 白板父类 处理白板逻辑
 */
public abstract class ABCBaseWhiteBoardActivity extends ABCLiveBaseActivity implements WBInterface {

    private static final String TOUCH_WIDTH = "touchWidth";
    private static final String TOUCH_HEIGHT = "touchHeight";

    protected static final int ACCESS_FINE_LOCATION_PERMISSION_CODE = 0x007;
    protected static final int WRITE_EXTERNAL_STORAGE_CODE = 0x008;
    private final static int OPEN_FILE = 0x999;
    private final static int REQUEST_CODE_BLE = 0x998;

    private ABCDatiDetailDialog mABCDatiDetailDialog = null;
    private ABCDatiTeacherDialog mABCDatiTeacherDialog = null;
    protected View mTeacherProgressView = null;
    protected View mStudentAnswerView = null;
    protected ABCTeacherQuestionProgress mABCTeacherProgress;
    protected ABCStudentAnswer mABCStudentAnswer = null;
    protected ABCCommonDialog mStopAnswerDialog = null;
    protected View whiteBoardView;


    /**
     * 记录老师出题状态
     */

    protected ABCTeacherDatiStatus mTeacherDatiStatus = new ABCTeacherDatiStatus();

    protected void resetTeacherDatiStatus(ABCTeacherDatiStatus status) {
        status.mType = ABCLiveUIConstants.TYPE_SINGLE_CHOICE;
        status.mSelectCount = 0;
        status.mCorrectAnswers = new boolean[]{false, false, false, false, false, false};
    }

    /**
     * 白板fragment
     */
    protected ABCWhiteboardFragment mWhiteboardFragment;
    /**
     * 云盘view
     */
    private ABCYunPanListView yunPanListView;
    private ABCLivePopView yunPanPopWindow;
    private PenBleWindow penBleWindow;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final float touchWidth = data.getFloatExtra(TOUCH_WIDTH, 0.0f);
                    final float touchHeight = data.getFloatExtra(TOUCH_HEIGHT, 0.0f);
                    PenSDK.getInstance().setLimitedPenRegion(touchWidth, touchHeight, new IPenRegionListener() {
                        @Override
                        public void setPenRegion(float topLeftX, float topLeftY, float bottomRightX, float bottomRightY) {
                            if (mWhiteboardFragment != null)
                                mWhiteboardFragment.setPenRegion(topLeftX, topLeftY, bottomRightX, bottomRightY);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void initFragment() {
        initWhiteBoardFragment();
        if (PenSDK.getInstance().getState() == PenSDK.STATE_CONNECTED) {
            openBleResult(true);
        } else {
            openBleResult(false);
        }

    }

    protected void changeWbScale() {
        if (isMatch) {
            onWhiteBoardSmall();
        } else {
            onWhiteBoardMatch();
        }
        int width = getResources().getDimensionPixelSize(R.dimen.abc_video_width);
        int hegiht = getResources().getDimensionPixelSize(R.dimen.abc_video_height);
        int margins = getResources().getDimensionPixelSize(R.dimen.abc_dp5);
        float tx = whiteBoardView.getWidth() / 2f - width / 2f - margins;
        float ty = whiteBoardView.getHeight() / 2f - hegiht / 2f - margins;

        PropertyValuesHolder scax, scay, tsx, tsy;
        if (!isMatch) {
            ViewCompat.setTranslationZ(findViewById(getWhiteBoardLayoutRes()), 0);

            scax = PropertyValuesHolder.ofFloat(View.SCALE_X, whiteBoardView.getScaleX(), 1f);
            scay = PropertyValuesHolder.ofFloat(View.SCALE_Y, whiteBoardView.getScaleY(), 1f);
            tsx = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, whiteBoardView.getTranslationX(), 1);
            tsy = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, whiteBoardView.getTranslationY(), 1);
        } else {
            ViewCompat.setTranslationZ(findViewById(getWhiteBoardLayoutRes()), 2);

            float v = (float) width / whiteBoardView.getWidth();
            float h = (float) hegiht / whiteBoardView.getHeight();
            scax = PropertyValuesHolder.ofFloat(View.SCALE_X, whiteBoardView.getScaleX(), v);
            scay = PropertyValuesHolder.ofFloat(View.SCALE_Y, whiteBoardView.getScaleY(), h);
            tsx = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, whiteBoardView.getTranslationX(), -tx);
            tsy = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, whiteBoardView.getTranslationY(), -ty);
        }

        ObjectAnimator.ofPropertyValuesHolder(whiteBoardView, scax, scay, tsx, tsy).setDuration(ABCPlayLiveActivity.ANIM_DURATION).start();

    }

    protected abstract void onWhiteBoardMatch();

    protected abstract void onWhiteBoardSmall();

    /**
     * 初始化白板页面
     */
    protected void initWhiteBoardFragment() {
        whiteBoardView = findViewById(getWhiteBoardLayoutRes());
        mWhiteboardFragment = ABCWhiteboardFragment.getInstance(roleType, PaperType.valOfWidth(this, mPaperType), PaperType.valOfHeight(this, mPaperType), this);
        PaperType.setPaperType(this, mPaperType, 0, 0);
        getFragmentManager().beginTransaction().replace(getWhiteBoardLayoutRes(), mWhiteboardFragment).commit();
    }

//    public void getScreenShot() {
//        if (mWhiteboardFragment != null) {
//            String cacheImageFile = FileCacheUtil.getCacheImageFile(this, String.valueOf(System.currentTimeMillis())).getAbsolutePath();
//            mWhiteboardFragment.getScreenShot(cacheImageFile, this);
//        }
//    }

    @Override
    public void onScreenShot(String path) {
        //call in main thread
    }

    /**
     * 初始化智能笔
     */
    protected void initHardwarePen() {
        if (isCanSDCardPer()) {
            PenSDK.getInstance().addOnPenListener(mPenStateChangeListener);
            PenSDK.getInstance().addOnPenListener(mPenDataListener);
            PenSDK.getInstance().addOnPenListener(penRegionListener);
            int state = PenSDK.getInstance().getState();
            changeBleSatte(state);
            PenSDK.getInstance().bind(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PenSDK.getInstance().unBind(this);
        PenSDK.getInstance().removeListener(mPenStateChangeListener);
        PenSDK.getInstance().removeListener(mPenDataListener);
        PenSDK.getInstance().removeListener(penRegionListener);
    }

    IPenRegionListener penRegionListener = new IPenRegionListener() {
        @Override
        public void setPenRegion(float topLeftX, float topLeftY, float bottomRightX, float bottomRightY) {
            if (mWhiteboardFragment != null) {
                mWhiteboardFragment.setPenRegion(topLeftX, topLeftY, bottomRightX, bottomRightY);
            }
        }
    };

    /**
     * 当前智能笔连接状态
     */
    private IPenStateChange mPenStateChangeListener = new IPenStateChange() {
        @Override
        public void onStateChange(int state) {
            changeBleSatte(state);
        }
    };

    private void changeBleSatte(int state) {
        if (penBleWindow != null) {
            penBleWindow.onStateChange(state);
        }
        if (state == PenSDK.STATE_CONNECTED) {
            openBleResult(true);
        } else {
            openBleResult(false);
        }
    }

    public abstract void openBleResult(boolean isOpenBlePen);


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE:
                initHardwarePen();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_PERMISSION_CODE:
                showBleSettingDialog();
                ABCLiveSDK.showToast(getString(R.string.abc_access_fine_location_permission_granted));
                break;
            case WRITE_EXTERNAL_STORAGE_CODE:
                showReadSDCardSettingDialog();
                ABCLiveSDK.showToast(getString(R.string.abc_external_storage_granted));
                break;
        }
    }


    private boolean getIsVertical() {
        return mPaperType == PaperType.PORTRAIT_A_5 || mPaperType == PaperType.PORTRAIT_16_9;
    }

    float prevX = -1;
    float prevY = -1;
    /**
     * 硬件笔数据回调接口
     */
    private IPenDataListener mPenDataListener = new IPenDataListener() {
        @Override
        public void onConnection(boolean isConnection) {
            if (isConnection) {
                if (PenSDK.getInstance().getState() == PenSDK.STATE_CONNECTED) {
                    setPenState(true);
                } else {
                    setPenState(false);
                }
            }
        }

        @Override
        public void onError(Exception e) {

        }

        /**
         *
         * @param type
         * @param px
         * @param py
         * @param calPress
         * @param notePageNo 本子的页数 如果为-1 代表本子 不支持页数
         */
        @Override
        public void onDataSet(final PenEventType type, final float px, final float py, final float calPress, int notePageNo) {
            if (mWhiteboardFragment == null) return;
            mBaseHandler.post(new Runnable() {
                @Override
                public void run() {
                    float x = px, y = py;
                    if (x > 0 && y > 0) {
                        switch (type) {
                            case PEN_DOWN:
                                mWhiteboardFragment.doMouseDown(x, y, calPress);
                                break;
                            case PEN_UP:
                                mWhiteboardFragment.doMouseUp(x, y, calPress);
                                break;
                            case PEN_MOVE:
                                mWhiteboardFragment.doMouseDragged(x, y, calPress);
                                break;
                            case PEN_HOVER:
                                mWhiteboardFragment.doHangDraw(x, y);
                                break;
                        }
                    }
                }
            });

        }

        @Override
        public void onResetTouchUp(int x, int y) {
            if (mWhiteboardFragment == null) return;
            mWhiteboardFragment.resetTouchUp(x, y);
        }

        @Override
        public void onScanResult(final List<BluetoothLeDevice> itemList) {
            mBaseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (penBleWindow != null)
                        penBleWindow.setScanResult(itemList);
                }
            });
        }

        @Override
        public void onScanClear() {
            mBaseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (penBleWindow != null)
                        penBleWindow.setScanResult(null);
                }
            });
        }

        @Override
        public void onScanAdd(final BluetoothLeDevice bluetoothLeDevice) {
            mBaseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (penBleWindow != null)
                        penBleWindow.addScanItem(bluetoothLeDevice);
                }
            });
        }
    };


    /**
     * 翻页
     *
     * @param b
     */
    protected void changeScreen(boolean b) {
        mWhiteboardFragment.changeScreen(b);
    }

    /**
     * 删除智能笔数据回调
     */
    @Override
    protected void onPause() {
        super.onPause();
        PenSDK.getInstance().removeListener(mPenDataListener);
        dismissAllDatiDialog();
    }

    /**
     * 添加智能笔数据回调
     */
    @Override
    protected void onResume() {
        super.onResume();
        PenSDK.getInstance().addOnPenListener(mPenDataListener);
    }


    public void openBlePen(boolean isOpen) {
        if (isOpen) {
            String[] pres = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (EasyPermissions.hasPermissions(this, pres)) {
                onBluetoothClick();
            } else {
                if (EasyPermissions.somePermissionDenied(this, pres)) {
                    showBleSettingDialog();
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.abc_access_fine_location_permission),
                            R.string.abc_allow, R.string.abc_deny, ACCESS_FINE_LOCATION_PERMISSION_CODE, pres);
                }
            }
        } else {
            PenSDK.getInstance().disconnectDevice();
        }

    }

    @Override
    public void onFragmentCreated() {
        ALog.d("onFragmentCreated");
        mAbcRoomSession = getRoomBridge();
        initHardwarePen();
        // TODO: 2017/6/6 以及是否启动编辑 默认可以编辑
    }

//    @Override
//    public void onPageChanged(int from, int to, int total, int timestamp) {
//        // TODO: 2017/6/6  页数更改
//    }


    /**
     * 清除当前页面-对话框
     */
    ABCCommonDialog cleanCommonDialog = null;

    protected void showCleanCurrentPageDialog() {
        if (cleanCommonDialog != null) cleanCommonDialog.dismiss();
        cleanCommonDialog = new
                ABCCommonDialog(this, 1, getString(R.string.abc_clean_wb),
                getString(R.string.abc_clean_wb_msg),
                getString(R.string.abc_cancel_str), getString(R.string.abc_del_str), new ABCCommonDialog.DialogListner() {
            @Override
            public void onConfirm() {
                mWhiteboardFragment.userClickClearScreen();
            }

            @Override
            public void onCancel() {
            }
        });
        cleanCommonDialog.show();
    }

    /**
     * 重置确认
     */
    ABCCommonDialog resetCommonDialog = null;

    protected void showResetPageDialog() {
        if (resetCommonDialog != null) resetCommonDialog.dismiss();
        resetCommonDialog = new ABCCommonDialog(this, 1, getString(R.string.abc_reset_str),
                getString(R.string.abc_reset_wb),
                getString(R.string.abc_cancel_str), getString(R.string.abc_confirm_str), new ABCCommonDialog.DialogListner() {
            @Override
            public void onConfirm() {
                mWhiteboardFragment.resetPDF();
            }

            @Override
            public void onCancel() {
                hideBottomUIMenu();
            }
        });
        resetCommonDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeStatusBar();
            }
        });
        resetCommonDialog.show();
    }


    public void loadPdf(final String localPath, final String urlPath) {
        if (isCanSDCardPer()) {
            if (TextUtils.isEmpty(localPath)) {
                ABCLiveSDK.showToast(getString(R.string.abc_not_find));
                return;
            }
            if (mWhiteboardFragment != null) {
                mWhiteboardFragment.loadLocalPdf(urlPath, localPath, new ABCWhiteboardFragment.OnPdfLoadCallBack() {
                    @Override
                    public void onPdfPageCount(int totalPage) {
                        mWhiteboardFragment.sendAddPDF(urlPath, mWhiteboardFragment.getGapRecTime(), totalPage);
                        onPageTxt(mWhiteboardFragment.getCurWBPage(), mWhiteboardFragment.getTotalWBPage());
                    }
                });
            }
        }
    }


    public boolean isCanSDCardPer() {
        String[] pres = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, pres)) {
            return true;
        } else {
            if (EasyPermissions.somePermissionDenied(this, pres)) {
                showReadSDCardSettingDialog();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.abc_permission_storage_code),
                        R.string.abc_allow, R.string.abc_deny, WRITE_EXTERNAL_STORAGE_CODE, pres);
            }
        }
        return false;
    }


    public void showYunPanListView() {
        if (yunPanListView == null) {
            yunPanListView = new ABCYunPanListView(this);
            yunPanListView.setYunPanListener(new ABCYunPanListView.YunPanListener() {
                @Override
                public void onClose() {
                    if (yunPanPopWindow.isShowing()) {
                        yunPanPopWindow.dismiss();
                    }
                }

                @Override
                public void onSelectYunPanData(String uri, String path) {
                    //loading 云盘
                    if (yunPanPopWindow.isShowing()) {
                        yunPanPopWindow.dismiss();
                    }
                    loadPdf(path, uri);
                }
            });
        }
        if (yunPanPopWindow == null) {
            yunPanPopWindow = new ABCLivePopView(this, yunPanListView, true);
        }
        yunPanPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeStatusBar();
            }
        });
        yunPanListView.refreshData();
        yunPanPopWindow.setClippingEnabled(false);
        yunPanPopWindow.showAtLocation(getContetView(), Gravity.LEFT, 0, 0);


    }


    public void refreshYunPanData() {
        if (yunPanListView != null) {
            yunPanListView.refreshData();
        }
    }

    protected void onBluetoothClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                ABCLiveSDK.showToast(getString(R.string.ble_pres));
                return;
            }

            BluetoothAdapter adapter = bluetoothManager.getAdapter();
            if (adapter == null) {
                ABCLiveSDK.showToast(getString(R.string.ble_pres));
                return;
            }

            if (!adapter.isEnabled()) {
                openBluetooth();
            } else {

                if (PenSDK.getInstance().getPenImpl() == EquilSDK.getInstance()) {
                    if (PenSDK.getInstance().getState() == PenSDK.STATE_DISCONNETED) {
                        ABCLiveSDK.showToast(getString(R.string.equil_ble));
                        return;
                    }
                    Intent intent = new Intent(this, CalibrationPointActivity.class);
                    intent.putExtra(CalibrationPointActivity.INTENT_ORIENTATION, false);
                    startActivityForResult(intent, REQUEST_CODE_BLE);
                    openBleResult(true);
                } else if (PenSDK.getInstance().getState() == PenSDK.STATE_DISCONNETED) {
                    if (penBleWindow == null) {
                        penBleWindow = new PenBleWindow(this, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        penBleWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                changeStatusBar();
                            }
                        });
                        penBleWindow.setClippingEnabled(false);
                    }
                    penBleWindow.showAtLocation(getContetView(), Gravity.CENTER, 0, 0);
                } else if (PenSDK.getInstance().getState() == PenSDK.STATE_CONNECTED) {
                    disconnectPen();
                }
            }
        }
    }

    /**
     * 断开蓝牙笔
     */
    protected void disconnectPen() {
        if (PenSDK.getInstance().getState() == PenSDK.STATE_CONNECTED) {
            ABCCommonDialog yesNoDialog = new ABCCommonDialog(this, 1, getString(R.string.disconnect_device),
                    "取消", getString(R.string.COMMON_OK), new ABCCommonDialog.DialogListner() {
                @Override
                public void onConfirm() {
                    PenSDK.getInstance().disconnectDevice();
                }

                @Override
                public void onCancel() {

                }
            });
            yesNoDialog.show();
        }
    }


    protected void setPenState(boolean isOpen) {

    }

    /**
     * 显示答题统计结果
     */
    protected void showDatiDetailDialog(GetAnswerStatsRsp get_answer_stats_rsp, ABCTeacherDatiStatus status) {

        dismissAllDatiDialog();

        if (get_answer_stats_rsp == null)
            return;
        if (status == null || status.mCorrectAnswers == null) return;

        final int type = mTeacherDatiStatus.mType;

        final int selectCount = mTeacherDatiStatus.mSelectCount;

        ArrayList<String> chartOptions = new ArrayList<>();
        ArrayList<Integer> chartOptionsNum = new ArrayList<>();
        ArrayList<String> chartCorrect = new ArrayList<>();
        ArrayList<String> gridName = new ArrayList<>();
        ArrayList<String> gridOptions = new ArrayList<>();
        ArrayList<String> gridDuration = new ArrayList<>();

        if (type == ABCLiveUIConstants.TYPE_YESNO_CHOICE) {
            chartOptions.add(getResources().getString(R.string.abc_right));
            chartOptions.add(getResources().getString(R.string.abc_wrong));

            chartOptionsNum.add(get_answer_stats_rsp.counta);
            chartOptionsNum.add(get_answer_stats_rsp.countb);

            for (int i = 0; i < status.mCorrectAnswers.length; i++) {
                if (status.mCorrectAnswers[i]) {
                    chartCorrect.add(ABCUtils.intToABC(i + 7));
                }
            }

            for (GetAnswerStatsRsp.Details detail : get_answer_stats_rsp.details) {
                gridName.add(detail.getUsername());
                gridOptions.add(ABCUtils.numToWrongOrRight(detail.getAnswers()));
                gridDuration.add(ABCUtils.getTimeFormat(detail.timecost));
            }
        } else {
            chartOptions.addAll(ABCUtils.getListByCount(selectCount));
            for (int i = 0; i < selectCount; i++) {
                if (i == 0) {
                    chartOptionsNum.add(get_answer_stats_rsp.counta);
                } else if (i == 1) {
                    chartOptionsNum.add(get_answer_stats_rsp.countb);
                } else if (i == 2) {
                    chartOptionsNum.add(get_answer_stats_rsp.countc);
                } else if (i == 3) {
                    chartOptionsNum.add(get_answer_stats_rsp.countd);
                } else if (i == 4) {
                    chartOptionsNum.add(get_answer_stats_rsp.counte);
                } else if (i == 5) {
                    chartOptionsNum.add(get_answer_stats_rsp.countf);
                }
            }

            for (int i = 0; i < status.mCorrectAnswers.length; i++) {
                if (status.mCorrectAnswers[i]) {
                    chartCorrect.add(ABCUtils.intToABC(i + 1));
                }
            }

            for (GetAnswerStatsRsp.Details detail : get_answer_stats_rsp.details) {
                gridName.add(detail.getUsername());
                gridOptions.add(ABCUtils.numToABC(detail.getAnswers()));
                gridDuration.add(ABCUtils.getTimeFormat(detail.timecost));
            }
        }

        mABCDatiDetailDialog = new ABCDatiDetailDialog(this, chartOptions, chartOptionsNum, chartCorrect,
                gridName, gridOptions, gridDuration, get_answer_stats_rsp.totalcount,
                new ABCDatiDetailDialog.DialogListner() {
                    @Override
                    public void onConfirm() {

                    }

                    @Override
                    public void onCancel() {

                    }
                }
        );
        mABCDatiDetailDialog.setCancelable(true);
        mABCDatiDetailDialog.show();
        //应用区域
        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        Window dialogWindow = mABCDatiDetailDialog.getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.height = outRect1.height();
        p.width = outRect1.width();
        dialogWindow.setAttributes(p);
    }

    /**
     * 出题
     */

    protected void showDatiTeacherDialog() {
        dismissAllDatiDialog();
        resetTeacherDatiStatus(mTeacherDatiStatus);
        mABCDatiTeacherDialog = new ABCDatiTeacherDialog(ABCBaseWhiteBoardActivity.this,
                new ABCDatiTeacherDialog.DialogListner() {
                    @Override
                    public void onConfirm(final int type, final int selectCount,
                                          final boolean[] correctAnswers, final String result) {
                        mTeacherDatiStatus.mType = type;
                        mTeacherDatiStatus.mCorrectAnswers = correctAnswers;
                        mTeacherDatiStatus.mSelectCount = selectCount;
                        /**老师发送答题正确选项**/
                        mAbcRoomSession.sendDispathQuestionCardReq(type, selectCount, result);
                    }

                    @Override
                    public void onCancel() {
                        ABCUtils.showToast(ABCBaseWhiteBoardActivity.this,
                                getResources().getString(R.string.abc_select_corret_answer));
                    }
                }
        );

        mABCDatiTeacherDialog.setCancelable(true);
        mABCDatiTeacherDialog.show();

        Window dialogWindow = mABCDatiTeacherDialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.5f);
        p.height = (int) (d.getHeight() * 0.8f);
        dialogWindow.setAttributes(p);
    }

    /**
     * 取消所有dialog
     */
    protected void dismissAllDatiDialog() {
        if (mTeacherProgressView != null) {
            mTeacherProgressView.setVisibility(View.GONE);
        }

        if (mStudentAnswerView != null) {
            mStudentAnswerView.setVisibility(View.GONE);
        }
        dismissDatiDetailDialog();
        dismissDatiTeacherDialog();
        dismissDatiStopAnswerHintDialog();
    }

    private void dismissDatiStopAnswerHintDialog() {
        if (mStopAnswerDialog != null && mStopAnswerDialog.isShowing()) {
            mStopAnswerDialog.dismiss();
        }
    }


    protected void dismissDatiTeacherDialog() {
        if (mABCDatiTeacherDialog != null && mABCDatiTeacherDialog.isShowing()) {
            mABCDatiTeacherDialog.dismiss();
        }
    }

    /**
     * 取消统计答题结果
     */
    protected void dismissDatiDetailDialog() {
        if (mABCDatiDetailDialog != null && mABCDatiDetailDialog.isShowing()) {
            mABCDatiDetailDialog.dismiss();
        }
    }

    /**
     * 显示答题进度页面
     *
     * @param answer_question_notify
     */
    protected void showTeacherProgress(AnswerQuestionNotify answer_question_notify) {
//        ALog.e("showTeacherProgress", "showTeacherProgress");
        dismissAllDatiDialog();
        if (mTeacherProgressView != null) {
            mTeacherProgressView.setVisibility(View.VISIBLE);
        }
        if (mABCTeacherProgress == null && mTeacherProgressView != null)
            mABCTeacherProgress = new ABCTeacherQuestionProgress(mTeacherProgressView, this);
        if (mABCTeacherProgress != null) {
            if (answer_question_notify == null) {
                mABCTeacherProgress.udpateParams(mTeacherDatiStatus, null);
            } else {
//            ALog.e("showTeacherPorgress", "answer" + JSONObject(answer_question_notify));
                mABCTeacherProgress.udpateParams(mTeacherDatiStatus, answer_question_notify);
            }
        }
    }

    protected void showStudentAnswer(final NewQuestionCard new_question_card) {
//        ALog.e("showStudentAnswer", "showStudentAnswer");

        dismissAllDatiDialog();
        if (mStudentAnswerView != null) {
            mStudentAnswerView.setVisibility(View.VISIBLE);
        }
        if (mABCStudentAnswer == null && mStudentAnswerView != null)
            mABCStudentAnswer = new ABCStudentAnswer(mStudentAnswerView, this);
        if (mABCStudentAnswer != null) {
            mABCStudentAnswer.udpateParams(new_question_card, new ABCDatiStudentController.OnSubmitListener() {
                @Override
                public void onSubmit(int type, String result) {
//                ALog.e("sendAnswer", " type " + type + " result " + result);
                    if (TextUtils.isEmpty(result)) {
                        ABCUtils.showToast(ABCBaseWhiteBoardActivity.this, "请选择答案!");
                    } else {
                        mAbcRoomSession.sendStudentAnswer(type, result, new_question_card.seq);
                        mStudentAnswerView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    protected void showStopAnswerDialog() {
        mStopAnswerDialog = new
                ABCCommonDialog(this, 1, "",
                getString(R.string.abc_stop_answer_hint),
                getString(R.string.abc_cancel_str), getString(R.string.abc_common_ok), new ABCCommonDialog.DialogListner() {
            @Override
            public void onConfirm() {
//                ALog.e("onConfirm","onConfirm");
//                mAbcRoomSession.sendDatiDetailReq();
                mAbcRoomSession.sendStopAnswer();
                onStopAnswerDialog();
            }

            @Override
            public void onCancel() {

            }
        });
        if (!mStopAnswerDialog.isShowing())
            mStopAnswerDialog.show();
    }


    @Override
    public void onWbShared(String uid, boolean isInit) {

    }

    @Override
    public void onWBUnShared(String uid) {

    }

    @Override
    public void onWatchChange(String uid) {

    }

    /**
     * ABCLiveBaseActivity.java
     */
    protected void onDismissDatiDialog() {
        dismissAllDatiDialog();
    }

    protected abstract void onStopAnswerDialog();

    //    /**
//     * 页数显示
//     *
//     * @param currentPage
//     * @param totalPage
//     */
//    @Override
//    public void onPageTxt(int currentPage, int totalPage) {
//
//    }
    protected abstract
    @IdRes
    int getWhiteBoardLayoutRes();


    protected abstract void changeStatusBar();

    @Override
    public void onLog(int errcode, final String... result) {
        if (errcode == Constants.ERROR_LOAD_PDF || errcode == Constants.ERROR_LOAD_PDF) {
            ABCUtils.showToast(this, "PDF解析错误");
        }

        StringBuilder sb = new StringBuilder();
        for (String str : result) {
            if (str != null) {
                sb.append(str);
                sb.append("--");
            }
        }
    }

}
