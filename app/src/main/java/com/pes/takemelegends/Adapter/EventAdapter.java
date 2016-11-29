package com.pes.takemelegends.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pes.takemelegends.Activity.EventDetailsActivity;
import com.pes.takemelegends.Activity.PreferencesActivity;
import com.pes.takemelegends.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Oscar on 22/10/2016.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<String[]> itemsData;
    private Context context;
    private final String imageUrl = "http://www.hutterites.org/wp-content/uploads/2012/03/placeholder.jpg";

    public EventAdapter(List<String[]> itemsData, Context context) {
        this.itemsData = itemsData;
        this.context = context;
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.typeTV.setText(itemsData.get(position)[0]);
        viewHolder.titleTV.setText(itemsData.get(position)[1]);
        viewHolder.location.setText(itemsData.get(position)[2]);
        viewHolder.date.setText(itemsData.get(position)[3]);
        Picasso.with(context).load(imageUrl).into(viewHolder.eventImage);

        Spannable spannable = new SpannableString("1240"+"\n"+"asistentes");
        //spannable.setSpan(new RelativeSizeSpan(1.2f), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.6f), 5, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.asistentsBtn.setText(spannable);

        Spannable spannable2 = new SpannableString("696"+"\n"+"takes");
        //spannable2.setSpan(new RelativeSizeSpan(1.2f), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable2.setSpan(new RelativeSizeSpan(0.6f), 4, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.takesBtn.setText(spannable2);
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView typeTV, titleTV, takesBtn, asistentsBtn, location, date;
        ImageButton shareBtn;
        ImageView eventImage;
        private final Context context;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            context = itemLayoutView.getContext();

            typeTV = (TextView) itemLayoutView.findViewById(R.id.type);
            titleTV = (TextView) itemLayoutView.findViewById(R.id.title);
            takesBtn = (TextView) itemLayoutView.findViewById(R.id.takesBtn);
            asistentsBtn = (TextView) itemLayoutView.findViewById(R.id.asistentsBtn);
            shareBtn = (ImageButton) itemLayoutView.findViewById(R.id.shareBtn);
            location = (TextView) itemLayoutView.findViewById(R.id.location);
            date = (TextView) itemLayoutView.findViewById(R.id.date);
            eventImage = (ImageView) itemLayoutView.findViewById(R.id.imageEvent);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.v("asdfasdf", String.valueOf(getLayoutPosition()));
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("id", getLayoutPosition());
            context.startActivity(intent);
        }
    }
}
