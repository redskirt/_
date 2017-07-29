package s2r;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisClusterTest {
    JedisCluster jc = null;

    static String prefix = "luffi:lbl";
    static String KEY_SPLIT = ":"; 

    String nameKey = prefix + KEY_SPLIT + "name";
    
	@Before
	public void init() {
        String[] serverArray = "127.0.0.1:7000,127.0.0.1:7001".split(",");
        Set<HostAndPort> nodes = new HashSet<>();
        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }
        jc = new JedisCluster(nodes, 1000, 1000, 1, null, new GenericObjectPoolConfig());
        jc.del(nameKey);// 删除测试key	
	}

	@Test
	public void rw() {
		println(jc.set("k1", "v1"));
		println(jc.get("k1"));
	}
	
	@Test
	public void test2() {
		System.out.println("www");
	}

	@After
	public void after() throws Exception {
		jc.close();
	}
	void println(Object o) {
		System.out.println(o);
	}
}
