package com.example.jo.eznotes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainAcitivity";

    EditText et;

    private static MainActivity instance;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManger;

    NoteListAdapter noteListAdapter;

    ArrayList<Note> noteList;

    CustomDialogClass cd;

    Button addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreate: Started.");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = this;

        addNote = (Button) findViewById(R.id.addNote);


        noteList = new ArrayList<>();

        noteList.add(new Note("A"));
        noteList.add(new Note("B"));
        noteList.add(new Note("C"));


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManger = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManger);
        //mRecyclerView.getRecycledViewPool().setMaxRecycledViews(1, 0);


        noteListAdapter = new NoteListAdapter(loadData());
        //noteListAdapter = new NoteListAdapter(noteList);


        mAdapter = noteListAdapter;
        mRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        final RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rlRoot);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

                if(heightDiff > 1000){
                    addNote.setVisibility(View.GONE);
                }else{
                    addNote.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public void addText(View v) {
        //noteListAdapter.getNotesList().add(0, new Note("New Note"));
        /*if (mAdapter.getItemCount() >= 1) {
            //mRecyclerView.removeViewAt(0);
        }*/


        //noteListAdapter.notifyItemInserted(0);
        //noteListAdapter.notifyItemRangeChanged(0, noteListAdapter.getNotesList().size());*/
        //mAdapter.notifyDataSetChanged();
        ((NoteListAdapter) mAdapter).getNotesList().add(0, new Note("New Note"));
        //((NoteListAdapter) mAdapter).notifyItemInserted(0);
        ((NoteListAdapter) mAdapter).notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
            int position_dragged = dragged.getAdapterPosition();
            int position_target = target.getAdapterPosition();

            Collections.swap(((NoteListAdapter) mAdapter).getNotesList(), position_dragged, position_target);
            ((NoteListAdapter) mAdapter).notifyItemMoved(position_dragged, position_target);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            cd = new CustomDialogClass(MainActivity.getInstance(), (NoteListAdapter) mAdapter, viewHolder.getAdapterPosition());
            cd.show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            float newDx = dX;
            if (isCurrentlyActive && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View view = recyclerView.getChildAt(viewHolder.getAdapterPosition());
                EditText et = (EditText) view.findViewById(R.id.myNote);


                if (dX < -540) {
                    et.setBackgroundResource(R.drawable.redborder);
                } else {
                    et.setBackgroundResource(R.drawable.border);
                }

            }

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE || actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                ((NoteListAdapter) mAdapter).evh.notes.clearFocus();
                viewHolder.itemView.setAlpha(0.5f);
            }

            if (!isCurrentlyActive) {
                viewHolder.itemView.setAlpha(1f);
            }


            if(cd != null){
                if(cd.isShowing()){

                }
            }

            super.onChildDraw(c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive);
        }


        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            //return super.getSwipeThreshold(viewHolder);
            return 0.5f;       //25% nach links und es wird gelöscht
        }


    };


    public void saveData() {

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(noteListAdapter.getNotesList());
        editor.putString("notes", json);
        editor.apply();
    }

    public ArrayList<Note> loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("notes", null);
        Type type = new TypeToken<ArrayList<Note>>() {
        }.getType();

        ArrayList<Note> n = gson.fromJson(json, type);


        if (n == null) {
            return new ArrayList<Note>();
        } else {
            return n;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPause", "Pause");
        saveData();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop", "onStop");

    }

    public static MainActivity getInstance() {
        return instance;
    }
}
