package com.starrysky.helper;

import android.content.Context;

import com.starrysky.R;
import com.starrysky.dto.SettingMenuDetailItem;
import com.starrysky.dto.SettingMenuItem;

import java.util.ArrayList;
import java.util.List;


public class SettingMenuProvider {
    public static List<SettingMenuItem> getSettingMenuDataList(Context context) {
        List<SettingMenuItem> list = new ArrayList<>();

        SettingMenuItem item = null;

        item = new SettingMenuItem();
        item.setName(context.getResources().getString(R.string.resolution));

        String value = SettingHelper.getSavedSetting(context,item);
        if( value != null ){
            item.setCurrentSettingValue(value);
        }else{
            item.setCurrentSettingValue("");
        }

        String[] detailAry = context.getResources().getStringArray(R.array.resolution);
        List<SettingMenuDetailItem> detailList = new ArrayList<>();
        SettingMenuDetailItem detailItem = null;
        for(String detailItemName : detailAry ){
            detailItem = new SettingMenuDetailItem();
            detailItem.setName(detailItemName);
            detailItem.setChecked(false);
            detailList.add(detailItem);
        }
        item.setSubMenus(detailList);
        list.add(item);

        // traffice
        item = new SettingMenuItem();
        item.setName(context.getResources().getString(R.string.traffice));
        value = SettingHelper.getSavedSetting(context,item);
        if( value != null ){
            item.setCurrentSettingValue(value);
        }else{
            item.setCurrentSettingValue("");
        }

        detailAry = context.getResources().getStringArray(R.array.traffice);
        detailList = new ArrayList<>();
        for(String detailItemName : detailAry ){
            detailItem = new SettingMenuDetailItem();
            detailItem.setName(detailItemName);
            detailItem.setChecked(false);
            detailList.add(detailItem);
        }
        item.setSubMenus(detailList);
        list.add(item);

        // analoggain
        item = new SettingMenuItem();
        item.setName(context.getResources().getString(R.string.analogGain));
        value = SettingHelper.getSavedSetting(context,item);
        if( value != null ){
            item.setCurrentSettingValue(value);
        }else{
            item.setCurrentSettingValue("");
        }

        detailAry = context.getResources().getStringArray(R.array.analoggain);
        detailList = new ArrayList<>();
        for(String detailItemName : detailAry ){
            detailItem = new SettingMenuDetailItem();
            detailItem.setName(detailItemName);
            detailItem.setChecked(false);
            detailList.add(detailItem);
        }
        item.setSubMenus(detailList);
        list.add(item);

        // digitalGain
        item = new SettingMenuItem();
        item.setName(context.getResources().getString(R.string.digitalGain));
        value = SettingHelper.getSavedSetting(context,item);
        if( value != null ){
            item.setCurrentSettingValue(value);
        }else{
            item.setCurrentSettingValue("");
        }

        detailAry = context.getResources().getStringArray(R.array.digitalgain);
        detailList = new ArrayList<>();
        for(String detailItemName : detailAry ){
            detailItem = new SettingMenuDetailItem();
            detailItem.setName(detailItemName);
            detailItem.setChecked(false);
            detailList.add(detailItem);
        }
        item.setSubMenus(detailList);
        list.add(item);

        // speed
        item = new SettingMenuItem();
        item.setName(context.getResources().getString(R.string.speed));
        value = SettingHelper.getSavedSetting(context,item);
        if( value != null ){
            item.setCurrentSettingValue(value);
        }else{
            item.setCurrentSettingValue("");
        }

        detailAry = context.getResources().getStringArray(R.array.speed);
        detailList = new ArrayList<>();
        for(String detailItemName : detailAry ){
            detailItem = new SettingMenuDetailItem();
            detailItem.setName(detailItemName);
            detailItem.setChecked(false);
            detailList.add(detailItem);
        }
        item.setSubMenus(detailList);
        list.add(item);

        // speed
        item = new SettingMenuItem();
        item.setName(context.getResources().getString(R.string.exposureTime));
        value = SettingHelper.getSavedSetting(context,item);
        if( value != null ){
            item.setCurrentSettingValue(value);
        }else{
            item.setCurrentSettingValue("");
        }

        detailAry = context.getResources().getStringArray(R.array.exposureTime);
        detailList = new ArrayList<>();
        for(String detailItemName : detailAry ){
            detailItem = new SettingMenuDetailItem();
            detailItem.setName(detailItemName);
            detailItem.setChecked(false);
            detailList.add(detailItem);
        }
        item.setSubMenus(detailList);
        list.add(item);

        // output Image Format
        item = new SettingMenuItem();
        item.setName(context.getResources().getString(R.string.outputImageFormat));
        value = SettingHelper.getSavedSetting(context,item);
        if( value != null ){
            item.setCurrentSettingValue(value);
        }else{
            item.setCurrentSettingValue("");
        }

        detailAry = context.getResources().getStringArray(R.array.outputImageFormat);
        detailList = new ArrayList<>();
        for(String detailItemName : detailAry ){
            detailItem = new SettingMenuDetailItem();
            detailItem.setName(detailItemName);
            detailItem.setChecked(false);
            detailList.add(detailItem);
        }
        item.setSubMenus(detailList);
        list.add(item);

        return list;
    }
}
