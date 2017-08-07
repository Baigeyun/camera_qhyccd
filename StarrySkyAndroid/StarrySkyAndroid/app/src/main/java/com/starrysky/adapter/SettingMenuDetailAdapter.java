package com.starrysky.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.starrysky.R;
import com.starrysky.dto.SettingMenuDetailItem;

import java.util.List;

public class SettingMenuDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<SettingMenuDetailItem> datas;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(SettingMenuDetailItem item);
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public SettingMenuDetailAdapter(Context context, List<SettingMenuDetailItem> datas) {
        mContext=context;
        this.datas=datas;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //根据item类别加载不同ViewHolder
        if(viewType==0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_setting_menu_detail, parent,false);
            ViewHolder holder = new ViewHolder(view);

            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //将数据与item视图进行绑定，如果是MyViewHolder就加载网络图片，如果是MyViewHolder2就显示页数
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder)holder;

            SettingMenuDetailItem curItem = datas.get(position);
            viewHolder.titleTextView.setText(curItem.getName());

            if( curItem.isChecked() == true ){
                viewHolder.checkedImageView.setVisibility(View.VISIBLE);
            }else{
                viewHolder.checkedImageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount(){
        return datas.size();
    }

    public void addItem(SettingMenuDetailItem item){
        this.datas.add(item);
        notifyDataSetChanged();
    }

    public void setDatas(List<SettingMenuDetailItem> subMenus) {
        this.datas = subMenus;
        notifyDataSetChanged();
    }

    public void unCheckAll() {
        if( datas!= null ){
            for( SettingMenuDetailItem item : datas){
                if (item != null) {
                    item.setChecked(false);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void toggle(SettingMenuDetailItem item) {
        if( datas!= null ){
            for( SettingMenuDetailItem curItem : datas){
                if (curItem != null && curItem.getName().equals(item.getName())) {
                    curItem.setChecked(!curItem.isChecked());
                }
            }
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView checkedImageView;

        public ViewHolder(View view)
        {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            checkedImageView = (ImageView) view.findViewById(R.id.checkedImageView);

            //给布局设置点击和长点击监听
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick( datas.get(getAdapterPosition()) );
                }
            });
        }
    }

}
