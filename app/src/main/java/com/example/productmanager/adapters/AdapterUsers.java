package com.example.productmanager.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productmanager.MainActivity;
import com.example.productmanager.R;
import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.User;
import com.example.productmanager.model.UserType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.UsersHolder> {
    private int resId;
    private Context ctx;
    private List<User> users;
    private FireManager fm = FireManager.getInstance();

    public AdapterUsers(Context ctx, int resId, List<User> users) {
        this.resId = resId;
        this.users = users;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UsersHolder holder = null;

        try {
            View view = ((AppCompatActivity) ctx).getLayoutInflater()
                    .inflate(this.resId, parent, false);
            holder = new UsersHolder(view);
        }
        catch (Exception e) {
            Log.i("Informacion", e.getMessage());
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersHolder holder, final int position) {
        holder.user = this.users.get(position);
        UserType currentUserType = MainActivity.currentUser.getType();

        if (currentUserType.canManageAdmins) {
            holder.bPromoteToAdmin.setImageDrawable(getShield(holder.user));
            holder.bPromoteToAdmin.setVisibility(View.VISIBLE);
        }

        if (currentUserType.canManageUsers)
            holder.bDeleteUser.setVisibility(View.VISIBLE);

        // Admin can't delete Admins
        if (currentUserType == UserType.ADMIN
                && holder.user.getType() == UserType.ADMIN) {
            holder.bDeleteUser.setVisibility(View.GONE);
        }

        holder.tvUsername.setText(holder.user.getUsername());
        holder.bPromoteToAdmin.setOnClickListener(setUserPermissions(holder));
        holder.bDeleteUser.setOnClickListener(deleteUser(position));
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }


    private Drawable getShield(User user) {
        Drawable shieldDrawable = ctx.getDrawable(R.drawable.ic_shield_gray_30dp);
        if (user.getType() == UserType.ADMIN)
            shieldDrawable = ctx.getDrawable(R.drawable.ic_shield_black_30dp);

        return shieldDrawable;
    }

    private View.OnClickListener setUserPermissions(final UsersHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserType newType = UserType.NORMAL;

                if (holder.user.getType() == UserType.NORMAL) {
                    newType = UserType.ADMIN;
                }

                holder.user.setType(newType);
                holder.bPromoteToAdmin.setImageDrawable(getShield(holder.user));
                fm.setUser(holder.user);
            }
        };
    }

    private View.OnClickListener deleteUser(final int position) {
        final User user = users.get(position);

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference userDocument = fm.dbUsersRef.document(user.getUsername());
                userDocument.delete()
                        .addOnSuccessListener(deleteUserOpinions(userDocument));
                users.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        };
    }

    private OnSuccessListener<Void> deleteUserOpinions(final DocumentReference documentRef) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fm.dbOpinionsRef.whereEqualTo("userRef", documentRef)
                        .get().addOnSuccessListener(deleteOpinions());
            }
        };
    }

    private OnSuccessListener<? super QuerySnapshot> deleteOpinions() {
        return new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                for (DocumentSnapshot document : documents) {
                    document.getReference().delete();
                }
            }
        };
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    class UsersHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvUsername;
        public ImageButton bPromoteToAdmin, bDeleteUser;
        public User user;

        public UsersHolder(@NonNull View userView) {
            super(userView);
            this.view = userView;
            this.tvUsername = userView.findViewById(R.id.tv_user_username);
            this.bPromoteToAdmin = userView.findViewById(R.id.ib_promote_to_admin);
            this.bDeleteUser = userView.findViewById(R.id.ib_delete_user);
        }
    }
}
