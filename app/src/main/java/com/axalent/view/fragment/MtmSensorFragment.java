package com.axalent.view.fragment;

import com.axalent.R;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.util.AxalentUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MtmSensorFragment extends HistoryFragment {
	
	private ShowDeviceActivity parent = null;
	
	@Override
	public void onAttach(Activity activity) {
		parent = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}

	public MtmSensorFragment(String historyName) {
		super(historyName);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		String roll = AxalentUtils.getDeviceAttributeValue(parent.getCurrentDevice(), AxalentUtils.ATTRIBUTE_ROLL);
		if (roll.equals("")) roll = "1";
		int[] imgs = {R.drawable.dice1, R.drawable.dice2, R.drawable.dice3, R.drawable.dice4, R.drawable.dice5, R.drawable.dice6};
		parent.getDeviceImg().setBackgroundResource(imgs[Integer.parseInt(roll)]);
	}

}
