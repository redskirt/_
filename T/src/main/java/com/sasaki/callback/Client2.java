package com.sasaki.callback;
/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年5月8日 上午10:16:31
 * @Description 
 * 
 */
public class Client2 implements NotifyStatusCallback {
	private Server server;
	
	public Client2(Server server) {
		super();
		this.server = server;
	}
 
	@Override
	public void notice(String status) {
		System.out.println("Server invoked process and return status. --> " + status);
	}
 
	/**
	 * 同步，所有操作都在主线程内完成
	 * @param message
	 */
	public void invokeServer(String message) {
		server.executeStatus(this, message);
	}
	
	/**
	 * 异步，启用新线程调用Server无需等待Server即时返回结果
	 * 
	 * @param message
	 *            端启用多线程（在匿名内部类中）处理message，将必须声明message为final，
	 *            保证Client传入的参数不被Server的线程内部改变，也Server可启用多线程处理Client的参数也
	 *            说明该调用是异步的。
	 */
	public void invokeServerByAsync(final String message) {
		server.executeStatus_(this, message);
		System.out.println("Inovked executeStatus_(). --> " + message);
	}

	public static void main(String[] args) throws InterruptedException {
		// new Client2(new Server()).invokeServer("Anthing...");
		for (int i = 0; i < 5; i++) {
			new Thread(() -> new Client2(new Server()).invokeServerByAsync("Do anthing..."), "").start();
			System.out.println("Thread " + i + " started.");
		}
//		while (true) Thread.sleep(2000);
	}
	

}
