package com.abc.live.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.live.ABCLiveUIParams;
import com.abc.live.ABCWeakReferenceHandler;
import com.abc.live.R;
import com.abc.live.util.ABCFileCacheUtil;
import com.abc.live.util.ABCSystemBarTintManager;
import com.abc.live.widget.common.ABCCommonDialog;
import com.abc.live.widget.common.ABCGuideHelper;
import com.abc.live.widget.common.ABCLansDialog;
import com.abc.live.widget.common.ABCLiveControllerView;
import com.abc.live.widget.common.ABCSendMsgView;
import com.abc.live.widget.common.ABCYunPanListView;
import com.abc.live.widget.common.KeyBoardWindow;
import com.abcpen.core.control.ABCRoomSession;
import com.abcpen.core.define.ABCConstants;
import com.abcpen.core.listener.pub.ABCLiveMsgListener;
import com.abcpen.core.listener.pub.ABCLivingStatusListener;
import com.abcpen.open.api.model.RoomMo;
import com.abcpen.pdf.PDFPlugin;
import com.abcpen.sdk.utils.BluetoothUtils;
import com.abcpen.sdk.utils.PaperType;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.liveaa.livemeeting.sdk.ABCErrorCode;
import com.liveaa.livemeeting.sdk.annotation.StatusCode;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.biz.core.PrefUtils;
import com.abcpen.open.api.model.ABCUserMo;
import com.liveaa.livemeeting.sdk.model.ImMsgMo;
import com.abcpen.open.api.model.UserMo;
import com.liveaa.livemeeting.sdk.util.BitmapUtil;
import com.liveaa.livemeeting.sdk.util.GetPathFromUri4kitkat;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by zhaocheng on 2017/5/17.
 */

public abstract class ABCLiveBaseActivity extends FragmentActivity implements
        ABCLiveMsgListener, ABCLivingStatusListener, ABCLiveControllerView.OnControllerItemClickListener,
        EasyPermissions.PermissionCallbacks, ABCSendMsgView.OnABCSendMsgListener {
    public static final String INTENT_EXTRA_ROOM = "room";
    public static final String INTENT_EXTRA_UI_PARAMS = "live_ui_params";
    private static final int CAMERA_PERMISSION_CODE = 0x0001;
    private static final int AUDIO_PERMISSION_CODE = 0x0002;
    static final int REQUEST_GALLERY = 0x003011;
    static final int REQUEST_GALLERY_FILE = 0x003012;
    static final int REQUEST_TAKE_PHOTO = 0x003010;
    static final int RESIZE_OK = 100;
    private static int REQUEST_BLUETOOTH_SETTING = 0x003013;

    protected RoomMo mRoomMo;
    protected PaperType mPaperType = PaperType.LANDSCAPE_16_9;
    protected ABCLiveUIParams mUIParams;
    protected int roleType;
    private Uri photoURI = null;

    //views ===================
    private FrameLayout mFameLayout;
    private ViewStub viewPassword;
    private TextView tvReconnectingView;
    private KProgressHUD loadingView;
    private ImageView loadingImg;
    protected boolean isCameraClick = false;
    protected boolean isAudioClick = false;
    protected FrameLayout frameMaxViewLayout = null;
    protected ABCGuideHelper guideHelper;
    BluetoothUtils mBluetoothUtils;
    protected ABCUserMo mSocketUserMo;
    protected RelativeLayout rlNetError;

    protected KeyBoardWindow inputMsgWindow;

    protected ABCRoomSession mAbcRoomSession;
    protected ABCWeakReferenceHandler mBaseHandler = null;
    protected ABCSystemBarTintManager tintManager;
    protected String token;


    @Override
    public void onCMDMsg(ABCUserMo uid, String data) {

    }

    @Override
    public void onCMDToUserMsg(ABCUserMo uid, ABCUserMo tuid, String data) {

    }

    @Override
    public void onChangeWbClick() {

    }

    private void initTinBar() {
        tintManager = new ABCSystemBarTintManager();
        tintManager.showStatusBar(this);
        tintManager.setStatusBarTintEnabled(ABCLiveSDK.IS_SHOW_TIN_BAR);
        tintManager.setNavigationBarTintEnabled(false);
        tintManager.setTintColor(R.color.abc_new_b2_80);
        tintManager.getStatusBarView().getBackground().setAlpha(225);
    }


    protected abstract void handlerActivityMessage(Message msg);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.abc_base_live);
        initTinBar();
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flag);
        ABCLiveSDK.getInstance(this).registerPDFPlugin(PDFPlugin.class);
        mBaseHandler = new ABCWeakReferenceHandler(this) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!isFinishing()) {
                    if (msg.what == RESIZE_OK) {
                        onTakeResult((String) msg.obj);
                    } else {
                        handlerActivityMessage(msg);
                    }
                }

            }
        };


        mRoomMo = getIntent().getParcelableExtra(INTENT_EXTRA_ROOM);
        mUIParams = getIntent().getParcelableExtra(INTENT_EXTRA_UI_PARAMS);
        roleType = mUIParams.roleType;
        mFameLayout = (FrameLayout) findViewById(R.id.fm_content_view);
        viewPassword = (ViewStub) findViewById(R.id.view_password);
        tvReconnectingView = (TextView) findViewById(R.id.tv_reconnecting_view);
        rlNetError = (RelativeLayout) findViewById(R.id.rl_net_error);
        mBluetoothUtils = new BluetoothUtils(this);

    }

    protected int getFrameMaxLayoutRes() {
        return 0;
    }

    protected boolean isMatch = false;
    protected boolean videoAnimIsLock = false;
    private ViewGroup lastParent;


    // video anim

    protected void initVideoSize(View view) {
        int vw = getResources().getDimensionPixelSize(R.dimen.abc_video_width);
        int vh = getResources().getDimensionPixelSize(R.dimen.abc_video_height);
        float vpx = view.getMeasuredWidth() / 2f - vw / 2f;
        float vpy = view.getMeasuredHeight() / 2f - vh / 2f;
        ObjectAnimator.ofFloat(view, View.SCALE_X, 1, vw / (float) view.getMeasuredWidth()).setDuration(0).start();
        ObjectAnimator.ofFloat(view, View.SCALE_Y, 1, vh / (float) view.getMeasuredHeight()).setDuration(0).start();
        ObjectAnimator.ofFloat(view, View.TRANSLATION_X, view.getTranslationX(), -vpx + getVideoLeftPadding()).setDuration(0).start();
        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getTranslationY(), -vpy + getVideoTopPadding()).setDuration(0).start();
    }


    public int getVideoTopPadding() {
        return 0;

    }


    public int getVideoLeftPadding() {
        return 0;
    }

    protected String matchUid;

    public void changeToMatchView(String uid, final View view) {
        frameMaxViewLayout = (FrameLayout) mFameLayout.findViewById(getFrameMaxLayoutRes());
        if (!isMatch) {
            lastParent = (ViewGroup) view.getParent();
            ((ViewGroup) view.getParent()).removeView(view);
            frameMaxViewLayout.setVisibility(View.VISIBLE);
            frameMaxViewLayout.addView(view);
            matchUid = uid;
            isMatch = true;
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
            frameMaxViewLayout.setVisibility(View.GONE);
            lastParent.addView(view);
            lastParent.setVisibility(View.VISIBLE);
            matchUid = "";
            isMatch = false;
        }
    }


    protected void changeToMatchStatusBar(boolean isMatch) {
        if (isMatch) {
            int flag = View.SYSTEM_UI_FLAG_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(flag);
        } else {
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(flag);
        }

    }

    private void init() {
        if (mRoomMo == null) {
            finish();
        }

        initViews();
        initFragment();
        if (mUIParams.isShowGuide) {
            mFameLayout.post(new Runnable() {
                @Override
                public void run() {
                    showGuide();
                }
            });
        }

    }

    private void showGuide() {
        guideHelper = new ABCGuideHelper(this);
        ABCGuideHelper.TipData[] tipDatas = getGuideHelper();
        if (tipDatas != null && tipDatas.length > 0) {
            for (ABCGuideHelper.TipData item : tipDatas) {
                guideHelper.addPage(item);
            }
            guideHelper.show(false);
        }
    }

    protected ABCGuideHelper.TipData[] getGuideHelper() {
        return null;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mFameLayout, true);
    }


    /**
     * 初始化manager
     *
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        init();
    }


    /**
     * loading 框
     */
    protected void showLoadingView() {
        if (loadingView != null)
            loadingView.dismiss();
        else
            loadingView = ABCLiveSDK.getDefaultLoadingDialog(this, true, "");

        loadingView.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingView = null;
        if (guideHelper != null) {
            guideHelper.release();
        }
    }

    /**
     * 隐藏loading框
     */
    protected void dismissLoadingView() {
        if (loadingView != null) {
            loadingView.dismiss();
        }
    }

    /**
     * 白板服务器无法连接dialog
     */
    private ABCCommonDialog disConnectDialog;

    protected void showWbDisConnectDialog() {
        if (disConnectDialog == null) {
            disConnectDialog = new ABCCommonDialog(this, 1
                    , getString(R.string.abc_dialog_hint), getString(R.string.abc_connect_error),
                    getString(R.string.abc_exit), getString(R.string.abc_try_again), new ABCCommonDialog.DialogListner() {
                @Override
                public void onConfirm() {
                    if (mAbcRoomSession != null) {
                        mAbcRoomSession.reConnectAll();
                    }

                }

                @Override
                public void onCancel() {
                    destroyData();
                }
            });

        }
        disConnectDialog.setCancelable(false);
        if (!disConnectDialog.isShowing()) {
            disConnectDialog.show();
        }

    }

    protected void setReconnectingViewVisibility(int visibility) {
        rlNetError.setVisibility(visibility);
        tvReconnectingView.setText(getString(R.string.abc_live_net_error));
    }


    ABCLansDialog lansDialog;

    //===========图片dialog
    protected void showPhotoDialog() {
        if (lansDialog != null) lansDialog.dismiss();
        lansDialog = new ABCLansDialog(this);
        lansDialog.setOnItemClickListener(new ABCLansDialog.OnItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                lansDialog.dismiss();
                if (position == 0) {
                    if (isCanOpenCamera(false)) {
                        dismissLocalCamera();
                        requestCamera();
                    }
                } else if (position == 1) {
                    dispatchGalleryPictureIntent();
                }
            }

            @Override
            public void onCreate() {
                lansDialog.addDataText(R.string.abc_to_camera, R.string.abc_to_album);
            }

            @Override
            public void onCancel() {

            }
        });
        lansDialog.show();

    }


    protected void dispatchGalleryPictureIntent() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_GALLERY_FILE);
            } catch (Exception e2) {
                Toast.makeText(this, R.string.abc_open_gallery_error, Toast.LENGTH_SHORT);
            }
        }

    }


    protected void requestCamera() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!path.exists()) {
            path.mkdirs();
        }

        String name = System.currentTimeMillis() + ".jpg";
        File file = new File(path, name);
        this.photoURI = Uri.fromFile(file);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        List resInfoList = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Uri uri = null;

        try {
            uri = FileProvider.getUriForFile(this,
                    getFileProviderName(),
                    file);
        } catch (Exception var10) {
            var10.printStackTrace();
            throw new RuntimeException("Please check IMKit Manifest FileProvider config.");
        }

        Iterator e = resInfoList.iterator();

        while (e.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo) e.next();
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.putExtra("output", uri);
        this.startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }


    protected void dispatchTakePictureIntent() {

        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = ABCFileCacheUtil.getCacheImageFile(this, "bs_" + System.currentTimeMillis());
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            getFileProviderName(),
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.abc_open_camera_error, Toast.LENGTH_SHORT);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (photoURI != null) {
                File fileWithUri = getFileWithUri(photoURI);
                if (fileWithUri != null) {
                    resizeImage(fileWithUri.getPath());
                }
            }
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            File fileWithUri = getFileWithUri(data.getData());
            if (fileWithUri != null) {
                resizeImage(fileWithUri.getPath());
            }
        } else if (requestCode == REQUEST_GALLERY_FILE && resultCode == Activity.RESULT_OK) {
            Uri photo = data.getData();
            if (photo != null) {
                final String filename = GetPathFromUri4kitkat.getPath(this, photo);
                File file = new File(filename);
                if (file.exists()) {
                    final String filePath = file.getAbsolutePath();
                    if (filePath.endsWith(".png") || filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
                        resizeImage(file.getAbsolutePath());
                    } else {
                        ABCLiveSDK.showToast(getString(R.string.abc_img_port));
                    }
                }
            }
        } else if (requestCode == ABCYunPanListView.REC_REQUESTCODE
                && resultCode == Activity.RESULT_OK &&
                data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                final String filename = GetPathFromUri4kitkat.getPath(this, uri);
                File file = new File(filename);
                if (file != null && file.exists()) {
                    final String filePath = file.getAbsolutePath();
                    if (filePath.endsWith(".pdf") || filePath.endsWith(".doc") ||
                            filePath.endsWith(".docx") || filePath.endsWith(".ppt") ||
                            filePath.endsWith(".pptx") || filePath.endsWith(".xls")
                            || filePath.endsWith(".xlsx")) {
                        uploadFile(file);
                    } else {
                        ABCLiveSDK.showToast(getString(R.string.abc_file_port));
                    }
                } else {
                    ABCLiveSDK.showToast(getString(R.string.abc_file_empty));
                }
            }
        }
    }

    protected abstract void uploadFile(File file);


    private void resizeImage(final String path) {
        showLoadingView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String outPath = ABCFileCacheUtil.getCacheImageFile(ABCLiveBaseActivity.this, String.valueOf(System.currentTimeMillis())).getPath();
                BitmapUtil.getResizeBitmapWithDegree(path, outPath);
                Message.obtain(mBaseHandler, RESIZE_OK, outPath).sendToTarget();
            }
        }).start();

    }


    protected abstract void onTakeResult(String path);

    public String getFileProviderName() {
        return getPackageName() + ".ABCFileProvider";
    }

    /**
     * 通过URI获取文件
     *
     * @param uri
     * @return
     */
    protected File getFileWithUri(Uri uri) {
        String picturePath = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,
                    filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (columnIndex >= 0) {
                picturePath = cursor.getString(columnIndex);  //获取照片路径
            } else if (TextUtils.equals(uri.getAuthority(), getFileProviderName())) {
                picturePath = parseOwnUri(uri);
            }
            cursor.close();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            picturePath = uri.getPath();
        }
        return TextUtils.isEmpty(picturePath) ? null : new File(picturePath);
    }


    public boolean isCanOpenCamera(boolean isClick) {
        String[] pres = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, pres)) {
            return true;
        } else {
            isCameraClick = isClick;
            if (EasyPermissions.somePermissionDenied(this, pres)) {
                showCameraSettingDialog();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.abc_permission_camera),
                        R.string.abc_allow, R.string.abc_deny, CAMERA_PERMISSION_CODE, pres);
            }
        }
        return false;
    }


    public boolean isCanOpenRecordAudio(boolean isClick) {
        String[] pres = {Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, pres)) {
            return true;
        } else {
            isAudioClick = isClick;
            if (EasyPermissions.somePermissionDenied(this, pres)) {
                showAudioSettingDialog();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.abc_permission_audio_recoding),
                        R.string.abc_allow, R.string.abc_deny, AUDIO_PERMISSION_CODE, pres);
            }
        }
        return false;
    }


    private void showCameraSettingDialog() {
        new AppSettingsDialog.Builder(this).setTitle(R.string.abc_dialog_hint)
                .setPositiveButton(R.string.abc_go_setting)
                .setRationale(R.string.abc_permission_camera)
                .setNegativeButton(R.string.abc_cancel).build().show();
    }

    private void showAudioSettingDialog() {
        new AppSettingsDialog.Builder(this).setTitle(R.string.abc_dialog_hint)
                .setPositiveButton(R.string.abc_go_setting)
                .setRationale(R.string.abc_permission_audio_recoding)
                .setNegativeButton(R.string.abc_cancel).build().show();
    }

    /**
     * 将TakePhoto 提供的Uri 解析出文件绝对路径
     *
     * @param uri
     * @return
     */
    protected String parseOwnUri(Uri uri) {
        if (uri == null) return null;
        String path;
        if (TextUtils.equals(uri.getAuthority(), getFileProviderName())) {
            path = new File(uri.getPath().replace("camera_photos/", "")).getAbsolutePath();
        } else {
            path = uri.getPath();
        }
        return path;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    protected void dismissLocalCamera() {

    }

    protected void showBleSettingDialog() {
        new AppSettingsDialog.Builder(this).setTitle(R.string.abc_dialog_hint)
                .setPositiveButton(R.string.abc_go_setting)
                .setRationale(R.string.abc_access_fine_location_permission_granted)
                .setNegativeButton(R.string.abc_cancel).build().show();
    }


    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * sdcard权限获取
     */
    protected void showReadSDCardSettingDialog() {
        new AppSettingsDialog.Builder(this).setTitle(R.string.abc_dialog_hint)
                .setPositiveButton(R.string.abc_go_setting)
                .setRationale(R.string.abc_external_storage_granted)
                .setNegativeButton(R.string.abc_cancel).build().show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    protected void showExitDialog() {
        ABCCommonDialog commonDialog = new ABCCommonDialog(this, 1,
                getString(R.string.abc_exit_str), getString(R.string.abc_cancel_str), getString(R.string.abc_confirm_str), new ABCCommonDialog.DialogListner() {
            @Override
            public void onConfirm() {
                destroyData();
            }

            @Override
            public void onCancel() {

            }
        });
        commonDialog.show();
    }


    /**
     * 是否能 聊天
     *
     * @param isEnable
     */
    public void sendEnableChat(boolean isEnable, String uid) {
        if (mAbcRoomSession != null) {
            mAbcRoomSession.sendEnableChat(isEnable, uid);
        }
    }

    /**
     * 是否能发言
     *
     * @param isEnable
     */
    public void sendEnableSpeak(boolean isEnable, String uid) {
        if (mAbcRoomSession != null) {
            mAbcRoomSession.sendEnableSpeak(isEnable, uid);
        }
    }

    /**
     * 邀请学生发言
     *
     * @param uid
     */
    public void sendInviteUser(String uid) {
        if (mAbcRoomSession != null) {
            mAbcRoomSession.sendInviteReqUser(uid);
        }
    }

    /**
     * 踢出人员
     *
     * @param
     */
    protected void sendKitOutUser(final String fid, final String tuid) {
//        ABCUserMo socketUserMo = userMos.containsForID(tuid);
//        if (socketUserMo != null) {
//            ABCCommonDialog dialog = new ABCCommonDialog(this, 1
//                    , getString(R.string.abc_kit_out_user, socketUserMo.uname),
//                    getString(R.string.abc_cancel), getString(R.string.abc_kit_out),
//                    new ABCCommonDialog.DialogListner() {
//                        @Override
//                        public void onConfirm() {
//                            if (mAbcRoomSession != null) {
//                                mAbcRoomSession.sendKitOutUser(fid, tuid);
//                            }
//                        }
//
//                        @Override
//                        public void onCancel() {
//
//                        }
//                    });
//            dialog.show();
//        }

    }



    protected void openBluetooth() {

        final boolean isBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        if (!isBluetoothLePresent) {
            ABCLiveSDK.showToast(getString(R.string.bluetooth_not_supported));
            return;
        }

        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivityForResult(intent, REQUEST_BLUETOOTH_SETTING);
    }


//    @Override
//    public void onReconnecting(int type, int count) {
//        setReconnectingViewVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onReconnected(int type) {
//        setReconnectingViewVisibility(View.GONE);
//    }


    /**
     * 房间主人(老师) 状态发生改变
     */
    @Override
    public void onHostStatusChange(ABCUserMo userMo, int status) {
        if (status == ABCConstants.HOST_LEAVE) {
            sendSystemMsg(getString(R.string.abc_host_leave));
        } else if (status == ABCConstants.HOST_NOT_IN) {
            ABCLiveSDK.showToast(R.string.abc_host_not_in);
        }
        onDismissDatiDialog();
    }

    protected abstract void sendSystemMsg(String string);

    protected abstract void onDismissDatiDialog();

    /**
     * 视频声音用户等状态回调
     *
     * @param code OPEN_CAMERA  打开相机回调
     *             CLOSE_CAMERA 关闭摄像头回调
     *             OPEN_MIC 打开麦克风回调
     *             CLOSE_MIC 关闭麦克风回调
     *             ON_KICKED_OUT 被踢出房间
     */
    @Override
    public void onStatusChange(@StatusCode int code) {
    }


    /**
     * @param error   LOGIN_OTHER_DEVICE  异地登录
     *                CONNECTION_FAIL 连接失败
     *                LOGIN_FAIL 登录失败
     *                CONNECTION_TIME_OUT 连接超时
     *                OPEN_CAMERA_FAIL 摄像头打开失败
     *                RECORDING_AUDIO_FAIL 声音打开失败
     *                OPEN_WB_SERVER_FAIL 白板服务器连接失败
     * @param objects type or errorMsg
     */
    @Override
    public void onError(int error, Object... objects) {

        switch (error) {
            case ABCErrorCode.LOGIN_OTHER_DEVICE:
                // TODO: 2017/5/11 异地登录
                destroyData();
                break;
            case ABCErrorCode.LOGIN_FAIL:
                // TODO: 2017/5/11 登录失败
                destroyData();
                break;
            case ABCErrorCode.CONNECTION_TIME_OUT:
                // TODO: 2017/5/11 连接超时
                break;
            case ABCErrorCode.OPEN_WB_SERVER_FAIL:
                // TODO: 2017/5/11 白板服务器连接失败
                break;

        }

    }

    protected void openAudioResult(boolean b) {

    }

    protected void openCameraResult(boolean b) {

    }


    /**
     * 接受到新消息 包含自己的消息
     *
     * @param imMsgMo imMsgMo
     */
    @Override
    public void onImMsgRec(ImMsgMo imMsgMo) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (isCameraClick) {
                    openCameraAgain();
                }
                break;
            case AUDIO_PERMISSION_CODE:
                if (isAudioClick) {
                    openAudioAgain();
                }
                break;
        }
    }

    protected void openAudioAgain() {

    }

    protected void openCameraAgain() {

    }

    ABCCommonDialog finishDialog;

    protected void showFinishDialog() {

        if (finishDialog != null) finishDialog.dismiss();
        finishDialog = new ABCCommonDialog(this, 2, getString(R.string.abc_room_close),
                getString(R.string.abc_time_confirm), new ABCCommonDialog.DialogListner() {
            @Override
            public void onConfirm() {

            }

            @Override
            public void onCancel() {

            }
        });
        finishDialog.setCancelable(true);
        finishDialog.show();
    }


    protected void showFinishDialogForOther() {

        if (finishDialog != null) finishDialog.dismiss();
        finishDialog = new ABCCommonDialog(this, 2, getString(R.string.abc_room_finish),
                getString(R.string.abc_time_confirm), new ABCCommonDialog.DialogListner() {
            @Override
            public void onConfirm() {

            }

            @Override
            public void onCancel() {

            }
        });
        finishDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onMeetingFinish();
            }
        });
        finishDialog.setCancelable(false);
        finishDialog.show();

    }

    protected abstract void onMeetingFinish();


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            switch (requestCode) {
                case CAMERA_PERMISSION_CODE:
                    showCameraSettingDialog();
                    openCameraResult(false);
                    ABCLiveSDK.showToast(getString(R.string.abc_permission_camera_granted));
                    break;
                case AUDIO_PERMISSION_CODE:
                    showAudioSettingDialog();
                    openAudioResult(false);
                    ABCLiveSDK.showToast(getString(R.string.abc_permission_audio_recoding_granted));
                    break;
            }
        }
    }

    @Override
    public void onSendMsg(String msg) {
        if (mSocketUserMo != null) {
            if (mSocketUserMo.forbidChatStatus != ABCConstants.DISABLE) {
                if (mAbcRoomSession != null) {
                    mAbcRoomSession.sendMessage(msg);
                }
            } else {
                ABCLiveSDK.showToast(R.string.abc_disable_chat);
            }
        }

        getInputMsgWindow().dismiss();
    }


    @Override
    public void onKeyBoardClick(View v) {
        if (mSocketUserMo != null) {
            if (mSocketUserMo.forbidChatStatus != ABCConstants.DISABLE) {
                showInputMsg();
            } else {
                ABCLiveSDK.showToast(R.string.abc_disable_chat);
            }
        }

    }

    protected void showInputMsg() {
        getInputMsgWindow().showAtLocation(mFameLayout, Gravity.BOTTOM, 0, 0);
    }


    public KeyBoardWindow getInputMsgWindow() {
        if (inputMsgWindow == null) {
            inputMsgWindow = new KeyBoardWindow(this);
            inputMsgWindow.setOnABCSendMsgListener(this);
        }
        return inputMsgWindow;
    }


    protected View getContetView() {
        return mFameLayout;
    }

    /**
     * 释放资源
     */
    protected abstract void destroyData();

    /**
     * 实例化manager
     *
     * @return
     */
    protected abstract ABCRoomSession getRoomBridge();

    protected abstract void initViews();

    protected abstract void initFragment();


    public void netError(View view) {
    }
}
