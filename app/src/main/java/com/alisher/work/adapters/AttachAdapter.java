package com.alisher.work.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
        String result = nature.getName().substring(nature.getName().lastIndexOf('-') + 1).trim();
        viewHolder.title.setText(result);
        viewHolder.createdAt.setText(DateUtils.getRelativeDateTimeString(ctx, nature
                        .getCreatedAt().getTime(), DateUtils.SECOND_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS, 0));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView createdAt;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name_attach);
            createdAt = (TextView) itemView.findViewById(R.id.createdAt_attach);
        }
    }
}
