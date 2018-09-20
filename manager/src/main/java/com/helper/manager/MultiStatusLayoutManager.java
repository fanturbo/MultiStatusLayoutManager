package com.helper.manager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
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

    /**
     * 四种默认布局 ID
     */
    private static final int DEFAULT_LOADING_LAYOUT_ID = R.layout.layout_mslm_loading;
    private static final int DEFAULT_EMPTY_LAYOUT_ID = R.layout.layout_mslm_empty;
    private static final int DEFAULT_ERROR_LAYOUT_ID = R.layout.layout_mslm_error;
    private static final int DEFAULT_NO_NEWORK_LAYOUT_ID = R.layout.layout_mslm_no_network;

    private volatile static MultiStatusLayoutManager instance; //声明成 volatile
    //展示多状态布局的父布局
    private View mContentLayout;
    private Object mTarget;
    private ViewGroup mParentLayout;
    /**
     * contentLayout 在 parentLayout 中的位置
     */
    private int mViewIndex;
    private LayoutInflater mInflater;
    private SparseIntArray mStatusViewMap;
    private SparseArray<View> mStatusViews;
    private SparseArray<View.OnClickListener> mClickListeners;
    private ViewGroup.LayoutParams mLayoutParams;
    private int mCurrentStatus;

    public MultiStatusLayoutManager(Builder builder) {
        mTarget = builder.mTarget;
        mInflater = builder.mInflater;
        mStatusViewMap = builder.mStatusViewMap;
        getContentLayoutParams();
        initStatusViews();
        initStatusViewClickListeners();
    }

    /**
     * 获取 contentLayout 的参数信息 LayoutParams、Parent
     */
    private void getContentLayoutParams() {
        if (mTarget instanceof Activity) {
            // 认为 contentLayout 是 activity 的跟布局
            // 所以它的父控件就是 android.R.id.content
            Activity activity = (Activity) mTarget;
            mParentLayout = activity.findViewById(android.R.id.content);
            mContentLayout = mParentLayout != null ? mParentLayout.getChildAt(0) : null;
        } else if (mTarget instanceof View) {
            // 有直接的父控件
            mContentLayout = (View) mTarget;
            mParentLayout = (ViewGroup) mContentLayout.getParent();
        } else if (mTarget instanceof Fragment || mTarget instanceof android.support.v4.app.Fragment) {
            // 有直接的父控件
            mContentLayout = ((Fragment) mTarget).getView();
            mParentLayout = (ViewGroup) mContentLayout.getParent();
        } else {
            throw new IllegalArgumentException("参数必须为view、Fragment或者Activity类型");
        }
        this.mLayoutParams = mContentLayout.getLayoutParams();
        int count = mParentLayout.getChildCount();
        for (int index = 0; index < count; index++) {
            if (mContentLayout == mParentLayout.getChildAt(index)) {
                // 获取 contentLayout 在 mParentLayout 中的位置
                mViewIndex = index;
                break;
            }
        }
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

    //因为需要传参，所以不能使用静态内部类的方式使用单例
    private static class SingletonHolder {
        private static final MultiStatusLayoutManager INSTANCE = new MultiStatusLayoutManager(null);
    }


    /**
     * 初始化预设的状态布局
     */
    private void initStatusViews() {
        mStatusViews = new SparseArray<>();
        if (mStatusViewMap == null || mStatusViews.size() == 0) {
            mStatusViewMap.put(Status.EMPTY_TYPE, DEFAULT_EMPTY_LAYOUT_ID);
            mStatusViewMap.put(Status.ERROR_TYPE, DEFAULT_ERROR_LAYOUT_ID);
            mStatusViewMap.put(Status.NO_NETWORK_TYPE, DEFAULT_NO_NEWORK_LAYOUT_ID);
            mStatusViewMap.put(Status.LOADING_TYPE, DEFAULT_LOADING_LAYOUT_ID);
        }
        for (int i = 0; i < mStatusViewMap.size(); i++) {
            int key = mStatusViewMap.keyAt(i);
            int value = mStatusViewMap.valueAt(i);
            View view = mInflater.inflate(value, null);
            if (view != null) {
                mStatusViews.put(key, view);
            }
        }
        mStatusViews.put(Status.CONTENT_TYPE, mContentLayout);
    }

    private void initStatusViewClickListeners() {
        mClickListeners = new SparseArray<>();
    }

    /**
     * 展示加载动画
     */
    public void showLoadingView() {
        showStatusView(Status.LOADING_TYPE);
    }

    /**
     * 展示加载动画
     *
     * @param loadingText
     */
    public void showLoadingView(String loadingText) {
        showStatusView(Status.LOADING_TYPE, loadingText);
    }

    /**
     * 展示出错页面
     */
    public void showErrorView() {
        showStatusView(Status.ERROR_TYPE);
    }

    /**
     * 展示出错页面
     *
     * @param errorText
     */
    public void showErrorView(String errorText) {
        showStatusView(Status.ERROR_TYPE, errorText);
    }

    /**
     * 展示空页面
     */
    public void showEmptyView() {
        showStatusView(Status.EMPTY_TYPE);
    }

    /**
     * 展示空页面
     *
     * @param emptyText
     */
    public void showEmptyView(String emptyText) {
        showStatusView(Status.EMPTY_TYPE, emptyText);
    }

    /**
     * 展示网络错误页面
     */
    public void showNoNetWorkView() {
        showStatusView(Status.NO_NETWORK_TYPE);
    }

    /**
     * 展示网络错误页面
     *
     * @param noNetworkText
     */
    public void showNoNetWorkView(String noNetworkText) {
        showStatusView(Status.NO_NETWORK_TYPE, noNetworkText);
    }

    /**
     * 显示原有布局
     */
    public void showContentView() {
        showStatusView(Status.CONTENT_TYPE);
    }

    /**
     * 隐藏原有布局
     */
    public void hideContentView() {
        hideStatusView(Status.CONTENT_TYPE);
    }

    /**
     * 展示各种状态的页面
     *
     * @param statusType
     */
    private void showStatusView(@Status.StatusType int statusType) {
        showStatusView(statusType, null);
    }

    /**
     * 展示各种状态的页面
     *
     * @param statusType
     */
    private void showStatusView(@Status.StatusType int statusType, String text) {
        checkStatusViewExist(statusType);
        if (mCurrentStatus != -1) {
            if (mCurrentStatus == statusType) {
                return;
            }
            if (mParentLayout != null) {
                mParentLayout.removeView(mStatusViews.get(mCurrentStatus));
            }
        }
        // 去除 view 的 父 view，才能添加到别的 ViewGroup 中
        View view = mStatusViews.get(statusType);
        if (view != null) {
            final View.OnClickListener clickListener = mClickListeners.get(statusType);
            if (clickListener != null) {
                view.setOnClickListener(clickListener);
            }
            mParentLayout.addView(view, mViewIndex, mLayoutParams);
        } else {
            throw new IllegalArgumentException("There is no corresponding layout for the status (%d) ");
        }
        mCurrentStatus = statusType;
    }

    /**
     * 隐藏各种状态的页面
     *
     * @param statusType
     */
    public void hideStatusView(@Status.StatusType int statusType) {
        if (mStatusViews == null || mStatusViews.get(statusType) == null) {
            return;
        }
        if (mCurrentStatus != -1) {
            if (mParentLayout != null) {
                mParentLayout.removeView(mStatusViews.get(mCurrentStatus));
            }
        }
        mCurrentStatus = -1;
    }

    /**
     * 检测状态视图是否存在
     */
    private void checkStatusViewExist(int statusType) {
        if (mStatusViews == null || mStatusViews.get(statusType) == null) {
            //如果没有主动设置某个状态的布局的话，添加默认布局
            initStatusViews();
        }
    }

    /**
     * 添加状态布局
     */
    public void addStatusView(@Status.StatusType int statusType, int id) {
        View view = View.inflate(mContentLayout.getContext(), id, null);
        if (view != null) {
            mStatusViews.put(statusType, view);
        }
    }

    /**
     * 清理缓存
     */
    public void release() {
        if (mClickListeners != null) {
            mClickListeners.clear();
        }
        if (mStatusViewMap != null) {
            mStatusViewMap.clear();
        }
        if (mStatusViews != null) {
            mStatusViews.clear();
        }
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
        private Object mTarget;
        public Context mContext;
        public LayoutInflater mInflater;
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
            mClickListeners.put(status, statusClickListener);
            return this;
        }

        /**
         * 创建状态布局 Build 对象
         *
         * @param target 原有布局，内容布局
         */
        public Builder(@NonNull Object target) {
            if (target == null) {
                throw new NullPointerException("参数不能为空");
            }
            this.mTarget = target;
            if (mTarget instanceof Activity) {
                mContext = (Context) mTarget;
            } else if (target instanceof View) {
                mContext = ((View) target).getContext();
            } else if (target instanceof Fragment || target instanceof android.support.v4.app.Fragment) {
                mContext = ((Fragment) target).getActivity();
            } else {
                throw new IllegalArgumentException("参数必须为view、Fragment或者Activity类型");
            }
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mStatusViewMap = new SparseIntArray();
            mClickListeners = new SparseArray<>();
        }

        public MultiStatusLayoutManager create() {
            return new MultiStatusLayoutManager(this);
        }

        //提供两种创建方式，一种是创建普通对象，用上面create，另一种用newBuilder->commit方法创建单例
        public static Builder newBuilder(@NonNull View contentLayout) {
            return new Builder(contentLayout);
        }

        public MultiStatusLayoutManager commit() {
            return new MultiStatusLayoutManager(this);
        }
    }
}
