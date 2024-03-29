package com.pes.takemelegends.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.pes.takemelegends.Activity.RewardsActivity;
import com.pes.takemelegends.Controller.ControllerFactory;
import com.pes.takemelegends.Controller.RewardController;
import com.pes.takemelegends.R;
import com.pes.takemelegends.Utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MarketPerLevelAdapter extends RecyclerView.Adapter<MarketPerLevelAdapter.ViewHolder> {

    private final ArrayList<JSONObject> itemsData;
    private final RewardsActivity activity;

    public MarketPerLevelAdapter(ArrayList<JSONObject> itemsData, RewardsActivity rewardsActivity) {
        this.itemsData = itemsData;
        activity = rewardsActivity;
    }

    @Override
    public MarketPerLevelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_per_lvl_row, parent, false);
        return new ViewHolder(itemLayoutView);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView productName;
        public final TextView productDesc;
        public final TextView productTakes;
        public final ImageView productImage;
        public final ImageButton productBtn;
        private final Context context;
        private final RewardController rewardController;
        private final SharedPreferencesManager shared;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            context = itemLayoutView.getContext();
            shared = new SharedPreferencesManager(context);
            rewardController = ControllerFactory.getInstance().getRewardController();
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
            final View content =  inflater.inflate(R.layout.reward_dialog, null);
            TextView name = (TextView) content.findViewById(R.id.nameProduct);
            TextView price = (TextView) content.findViewById(R.id.price);
            TextView info = (TextView) content.findViewById(R.id.infoProduct);
            Button btnConfirm = (Button) content.findViewById(R.id.btn_confirm);
            Button btnCancel = (Button) content.findViewById(R.id.btn_cancel);
            name.setText(productName.getText());
            price.setText(productTakes.getText()+" takes");
            info.setText(productDesc.getText());
            dialog.setContentView(content);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rewardController.postUserReward(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String takes = response.getJSONObject("purchase").getString("takes_left");
                                shared.setTotalTakes(Integer.valueOf(takes));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    activity.refreshUserInfo();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            dialog.dismiss();
                            new AlertDialog.Builder(context).setTitle("Error")
                                    .setMessage("No tienes suficientes takes.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).create().show();
                        }
                    }, context, productName.getText().toString());
                }
            });
            dialog.show();
        }
    }

}
