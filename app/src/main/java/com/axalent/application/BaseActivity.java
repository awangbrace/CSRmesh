/**
 * File Name                   : ���н����Base��
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.application;

import java.util.Locale;
import com.axalent.util.AxalentUtils;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
	
	protected SharedPreferences sharedPreferences = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		switchoverLanguage();
	}


    /**
     * �л�����
     */
    private void switchoverLanguage() {
        Resources resources = getResources();//���res��Դ����
        Configuration config = resources.getConfiguration();//������ö���
        sharedPreferences = getSharedPreferences(AxalentUtils.USER_FILE_NAME, MODE_PRIVATE);
        int languageItem = sharedPreferences.getInt("languageItem", 0);
        switch (languageItem) {
            case 0:
            	config.locale = Locale.ENGLISH;
                break;
            case 1:
            	config.locale = Locale.CHINA;
                break;
        }
        resources.updateConfiguration(config, resources .getDisplayMetrics());
    }
    

}
