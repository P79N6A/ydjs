package com.yd.ydsp.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Set;

/**
 * @author zengyixun
 */
public class RedisManager {
	public static final Logger logger = LoggerFactory.getLogger(RedisManager.class);
	
	private String host = "127.0.0.1";
	
	private int port = 6379;
	
	// 0 - never expire
	private int expire = 0;
	
	//timeout for jedis try to connect to redis server, not expire time! In milliseconds
	private int timeout = 0;
	
	private String password = "";
	
	private static JedisPool jedisPool = null;
	
	public RedisManager() {

	}
	
	/**
	 * 初始化方法
	 */
	public void init(){
		if(this.password.equals("123456")){
			this.password="";
		}
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(100);
		jedisPoolConfig.setMaxTotal(300);
		jedisPoolConfig.setMaxWaitMillis(timeout);
		jedisPoolConfig.setTestOnBorrow(true);
		if(jedisPool == null){
			if(password != null && !"".equals(password)){
				jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
			}else if(timeout != 0){
				jedisPool = new JedisPool(jedisPoolConfig, host, port,timeout);
			}else{
				jedisPool = new JedisPool(jedisPoolConfig, host, port);
			}
			
		}
	}

	/**
	 * 清理方法
	 */

	public void destroy(){
		if(jedisPool != null){
			jedisPool.destroy();
		}
	}
	
	/**
	 * get value from redis
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key){
		byte[] value = null;
		Jedis jedis = jedisPool.getResource();
		try{
			value = jedis.get(key);
		}finally{
			if(jedis!=null){
				jedis.close();
			}
//			jedisPool.returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * set 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] set(byte[] key,byte[] value){
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.set(key,value);
			if(this.expire != 0){
				jedis.expire(key, this.expire);
		 	}
		}finally{
			if(jedis!=null){
				jedis.close();
			}
//			jedisPool.returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * set 
	 * @param key
	 * @param value
	 * @param expire  精确到秒
	 * @return
	 */
	public byte[] set(byte[] key,byte[] value,int expire){
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.set(key,value);
			if(expire != 0){
				jedis.expire(key, expire);
		 	}
		}finally{
			if(jedis!=null){
				jedis.close();
			}
//			jedisPool.returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * del
	 * @param key
	 */
	public void del(byte[] key){
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.del(key);
		}finally{
			if(jedis!=null){
				jedis.close();
			}
//			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * flush
	 */
	public void flushDB(){
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.flushDB();
		}finally{
			if(jedis!=null){
				jedis.close();
			}
//			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * size
	 */
	public Long dbSize(){
		Long dbSize = 0L;
		Jedis jedis = jedisPool.getResource();
		try{
			dbSize = jedis.dbSize();
		}finally{
			if(jedis!=null){
				jedis.close();
			}
//			jedisPool.returnResource(jedis);
		}
		return dbSize;
	}

	/**
	 * keys
	 * @return
	 */
	public Set<byte[]> keys(String pattern){
		Set<byte[]> keys = null;
		Jedis jedis = jedisPool.getResource();
		try{
			keys = jedis.keys(pattern.getBytes());
		}finally{
			if(jedis!=null){
				jedis.close();
			}
//			jedisPool.returnResource(jedis);
		}
		return keys;
	}

	/**
	 * 加锁
	 * @param locaName  锁的key
	 * @param identifier 唯一value，用来区分是哪个客户加的锁，避免删除别人的锁
	 * @param acquireTimeout  获取锁的超时时间 单位毫秒
	 * @param timeout   锁的超时时间 单位毫秒
	 * @return 锁标识
	 */
	public boolean lockWithTimeout(String locaName, String identifier,
								  long acquireTimeout, long timeout) {
		Jedis conn = null;
		boolean result = false;
		try {
			// 获取连接
			conn = jedisPool.getResource();
			// 锁名，即key值
			String lockKey = "lock_" + locaName;
			// 超时时间，上锁后超过此时间则自动释放锁
			int lockExpire = (int)(timeout / 1000);

			// 获取锁的超时时间，超过这个时间则放弃获取锁
			long end = System.currentTimeMillis() + acquireTimeout;
			while (System.currentTimeMillis() < end) {
				if (conn.setnx(lockKey, identifier) == 1) {
					conn.expire(lockKey, lockExpire);
					//是否加锁成功
					result = true;
					break;
				}
				// 返回-1代表key没有设置超时时间，为key设置一个超时时间
                if (conn.ttl(lockKey) == -1) {
					conn.expire(lockKey, lockExpire);
					result = true;
					break;
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					logger.error("Jedis InterruptedException:",e);
					Thread.currentThread().interrupt();
				}
			}
		} catch (JedisException e) {
			logger.error("JedisException:",e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return result;
	}

	/**
	 * 释放锁
	 * @param lockName 锁的key
	 * @param identifier    释放锁的标识
	 * @return 反回值无所谓，因为会自己超时的，为true是成功的
	 */
	public boolean releaseLock(String lockName, String identifier) {
		Jedis conn = null;
		String lockKey = "lock_" + lockName;
		boolean retFlag = false;
		try {
			conn = jedisPool.getResource();
			while (true) {
				// 监视lock，准备开始事务
				conn.watch(lockKey);
				// 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
				if (identifier.equals(conn.get(lockKey))) {
					Transaction transaction = conn.multi();
					transaction.del(lockKey);
					List<Object> results = transaction.exec();
					if (results == null) {
						continue;
					}
					retFlag = true;
				}
				conn.unwatch();
				break;
			}
		} catch (JedisException e) {
			logger.error("JedisException:",e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return retFlag;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
