package com.szjoin.joinmapmodule.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.szjoin.joinmapmodule.R;
import com.szjoin.joinmapmodule.bean.JoinCityBean;
import com.szjoin.joinmapmodule.bean.JoinCityHotBean;
import com.szjoin.joinmapmodule.interfaces.JoinInnerListener;


import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/8 21:22
 */
public class JoinGridListAdapter extends RecyclerView.Adapter<JoinGridListAdapter.GridViewHolder> {
    public static final int SPAN_COUNT = 3;

    private Context mContext;
    private List<JoinCityHotBean> mData;
    private JoinInnerListener mInnerListener;

    public JoinGridListAdapter(Context context, List<JoinCityHotBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    private String iconTxt = "热";

    public void setIconTxt(String iconTxt) {
        this.iconTxt = iconTxt;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cp_grid_item_layout, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        final JoinCityBean data = mData.get(pos);
        if (data == null) return;
        //设置item宽高
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;

        int space = mContext.getResources().getDimensionPixelSize(R.dimen.cp_grid_item_space);
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.cp_default_padding);
        int indexBarWidth = mContext.getResources().getDimensionPixelSize(R.dimen.cp_index_bar_width);
        int itemWidth = (screenWidth - padding - space * (SPAN_COUNT - 1) - indexBarWidth) / SPAN_COUNT;
        ViewGroup.LayoutParams lp = holder.container.getLayoutParams();
        lp.width = itemWidth;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.container.setLayoutParams(lp);

        holder.name.setText(data.getName());
        if (data.isHot()) {
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setText(iconTxt);
        } else {
            holder.type.setVisibility(View.GONE);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInnerListener != null) {
                    mInnerListener.dismiss(pos, data);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        TextView name;
        TextView type;

        public GridViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.cp_grid_item_layout);
            name = itemView.findViewById(R.id.cp_gird_item_name);
            type = itemView.findViewById(R.id.cp_gird_item_type);
        }
    }

    public void setInnerListener(JoinInnerListener listener) {
        this.mInnerListener = listener;
    }
}
