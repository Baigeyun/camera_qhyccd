package com.starrysky.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import com.starrysky.R;
import com.starrysky.listener.OnSettingDialogOkBtnClick;

public class SettingDialogFragment extends DialogFragment {

    private Spinner analogGainSpinner;
    private Spinner digitalGainSpinner;
    private Spinner resolutionSpinner;
    private Spinner trafficeSpinner;
    private Spinner speedSpinner;
    private Spinner exposureTimeSpinner;

    private OnSettingDialogOkBtnClick onSettingDialogOkBtnClick;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_setting, null);

        resolutionSpinner = (Spinner) view.findViewById(R.id.resolutionSpinner);

        trafficeSpinner = (Spinner) view.findViewById(R.id.trafficeSpinner);
        speedSpinner = (Spinner) view.findViewById(R.id.speedSpinner);
        exposureTimeSpinner = (Spinner) view.findViewById(R.id.exposureTimeSpinner);
        digitalGainSpinner = (Spinner) view.findViewById(R.id.DigitalGainSpinner);
        analogGainSpinner = (Spinner) view.findViewById(R.id.AnalogGainSpinner);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if( onSettingDialogOkBtnClick != null ){
                            String resolution = resolutionSpinner.getSelectedItem().toString();

                            String traffice = trafficeSpinner.getSelectedItem().toString();
                            String speed = speedSpinner.getSelectedItem().toString();
                            String exposureTime = exposureTimeSpinner.getSelectedItem().toString();
                            String digitalgain = digitalGainSpinner.getSelectedItem().toString();
                            String analoggain = analogGainSpinner.getSelectedItem().toString();
                            onSettingDialogOkBtnClick.onClick(resolution,analoggain,digitalgain,traffice,speed,exposureTime);
                        }
                    }
                });
                /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                })*/


        return builder.create();

    }

    public void setOnSettingDialogOkBtnClick(OnSettingDialogOkBtnClick onSettingDialogOkBtnClick) {
        this.onSettingDialogOkBtnClick = onSettingDialogOkBtnClick;
    }
}
