package com.starrysky.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.starrysky.R;
import com.starrysky.adapter.CategoryListAdapter;
import com.starrysky.adapter.SubCategoryListAdapter;
import com.starrysky.dto.Category;
import com.starrysky.dto.SubCategory;
import com.starrysky.helper.SharedPreferencesHelper;
import com.starrysky.listener.OnSelectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OptionPanelView extends LinearLayout {
    private int mSelectedCategoryId;
    private int mSelectedSubCategoryId;

    // view
    private RecyclerView mCategoryRecyclerView;
    private ImageView mSettingImageView;

    private RecyclerView mSubCategoryRecyclerView;
    private ImageView mPlusBtnView;
    private ImageView mMinusBtnView;

    private String[] categoryDataAry;
    private String[][] subCategoryDataAry;

    // adapter
    private CategoryListAdapter mCategoryListAdapter;
    private SubCategoryListAdapter mSubCategoryListAdapter;

    // data
    private Map<Category,List<SubCategory>> data;

    //listener
    private OnSelectListener<SubCategory> onSubCategorySelectListener;


    private Category currentCategory;

    public OptionPanelView(Context context) {
        super(context);
        init(context,null, 0);
    }

    public OptionPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs, 0);
    }

    public OptionPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs, defStyle);
    }

    private void init(Context context,AttributeSet attrs, int defStyle) {
        initData();

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.OptionPanelView, defStyle, 0);

        mSelectedCategoryId = a.getInt(R.styleable.OptionPanelView_selectedCategoryId,0);
        mSelectedSubCategoryId = a.getInt(R.styleable.OptionPanelView_selectedSubCategoryId,0);

        a.recycle();

        initView(context);
    }

    private void initData() {
        categoryDataAry = new String[]{
                getResources().getString(R.string.resolution),
                getResources().getString(R.string.traffice),
                getResources().getString(R.string.analogGain),
                getResources().getString(R.string.digitalGain),
                getResources().getString(R.string.speed),
                getResources().getString(R.string.exposureTime),
        };

        subCategoryDataAry = new String[][]{
                getResources().getStringArray(R.array.resolution),
                getResources().getStringArray(R.array.traffice),
                getResources().getStringArray(R.array.analoggain),
                getResources().getStringArray(R.array.digitalgain),
                getResources().getStringArray(R.array.speed),
                getResources().getStringArray(R.array.exposureTime)
        };
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_option_panel, this, true);

        /* init category view */
        mCategoryRecyclerView = (RecyclerView) findViewById(R.id.categoryRecyclerView);
        mSettingImageView = (ImageView) findViewById(R.id.settingImageButton);

        LinearLayoutManager categoryRecyclerViewLayoutManager = new LinearLayoutManager(context);
        categoryRecyclerViewLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        mCategoryRecyclerView.setLayoutManager(categoryRecyclerViewLayoutManager);
        List<Category> categoryListData = initCategoryData(0);
        mCategoryListAdapter = new CategoryListAdapter(context,categoryListData);
        mCategoryRecyclerView.setAdapter(mCategoryListAdapter);
        /*********************/
        /* init sub category view */
        mSubCategoryRecyclerView = (RecyclerView) findViewById(R.id.subCategoryRecyclerView);
        mPlusBtnView = (ImageView) findViewById(R.id.plusImageView);
        mMinusBtnView = (ImageView) findViewById(R.id.minusImageView);

        LinearLayoutManager subCategoryRecyclerViewLayoutManager = new LinearLayoutManager(context);
        subCategoryRecyclerViewLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        mSubCategoryRecyclerView.setLayoutManager(subCategoryRecyclerViewLayoutManager);
        List<SubCategory> subCategoryListData = initSubCategoryData(0);
        mSubCategoryListAdapter = new SubCategoryListAdapter(context,subCategoryListData);
        mSubCategoryRecyclerView.setAdapter(mSubCategoryListAdapter);
        /*********************/
    }



    private List<Category> initCategoryData(int selectedIndex) {
        List<Category> list = new ArrayList<>();
        if( categoryDataAry != null && categoryDataAry.length > 0 ){
            int index = 0 ;
            for(String categoryName: categoryDataAry ){
                Category category = new Category();
                category.setIndex(index);
                category.setName(categoryName);
                if( selectedIndex == index ){
                    currentCategory = category;
                    category.setSelected(true);
                }else{
                    category.setSelected(false);
                }
                list.add(category);

                index ++;
            }
        }

        return list;
    }

    private List<SubCategory> initSubCategoryData(int selectedCategoryIndex) {
        List<SubCategory> list = new ArrayList<>();

        if( categoryDataAry != null && subCategoryDataAry != null ){
            String[] subCateAry = subCategoryDataAry[selectedCategoryIndex];
            int index = 0 ;
            for(String subCateName : subCateAry ){
                SubCategory subCate = new SubCategory();
                subCate.setIndex(index);
                subCate.setName(subCateName);

                SubCategory savedSubCategory = getSavedSubCategory(selectedCategoryIndex);

                if (savedSubCategory != null) {
                    if( savedSubCategory.getIndex() == index ){
                        subCate.setSelected(true);

                    }else{
                        subCate.setSelected(false);
                    }
                }

                list.add(subCate);

                index++;
            }
        }

        return list;
    }

    private SubCategory getSavedSubCategory( int selectedCategoryIndex ) {
        Object obj = SharedPreferencesHelper.get(getContext(), SharedPreferencesHelper.KEY_SETTING_CATEGORY_PRIFIX + selectedCategoryIndex,(Integer)(-1));
        Integer subCategoryIndex = (Integer)obj;;
        if (subCategoryIndex == -1) {
            return null;
        }else{
            SubCategory subCategory = getSubCategoryByIndex(selectedCategoryIndex,subCategoryIndex);
            return subCategory;
        }
    }

    private SubCategory getSubCategoryByIndex(Integer selectedCategoryIndex,Integer subCategoryIndex) {
        String subCategoryName = subCategoryDataAry[selectedCategoryIndex][subCategoryIndex];
        SubCategory subCategory = new SubCategory();
        subCategory.setName( subCategoryName);
        subCategory.setSelected( null );
        subCategory.setIndex( subCategoryIndex );
        return subCategory;
    }

    public void setData(Map<Category, List<SubCategory>> data) {
        this.data = data;
    }

    public void setOnCategorySelectListener(final OnSelectListener<Category> categorySelectListener) {
        mCategoryListAdapter.setCategorySelectListener(new OnSelectListener() {
            @Override
            public void onSelect(Object selectedCategory) {
                currentCategory = (Category)selectedCategory;

                renderSubCategoryList(currentCategory.getIndex());


                if( categorySelectListener != null ){
                    categorySelectListener.onSelect(currentCategory);
                }
            }
        });
    }

    private void renderSubCategoryList(Integer index) {
        if( subCategoryDataAry != null && subCategoryDataAry.length > index ){
            List<SubCategory> subCateList = initSubCategoryData(index);

            mSubCategoryListAdapter.reloadSubCategory(subCateList);
        }
    }

    public void setOnSubCategorySelectListener(OnSelectListener<SubCategory> OnSubCategorySelectListener) {
        this.onSubCategorySelectListener = OnSubCategorySelectListener;
        mSubCategoryListAdapter.setOnSubCategorySelectListener(OnSubCategorySelectListener);
    }

    public void setOnSettingButtonClickListener(OnClickListener onSettingButtonClickListener) {
        mSettingImageView.setOnClickListener(onSettingButtonClickListener);
    }

    public void setOnMinusButtonClickListener(OnClickListener onMinusButtonClickListener) {
        mMinusBtnView.setOnClickListener(onMinusButtonClickListener);
    }

    public void setOnPlusButtonClickListener(OnClickListener onPlusButtonClickListener) {
        mPlusBtnView.setOnClickListener(onPlusButtonClickListener);
    }

    public void onCategorySelect(Category category) {

    }

    public Category getCurrentCategory() {
        return currentCategory;
    }
}
