package com.sasaki.callback;
/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年5月8日 上午10:12:40
 * @Description 
 * 
 */
public class Server {
	/**
	 * 4) Server执行业务方法，参数即是Client实例，执行完毕后可调用notice()通知Client执行完毕
	 * @param callback
	 */
	public void execute(NotifyCallback callback){
		System.out.println("2. Server execute --> Do anything.");
		callback.notice();
		System.out.println("4. Notify --> Server notified client, method execute completed.");
	}
	
	public void executeStatus(NotifyStatusCallback callback, final String message) {
		System.out.println("Server received message of client. --> " + message);
		String success = "200";
		callback.notice(success);
	}
	
	public void executeStatus_(NotifyStatusCallback callback, final String message) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		System.out.println(Thread.currentThread().getName() + " --> " + message);
		String success = "200";
		callback.notice(success);
	}
}
