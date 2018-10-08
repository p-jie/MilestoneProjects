
package com.example.jacky.recycler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jacky.R;
import com.example.jacky.base.BaseFragment;
import com.example.jacky.base.LazyFragment;
import com.example.jacky.mvp.factory.PresenterMvpFactory;
import com.example.jacky.mvp.factory.PresenterMvpFactoryImpl;
import com.example.jacky.mvp.presenter.BaseMvpPresenter;
import com.example.jacky.mvp.proxy.BaseMvpProxy;
import com.example.jacky.mvp.proxy.PresenterProxyInterface;
import com.example.jacky.mvp.view.BaseMvpView;
import com.example.jacky.recycler.adapter.base.BaseQuickAdapter;
import com.example.jacky.recycler.recyclerInterface.OnLoadListener;
import com.example.jacky.recycler.recyclerInterface.OnStopLoadListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;


/**
 * @param <T>  数据模型(model/JavaBean)类
 * @param <VH> ViewHolder或其子类
 * @param <A>  管理LV的Adapter
 * @param <V>  BaseView的子类
 * @param <P>  BasePresenter的子类
 * @author jacky
 * @date 2018/4/8
 * @description 基础RecyclerView Fragment
 */
public abstract class BaseRecyclerFragment<T, A extends BaseQuickAdapter, V extends BaseMvpView, P extends BaseMvpPresenter<V>>
        extends LazyFragment implements PresenterProxyInterface<V, P>, BaseQuickAdapter.OnItemClickListener
        , BaseQuickAdapter.OnItemLongClickListener, OnRefreshListener, OnLoadMoreListener, OnStopLoadListener {
    private static final String TAG = "BaseRecyclerFragment";
    /**
     * 调用onSaveInstanceState时存入Bundle的key
     */
    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";
    /**
     * 创建被代理对象,传入默认Presenter的工厂
     */
    private BaseMvpProxy<V, P> mProxy = new BaseMvpProxy<>(PresenterMvpFactoryImpl.<V, P>createFactory(getClass()));

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     * @must 1.不要在子类重复这个类中onCreateView中的代码;
     * 2.在子类onCreateView中
     * super.onCreateView(inflater, container, savedInstanceState);
     * <p>
     * initView();
     * initData();
     * initEvent();
     * <p>
     * return view;
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //类相关初始化，必须使用<<<<<<<<<<<<<<<<<<
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(setLayout());
        //类相关初始化，必须使用>>>>>>>>>>>>>>>>

        return view;
    }

    protected abstract int setLayout();
    // UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 显示列表的RecyclerView
     *
     * @warn 只使用rvBaseRecycler为显示列表数据的AbsRecyclerView(RecyclerView, GridView等)，不要在子类中改变它
     */
    protected RecyclerView rvBaseRecycler;


    /**
     * 下拉刷新  上拉加载
     */
    protected SmartRefreshLayout srlBaseHttpRecycler;

    /**
     * 管理LV的Item的Adapter
     */
    protected A adapter;

    /**
     * 如果在子类中调用(即super.initView());则view必须含有initView中初始化用到的id且id对应的View的类型全部相同；
     * 否则必须在子类initView中重写这个类中initView内的代码(所有id替换成可用id)
     */
    @Override
    public void initView() {// 必须调用

        rvBaseRecycler = findView(R.id.rvBaseRecycler);
        srlBaseHttpRecycler = findView(R.id.srlBaseHttpRecycler);
        if (rvBaseRecycler != null) {
            rvBaseRecycler.setLayoutManager(new LinearLayoutManager(context));
            rvBaseRecycler.setHasFixedSize(true);
        }

    }

    /**
     * 设置adapter
     *
     * @param adapter
     */
    public void setAdapter(A adapter) {
        if (adapter != null && adapter instanceof BaseQuickAdapter) {
            adapter.setOnItemClickListener(this);
            adapter.setOnItemLongClickListener(this);
            if (srlBaseHttpRecycler != null) {
                adapter.setOnLoadListener(new OnLoadListener() {
                    @Override
                    public void onRefresh() {
                        srlBaseHttpRecycler.autoRefresh();
                    }

                    @Override
                    public void onLoadMore() {
                        srlBaseHttpRecycler.autoLoadMore();
                    }
                });
            }
        }
        this.adapter = adapter;
        rvBaseRecycler.setAdapter(adapter);
    }

    /**
     * 刷新列表数据（已在UI线程中），一般需求建议直接调用setList(List<T> l, AdapterCallBack<A> callBack)
     *
     * @param list
     */
    public abstract void setList(List<T> list);

    /**
     * 显示列表（已在UI线程中）
     *
     * @param callBack
     */
    public void setList(AdapterCallBack<A> callBack) {
        if (adapter == null) {
            setAdapter(callBack.createAdapter());
        }
        callBack.refreshAdapter();
    }

    @Override
    public void initData() {// 必须调用
    }

    /**
     * 获取列表，在非UI线程中
     *
     * @param page 在onLoadSucceed中传回来保证一致性
     * @must 获取成功后调用onLoadSucceed
     */
    public abstract void getListAsync(int page);


    public void loadData(int page) {
        loadData(page, false);
    }

    /**
     * 列表首页页码。有些服务器设置为1，即列表页码从1开始
     */
    public static final int PAGE_NUM_0 = 0;

    /**
     * 数据列表
     */
    private List<T> list;
    /**
     * 正在加载
     */
    protected boolean isLoading = false;
    /**
     * 还有更多可加载数据
     */
    protected boolean isHaveMore = true;
    /**
     * 加载页码，每页对应一定数量的数据
     */
    private int page;
    private int loadCacheStart;

    /**
     * 加载数据，用getListAsync方法发请求获取数据
     *
     * @param page_
     * @param isCache
     */
    private void loadData(int page_, final boolean isCache) {
        if (isLoading) {
            Log.w(TAG, "loadData  isLoading >> return;");
            stopLoadData(page_);
            return;
        }
        isLoading = true;
        isSucceed = false;

        if (page_ <= PAGE_NUM_0) {
            page_ = PAGE_NUM_0;
            isHaveMore = true;
            loadCacheStart = 0;//使用则可像网络正常情况下的重载，不使用则在网络异常情况下不重载（导致重载后加载数据下移）
        } else {
            if (isHaveMore == false) {
                stopLoadData(page_);
                return;
            }
            loadCacheStart = list == null ? 0 : list.size();
        }
        this.page = page_;
        Log.i(TAG, "loadData  page_ = " + page_ + "; isCache = " + isCache
                + "; isHaveMore = " + isHaveMore + "; loadCacheStart = " + loadCacheStart);

        runThread(TAG + "loadData", new Runnable() {

            @Override
            public void run() {
                getListAsync(page);
            }
        });
    }

    /**
     * 停止加载数据
     * isCache = false;
     *
     * @param page
     */
    public synchronized void stopLoadData(int page) {
        stopLoadData(page, false);
    }

    /**
     * 停止加载数据
     *
     * @param page
     * @param isCache
     */
    private synchronized void stopLoadData(int page, boolean isCache) {
        Log.i(TAG, "stopLoadData  isCache = " + isCache);
        isLoading = false;
        dismissProgressDialog();

        if (isCache) {
            Log.d(TAG, "stopLoadData  isCache >> return;");
            return;
        }

        if (onStopLoadListener == null) {
            Log.w(TAG, "stopLoadData  onStopLoadListener == null >> return;");
            return;
        }
        onStopLoadListener.onStopRefresh();
        if (page > PAGE_NUM_0) {
            onStopLoadListener.onStopLoadMore(isHaveMore);
        }
    }


    private boolean isSucceed = false;

    /**
     * 处理列表
     *
     * @param page
     * @param newList 新数据列表
     * @param isCache
     * @return
     */
    public synchronized void handleList(int page, List<T> newList, boolean isCache) {
        if (newList == null) {
            newList = new ArrayList<T>();
        }
        isSucceed = !newList.isEmpty();
        Log.i(TAG, "\n\n<<<<<<<<<<<<<<<<<\n handleList  newList.size = " + newList.size() + "; isCache = " + isCache
                + "; page = " + page + "; isSucceed = " + isSucceed);

        if (page <= PAGE_NUM_0) {
            Log.i(TAG, "handleList  page <= PAGE_NUM_0 >>>>  ");
            saveCacheStart = 0;
            list = new ArrayList<T>(newList);

        } else {
            Log.i(TAG, "handleList  page > PAGE_NUM_0 >>>>  ");
            if (list == null) {
                list = new ArrayList<T>();
            }
            saveCacheStart = list.size();
            isHaveMore = !newList.isEmpty();
            if (isHaveMore) {
                list.addAll(newList);
            }
        }

        Log.i(TAG, "handleList  list.size = " + list.size() + "; isHaveMore = " + isHaveMore
                + "; saveCacheStart = " + saveCacheStart
                + "\n>>>>>>>>>>>>>>>>>>\n\n");
    }


    /**
     * 加载成功
     * isCache = false;
     *
     * @param page
     * @param newList
     */
    public synchronized void onLoadSucceed(final int page, final List<T> newList) {
        onLoadSucceed(page, newList, false);
    }

    /**
     * 加载成功
     *
     * @param page
     * @param newList
     * @param isCache newList是否为缓存
     */
    private synchronized void onLoadSucceed(final int page, final List<T> newList, final boolean isCache) {
        runThread(TAG + "onLoadSucceed", new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onLoadSucceed  page = " + page + "; isCache = " + isCache + " >> handleList...");
                handleList(page, newList, isCache);

                runUiThread(new Runnable() {

                    @Override
                    public void run() {
                        stopLoadData(page, isCache);
                        setList(list);
                    }
                });
            }
        });
    }

    /**
     * 加载失败
     *
     * @param page
     * @param e
     */
    public synchronized void onLoadFailed(int page, Exception e) {
        Log.e(TAG, "onLoadFailed page = " + page + "; e = " + (e == null ? null : e.getMessage()));
        stopLoadData(page);
        showShortToast(R.string.get_failed);
    }


    private int saveCacheStart;


    @Override
    public void initEvent() {
        setOnStopLoadListener(this);

        srlBaseHttpRecycler.setOnRefreshListener(this);
        srlBaseHttpRecycler.setOnLoadMoreListener(this);
    }


    private OnStopLoadListener onStopLoadListener;

    /**
     * 设置停止加载监听
     *
     * @param onStopLoadListener
     */
    protected void setOnStopLoadListener(OnStopLoadListener onStopLoadListener) {
        this.onStopLoadListener = onStopLoadListener;
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        onRefresh();
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        onLoadMore();
    }

    @Override
    public void onStopRefresh() {
        runUiThread(new Runnable() {

            @Override
            public void run() {
                srlBaseHttpRecycler.finishRefresh();
                srlBaseHttpRecycler.setLoadmoreFinished(false);
            }
        });
    }

    @Override
    public void onStopLoadMore(final boolean isHaveMore) {
        runUiThread(new Runnable() {

            @Override
            public void run() {
                if (isHaveMore) {
                    srlBaseHttpRecycler.finishLoadMore();
                } else {
                    srlBaseHttpRecycler.finishLoadMoreWithNoMoreData();
                }
                isLoading = false;
                srlBaseHttpRecycler.setNoMoreData(! isHaveMore);
            }
        });
    }

    /**
     * 刷新（从头加载）
     *
     * @must 在子类onCreate中调用，建议放在最后
     */
    public void onRefresh() {
        loadData(PAGE_NUM_0);
    }

    /**
     * 加载更多
     */
    public void onLoadMore() {
        if (isSucceed == false && page <= PAGE_NUM_0) {
            Log.w(TAG, "onLoadMore  isSucceed == false && page <= PAGE_NUM_0 >> return;");
            return;
        }
        loadData(page + (isSucceed ? 1 : 0));
    }


    // 系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    /**
     * 重写后可自定义对这个事件的处理
     *
     * @param parent
     * @param view
     * @param position
     */

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    /**
     * 重写后可自定义对这个事件的处理，如果要在长按后不触发onItemClick，则需要 return true;
     *
     * @param parent
     * @param view
     * @param position
     */
    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mProxy.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mProxy.onResume((V) this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_SAVE_KEY, mProxy.onSaveInstanceState());
    }


    /**
     * 可以实现自己PresenterMvpFactory工厂
     *
     * @param presenterFactory PresenterFactory类型
     */
    @Override
    public void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory) {
        mProxy.setPresenterFactory(presenterFactory);
    }


    /**
     * 获取创建Presenter的工厂
     *
     * @return PresenterMvpFactory类型
     */
    @Override
    public PresenterMvpFactory<V, P> getPresenterFactory() {
        return mProxy.getPresenterFactory();
    }

    /**
     * 获取Presenter
     *
     * @return P
     */
    @Override
    public P getMvpPresenter() {
        return mProxy.getMvpPresenter();
    }


    @Override
    public void onDestroy() {
        isLoading = false;
        isHaveMore = false;

        super.onDestroy();

        rvBaseRecycler = null;
        list = null;

        onStopLoadListener = null;
        mProxy.onDestroy();

    }

}