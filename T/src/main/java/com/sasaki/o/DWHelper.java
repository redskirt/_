package com.sasaki.o;
//package org.suanhua.bigdata.util;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.URI;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.LocatedFileStatus;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.fs.RemoteIterator;
//import org.apache.hadoop.io.IOUtils;
//import org.suanhua.util.LogUtil;
//
///**
// * @Author Wei Liu
// * @Mail wei.liu@suanhua.org
// * @Timestamp 2017年6月9日 上午11:32:25
// * @Description Hadoop/Hive操作工具类
// *
// */
//public class DWHelper {
//	final static String hdfsStr				= "hdfs:///node02:8020";
//	final static String hiveJDBCDriver 		= "org.apache.hive.jdbc.HiveDriver";
//	final static String hiveConnectStr 		= "jdbc:hive2://node02:10000";
//	final static String user 				= "hive";
//	final static String password 			= "hive";
//
//	private static Connection connection 	= null;
//	private static Statement statement 		= null;
//	private static ResultSet result 		= null;
//
//	static Configuration conf 				= null;
//	static FileSystem hdfs					= null;
//
//	static {
//		conf = new Configuration();
//		conf.set("hadoop.job.ugi", "hadoop,hadoop");
//
//		try {
//			Class.forName(hiveJDBCDriver);
//			hdfs = FileSystem.get(conf);
//			init();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	static enum E { SHARED, EXCLUSIVE, ; }
//
//	/**
//	 *
//	 * @param fullPath	hdfs全路径，例 /tmp/_/data/_SUCCESS
//	 * @param recursive 默认参数 false，非级联删除
//	 * @return
//	 */
//	public static boolean remove(String fullPath, Boolean... recursive) {
//		try {
//			if(CommonUtil.isEmpty(recursive)/*<-- default argument*/ || !recursive[0])
//				return hdfs.delete(new Path(fullPath), false);
//			else
//				return hdfs.delete(new Path(fullPath), true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	/**
//	 * 列出当前目录文件，不含子目录
//	 * @param fullPath
//	 */
//	public static void ls(String fullPath) {
//		try {
//			RemoteIterator<LocatedFileStatus> files = hdfs.listFiles(new Path(fullPath), false);
//			while(files.hasNext()) {
//				LocatedFileStatus file = files.next();
//				System.out.println(file.getPermission() + " " + file.getPath());
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void create(Object[][] $column___$attr) {
//
//	}
//
//	public static void showResult(String query) throws Exception {
//		queryResult(query).forEach(DWHelper::println);
//	}
//
//	@SuppressWarnings("unchecked")
//	public static <T> List<T> queryResult(String query) throws Exception {
//		if(haveLost()) relive();
//
//		List<T> list = new ArrayList<>();
//		result = statement.executeQuery(query);
//		while(result.next())
//			list.add((T) result.getObject(1));
//
//		return list;
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @throws Exception
//	 */
//	public static void query(String query) throws Exception {
//		if(haveLost()) relive();
//		result = statement.executeQuery(query);
//	}
//
//	/**
//	 * 判断表是否存在
//	 * @param tableName
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean exists(String tableName) throws Exception {
//		if(haveLost()) relive();
//		return queryResult("show tables '" + tableName + "'").size() != 0 ? true : false;
//	}
//
//	/**
//	 *
//	 * @param tableName
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean desc(String tableName) throws Exception {
//		if(haveLost()) relive();
//		return queryResult("desc '" + tableName + "'").size() != 0 ? true : false;
//	}
//
//	/**
//	 *
//	 * @param tableName
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean isLocked(String tableName) throws Exception {
//		if(haveLost()) relive();
//		return queryResult("show locks '" + tableName + "'").contains(E.EXCLUSIVE.name()/*互斥锁*/);
//	}
//
//	/**
//	 *
//	 * @param tableName
//	 * @throws Exception
//	 */
//	public static void unlock(String tableName) throws Exception {
//		if(haveLost()) relive();
//		if(isLocked(tableName)) query("unlock table " + tableName);
//	}
//
//	/**
//	 * 指定字符串写入HDFS目录
//	 * @param outFileFullPath
//	 * @param outStr
//	 * @throws IOException
//	 */
//	public static void writeHDFS(String outFileFullPath, String outStr) throws IOException {
//		ByteArrayInputStream bis = null;
//		OutputStream output = null;
//		try {
//			bis = new ByteArrayInputStream(outStr.getBytes());
//			FileSystem file = FileSystem.get(URI.create(outFileFullPath), conf);
//			output = file.create(new Path(outFileFullPath), () -> System.out.println("--> Write Complete!"));
//			IOUtils.copyBytes(bis, output, 4096, true);
//		}
//		finally {
//			if(nonNull(bis)) bis.close();
//			if(nonNull(output)) output.close();
//		}
//	}
//
//	/**
//	 * 加载表数据到Hive表
//	 * @param sourceDataFullPath
//	 * @param tableName
//	 * @throws Exception
//	 */
//	public static void load(String sourceDataFullPath, String tableName) throws Exception {
//
//		statement.execute("load data inpath '" + sourceDataFullPath + "' overwrite into table " + tableName);
//	}
//
//	/**
//	 * 从源表查询覆盖到目标表
//	 * 可用于orc到 parquet的表存储格式转换
//	 * @param _tableName	:	source
//	 * @param tableName_	:	target
//	 * @throws Exception
//	 */
//	public static void queryInsert(String _tableName, String tableName_) throws Exception {
//		if(haveLost()) relive();
//
//		new Thread(() -> {
//			try 	{ statement.execute("insert overwrite table " + tableName_ + " select * from " + _tableName); }
//			catch 	(Exception e) { e.printStackTrace(); }
//			finally {
//				try { close(); } catch (Exception e) { e.printStackTrace(); }
//			}
//		}).start();
//
//		println("--> 已提交MapReduce任务，详情请查看YARN: http:" + hiveConnectStr.split(":")[2] + ":8088");
//		warn(_tableName + " " + tableName_);
//	}
//
//	/**
//	 * 删除Hive表
//	 * @param tableName
//	 * @throws Exception
//	 */
//	public static void drop(String tableName) throws Exception {
//		if(haveLost()) relive();
//
//		try 	{ statement.execute("drop table if exists " + tableName); }
//		finally { close(); }
//	}
//
//	/**
//	 * 关闭资源
//	 * @throws SQLException
//	 */
//	private static void close() throws SQLException {
//		if(!result.isClosed()) result.close();
//		if(!statement.isClosed()) statement.close();
//		if(!connection.isClosed()) connection.close();
//	}
//
//	/**
//	 * 初始连接和查询
//	 * @throws Exception
//	 */
//	private static void init() throws Exception {
//		connection = DriverManager.getConnection(hiveConnectStr, user, password);
//		statement  = connection.createStatement();
//	}
//
//	/**
//	 * 执行单步查询后连接关闭，重新获得连接
//	 * @throws Exception
//	 */
//	public static void relive() throws Exception {
//		if(haveLost()) init();
//	}
//
//	public static boolean haveLost() throws SQLException { return statement.isClosed(); }
//
//	private static boolean nonNull(Object o) { return null != o; }
//
//	private static void println(Object t) { LogUtil.info(t.toString()); }
//
//	public static void warn(String t) {
//		LogUtil.warn("--> 警告：避免MapReduce执行冲突，60s内，请勿重复提交本操作！" + t);
//	}
//
//	static interface CallableStatement {
//		public ResultSet call(String query);
//	}
//
//	/**
//	 * 模板方法
//	 * @param call
//	 * @return
//	 */
//	public final static ResultSet queryResult(String query, CallableStatement call) {
//		try {
//			if(haveLost()) relive();
//			return call.call(query);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try 	{ close(); }
//			catch (SQLException e) { e.printStackTrace(); }
//		}
//		return null;
//	}
//
//	public static void main(String[] args) {
//		try {
////			load("hdfs://node01:8020/tmp/_/t6_", "default.t6_");
////			queryInsert("t6_", "t6");
////			drop("default.t5_");
//
////			result = statement.executeQuery("load data inpath 'hdfs://node01:8020/tmp/_/t6_' overwrite into table t6_");
////			result = statement.executeQuery("desc t6_");
////			while(result.next()) System.out.println(result.getString(1));
//
////			showResult("desc t6_");
//
////			System.out.println(remove("/tmp/_/data/_SUCCESS"));
//			ls("/user");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
//
