package dev.journey.auotodismiss.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import dev.journey.autodismiss.R;

/**
 * gwj
 * 自定义控件  实现自动消失 控件 回收
 */

public class MessagQuenView extends ViewGroup {
    private Context context;

    public MessagQuenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MessagQuenView(Context context) {
        super(context);
    }

    /*
     * 回收线程， 每隔一段时间去检查过期的View 触发回收事件
     */
    private ScheduledExecutorService mservice = Executors.newSingleThreadScheduledExecutor();

    private void setSchedule() {

        if (mservice.isShutdown()) {
            mservice = Executors.newSingleThreadScheduledExecutor();
        }
        mservice.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                schedualRemove();
            }
        }, 0, 1200, TimeUnit.MILLISECONDS);
    }

    public void shutDownSchedulSeervice() {
        mservice.shutdownNow();
    }

    private void init() {
        setSchedule();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        // setMeasuredDimension(measureSize(widthMeasureSpec),
        // measureSize(heightMeasureSpec));

        setMeasuredDimension(measureWidthSize(widthMeasureSpec), measureSize(heightMeasureSpec));
    }

    private int measureWidthSize(int widthMeasureSpec) {
        int result = 0; // 结果
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST: // 子容器可以是声明大小内的任意大小
                result = specSize;
                break;
            case MeasureSpec.EXACTLY: // 父容器已经为子容器设置了尺寸,子容器应当服从这些边界,不论子容器想要多大的空间.
                // 比如EditTextView中的DrawLeft
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED: // 父容器对于子容器没有任何限制,子容器想要多大就多大.
                // 所以完全取决于子view的大小
                result = 1500;
                break;
            default:
                break;
        }
        return result * 2 / 3;
    }

    private int measureSize(int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int result = 0; // 结果
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST: // 子容器可以是声明大小内的任意大小
                result = specSize;
                break;
            case MeasureSpec.EXACTLY: // 父容器已经为子容器设置了尺寸,子容器应当服从这些边界,不论子容器想要多大的空间.
                // 比如EditTextView中的DrawLeft
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED: // 父容器对于子容器没有任何限制,子容器想要多大就多大.
                // 所以完全取决于子view的大小
                result = 1500;
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /*
         * 设置 左右的 padding
		 */
        l = l + getPaddingLeft();
        r = r - getPaddingRight();
        t = t + getPaddingTop();
        b = b - getPaddingBottom();

        Stack<View> drawChild = new Stack<View>();
        drawChild.clear();
        for (Iterator<View> itrator = childQueue.iterator(); itrator.hasNext(); ) {
            View child = itrator.next();
            drawChild.add(child);
        }
        // 设置 绘制最顶层的高度
        int dHeight = b;
        while (drawChild.size() >= 1) {
            View child = drawChild.pop();
            child.layout(l, dHeight - child.getMeasuredHeight(), r, dHeight);
            dHeight = dHeight - child.getHeight();
        }
    }

    /*
     * 	所有子View的List容器
     */
    private List<View> childQueue = new ArrayList<View>();

    /*
     * 通知View 移除控件操作
     */
    private final Handler handlerAnim = new Handler() {
        public void handleMessage(Message msg) {
            removeView((View) msg.obj);
            childQueue.remove((View) msg.obj);

        }

        ;
    };

    /*
     * 通知View 移除控件操作
     */
    private final Handler handlerCallRemove = new Handler() {
        public void handleMessage(Message msg) {

            final View child = (View) msg.obj;
            child.setVisibility(View.VISIBLE);

            Animation anim = AnimationUtils.loadAnimation(context, R.anim.live_message_out);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Message msg = handlerAnim.obtainMessage();
                    msg.obj = child;
                    handlerAnim.sendMessage(msg);
                }
            });
            child.startAnimation(anim);

        }

        ;
    };

    public void schedualRemove() {

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child != null) {
                ItemTag tag = (ItemTag) child.getTag();
                if (!tag.isMove) {
                    int delt = (int) ((System.currentTimeMillis() - tag.time) / 1000);
                    if (delt > 2) {
                        // 重新设置 tag 标签
                        tag.isMove = true;
                        child.setTag(tag);

                        Message msg = handlerCallRemove.obtainMessage();
                        msg.obj = child;
                        handlerCallRemove.sendMessage(msg);
                    }
                } else {
                    // tag 设置 wei true

                }

            }
        }

    }

    // 添加View 控件
    public void addNewView(View childv) {

        ItemTag tag = new ItemTag();
        tag.time = System.currentTimeMillis();
        childv.setTag(tag);
        childQueue.add(childv);
        //		Animation anim = AnimationUtils.loadAnimation(context, R.anim.live_im_msgv_scal_in);
        //		childv.startAnimation(anim);
        this.addView(childv);
    }

    /*
     * View的tag标记
     */
    class ItemTag {
        public long time;
        public boolean isMove = false;
    }

}
