package cc.brainbook.viewpager.viewpagerbanner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

import static cc.brainbook.viewpager.viewpagerbanner.BuildConfig.DEBUG;

/**
 * Description.
 *
 * @author Robert Han
 * @email brainbook.cc@outlook.com
 * @website www.brainbook.cc
 * @time 2016/4/15 21:58
 */
public class ViewPagerBanner extends RelativeLayout {

    private ViewPager mViewPager;

    public ViewPagerBanner(Context context) {
        super(context);
        initView(context);
    }

    public ViewPagerBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ViewPagerBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ViewPagerBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        final View view = View.inflate(context, R.layout.include_viewpager, this);
        mViewPager = view.findViewById(R.id.viewpager);
    }

    public ViewPager getViewPager(){
        return mViewPager;
    }


    private static final int AUTO_PLAY_MESSAGE = 1;
    private boolean mIsAutoPlay = true;
    private int mAutoPlayDelay = 3000;  // ms
    private int mAutoPlayStep = 1;      // forward: 1, backward: -1
    private boolean mAutoPlayEnableTouch = false;

    public boolean getIsAutoPlay(){
        return mIsAutoPlay;
    }
    public ViewPagerBanner setIsAutoPlay(boolean isAutoPlay){
        mIsAutoPlay = isAutoPlay;
        return this;
    }
    public int getAutoPlayDelay(){
        return mAutoPlayStep;
    }
    public ViewPagerBanner setAutoPlayDelay(int delay){
        mAutoPlayDelay = delay;
        return this;
    }
    public int getAutoPlayStep(){
        return mAutoPlayStep;
    }
    public ViewPagerBanner setAutoPlayStep(int step){
        mAutoPlayStep = step;
        return this;
    }

    public void startAutoPlay() {
        mBannerHandler.removeMessages(AUTO_PLAY_MESSAGE);///should stop before start!
        mBannerHandler.sendEmptyMessageDelayed(AUTO_PLAY_MESSAGE, mAutoPlayDelay);
        mIsAutoPlay = true;
        mAutoPlayEnableTouch = true;
    }

    public void stopAutoPlay() {
        mBannerHandler.removeMessages(AUTO_PLAY_MESSAGE);
        mIsAutoPlay = false;
        mAutoPlayEnableTouch = false;
    }

    private BannerHandler mBannerHandler = new BannerHandler(this);

    private static class BannerHandler extends Handler {
        private final WeakReference<ViewPagerBanner> mWeakReference;

        public BannerHandler(ViewPagerBanner viewPagerBanner) {
            mWeakReference = new WeakReference<>(viewPagerBanner);
        }

        @Override
        public void handleMessage(Message msg) {
            if (DEBUG) Log.d("TAG", "======================= handleMessage =======================");
            final ViewPagerBanner viewPagerBanner = mWeakReference.get();
            if (null != viewPagerBanner && viewPagerBanner.mIsAutoPlay) {
                final ViewPager viewPager = viewPagerBanner.getViewPager();
                if(null != viewPager.getAdapter() && 0 < viewPager.getAdapter().getCount()){    ///the number of views is 0

                    final int curPage = viewPager.getCurrentItem();
                    if (DEBUG) Log.d("TAG", "-------------- curPage: " + curPage + " is time up! --------------");
                    if(null != viewPagerBanner.mOnAutoPlayTimeUpListener) {
                        viewPagerBanner.mOnAutoPlayTimeUpListener.onAutoPlayTimeUp(viewPagerBanner, curPage);
                    }

                    if (DEBUG) Log.d("TAG", "handleMessage#setCurrentItem( curPage: " + curPage + ", mAutoPlayStep: " + viewPagerBanner.mAutoPlayStep + " )");
                    viewPager.setCurrentItem(curPage + viewPagerBanner.mAutoPlayStep);

                    ///In the case of `adapter.setCanLoop(false)`, if the view pager try to cross the border,
                    ///the current item will remain unchanged after setCurrentItem(). Judging from this out of bounds.
                    ///Note: In the case of `adapter.setCanLoop(true)` and the number of views is 1,
                    ///the current item will also remain unchanged after setCurrentItem().
                    if(curPage != viewPager.getCurrentItem()) {
                        viewPagerBanner.mBannerHandler.sendEmptyMessageDelayed(AUTO_PLAY_MESSAGE, viewPagerBanner.mAutoPlayDelay);
                    }
                }
            }
        }
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (mAutoPlayEnableTouch) {
                startAutoPlay();
            }
        } else if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            // 停止翻页
            if (mAutoPlayEnableTouch) {
//                stopAutoPlay();   /// Do not use this!
                mBannerHandler.removeMessages(AUTO_PLAY_MESSAGE);
                mIsAutoPlay = false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    ///不必再执行startAutoPlay()来启动轮播了
    @Override
    protected void onAttachedToWindow() {
        if (DEBUG) Log.d("TAG", "onAttachedToWindow: ");
        if (mIsAutoPlay) {
            startAutoPlay();
        }
        super.onAttachedToWindow();
    }

    ///返回退出时立即停止handler！否则以前还会执行两次
    @Override
    protected void onDetachedFromWindow() {
        if (DEBUG) Log.d("TAG", "onDetachedFromWindow: ");
        mBannerHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    /**
     * Listener used to dispatch time up event on auto play interval of the current view.
     */
    public OnAutoPlayTimeUpListener mOnAutoPlayTimeUpListener;

    /**
     * Interface definition for a callback to be invoked when a view pager is on auto play interval of the current view.
     */
    public interface OnAutoPlayTimeUpListener {
        void onAutoPlayTimeUp(@NonNull ViewPagerBanner container, int position);
    }

    /**
     * Register a callback to be invoked when this view pager is on auto play interval of the current view.
     *
     * @param l     The callback that will run
     */
    public void setOnAutoPlayTimeUpListener(@Nullable OnAutoPlayTimeUpListener l) {
        mOnAutoPlayTimeUpListener = l;
    }

}
