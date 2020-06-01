package com.vblitzmaker.appremover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    ArrayList<AppItem> mAppList;
    Context mContext;
    LayoutInflater mInflator;
    OnItemClickListener listener;

    public AppAdapter(Context ctxt,ArrayList<AppItem> appItemList)
    {
        this.mContext = ctxt;
        this.mAppList=appItemList;
        mInflator = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.app_item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppItem item = getItem(position);
        holder.tvAppName.setText(item.getA_name());
        holder.ivAppIcon.setImageDrawable(item.getA_icon());
    }

    @Override
    public int getItemCount() {
        return mAppList.size();
    }

    public AppItem getItem(int i) {
        return mAppList.get(i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAppName;
        ImageView ivAppIcon,ivAppDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
            ivAppDelete = itemView.findViewById(R.id.ivDelete);
            ivAppDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(listener!=null)
            {
                listener.onItemClick(view,getAdapterPosition());
            }
        }
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view,int position);
    }
}
