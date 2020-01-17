# 《redis深度历险》学习笔记

## redis基础知识，包含常见命令
- 常用针对key的命令
>命令
>>DEL key：该命令用于在 key 存在时删除 key
>>EXISTS key：检查给定 key 是否存在
>>EXPIRE key seconds：为给定 key 设置过期时间，以秒计
>>PEXPIRE key milliseconds：设置 key 的过期时间以毫秒计
>>KEYS pattern：查找所有符合给定模式( pattern)的 key
>>PTTL key：以毫秒为单位返回 key 的剩余的过期时间
>>TTL key ：以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)
>>
>>DUMP key：序列化给定 key ，并返回被序列化的值
>>EXPIREAT key timestamp：EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置过期时间。 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)
>>PEXPIREAT key milliseconds-timestamp ：设置 key 过期时间的时间戳(unix timestamp) 以毫秒计
>>MOVE key db：将当前数据库的 key 移动到给定的数据库 db 当中
>>PERSIST key ：移除 key 的过期时间，key 将持久保持
>>RANDOMKEY：从当前数据库中随机返回一个 key
>>RENAME key newkey ：修改 key 的名称
>>RENAMENX key newkey ：仅当 newkey 不存在时，将 key 改名为 newkey 
>>TYPE key ：返回 key 所储存的值的类型

### redis五种数据结构
- string
> 命令：
>>SET key value：设置指定 key 的值
>>GET key：获取指定 key 的值
>>GETSET key value：将给定 key 的值设为 value ，并返回 key 的旧值(old value)
>>MGET key1 [key2..]：获取所有(一个或多个)给定 key 的值
>>SETEX key seconds value：将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)
>>SETNX key value：只有在 key 不存在时设置 key 的值
>>STRLEN key：返回 key 所储存的字符串值的长度
>>MSET key value [key value ...]：同时设置一个或多个 key-value 对
>>MSETNX key value [key value ...]：同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
>>INCR key：将 key 中储存的数字值增一
>>INCRBY key increment：将 key 所储存的值加上给定的增量值（increment）
>>DECR key：将 key 中储存的数字值减一
>>DECRBY key decrement：所储存的值减去给定的减量值（decrement）
>>
>>GETRANGE key start end：返回 key 中字符串值的子字符
>>GETBIT key offset：对 key 所储存的字符串值，获取指定偏移量上的位(bit)
>>SETBIT key offset value：对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)
>>SETRANGE key offset value：用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
>>PSETEX key milliseconds value：这个命令和 SETEX 命令相似，但它以毫秒为单位设置 key 的生存时间，而不是像 SETEX 命令那样，以秒为单位
>>INCRBYFLOAT key increment：将 key 所储存的值加上给定的浮点增量值（increment）
>>APPEND key value：如果 key 已经存在并且是一个字符串， APPEND 命令将指定的 value 追加到该 key 原来值（value）的末尾

- list 是底层是链表结构，类似于LinkedList，可以实现右进左出或右进右出，也就是队列和链表数据结构；
redis内部实际上并不是使用纯粹的链表结构来实现，而是一种叫做quickList的一个结构：首先在元素较少的情况下会使用一块连续的内存，这个结构称为zipList，也就是压缩列表。
当数据量较多时，会改成quickList，将多个zipList使用双向指针串起来使用，这样既满足了快速的插入删除性能，又不会出现太大的空间冗余。
>命令：
>>BLPOP key1 [key2 ] timeout：移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
>>BRPOP key1 [key2 ] timeout：移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
>>LINDEX key index ：通过索引获取列表中的元素
>>LLEN key：获取列表长度
>>LPOP key ：移出并获取列表的第一个元素
>>LPUSH key value1 [value2] ：将一个或多个值插入到列表头部
>>
>>BRPOPLPUSH source destination timeout：从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
>>LINSERT key BEFORE|AFTER pivot value ：在列表的元素前或者后插入元素
>>LPUSHX key value：将一个值插入到已存在的列表头部
>>LRANGE key start stop ：获取列表指定范围内的元素
>>LREM key count value：移除列表元素
>>LSET key index value：通过索引设置列表元素的值
>>LTRIM key start stop：对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
>>RPOP key：移除列表的最后一个元素，返回值为移除的元素
>>RPOPLPUSH source destination：移除列表的最后一个元素，并将该元素添加到另一个列表并返回
>>RPUSH key value1 [value2]：在列表中添加一个或多个值
>> RPUSHX key value：为已存在的列表添加值

- hash，redis的hash和java的hashMap类似，都是数组+链表二维结构，不过redis的hash的value只能是字符串；
且rehash操作也不同，java的rehash是一次性全部rehash，大数据量下非常耗时，redis的rehash是渐进式hash，在hash的同时会保留新旧两个hash结构，
查询时也会同时查询两个hash结构，在后序的定时任务中循序渐进的将旧hash的内容一点点迁移到新hash中。
>命令
>>HDEL key field1 [field2]：删除一个或多个哈希表字段
>>HEXISTS key field：查看哈希表 key 中，指定的字段是否存在
>>HGET key field：获取存储在哈希表中指定字段的值
>>HGETALL key：获取在哈希表中指定 key 的所有字段和值
>>HINCRBY key field increment：为哈希表 key 中的指定字段的整数值加上增量 increment
>>HINCRBYFLOAT key field increment：为哈希表 key 中的指定字段的浮点数值加上增量 increment 
>>HKEYS key：获取所有哈希表中的字段
>>HLEN key：获取哈希表中字段的数量
>>HMGET key field1 [field2]：获取所有给定字段的值
>>HMSET key field1 value1 [field2 value2 ]：同时将多个 field-value (域-值)对设置到哈希表 key 中
>>HSET key field value：将哈希表 key 中的字段 field 的值设为 value
>>HSETNX key field value：只有在字段 field 不存在时，设置哈希表字段的值
>>HVALS key：获取哈希表中所有值
>>HSCAN key cursor [MATCH pattern] [COUNT count]：迭代哈希表中的键值对

- set,相当于java中的hashSet
>命令
>>SADD key member1 [member2]：向集合添加一个或多个成员
>>SCARD key：获取集合的成员数
>>SDIFF key1 [key2]：返回给定所有集合的差集
>>SDIFFSTORE destination key1 [key2]：返回给定所有集合的差集并存储在 destination 中
>>SINTER key1 [key2]：返回给定所有集合的交集
>>SINTERSTORE destination key1 [key2]：返回给定所有集合的交集并存储在 destination 中
>>SISMEMBER key member：判断 member 元素是否是集合 key 的成员
>>SMEMBERS key：返回集合中的所有成员
>>SMOVE source destination member：将 member 元素从 source 集合移动到 destination 集合
>>SPOP key：移除并返回集合中的一个随机元素
>>SRANDMEMBER key [count]：返回集合中一个或多个随机数
>>SREM key member1 [member2]：移除集合中一个或多个成员
>>SUNION key1 [key2]：返回所有给定集合的并集
>>SUNIONSTORE destination key1 [key2]：所有给定集合的并集存储在 destination 集合中
>>SSCAN key cursor [MATCH pattern] [COUNT count]：迭代集合中的元素

- zset 有序列表，类似于java的SortedSet和HashMap的结合体，一方面是一个set，保证了内部value的唯一性，另一方面它可以给每个value赋予一个score，代表了这个value的排序权重，
内部实现是用一种【跳跃链表】的数据结构
>命令
>>ZADD key score1 member1 [score2 member2]：向有序集合添加一个或多个成员，或者更新已存在成员的分数
>>ZCARD key：获取有序集合的成员数
>>ZCOUNT key min max：计算在有序集合中指定区间分数的成员数
>>ZINCRBY key increment member：有序集合中对指定成员的分数加上增量 increment
>>ZINTERSTORE destination numkeys key [key ...]：计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
>>ZLEXCOUNT key min max：在有序集合中计算指定字典区间内成员数量
>>ZRANGE key start stop [WITHSCORES]：通过索引区间返回有序集合指定区间内的成员
>>ZRANGEBYLEX key min max [LIMIT offset count]：通过字典区间返回有序集合的成员
>>ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT]：通过分数返回有序集合指定区间内的成员
>>ZRANK key member：返回有序集合中指定成员的索引
>>ZREM key member [member ...]：移除有序集合中的一个或多个成员
>>ZREMRANGEBYLEX key min max：移除有序集合中给定的字典区间的所有成员
>>ZREMRANGEBYRANK key start stop：移除有序集合中给定的排名区间的所有成员
>>ZREMRANGEBYSCORE key min max：移除有序集合中给定的分数区间的所有成员
>>ZREVRANGE key start stop [WITHSCORES]：返回有序集中指定区间内的成员，通过索引，分数从高到低
>>ZREVRANGEBYSCORE key max min [WITHSCORES]：返回有序集中指定分数区间内的成员，分数从高到低排序
>>ZREVRANK key member：返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
>>ZSCORE key member：返回有序集中，成员的分数值
>>ZUNIONSTORE destination numkeys key [key ...]：计算给定的一个或多个有序集的并集，并存储在新的 key 中
>>ZSCAN key cursor [MATCH pattern] [COUNT count]：迭代有序集合中的元素（包括元素成员和元素分值）

### redis发布订阅
>>PSUBSCRIBE pattern [pattern ...]：订阅一个或多个符合给定模式的频道
>>PUBSUB subcommand [argument [argument ...]]：查看订阅与发布系统状态
>>PUBLISH channel message：将信息发送到指定的频道
>>PUNSUBSCRIBE [pattern [pattern ...]]：退订所有给定模式的频道
>>SUBSCRIBE channel [channel ...]：订阅给定的一个或多个频道的信息
>>UNSUBSCRIBE [channel [channel ...]]：指退订给定的频道

### redis事务
- Redis 事务可以一次执行多个命令， 并且带有以下三个重要的保证：
1.批量操作在发送 EXEC 命令前被放入队列缓存；
2.收到 EXEC 命令后进入事务执行，事务中任意命令执行失败，其余的命令依然被执行；
3.在事务执行过程，其他客户端提交的命令请求不会插入到事务执行命令序列中
>命令：
>>DISCARD：取消事务，放弃执行事务块内的所有命令
>>EXEC：执行所有事务块内的命令
>>MULTI：标记一个事务块的开始
>>UNWATCH：取消 WATCH 命令对所有 key 的监视
>>WATCH key [key ...]：监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断

## 应用：千帆竞发-分布式锁
- 通常使用set key value ex 过期时间 nx，如set flag true ex 5 nx，该操作是redis提供的一个原子性操作。
- redis无法解决持有锁的线程业务逻辑过长导致锁失效问题，也就是无法解决锁的续租问题，该问题在etcd中能够解决，并且redis分布式锁是AP模型，
而实际上分布式锁需要解决的业务场景是CP场景，所以一般极端情况下无法满足分布式锁的业务场景，可以使用zookeeper或者etcd来实现分布式锁。

## 应用：缓兵之计-延迟队列
- 使用redis的list实现队列消息，使用blpop/brpop来消费队列中消息，如果队列中没有消息则会睡眠

## 应用：节衣缩食-位图
- 主要能够节省存储空间，使用bite存储数据

## 应用：四两拨千斤-HyperLogLog
- redis提供的HyperLogLog这种数据类型是用来解决类似于UV这种统计问题，提供了不精确的去重计数方案，标准误差在0.81%。
>业务场景如下：一个有大量用户的平台需要访问每一个页面每天有多少用户访问，即UV；解决方案1：使用传统方案，即在redis中每一个页面存放一个set，
>redis的key是这个页面的标识，set中是访问的用户id，那么每一次都将访问的用户id添加（sadd）到set中，最后统计一下数量（scard）一下即可，但是这样
>当存在大量用户时会导致redis中每个页面的set中存放着大量的用户id，占用空间比较大，但是实际上只是为了统计数量。
>可以使用解决方案2：使用HyperLogLog来存储;每个TyperLogLog占据12k的存储空间，某些场景下就不合适，如每个用户一天的访问次数，如果用户的数量在百万甚至千万以上，那么就不合适，
>实际上HyperLogLog比较适合不精确统计，并且key的数量比较少的情况，如每个页面统计，一个网站的页面最多也就上百上千个，但是如果用户为key就不合适了，很多网站用户一般都上十万甚至更多。
>命令：
>>pfadd key value :增加
>>pfcount key value：统计数量

## 应用：层峦叠嶂-布隆过滤器
- 布隆过滤器是专门用来解决去重问题，不是完全精确，但是它占用空间非常小，一般节省90%左右的空间。该数据结构可以理解成是一个不那么精确的set。
- 当布隆过滤器说某个值存在时，这个值可能不存在；当布隆过滤器说某个值不存在是，该值肯定不存在。
- 注意布隆过滤器在原始redis中是没有的，需要单独下载并一插件的形式加载到redis中启动，具体详情可以百度
>命令
>>bf.add：添加元素
>>bf.exists：判断元素是否存在
>>bf.madd：批量添加元素
>>bf.mexists：批量判断元素是否存在
- 布隆过滤器原理--了解一下，主要能够节省比较大量的空间，可以显著的降低数据库的IO请求数量

## 应用：短尾求生-简单限流
- 为了控制流量，限流的一个目的是控制请求，防止大量请求把系统压垮；另外一个目的是控制用户行为。
- 场景：系统要限制某个用户的某个动作在某个时间范围内只允许操作N次，如一个用户一分钟只能够回复5个帖子
- 解决方案：这个限流需求中存在着一个滑动时间窗口，只用zset数据结构的score值圈出这个时间窗口，只需要保留时间窗口之内的值，时间窗口之外的不用保存。
- 一个zset结构记录用户的行为历史，每一个行为都会作为zset中的一个key保存下来。同一个用户同一种行为用一个zset记录。通过统计滑动窗口的行为数量与阈值max_count进行比较
就可以得出当前行为是否允许。

## 通用知识点
- 当出现批量执行命令的情况下，建议使用redis的管道：pipeline技术批量执行命令，大大缩短执行命令的时间，在十万级以上测试时时间差距在50倍左右，必要情况下可以分多次执行pipeline，如每次执行十万

## 应用：一毛不拔-漏斗限流
- 漏斗的容量是有限的，漏斗的剩余空间就代表当前行为可以持续进行的数量，漏斗的流水速率代表着系统允许该行为的最大频率。
- redis 4.0可以使用redis-cell模块实现漏斗算法：
cl.throttle KEY capacity(漏斗的容量，如30) operations(能够操作的次数，如30) seconds(表示时间周期) quota(默认是1，即一个一个流动)

## 应用：进水楼台-GeoHash
- redis 3.2版本增加地理位置GEO模块，能够实现附近的餐馆之类的功能
- 计算附近的点的做法业内一般使用GeoHash算法。GeoHash算法是将二维的经纬度数据映射到一维的整数，这样所有的元素都将在挂载到一条线上，
距离靠近的二维坐标映射到一维后的点之间的距离也会很接近
在使用Redis进行Geo查询时，它的内部结构实际上只是一个zset(skiplist)。通过zset的score排序就可以得到坐标附近的其他元素，通过将score
还原成坐标值就可以得到元素的原始坐标。
>命令
>>geoadd keyName 经度 纬度 keyValue：新增一个点位,如：
>>>geoadd company 116.48105 39.996794 juejin
>>>geoadd company 116.514203 39.905409 ireader
>>>geoadd company 116.489033 40.007669 meituan
>>>geoadd company 116.562108 39.787602 jd 116.334255 40.027400 xiaomi

>>geolist keyName keyValue1 keyValue2 单位：m/km/ml/ft：查询两个点之间的距离，如：
>>>geodist company juejin ireader km
>>>geodist company juejin jd km

>>geopos keyName keyValue：获取点位的经纬度，如：
>>>geopos company juejin

>>geohash keyName keyValue：获取元素经纬度编码字符串，使用http://geohash.org/${hash}中直接定位，如：
>>>geohash company juejin

>>georadiusbymember keyName keyValue 距离 距离单位 count 数量 正序或降序，如：georadiusbymember company，如：
>>>georadiusbymember company ireader 20 km count 3 asc：范围 20 公里以内最多 3 个元素按距离正排，它不会排除自身
>>>georadiusbymember company ireader 20 km count 3 desc:范围 20 公里以内最多 3 个元素按距离倒排
>>三个可选参数 withcoord withdist withhash 用来携带附加参数,withdist 很有用，它可以用来显示距离,如
>>> georadiusbymember company ireader 20 km withcoord withdist withhash count 3 asc

>>redis还提供根据经纬度查询附近的元素，具体参数与georadiusbymember相同，只需要将命令改为georadius，将keyValue改成经纬度即可，如：
>>>georadius company 116.514202 39.905409 20 km withdist count 3 asc

>注意事项：
>>在一个地图应用中，车的数据、餐馆的数据、人的数据可能会有百万千万条，如果使用Redis 的 Geo 数据结构，它们将全部放在一个 zset 集合中。
>>在 Redis 的集群环境中，集合可能会从一个节点迁移到另一个节点，如果单个 key 的数据过大，会对集群的迁移工作造成较大的影响，
>>在集群环境中单个 key 对应的数据量不宜超过 1M，否则会导致集群迁移出现卡顿现象，影响线上服务的正常运行。
>>所以，这里建议 Geo 的数据使用单独的 Redis 实例部署，不使用集群环境。
>>如果数据量过亿甚至更大，就需要对 Geo 数据进行拆分，按国家拆分、按省拆分，按
>>市拆分，在人口特大城市甚至可以按区拆分。这样就可以显著降低单个 zset 集合的大小。

## 应用：大海捞针-Scan
- redis提供keys patten来通过正则表达式查询指定的key，但是该方式有两个缺点：
1.没有 offset、 limit 参数， 一次性吐出所有满足条件的 key， 万一实例中有几百 w 个key 满足条件， 当你看到满屏的字符串刷的没有尽头时， 你就知道难受了。
2.keys 算法是遍历算法， 复杂度是 O(n)， 如果实例中有千万级以上的 key， 这个指令就会导致 Redis 服务卡顿， 所有读写 Redis 的其它的指令都会被延后甚至会超时报错， 
因为Redis 是单线程程序， 顺序执行所有指令， 其它指令必须等到当前的 keys 指令执行完了才可以继续。
- 为了解决这个问题，redis在2.8版本中提供了scan，scan的特点如下：
1.复杂度虽然也是 O(n)， 但是它是通过游标分步进行的， 不会阻塞线程;
2.提供 limit 参数， 可以控制每次返回结果的最大条数， limit 只是一个 hint， 返回的结果可多可少(limit不是限定返回结果的数量，而是限定服务器单词遍历的字典槽位数量);
3.同 keys 一样， 它也提供模式匹配功能;
4.服务器不需要为游标保存状态， 游标的唯一状态就是 scan 返回给客户端的游标整数;
5.返回的结果可能会有重复， 需要客户端去重复， 这点非常重要;
6.遍历的过程中如果有数据修改， 改动后的数据能不能遍历到是不确定的;
7.单次返回的结果是空的并不意味着遍历结束， 而要看返回的游标值是否为零;
>命令
>>scan cursor match patten count 数量，最开始cursor为0，每一次遍历的cursor为上一次遍历返回的cursor，直到cursor为0则表示遍历结束，如：scan 0 match key99* count 1000

>字典的结构：
>>在 Redis 中所有的 key 都存储在一个很大的字典中，这个字典的结构和 Java 中的HashMap 一样，是一维数组 + 二维链表结构，第一维数组的大小总是 2^n(n>=0)，扩容一
>>次数组大小空间加倍，也就是 n++。scan 指令返回的游标就是第一维数组的位置索引，我们将这个位置索引称为槽 (slot)。如果不考虑字典的扩容缩容，直接按数组下标挨个遍历就行了。
>>limit 参数就表示需要遍历的槽位数，之所以返回的结果可能多可能少，是因为不是所有的槽位上都会挂接链表，有些槽位可能是空的，还有些槽位上挂接的链表上的元素可能会有多个。
>>每一次遍历都会将 limit数量的槽位上挂接的所有链表元素进行模式匹配过滤后，一次性返回给客户端。

>>Java 的 HashMap 在扩容时会一次性将旧数组下挂接的元素全部转移到新数组下面。如果 HashMap 中元素特别多，线程就会出现卡顿现象。 Redis 为了解决这个问题，它采用渐
>>进式 rehash它会同时保留旧数组和新数组，然后在定时任务中以及后续对 hash 的指令操作中渐渐地将旧数组中挂接的元素迁移到新数组上。这意味着要操作处于 rehash 中的字典，
>需要同时访问新旧两个数组结构。如果在旧数组下面找不到元素，还需要去新数组下面去寻找。scan 也需要考虑这个问题，对与 rehash 中的字典，它需要同时扫描新旧槽位，然后将结果融合后返回给客户端。

## 原理：鞭辟入里-线程IO模型
- redis是个单线程程序。
- redis单线程为什么这么快？：因为它所有的数据都在内存中，所有的运算都是内存级别的运算。正因为 Redis 是单线程，所以要小心使用 Redis 指令，对于那些时间复杂度为 O(n) 级别的指令，
一定要谨慎使用，一不小心就可能会导致 Redis 卡顿。
- redis单线程如何处理那么多的并发客户端链接？：多路复用，select系列的时间轮询，非阻塞IO。
>非阻塞IO
>>非阻塞 IO 在套接字对象上提供了一个选项 Non_Blocking，当这个选项打开时，读写方法不会阻塞，而是能读多少读多少，能写多少写多少。能读多少取决于内核为套接字分配的
>>读缓冲区内部的数据字节数，能写多少取决于内核为套接字分配的写缓冲区的空闲空间字节数。读方法和写方法都会通过返回值来告知程序实际读写了多少字节。
>事件轮询（多路复用）
>>最简单的事件轮询 API 是 select 函数，它是操作系统提供给用户程序的 API。输入是读写描述符列表 read_fds & write_fds，输出是与之对应的可读可写事件。
>>同时还提供了一个 timeout 参数，如果没有任何事件到来，那么就最多等待 timeout 时间，线程处于阻塞状态。一旦期间有任何事件到来，就可以立即返回。时间过
>>了之后还是没有任何事件到来，也会立即返回。 拿到事件后，线程就可以继续挨个处理相应的事件。处理完了继续过来轮询。于是线程就进入了一个死循环，
>>我们把这个死循环称为事件循环，一个循环为一个周期。
```java
/*
伪代码如下：
read_events,write_events = select(read_fds,write_fds,timeout)
for event in read_events:
    handle_read(event.fd);
for event in write_events:
    handle_write(event.fd)
handle_others()
*/
```
>>因为我们通过 select 系统调用同时处理多个通道描述符的读写事件，因此我们将这类系统调用称为多路复用 API。现代操作系统的多路复用 API 已经不再使用 select 系统调用，
>>而改用 epoll(linux)和 kqueue(freebsd & macosx)，因为 select 系统调用的性能在描述符特别多时性能会非常差。
- 指令队列：Redis 会将每个客户端套接字都关联一个指令队列。客户端的指令通过队列来排队进行顺序处理，先到先服务。
- 响应队列：Redis 同样也会为每个客户端套接字关联一个响应队列。 Redis 服务器通过响应队列来将指令的返回结果回复给客户端。 如果队列为空，
那么意味着连接暂时处于空闲状态，不需要去获取写事件，也就是可以将当前的客户端描述符从 write_fds 里面移出来。等到队列有数据了，
再将描述符放进去。避免 select 系统调用立即返回写事件，结果发现没什么数据可以写。出这种情况的线程会飙高 CPU。
- 定时任务：Redis 的定时任务会记录在一个称为最小堆的数据结构中。这个堆中，最快要执行的任务排在堆的最上方。在每个循环周期， 
Redis 都会将最小堆里面已经到点的任务立即进行处理。处理完毕后，将最快要执行的任务还需要的时间记录下来，这个时间就是 select 系统调
用的 timeout 参数。因为 Redis 知道未来 timeout 时间内，没有其它定时任务需要处理，所以可以安心睡眠 timeout 的时间。

## 原理：交头接耳-通信协议
- RESP(Redis Serialization Protocol)：RESP 是 Redis 序列化协议的简写。它是一种直观的文本协议，优势在于实现异常简单，解析性能极好。
>Redis 协议将传输的结构数据分为 5 种最小单元类型，单元结束时统一加上回车换行符号\r\n。
>>1、 单行字符串 以 + 符号开头。
>>2、 多行字符串 以 $ 符号开头， 后跟字符串长度。
>>3、 整数值 以 : 符号开头， 后跟整数的字符串形式。
>>4、 错误消息 以 - 符号开头。
>>5、 数组 以 * 号开头， 后跟数组的长度。
- 客户端->服务器：客户端向服务器发送的指令只有一种格式，多行字符串数组；服务器->客户端：多种消息组合

## 原理：未雨绸缪-持久化
- redis的持久化机制有两种，一种是快照，另外一种是AOF日志。
### 快照原理
>快照是一次全量备份， AOF 日志是连续的增量备份。快照是内存数据的二进制序列化形式，在存储上非常紧凑，而 AOF 日志记录的是内存数据修改的指令记录文本。 
>AOF 日志在长期的运行过程中会变的无比庞大，数据库重启时需要加载 AOF 日志进行指令重放，这个时间就会无比漫长。所以需要定期进行 AOF 重写，给 AOF 日志进行瘦身.
- redis快照使用操作系统的多进程COW(Copy On Write)机制来实现快照持久化。
- fork(多进程)
>Redis 在持久化时会调用 glibc 的函数 fork 产生一个子进程，快照持久化完全交给子进程来处理，父进程继续处理客户端请求。子进程刚刚产生时，它和父进程共享内存里面的代
>码段和数据段。这时你可以将父子进程想像成一个连体婴儿，共享身体。这是 Linux 操作系统的机制，为了节约内存资源，所以尽可能让它们共享起来。在进程分离的一瞬间，内存的
>增长几乎没有明显变化。
> fork 函数会在父子进程同时返回，在父进程里返回子进程的 pid，在子进程里返回零。如果操作系统内存资源不足， pid 就会是负数，表示 fork 失败。
>子进程做数据持久化，它不会修改现有的内存数据结构，它只是对数据结构进行遍历读取，然后序列化写到磁盘中。但是父进程不一样，它必须持续服务客户端请求，然后对内存
>数据结构进行不间断的修改。
>这个时候就会使用操作系统的 COW 机制来进行数据段页面的分离。数据段是由很多操作系统的页面组合而成，当父进程对其中一个页面的数据进行修改时，会将被共享的页面复
>制一份分离出来，然后对这个复制的页面进行修改。这时子进程相应的页面是没有变化的，还是进程产生时那一瞬间的数据。
>随着父进程修改操作的持续进行，越来越多的共享页面被分离出来，内存就会持续增长。但是也不会超过原有数据内存的 2 倍大小。另外一个 Redis 实例里冷数据占的比例往
>往是比较高的，所以很少会出现所有的页面都会被分离，被分离的往往只有其中一部分页面。每个页面的大小只有 4K，一个 Redis 实例里面一般都会有成千上万的页面。
>子进程因为数据没有变化，它能看到的内存里的数据在进程产生的一瞬间就凝固了，再也不会改变，这也是为什么 Redis 的持久化叫「快照」的原因。接下来子进程就可以非常安
>心的遍历数据了进行序列化写磁盘了。
### AOF原理
>AOF 日志存储的是 Redis 服务器的顺序指令序列， AOF 日志只记录对内存进行修改的指令记录。
>假设 AOF 日志记录了自 Redis 实例创建以来所有的修改性指令序列，那么就可以对一个空的 Redis 实例顺序执行所有的指令，也就是「重放」，来恢复 Redis 当前实例的内
>存数据结构的状态。
>Redis 会在收到客户端修改指令后，先进行参数校验，如果没问题，就立即将该指令文本存储到 AOF 日志中，也就是先存到磁盘，然后再执行指令。这样即使遇到突发宕机，已
>经存储到 AOF 日志的指令进行重放一下就可以恢复到宕机前的状态
>Redis 在长期运行的过程中， AOF 的日志会越变越长。如果实例宕机重启，重放整个AOF 日志会非常耗时，导致长时间 Redis 无法对外提供服务。所以需要对 AOF 日志瘦身。
- AOF重写
>Redis 提供了 bgrewriteaof 指令用于对 AOF 日志进行瘦身。其原理就是开辟一个子进程对内存进行遍历转换成一系列 Redis 的操作指令，序列化到一个新的 AOF 日志文件中。
>序列化完毕后再将操作期间发生的增量 AOF 日志追加到这个新的 AOF 日志文件中，追加完毕后就立即替代旧的 AOF 日志文件了，瘦身工作就完成了。
- fsync
>AOF 日志是以文件的形式存在的，当程序对 AOF 日志文件进行写操作时，实际上是将内容写到了内核为文件描述符分配的一个内存缓存中，然后内核会异步将脏数据刷回到磁盘的。
>这就意味着如果机器突然宕机， AOF 日志内容可能还没有来得及完全刷到磁盘中，这个时候就会出现日志丢失。那该怎么办？
>Linux 的 glibc 提供了 fsync(int fd)函数可以将指定文件的内容强制从内核缓存刷到磁盘。只要 Redis 进程实时调用 fsync 函数就可以保证 aof 日志不丢失。但是 fsync 是一个
>磁盘 IO 操作，它很慢！如果 Redis 执行一条指令就要 fsync 一次，那么 Redis 高性能的地位就不保了。
>所以在生产环境的服务器中， Redis 通常是每隔 1s 左右执行一次 fsync 操作，周期 1s是可以配置的。这是在数据安全性和性能之间做了一个折中，在保持高性能的同时，尽可能
>使得数据少丢失。
### 运维
>快照是通过开启子进程的方式进行的，它是一个比较耗资源的操作。
>1、 遍历整个内存，大块写磁盘会加重系统负载
>2、 AOF 的 fsync 是一个耗时的 IO 操作，它会降低 Redis 性能，同时也会增加系统 IO 负担
>所以通常 Redis 的主节点是不会进行持久化操作，持久化操作主要在从节点进行。从节点是备份节点，没有来自客户端请求的压力，它的操作系统资源往往比较充沛。
>但是如果出现网络分区，从节点长期连不上主节点，就会出现数据不一致的问题，特别是在网络分区出现的情况下又不小心主节点宕机了，那么数据就会丢失，所以在生产环境要
>做好实时监控工作，保证网络畅通或者能快速修复。另外还应该再增加一个从节点以降低网络分区的概率，只要有一个从节点数据同步正常，数据也就不会轻易丢失。

## 原理：雷厉风行-管道
- redis管道是批量执行redis命令，只发生一次网络交互。实际上 Redis 管道(Pipeline) 本身并不是 Redis 服务器直接提供的技术，
这个技术本质上是由客户端提供的，跟服务器没有什么直接的关系。管道中指令越多，效果越好。
- redis自带的压测工具：redis-benchmark
### 深入理解管道本质
- 完整的请求交互流程：
>1.客户端进程调用 write 将消息写到操作系统内核为套接字分配的发送缓冲 send buffer。
>2.客户端操作系统内核将发送缓冲的内容发送到网卡， 网卡硬件将数据通过「网际路由」送到服务器的网卡。
>3.服务器操作系统内核将网卡的数据放到内核为套接字分配的接收缓冲 recv buffer
>4.服务器进程调用 read 从接收缓冲中取出消息进行处理。
>5.服务器进程调用 write 将响应消息写到内核为套接字分配的发送缓冲 send buffer。
>6.服务器操作系统内核将发送缓冲的内容发送到网卡， 网卡硬件将数据通过「网际路由」送到客户端的网卡。
>7.客户端操作系统内核将网卡的数据放到内核为套接字分配的接收缓冲 recv buffer。
>8.客户端进程调用 read 从接收缓冲中取出消息返回给上层业务逻辑进行处理。
>9.结束。
### 重要！！！网络IO耗时的真正面目以及epoll的本质👇
>我们开始以为 write 操作是要等到对方收到消息才会返回，但实际上不是这样的。 write操作只负责将数据写到本地操作系统内核的
>发送缓冲然后就返回了。剩下的事交给操作系统内核异步将数据送到目标机器。但是如果发送缓冲满了，那么就需要等待缓冲空出空闲空间
>来，这个就是写操作 IO 操作的真正耗时。
>我们开始以为 read 操作是从目标机器拉取数据，但实际上不是这样的。 read 操作只负责将数据从本地操作系统内核的接收缓冲中取出来就了事了。
>但是如果缓冲是空的，那么就需要等待数据到来，这个就是读操作 IO 操作的真正耗时。
>所以对于 value = redis.get(key)这样一个简单的请求来说， write 操作几乎没有耗时，直接写到发送缓冲就返回，而 read 就会比较耗时了，
>因为它要等待消息经过网络路由到目标机器处理后的响应消息,再回送到当前的内核读缓冲才可以返回。这才是一个网络来回的真正开销。
>而对于管道来说，连续的 write 操作根本就没有耗时，之后第一个 read 操作会等待一个网络的来回开销，然后所有的响应消息就都已经回送到内核的读缓冲了，
>后续的 read 操作直接就可以从缓冲拿到结果，瞬间就返回了。

## 原理：同舟共济-事务
- 每个事务的操作都有 begin、 commit 和 rollback， begin 指示事务的开始， commit 指示事务的提交， rollback 指示事务的回滚。
Redis 在形式上看起来也差不多，分别是 multi/exec/discard。 multi 指示事务的开始，exec 指示事务的执行， discard 指示事务的丢弃。
- 所有的指令在 exec 之前不执行，而是缓存在服务器的一个事务队列中，服务器一旦收到 exec 指令，才开执行整个事务队列，执行完毕
后一次性返回所有指令的运行结果。因为 Redis 的单线程特性，它不用担心自己在执行队列的时候被其它指令打搅，可以保证他们能得到的「原子性」执行。
- redis的事务只提供了隔离性，在事务中发生错误不会影响其中任何一个正确指令的执行，也就是不具备原子性。
- discard是在执行exec之前执行，会丢弃所有的指令。
- 可以使用pipeline来批量执行多个指令，节省网络IO。
- 分布式锁是一种悲观锁，也就是一种排他锁。
- **redis提供watch机制，类似于乐观锁，当执行指令时如果watch到关心的数据有改变，则会执行指令失败，并抛出异常**
```java
/*
使用watch机制的伪代码
while true
    do_watch()
    commands()
    multi()
    send_commands()
    try
        exec()
        break
    except WatchError
        continue
*/
```
```java
package com.ant;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis事务demo
 */
public class TransactionDemo {
    public static void main(String[] args) throws Exception{
        Jedis jedis = new Jedis();
        String userId = "ant";
        String key = String.format("account_{}",userId);
        PU.println(key);
        jedis.setnx(key,String.valueOf(6));
        int val = doubleAccount(jedis,key);
        PU.println(val);

    }
    public static int doubleAccount(Jedis jedis,String key)throws Exception{
        while (true){
            jedis.watch(key);
            TimeUnit.SECONDS.sleep(5);
            int value = Integer.parseInt(jedis.get(key));
            value *= 2;
            Transaction transaction = jedis.multi();
            transaction.set(key,String.valueOf(value));
            List<Object> exec = transaction.exec();
            if(exec!=null){
                //成功
                break;
            }
        }
        return Integer.parseInt(jedis.get(key));
    }
}

```

## 原理：小道消息-PubSub
- redis支持的消息队列模式的缺点是不支持多播机制。
- 消息多播：消息多播允许生产者生产一次消息，中间件负责将消息复制到多个消息队列，每个消息队列由相应的消费组进行消费。它是分布式系统常用的一种解耦方式，
用于将多个消费组的逻辑进行拆分。支持了消息多播，多个消费组的逻辑就可以放到不同的子系统中。

## 原理：开源节流-小对象压缩
>如果 Redis 内部管理的集合数据结构很小，它会使用紧凑存储形式压缩存储。
>这就好比 HashMap 本来是二维结构，但是如果内部元素比较少，使用二维结构反而浪费空间，还不如使用一维数组进行存储，需要查找时，因为元素少进行遍历也很快，
>甚至可以比 HashMap 本身的查找还要快。比如下面我们可以使用数组来模拟 HashMap 的增删改操作。
>Redis 的 ziplist 是一个紧凑的字节数组结构，如下图所示，每个元素之间都是紧挨着的。如果它存储的是 hash 结构，那么 key 和 value 会作为两个 entry 相邻存在一起。
>如果它存储的是 zset，那么 value 和 score 会作为两个 entry 相邻存在一起。
>Redis 的 intset 是一个紧凑的整数数组结构，它用于存放元素都是整数的并且元素个数较少的 set 集合。
>如果整数可以用 uint16 表示，那么 intset 的元素就是 16 位的数组，如果新加入的整数超过了 uint16 的表示范围，那么就使用 uint32 表示，如果新加入的元素超过了 uint32
>的表示范围，那么就使用 uint64 表示， Redis 支持 set 集合动态从 uint16 升级到 uint32，再升级到 uint64。
>如果 set 里存储的是字符串，那么 sadd 立即升级为 hashtable 结构。
### 内存回收机制
>Redis 并不总是可以将空闲内存立即归还给操作系统。
>如果当前 Redis 内存有 10G，当你删除了 1GB 的 key 后，再去观察内存，你会发现内存变化不会太大。原因是操作系统回收内存是以页为单位，如果这个页上只要有一个 key
>还在使用，那么它就不能被回收。 Redis 虽然删除了 1GB 的 key，但是这些 key 分散到了很多页面中，每个页面都还有其它 key 存在，这就导致了内存不会立即被回收。
>不过，如果你执行 flushdb，然后再观察内存会发现内存确实被回收了。原因是所有的key 都干掉了，大部分之前使用的页面都完全干净了，会立即被操作系统回收。
>Redis 虽然无法保证立即回收已经删除的 key 的内存，但是它会重用那些尚未回收的空闲内存。这就好比电影院里虽然人走了，但是座位还在，下一波观众来了，直接坐就行。而
>操作系统回收内存就好比把座位都给搬走了。

## 原理：有备无患-主从同步
- CAP原理
>C-Consistent：一致性
>A-Availability：可用性
>P-Partition tolerance：分区容忍性/分区容错性
>>分布式系统的节点往往都是分布在不同的机器上进行网络隔离开的，这意味着必然会有网络断开的风险，这个网络断开的场景的专业词汇叫着「 网络分区」。
>>在网络分区发生时，两个分布式节点之间无法进行通信，我们对一个节点进行的修改操作将无法同步到另外一个节点，所以数据的「一致性」将无法满足，
>>因为两个分布式节点的数据不再保持一致。除非我们牺牲「可用性」，也就是暂停分布式节点服务，在网络分区发生时，不再提供修改数据的功能，直到网络状况完全恢复正常再继续对外提供服务。
- 一句话概括 CAP 原理就是——网络分区发生时，一致性和可用性两难全。
- redis是AP模型，所以redis做分布式锁有风险
>Redis 的主从数据是异步同步的，所以分布式的 Redis 系统并不满足「 一致性」要求。
- 最终一致性，redis主从复制是异步同步的，在主从网络断开的情况下，主节点依旧可以正常对外提供修改服务，所以 Redis 满足「 可用性」。Redis 保证「 最终一致性」.

## 集群：李代桃僵-sentinel（哨兵）
>我们可以将 Redis Sentinel 集群看成是一个 ZooKeeper 集群，它负责持续监控主从节点的健康，当主节点挂掉时，自动选择一个最优的从节点切换为
>主节点。客户端来连接集群时，会首先连接 sentinel，通过 sentinel 来查询主节点的地址，然后再去连接主节点进行数据交互。当主节点发生故障时，
>客户端会重新向 sentinel 要地址， sentinel 会将最新的主节点地址告诉客户端。如此应用程序将无需重启即可自动完成节点切换。
- 消息丢失
>Redis 主从采用异步复制，意味着当主节点挂掉时，从节点可能没有收到全部的同步消息，这部分未同步的消息就丢失了。如果主从延迟特别大，那么丢失的数据就可能会特别
>多。 Sentinel 无法保证消息完全不丢失，但是也尽可能保证消息少丢失。它有两个选项可以限制主从延迟过大。min-slaves-to-write 1、min-slaves-max-lag 10
>第一个参数表示主节点必须至少有一个从节点在进行正常复制，否则就停止对外写服务，丧失可用性。（CP？）何为正常复制，何为异常复制？这个就是由第二个参数控制的，
>它的单位是秒，表示如果 10s 没有收到从节点的反馈，就意味着从节点同步不正常，要么网络断开了，要么一直没有给反馈。

## 集群：分而治之-codis
- codis是一个redis集群中间件，能够把多个小的redis实例（可以是主从）综合起来，是Go语言开发，使用redis协议，无状态的一个中间件，
支持动态增加redis，codis同样可以做集群。
- codis分片原理：
>Codis 将所有的 key 默认划分为 1024 个槽位(slot)，它首先对客户端传过来的 key 进行 crc32 运算计算哈希值，
再将 hash 后的整数值对 1024 这个整数进行取模得到一个余数，这个余数就是对应 key 的槽位。
- codis之间的槽位映射：
>如果 Codis 的槽位映射关系只存储在内存里，那么不同的 Codis 实例之间的槽位关系就无法得到同步。
所以 Codis 还需要一个分布式配置存储数据库专门用来持久化槽位关系。Codis 开始使用 ZooKeeper，后来连 etcd 也一块支持了。
Codis 将槽位关系存储在 zk 中，并且提供了一个 Dashboard 可以用来观察和修改槽位关系，当槽位关系变化时， 
Codis Proxy 会监听到变化并重新同步槽位关系，从而实现多个Codis Proxy 之间共享相同的槽位关系配置。
- 扩容
>刚开始 Codis 后端只有一个 Redis 实例， 1024 个槽位全部指向同一个 Redis。然后一个 Redis 实例内存不够了，所以又加了一个 Redis 实例。
>这时候需要对槽位关系进行调整，将一半的槽位划分到新的节点。这意味着需要对这一半的槽位对应的所有 key 进行迁移，迁移到新的 Redis 实例。
>Codis 对 Redis 进行了改造，增加了 SLOTSSCAN 指令，可以遍历指定 slot 下所有的key。 Codis 通过 SLOTSSCAN 扫描出待迁移槽位的所有的 key，
>然后挨个迁移每个 key 到新的 Redis 节点。
>在迁移过程中， Codis 还是会接收到新的请求打在当前正在迁移的槽位上，Codis 无法判定迁移过程中的 key 究竟在哪个实例中，所以它采用了另
>一种完全不同的思路。当 Codis 接收到位于正在迁移槽位中的 key 后，会立即强制对当前的单个 key 进行迁移，迁移完成后，再将请求转发到新的 Redis 实例。
- 自动均衡
>Redis 新增实例，手工均衡 slots 太繁琐，所以 Codis 提供了自动均衡功能。自动均衡会在系统比较空闲的时候观察每个 Redis 实例对应的 Slots 数量，
>如果不平衡，就会自动进行迁移。
- codis的代价
>Codis 给 Redis 带来了扩容的同时，也损失了其它一些特性。因为 Codis 中所有的 key分散在不同的 Redis 实例中，所以事务就不能再支持了;
>同样为了支持扩容，单个 key 对应的 value 不宜过大，因为集群的迁移的最小单位是key，对于一个 hash 结构，它会一次性使用 hgetall 拉取所有的内容，
>然后使用 hmset 放置到另一个节点。如果 hash 内部的 kv 太多，可能会带来迁移卡顿。官方建议单个集合结构的总字节容量不要超过 1M。
- codis的优点
>Codis 在设计上相比 Redis Cluster 官方集群方案要简单很多，因为它将分布式的问题交给了第三方 zk/etcd 去负责，自己就省去了复杂的分布式一致性
>代码的编写维护工作。而Redis Cluster 的内部实现非常复杂，它为了实现去中心化，混合使用了复杂的 Raft 和Gossip 协议，还有大量的需要调优的配置参数，
>当集群出现故障时，维护人员往往不知道从何处着手。
- MGET指令的操作过程
>mget 指令用于批量获取多个 key 的值，这些 key 可能会分布在多个 Redis 实例中。Codis 的策略是将 key 按照所分配的实例打散分组，
>然后依次对每个实例调用 mget 方法，最后将结果汇总为一个，再返回给客户端。

## 集群：众志成城-cluster
> RedisCluster 是 Redis 的亲儿子，它是 Redis 作者自己提供的 Redis 集群化方案。相对于 Codis 的不同，它是去中心化的。
Redis Cluster 将所有数据划分为 16384 的 slots，它比 Codis 的 1024 个槽划分的更为精细，每个节点负责其中一部分槽位。
>槽位的信息存储于每个节点中，它不像 Codis，它不需要另外的分布式存储来存储节点槽位信息。
>当 Redis Cluster 的客户端来连接集群时，它也会得到一份集群的槽位配置信息。这样当客户端要查找某个 key 时，可以直接定位到目标节点。
- 槽位定位算法
>Cluster 默认会对 key 值使用 crc32 算法进行 hash 得到一个整数值，然后用这个整数值对 16384 进行取模来得到具体槽位。
- redis cluster使用gossip协议来广播状态
>因为 Redis Cluster 是去中心化的，一个节点认为某个节点失联了并不代表所有的节点都认为它失联了。所以集群还得经过一次协商的过程，
>只有当大多数节点都认定了某个节点失联了，集群才认为该节点需要进行主从切换来容错。
>Redis 集群节点采用 Gossip 协议来广播自己的状态以及自己对整个集群认知的改变。比如一个节点发现某个节点失联了 (PFail)，它
>会将这条信息向整个集群广播，其它节点也就可以收到这点失联信息。如果一个节点收到了某个节点失联的数量 (PFail Count) 已经达到
>了集群的大多数，就可以标记该节点为确定下线状态 (Fail)，然后向整个集群广播，强迫其它节点也接收该节点已经下线的事实，并立即对该失联节点进行主从切换。







