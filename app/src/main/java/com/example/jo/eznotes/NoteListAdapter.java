package com.example.jo.eznotes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ExampleViewHolder> {

    private ArrayList<Note> notesList;
    public ExampleViewHolder evh;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public EditText notes;
        public MovementMethod movementMethod;
        public KeyListener keyListener;

        public TextView tvPreview;

        public Button deleteNote;

        public LinearLayout linearLayout;
        public RelativeLayout relativeLayout;
        public Button saveButton;
        public Button doneSaveButton;

        public TextView tvDatum;

        public Note currentNote;

        public View view;

        String oldNote;
        int oldLineNote = 0;

        NoteListAdapter nla;


        public ExampleViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            notes = (EditText) itemView.findViewById(R.id.myNote);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.llayout);
            saveButton = (Button) itemView.findViewById(R.id.saveButton);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rlayout);
            tvPreview = (TextView) itemView.findViewById(R.id.tvPreview);
            deleteNote = (Button) itemView.findViewById(R.id.deleteNote);
            doneSaveButton = (Button) itemView.findViewById(R.id.doneSaveButton);

            tvDatum = (TextView) itemView.findViewById(R.id.tvDatum);


            linearLayout.setOnClickListener(this);

            notes.setOnClickListener(this);
            editTextAus();


            saveButton.setOnClickListener(this);
            doneSaveButton.setOnClickListener(this);
            deleteNote.setOnClickListener(this);

            notes.setInputType(InputType.TYPE_NULL);


        }

        @Override
        public void onClick(View v) {
            if (v.getId() == linearLayout.getId()) {
                if (!currentNote.isExpanded()) {

                    currentNote.setExpanded(true);
                    notes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    notes.setFocusable(false);

                    if (currentNote.getNoteTitel() == "New Note") {
                        notes.setText(currentNote.boldTitle());

                    } else {
                        notes.setText(currentNote.noteWithBoldTitle());
                    }


                    notes.setMaxLines(currentNote.getAnzLine() + 1);
                    notes.setLines(currentNote.getAnzLine() + 1);
                    saveButton.setVisibility(View.VISIBLE);
                    tvPreview.setVisibility(View.INVISIBLE);


                }
                if (currentNote.isExpanded()) {

                    notes.setMovementMethod(movementMethod);
                    notes.setKeyListener(keyListener);
                    notes.setFocusable(true);
                    notes.setFocusableInTouchMode(true);

                    notes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                    notes.setMaxLines(currentNote.getAnzLine() + 2);
                    notes.setLines(currentNote.getAnzLine() + 2);

                    if (notes.getText().toString() != null) {
                        oldNote = notes.getText().toString();
                    }

                    currentNote.setExpanded(false);

                    //LIVE BOLD TEXT
                    notes.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            if (notes.getLineCount() <= notes.getText().toString().split("\r\n|\r|\n").length) {
                                notes.setMaxLines(notes.getLineCount() + 1);
                                notes.setLines(notes.getLineCount() + 1);
                            }

                            if (oldNote.length() < notes.getText().toString().length() || oldNote.length() > notes.getText().toString().length()) {
                                doneSaveButton.setVisibility(View.VISIBLE);

                            } else {
                                doneSaveButton.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {


                        }
                    });

                }


            }

            if (v.getId() == saveButton.getId()) {

                String s = notes.getText().toString();
                Note n = new Note(notes.getText().toString());
                n.setCurrPosition(currentNote.getCurrPosition());
                nla.notesList.set(currentNote.getCurrPosition(), n);

                currentNote = nla.notesList.get(currentNote.getCurrPosition());

                notes.setText(currentNote.boldTitle());
                tvPreview.setText(currentNote.getPreview());



                notes.setMaxLines(1);
                notes.setLines(1);

                saveButton.setVisibility(View.INVISIBLE);

                editTextAus();
                keyboardDown();
                notes.setInputType(InputType.TYPE_NULL);
                currentNote.setExpanded(false);
                tvPreview.setVisibility(View.VISIBLE);
                doneSaveButton.setVisibility(View.GONE);
                MainActivity.getInstance().saveData();

                doneSaveButton.setVisibility(View.GONE);


            }

            if (v.getId() == doneSaveButton.getId()) {
                String s = notes.getText().toString();
                Note n = new Note(notes.getText().toString());
                n.setCurrPosition(currentNote.getCurrPosition());

                nla.notesList.set(currentNote.getCurrPosition(), n);

                currentNote = nla.notesList.get(currentNote.getCurrPosition());

                notes.setText(currentNote.noteWithBoldTitle());
                notes.setMaxLines(currentNote.getAnzLine() + 2);
                notes.setLines(currentNote.getAnzLine() + 2);
                doneSaveButton.setVisibility(View.GONE);

                oldNote = currentNote.getMyNote();

                MainActivity.getInstance().saveData();

                keyboardDown();



                relativeLayout.setFocusable(true);
                relativeLayout.setFocusableInTouchMode(true);


                notes.clearFocus();

                notes.setFocusable(true);
                notes.setFocusableInTouchMode(true);
            }

            if (v.getId() == deleteNote.getId()) {
                Log.i("delete", "button");
                nla.notesList.remove(currentNote);
                nla.notifyDataSetChanged();

            }

            if(v.getId() == notes.getId()){

                if(currentNote.getNoteTitel() == "New Note"){

                    notes.setText("");
                }
            }


        }

        public void editTextAus() {
            movementMethod = notes.getMovementMethod();
            keyListener = notes.getKeyListener();
            notes.setMovementMethod(null);
            notes.setKeyListener(null);

        }



        public void keyboardDown() {

            /*if (imm.isAcceptingText()) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }*/
            final RelativeLayout rootView = (RelativeLayout)MainActivity.getInstance().findViewById(R.id.rlRoot);
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                    InputMethodManager imm = (InputMethodManager) MainActivity.getInstance().getSystemService(Activity.INPUT_METHOD_SERVICE);

                    if(heightDiff > 100){

                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }else{
                    }
                }
            });



        }



    }

    //---------------------

    public NoteListAdapter(ArrayList<Note> noteList) {
        notesList = noteList;

    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_view_layout, viewGroup, false);


        evh = new ExampleViewHolder(v);


        //new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);

        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ExampleViewHolder exampleViewHolder, int i) {

            exampleViewHolder.setIsRecyclable(false);   // löst das problem, wenn ein item hinzugefügt oder gelöscht wird,
        // dass das item davor oder danach vom gelöschten item(view) nicht die view übernimmt


            exampleViewHolder.nla = this;

            Note note = notesList.get(exampleViewHolder.getAdapterPosition());
            exampleViewHolder.currentNote = note;

            exampleViewHolder.tvDatum.setText(exampleViewHolder.currentNote.getDatum());
            exampleViewHolder.currentNote.setCurrPosition(exampleViewHolder.getAdapterPosition());

            exampleViewHolder.notes.clearFocus();
            exampleViewHolder.notes.setText(note.boldTitle());
            exampleViewHolder.tvPreview.setText(note.getPreview());



    }



    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public ArrayList<Note> getNotesList() {
        return notesList;
    }

    public void setNotesList(ArrayList<Note> notesList) {
        this.notesList = notesList;
    }


}
