package com.sasaki.o;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年6月16日 上午9:57:05
 * @Description 
 * 
 */
public class DWHelper_ {
	final static String hiveJDBCDriver 		= "org.apache.hive.jdbc.HiveDriver";
	final static String hiveConnectStr 		= "jdbc:hive2://node02:10000";
	final static String user 				= "hive";
	final static String password 			= "hive";
	
	private static Connection connection 	= null;
	private static Statement statement 		= null;
	private static ResultSet result 		= null;
	
	static Configuration conf 				= null;
	
	static {
		try {
			Class.forName(hiveJDBCDriver);
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		conf = new Configuration();
		conf.set("hadoop.job.ugi", "hadoop,hadoop"); 
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> queryResult(final String query) throws Exception {
		return (List<T>) Template.getInstance().query(statement -> {
			ArrayList<T> list = new ArrayList<>(); 
			result = statement.executeQuery(query);
			while(result.next()) list.add((T) result.getObject(1));
			return list;
		});
	}
	
	/**
	 * 模板
	 * @author wei.liu
	 *
	 */
	static class Template {
		private Template() {
			if(null != template ) return;
		}
		
		private static Template template = null;
		
		public static Template getInstance() {
			if(null != template ) template = new Template();
			return template;
		}
		
		/**
		 * 执行模板方法
		 * @param call
		 * @return
		 * @throws Exception
		 */
		private final Object execute(CallableStatement call) throws Exception {
			try 	{
				if(haveLost()) relive();
				return call.call(statement);
			} 
			finally { close(); }
		}

		public Object query(CallableStatement call) throws Exception {
			return execute(call);
		}
	}
	
	@FunctionalInterface
	static interface CallableStatement {
		Object call(Statement statement) throws Exception;
	}
	
	/**
	 * 执行单步查询后连接关闭，重新获得连接
	 * @throws Exception
	 */
	public static void relive() throws Exception { 
		if(haveLost()) init(); 
	}
	
	public static boolean haveLost() throws SQLException { return statement.isClosed(); }
	
	/**
	 * 关闭资源
	 * @throws SQLException
	 */
	private static void close() throws SQLException {
		if(!result.isClosed()) result.close();
		if(!statement.isClosed()) statement.close();
		if(!connection.isClosed()) connection.close();
	}
	
	/**
	 * 初始连接和查询
	 * @throws Exception
	 */
	private static void init() throws Exception {
		connection = DriverManager.getConnection(hiveConnectStr, user, password);
		statement  = connection.createStatement();
	}

	
}

