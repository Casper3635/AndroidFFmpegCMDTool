package com.orangecoder.androidffmpegcmdtool.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class GPHorizontalScrollView extends HorizontalScrollView{
	private GPHorizontalScrollViewListener mListener;

	public GPHorizontalScrollView(Context context) {
		super(context);
	}
	
	public GPHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GPHorizontalScrollView(Context context, AttributeSet attrs,
								  int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public interface GPHorizontalScrollViewListener{
		void onScrollChanged(int l, int t, int oldl, int oldt);
	}
	
	public void setListener(GPHorizontalScrollViewListener listener){
		mListener = listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if(mListener != null)
		{
			mListener.onScrollChanged(l, t, oldl, oldt);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}
	

}
