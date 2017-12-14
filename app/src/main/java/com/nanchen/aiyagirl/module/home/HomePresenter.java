package com.nanchen.aiyagirl.module.home;

import com.nanchen.aiyagirl.model.CategoryResult;
import com.nanchen.aiyagirl.model.CategoryResult.ResultsBean;
import com.nanchen.aiyagirl.model.PictureModel;
import com.nanchen.aiyagirl.module.home.HomeContract.IHomePresenter;
import com.nanchen.aiyagirl.module.home.HomeContract.IHomeView;
import com.nanchen.aiyagirl.net.NetWork;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-04-14  12:31
 */

public class HomePresenter implements IHomePresenter{

    private Subscription mSubscription;
    private IHomeView mHomeView;
    private List<PictureModel> mModels;

    HomePresenter(IHomeView homeView){
        this.mHomeView = homeView;
        mModels = new ArrayList<>();
    }

    @Override
    public void subscribe() {
        getBannerData();
    }

    @Override
    public void unSubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    //获取banner数据
    List<PictureModel> getBannerModels(){
        return this.mModels;
    }

    @Override
    public void getBannerData() {
        mSubscription = NetWork.getGankApi()
                .getCategoryData("福利",5,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CategoryResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mHomeView.showBannerFail("Banner 图加载失败");
                    }

                    @Override
                    public void onNext(CategoryResult categoryResult) {
                        if (categoryResult != null && categoryResult.results != null && categoryResult.results.size() > 0){
                            List<String> imgUrls = new ArrayList<>();
                            //遍历转对象
                            for (ResultsBean result : categoryResult.results) {
                                if (!result.url.isEmpty()){
                                    imgUrls.add(result.url);
                                }
                                PictureModel model = new PictureModel();
                                model.desc = result.desc;
                                model.url = result.url;
                                mModels.add(model);
                            }
                            //遍历之后把数据传给view
                            mHomeView.setBanner(imgUrls);
                        }else{
                            mHomeView.showBannerFail("Banner 图加载失败");
                        }
                    }
                });
    }

}
