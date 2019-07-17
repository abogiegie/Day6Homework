package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private TodoDbHelper todoDbHelper = null;
    private RadioGroup radioGroup;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todoDbHelper = new TodoDbHelper(NoteActivity.this);
        sqLiteDatabase = todoDbHelper.getWritableDatabase();
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        radioGroup = findViewById(R.id.radio_group);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(), getSelectLevel());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(todoDbHelper != null){
            todoDbHelper.close();
        }
        super.onDestroy();
    }

    private static String getNowDateStr(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date now = new Date();
        return dateFormat.format(now);
    }

    private boolean saveNote2Database(String content, int level) {
        // TODO 插入一条新数据，返回是否插入成功
        String now = getNowDateStr();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoContract.ToDoList.CONTENT, content);
        contentValues.put(TodoContract.ToDoList.DATE, now);
        contentValues.put(TodoContract.ToDoList.STATE, 0);
        contentValues.put(TodoContract.ToDoList.LEVEL, level);

        long newRowId = sqLiteDatabase.insert(TodoContract.ToDoList.TABLE_NAME, null, contentValues);
        return (newRowId != -1);
    }

    public int getSelectLevel() {
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.btn_high:
                return 3;
            case R.id.btn_medium:
                return 2;
            default:
                return 1;
        }
    }
}
