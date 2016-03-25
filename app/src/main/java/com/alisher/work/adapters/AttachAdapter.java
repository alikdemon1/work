package com.alisher.work.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.models.Attachment;
import com.alisher.work.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/24/2016.
 */
public class AttachAdapter extends RecyclerView.Adapter<AttachAdapter.ViewHolder> {
    private final Context ctx;
    List<Attachment> mItems;

    public AttachAdapter(Context context, List<Attachment> list) {
        this.mItems = list;
        ctx = context;
    }

    public void setAttachments(List<Attachment> list){
        mItems.clear();
        mItems.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.attach_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Attachment nature = mItems.get(i);
        viewHolder.title.setText(nature.getName());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name_attach);
        }
    }
}
