package com.nanchen.aiyagirl.module.picture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.nanchen.aiyagirl.R;
import com.nanchen.aiyagirl.base.BaseActivity;
import com.nanchen.aiyagirl.module.picture.PictureContract.PictureView;
import com.nanchen.aiyagirl.module.picture.PictureContract.Presenter;
import com.nanchen.aiyagirl.utils.Utils;
import com.youth.banner.Banner;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 大图页面
 * <p>
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-04-24  10:11
 */
public class PictureActivity extends BaseActivity implements PictureView {

    public static final String EXTRA_IMAGE_URL = "com.nanchen.aiyagirl.module.picture.PictureActivity.EXTRA_IMAGE_URL";
    public static final String EXTRA_IMAGE_TITLE = "com.nanchen.aiyagirl.module.picture.PictureActivity.EXTRA_IMAGE_TITLE";
    public static final String TRANSIT_PIC = "picture";
    String mImageUrl, mImageTitle;
    private Presenter mPresenter;
    private Bitmap mBitmap = null;

    @BindView(R.id.picture_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.picture_img)
    PhotoView mImgView;
    @BindView(R.id.picture_btn_save)
    ImageButton mBtnSave;
    @BindView(R.id.picture_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.picture_progress)
    ProgressBar mProgressBar;

    public static void start(Activity context, String url, String desc, Banner banner) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(EXTRA_IMAGE_URL, url);
        intent.putExtra(EXTRA_IMAGE_TITLE, desc);
        //activity动画，1上下文，2view，3string，过度动画标识，其实是transitionName
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context, banner, TRANSIT_PIC);//与xml文件对应，其实没有，是直接设置的
        ActivityCompat.startActivity(context, intent, options.toBundle());
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_picture;
    }

    @Override
    protected void initView(Bundle savedInstanceSate) {
        showProgress();
        mPresenter = new PicturePresenter(this);
        parseIntent();
        ViewCompat.setTransitionName(mImgView, TRANSIT_PIC);

//        mAppBarLayout.setAlpha(0.7f);
        mToolbar.setTitle(TextUtils.isEmpty(mImageTitle) ? "图片预览" : mImageTitle);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(mToolbar);
        //全屏预览，隐藏mToolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

//        Glide.with(Utils.getContext())
//                .load(mImageUrl)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                        hideProgress();
//                        mImgView.setImageDrawable(resource);
//                    }
//                });

        //利用回调直接拿到bitmap(下载用)，不需要在presenter里面转
        Glide.with(Utils.getContext())
                .load(mImageUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        hideProgress();
                        mBitmap = bitmap;
                        mImgView.setImageBitmap(bitmap);
                    }
                });

    }

    private void parseIntent() {
        mImageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        mImageTitle = getIntent().getStringExtra(EXTRA_IMAGE_TITLE);
    }

    @OnClick(R.id.picture_btn_save)
    public void onClick() {
        //保存图片
        if (mPresenter != null) {
//            mPresenter.saveGirl(mImageUrl, mImgView.getWidth(), mImgView.getHeight(), mImageTitle);
            mPresenter.saveGirl(mImageUrl, mBitmap, mImageTitle);
        }
    }

    @OnClick(R.id.picture_img)
    public void onPictureClick() {
        //判断，是否显示mToolbar
        if (getSupportActionBar() != null) {
            if (getSupportActionBar().isShowing()) {
                getSupportActionBar().hide();
            } else {
                getSupportActionBar().show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
