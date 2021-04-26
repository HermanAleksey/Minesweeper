package com.example.sapper.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sapper.R;
import com.example.sapper.model.dto.RoomDTO;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class RoomListAdapter extends ArrayAdapter<RoomDTO> implements View.OnClickListener {

    private ArrayList<RoomDTO> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvId;
        TextView tvPlayer_1;
        TextView tvPlayer_2;
    }

    public RoomListAdapter(ArrayList<RoomDTO> data, Context context) {
        super(context, R.layout.rooms_list_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {
        /** -------------------------------------- FOR onClick on inner views---------------------------------------------------------**/
        if (v.getId() == R.id.tv_rooms_list_item_id) {
            Snackbar.make(v, "Release date ", Snackbar.LENGTH_LONG)
                    .setAction("No action", null).show();
        }
    }

    private int lastPosition = -1;

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RoomDTO dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rooms_list_item, parent, false);
            viewHolder.tvId = convertView.findViewById(R.id.tv_rooms_list_item_id);
            viewHolder.tvPlayer_1 = convertView.findViewById(R.id.tv_rooms_list_item_player_1);
            viewHolder.tvPlayer_2 = convertView.findViewById(R.id.tv_rooms_list_item_player_2);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.tvId.setText("" + dataModel.getId());
        viewHolder.tvPlayer_1.setText(""+dataModel.getPlayer_1().getUsername());
        viewHolder.tvPlayer_2.setText(""+dataModel.getPlayer_2().getUsername());
        viewHolder.tvId.setOnClickListener(this);
        // Return the completed view to render on screen
        return convertView;
    }
}