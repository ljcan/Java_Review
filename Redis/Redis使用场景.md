### List使用场景
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
