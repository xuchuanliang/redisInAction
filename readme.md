# 《redis深度历险》学习笔记
## redis五种数据结构

- string
> 命令：
>> set key value；
>>get key；
>>expire key 过期时间；设置多长时间过期
>>setex key 过期时间 value; 设置多长时间过期，相当于set key value + expire key 过期时间
>>setnx key value 如果不存在则执行
>> incr key 自增
>> incrby key count 增加count数量

- list 是底层是链表结构，类似于LinkedList，可以实现右进左出或右进右出，也就是队列和链表数据结构；
redis内部实际上并不是使用纯粹的链表结构来实现，而是一种叫做quickList的一个结构：首先在元素较少的情况下会使用一块连续的内存，这个结构称为zipList，也就是压缩列表。
当数据量较多时，会改成quickList，将多个zipList使用双向指针串起来使用，这样既满足了快速的插入删除性能，又不会出现太大的空间冗余。
>命令：
>>rpush listName key1 key2...
>>llen listName  获取长度
>>lpop listName
>>rpop listName
>>ltrim listName start_index end_index 保留开始和结束索引内的数据

- hash，redis的hash和java的hashMap类似，都是数组+链表二维结构，不过redis的hash的value只能是字符串；
且rehash操作也不同，java的rehash是一次性全部rehash，大数据量下非常耗时，redis的rehash是渐进式hash，在hash的同时会保留新旧两个hash结构，
查询时也会同时查询两个hash结构，在后序的定时任务中循序渐进的将旧hash的内容一点点迁移到新hash中。
>命令
>>hset hashName keyName value
>>hgetall hashName
>>hlen hashName
>>hget hashName keyName
>>hmset hashName keyName1 value1 keyName2 value2...批量在hash中设置值

- set,相当于java中的hashSet
>命令
>>sadd setName value
>>smembers setName  查询set集合内容
>>sismember setName value 查询某个value是否存在
>>scard setName 查询set长度
>>spop setName 弹出一个

- zset 有序列表，类似于java的SortedSet和HashMap的结合体，一方面是一个set，保证了内部value的唯一性，另一方面它可以给每个value赋予一个score，代表了这个value的排序权重，
内部实现是用一种【跳跃链表】的数据结构
>命令
>>zadd zsetName scoreValue value 
>>zrange zsetName 排序列出集合
>>zrevrange zsetName 逆序排列
>>zcard zsetName 查询zset长度
>>zrank zsetName value 查询该值的排名
>>zrangebyscore zsetName scoreStart scoreEnd 根据分值遍历zset
>>zrem zsetName key 删除

- 容器的通用命令和规则：list/set/hash/zset

## 千帆竞发-分布式锁
- 通常使用set key value ex 过期时间 nx，如set flag true ex 5 nx，该操作是redis提供的一个原子性操作。
- redis无法解决持有锁的线程业务逻辑过长导致锁失效问题，也就是无法解决锁的续租问题，该问题在etcd中能够解决，并且redis分布式锁是AP模型，
而实际上分布式锁需要解决的业务场景是CP场景，所以一般极端情况下无法满足分布式锁的业务场景，可以使用zookeeper或者etcd来实现分布式锁。

## 缓兵之计-延迟队列
- 使用redis的list实现队列消息，使用blpop/brpop来消费队列中消息，如果队列中没有消息则会睡眠

## 节衣缩食-位图
- 主要能够节省存储空间，使用bite存储数据

## 四两拨千斤-HyperLogLog
- redis提供的HyperLogLog这种数据类型是用来解决类似于UV这种统计问题，提供了不精确的去重计数方案，标准误差在0.81%。
>业务场景如下：一个有大量用户的平台需要访问每一个页面每天有多少用户访问，即UV；解决方案1：使用传统方案，即在redis中每一个页面存放一个set，
>redis的key是这个页面的标识，set中是访问的用户id，那么每一次都将访问的用户id添加（sadd）到set中，最后统计一下数量（scard）一下即可，但是这样
>当存在大量用户时会导致redis中每个页面的set中存放着大量的用户id，占用空间比较大，但是实际上只是为了统计数量。
>可以使用解决方案2：使用HyperLogLog来存储;每个TyperLogLog占据12k的存储空间，某些场景下就不合适，如每个用户一天的访问次数，如果用户的数量在百万甚至千万以上，那么就不合适，
>实际上HyperLogLog比较适合不精确统计，并且key的数量比较少的情况，如每个页面统计，一个网站的页面最多也就上百上千个，但是如果用户为key就不合适了，很多网站用户一般都上十万甚至更多。
>命令：
>>pfadd key value :增加
>>pfcount key value：统计数量

##层峦叠嶂-布隆过滤器
- 布隆过滤器是专门用来解决去重问题，不是完全精确，但是它占用空间非常小，一般节省90%左右的空间。该数据结构可以理解成是一个不那么精确的set。
- 当布隆过滤器说某个值存在时，这个值可能不存在；当布隆过滤器说某个值不存在是，该值肯定不存在。
- 注意布隆过滤器在原始redis中是没有的，需要单独下载并一插件的形式加载到redis中启动，具体详情可以百度
>命令
>>bf.add 添加元素
>>bf.exists 判断元素是否存在
>>bf.madd 批量添加元素
>>bf.mexists 批量判断元素是否存在
- 布隆过滤器原理--了解一下，主要能够节省比较大量的空间，可以显著的降低数据库的IO请求数量

##短尾求生-简单限流
- 为了控制流量，限流的一个目的是控制请求，防止大量请求把系统压垮；另外一个目的是控制用户行为。
- 场景：系统要限制某个用户的某个动作在某个时间范围内只允许操作N次，如一个用户一分钟只能够回复5个帖子
- 解决方案：这个限流需求中存在着一个滑动时间窗口，只用zset数据结构的score值圈出这个时间窗口，只需要保留时间窗口之内的值，时间窗口之外的不用保存。
- 一个zset结构记录用户的行为历史，每一个行为都会作为zset中的一个key保存下来。同一个用户同一种行为用一个zset记录。通过统计滑动窗口的行为数量与阈值max_count进行比较
就可以得出当前行为是否允许。

## 通用知识点
- 当出现批量执行命令的情况下，建议使用redis的管道：pipeline技术批量执行命令，大大缩短执行命令的时间，在十万级以上测试时时间差距在50倍左右，必要情况下可以分多次执行pipeline，如每次执行十万

