package com.example.bluedemo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.inuker.bluetooth.library.search.SearchResult;

import java.util.ArrayList;

/**
 * @author: ZhangMin
 * @date: 2020/4/17 10:37
 * @version: 1.0
 * @desc:
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyTVHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;

    private final ArrayList<SearchResult> mData;

    interface OnItemClickListener {
        void onItemClick(String s);

    }

    OnItemClickListener onItemClickListener;


    public RVAdapter(Context context, ArrayList<SearchResult> data) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = data;
    }

    @Override
    public RVAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RVAdapter.MyTVHolder(mLayoutInflater.inflate(R.layout.rv_txt_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RVAdapter.MyTVHolder holder, final int pos) {
        holder.mTvName.setText(mData.get(pos).getName());
        holder.mTextView.setText(mData.get(pos).getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, DeviceActivity.class);
                intent.putExtra("mac", mData.get(pos).getAddress());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setOnItemclickListener(OnItemClickListener onItemclickListener) {
        this.onItemClickListener = onItemclickListener;
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        TextView mTvName;

        MyTVHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textView);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
