package com.example.flashcardlearningapp.Adapter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcardlearningapp.Model.Flashcard;
import com.example.flashcardlearningapp.R;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private List<Flashcard> flashcardList;
    private OnFlashcardClickListener onFlashcardClickListener;
    private OnFlashcardLongClickListener onFlashcardLongClickListener;

    public interface OnFlashcardClickListener {
        void onFlashcardClick(Flashcard flashcard);
    }

    public interface OnFlashcardLongClickListener {
        void onFlashcardLongClick(Flashcard flashcard, View view);
    }

    public FlashcardAdapter(OnFlashcardClickListener clickListener, OnFlashcardLongClickListener longClickListener) {
        this.onFlashcardClickListener = clickListener;
        this.onFlashcardLongClickListener = longClickListener;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        Flashcard flashcard = flashcardList.get(position);
        holder.tvTitle.setText(flashcard.getTitle());
        holder.tvCreationTime.setText(flashcard.getCreationTime());
        holder.tvAccessCount.setText("Access: " + flashcard.getAccessCount());
        holder.tvCreator.setText("Creator: " + flashcard.getCreatorName()); // Bind creator name

        holder.itemView.setOnClickListener(v -> {
            if (onFlashcardClickListener != null) {
                onFlashcardClickListener.onFlashcardClick(flashcard);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onFlashcardLongClickListener != null) {
                onFlashcardLongClickListener.onFlashcardLongClick(flashcard, v);
                return true;
            }
            return false;
        });

        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menu.setHeaderTitle(flashcard.getTitle());
            menu.add(ContextMenu.NONE, 1, ContextMenu.NONE, "Edit");
            menu.add(ContextMenu.NONE, 2, ContextMenu.NONE, "Delete");
        });
    }

    @Override
    public int getItemCount() {
        return flashcardList != null ? flashcardList.size() : 0;
    }

    public void setFlashcardList(List<Flashcard> flashcardList) {
        this.flashcardList = flashcardList;
        notifyDataSetChanged();
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCreationTime, tvAccessCount, tvCreator;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCreationTime = itemView.findViewById(R.id.tvCreationTime);
            tvAccessCount = itemView.findViewById(R.id.tvAccessCount);
            tvCreator = itemView.findViewById(R.id.tvCreator); // Add creator TextView
        }
    }
}