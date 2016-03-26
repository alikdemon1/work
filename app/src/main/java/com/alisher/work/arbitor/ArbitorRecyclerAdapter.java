package com.alisher.work.arbitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/5/2016.
 */
public class ArbitorRecyclerAdapter extends RecyclerView.Adapter<ArbitorRecyclerAdapter.ViewHolder>{
    private final Context ctx;
    List<Task> mItems;

    public ArbitorRecyclerAdapter(Context context) {
        mItems = new ArrayList<>();
        ctx = context;
    }

    public void setTasks(List<Task> list){
        mItems.clear();
        mItems.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.arbitor_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Task nature = mItems.get(i);
        viewHolder.title.setText(nature.getTitle());
        viewHolder.desc.setText(nature.getDesc());
        viewHolder.img.setImageBitmap(nature.getImage());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView title;
        public TextView desc;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.category_imageArb);
            desc = (TextView) itemView.findViewById(R.id.category_descArb);
            title = (TextView) itemView.findViewById(R.id.category_titleArb);
        }
    }
}
