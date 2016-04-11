package com.alisher.work.arbitor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.chat.utils.Const;

/**
 * Created by Sergey Kompaniyets on 25.03.2016.
 */
public class DescriptionFragment extends Fragment {

    TextView tvN, tvDec, tvCost, tvDate;
    ImageView iv;

    public DescriptionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.desc_fragment, container, false);
        iv = (ImageView) v.findViewById(R.id.fr_img_dec);
        tvN = (TextView) v.findViewById(R.id.fr_name_dec);
        tvDec = (TextView) v.findViewById(R.id.fr_dec_dec);
        tvCost = (TextView) v.findViewById(R.id.fr_cost_dec);
        tvDate = (TextView) v.findViewById(R.id.fr_deadline_dec);

        ArbitorActivity list = (ArbitorActivity)getActivity();
        //tvN.setText(args.getString("title_desc"));
        tvN.setText(list.getMyData().getTitle());
        tvDec.setText(list.getMyData().getDesc());
        iv.setImageBitmap(list.getMyData().getImage());
        tvCost.setText(list.getMyData().getPrice()+" $");
        tvDate.setText(list.getMyData().getEndTime().toString());
//        tvDec.setText(this.getArguments().getString("newTaskDesc"));
//        //iv.setImageBitmap((Bitmap) getArguments().getString("newTaskImage"));
//        tvCost.setText(this.getArguments().getString("newTaskCost")+" $");
//        tvDate.setText(this.getArguments().getString("newTaskDeadline"));
        return v;
    }

    public static DescriptionFragment newInstance(@NonNull final String title) {
        final DescriptionFragment fragment = new DescriptionFragment();
        final Bundle args = new Bundle();
        args.putString("title_desc", title);
        fragment.setArguments(args);

        return fragment;
    }
}
