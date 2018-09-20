package com.helper.manager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 多状态布局管理
 * Created by tubro on 2018/9/18.
 */

public class MultiStatusLayoutManager {

    private volatile static MultiStatusLayoutManager instance; //声明成 volatile
    //展示多状态布局的父布局
    private View mContentLayout;
    private Object mTarget;
    private ViewGroup mParentLayout;

    private LayoutInflater mInflater;
    private SparseIntArray mStatusViewMap;
    private SparseArray<View> mStatusViews;
    private SparseArray<View.OnClickListener> mClickListeners;
    private ViewGroup.LayoutParams mLayoutParams;

    public MultiStatusLayoutManager(Builder builder) {
        mStatusViewMap = builder.mStatusViewMap;
        mClickListeners = builder.mClickListeners;
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static MultiStatusLayoutManager getInstance(Builder builder) {
        if (instance == null) {                         //Single Checked
            synchronized (MultiStatusLayoutManager.class) {
                if (instance == null) {                 //Double Checked
                    instance = new MultiStatusLayoutManager(builder);
                }
            }
        }
        return instance;
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static MultiStatusLayoutManager getInstance() {
        if (instance == null) {                         //Single Checked
            throw new IllegalArgumentException("You must use commit method first before use getInstance without builder");
        }
        return instance;
    }

    //因为需要传参，所以不能使用静态内部类的方式使用单例
    private static class SingletonHolder {
        private static final MultiStatusLayoutManager INSTANCE = new MultiStatusLayoutManager(null);
    }


    public SwitchStatusLayoutManager regist(Object target) {
        mTarget = target;
        return new SwitchStatusLayoutManager(this);
    }

    public View getContentLayout() {
        return mContentLayout;
    }

    public Object getTarget() {
        return mTarget;
    }

    public ViewGroup getParentLayout() {
        return mParentLayout;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    public SparseIntArray getStatusViewMap() {
        return mStatusViewMap;
    }

    public SparseArray<View> gemStatusViews() {
        return mStatusViews;
    }

    public SparseArray<View.OnClickListener> getClickListeners() {
        return mClickListeners;
    }

    public ViewGroup.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    /**
     * 多状态 参考https://www.jianshu.com/p/1fb27f46622c
     */
    interface Status {

        int CONTENT_TYPE = 0;
        int LOADING_TYPE = 1;
        int ERROR_TYPE = 2;
        int EMPTY_TYPE = 3;
        int NO_NETWORK_TYPE = 4;

        @IntDef({CONTENT_TYPE, LOADING_TYPE, ERROR_TYPE, EMPTY_TYPE, NO_NETWORK_TYPE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface StatusType {
        }
    }

    public static class Builder {
        public SparseIntArray mStatusViewMap;
        public SparseArray<View.OnClickListener> mClickListeners;

        public Builder setErrorView(int layoutId) {
            return setStatusView(Status.ERROR_TYPE, layoutId);
        }

        public Builder setLoadingView(int layoutId) {
            return setStatusView(Status.LOADING_TYPE, layoutId);
        }

        public Builder setNoNetWorkView(int layoutId) {
            return setStatusView(Status.NO_NETWORK_TYPE, layoutId);
        }

        public Builder setEmptyView(int layoutId) {
            return setStatusView(Status.EMPTY_TYPE, layoutId);
        }

        private Builder setStatusView(@Status.StatusType int status, int layoutId) {
            if (mStatusViewMap == null) {
                mStatusViewMap = new SparseIntArray();
            }
            mStatusViewMap.put(status, layoutId);
            return this;
        }

        /**
         * 添加无网络点击事件
         */
        public Builder addNoNetWorkClickListener(View.OnClickListener clickListener) {
            return addOnClickListener(Status.NO_NETWORK_TYPE, clickListener);
        }

        /**
         * 添加错误布局点击事件
         */
        public Builder addErrorClickListener(View.OnClickListener statusClickListener) {
            return addOnClickListener(Status.ERROR_TYPE, statusClickListener);
        }

        /**
         * 添加其他状态布局点击事件，以方便自定义设置
         */
        public Builder addOnClickListener(@Status.StatusType int status, View.OnClickListener statusClickListener) {
            if (mClickListeners == null) {
                mClickListeners = new SparseArray();
            }
            mClickListeners.put(status, statusClickListener);
            return this;
        }

        //提供两种创建方式
        // 1.一种是创建普通对象，用create(target)获取SwitchStatusLayoutManager对象
        // 2.另一种用commit方法创建单例,然后调用regist(target)获取SwitchStatusLayoutManager对象
        public SwitchStatusLayoutManager create(@NonNull Object target) {
            MultiStatusLayoutManager multiStatusLayoutManager = new MultiStatusLayoutManager(this);
            return multiStatusLayoutManager.regist(target);
        }

        public void commit() {
            getInstance(this);
        }
    }
}
