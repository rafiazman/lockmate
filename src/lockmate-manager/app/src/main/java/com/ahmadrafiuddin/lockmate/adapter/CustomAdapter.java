package com.ahmadrafiuddin.lockmate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmadrafiuddin.lockmate.R;
import com.ahmadrafiuddin.lockmate.model.QrKey;

import java.util.List;

/**
 * Created by X230 on 26/3/2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private Context context;
    private List<QrKey> listOfKeys;

    public CustomAdapter(Context context, List<QrKey> listOfKeys) {
        this.context = context;
        this.listOfKeys = listOfKeys;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(listOfKeys.get(position).getStudentName());
        holder.matricNumber.setText(listOfKeys.get(position).getMatricNo());
    }

    @Override
    public int getItemCount() {
        return listOfKeys.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private TextView matricNumber;
        private View container;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.img_avatar);
            name = (TextView) itemView.findViewById(R.id.lbl_name);
            matricNumber = (TextView) itemView.findViewById(R.id.lbl_matric_num);
            container = itemView.findViewById(R.id.cont_user_root);
        }
    }
}
