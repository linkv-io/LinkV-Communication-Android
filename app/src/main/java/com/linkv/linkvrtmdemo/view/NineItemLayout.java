package com.linkv.linkvrtmdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linkv.linkvrtmdemo.R;
import com.linkv.linkvrtmdemo.utils.LogUtils;


public class NineItemLayout extends FrameLayout {

    private FrameLayout fl_nine_surface_container;
    private View iv_head_icon;
    private View iv_talk;
    private View iv_close;
    private TextView tv_nine_resolution;
    private TextView tv_nine_fps;
    private TextView tv_backslash;
    private SurfaceView surfaceView;

    public NineItemLayout(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public NineItemLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public NineItemLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nine_live, this, true);
        fl_nine_surface_container = view.findViewById(R.id.fl_nine_surface_container);
        iv_head_icon = view.findViewById(R.id.iv_head_icon);
        iv_talk = view.findViewById(R.id.iv_talk);
        iv_close = view.findViewById(R.id.iv_close);
        tv_nine_resolution = view.findViewById(R.id.tv_nine_resolution);
        tv_nine_fps = view.findViewById(R.id.tv_nine_fps);
        tv_backslash = view.findViewById(R.id.tv_backslash);
        surfaceView = view.findViewById(R.id.surfaceView);
        LogUtils.d("NineItemLayout","findViewById");
    }

    public ViewGroup getSurfaceContainer() {
        return fl_nine_surface_container;
    }

    public View getHeadIcon() {
        return iv_head_icon;
    }

    public View getTalkIcon() {
        return iv_talk;
    }

    public View getCloseIcon() {
        return iv_close;
    }

    public TextView getResolutionView() {
        return tv_nine_resolution;
    }

    public TextView getFpsView() {
        return tv_nine_fps;
    }

    public TextView getBackslash() {
        return tv_backslash;
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }
}
