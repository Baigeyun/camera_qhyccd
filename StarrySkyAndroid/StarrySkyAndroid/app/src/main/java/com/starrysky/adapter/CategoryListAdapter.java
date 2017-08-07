package com.starrysky.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.starrysky.R;
import com.starrysky.dto.Category;
import com.starrysky.listener.OnSelectListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter  extends RecyclerView.Adapter<CategoryListAdapter.CategoryListItemViewHolder> {
    private Context context;
    private List<Category> categoryListData;

    private OnSelectListener<Category> categorySelectListener;


    public CategoryListAdapter(Context context ,List<Category> dateItems) {
        this.context = context;
        this.categoryListData = (dateItems != null ? dateItems : new ArrayList<Category>());
    }

    @Override
    public CategoryListAdapter.CategoryListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_category, viewGroup, false);
        return new CategoryListAdapter.CategoryListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryListAdapter.CategoryListItemViewHolder viewHolder, final int position) {
        final Category category = categoryListData.get(position);

        viewHolder.categoryNameTextView.setText(category.getName());
        if( category.isSelected() == true ){
            viewHolder.cursorImageView.setVisibility(View.VISIBLE);
        }else{
            viewHolder.cursorImageView.setVisibility(View.GONE);
        }

        viewHolder.categoryNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set cursor
                deselectAll();
                selectByIndex(position);

                if( categorySelectListener != null ){
                    categorySelectListener.onSelect(category);
                }
            }
        });

    }

    private void deselectAll() {
        for(Category category : categoryListData){
            category.setSelected(false);
        }

        notifyDataSetChanged();
    }

    private void selectByIndex(int index){
        categoryListData.get(index).setSelected(true);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (this.categoryListData != null) ? this.categoryListData.size() : 0;
    }

    public void setCategorySelectListener(OnSelectListener categorySelectListener) {
        this.categorySelectListener = categorySelectListener;
    }


    class CategoryListItemViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        ImageView cursorImageView;

        public CategoryListItemViewHolder(View itemView) {
            super(itemView);
            this.categoryNameTextView = (TextView) itemView.findViewById(R.id.categoryNameTextView);
            this.cursorImageView = (ImageView) itemView.findViewById(R.id.cursorImageView);
        }
    }

}
