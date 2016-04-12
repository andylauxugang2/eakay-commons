package cn.eakay.commons.util.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import cn.eakay.commons.util.config.Global;
import cn.eakay.commons.util.object.ObjectUtils;
import cn.eakay.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Jedis Cache 工具类
 * 
 * @author hymagic
 * @version 2014-6-29 resources文件中放置 eakey.properties 參見 Global中
 *          PropertiesLoader redis.keyPrefix=eakay redis.host=127.0.0.1
 *          redis.port=6379 redis.timeout=300
 */
@Slf4j
public class JedisUtils
{

	public static final String KEY_PREFIX = Global.getConfig("redis.keyPrefix");
	public static final String HOST = Global.getConfig("redis.host");
	public static final Integer PORT = Integer.valueOf(Global.getConfig("redis.port"));
	public static final Integer TIME_OUT = Integer.valueOf(Global.getConfig("redis.timeout"));

	private static JedisPool jedisPool = null;

	static
	{
		poolInit();
	}

	/**
	 * 同步初始化
	 */
	private static synchronized void poolInit()
	{
		if (jedisPool == null)
		{
			initialPool();
		}
	}

	/**
	 * 初始化Redis连接池
	 */
	private static void initialPool()
	{
		try
		{
			JedisPoolConfig config = new JedisPoolConfig();
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxTotal个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxTotal(500);
			// 控制一个pool最多有多少个状态为idle的jedis实例
			config.setMaxIdle(5);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(1000 * 100);
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(true);
			jedisPool = new JedisPool(config, HOST, PORT, TIME_OUT);
		} catch (Exception e)
		{
			log.error("First create JedisPool error : ", e);
		}
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static String get(String key)
	{
		String value = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(key))
			{
				value = jedis.get(key);
				value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
				log.debug("get {} = {}", key, value);
			}
		} catch (Exception e)
		{
			log.warn("get {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Object getObject(String key)
	{
		Object value = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(getBytesKey(key)))
			{
				value = toObject(jedis.get(getBytesKey(key)));
				log.debug("getObject {} = {}", key, value);
			}
		} catch (Exception e)
		{
			log.warn("getObject {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String set(String key, String value, int cacheSeconds)
	{
		String result = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.set(key, value);
			if (cacheSeconds != 0)
			{
				jedis.expire(key, cacheSeconds);
			}
			log.debug("set {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("set {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setObject(String key, Object value, int cacheSeconds)
	{
		String result = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.set(getBytesKey(key), toBytes(value));
			if (cacheSeconds != 0)
			{
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObject {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setObject {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取List缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static List<String> getList(String key)
	{
		List<String> value = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(key))
			{
				value = jedis.lrange(key, 0, -1);
				log.debug("getList {} = {}", key, value);
			}
		} catch (Exception e)
		{
			log.warn("getList {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取List缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static List<Object> getObjectList(String key)
	{
		List<Object> value = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(getBytesKey(key)))
			{
				List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
				value = new ArrayList<>();
				for (byte[] bs : list)
				{
					value.add(toObject(bs));
				}
				log.debug("getObjectList {} = {}", key, value);
			}
		} catch (Exception e)
		{
			log.warn("getObjectList {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置List缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setList(String key, List<String> value, int cacheSeconds)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(key))
			{
				jedis.del(key);
			}
			result = jedis.rpush(key, (String[]) value.toArray());
			if (cacheSeconds != 0)
			{
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setList {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setList {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置List缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setObjectList(String key, List<Object> value, int cacheSeconds)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(getBytesKey(key)))
			{
				jedis.del(key);
			}
			List<byte[]> list = new ArrayList<>();
			for (Object o : value)
			{
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
			if (cacheSeconds != 0)
			{
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectList {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setObjectList {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long listAdd(String key, String... value)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.rpush(key, value);
			log.debug("listAdd {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("listAdd {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long listObjectAdd(String key, Object... value)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			List<byte[]> list = new ArrayList<>();
			for (Object o : value)
			{
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
			log.debug("listObjectAdd {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("listObjectAdd {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Set<String> getSet(String key)
	{
		Set<String> value = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(key))
			{
				value = jedis.smembers(key);
				log.debug("getSet {} = {}", key, value);
			}
		} catch (Exception e)
		{
			log.warn("getSet {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Set<Object> getObjectSet(String key)
	{
		Set<Object> value = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(getBytesKey(key)))
			{
				value = new HashSet<>();
				Set<byte[]> set = jedis.smembers(getBytesKey(key));
				for (byte[] bs : set)
				{
					value.add(toObject(bs));
				}
				log.debug("getObjectSet {} = {}", key, value);
			}
		} catch (Exception e)
		{
			log.warn("getObjectSet {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setSet(String key, Set<String> value, int cacheSeconds)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(key))
			{
				jedis.del(key);
			}
			result = jedis.sadd(key, (String[]) value.toArray());
			if (cacheSeconds != 0)
			{
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setSet {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setSet {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setObjectSet(String key, Set<Object> value, int cacheSeconds)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(getBytesKey(key)))
			{
				jedis.del(key);
			}
			Set<byte[]> set = new HashSet<>();
			for (Object o : value)
			{
				set.add(toBytes(o));
			}
			result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
			if (cacheSeconds != 0)
			{
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectSet {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setObjectSet {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long setSetAdd(String key, String... value)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.sadd(key, value);
			log.debug("setSetAdd {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setSetAdd {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long setSetObjectAdd(String key, Object... value)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			Set<byte[]> set = new HashSet<>();
			for (Object o : value)
			{
				set.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
			log.debug("setSetObjectAdd {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setSetObjectAdd {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Map<String, String> getMap(String key)
	{
		Map<String, String> value = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(key))
			{
				value = jedis.hgetAll(key);
				log.debug("getMap {} = {}", key, value);
			}
		} catch (Exception e)
		{
			log.warn("getMap {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Map<String, Object> getObjectMap(String key)
	{
		Map<String, Object> value = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(getBytesKey(key)))
			{
				value = new HashMap<>();
				Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
				for (Map.Entry<byte[], byte[]> e : map.entrySet())
				{
					value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
				}
				log.debug("getObjectMap {} = {}", key, value);
			}
		} catch (Exception e)
		{
			log.warn("getObjectMap {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setMap(String key, Map<String, String> value, int cacheSeconds)
	{
		String result = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(key))
			{
				jedis.del(key);
			}
			result = jedis.hmset(key, value);
			if (cacheSeconds != 0)
			{
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setMap {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setMap {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setObjectMap(String key, Map<String, Object> value, int cacheSeconds)
	{
		String result = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(getBytesKey(key)))
			{
				jedis.del(key);
			}
			Map<byte[], byte[]> map = new HashMap<>();
			for (Map.Entry<String, Object> e : value.entrySet())
			{
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			if (cacheSeconds != 0)
			{
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectMap {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("setObjectMap {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static String mapPut(String key, Map<String, String> value)
	{
		String result = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.hmset(key, value);
			log.debug("mapPut {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("mapPut {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static String mapObjectPut(String key, Map<String, Object> value)
	{
		String result = null;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			Map<byte[], byte[]> map = new HashMap<>();
			for (Map.Entry<String, Object> e : value.entrySet())
			{
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			log.debug("mapObjectPut {} = {}", key, value);
		} catch (Exception e)
		{
			log.warn("mapObjectPut {} = {}", key, value, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long mapRemove(String key, String mapKey)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.hdel(key, mapKey);
			log.debug("mapRemove {}  {}", key, mapKey);
		} catch (Exception e)
		{
			log.warn("mapRemove {}  {}", key, mapKey, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long mapObjectRemove(String key, String mapKey)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
			log.debug("mapObjectRemove {}  {}", key, mapKey);
		} catch (Exception e)
		{
			log.warn("mapObjectRemove {}  {}", key, mapKey, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static boolean mapExists(String key, String mapKey)
	{
		boolean result = false;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.hexists(key, mapKey);
			log.debug("mapExists {}  {}", key, mapKey);
		} catch (Exception e)
		{
			log.warn("mapExists {}  {}", key, mapKey, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static boolean mapObjectExists(String key, String mapKey)
	{
		boolean result = false;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
			log.debug("mapObjectExists {}  {}", key, mapKey);
		} catch (Exception e)
		{
			log.warn("mapObjectExists {}  {}", key, mapKey, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static long del(String key)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(key))
			{
				result = jedis.del(key);
				log.debug("del {}", key);
			} else
			{
				log.debug("del {} not exists", key);
			}
		} catch (Exception e)
		{
			log.warn("del {}", key, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static long delObject(String key)
	{
		long result = 0;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			if (jedis.exists(getBytesKey(key)))
			{
				result = jedis.del(getBytesKey(key));
				log.debug("delObject {}", key);
			} else
			{
				log.debug("delObject {} not exists", key);
			}
		} catch (Exception e)
		{
			log.warn("delObject {}", key, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static boolean exists(String key)
	{
		boolean result = false;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.exists(key);
			log.debug("exists {}", key);
		} catch (Exception e)
		{
			log.warn("exists {}", key, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static boolean existsObject(String key)
	{
		boolean result = false;
		Jedis jedis = null;
		try
		{
			jedis = getResource();
			result = jedis.exists(getBytesKey(key));
			log.debug("existsObject {}", key);
		} catch (Exception e)
		{
			log.warn("existsObject {}", key, e);
		} finally
		{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取资源
	 * 
	 * @return
	 * @throws JedisException
	 */
	public static Jedis getResource() throws JedisException
	{
		Jedis jedis = null;
		try
		{
			jedis = jedisPool.getResource();
			// log.debug("getResource.", jedis);
		} catch (JedisException e)
		{
			log.warn("getResource.", e);
			returnBrokenResource(jedis);
			throw e;
		}
		return jedis;
	}

	/**
	 * 归还资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	@SuppressWarnings("deprecation")
	public static void returnBrokenResource(Jedis jedis)
	{
		if (jedis != null)
		{
			jedisPool.returnBrokenResource(jedis);
		}
	}

	/**
	 * 释放资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	@SuppressWarnings("deprecation")
	public static void returnResource(Jedis jedis)
	{
		if (jedis != null)
		{
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 获取byte[]类型Key
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] getBytesKey(Object object)
	{
		if (object instanceof String)
		{
			return StringUtils.getBytes((String) object);
		} else
		{
			return ObjectUtils.serialize(object);
		}
	}

	/**
	 * Object转换byte[]类型
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] toBytes(Object object)
	{
		return ObjectUtils.serialize(object);
	}

	/**
	 * byte[]型转换Object
	 * 
	 * @param key
	 * @return
	 */
	public static Object toObject(byte[] bytes)
	{
		return ObjectUtils.unserialize(bytes);
	}

}
