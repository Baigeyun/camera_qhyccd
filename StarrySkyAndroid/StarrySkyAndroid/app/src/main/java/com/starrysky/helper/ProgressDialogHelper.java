package com.starrysky.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import butterknife.ButterKnife;


public class ProgressDialogHelper {
    private ProgressDialog progressDialog;
    private ProgressDialogHelper(){}

    static class Holder{
        private static ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper();
    }

    public static ProgressDialogHelper getInstance(){
        return Holder.progressDialogHelper;
    }

    public void showProgressDialog(Context context,String message){
        ButterKnife.bind(this,(Activity)context);
        if(progressDialog == null ){
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();


    }

    public void hideProcessDialog(){
        if( progressDialog != null ){
            progressDialog.hide();
        }
    }

    public void setMessage(String message){
        progressDialog.setMessage(message);
    }
}
