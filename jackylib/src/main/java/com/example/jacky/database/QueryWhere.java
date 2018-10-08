package com.example.jacky.database;

public class QueryWhere {
	
	private StringBuffer whereBuffer;
	
	private QueryWhere() {
		whereBuffer = new StringBuffer();
	}
	
	public void clear(){
		whereBuffer = new StringBuffer();
	}
	
	public static QueryWhere queryBuilder(){
		return new QueryWhere();
	}
	/**
	 * ����>
	 * @param columnName
	 * @param value
	 * @return
	 */
	public QueryWhere greater(String columnName, Object value){
		if(value instanceof String){
			whereBuffer.append(columnName).append(">'").append(value).append("'");
		}else{
			whereBuffer.append(columnName).append(">").append(value);
		}
		return this;
	}
	/**
	 * С��<
	 * @param columnName
	 * @param value
	 * @return
	 */
	public QueryWhere less(String columnName, Object value){
		if(value instanceof String){
			whereBuffer.append(columnName).append(">'").append(value).append("'");
		}else{
			whereBuffer.append(columnName).append(">").append(value);
		}
		return this;
	}
	/**
	 *
	 * @param columnName
	 * @param value
	 * @return
	 * 如果boolean查询  0:false  1:true
	 */
	public QueryWhere notEq(String columnName, Object value){
		if(value instanceof String){
			whereBuffer.append(columnName).append("!='").append(value).append("'");
		}else{
			whereBuffer.append(columnName).append("!=").append(value);
		}
		return this;
	}
	/**
	 *
	 * @param columnName
	 * @param value
	 * @return
	 * 如果boolean查询  0:false  1:true
	 */
	public QueryWhere eq(String columnName, Object value){
		if(value instanceof String){
			whereBuffer.append(columnName).append("='").append(value).append("'");
		}else{
			whereBuffer.append(columnName).append("=").append(value);
		}
		return this;
	}
	public QueryWhere like(String columnName, Object value){
		//if(value instanceof String){
			whereBuffer.append(columnName).append(" like '%").append(value).append("%'");
		/*}else{
			whereBuffer.append(columnName).append("=").append(value);
		}*/
		return this;
	}
	public QueryWhere and(){
		whereBuffer.append(" and ");
		return this;
	}
	
	public QueryWhere or(){
		whereBuffer.append(" or ");
		return this;
	}

	public QueryWhere brace(QueryWhere newWhereBuffer){
		whereBuffer.append(" ( ").append(newWhereBuffer.toWhereQueryString()).append(" ) ");
		return this;
	}
	public QueryWhere braceLeft(){
		whereBuffer.append(" ( ");
		return this;
	}
	
	public QueryWhere braceRight(){
		whereBuffer.append(" ) ");
		return this;
	}
	
	public QueryWhere orderByAsc(String columnName){
		whereBuffer.append(" order by ").append(columnName).append(" asc");
		return this;
	}
	
	public QueryWhere orderByDesc(String columnName){
		whereBuffer.append(" order by ").append(columnName).append(" desc");
		return this;
	}
	
	public QueryWhere limit(int start,int page){
		whereBuffer.append(" limit ").append(start).append(",").append(page);
		return this;
	}
	
	public String toWhereQueryString(){
		return whereBuffer.toString();
	}
}