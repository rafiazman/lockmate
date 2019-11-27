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
 * Created by X230 on 25/3/2017.
 */

public class LockmateAdapter extends RecyclerView.Adapter<LockmateAdapter.LockmateHolder> {
    private List<QrKey> listOfQrKeys;
    private LayoutInflater inflater;

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public LockmateAdapter(List<QrKey> listOfQrKeys, Context c) {
        this.listOfQrKeys = listOfQrKeys;
        this.inflater = LayoutInflater.from(c);
    }

    @Override
    public LockmateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_user_list, parent, false);
        return new LockmateHolder(view);
    }

    @Override
    public void onBindViewHolder(LockmateHolder lockmateHolder, int pos) {
        QrKey qrKey = listOfQrKeys.get(pos);

        lockmateHolder.name.setText(qrKey.getStudentName());
        lockmateHolder.matricNumber.setText(qrKey.getMatricNo());
        lockmateHolder.avatar.setImageResource(R.drawable.ic_account_circle_black_24dp);
    }

    @Override
    public int getItemCount() {
        return listOfQrKeys.size();
    }

    class LockmateHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView avatar;
        private TextView name;
        private TextView matricNumber;
        private View container;

        public LockmateHolder(View itemView) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.img_avatar);
            name = (TextView) itemView.findViewById(R.id.lbl_name);
            matricNumber = (TextView) itemView.findViewById(R.id.lbl_matric_num);
            container = itemView.findViewById(R.id.cont_user_root);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // If view is user container, do this
            if (v.getId() == R.id.cont_user_root) {
                itemClickCallback.onItemClick(getAdapterPosition());
            }
            else {

            }
        }
    }
}
