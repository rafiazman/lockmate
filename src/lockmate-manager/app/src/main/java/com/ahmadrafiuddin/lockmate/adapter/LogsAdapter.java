package com.ahmadrafiuddin.lockmate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmadrafiuddin.lockmate.R;
import com.ahmadrafiuddin.lockmate.model.AccessLog;

import java.util.List;

/**
 * Created by T430 on 16/5/2017.
 */

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {
    private Context context;
    private List<AccessLog> records;

    public LogsAdapter(Context context, List<AccessLog> records) {
        this.context = context;
        this.records = records;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_log_list, parent, false);

        return new LogsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(records.get(position).getStudentName());
        holder.matricNumber.setText(records.get(position).getMatricNo());
        holder.scanTime.setText(records.get(position).getScan_dateTime());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private TextView matricNumber;
        private TextView scanTime;
        private View container;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.img_log_avatar);
            name = (TextView) itemView.findViewById(R.id.lbl_log_name);
            matricNumber = (TextView) itemView.findViewById(R.id.lbl_log_matric_num);
            scanTime = (TextView) itemView.findViewById(R.id.lbl_log_scantime);
            container = itemView.findViewById(R.id.cont_log_root);
        }
    }
}
