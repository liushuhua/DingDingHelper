/*
 * Copyright (C)  Justson(https://github.com/Justson/DingDingHelper)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ucmap.dingdinghelper.timing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ucmap.dingdinghelper.app.App;
import com.ucmap.dingdinghelper.entity.MessageEvent;
import com.ucmap.dingdinghelper.services.DingDingHelperAccessibilityService;
import com.ucmap.dingdinghelper.utils.Constants;
import com.ucmap.dingdinghelper.utils.DingHelperUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class TimingBroadcastReceiver extends BroadcastReceiver {

    /*标志位*/
    private int flag = -1;

    private static long lastTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        long tmp = System.currentTimeMillis();
        if (tmp - lastTime <= 15000) {
            Toast.makeText(App.mContext, "上一次打卡时间没超过15s", Toast.LENGTH_SHORT).show();
            return;
        }
        lastTime = tmp;
        // TODO Auto-generated method stub
        long intervalMillis = intent.getLongExtra("intervalMillis", 0);
        if (intervalMillis != 0 && !Constants.IS_NOTITY_TYPE_CHECK_IN_TAG) {
            TimingManagerUtil.resetTiming(context, System.currentTimeMillis() + intervalMillis,
                    intent);
        }

        List<String> mList = new ArrayList<String>();
        if (!DingDingHelperAccessibilityService.IS_ENABLE_DINGDINGHELPERACCESSIBILITYSERVICE) {

            mList.add(Constants.POINT_SERVICES_ORDER);
            mList.add(Constants.ENABLE_SERVICE_PUT);
            flag = 0;
        } else {
        /*唤醒屏幕*/
            if (!DingHelperUtils.isScreenLight(App.mContext)) {
                mList.add("input keyevent 26");
            }

            if (DingHelperUtils.isScreenLocked(App.mContext)) {
                 /*从下往上滑动解锁*/
                mList.add("input swipe 200 800 200 100");
            }
            flag = 1;
        }
        if (!mList.isEmpty()) {
            new BootOrderThread(mList, flag).start();
        } else {
            EventBus.getDefault().post(new MessageEvent(flag));
        }
        flag = -1;

    }


}
