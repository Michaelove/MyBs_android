package com.my.design.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.loongfly.mybs.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by Loong Fly on 2018/2/26.
 */

public class ReFlashListView extends ListView implements AbsListView.OnScrollListener {
    View head;
    int firstVisibleItem;//当前第一个可见的item位置
    int headHeight; //顶部布局文件的高度
    boolean isRemark;//标记，当前是在Listview最顶端摁下的;
    int startY;//摁下时的Y值
    int state;//当前的状态
    int scrollState;//当前滚动状态
    final int NONE=0;  //正常状态
    final int PULL=1;  //提示下拉状态
    final int RELEASE=2; //提示释放状态
    final int REFLASHING=3; //刷新状态

    IReFlashListener iReFlashListener;
    public ReFlashListView(Context context) {
        super(context);
        initView(context);
    }

    public ReFlashListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ReFlashListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    public void initView(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);
        head=inflater.inflate(R.layout.header_layout,null);
        measureView(head);
        headHeight=head.getMeasuredHeight();
        topPadding(-headHeight);
        this.addHeaderView(head);
        this.setOnScrollListener(this);
    }
/**
 *
 * 通知父布局。具体需要了解一下
 * */
    private void measureView(View view){
        ViewGroup.LayoutParams p=view.getLayoutParams();
        if(p==null){
            p=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        }
        int width=ViewGroup.getChildMeasureSpec(0,0,p.width);
        int height;
        int tempHeight=p.height;
        if(tempHeight>0){
            height=MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else{
            height=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        }
        view.measure(width,height);
    }
    private void topPadding(int topPadding){
        head.setPadding(head.getPaddingLeft(),topPadding,head.getPaddingRight(),head.getPaddingBottom());
        head.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState=scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem=firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(this.firstVisibleItem==0){
                    isRemark=true;
                    startY=(int)ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMouse(ev);
                break;
            case MotionEvent.ACTION_UP:
                if(state==RELEASE){
                    state=REFLASHING;
                    reflashViewByState();
                    //加载数据
                    iReFlashListener.onReflash();


                }else if(state==PULL){
                    state=NONE;
                    isRemark=false;
                    reflashViewByState();
                }

                break;
        }
        return super.onTouchEvent(ev);
    }

    /*
    * 获取完数据
    * */

    public void reflashComplete(){
        state=NONE;
        isRemark=false;
        reflashViewByState();
        TextView lastupdatetime=(TextView)head.findViewById(R.id.lastupdate_time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月");
        Date date=new Date(System.currentTimeMillis());
        String time=format.format(date);
        lastupdatetime.setText(time);
    }
/*根据当前状态,改变界面显示
* */
    private void reflashViewByState(){
        TextView tip=(TextView)head.findViewById(R.id.tip);
        ImageView arrow=(ImageView) head.findViewById(R.id.arrow);
        ProgressBar progress=(ProgressBar) head.findViewById(R.id.progress);
        RotateAnimation anim=new RotateAnimation(0,180,RotateAnimation.RELATIVE_TO_SELF,0.5f,
                                                                              RotateAnimation.RELATIVE_TO_SELF,0.5f);  //后几个参数为了确定旋转中心
        RotateAnimation anim1=new RotateAnimation(180,0,RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);

        anim.setDuration(500);
        anim.setFillAfter(true);
        anim1.setDuration(500);
        anim1.setFillAfter(true);
        switch(state){
            case NONE:
                arrow.clearAnimation();
                topPadding(-headHeight);
                break;
            case PULL:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("下拉刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);
                break;
            case RELEASE:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("松开刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case REFLASHING:
                topPadding(60);
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }

    /**
     * 刷新数据,接口回调
     */
    public interface IReFlashListener{
        public void onReflash();
    }

    public void setInterface(IReFlashListener iReFlashListener){
        this.iReFlashListener=iReFlashListener;
    }
    /*
    * 判断移动过程操作
    * */
    private  void onMouse(MotionEvent ev){
        if(!isRemark){
            return;
        }
        int tempY=(int)ev.getY();
        int space=tempY-startY;
        int topPadding=space-headHeight;
        switch (state){
            case NONE:
                if(space>0){
                    state=PULL;
                    reflashViewByState();
                }
                break;
            case PULL:
                topPadding(topPadding);
                if((space>headHeight+10) && scrollState==SCROLL_STATE_TOUCH_SCROLL){
                    state=RELEASE;
                    reflashViewByState();
                }
                break;
            case RELEASE:
                topPadding(topPadding);
                if((space<headHeight+10) ) {
                    state=PULL;
                    reflashViewByState();

                }else if(space<=0){
                    state=NONE;
                    isRemark=false;
                    reflashViewByState();
                }
                break;
            case REFLASHING:
                break;
        }
    }
}
