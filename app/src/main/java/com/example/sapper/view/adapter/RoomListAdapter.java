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
import com.example.sapper.view.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class RoomListAdapter extends ArrayAdapter<RoomDTO> implements View.OnClickListener {

    private ArrayList<RoomDTO> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tv_id;
        TextView tv_player_1;
        TextView tv_player_2;
        TextView tv_height;
        TextView tv_width;
        TextView tv_mines;
        TextView tv_time_m;
        TextView tv_time_s;
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

            viewHolder.tv_id = convertView.findViewById(R.id.tv_rooms_list_item_id);
            viewHolder.tv_player_1 = convertView.findViewById(R.id.tv_rooms_list_item_player_1);
            viewHolder.tv_player_2 = convertView.findViewById(R.id.tv_rooms_list_item_player_2);
            viewHolder.tv_height = convertView.findViewById(R.id.tv_rooms_list_item_height);
            viewHolder.tv_width = convertView.findViewById(R.id.tv_rooms_list_item_width);
            viewHolder.tv_mines = convertView.findViewById(R.id.tv_rooms_list_item_mines);
            viewHolder.tv_time_m = convertView.findViewById(R.id.tv_rooms_list_item_time_min);
            viewHolder.tv_time_s = convertView.findViewById(R.id.tv_rooms_list_item_time_sec);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.tv_id.setText("" + dataModel.getId());
        String pl1 = "-";
        String pl2 = "-";
        if (dataModel.getPlayer_1().getUsername()!=null){
            pl1 = dataModel.getPlayer_1().getUsername();
        }
        if (dataModel.getPlayer_2().getUsername()!=null){
            pl2 = dataModel.getPlayer_2().getUsername();
        }

        viewHolder.tv_player_1.setText(pl1);
        viewHolder.tv_player_2.setText(pl2);
//        viewHolder.tv_id.setOnClickListener(this);

        viewHolder.tv_height.setText(""+dataModel.getHeight());
        viewHolder.tv_width.setText(""+dataModel.getWidth());
        viewHolder.tv_mines.setText(""+dataModel.getMinesCount());
        viewHolder.tv_time_m.setText(""+dataModel.getTimeMin());
        viewHolder.tv_time_s.setText(""+dataModel.getTimeSec());
        // Return the completed view to render on screen
        return convertView;
    }
}