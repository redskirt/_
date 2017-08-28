package com.sasaki.callback;
/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年5月8日 上午10:12:16
 * @Description 
 * 
 */
public class Client implements NotifyCallback {
	private Server server;
	
	/**
	 * 1) 构造Client时传递Server的引用
	 * @param server
	 */
	public Client(Server server) {
		super();
		this.server = server;
	}
 
	/**
	 * 3) Client注册回调方法，告知Server以什么样的方式告知Client某方法执行完毕，即过程回调
	 */
	@Override
	public void notice() {
		System.out.println("3. Nofity --> Callback processing.");
	}
	
	/**
	 * 2) Client调用Server方法，传递Callback对象，即Client的实例
	 */
	public void invokeServer() {
		server.execute(this);
		System.out.println("5. Invoked Server --> Client invoked server method.");
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		Client client = new Client(server);
		
		System.out.println("1. Invoke Server --> Client invoke method of server.");
		client.invokeServer();
	}
}
