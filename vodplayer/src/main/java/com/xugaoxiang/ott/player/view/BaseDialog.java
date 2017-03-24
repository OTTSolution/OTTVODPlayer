package com.xugaoxiang.ott.player.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.xugaoxiang.ott.player.R;

/**
 * Created by user on 2016/9/28.
 */
public abstract class BaseDialog extends AlertDialog implements View.OnClickListener{

    protected BaseDialog(Context context) {
        super(context , R.style.BaseDialog);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    public abstract void initView();
    public void initData(){};
    public void initListener(){}
    public void processClick(View view){}

    @Override
    public void onClick(View v) {
        processClick(v);
    }

}
