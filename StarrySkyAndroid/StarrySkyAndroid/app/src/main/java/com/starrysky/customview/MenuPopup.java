package com.starrysky.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.starrysky.R;
import com.starrysky.adapter.SettingMenuAdapter;
import com.starrysky.adapter.SettingMenuDetailAdapter;
import com.starrysky.dto.SettingMenuDetailItem;
import com.starrysky.dto.SettingMenuItem;
import com.starrysky.helper.SettingHelper;
import com.starrysky.helper.SettingMenuProvider;

import java.util.ArrayList;


public class MenuPopup extends PopupWindow {
    private View contentView;
    private Context context;
    private NonSwipeableViewPager mPager;
    private int popupWidth;
    private SettingMenuItem curSettingMenuItem;

    private OnSettingSetListener onSettingSetListener;

    public MenuPopup(final Context context) {
        super(context);
        this.context = context;
        this.initPopupWindow();

    }

    private void initPopupWindow() {
        //使用view来引入布局
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_menu, null);

        setContentView(contentView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        if( context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // Portrait Mode
            popupWidth = screenWidth*2/3;
            setHeight(screenHeight/2);
        } else {
            // Landscape Mode
            popupWidth = screenWidth/2;
            setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        }

        setWidth(popupWidth);

        this.setFocusable(true);
        this.setOutsideTouchable(true);

        initPager(inflater);
    }

    private void initPager(LayoutInflater inflater) {
        mPager = (NonSwipeableViewPager) contentView.findViewById(R.id.pager);


        View settingMenuView = inflater.inflate(R.layout.view_setting_menu,null);
        final View settingMenuDetailView = inflater.inflate(R.layout.view_setting_menu_detail,null);

        ImageView closeImageView = (ImageView)settingMenuView.findViewById(R.id.closeImageView);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuPopup.this.dismiss();
            }
        });

        RecyclerView settingMenuRecyclerView = (RecyclerView)settingMenuView.findViewById(R.id.settingMenuRecyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(context,R.drawable.divider_line));
        settingMenuRecyclerView.addItemDecoration(dividerItemDecoration);
        settingMenuRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        final SettingMenuAdapter settingMenuAdapter = new SettingMenuAdapter(context, SettingMenuProvider.getSettingMenuDataList(context));
        settingMenuRecyclerView.setAdapter(settingMenuAdapter );

        settingMenuAdapter.setOnItemClickListener( new SettingMenuAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(SettingMenuItem item) {
                curSettingMenuItem = item;
                renderSettingMenuDetailView(settingMenuDetailView,item);

                mPager.setCurrentItem(1);
            }
        });


        // setting menu detail view
        ImageView backImageView = (ImageView)settingMenuDetailView.findViewById(R.id.backImageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });

        RecyclerView settingMenuDetailRecyclerView = (RecyclerView)settingMenuDetailView.findViewById(R.id.recyclerView);
        settingMenuDetailRecyclerView.addItemDecoration(dividerItemDecoration);
        settingMenuDetailRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        final SettingMenuDetailAdapter settingMenuDetailAdapter = new SettingMenuDetailAdapter(context,new ArrayList<SettingMenuDetailItem>());
        settingMenuDetailRecyclerView.setAdapter(settingMenuDetailAdapter);

        settingMenuDetailAdapter.setOnItemClickListener(new SettingMenuDetailAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(SettingMenuDetailItem item) {
                if( item.isChecked() == false ){
                    settingMenuDetailAdapter.unCheckAll();
                }

                settingMenuDetailAdapter.toggle(item);

                SettingHelper.saveSetting(context,curSettingMenuItem,item);
                settingMenuAdapter.onSet(curSettingMenuItem,item);
                mPager.setCurrentItem(0);
                if( onSettingSetListener != null ){
                    onSettingSetListener.onSet(curSettingMenuItem,item);
                }
            }
        });

        View[] views = new View[]{settingMenuView,settingMenuDetailView};

        SlidePagerAdapter mPagerAdapter = new SlidePagerAdapter( inflater , views);
        mPager.setAdapter(mPagerAdapter);
    }

    private void renderSettingMenuDetailView(View settingMenuDetailView,SettingMenuItem item) {
        if( item != null ){
            TextView titleTextView = (TextView)settingMenuDetailView.findViewById(R.id.title);
            titleTextView.setText(item.getName());

            RecyclerView settingMenuDetailRecyclerView = (RecyclerView)settingMenuDetailView.findViewById(R.id.recyclerView);
            SettingMenuDetailAdapter settingMenuDetailAdapter = settingMenuDetailRecyclerView.getAdapter() == null ? null : (SettingMenuDetailAdapter)settingMenuDetailRecyclerView.getAdapter();
            if( settingMenuDetailAdapter == null ){
                settingMenuDetailAdapter = new SettingMenuDetailAdapter(context,new ArrayList<SettingMenuDetailItem>());
                settingMenuDetailRecyclerView.setAdapter(settingMenuDetailAdapter);
            }
            if( item.getSubMenus() != null && item.getSubMenus().size() > 0 ){
                String savedValue = SettingHelper.getSavedSetting(context,item);
                if( savedValue != null ){
                    for( SettingMenuDetailItem detailItem : item.getSubMenus()){
                        if( detailItem.getName().equals(savedValue) ){
                            detailItem.setChecked(true);
                        }
                    }
                }
                settingMenuDetailAdapter.setDatas(item.getSubMenus());

            }
        }
    }



    public void show(View parent){
        this.showAtLocation(parent, Gravity.CENTER, 0, 0);


    }

    public void showAlignLeft(View anchor){
        this.showAsDropDown(anchor,-popupWidth,0);
        this.update();
    }
    private class SlidePagerAdapter extends PagerAdapter  {
        private LayoutInflater inflater;
        private View[] views;

        public SlidePagerAdapter(LayoutInflater inflater,View[] views) {
            this.inflater = inflater;
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o==view;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            View view = null;
            if( position == 0 ){
                view = views[0];
                container.addView(view);
            }else{
                view = views[1];
                container.addView(view);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((Button)object);
        }
    }

    public void setOnSettingSetListener(OnSettingSetListener onSettingSetListener) {
        this.onSettingSetListener = onSettingSetListener;
    }

    public interface OnSettingSetListener{
        void onSet(SettingMenuItem item,SettingMenuDetailItem detailItem);
    }
}
