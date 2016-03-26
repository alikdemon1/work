package com.alisher.work.admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.models.Perform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/26/2016.
 */
public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder>{
    private final Context ctx;
    List<Perform> mItems;

    public AdminAdapter(Context context) {
        mItems = new ArrayList<>();
        ctx = context;
    }

    public void setPerforms(List<Perform> list){
        mItems.clear();
        mItems.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.admin_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Perform nature = mItems.get(i);
        viewHolder.title.setText(nature.getFirstName() +" "+ nature.getLastName());
        viewHolder.img.setImageResource(nature.getImg());
        viewHolder.ratingBar.setRating(nature.getRating());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView title;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.admin_image);
            title = (TextView) itemView.findViewById(R.id.admin_title);
            ratingBar = (RatingBar) itemView.findViewById(R.id.admin_rating);
        }
    }
}
