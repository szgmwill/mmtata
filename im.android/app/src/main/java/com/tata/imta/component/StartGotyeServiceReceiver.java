package com.tata.imta.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tata.imta.helper.LogHelper;

public class StartGotyeServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
        LogHelper.debug("StartGotyeServiceReceiver", "BroadcastReceiver onReceive>>>>>>>");
		Intent intent = new Intent(context, GotyeService.class);
		context.startService(intent);
	}

}
