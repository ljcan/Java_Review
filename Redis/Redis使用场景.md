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


