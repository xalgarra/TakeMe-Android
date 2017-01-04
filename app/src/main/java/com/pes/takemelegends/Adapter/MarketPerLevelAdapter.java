package com.pes.takemelegends.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pes.takemelegends.Activity.LoginActivity;
import com.pes.takemelegends.Activity.PreferencesActivity;
import com.pes.takemelegends.Activity.RewardsActivity;
import com.pes.takemelegends.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Oscar on 08/11/2016.
 */

public class MarketPerLevelAdapter extends RecyclerView.Adapter<MarketPerLevelAdapter.ViewHolder> {

    private ArrayList<JSONObject> itemsData;

    public MarketPerLevelAdapter(ArrayList<JSONObject> itemsData) {
        this.itemsData = itemsData;
    }

    @Override
    public MarketPerLevelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_per_lvl_row, parent, false);
        MarketPerLevelAdapter.ViewHolder viewHolder = new MarketPerLevelAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MarketPerLevelAdapter.ViewHolder viewHolder, int position) {
        try {
            viewHolder.productName.setText(itemsData.get(position).getString("name"));
            viewHolder.productDesc.setText(itemsData.get(position).getString("description"));
            viewHolder.productTakes.setText(itemsData.get(position).getString("takes"));
            viewHolder.productImage.setImageResource(R.drawable.ic_ticket);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView productName, productDesc, productTakes;
        public ImageView productImage;
        public ImageButton productBtn;
        private final Context context;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            context = itemLayoutView.getContext();
            productName = (TextView) itemLayoutView.findViewById(R.id.productName);
            productDesc = (TextView) itemLayoutView.findViewById(R.id.productDescription);
            productTakes = (TextView) itemLayoutView.findViewById(R.id.productTakes);
            productBtn = (ImageButton) itemLayoutView.findViewById(R.id.productBuy);
            productImage = (ImageView) itemLayoutView.findViewById(R.id.productImage);
            productBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Dialog dialog = new Dialog(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View content =  inflater.inflate(R.layout.reward_dialog, null);
            TextView name = (TextView) content.findViewById(R.id.nameProduct);
            TextView price = (TextView) content.findViewById(R.id.price);
            TextView info = (TextView) content.findViewById(R.id.infoProduct);
            name.setText(productName.getText());
            price.setText(productTakes.getText()+" takes");
            info.setText(productDesc.getText());
            dialog.setContentView(content);

            dialog.show();


        }
    }

}
