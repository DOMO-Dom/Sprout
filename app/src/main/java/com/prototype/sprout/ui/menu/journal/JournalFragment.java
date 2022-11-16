package com.prototype.sprout.ui.menu.journal;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.prototype.sprout.database.AppDatabase;
import com.prototype.sprout.database.note.Note;
import com.prototype.sprout.database.note.NoteViewModel;
import com.prototype.sprout.databinding.FragmentJournalBinding;
import com.prototype.sprout.ui.menu.journal.adapter.NoteAdapter;

import java.util.List;

public class JournalFragment extends Fragment {

    private FragmentJournalBinding binding;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJournalBinding.inflate(inflater, container, false);

        binding.journalRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        NoteViewModel noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        noteList = noteViewModel.getNoteList();

        noteAdapter = new NoteAdapter(noteList);
        binding.journalRecyclerView.setAdapter(noteAdapter);

        noteViewModel.getNoteListLiveData().observe(getViewLifecycleOwner(), notes -> {
            noteAdapter.setNotes(notes);
        });

//        getNotes();
        onBackPress();
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void onBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Do Something
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    /**
     * What is this?
     */
    private void getNotes() {
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return AppDatabase.getDbInstance(requireContext()).noteDao().getAllNoteList();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (noteList.size() == 0) {
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                } else {
                    noteList.add(0, notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                }
                binding.journalRecyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        noteAdapter = null;
        binding = null;
    }
}