# 《redis深度历险》学习笔记
## redis五种数据结构
- string
> 命令：
>> set key value；
>>get key；
>>expire key；
>>setex key second value;
>>setnx key name
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