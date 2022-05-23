package be.kuleuven.findaset.base;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import be.kuleuven.findaset.R;

public class RVAdapterNormal extends RecyclerView.Adapter<RVAdapterNormal.ViewHolder> {
    private String[] userNames;
    private String[] userTimes;
    private String[] userRankings;

    public RVAdapterNormal(String[] userNames, String[] userTimes, String[] userRankings) {
        this.userNames = userNames;
        this.userTimes = userTimes;
        this.userRankings = userRankings;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_leaderboard, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.tvUserName.setText(userNames[position]);
        viewHolder.tvTimes.setText(userTimes[position]);
        viewHolder.tvRanking.setText(userRankings[position]);

        String ranking = userRankings[position];
        if (ranking.equals("1") || ranking.equals("2") || ranking.equals("3")) {
            viewHolder.layoutBoard.setBackgroundColor(Color.rgb(140, 188, 255));
        } else {
            viewHolder.layoutBoard.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return userNames.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvTimes;
        TextView tvRanking;
        ConstraintLayout layoutBoard;

        public ViewHolder(View view) {
            super(view);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvTimes = (TextView) view.findViewById(R.id.tvTimes);
            tvRanking = (TextView) view.findViewById(R.id.tvRanking);
            layoutBoard = (ConstraintLayout) view.findViewById(R.id.rlContent);
        }
    }
}