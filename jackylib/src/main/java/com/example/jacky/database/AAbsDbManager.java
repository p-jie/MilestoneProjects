package com.example.jacky.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.jacky.database.annotation.Autoincrement;
import com.example.jacky.database.annotation.Ignore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class AAbsDbManager{
	
	protected static AAbsDbManager dbManager;
	
	private SQLiteDatabase mDb;
	private AbsDbHelper mHelper;
	protected AAbsDbManager(AbsDbHelper helper) {
		mHelper = helper;
		mDb = helper.getWritableDatabase();
	}
	
	/**
	 * 
	 * @param object
	 * @param values
	 * @param clz
	 * @return
	 * @throws Exception
	 */
	abstract protected<T> ContentValues createValues(T object, ContentValues values, Class<?> clz) throws Exception;
	
	/**
	 * 通过object得到id的值
	 * @return
	 */
	abstract protected<T> Integer fieldIdValue(T object);
	/**
	 * 删除所有的表对象
	 */
	public void resetAllTable() {
		// TODO 自动生成的方法存根
		for(Class<?> table:mHelper.setTabls()){
			mDb.execSQL("DROP TABLE IF EXISTS "+table.getSimpleName());
			mDb.execSQL(mHelper.createTableSql(table));
		}
		
	}
	/**
	 * 通过Id注解得到Id对应的字段名
	 * @return
	 */
	protected <T> String fieldIdName(Class<?> clz) {
		// TODO 自动生成的方法存根
		Field[] fields = clz.getDeclaredFields();
		String idName = "id";
		for (Field field : fields) {
			Autoincrement id = field.getAnnotation(Autoincrement.class);
			if (id != null) {
				idName = field.getName();break;
			}
		}
		return idName;
	}
	
	/**
	 *  把一个字符串的第一个字母大写、效率是最高的、 
	 * @param fildeName
	 * @return
	 * @throws Exception
	 */
	protected String getMethodName(String fildeName) throws Exception {
        byte[] items = fildeName.getBytes();  
        items[0] = (byte) ((char) items[0] - 'a' + 'A');  
        return new String(items);
    }
    
	/**
	 * 批量插入
	 * @param mList
	 * @return
	 */
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
	/**
	 * 插入单个对象
	 * @param object
	 * @return
	 */
	public<T> long insertObject(T object) {
		ContentValues values = new ContentValues();
		Class<?> clz = object.getClass();
        // 获取实体类的所有属性，返回Field数组  
        try {
        	values = createValues(object,values,clz);
        } catch (NoSuchMethodException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		//Logs.wslog(" values " + values.toString());
		return mDb.insert(clz.getSimpleName(), null, values);
	}
	/**
	 * 修改对象
	 * @param object
	 */
	public<T> void updateById(T object) {
		//Logs.wslog(" updateById ");
		ContentValues values = new ContentValues();
		Class<?> clz = object.getClass();
		try {
			values = createValues(object,values,clz);
       	 	Integer val = fieldIdValue(object);
       	 	//Logs.wslog(" values " + values.toString());
			mDb.update(object.getClass().getSimpleName(), values, fieldIdName(clz)+" = " + val, null);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			//Logs.wslog(" Exception " + e.toString());
		}
	}
	/**
	 * 
	 * @param modle
	 * @param where
	 * @param values
	 */
	public int updateWhere(Class<?> modle, QueryWhere where, ContentValues values) {
		return mDb.update(modle.getSimpleName(), values, where.toWhereQueryString(), null);
	}
	/**
	 * 清空表数据-
	 * @param modle
	 */
	public void clearTable(Class<?> modle) {
		mDb.delete(modle.getSimpleName(), null, null);
		String updatSql = "update sqlite_sequence SET seq = 0 where name ='"+modle.getSimpleName()+"'";
		mDb.execSQL(updatSql);
	}
	
	public void deleteById(Class<?> modle, int id) {
		mDb.delete(modle.getSimpleName(), fieldIdName(modle)+"=" + id, null);
	}
	
	
	public void delete(Class<?> modle, Integer... ids) {
        if (ids.length > 0) {  
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < ids.length; i++) {  
                sb.append('?').append(',');  
            }  
            sb.deleteCharAt(sb.length() - 1);  
            mDb.delete(modle.getSimpleName(), fieldIdName(modle)+" in("+ sb+")", null);
        }  
    } 
	
	public void deleteById(Class<?> modle, int[] ids) {
		StringBuffer sb = new StringBuffer();
		int k = ids.length;
		for (int i = 0; i < k; i++) {
			if (i + 1 == k) {
				sb.append(String.valueOf(ids[i])).append(")");
			} else {
				sb.append(String.valueOf(ids[i])).append(",");
			}
		}
		mDb.delete(modle.getSimpleName(), fieldIdName(modle)+" in("+ ids+")", null);
	}
	
	public void deleteById(Class<?> modle, String ids) {
		mDb.delete(modle.getSimpleName(), fieldIdName(modle)+" in("+ ids+")", null);
	}

	public void deleteWhere(Class<?> modle, String where) {
		mDb.delete(modle.getSimpleName(), where, null);
	}
	
	public void deleteWhere(Class<?> modle, QueryWhere where) {
		mDb.delete(modle.getSimpleName(), where.toWhereQueryString(), null);
	}
	
	public<T> T getObjById(Class<T> modle, int id) {
		List<T> os = query(modle,fieldIdName(modle)+"="+id);
		return os.get(0);
	}
	
	@SuppressLint("SimpleDateFormat")
	public<T> List<T> cursorToObject(Class<T> modle, Cursor c) {
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
						} else if (type.isAssignableFrom(double.class)) {
							field.setAccessible(true);
							field.set(o, c.getDouble(cs));
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
	/**
	 *
	 * @return Cursor
	 */
	public<T> Cursor queryTheCursor(Class<T> modle) {
		Cursor c = mDb.rawQuery("SELECT * FROM " + modle.getSimpleName()
				+ " ORDER BY "+fieldIdName(modle)+" DESC ", null);
		return c;
	}
	/**
	 * 
	 * @param modle
	 * @param where
	 * @return
	 */
	public<T> Cursor queryTheCursor(Class<T> modle, String where) {
		Cursor c = mDb.rawQuery("SELECT * FROM " + modle.getSimpleName() + " WHERE " + where, null);
		return c;
	}

	public<T> Cursor queryPublic(String selectTop, Class<T> modle, QueryWhere where) {
		String sql = "SELECT "+selectTop+" FROM " + modle.getSimpleName();
		if(null!=where&&!where.equals("")){
			sql+= " WHERE " + where.toWhereQueryString();
		}
		Cursor c = mDb.rawQuery(sql, null);
		return c;
	}

	/**
	 * query all pics, return list
	 * 
	 * @return List<Pic_receipt>
	 */
	public<T> List<T> query(Class<T> modle) {
		Cursor c = queryTheCursor(modle);
		List<T> os = cursorToObject(modle,c);
		c.close();
		return os;
	}
	
	public<T> List<T> query(Class<T> modle, String where) {
		Cursor c = queryTheCursor(modle,where);
		List<T> os = cursorToObject(modle,c);
		c.close();
		return os;
	}
	
	public<T> List<T> query(Class<T> modle, QueryWhere where) {
		Cursor c = queryTheCursor(modle,where.toWhereQueryString());
		List<T> os = cursorToObject(modle,c);
		c.close();
		return os;
	}

	public<T> int queryCount(Class<T> modle) {
		return queryCount(modle,null);
	}
	public<T> int queryCount(Class<T> modle, QueryWhere where) {
		int catCount = -1;
		Cursor c = queryPublic("COUNT(*)",modle,where);
		//Logs.wslog(c.getColumnCount()+"----");
		if (c.moveToFirst()) {
			catCount = c.getInt(0);
		}
		c.close();
		return catCount;
	}

	
	/**
	 * close database
	 */
	public void closeDB() {
		if (mDb != null)
			mDb.close();
	}

	public SQLiteDatabase getDb() {
		return mDb;
	}

}