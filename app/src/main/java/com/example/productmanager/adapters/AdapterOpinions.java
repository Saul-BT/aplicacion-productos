package com.example.productmanager.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productmanager.MainActivity;
import com.example.productmanager.ProductDetailsFragment;
import com.example.productmanager.R;
import com.example.productmanager.model.Opinion;

import java.util.List;

public class AdapterOpinions extends RecyclerView.Adapter<AdapterOpinions.OpinionsHolder> {
    private int redId;
    private Context ctx;
    public List<Opinion> opinions;

    public AdapterOpinions(Context ctx, int redId, List<Opinion> opinions) {
        this.redId = redId;
        this.ctx = ctx;
        this.opinions = opinions;
    }

    @NonNull
    @Override
    public OpinionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OpinionsHolder holder = null;

        try {
            View view = ((AppCompatActivity) ctx).getLayoutInflater()
                    .inflate(this.redId, parent, false);
            holder = new OpinionsHolder(view);
        }
        catch (Exception e) {
            Log.i("Informacion", e.getMessage());
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OpinionsHolder holder, int position) {
        holder.opinion = opinions.get(position);

        holder.tvDate.setText(holder.opinion.getDateFormatted());
        holder.tvAuthor.setText(holder.opinion.getAuthor());
        holder.tvMessage.setText(holder.opinion.getMessage());

        boolean isCurrentUser = holder.opinion.getAuthor()
                .equals(MainActivity.currentUser.getUsername());
        int newBackColor = ctx.getResources().getColor(isCurrentUser
                                                        ? R.color.messageMineBackColor
                                                        : R.color.messageBackColor);
        GradientDrawable bg = (GradientDrawable) holder.opinionContainer.getBackground();

        bg.setColor(newBackColor);
    }

    public void setOpinions(List<Opinion> opinions) {
        this.opinions = opinions;
    }

    @Override
    public int getItemCount() {
        return this.opinions.size();
    }

    class OpinionsHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvDate;
        private TextView tvAuthor;
        private TextView tvMessage;
        private ConstraintLayout opinionContainer;
        private Opinion opinion;

        public OpinionsHolder(@NonNull View opinionView) {
            super(opinionView);
            this.view = opinionView;
            this.tvDate = opinionView.findViewById(R.id.tv_opinion_date);
            this.tvAuthor = opinionView.findViewById(R.id.tv_opinion_author);
            this.tvMessage = opinionView.findViewById(R.id.tv_opinion_message);
            this.opinionContainer = opinionView.findViewById(R.id.opinion_container);
        }
    }
}
