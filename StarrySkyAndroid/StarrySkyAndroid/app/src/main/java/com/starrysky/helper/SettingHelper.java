package com.starrysky.helper;

import android.content.Context;

import com.starrysky.dto.SettingMenuDetailItem;
import com.starrysky.dto.SettingMenuItem;


public class SettingHelper {
    public static void saveSetting(Context context , SettingMenuItem settingItem, SettingMenuDetailItem settingDetailItem) {
        if( settingItem != null && settingDetailItem != null ){
            SharedPreferencesHelper.put(context, SharedPreferencesHelper.KEY_SETTING_CATEGORY_PRIFIX + settingItem.getName(), settingDetailItem.getName() );
        }
    }

    public static String getSavedSetting( Context context ,SettingMenuItem settingItem) {
        Object obj = SharedPreferencesHelper.get(context, SharedPreferencesHelper.KEY_SETTING_CATEGORY_PRIFIX + settingItem.getName(),"");
        String settingValue = obj == null ? null : (String)obj;
        if ( settingValue  == null ) {
            return null;
        }else{
            return settingValue;
        }
    }

    public static String getSavedSetting( Context context ,String settingItemName) {
        Object obj = SharedPreferencesHelper.get(context, SharedPreferencesHelper.KEY_SETTING_CATEGORY_PRIFIX + settingItemName,"");
        String settingValue = obj == null ? null : (String)obj;
        if ( settingValue  == null ) {
            return null;
        }else{
            return settingValue;
        }
    }
}
