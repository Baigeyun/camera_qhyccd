package com.starrysky.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.starrysky.R;
import com.starrysky.dto.SubCategory;
import com.starrysky.listener.OnSelectListener;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryListAdapter extends RecyclerView.Adapter<SubCategoryListAdapter.SubCategoryListItemViewHolder> {
    private Context context;
    private List<SubCategory> subCategoryListData;
    private OnSelectListener<SubCategory> onSubCategorySelectListener;

    public SubCategoryListAdapter(Context context , List<SubCategory> dateItems) {
        this.context = context;
        this.subCategoryListData = (dateItems != null ? dateItems : new ArrayList<SubCategory>());
    }

    @Override
    public SubCategoryListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_sub_category, viewGroup, false);
        return new SubCategoryListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubCategoryListItemViewHolder viewHolder, final int position) {
        final SubCategory subCategory = subCategoryListData.get(position);

        viewHolder.subCategoryNameTextView.setText(subCategory.getName());
        if( subCategory.getSelected() != null &&  subCategory.getSelected() == true ){
            viewHolder.cursorImageView.setVisibility(View.VISIBLE);
        }else{
            viewHolder.cursorImageView.setVisibility(View.GONE);
        }

        viewHolder.subCategoryNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set cursor
                deselectAll();
                selectByIndex(position);

                if( onSubCategorySelectListener != null ){
                    onSubCategorySelectListener.onSelect(subCategory);
                }
            }
        });
    }

    private void deselectAll() {
        for(SubCategory subCategory : subCategoryListData){
            subCategory.setSelected(false);
        }

        notifyDataSetChanged();
    }

    private void selectByIndex(int index){
        subCategoryListData.get(index).setSelected(true);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (this.subCategoryListData != null) ? this.subCategoryListData.size() : 0;
    }

    public void setOnSubCategorySelectListener(OnSelectListener<SubCategory> onSubCategorySelectListener) {
        this.onSubCategorySelectListener = onSubCategorySelectListener;
    }

    public void reloadSubCategory(List<SubCategory> subCateList) {
        this.subCategoryListData = subCateList;
        notifyDataSetChanged();
    }

    class SubCategoryListItemViewHolder extends RecyclerView.ViewHolder {
        TextView subCategoryNameTextView;
        ImageView cursorImageView;

        public SubCategoryListItemViewHolder(View itemView) {
            super(itemView);
            this.subCategoryNameTextView = (TextView) itemView.findViewById(R.id.subCategoryNameTextView);
            this.cursorImageView = (ImageView) itemView.findViewById(R.id.cursorImageView);
        }
    }

}
