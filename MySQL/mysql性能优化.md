EXPLAIN:
1. 使用方式：
explain SQL;
2. 返回结果：
	1，ID：执行查询的序列号；
	2，select_type：使用的查询类型
		1，DEPENDENT SUBQUERY：子查询中内层的第一个SELECT，依赖于外部查询的结果集；
		2，DEPENDENT UNION：子查询中的UNION，且为UNION 中从第二个SELECT 开始的后面所有SELECT，同样依赖于外部查询的结果集；
		3，PRIMARY：子查询中的最外层查询，注意并不是主键查询；
		4，SIMPLE：除子查询或者UNION 之外的其他查询；
		5，SUBQUERY：子查询内层查询的第一个SELECT，结果不依赖于外部查询结果集；
		6，UNCACHEABLE SUBQUERY：结果集无法缓存的子查询；
		7，UNION：UNION 语句中第二个SELECT 开始的后面所有SELECT，第一个SELECT 为PRIMARY
		8，UNION RESULT：UNION 中的合并结果；
	3，table：这次查询访问的数据表；
	4，type：对表所使用的访问方式：
		1，all：全表扫描
		2，const：读常量，且最多只会有一条记录匹配，由于是常量，所以实际上只需要读一次；
		3，eq_ref：最多只会有一条匹配结果，一般是通过主键或者唯一键索引来访问；
		4，fulltext：全文检索，针对full text索引列；
		5，index：全索引扫描；
		6，index_merge：查询中同时使用两个（或更多）索引，然后对索引结果进行merge 之后再读取表数据；
		7，index_subquery：子查询中的返回结果字段组合是一个索引（或索引组合），但不是一个主键或者唯一索引；
		8，rang：索引范围扫描；
		9，ref：Join 语句中被驱动表索引引用查询；
		10，ref_or_null：与ref 的唯一区别就是在使用索引引用查询之外再增加一个空值的查询；
		11，system：系统表，表中只有一行数据；
		12，unique_subquery：子查询中的返回结果字段组合是主键或者唯一约束；
	5，possible_keys：可选的索引；如果没有使用索引，为null；
	6，key：最终选择的索引；
	7，key_len：被选择的索引长度；
	8，ref：过滤的方式，比如const（常量），column（join），func（某个函数）；
	9，rows：查询优化器通过收集到的统计信息估算出的查询条数；
	10，Extra：查询中每一步实现的额外细节信息
		1，Distinct：查找distinct 值，所以当mysql 找到了第一条匹配的结果后，将停止该值的查询而转为后面其他值的查询；
		2，Full scan on NULL key：子查询中的一种优化方式，主要在遇到无法通过索引访问null值的使用使用；
		3，Impossible WHERE noticed after reading const tables：MySQL Query Optimizer 通过收集到的统计信息判断出不可能存在结果；
		4，No tables：Query 语句中使用FROM DUAL 或者不包含任何FROM 子句；
		5，Not exists：在某些左连接中MySQL Query Optimizer 所通过改变原有Query 的组成而使用的优化方法，可以部分减少数据访问次数；
		6，Select tables optimized away：当我们使用某些聚合函数来访问存在索引的某个字段的时候，MySQL Query Optimizer 会通过索引而直接一次定位到所需的数据行完成整个查询。当然，前提是在Query 中不能有GROUP BY 操作。如使用MIN()或者MAX（）的时候；
		7，Using filesort：当我们的Query 中包含ORDER BY 操作，而且无法利用索引完成排序操作的时候，MySQL Query Optimizer 不得不选择相应的排序算法来实现。
		8，Using index：所需要的数据只需要在Index 即可全部获得而不需要再到表中取数据；
		9，Using index for group-by：数据访问和Using index 一样，所需数据只需要读取索引即可，而当Query 中使用了GROUP BY 或者DISTINCT 子句的时候，如果分组字段也在索引中，Extra 中的信息就会是Using index for group-by；
		10，Using temporary：当MySQL 在某些操作中必须使用临时表的时候，在Extra 信息中就会出现Using temporary 。主要常见于GROUP BY 和ORDER BY 等操作中。
		11，Using where：如果我们不是读取表的所有数据，或者不是仅仅通过索引就可以获取所有需要的数据，则会出现Using where 信息；
		12，Using where with pushed condition：这是一个仅仅在NDBCluster 存储引擎中才会出现的信息，而且还需要通过打开Condition Pushdown 优化功能才可能会被使用。控制参数为engine_condition_pushdown 。


**profiling:**
Query Profiler是MYSQL5.1之后提供的一个很方便的用于诊断Query执行的工具，能够准确的获取一条查询执行过程中的CPU，IO等情况；
1. 开启profiling：set profiling=1;
2. 执行QUERY，在profiling过程中所有的query都可以记录下来；
3. 查看记录的query：show profiles；
4. 选择要查看的profile：show profile cpu, block io for query 6；

status是执行SQL的详细过程；
Duration：执行的具体时间；
CPU_user：用户CPU时间；
CPU_system：系统CPU时间；
Block_ops_in：IO输入次数；
Block_ops_out：IO输出次数；

profiling只对本次会话有效；
