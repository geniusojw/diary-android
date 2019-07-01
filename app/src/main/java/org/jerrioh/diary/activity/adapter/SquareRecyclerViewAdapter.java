package org.jerrioh.diary.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.pop.PostReadPopActivity;
import org.jerrioh.diary.model.PostIt;

import java.util.List;
import java.util.Random;

public class SquareRecyclerViewAdapter extends RecyclerView.Adapter<SquareRecyclerViewAdapter.SquareViewHolder> {

    private Random random = new Random();
    private List<List<PostIt>> postItData;
    private Context context;

    public class SquareViewHolder extends RecyclerView.ViewHolder {
        public SquareViewHolder(View v) {
            super(v);
        }
    }

    public SquareRecyclerViewAdapter(List<List<PostIt>> postItData, Context context) {
        this.postItData = postItData;
        this.context = context;
    }

    @NonNull
    @Override
    public SquareViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_square, viewGroup, false);
        return new SquareViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SquareViewHolder squareViewHolder, int i) {
        List<PostIt> postIt = postItData.get(i);

        if (postIt.size() > 0) {
            LinearLayout layout = squareViewHolder.itemView.findViewById(R.id.linear_layout_row_square_post1);
            TextView text = squareViewHolder.itemView.findViewById(R.id.text_view_row_square_post1);
            PostIt post = postIt.get(0);
            setUpPost(layout, text, post);
        }

        if (postIt.size() > 1) {
            LinearLayout layout = squareViewHolder.itemView.findViewById(R.id.linear_layout_row_square_post2);
            TextView text = squareViewHolder.itemView.findViewById(R.id.text_view_row_square_post2);
            PostIt post = postIt.get(1);
            setUpPost(layout, text, post);
        }

        if (postIt.size() > 2) {
            LinearLayout layout = squareViewHolder.itemView.findViewById(R.id.linear_layout_row_square_post3);
            TextView text = squareViewHolder.itemView.findViewById(R.id.text_view_row_square_post3);
            PostIt post = postIt.get(2);
            setUpPost(layout, text, post);
        }

    }

    private static final int PADDING_SIZE = 50;

    private void setUpPost(LinearLayout layout, TextView text, PostIt postIt) {
        int paddingLeft = random.nextInt(PADDING_SIZE);
        int paddingRight = PADDING_SIZE - paddingLeft;

        int paddingTop = random.nextInt(PADDING_SIZE);
        int paddingBottom = PADDING_SIZE - paddingTop;

        layout.setVisibility(View.VISIBLE);
        layout.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        text.setText(postIt.getContent() + "\n - " + postIt.getAuthorNickname());

        text.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostReadPopActivity.class);
            intent.putExtra("post", postIt);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postItData.size();
    }
}
