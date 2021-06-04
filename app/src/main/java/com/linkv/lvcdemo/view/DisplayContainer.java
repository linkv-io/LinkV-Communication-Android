package com.linkv.lvcdemo.view;

import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayContainer {

    private NineItemLayout nineItemLayout;
    private ViewGroup layout;
    private TextView tv_resolution;
    private TextView tv_fps;
    private TextView tv_backslash;
    private View closeView;
    private String uid;
    private View iv_head;
    private View iv_talk;
    private int index = -1;

    public NineItemLayout getNineItemLayout() {
        return nineItemLayout;
    }

    public DisplayContainer setNineItemLayout(NineItemLayout nineItemLayout) {
        this.nineItemLayout = nineItemLayout;
        return this;
    }

    public ViewGroup getLayout() {
        return layout;
    }

    public DisplayContainer setLayout(ViewGroup layout) {
        this.layout = layout;
        return this;
    }

    public TextView getResolutionView() {
        return tv_resolution;
    }

    public DisplayContainer setResolutionView(TextView tv_resolution) {
        this.tv_resolution = tv_resolution;
        return this;
    }

    public TextView getFpsView() {
        return tv_fps;
    }

    public DisplayContainer setFpsView(TextView tv) {
        this.tv_fps = tv;
        return this;
    }

    public View getCloseView() {
        return closeView;
    }

    public DisplayContainer setCloseView(View view) {
        this.closeView = view;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public DisplayContainer setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public View getHeadImage(){
        return iv_head;
    }

    public DisplayContainer setHeadImage(View view){
        this.iv_head = view;
        return this;
    }

    public View getTalkIcon(){
        return iv_talk;
    }

    public DisplayContainer setTalkIcon(View view){
        this.iv_talk = view;
        return this;
    }

    public View getBackslash(){
        return tv_backslash;
    }

    public DisplayContainer setBackslash(TextView view){
        this.tv_backslash = view;
        return this;
    }

    public DisplayContainer setIndex(int index){
        this.index = index;
        return this;
    }

    public int getIndex(){
        return index;
    }

    public SurfaceView getSurfaceView() {
        return nineItemLayout.getSurfaceView();
    }


    public void setSurfaceView(SurfaceView surfaceView) {
        nineItemLayout.setSurfaceView(surfaceView);
    }


}
