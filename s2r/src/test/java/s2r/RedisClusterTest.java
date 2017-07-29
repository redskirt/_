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
		p(jc.set("k1", "v1"));
		p(jc.get("k1"));
	}
	
	@Test
	public void genericOperater() throws Exception {
		// set if not exist.
//		assert jc.setnx("k2", "v2") == 1;
//		assert jc.setnx("k2", "v2") == 0;
		
		// 带时间set/s
//		p(jc.setex("k3", 3, "v3")); 
//        for(int i = 0 ; i < 5 ; i ++){
//            p(jc.get("k3")); //过期以后redis集群自动删除
//            Thread.sleep(1000);
//        }
        
        // 操作子串
//        p(jc.set("k4", "123456"));
//        p(jc.get("k4"));
//        p(jc.setrange("k4", 3, "abc"));
//        p(jc.get("k4"));
//        p(jc.getrange("k4", 3, 5));
        
        // 批量操作key mset
		/**
		 * keySlot算法中，如果key包含{}，就会使用第一个{}内部的字符串作为hash key，这样就可以保证拥有同样{}内部字符串的key就会拥有相同slot。
		 * 本来可以hash到不同的slot中的数据都放到了同一个slot中，所以使用的时候要注意数据不要太多导致一个slot数据量过大，数据分布不均匀！
		 * mset 是原子性的
		 */
		String sortBy = "{s:}";
//        p(jc.mset(
//        		sortBy + "name", "sasaki", 
//        		sortBy + "age", "20",
//        		sortBy + "score", "100",
//        		sortBy + "salary", "2000.00"));
//        p(jc.get(sortBy + "score"));
		
        p(jc.del(sortBy + "name"));
        p(jc.del(sortBy + "age"));
        p(jc.del(sortBy + "score"));
        p(jc.del(sortBy + "salary"));
        
//        jc.mget(sortBy + "name", sortBy + "age", sortBy + "score", sortBy + "salary").forEach(System.out::println);

        /**
         * MSETNX 命令：它只会在所有给定 key 都不存在的情况下进行设置操作。
         */
        p(jc.msetnx(// 返回值Long 1
        		sortBy + "name", "sasaki", 
        		sortBy + "age", "20",
        		sortBy + "score", "100",
        		sortBy + "salary", "2000.00"));
        jc.mget(sortBy + "name", sortBy + "age", sortBy + "score", sortBy + "salary").forEach(System.out::println);
        
        p(jc.msetnx(// 该操作将全部无效，因为key已存在
        		sortBy + "name", "sasaki", 
        		sortBy + "phone", "1700000000"));
        
	}

	@After
	public void after() throws Exception {
		jc.close();
	}
	
	void p(Object o) { System.out.println(o); }
}
