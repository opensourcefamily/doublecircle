package com.studyinghome.doublecircle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 重写relativeLayout
 *
 * @author Leslie
 * @email panxiang_work@163.com
 * @create 2019-10-30 11:46
 */
public class SuitRelativeLayout extends RelativeLayout {
    private Context mContext;
    private boolean isMeasured = false;

    public SuitRelativeLayout(Context context) {
        super(context);
        mContext = context;
        isMeasured = false;
    }

    public SuitRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //这个地方之所以要初始化，主要是考虑到如果屏幕旋转，那么要重新measure，不然会就达不到效果了。
        isMeasured = false;
    }

    public SuitRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        isMeasured = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("wang", widthMeasureSpec + "  onMeasure->" + heightMeasureSpec);

        if (!isMeasured) {
            SuitUITool uiTool = SuitUITool.getInstance(mContext);
            for (int i = 0; i < this.getChildCount(); i++) {
                View child = this.getChildAt(i);
                //获取孩子view的布局属性
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (child.getLayoutParams());
                //真实的宽高
                params.width = (int) (params.width * uiTool.getScale_width());
                params.height = (int) (params.height * uiTool.getScale_height());
                params.leftMargin = (int) (params.leftMargin * uiTool.getScale_width());
                params.rightMargin = (int) (params.rightMargin * uiTool.getScale_width());
                params.topMargin = (int) (params.topMargin * uiTool.getScale_height());
                params.bottomMargin = (int) (params.bottomMargin * uiTool.getScale_height());
                Log.e("wang", params.width + "  height->" + params.height);
            }
            isMeasured = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
