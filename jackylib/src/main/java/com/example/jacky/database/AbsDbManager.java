package com.example.jacky.database;

import android.content.ContentValues;


import com.example.jacky.database.annotation.Autoincrement;
import com.example.jacky.database.annotation.Ignore;

import java.lang.reflect.Field;

public abstract class AbsDbManager extends AAbsDbManager{

	protected AbsDbManager(AbsDbHelper helper) {
		super(helper);
		// TODO 自动生成的构造函数存根
	}
	
	@Override
	protected <T> ContentValues createValues(T object, ContentValues values,
                                             Class<?> clz) throws Exception {
		// 获取实体类的所有属性，返回Field数组  
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
			Autoincrement autoincrement = field.getAnnotation(Autoincrement.class);
			if (autoincrement != null) {
				continue;
			}
			Ignore ignore = field.getAnnotation(Ignore.class);
			if (ignore != null) {
				continue;
			}
			String type = field.getGenericType().toString(); // 获取属性的类型
			//Logs.wslog("--type--"+type);
			Object value = field.get(object);
			if (type.equals("class java.lang.String")) {
				if(value==null){
					values.put(field.getName(),(String)value);
				}else{
					values.put(field.getName(),(String)value.toString().trim());
				}
				
			}else if(type.equals("int")){
				values.put(field.getName(),(Integer)value);
			}else if(type.equals("boolean")){
				int flag = 0;
				if((Boolean)value==true){
					flag = 1;
				}
				values.put(field.getName(),flag);
			}else if(type.equals("double")){
				values.put(field.getName(),(Double)value);
			}else if(type.equals("float")){
				values.put(field.getName(),(Float)value);
			}else if(type.equals("long")){
				values.put(field.getName(),(Long) value);
			}
			
		}
		return values;
	}
	@Override
	protected <T> Integer fieldIdValue(T object) {
		// TODO 自动生成的方法存根
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			Autoincrement id = field.getAnnotation(Autoincrement.class);
			if (id != null) {
				try {
					Object value = field.get(object);
					return (Integer) value;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
			}
		 }
		 return 0;
	}
	/*protected static AbsDbManager dbManager;
	
	private SQLiteDatabase mDb;
	
	protected AbsDbManager(AbsDbHelper helper) {
		mDb = helper.getWritableDatabase();
	}
	
	//批量插入
	public<T> long insertList(List<T> mList) {
		long flag = 0;
		try{
			mDb.beginTransaction(); // 手动设置开始事务
			for (T obj : mList) {
				flag = insertObject(obj);
			}
			mDb.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
		}catch (Exception e) {
			flag = 0;
			e.printStackTrace();
		} finally {
			// 结束事务
			mDb.endTransaction();// 处理完成
		}
		return flag;
	}
	
	public<T> long insertObject(T object) {
		ContentValues values = new ContentValues();
		Class<?> clz = object.getClass();
        // 获取实体类的所有属性，返回Field数组  
        Field[] fields = clz.getDeclaredFields();
        //Logs.wslog("fields--"+fields.length);
        try {
			for (Field field : fields) {
				
				Autoincrement autoincrement = field.getAnnotation(Autoincrement.class);
				if (autoincrement != null) {
					continue;
				}
				Ignore ignore = field.getAnnotation(Ignore.class);
				if (ignore != null) {
					continue;
				}
				String type = field.getGenericType().toString(); // 获取属性的类型
				//Logs.wslog("--type--"+type);
				Object value = field.get(object);
				if (type.equals("class java.lang.String")) {
					if(value==null){
						values.put(field.getName(),(String)value);
					}else{
						values.put(field.getName(),(String)value.toString().trim());
					}
					
				}else if(type.equals("int")){
					values.put(field.getName(),(Integer)value);
				}else if(type.equals("boolean")){
					int flag = 0;
					if((Boolean)value==true){
						flag = 1;
					}
					values.put(field.getName(),flag);
				}else if(type.equals("double")){
					values.put(field.getName(),(Double)value);
				}else if(type.equals("float")){
					values.put(field.getName(),(Float)value);
				}
				
			}
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return mDb.insert(clz.getSimpleName(), null, values);
	}
	
	public<T> void updateById(T object) {
		Logs.wslog(" updateById ");
		ContentValues values = new ContentValues();
		Class<?> clz = object.getClass();
		Field[] fs = clz.getDeclaredFields();
		try {
			for (Field field : fs) {
				Autoincrement autoincrement = field.getAnnotation(Autoincrement.class);
				 if (autoincrement != null) {
					continue;
				 }
				Ignore ignore = field.getAnnotation(Ignore.class);
				 if (ignore != null) {
					continue;
				 }
				 String type = field.getGenericType().toString(); // 获取属性的类型
				 Object value = field.get(object);
				 if (type.equals("class java.lang.String")) {
					values.put(field.getName(),(String)value);
				 }else if(type.equals("int")){
					values.put(field.getName(),(Integer)value);
				 }else if(type.equals("boolean")){
					int flag = 0;
					if((Boolean)value==true){
						flag = 1;
					}
					values.put(field.getName(),flag);
				 }else if(type.equals("double")){
					values.put(field.getName(),(Double)value);
				 }
			}
			Field field = clz.getField("_id");
			Logs.wslog(" values " + values.toString());
			mDb.update(object.getClass().getSimpleName(), values, "_id = " + field.get(object), null);
		} catch (Exception e) {
			e.printStackTrace();
			Logs.wslog(" Exception " + e.toString());
		}
	}
	*//**
	 * 
	 * @param modle
	 * @param where
	 * @param values
	 *//*
	public int updateWhere(Class<?> modle,QueryWhere where,ContentValues values) {
		return mDb.update(modle.getSimpleName(), values, where.toWhereQueryString(), null);
	}
	*//**
	 * 清空表数据-
	 * @param modle
	 *//*
	public void clearTable(Class<?> modle) {
		mDb.delete(modle.getSimpleName(), null, null);
		String updatSql = "update sqlite_sequence SET seq = 0 where name ='"+modle.getSimpleName()+"'";
		mDb.execSQL(updatSql);
	}
	
	public void deleteById(Class<?> modle,int id) {
		mDb.delete(modle.getSimpleName(), "_id=" + id, null);
	}
	
	
	public void delete(Class<?> modle,Integer... ids) {  
        if (ids.length > 0) {  
            StringBuffer sb = new StringBuffer();  
            for (int i = 0; i < ids.length; i++) {  
                sb.append('?').append(',');  
            }  
            sb.deleteCharAt(sb.length() - 1);  
            mDb.delete(modle.getSimpleName(), "_id in("+ sb+")", null);
        }  
    } 
	
	public void deleteById(Class<?> modle,int[] ids) {
		StringBuffer sb = new StringBuffer();
		int k = ids.length;
		for (int i = 0; i < k; i++) {
			if (i + 1 == k) {
				sb.append(String.valueOf(ids[i])).append(")");
			} else {
				sb.append(String.valueOf(ids[i])).append(",");
			}
		}
		mDb.delete(modle.getSimpleName(), "_id in("+ ids+")", null);
	}
	
	public void deleteById(Class<?> modle,String ids) {
		mDb.delete(modle.getSimpleName(), "_id in("+ ids+")", null);
	}

	public void deleteWhere(Class<?> modle,String where) {
		mDb.delete(modle.getSimpleName(), where, null);
	}
	
	public void deleteWhere(Class<?> modle,QueryWhere where) {
		mDb.delete(modle.getSimpleName(), where.toWhereQueryString(), null);
	}
	
	public<T> T getObjById(Class<T> modle,int id) {
		List<T> os = query(modle,"_id="+id);
		return os.get(0);
	}
	
	@SuppressLint("SimpleDateFormat")
	public<T> List<T> cursorToObject(Class<T> modle,Cursor c) {
		ArrayList<T> os = new ArrayList<T>();
		try {
			while (c.moveToNext()) {
				T o = (T)modle.newInstance();
				for (Field field : modle.getDeclaredFields()) {
					Ignore ignore = field.getAnnotation(Ignore.class);
					if (ignore != null) {
						continue;
					}
					int cs = c.getColumnIndex(field.getName());
					if (cs != -1) {
						Class<?> type = field.getType();
						if (type.isAssignableFrom(int.class)) {
							field.setAccessible(true);
							field.set(o, c.getInt(cs));
						} else if (type.isAssignableFrom(String.class)) {
							field.setAccessible(true);
							field.set(o, c.getString(cs));
						} else if (type.isAssignableFrom(long.class)) {
							field.setAccessible(true);
							field.set(o, c.getLong(cs));
						} else if (type.isAssignableFrom(boolean.class)) {
							boolean flag = false;
							if(c.getInt(cs)==1){
								flag = true;
							}
							field.setAccessible(true);
							field.set(o, flag);
						} else if (type.isAssignableFrom(float.class)) {
							field.setAccessible(true);
							field.set(o, c.getFloat(cs));
						} 
					}
				}
				os.add(o);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return os;
	}
	
	public<T> Cursor queryTheCursor(Class<T> modle,String where) {
		Cursor c = mDb.rawQuery("SELECT * FROM " + modle.getSimpleName() + " WHERE " + where, null);
		return c;
	}
	public<T> List<T> query(Class<T> modle,String where) {
		Cursor c = queryTheCursor(modle,where);
		List<T> os = cursorToObject(modle,c);
		c.close();
		return os;
	}
	
	public<T> List<T> query(Class<T> modle,QueryWhere where) {
		Cursor c = queryTheCursor(modle,where.toWhereQueryString());
		List<T> os = cursorToObject(modle,c);
		c.close();
		return os;
	}
	public<T> Cursor queryTheCursor(Class<T> modle,int top,String where) {
		Cursor c = mDb.rawQuery("SELECT top ("+top+") * FROM " + modle.getSimpleName() + " WHERE " + where, null);
		return c;
	}
	public<T> List<T> query(Class<T> modle,int top,QueryWhere where) {
		Cursor c = queryTheCursor(modle,top,where.toWhereQueryString());
		List<T> os = cursorToObject(modle,c);
		c.close();
		return os;
	}
	*//**
	 * close database
	 *//*
	public void closeDB() {
		if (mDb != null)
			mDb.close();
	}

	public SQLiteDatabase getDb() {
		return mDb;
	}*/

}