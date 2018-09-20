package com.helper.manager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 多状态布局管理
 * Created by tubro on 2018/9/20.
 */

public class SwitchStatusLayoutManager {

    /**
     * 四种默认布局 ID
     */
    private static final int DEFAULT_LOADING_LAYOUT_ID = R.layout.layout_mslm_loading;
    private static final int DEFAULT_EMPTY_LAYOUT_ID = R.layout.layout_mslm_empty;
    private static final int DEFAULT_ERROR_LAYOUT_ID = R.layout.layout_mslm_error;
    private static final int DEFAULT_NO_NEWORK_LAYOUT_ID = R.layout.layout_mslm_no_network;
    /**
     * contentLayout 在 parentLayout 中的位置
     */
    private int mViewIndex;
    private Object mTarget;
    private LayoutInflater mInflater;
    private int mCurrentStatus;
    private View mContentLayout;
    private ViewGroup mParentLayout;
    private SparseIntArray mStatusViewMap;
    private SparseArray<View> mStatusViews;
    private SparseArray<View.OnClickListener> mClickListeners;
    private ViewGroup.LayoutParams mLayoutParams;

    public SwitchStatusLayoutManager(MultiStatusLayoutManager multiStatusLayoutManager) {
        mTarget = multiStatusLayoutManager.getTarget();
        mStatusViewMap = multiStatusLayoutManager.getStatusViewMap();
        mClickListeners = multiStatusLayoutManager.getClickListeners();
        getContentLayoutParams();
        initStatusViews();
        initStatusViewClickListeners();
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
     * 初始化预设的状态布局
     */
    private void initStatusViews() {
        if (mStatusViews == null)
            mStatusViews = new SparseArray<>();
        if (mStatusViewMap == null)
            mStatusViewMap = new SparseIntArray();
        if (mStatusViewMap.get(MultiStatusLayoutManager.Status.EMPTY_TYPE) == 0) {
            mStatusViewMap.put(MultiStatusLayoutManager.Status.EMPTY_TYPE, DEFAULT_EMPTY_LAYOUT_ID);
        }
        if (mStatusViewMap.get(MultiStatusLayoutManager.Status.ERROR_TYPE) == 0) {
            mStatusViewMap.put(MultiStatusLayoutManager.Status.ERROR_TYPE, DEFAULT_ERROR_LAYOUT_ID);
        }
        if (mStatusViewMap.get(MultiStatusLayoutManager.Status.NO_NETWORK_TYPE) == 0) {
            mStatusViewMap.put(MultiStatusLayoutManager.Status.NO_NETWORK_TYPE, DEFAULT_NO_NEWORK_LAYOUT_ID);
        }
        if (mStatusViewMap.get(MultiStatusLayoutManager.Status.LOADING_TYPE) == 0) {
            mStatusViewMap.put(MultiStatusLayoutManager.Status.LOADING_TYPE, DEFAULT_LOADING_LAYOUT_ID);
        }
        for (int i = 0; i < mStatusViewMap.size(); i++) {
            int key = mStatusViewMap.keyAt(i);
            int value = mStatusViewMap.valueAt(i);
            View view = mInflater.inflate(value, null);
            if (view != null) {
                mStatusViews.put(key, view);
            }
        }
        mStatusViews.put(MultiStatusLayoutManager.Status.CONTENT_TYPE, mContentLayout);
    }

    /**
     * 获取 contentLayout 的参数信息 LayoutParams、Parent
     */
    private void getContentLayoutParams() {
        if (mTarget == null) {
            throw new NullPointerException("参数不能为空");
        }
        Context context = null;
        if (mTarget instanceof Activity) {
            // 认为 contentLayout 是 activity 的跟布局
            // 所以它的父控件就是 android.R.id.content
            context = (Activity) mTarget;
            mParentLayout = ((Activity) context).findViewById(android.R.id.content);
            mContentLayout = mParentLayout != null ? mParentLayout.getChildAt(0) : null;
        } else if (mTarget instanceof View) {
            // 有直接的父控件
            mContentLayout = (View) mTarget;
            context = ((View) mTarget).getContext();
            mParentLayout = (ViewGroup) mContentLayout.getParent();
        } else if (mTarget instanceof Fragment) {
            // 有直接的父控件
            mContentLayout = ((Fragment) mTarget).getView();
            context = ((Fragment) mTarget).getActivity();
            mParentLayout = (ViewGroup) mContentLayout.getParent();
        } else if (mTarget instanceof android.support.v4.app.Fragment) {
            // 有直接的父控件
            mContentLayout = ((android.support.v4.app.Fragment) mTarget).getView();
            context = ((android.support.v4.app.Fragment) mTarget).getActivity();
            mParentLayout = (ViewGroup) mContentLayout.getParent();
        } else {
            throw new IllegalArgumentException("参数必须为view、Fragment或者Activity类型");
        }
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLayoutParams = mContentLayout.getLayoutParams();
    }

    private void initStatusViewClickListeners() {
        if (mClickListeners == null)
            mClickListeners = new SparseArray<>();
    }

    /**
     * 展示加载动画
     */
    public void showLoadingView() {
        showStatusView(MultiStatusLayoutManager.Status.LOADING_TYPE);
    }

    /**
     * 展示加载动画
     *
     * @param loadingText
     */
    public void showLoadingView(String loadingText) {
        showStatusView(MultiStatusLayoutManager.Status.LOADING_TYPE, loadingText);
    }

    /**
     * 展示出错页面
     */
    public void showErrorView() {
        showStatusView(MultiStatusLayoutManager.Status.ERROR_TYPE);
    }

    /**
     * 展示出错页面
     *
     * @param errorText
     */
    public void showErrorView(String errorText) {
        showStatusView(MultiStatusLayoutManager.Status.ERROR_TYPE, errorText);
    }

    /**
     * 展示空页面
     */
    public void showEmptyView() {
        showStatusView(MultiStatusLayoutManager.Status.EMPTY_TYPE);
    }

    /**
     * 展示空页面
     *
     * @param emptyText
     */
    public void showEmptyView(String emptyText) {
        showStatusView(MultiStatusLayoutManager.Status.EMPTY_TYPE, emptyText);
    }

    /**
     * 展示网络错误页面
     */
    public void showNoNetWorkView() {
        showStatusView(MultiStatusLayoutManager.Status.NO_NETWORK_TYPE);
    }

    /**
     * 展示网络错误页面
     *
     * @param noNetworkText
     */
    public void showNoNetWorkView(String noNetworkText) {
        showStatusView(MultiStatusLayoutManager.Status.NO_NETWORK_TYPE, noNetworkText);
    }

    /**
     * 显示原有布局
     */
    public void showContentView() {
        showStatusView(MultiStatusLayoutManager.Status.CONTENT_TYPE);
    }

    /**
     * 隐藏原有布局
     */
    public void hideContentView() {
        hideStatusView(MultiStatusLayoutManager.Status.CONTENT_TYPE);
    }

    /**
     * 展示各种状态的页面
     *
     * @param statusType
     */
    private void showStatusView(@MultiStatusLayoutManager.Status.StatusType int statusType) {
        showStatusView(statusType, null);
    }

    /**
     * 展示各种状态的页面
     *
     * @param statusType
     */
    private void showStatusView(@MultiStatusLayoutManager.Status.StatusType int statusType, String text) {
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
            if (mClickListeners != null) {
                final View.OnClickListener clickListener = mClickListeners.get(statusType);
                if (clickListener != null) {
                    view.setOnClickListener(clickListener);
                }
            }
            mParentLayout.addView(view);
        } else {
            throw new IllegalArgumentException(String.format("the status (%d) layout is  not exist", statusType));
        }
        mCurrentStatus = statusType;
    }


    /**
     * 隐藏各种状态的页面
     *
     * @param statusType
     */
    public void hideStatusView(@MultiStatusLayoutManager.Status.StatusType int statusType) {
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
    public void addStatusView(@MultiStatusLayoutManager.Status.StatusType int statusType, int id) {
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
}
