package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Homework";
    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper todoDbHelper = null;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todoDbHelper = new TodoDbHelper(MainActivity.this);
        sqLiteDatabase = todoDbHelper.getWritableDatabase();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        String[] todos = {
                BaseColumns._ID,
                TodoContract.ToDoList.DATE,
                TodoContract.ToDoList.STATE,
                TodoContract.ToDoList.CONTENT,
                TodoContract.ToDoList.LEVEL
        };

        String order = TodoContract.ToDoList.LEVEL + " DESC, " +
                TodoContract.ToDoList.DATE + " DESC";

        Cursor cursor = sqLiteDatabase.query(
                TodoContract.ToDoList.TABLE_NAME,
                todos,
                null,
                null,
                null,
                null,
                order
        );

        List<Note> notes = new ArrayList<>();
        while(cursor.moveToNext()) {
            try{
                long noteId = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.ToDoList._ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(TodoContract.ToDoList.DATE));
                int state = cursor.getInt(cursor.getColumnIndexOrThrow(TodoContract.ToDoList.STATE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(TodoContract.ToDoList.CONTENT));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(TodoContract.ToDoList.LEVEL));
                Note tempNote = new Note(noteId);
                tempNote.setDate(StrToDate(date));
                tempNote.setState(State.from(state));
                tempNote.setContent(content);
                tempNote.setLevel(level);
                notes.add(tempNote);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }
        return notes;
    }

    private Date StrToDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date date = dateFormat.parse(dateStr);
        return date;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        String selection = TodoContract.ToDoList._ID + " = ? ";
        String noteId = "" + note.id;
        String[] selectionArgs = {noteId};
        int count = sqLiteDatabase.delete(TodoContract.ToDoList.TABLE_NAME, selection, selectionArgs);
        if(count > 0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private void updateNode(Note note) {
        // TODO 更新数据
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoContract.ToDoList.STATE, note.getState().intValue);

        String selection = TodoContract.ToDoList._ID + " = ?";
        String noteId = "" + note.id;
        String[] selectionArgs = {noteId};

        int count = sqLiteDatabase.update(
                TodoContract.ToDoList.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
        );
        if(count > 0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }
}
