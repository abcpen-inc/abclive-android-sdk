package com.abc.live;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.abcpen.core.define.ABCConstants;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveParams;

/**
 * Created by zhaocheng on 2017/5/16.
 */

public class ABCLiveUIParams implements Parcelable {

    public static final int DEFAULT_MAX_COUNT = 3;

    public boolean defaultOpenMic = true;

    public boolean defaultOpenCamera = false;

    public
    @DrawableRes
    int userDefaultIcon = R.drawable.abc_default_icon;

    public int soundMethod = ABCLiveParams.SPEAK_PHONE;


    public boolean isPlayLive;

    public String uid;
    public int roleType = ABCConstants.NONE_TYPE;
    public String avatarUrl;
    public boolean defaultOpenBeauty = true;
    public String nickName;
    public boolean isShowGuide = false;
    public boolean isManager = false;
    public long endTime;
    public String userExt;
    public long startTime;

    public ABCLiveUIParams() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.defaultOpenMic ? (byte) 1 : (byte) 0);
        dest.writeByte(this.defaultOpenCamera ? (byte) 1 : (byte) 0);
        dest.writeInt(this.userDefaultIcon);
        dest.writeInt(this.soundMethod);
        dest.writeByte(this.isPlayLive ? (byte) 1 : (byte) 0);
        dest.writeString(this.uid);
        dest.writeInt(this.roleType);
        dest.writeString(this.avatarUrl);
        dest.writeByte(this.defaultOpenBeauty ? (byte) 1 : (byte) 0);
        dest.writeString(this.nickName);
        dest.writeByte(this.isShowGuide ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isManager ? (byte) 1 : (byte) 0);
        dest.writeLong(this.endTime);
        dest.writeString(this.userExt);
        dest.writeLong(this.startTime);
    }

    protected ABCLiveUIParams(Parcel in) {
        this.defaultOpenMic = in.readByte() != 0;
        this.defaultOpenCamera = in.readByte() != 0;
        this.userDefaultIcon = in.readInt();
        this.soundMethod = in.readInt();
        this.isPlayLive = in.readByte() != 0;
        this.uid = in.readString();
        this.roleType = in.readInt();
        this.avatarUrl = in.readString();
        this.defaultOpenBeauty = in.readByte() != 0;
        this.nickName = in.readString();
        this.isShowGuide = in.readByte() != 0;
        this.isManager = in.readByte() != 0;
        this.endTime = in.readLong();
        this.userExt = in.readString();
        this.startTime = in.readLong();
    }

    public static final Creator<ABCLiveUIParams> CREATOR = new Creator<ABCLiveUIParams>() {
        @Override
        public ABCLiveUIParams createFromParcel(Parcel source) {
            return new ABCLiveUIParams(source);
        }

        @Override
        public ABCLiveUIParams[] newArray(int size) {
            return new ABCLiveUIParams[size];
        }
    };
}
