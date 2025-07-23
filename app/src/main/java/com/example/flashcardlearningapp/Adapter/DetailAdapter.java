package com.example.flashcardlearningapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashcardlearningapp.Model.FlashcardContent;
import com.example.flashcardlearningapp.R;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    private List<FlashcardContent> data;

    public void setData(List<FlashcardContent> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flashcard_pair, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data != null && position < data.size()) {
            FlashcardContent content = data.get(position);
            holder.tvPairQuestion.setText("Q: " + content.getQuestion());
            holder.tvPairAnswer.setText("A: " + content.getAnswer());
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPairQuestion, tvPairAnswer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPairQuestion = itemView.findViewById(R.id.tvPairQuestion);
            tvPairAnswer = itemView.findViewById(R.id.tvPairAnswer);
        }
    }
}