package com.orangecoder.androidffmpegcmdtool.activity;

import android.app.Activity;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangecoder.androidffmpegcmdtool.R;
import com.orangecoder.androidffmpegcmdtool.view.GifMovieView;

import java.io.File;

public class PreviewVideoProcessResultActivity extends Activity {

	private static final String TAG = "PreviewVideoProcessResultActivity";
	
	public static final String KEY_TYPE = "type";
	public static final String KEY_GIF_LOCALPATH = "gif";
	public static final String KEY_PIC_LOCALPATH = "pic";
	public static final String KEY_VIDEO_URL = "videourl";
	public static final int VALUE_PREVIEW_GIF = 1;
	public static final int VALUE_PREVIEW_PIC = 2;
	
	private int previewType = 0;
	private String previewPicLocalPath;
	private String previewGifLocalPath;
	private String previewVideoUrl;
	private ImageLoader imageLoader;
	private MyOnClickListener mOnClickListener;
	
	private ImageView iv_pic;
	private GifMovieView gmv_gif;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previewvideoprocessresult);
		
		initData();
		initView();
		initEvent();
	}
	
	private void initData()
	{
		previewType = getIntent().getIntExtra(KEY_TYPE, 0);
		if(previewType == 0)
		{
			return;
		}
		previewGifLocalPath = getIntent().getExtras().getString(
				KEY_GIF_LOCALPATH, "");
		previewPicLocalPath = getIntent().getExtras().getString(
				KEY_PIC_LOCALPATH, "");
		previewVideoUrl = getIntent().getExtras().getString(
				KEY_VIDEO_URL, "");
		
		imageLoader = ImageLoader.getInstance();
		mOnClickListener = new MyOnClickListener();
	}

	private void initView()
	{
		TextView tv_title = (TextView) findViewById(
				R.id.tv_previewVideoProcessResultActivity_title);
		gmv_gif = (GifMovieView) findViewById(
				R.id.gmv_previewVideoProcessResultActivity_gif);
		gmv_gif.setVisibility(View.GONE);
		
		iv_pic = (ImageView) findViewById(
				R.id.iv_previewVideoProcessResultActivity_pic);
		iv_pic.setVisibility(View.GONE);
		
		switch (previewType) {
		case VALUE_PREVIEW_GIF:
			tv_title.setText("预览GIF");
			gmv_gif.setVisibility(View.VISIBLE);
			Movie movie = Movie.decodeFile(previewGifLocalPath);
			gmv_gif.setMovie(movie);
			break;
		case VALUE_PREVIEW_PIC:
			tv_title.setText("预览连拍图");
			iv_pic.setVisibility(View.VISIBLE);
			imageLoader.displayImage(Uri.fromFile(new File(
					previewPicLocalPath)).toString(), iv_pic);
			break;
		}
	}
	
	private void initEvent() {
		findViewById(R.id.layout_previewVideoProcessResultActivity_back)
			.setOnClickListener(mOnClickListener);
		findViewById(R.id.layout_previewVideoProcessResultActivity_share)
			.setOnClickListener(mOnClickListener);
	}
	
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layout_previewVideoProcessResultActivity_back:
				finish();
				break;
			case R.id.layout_previewVideoProcessResultActivity_share:
//				Intent intent_share = new Intent();
//				switch (previewType) {
//				case VALUE_PREVIEW_GIF:
//					intent_share.putExtra(ShareToSocialNetworkActivity.KEY_SHARE_TYPE,
//							ShareToSocialNetworkActivity.VALUE_SHARETYPE_GIF);
//					intent_share.putExtra(ShareToSocialNetworkActivity.KEY_GIFPATH,
//							previewGifLocalPath);
//					break;
//				case VALUE_PREVIEW_PIC:
//					intent_share.putExtra(ShareToSocialNetworkActivity.KEY_SHARE_TYPE,
//							ShareToSocialNetworkActivity.VALUE_SHARETYPE_PIC);
//					intent_share.putExtra(ShareToSocialNetworkActivity.KEY_IAMGEPATH,
//							previewPicLocalPath);
//					break;
//				}
//				intent_share.putExtra(ShareToSocialNetworkActivity.KEY_VIDEOURL,
//						previewVideoUrl);
//				intent_share.setClass(PreviewVideoProcessResultActivity.this,
//						ShareToSocialNetworkActivity.class);
//				startActivity(intent_share);
				break;
			}
		}
	}
}
