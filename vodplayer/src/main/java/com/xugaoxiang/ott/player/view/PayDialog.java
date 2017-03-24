package com.xugaoxiang.ott.player.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xugaoxiang.ott.player.R;

/**
 * Created by user on 2016/9/28.
 */
public class PayDialog extends BaseDialog {

    private String title;
    private String msg;
    private String confirm;
    private String cancle;
    TextView dialogTitle;
    TextView dialogMsg;
    TextView tvConfirm;
    TextView tvCancel;
    private DialogOnClickListerne listerne;
    public PayDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_pay);
        dialogTitle = (TextView) findViewById(R.id.dialog_title);
        dialogMsg = (TextView) findViewById(R.id.dialog_msg);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
    }

    @Override
    public void initData() {
        setDialogTitle(title);
        setDialogMsg(msg);
        tvConfirm.setText(confirm);
        tvCancel.setText(cancle);
    }

    @Override
    public void initListener() {
        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    public void setTitle(String title){
        this.title = title;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public void setConfirm(String confirm){ this.confirm = confirm;}
    public void setCancle(String cancle){ this.cancle = cancle;}
    public void setClickListener(DialogOnClickListerne listerne){
        this.listerne = listerne;
    }

    private void setDialogTitle(String title){
        dialogTitle.setText(title);
    }
    private void setDialogMsg(String msg){
        dialogMsg.setText(msg);
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.tv_confirm:
                if (listerne != null){
                    listerne.confirm();
                }
                break;
            case R.id.tv_cancel:
                if (listerne != null){
                    listerne.cancle();
                    this.dismiss();
                }
                break;
        }
    }

    public interface DialogOnClickListerne{
        public void confirm();
        public void cancle();
    }
}
