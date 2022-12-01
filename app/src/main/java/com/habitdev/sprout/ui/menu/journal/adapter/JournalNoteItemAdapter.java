package com.habitdev.sprout.ui.menu.journal.adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.habitdev.sprout.R;
import com.habitdev.sprout.database.note.model.Note;
import com.habitdev.sprout.enums.AppColor;
import com.habitdev.sprout.interfaces.IRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class JournalNoteItemAdapter extends RecyclerView.Adapter<JournalNoteItemAdapter.NoteViewHolder> {

    private List<Note> notes;
    private final IRecyclerView IRecyclerView;

    //for searchnote
    private Timer timer;
    private List<Note> notesSource;

    public JournalNoteItemAdapter(List<Note> notes, IRecyclerView IRecyclerView) {
        this.notes = notes;
        this.IRecyclerView = IRecyclerView;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_journal_parent_note_item, parent, false), IRecyclerView
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bindNote(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateNotes(List<Note> notes) {
        this.notes = notes;
        this.notesSource = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle, noteSubtitle, noteContent, noteDateTime;
        CardView layout_note;
        Drawable cloud, amethyst, sunflower, nephritis, bright_sky_blue, alzarin;
        ColorStateList cs_cloud, cs_amethyst, cs_sunflower, cs_nephritis, cs_bright_sky_blue, cs_alzarin;


        public NoteViewHolder(@NonNull View itemView, IRecyclerView IRecyclerView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteSubtitle = itemView.findViewById(R.id.noteSubtitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            noteDateTime = itemView.findViewById(R.id.noteDateTime);
            layout_note = itemView.findViewById(R.id.layout_note);

            itemView.setOnClickListener(v -> {
                if(IRecyclerView != null){
                    int position = getBindingAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        IRecyclerView.onItemClick(position);
                    }
                }
            });

            cloud = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_item_cloud);
            amethyst = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_item_amethyst);
            sunflower = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_item_sunflower);
            nephritis = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_item_nephritis);
            bright_sky_blue = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_item_brightsky_blue);
            alzarin = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_item_alzarin);

            cs_cloud = ContextCompat.getColorStateList(itemView.getContext(), R.color.CLOUDS);
            cs_amethyst = ContextCompat.getColorStateList(itemView.getContext(), R.color.AMETHYST);
            cs_sunflower = ContextCompat.getColorStateList(itemView.getContext(), R.color.SUNFLOWER);
            cs_nephritis = ContextCompat.getColorStateList(itemView.getContext(), R.color.NEPHRITIS);
            cs_bright_sky_blue = ContextCompat.getColorStateList(itemView.getContext(), R.color.BRIGHT_SKY_BLUE);
            cs_alzarin = ContextCompat.getColorStateList(itemView.getContext(), R.color.ALIZARIN);
        }

        void bindNote(Note note) {
            noteTitle.setText(note.getTitle());

            if (note.getSubtitle().trim().isEmpty()) {
                noteSubtitle.setVisibility(View.GONE);
            } else {
                noteSubtitle.setText(note.getSubtitle());
            }

            noteContent.setText(note.getNoteContent());
            noteDateTime.setText(note.getDateTime());

            if (note.getColor().equals(AppColor.ALZARIN.getColor())){
                layout_note.setBackground(alzarin);
                layout_note.setBackgroundTintList(cs_alzarin);
            } else if (note.getColor().equals(AppColor.AMETHYST.getColor())){
                layout_note.setBackground(amethyst);
                layout_note.setBackgroundTintList(cs_amethyst);
            } else if (note.getColor().equals(AppColor.BRIGHT_SKY_BLUE.getColor())){
                layout_note.setBackground(bright_sky_blue);
                layout_note.setBackgroundTintList(cs_bright_sky_blue);
            } else if (note.getColor().equals(AppColor.NEPHRITIS.getColor())){
                layout_note.setBackground(nephritis);
                layout_note.setBackgroundTintList(cs_nephritis);
            } else if (note.getColor().equals(AppColor.SUNFLOWER.getColor())){
                layout_note.setBackground(sunflower);
                layout_note.setBackgroundTintList(cs_sunflower);
            } else {
                layout_note.setBackground(cloud);
                layout_note.setBackgroundTintList(cs_cloud);
            }
        }
    }

    public void searchNote(final String keyword){
       timer = new Timer();
       timer.schedule(new TimerTask() {
           @Override
           public void run() {
               if(keyword.trim().isEmpty()){
                   notes = notesSource;
               } else {
                   ArrayList<Note> tempNote = new ArrayList<>();
                   for (Note note: notesSource){
                       String searchKeyword = keyword.toLowerCase();
                       if (note.getTitle().toLowerCase().contains(searchKeyword) ||
                           note.getSubtitle().toLowerCase().contains(searchKeyword) ||
                           note.getNoteContent().toLowerCase().contains(searchKeyword) ||
                           note.getDateTime().toLowerCase().contains(searchKeyword)){
                            tempNote.add(note);
                       }
                   }
                   notes = tempNote;
               }
               new Handler(Looper.getMainLooper()).post(new Runnable() {
                   @Override
                   public void run() {
                       notifyDataSetChanged();
                   }
               });
           }
       }, 500);
    }
    public void cancelTimer(){
        if (timer != null) timer.cancel();
    }
}
