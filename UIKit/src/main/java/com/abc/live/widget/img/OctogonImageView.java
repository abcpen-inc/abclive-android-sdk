package com.abc.live.widget.img;

import android.content.Context;
import android.util.AttributeSet;

import com.abc.live.R;
import com.abc.live.widget.img.shader.ShaderHelper;
import com.abc.live.widget.img.shader.SvgShader;


public class OctogonImageView extends ShaderImageView {

    public OctogonImageView(Context context) {
        super(context);
    }

    public OctogonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OctogonImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_octogon);
    }
}
