package com.my.design.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.loongfly.mybs.R;

/**
 * Created by Loong Fly on 2018/2/21.
 */

public class MyListView extends ListView implements AbsListView.OnScrollListener {
    View foot;
    int totalItemCount;
    int lastVisibleItem;
    boolean isLoading;
    ILoadListener loadinterface;
    public MyListView(Context context) {
        super(context);
        initView(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }



    public void initView(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);
        foot=inflater.inflate(R.layout.foot_boot,null);
        foot.findViewById(R.id.foot_layout).setVisibility(View.GONE);
        this.addFooterView(foot);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(totalItemCount==lastVisibleItem && scrollState==SCROLL_STATE_IDLE){
            foot.findViewById(R.id.foot_layout).setVisibility(View.VISIBLE);
            if(!isLoading){
                isLoading=true;
                loadinterface.onLoad();
            }
            //加载数据
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem=firstVisibleItem+totalItemCount;
        this.totalItemCount=totalItemCount;

    }

    public void setInterface(ILoadListener iLoadListener){
        this.loadinterface=iLoadListener;
    }
    //加载更多数据的回调接口
    public interface ILoadListener{
        public void onLoad();
    }
}
