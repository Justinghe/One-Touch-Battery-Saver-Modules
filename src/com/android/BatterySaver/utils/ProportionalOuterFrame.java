package com.android.BatterySaver.utils;

import com.android.BatterySaver.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class ProportionalOuterFrame extends RelativeLayout {

	public ProportionalOuterFrame(Context context) {
		super(context);
	}

	public ProportionalOuterFrame(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public ProportionalOuterFrame(Context context, AttributeSet attributeSet,
			int paramInt) {
		super(context, attributeSet, paramInt);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = View.MeasureSpec.getSize(widthMeasureSpec);
		int height = View.MeasureSpec.getSize(heightMeasureSpec);
		Resources resources = getContext().getResources();
		float scale = resources.getFraction(R.dimen.setup_border_width, 1, 1);
		int bottom = resources
				.getDimensionPixelSize(R.dimen.setup_margin_bottom);
		setPadding((int) scale * width, (int) scale * height, (int) scale
				* width, bottom);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
