package com.szjoin.joinmapmodule.decoration;

import android.content.Context;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.szjoin.joinmapmodule.R;
import com.szjoin.joinmapmodule.bean.JoinCityBean;

import java.util.List;


public class JoinSectionItemDecoration extends RecyclerView.ItemDecoration {
    private final String TAG = getClass().getSimpleName();

    private List<JoinCityBean> mData;
    private Paint mBgPaint;
    private TextPaint mTextPaint;
    private Rect mBounds;

    private int mSectionHeight;
    private int mBgColor;
    private int mTextColor;
    private int mTextSize;

    private float dividerHeight;
    private Paint mPaint_divider_head;

    public JoinSectionItemDecoration(Context context, List<JoinCityBean> data) {
        this.mData = data;

        mBgColor = context.getResources().getColor(R.color.cp_color_section_bg_back);
        mSectionHeight = context.getResources().getDimensionPixelSize(R.dimen.cp_section_height);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.cp_section_text_size);
        mTextColor = context.getResources().getColor(R.color.cp_color_black);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBgColor);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mBounds = new Rect();
        mPaint_divider_head = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_divider_head.setColor(context.getResources().getColor(R.color.cp_color_section_bg));
        dividerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.getResources().getDisplayMetrics());
    }

    public void setData(List<JoinCityBean> data) {
        this.mData = data;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (mData != null && !mData.isEmpty() && position <= mData.size() - 1 && position > -1) {
                if (position == 0) {
                    drawSection(c, left, right, child, params, position);
                } else {
                    if (null != mData.get(position).getSection()
                            && !mData.get(position).getSection().equals(mData.get(position - 1).getSection())) {
                        drawSection(c, left, right, child, params, position);
                    }
                }
            }
        }
    }

    private void drawSection(Canvas c, int left, int right, View child,
                             RecyclerView.LayoutParams params, int position) {
        // 画头部 “热门城市”“定位城市”“A”的背景部分
        c.drawRect(left,
                child.getTop() - params.topMargin - mSectionHeight,
                right,
                child.getTop() - params.topMargin, mBgPaint);
        // 字母“A”的背景部分下方的分割线
        if (!mData.get(position).getSection().equals("定位城市") && !mData.get(position).getSection().equals("热门城市")) {
            c.drawRect(left + 40, child.getTop() - params.topMargin, right - 120,
                    child.getTop() - params.topMargin + dividerHeight, mPaint_divider_head);
        }
        mTextPaint.getTextBounds(mData.get(position).getSection(),
                0,
                mData.get(position).getSection().length(),
                mBounds);
        // 画头部 “热门城市”“定位城市”“A”的文字
        c.drawText(mData.get(position).getSection(),
                child.getPaddingLeft(),
                child.getTop() - params.topMargin - (mSectionHeight / 2 - mBounds.height() / 2),
                mTextPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        if (pos < 0) return;
        if (mData == null || mData.isEmpty()) return;
        String section = mData.get(pos).getSection();
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;

        boolean flag = false;
        if ((pos + 1) < mData.size()) {
            if (null != section && !section.equals(mData.get(pos + 1).getSection())) {
                if (child.getHeight() + child.getTop() < mSectionHeight) {
                    c.save();
                    flag = true;
                    c.translate(0, child.getHeight() + child.getTop() - mSectionHeight);
                }
            }
        }

        // 背景
        c.drawRect(parent.getPaddingLeft(),
                parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(),
                parent.getPaddingTop() + mSectionHeight + dividerHeight, mBgPaint);
        // 下横线
        if (!section.equals("定位城市")) {
            c.drawRect(parent.getPaddingLeft() + 40,
                    parent.getPaddingTop() + mSectionHeight,
                    parent.getRight() - parent.getPaddingRight(),
                    parent.getPaddingTop() + mSectionHeight + dividerHeight, mPaint_divider_head);
        }
        // 文字
        mTextPaint.getTextBounds(section, 0, section.length(), mBounds);
        c.drawText(section,
                child.getPaddingLeft(),
                parent.getPaddingTop() + mSectionHeight - (mSectionHeight / 2 - mBounds.height() / 2),
                mTextPaint);
        if (flag)
            c.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (mData != null && !mData.isEmpty() && position <= mData.size() - 1 && position > -1) {
            if (position == 0) {
                outRect.set(0, mSectionHeight, 0, 0);
            } else {
                if (null != mData.get(position).getSection()
                        && !mData.get(position).getSection().equals(mData.get(position - 1).getSection())) {
                    outRect.set(0, mSectionHeight, 0, 0);
                }
            }
        }
    }

}
