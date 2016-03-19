package com.alisher.work.newtask;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/5/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    private final Context ctx;
    List<Category> mItems;

    public CategoryAdapter(Context context) {
        mItems = new ArrayList<>();
        ctx = context;
    }

    public void setCategories(List<Category> list){
        mItems.clear();
        mItems.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Category nature = mItems.get(i);
        viewHolder.title.setText(nature.getName());
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
            img = (ImageView) itemView.findViewById(R.id.category_image);
            desc = (TextView) itemView.findViewById(R.id.category_desc);
            title = (TextView) itemView.findViewById(R.id.category_title);
        }
    }
}
