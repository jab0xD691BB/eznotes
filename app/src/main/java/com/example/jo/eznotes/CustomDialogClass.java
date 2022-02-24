package com.example.jo.eznotes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CustomDialogClass extends Dialog implements View.OnClickListener {

    public Activity a;

    public NoteListAdapter noteListAdapter;
    public int position;

    public Button yes, no;

    public TextView tvTitel;


    public boolean result;

    public CustomDialogClass(@NonNull Activity a, NoteListAdapter nla, int position) {
        super(a);
        this.a = a;
        this.noteListAdapter = nla;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.btnYes);
        no = (Button) findViewById(R.id.btnNo);

        tvTitel = (TextView) findViewById(R.id.tvTitle);
        tvTitel.setText("Delete " + noteListAdapter.getNotesList().get(position).getNoteTitel() + "?");

        yes.setOnClickListener(this);
        no.setOnClickListener(this);


        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ((NoteListAdapter) noteListAdapter).notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnYes:
                ((NoteListAdapter) noteListAdapter).getNotesList().remove(position);
                ((NoteListAdapter) noteListAdapter).notifyDataSetChanged();
                break;
            case R.id.btnNo:
                ((NoteListAdapter) noteListAdapter).notifyDataSetChanged();
                break;
            default:

                break;
        }

        dismiss();

    }
}
