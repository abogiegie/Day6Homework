package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static class ToDoList implements BaseColumns {
        public static final String TABLE_NAME = "List";
        public static final String DATE = "Date";
        public static final String STATE = "State";
        public static final String CONTENT = "Content";
        public static final String LEVEL = "Level";
    }

    public static String getSqlCreateList() {
        return SQL_CREATE_LIST;
    }

    public static String getSqlDeleteList() {
        return SQL_DELETE_LIST;
    }

    public static String getSqlAddLevel(){
        return SQL_ADD_LEVEL;
    }

    private static final String SQL_CREATE_LIST =
            "CREATE TABLE " + ToDoList.TABLE_NAME + " (" +
            ToDoList._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ToDoList.DATE + " TEXT, " +
            ToDoList.STATE + " INTEGER, " +
            ToDoList.CONTENT + " TEXT, " +
            ToDoList.LEVEL + " INTEGER)";

    private static final String SQL_ADD_LEVEL =
            "ALTER TABLE " + ToDoList.TABLE_NAME + " ADD " + ToDoList.LEVEL + " INTEGER";

    private static final String SQL_DELETE_LIST = "DROP TABLE IF EXISTS" + ToDoList.TABLE_NAME;

    private TodoContract() {
    }

}
