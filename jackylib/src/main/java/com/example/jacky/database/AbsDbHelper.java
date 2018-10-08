package com.example.jacky.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.jacky.database.annotation.Autoincrement;
import com.example.jacky.database.annotation.Ignore;

import java.lang.reflect.Field;

public abstract class AbsDbHelper extends SQLiteOpenHelper {

	
	private static int DATABASE_VERSION = 3;
	
	protected abstract Class<?>[] setTabls();
	
	
	public AbsDbHelper(Context context, String databaseName, int databaseVersion) {
		super(context, databaseName, null, databaseVersion);
		DATABASE_VERSION = databaseVersion;
	}
	
	//数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		//Logs.customlog("TxtDb","Creating tables for schema version " + DATABASE_VERSION);
		for(Class<?> table:setTabls()){
			db.execSQL(createTableSql(table));
		}
	}
	//如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Logs.customlog("TxtDb","Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
		
		dropTableAll(db);
        onCreate(db);
	}
	
	private void dropTableAll(SQLiteDatabase db){
		for(Class<?> table:setTabls()){
			db.execSQL("DROP TABLE IF EXISTS "+table.getSimpleName());
		}
	}
	
	/**
	 * 自动建表
	 * @param tClass 类别
	 * @return
	 */
	public String createTableSql(Class<?> tClass) {
		
		Field[] fields = tClass.getDeclaredFields();
		StringBuffer createSql = new StringBuffer("CREATE TABLE IF NOT EXISTS ").append(tClass.getSimpleName()).append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT,");
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String type = field.getGenericType().toString(); // 获取属性的类型
				/*Logs.wslog("getName---"+field.getName());
				Logs.wslog("gtype---"+type);*/
				Autoincrement autoincrement = field.getAnnotation(Autoincrement.class);
				if (autoincrement != null) {
					continue;
				}
				
				Ignore ignore = field.getAnnotation(Ignore.class);
				if (ignore != null) {
					continue;
				}
				
				if(type.equals("class java.lang.String")){
					createSql.append(field.getName()).append(" TEXT,");
				}else if((type.equals("int"))||(type.equals("boolean"))||(type.equals("long"))){
					createSql.append(field.getName()).append(" INTEGER,");
				}else if((type.equals("double"))||(type.equals("float"))){
					createSql.append(field.getName()).append(" REAL,");
				}
			}
			
			createSql.deleteCharAt(createSql.length()-1);  
			createSql.append(")");
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		//Logs.customlog("TxtDb","createSql---"+createSql.toString());
		return createSql.toString();
	}
	
}
