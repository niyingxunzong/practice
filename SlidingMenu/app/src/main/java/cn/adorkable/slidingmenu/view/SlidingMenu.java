package cn.adorkable.slidingmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

import cn.adorkable.slidingmenu.R;

/**
 * Created by LiuQiang on 2015/7/9.
 */
public class SlidingMenu extends HorizontalScrollView {

    private LinearLayout mWrapper;    //最外层
    private ViewGroup mMenu;    // 菜单区域
    private ViewGroup mContent;    //内容区域
    private int mScreenWidth;
    private int mMenuWidth;    //menu 的宽度

    private int mMenuRightPadding = 50;    //menu与屏幕右侧的距离默认50dp

    private boolean isChildViewMeasure = false;    //保证子View只测量一次
    private boolean isOpen = false;    //记录目录是否展开

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //读取自定义属性
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyleAttr, 0);

        //获取属性的个数
        int num = typedArray.getIndexCount();
        for(int i=0;i<num;i++){
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.SlidingMenu_right_padding:
                    mMenuRightPadding = typedArray.getDimensionPixelSize(attr,
                            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,
                                    context.getResources().getDisplayMetrics()));
                    break;
                default:
                    break;
            }
        }

        typedArray.recycle();    //记得释放

        //获取屏幕宽度
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;

        /*//dp转换成像素
        mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mMenuRightPadding,
                context.getResources().getDisplayMetrics());*/

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (!isChildViewMeasure) {
            mWrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWrapper.getChildAt(0);
            mContent = (ViewGroup) mWrapper.getChildAt(1);
            //设置memu的宽度
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;

            isChildViewMeasure = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();    //隐藏在左边的宽度
                if (scrollX >= mMenuWidth / 2) {    //隐藏的部分大于menu的1/2
                    //将menu完全隐藏
                    //这个方法可以理解为视窗的的移动
                    //比如下面这个是视窗的左上角移动到（mMenuWidth,0）这个坐标点处
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpen = true;
                }
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    //滚动条向y右走，l变大,其实l是ScrollView左边边缘溢出屏幕左侧的宽度
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //所以当menu展开的时候l是从mMenuWidth---0变化的，也就是在看不到menu的时候menu溢出屏幕左侧了
        float scale = l * 1.0f / mMenuWidth;

        //调用动画，设置菜单的移动动画
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale);
        //
        float leftScale = 1.0f-scale*0.3f;
        float leftAlpha = 0.6f + 0.4f*(1-scale);
        ViewHelper.setScaleX(mMenu,leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu,leftAlpha);

        //设置内容区域的缩放
        float rightScale = 0.7f+0.3f*scale;
        //设置缩放中心点
        ViewHelper.setPivotX(mContent,0);
        ViewHelper.setPivotY(mContent,mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent,rightScale);
        ViewHelper.setScaleY(mContent,rightScale);

        System.out.println(l);
    }

    /**
     * 打开菜单
     */
    public void openMenu(){
        if(isOpen) return;
        this.smoothScrollTo(0,0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu(){
        if(!isOpen)return;
        this.smoothScrollTo(mMenuWidth,0);
        isOpen = false;
    }
}
