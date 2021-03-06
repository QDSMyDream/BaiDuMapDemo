package com.szjoin.joinmapmodule.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.szjoin.joinmapmodule.R;
import com.szjoin.joinmapmodule.bean.JoinCityBean;


import java.util.List;

public class JoinDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final String TAG = getClass().getSimpleName();
    private float dividerHeight;
    private float dividerHeight_big;
    private Paint mPaint;
    private Paint mPaint_big;
    private List<JoinCityBean> mData;

    private int mBgColor;

    public JoinDividerItemDecoration(Context context, List<JoinCityBean> data) {
        this.mData = data;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_big = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.cp_color_section_bg) );
        mPaint_big.setColor(context.getResources().getColor(R.color.cp_color_section_bg_deep));
        dividerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.getResources().getDisplayMetrics());
        dividerHeight_big = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = (int) dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight() - 120;

        for (int i = 0; i < childCount - 1; i++) {
            String section = mData.get(i).getSection();
            View view = parent.getChildAt(i);

            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left + 40, top, right, bottom, mPaint);
        }
    }
}
