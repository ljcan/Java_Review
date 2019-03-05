### 一、List使用场景
##### 1.可以使用redis的list模拟队列,堆栈
##### 2.朋友圈点赞；
      规定:朋友圈内容的格式:
            1,内容:   user:x:post:x content来存储;
            2,点赞:  post:x:good list来存储;
      1，创建一条微博内容：set user:1:post:91 'hello redis';
      2，点赞：
                    lpush post:91:good '{id:1,name:stef,img:xxx.jpg}'
                    lpush post:91:good '{id:2,name:xl,img:xxx.jpg}'
                    lpush post:91:good '{id:3,name:xm,img:xxx.jpg}'
      3，查看有多少人点赞: llen post:91:good
      4，查看哪些人点赞：lrange post:91:good 0 -1

##### 3.回帖
     1，创建一个帖子：set user:1:post:90 'wohenshuai'
     2，创建一个回帖：set postreply:1 'nonono'
     3，把回帖和帖子关联：lpush post:90:replies 1
     4，再来一条回帖：set postreply:2 'hehe'
                               lpush post:90:replies 2
     5，查询帖子的回帖：lrange post:90:replies  0 -1
                               get postreply:2
                              
### 二、Set的使用场景
1. SET结构的常见操作：
```
	1，SADD：给set添加一个元素
	SADD language 'java'

	2，SREM：从set中移除一个给定元素
	SREM language 'php'

	3，SISMEMBER：判断给定的一个元素是否在set中，如果存在，返回1，如果不存在，返回0
	sismember language 'php'

	4，SMEMBERS：返回指定set内所有的元素，以一个list形式返回
	smembers language

	5，SCARD：返回set的元素个数
   	scard language

   	6，SRANDMEMBER key count:返回指定set中随机的count个元素
   	srandmember friends 3  //随机推荐3个用户（典型场景，抽奖）

	7，SUNION(并集)：综合多个set的内容，并返回一个list的列表，包含综合后的所有元素;
	sadd language 'php'
	sadd pg 'c'
	sadd pg 'c++'
	sadd pgs 'java'
	sadd pgs 'swift'
	sunion language pg pgs

	8，SINTER key [key ...] (交集)：获取多个key对应的set之间的交集
	SINTER friends:user:1000 friends:user:1001 friends:user:1002 =>获取1000,1001,1002三个用户的共同好友列表；

	9， SINTERSTORE destination key [key ...] ：获取多个key对应的set之间的交集，并保存为新的key值；目标也是一个set；
	SINTER groupfriends friends:user:1000 friends:user:1001 friends:user:1002 =>获取三个用户共同的好友列表并保存为组好友列表；
	
	10，SDIFF key1 key2:求差集
	    SDIFFSTORE key key1 key2:求差集并且存储在新的key中。
  ```
  2. set的使用场景：
  ```
    1，去重；
    2，抽奖;
           1,准备一个抽奖池:sadd luckdraws 1 2 3 4 5 6 7 8 9 10 11 12 13
           2,抽3个三等奖:srandmember luckdraws 3
                                        srem luckdraws 11 1 10
           3,抽2个二等奖:

    3，做set运算（好友推荐）
        1，初始化好友圈    sadd user:1:friends 'user:2' 'user:3' 'user:5'
                          sadd user:2:friends 'user:1' 'user:3' 'user:6'
                          sadd user:3:friends 'user:1' 'user:7' 'user:8'
        2,把user:1的好友的好友集合做并集;
            user:1 user:3 user:6 user:7 user:8
        3,让这个并集和user:1的好友集合做差集;
            user:1 user:6 user:7 user:8
        4,从差集中去掉自己
           user:6 user:7 user:8
        5,随机选取推荐好友 
   ```

### 三、Sort Set的使用场景
1. SET是一种非常方便的结构，但是数据无序，redis提供了一个sorted set，每一个添加的值都有一个对应的分数，可以通过这个分数进行排序;sorted set中的排名是按照分组升序排列
2. Sortedset的常用操作：
```
	1，ZADD：添加一个带分数的元素，也可以同时添加多个：
	ZADD hackers 1940 "Alan Kay"
	ZADD hackers 1906 "Grace Hopper"
	ZADD hackers 1969 "Linus Torvalds"
	ZADD hackers 1940 "Alan Kay" 1906 "Grace Hopper" 1969 "Linus Torvalds"
	
	2， ZCOUNT key min max ：给定范围分数的元素个数：
	ZCOUNT hackers 1940 1960 =>1940到1960的hacker个数；

	3， ZRANK key member ：查询指定元素的分数在整个列表中的排名（从0开始）
	ZRANK hackers "Alan Kay" =>alan kay的年龄在所有hacker中的排名；

	4，zrange hackers 0 -1 =>
	1) "Grace Hopper"
	2) "Alan Kay"
	3) "Linus Torvalds"

   	5，ZREVRANGE key start stop：按照分数从小到大排; 
```

3. sorted set的使用场景：sorted set算是redis中最有用的一种结构，非常适合用于做海量的数据的排行（比如一个巨型游戏的用户排名）；sorted set的速度非常快；
```
   示例1，天梯排名：
	1，添加初始排名和分数：
	2，查询fat在当前ladder中的排名：
	3，查询ladder中的前3名：
	4，jian增加了20ladder score：

   示例2，
              LRU淘汰最长时间没使用；
              LFU淘汰最低使用频率；
```	      

### 四、Hash的使用场景
1. hashes可以理解为一个map，这个map由一对一对的字段和值组成，所以，可以用hashes来保存一个对象：
2. hashes的常见操作：
```
	1，HSET:给一个hashes添加一个field和value；
	HSET user:1000 name "John Smith"
	HSET user:1000 email "john.smith@example.com"
	HSET user:1000 password "s3cret"
	
	2，HGET可以得到一个hashes中的某一个属性的值：
	HGET user:1000 name =>"John Smith"

	3，HGETALL：一次性取出一个hashes中所有的field和value，使用list输出，一个field，一个value有序输出；
	HGETALL user:1000 =>
	1) "name"
	2) "John Smith"
	3) "email"
	4) "john.smith@example.com"
	5) "password"
	6) "s3cret"

	4，HMSET:一次性的设置多个值(hashes multiple set)
	HMSET user:1001 name "Mary Jones" password "hidden" email "mjones@example.com"

	5，HMGET：一次性的得到多个字段值(hashes multiple get)，以列表形式返回；
	HMGET user:1001 name email =>
	1)"Mary Jones"
	2)"mjones@example.com"

	6，HINCRBY：给hashes的一个field的value增加一个值(integer)，这个增加操作是原子操作：
	HSET user:1000 visits 10
	HINCRBY user:1000 visits 1 => 11
	HINCRBY user:1000 visits 10 => 21

	7，HKEYS：得到一个key的所有fields字段，以list返回：
	HKEYS user:1000 =>
	1)"name"
	2)"password"
	3)"email"

	8，HDEL:删除hashes一个指定的filed；
	HDEL user:1000 visits
```
3. 使用场景:
 	1,使用hash来保存一个对象更直观;(建议不使用hash来保存)
	2,分组
                                     set  user:id  1
                                     set  dept:id  1
                                     HMSET ids  user:id 1  dept:id 1  orderbill:id  1
                                     HINCRBY ids user:id
                                     HINCRBY ids dept:id
                                     HMSET users user:1 "{id:1,name:xx}" user:2 "{id:2,name:xx}"




