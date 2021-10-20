package com.hienqp.roomdatabasetincodeyoutube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private List<User> mListUser;
    private IClickItemUser iClickItemUser;

    public UserAdapter(IClickItemUser iClickItemUser) {
        this.iClickItemUser = iClickItemUser;
    }

    public void setData(List<User> list) {
        UserAdapter.this.mListUser = list;
        notifyDataSetChanged();
    }

    public interface IClickItemUser {
        void updateUser(User user);
        void deleteUser(User user);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = mListUser.get(position);
        if (user == null) {
            return;
        }

        holder.tvUsername.setText(user.getUsername());
        holder.tvAddress.setText(user.getAddress());

        // set data cho TextView year
        holder.tvYear.setText(user.getYear());

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemUser.updateUser(user);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemUser.deleteUser(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListUser != null) {
            return mListUser.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvAddress;
        private Button btnUpdate;
        private Button btnDelete;

        // khai báo TextView year
        private TextView tvYear;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            btnUpdate = (Button) itemView.findViewById(R.id.btn_update);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);

            // ánh xạ TextView year
            tvYear = (TextView) itemView.findViewById(R.id.tv_year);
        }
    }
}
