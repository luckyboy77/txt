package wg.fnd.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import wg.fnd.utils.mns.MQServiceHelper;

/**
 * Redis缓存处理类
 * 
 * @name RedisCacheHelper
 * @description
 * @author xianzhi.chen@hand-china.com 2017年3月15日下午4:30:24
 * @version
 */
public class RedisCacheHelper<T> {

	@Autowired
	protected ObjectMapper objectMapper;

	private Logger logger = LoggerFactory.getLogger(MQServiceHelper.class);

	private RedisTemplate<String, String> redisTemplate;
	private String categroy;
	private ValueOperations<String, String> valueOpr;
	private ListOperations<String, String> listOpr;
	private SetOperations<String, String> setOpr;
	private ZSetOperations<String, String> zSetOpr;
	private HashOperations<String, String, String> hashOpr;
	
    protected RedisSerializer<String> strSerializer;

	public RedisCacheHelper(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
		strSerializer = redisTemplate.getStringSerializer();
		valueOpr = this.redisTemplate.opsForValue();
		listOpr = this.redisTemplate.opsForList();
		setOpr = this.redisTemplate.opsForSet();
		zSetOpr = this.redisTemplate.opsForZSet();
		hashOpr = this.redisTemplate.opsForHash();
	}

	/**
	 * 获取Key的全路径
	 * 
	 * @param key
	 * @return
	 */
	private String getFullKey(String key) {
		return this.categroy + ":" + key;
	}
	
	/**
	 * 设置事务处理开关
	 */
	public void setEnableTransactionSupport(boolean b) {
		redisTemplate.setEnableTransactionSupport(b);
	}


	/**
	 * 设置Key的TTL时间
	 * 
	 * @param key
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public Boolean expire(String key, long timeout, TimeUnit unit) {
		return redisTemplate.expire(getFullKey(key), timeout, unit);
	}

	/**
	 * 根据通配符获取key
	 * 
	 * @param key
	 */
	public Set<String> keys(String pattern) {
		return redisTemplate.keys(pattern);
	}
	
	/**
	 * 删除Key
	 * 
	 * @param key
	 */
	public void deleteKey(String key) {
		redisTemplate.delete(getFullKey(key));
	}

	/**
	 * 删除Key
	 * 
	 * @param keys
	 */
	public void deleteKeys(Collection<String> keys) {
		Set<String> hs = new HashSet<String>();
		for (String key : keys) {
			hs.add(getFullKey(key));
		}
		redisTemplate.delete(hs);
	}

	/**
	 * String 设置值
	 * 
	 * @param key
	 * @param value
	 */
	public void strSet(String key, String value) {
		valueOpr.set(getFullKey(key), value);
	}

	/**
	 * String 设置值（可指定TTL）
	 * 
	 * @param key
	 * @param value
	 * @param timeout
	 * @param unit
	 */
	public void strSet(String key, String value, long timeout, TimeUnit unit) {
		valueOpr.set(getFullKey(key), value, timeout, unit);
	}

	/**
	 * String 获取值
	 * 
	 * @param key
	 * @return
	 */
	public String strGet(String key) {
		return valueOpr.get(getFullKey(key));
	}

	/**
	 * String 获取值
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public String strGet(String key, Long start, Long end) {
		return valueOpr.get(getFullKey(key), start, end);
	}

	/**
	 * String 获取自增字段，递减字段可使用delta为负数的方式
	 * 
	 * @param key
	 * @param delta
	 * @return
	 */
	public Long strIncrement(String key, Long delta) {
		return valueOpr.increment(getFullKey(key), delta);
	}

	/**
	 * List 推入数据至列表左端
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lstLeftPush(String key, String value) {
		return listOpr.leftPush(getFullKey(key), value);
	}

	/**
	 * List 推入数据至列表左端
	 * 
	 * @param key
	 * @param values
	 *            Collection集合
	 * @return
	 */
	public Long lstLeftPushAll(String key, Collection<String> values) {
		return listOpr.leftPushAll(getFullKey(key), values);
	}

	/**
	 * List 推入数据至列表右端
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lstRightPush(String key, String value) {
		return listOpr.rightPush(getFullKey(key), value);
	}

	/**
	 * List 推入数据至列表右端
	 * 
	 * @param key
	 * @param values
	 *            Collection集合
	 * @return
	 */
	public Long lstRightPushAll(String key, Collection<String> values) {
		return listOpr.rightPushAll(getFullKey(key), values);
	}

	/**
	 * List 返回列表键key中，从索引start至索引end范围的所有列表项。两个索引都可以是正数或负数
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> lstRange(String key, long start, long end) {
		return listOpr.range(getFullKey(key), start, end);
	}

	/**
	 * List 移除并返回列表最左端的项
	 * 
	 * @param key
	 * @return
	 */
	public String lstLeftPop(String key) {
		return listOpr.leftPop(getFullKey(key));
	}

	/**
	 * List 移除并返回列表最右端的项
	 * 
	 * @param key
	 * @return
	 */
	public String lstRightPop(String key) {
		return listOpr.rightPop(getFullKey(key));
	}

	/**
	 * List 返回指定key的长度
	 * 
	 * @param key
	 * @return
	 */
	public Long lstLen(String key) {
		return listOpr.size(getFullKey(key));
	}

	/**
	 * List 设置指定索引上的列表项。将列表键 key索引index上的列表项设置为value。
	 * 如果index参数超过了列表的索引范围，那么命令返回了一个错误
	 * 
	 * @param key
	 * @param index
	 * @param value
	 */
	public void lstSet(String key, long index, String value) {
		listOpr.set(getFullKey(key), index, value);
	}

	/**
	 * List 根据参数 count的值，移除列表中与参数value相等的元素。 count的值可以是以下几种：count > 0
	 * :从表头开始向表尾搜索，移除与 value相等的元素，数量为 count
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	public Long lstRemove(String key, long index, String value) {
		return listOpr.remove(getFullKey(key), index, value);
	}

	/**
	 * List 返回列表键key中，指定索引index上的列表项。index索引可以是正数或者负数
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public String lstIndex(String key, long index) {
		return listOpr.index(getFullKey(key), index);
	}

	/**
	 * List 对一个列表进行修剪(trim)，让列表只保留指定索引范围内的列表项，而将不在范围内的其它列表项全部删除。 两个索引都可以是正数或者负数
	 * 
	 * @param key
	 * @param start
	 * @param end
	 */
	public void lstTrim(String key, long start, long end) {
		listOpr.trim(getFullKey(key), start, end);
	}

	/**
	 * Set 将一个或多个元素添加到给定的集合里面，已经存在于集合的元素会自动的被忽略， 命令返回新添加到集合的元素数量。
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public Long setAdd(String key, String... values) {
		return setOpr.add(getFullKey(key), values);
	}

	/**
	 * Set 将返回集合中所有的元素。
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> setMembers(String key) {
		return setOpr.members(getFullKey(key));
	}

	/**
	 * Set 检查给定的元素是否存在于集合
	 * 
	 * @param key
	 * @param o
	 * @return
	 */
	public Boolean setIsmember(String key, String o) {
		return setOpr.isMember(getFullKey(key), o);
	}

	/**
	 * Set 返回集合包含的元素数量（也即是集合的基数）
	 * 
	 * @param key
	 * @return
	 */
	public Long setSize(String key) {
		return setOpr.size(getFullKey(key));
	}

	/**
	 * Set 计算所有给定集合的交集，并返回结果
	 * 
	 * @param key
	 * @param otherKey
	 * @return
	 */
	public Set<String> setIntersect(String key, String otherKey) {
		return setOpr.intersect(getFullKey(key), otherKey);
	}

	/**
	 * Set 计算所有的并集并返回结果
	 * 
	 * @param key
	 * @param otherKeys
	 * @return
	 */
	public Set<String> setUnion(String key, String otherKey) {
		return setOpr.union(getFullKey(key), otherKey);
	}

	/**
	 * Set 计算所有的并集并返回结果
	 * 
	 * @param key
	 * @param otherKeys
	 * @return
	 */
	public Set<String> setUnion(String key, Collection<String> otherKeys) {
		return setOpr.union(getFullKey(key), otherKeys);
	}

	/**
	 * Set 返回一个集合的全部成员，该集合是所有给定集合之间的差集
	 * 
	 * @param key
	 * @param otherKey
	 * @return
	 */
	public Set<String> setDifference(String key, String otherKey) {
		return setOpr.difference(getFullKey(key), otherKey);
	}

	/**
	 * Set 返回一个集合的全部成员，该集合是所有给定集合之间的差集
	 * 
	 * @param key
	 * @param otherKeys
	 * @return
	 */
	public Set<String> setDifference(String key, Collection<String> otherKeys) {
		return setOpr.difference(getFullKey(key), otherKeys);
	}

	/**
	 * set 删除数据
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long setRemove(String key, String value) {
		return setOpr.remove(getFullKey(key), value);
	}

	/**
	 * ZSet Zadd 命令用于将一个或多个成员元素及其分数值加入到有序集当中。
	 * 如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证该成员在正确的位置上。
	 * 分数值可以是整数值或双精度浮点数。 如果有序集合 key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当 key
	 * 存在但不是有序集类型时，返回一个错误。
	 * 
	 * @param key
	 * @param value
	 * @param score
	 */
	public Boolean zSetAdd(String key, String value, double score) {
		return zSetOpr.add(getFullKey(key), value, score);
	}

	/**
	 * ZSet 返回有序集合中，指定元素的分值
	 * 
	 * @param key
	 * @param o
	 * @return
	 */
	public Double zSetScore(String key, String value) {
		return zSetOpr.score(getFullKey(key), value);
	}

	/**
	 * ZSet 为有序集合指定元素的分值加上增量increment，命令返回执行操作之后，元素的分值 可以通过将 increment设置为负数来减少分值
	 * 
	 * @param key
	 * @param value
	 * @param delta
	 * @return
	 */
	public Double zSetIncrementScore(String key, String value, double delta) {
		return zSetOpr.incrementScore(getFullKey(key), value, delta);
	}

	/**
	 * ZSet 返回指定元素在有序集合中的排名，其中排名按照元素的分值从小到大计算。排名以 0 开始
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long zSetRank(String key, String value) {
		return zSetOpr.rank(getFullKey(key), value);
	}

	/**
	 * ZSet 返回成员在有序集合中的逆序排名，其中排名按照元素的分值从大到小计算
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long zSetReverseRank(String key, String value) {
		return zSetOpr.reverseRank(getFullKey(key), value);
	}

	/**
	 * ZSet 返回有序集合的基数
	 * 
	 * @param key
	 * @return
	 */
	public Long zSetSize(String key) {
		return zSetOpr.size(getFullKey(key));
	}

	/**
	 * ZSet 删除数据
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long zSetRemove(String key, String value) {
		return zSetOpr.remove(getFullKey(key), value);
	}

	/**
	 * ZSet 返回有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从大到小)的次序排列。
	 * 具有相同分数值的成员按字典序的逆序(reverse lexicographical order )排列。
	 * 
	 * @param key
	 *            Redis Key
	 * @param start
	 * @param end
	 * @return Set
	 */
	public Set<String> zSetRange(String key, Long start, Long end) {
		return zSetOpr.range(getFullKey(key), start, end);
	}

	/**
	 * ZSet
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> zSetReverseRange(String key, Long start, Long end) {
		return zSetOpr.reverseRange(getFullKey(key), start, end);
	}

	/**
	 * ZSet 返回有序集合在按照分值升序排列元素的情况下，分值在 min 和 max范围之内的所有元素
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> zSetRangeByScore(String key, Double min, Double max) {
		return zSetOpr.rangeByScore(getFullKey(key), min, min);
	}

	/**
	 * ZSet 返回有序集合在按照分值降序排列元素的情况下，分值在 min 和 max范围之内的所有元素
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> zSetReverseRangeByScore(String key, Double min,
			Double max) {
		return zSetOpr.reverseRangeByScore(getFullKey(key), min, min);
	}

	/**
	 * ZSet 返回有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从小到大)的次序排列。
	 * 具有相同分数值的成员按字典序的顺序(reverse lexicographical order )排列。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return Set
	 */
	public Set<String> zSetRangeByScore(String key, Double min, Double max,
			Long offset, Long count) {
		return zSetOpr.rangeByScore(getFullKey(key), min, max, offset, count);
	}

	/**
	 * 返回有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从大到小)的次序排列。 具有相同分数值的成员按字典序的逆序(reverse
	 * lexicographical order )排列。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<String> zSetReverseRangeByScore(String key, Double min,
			Double max, Long offset, Long count) {
		return zSetOpr.reverseRangeByScore(getFullKey(key), min, max, offset,
				count);
	}

	/**
	 * ZSet 返回有序集合在升序排列元素的情况下，分值在 min和 max范围内的元素数量
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Long zSetCount(String key, Double min, Double max) {
		return zSetOpr.count(getFullKey(key), min, max);
	}

	/**
	 * Hash 将哈希表 key 中的域 field的值设为 value。如果 key不存在，一个新的哈希表被创建并进行HSET操作。 如果域
	 * field已经存在于哈希表中，旧值将被覆盖
	 * 
	 * @param key
	 * @param hashKey
	 * @param value
	 */
	public void hshPut(String key, String hashKey, String value) {
		hashOpr.put(getFullKey(key), hashKey, value);
	}

	/**
	 * Hash 批量插入值，Map的key代表Field
	 * 
	 * @param key
	 * @param map
	 */
	public void hshPutAll(String key, Map<String, String> map) {
		hashOpr.putAll(key, map);
	}

	/**
	 * Hash 返回哈希表 key 中给定域 field的值，返回值：给定域的值。当给定域不存在或是给定 key不存在时，返回 nil。
	 * 
	 * @param key
	 * @param hashKey
	 * @return
	 */
	public String hshGet(String key, String hashKey) {
		return hashOpr.get(getFullKey(key), hashKey);
	}

	/**
	 * Hash 返回散列键 key 中，一个或多个域的值，相当于同时执行多个 HGET
	 * 
	 * @param key
	 * @param hashKeys
	 * @return
	 */
	public List<String> hshMultiGet(String key, Collection<String> hashKeys) {
		return hashOpr.multiGet(getFullKey(key), hashKeys);
	}

	/**
	 * Hash 获取散列Key中所有的键值对
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> hshGetAll(String key) {
		return hashOpr.entries(getFullKey(key));
	}

	/**
	 * Hash 查看哈希表 key 中，给定域 field是否存在
	 * 
	 * @param key
	 * @param hashKey
	 * @return
	 */
	public Boolean hshHasKey(String key, Object hashKey) {
		return hashOpr.hasKey(getFullKey(key), hashKey);
	}

	/**
	 * Hash 返回哈希表 key 中的所有域
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> hshKeys(String key) {
		return hashOpr.keys(getFullKey(key));
	}

	/**
	 * Hash 返回散列键 key 中，所有域的值
	 * 
	 * @param key
	 * @return
	 */
	public List<String> hshVals(String key) {
		return hashOpr.values(getFullKey(key));
	}

	/**
	 * Hash 返回散列键 key中指定Field的域的值
	 * 
	 * @param key
	 * @param hashKeys
	 * @return
	 */
	public List<String> hshVals(String key, Collection<String> hashKeys) {
		return hashOpr.multiGet(getFullKey(key), hashKeys);
	}

	/**
	 * Hash 散列键 key的数量
	 * 
	 * @param key
	 * @return
	 */
	public Long hshSize(String key) {
		return hashOpr.size(getFullKey(key));
	}
	
	/**
	 * Hash 删除散列键 key 中的一个或多个指定域，以及那些域的值。不存在的域将被忽略。命令返回被成功删除的域值对数量
	 * 
	 * @param key
	 * @param hashKeys
	 */
	public void hshDelete(String key, String... hashKeys) {
		hashOpr.delete(getFullKey(key), hashKeys);
	}

	public String objectToString(Object obj) {
		if (obj instanceof String || obj instanceof Number) {
			return obj.toString();
		} else {
			try {// JSON对象
				return objectMapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				if (logger.isErrorEnabled()) {
					logger.error("push data failed.", e);
				}
			}
		}
		return null;
	}

	/**
	 * 字符串转对象
	 *
	 * @param value
	 * @param cls
	 * @return
	 */
	public T stringToObject(String value, Class<T> cls) {
		try {
			return objectMapper.readValue(value, cls);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setCategroy(String categroy) {
		this.categroy = categroy;
	}

	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	public ValueOperations<String, String> getValueOpr() {
		return valueOpr;
	}

	public ListOperations<String, String> getListOpr() {
		return listOpr;
	}

	public SetOperations<String, String> getSetOpr() {
		return setOpr;
	}

	public ZSetOperations<String, String> getzSetOpr() {
		return zSetOpr;
	}

	public HashOperations<String, String, String> getHashOpr() {
		return hashOpr;
	}

}
