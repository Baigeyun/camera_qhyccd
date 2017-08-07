package com.starrysky.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starrysky.R;
import com.starrysky.dto.SettingMenuDetailItem;
import com.starrysky.dto.SettingMenuItem;

import java.util.List;

public class SettingMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<SettingMenuItem> datas;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(SettingMenuItem item);
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public SettingMenuAdapter(Context context,List<SettingMenuItem> datas) {
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_setting_menu, parent,false);
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

            SettingMenuItem curItem = datas.get(position);
            viewHolder.titleTextView.setText(curItem.getName());
            viewHolder.valueTextView.setText(curItem.getCurrentSettingValue());
        }

    }

    @Override
    public int getItemCount(){
        return datas.size();
    }

    public void addItem(SettingMenuItem item){
        this.datas.add(item);
        notifyDataSetChanged();
    }

    /**
     * 当设置值被设置，更新设置列表value
     * @param curSettingMenuItem
     * @param detailItem
     */
    public void onSet(SettingMenuItem curSettingMenuItem, SettingMenuDetailItem detailItem) {
        if( datas != null ){
            for( SettingMenuItem item : datas){
                if( item.getName().equals(curSettingMenuItem.getName()) ){
                    item.setCurrentSettingValue(detailItem.getName());
                }
            }
            notifyDataSetChanged();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView valueTextView;

        public ViewHolder(View view)
        {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            valueTextView = (TextView) view.findViewById(R.id.valueTextView);

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
