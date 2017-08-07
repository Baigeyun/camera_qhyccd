package com.starrysky.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starrysky.R;
import com.starrysky.activity.CameraViewportActivity;
import com.starrysky.dto.CameraDevice;
import com.starrysky.listener.OnScanResultItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ScanResultListAdapter extends RecyclerView.Adapter<ScanResultListAdapter.ScanResultListItemViewHolder> {
    private Context context;
    private List<CameraDevice> cameraDeviceList;

    /* listener */
    private OnScanResultItemClickListener onScanResultItemClickListener;

    public ScanResultListAdapter(Context context ,List<CameraDevice> cameraDeviceList) {
        this.context = context;
        this.cameraDeviceList = (cameraDeviceList != null ? cameraDeviceList : new ArrayList<CameraDevice>());
    }

    @Override
    public ScanResultListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_scan_result, viewGroup, false);
        return new ScanResultListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ScanResultListItemViewHolder viewHolder, int position) {
        CameraDevice cameraDevice = cameraDeviceList.get(position);

        viewHolder.leftText.setText(String.valueOf(cameraDevice.getVid()));
        viewHolder.rightText.setText(cameraDevice.getCameraName());
    }

    @Override
    public int getItemCount() {
        return (this.cameraDeviceList != null) ? this.cameraDeviceList.size() : 0;
    }

    public void setData(List<CameraDevice> cameraDeviceList){
        this.cameraDeviceList = (cameraDeviceList == null) ? new ArrayList<CameraDevice>(): cameraDeviceList;
        notifyDataSetChanged();
    }

    public void setOnScanResultItemClickListener(OnScanResultItemClickListener onScanResultItemClickListener) {
        this.onScanResultItemClickListener = onScanResultItemClickListener;
    }

    class ScanResultListItemViewHolder extends RecyclerView.ViewHolder {
        TextView leftText;
        TextView rightText;

        public ScanResultListItemViewHolder(View itemView) {
            super(itemView);
            this.leftText = (TextView) itemView.findViewById(R.id.leftText);
            this.rightText = (TextView) itemView.findViewById(R.id.rightText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( onScanResultItemClickListener != null ){
                        int position = getAdapterPosition();
                        CameraDevice cameraDevice = cameraDeviceList.get(position);

                        onScanResultItemClickListener.onClick(cameraDevice);
                    }

                }
            });
        }
    }
}
