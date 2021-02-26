```

public void ff(TreeNode root){
		List<TreeNode> list = new ArrayList<TreeNode>();
		dfs(root,list);
		for(int i=1;i<list.size();i++){
			TreeNode cur = list.get(i);
			TreeNode pre = list.get(i-1);
			pre.right = cur;
			pre.left = null;
		}
}

public void dfs(TreeNode node,List<TreeNode> list){
	if(node==null){
		return;
	}

	list.add(node);
	dfs(node.left);
	dfs(node.right);
}



public void func(int[] nums){
	int minPrice = 0;
	int maxProfile = 0;
	for(int i=0;i<nums.length;i++){
		if(nums[i]<minPrice){
			minPrice = nums[i];
		}else if(nums[i]-minPrice > maxProfile){
			maxProfile = nums[i] - minPrice;
		}
	}
	return maxProfile;
}


int max=-Integer.MAX_VALUE;
int tmp = 0;
public void func(TreeNode node){
	if(node==null){
		return;
	}
	tmp+=node.val;
	if(tmp>max){
		max=tmp;
	}
	func(node.left);
	func(node.right);
}


//最长序列

public void func(int[] nums){
	int max = 0;
	int len = nums.length;
	for(int i=0;i<len;i++){
		if(nums[i]>max){
			max = nums[i];
		}
	}
	int[] mark = new int[max+1];
	for(int i=0;i<len;i++){
		mark[nums[i]]=1;
	}
	int begin = 0,end = 0,j=0,maxLength=1;
	while(end<max+1){
		while(end<max+1&&mark[end]==1){
			maxLength = Math.max(maxLength,end-begin+1);
			end++;
		}
		while(mark[end]==0){
			end++;
		}
		begin = end;
	}
}

public void func2(int[] nums){
	if(nums.length==0)
         return 0;

	Set<Integer> set = new HashSet<Integer>();
	for(int i:nums){
		set.add(i);
	}
	int tmp = 0,maxLength = 1;
	for(int num:set){
		if(num>=Integer.MAX_VALUE||num<=-Integer.MAX_VALUE)
			continue;
		if(!set.contains(num-1)){
			tmp = num + 1;
			int curLength = 1;
			while(set.contains(tmp++)){
				if(num>=Integer.MAX_VALUE||num<=-Integer.MAX_VALUE)
					break;
				curLength++;
			}
			maxLength = Math.max(maxLength,curLength);
		}
	}
}


//单词拆分
public boolean func(String s,List<String> words){
	return dfs(s,words,0,words.size(),new StringBuilder());
}

public boolean dfs(String s,List<String> words,int index,int size,StringBuilder buffer){
	if(index>=size||buffer.size()>s.length()){
		return false;
	}
	buffer.append(words.get(index));
	if(s.equals(buffer.toString())){
		return true;
	}

	return dfs(s,words,index,size,buffer)||dfs(s,words,index+1,size,buffer);


}

----------
public boolean func(String s,List<String> wordDict){
	int len = s.length();
	Set<String> set = new HashSet<String>(wordDict);
	boolean[] dp = new boolean[len];
	dp[0]=true;
	for(int i=1;i<len;i++){
		for(int j=0;j<i;j++){
			if(dp[j]&&set.contains(s.substring(j,i))){
				dp[i]=true;
				break;
			}
		}
	}
	return dp[len-1];
	
}

//判断链表是否有环
public boolean func(ListNode head){
	if(head==null){
		return false;
	}
	ListNode frist = head;
	ListNode slow = head;
	while(frist!=null){
		if(frist.next!=null)
			frist = frist.next.next;
		else
			frist = frist.next;
		slow = slow.next;
		if(frist!=null&&frist==slow){
			return true;
		}
	}
	return false;
}

//找出环形链表的入环的第一个节点


//数组连续元素最大乘积

public int maxValue(int[] nums){
	
}

//两个链表的相交点
public ListNode func(ListNode headA,ListNode headB){
	ListNode tmpA=headA;
	ListNode tmpB=headB;
	ListNode a = headA;
	ListNode b = headB;
	int lenA = 0;
	int lenB = 0;
	while(tmpA!=null){
		lenA++;
		tmpA=tmpA.next;
	}
	while(tmpB!=null){
		lenB++;
		tmpB=tmpB.next;
	}
	if(lenA>lenB){
		int dis = lenA-lenB;
		while(dis-->0){
			a=a.next;
		}
		while(a!=null){
			if(a==b){
				return a;
			}
			a=a.next;
			b=b.next;
		}
	}else{
		int dis = lenB-lenA;
		while(dis-->0){
			b=b.next;
		}
		while(b!=null){
			if(b==a){
				return b;
			}
			a=a.next;
			b=b.next;
		}
	}
	return null;
}

//出现半数以上的数
public int func(int[] nums){
	int count=1;
	int index=0;
	for(int i=1;i<nums.length;i++){
		if(count==0){
			index=i-1;
			count=1;
		}
		if(nums[i]==nums[index]){
			count++;
		}else{
			count--;
		}
	}
	if(count<=0){
		return 0;
	}
	return nums[index];
}

//偷盗的最大金额，不能是相邻的元素
public int func(int[] nums){
	int len = nums.length;
	if(len==0){
		return 0;
	}
	if(len==1){
		return nums[0];
	}
	if(len==2){
		return Math.max(nums[0],nums[1]);
	}
	int[] dp=new int[len];
	dp[0]=nums[0];
	dp[1]=Math.max(nums[0],nums[1]);
	for(int i=2;i<len;i++){
		dp[i]=Math.max(dp[i-2]+nums[i],dp[i-1]);
	}
	return dp[len-1];
	
}

//大陆的个数
public int isLand(char[][] grid){
	int nums = 0;
	for(int x=0;x<grid.length;x++){
		for(int y=0;y<grid[0].length;y++){
			if(grid[x][y]=='1'){
				dfs(grid,x,y);
				nums++;
			}
		}
	}
	return nums;
}

public void dfs(char[][] grid,int x,int y){
	if(x<0||y<0||x>=grid.length||y>=grid[0].length||grid[x][y]=='0'){
		return;
	}
	grid[x][y]='0';
	dfs(grid,x+1,y);
	dfs(grid,x-1,y);
	dfs(grid,x,y+1);
	dfs(grid,x,y-1);
}

//寻找第K大元素
public int func(int[] nums,int k){
	int len = nums.length;
	int target = len - k;
	int low = 0;
	int high = len - 1;
	while(true){
		int index = partition(nums,low,high);
		if(index<target){
			low = index+1;
		}else if(index>target){
			high = index-1;
		}else{
			return nums[index];
		}
	}
	
}

public int partition(int[] nums,int low,int high){
	int i=low;
	int pvit = nums[high];
	for(int j=low;j<high;j++){
		if(nums[j]<pvit){
			swap(nums,i,j);
			i++;
		}
	}
	swap(nums,i,high);
	return i;
}

public void swap(int[] nums,int i,int j){
	int tmp = nums[i];
	nums[i] = nums[j];
	nums[j] = tmp;
}

//计算最大陆地面积
public int func(char[][] matrix){
	int rows = matrix.length;
	int clos = matrix[0].length;
	int[][] dp = new int[rows][clos];
	int maxLen = 0;
	for(int i=0;i<rows;i++){
		for(int j=0;j<clos;j++){
			if(matrix[i][j]=='1'){
				if(i==0||j==0){
					dp[i][j]=1;
				}else{
					dp[i][j]=Math.min(dp[i-1][j-1],Math.min(dp[i-1][j],dp[i][j-1]))+1;
				}
			}
			maxLen = Math.max(maxLen,dp[i][j]);
		}
	}
	int area = maxLen*maxLen;
	return area;
}

//对称二叉树
public TreeNode func(TreeNode root){
	if(root==null){
		return null;
	}
	TreeNode tmp = root.left;
	root.left = root.right;
	root.right = tmp;

	func(root.left);
	func(root.right);

	return root;
}

//回文链表
public boolean func(ListNode head){
	Stack<Integer> stack = new Stack<Integer>();
	int len = 0,i=0;
	ListNode tmp = head;
	while(tmp!=null){
		len++;
		stack.push(tmp.val);
		tmp=tmp.next;
	}
	while(i++<len/2){
		int aa = stack.pop();
		if(aa!=head.val){
			return false;
		}
		head = head.next;
	}
	return true;
}

//除自身以外数组的乘积
public int[] func(int[] nums){
	int len = nums.length;
	int[] L = new int[len];
	int[] R =new int[len];
	L[0] = 1;
	R[len-1] = 1;
	int[] ans = new int[len];
	for(int i=1;i<len;i++){
		L[i] = L[i-1]*nums[i-1];
	}
	for(int i=len-2;i>=0;i--){
		R[i] = R[i+1]*nums[i+1];
	}

	for(int i=0;i<len;i++){
		ans[i] = L[i]*R[i];
	}
	return ans;
}

//从左到右升序，从上到下升序的数组中查找目标数字,从左下角的元素开始遍历，如果小于目标数，则向上后退一行，反之向右走一列。
public boolean func(int[][] nums,int target){
	int rows = nums.length;
	if(rows==0)
		return false;
	int cols = nums[0].length;
	int x = rows - 1,y = 0;
	while(x<rows&&x>=0&&y<cols&&y>=0){
		if(nums[x][y]==target){
			return true;
		}else if(nums[x][y]<target){
			y++;
		}else if(nums[x][y]>target){
			x--;
		}
	}
	return false;
}

//完全平方数
public int func(int n){
	int count = 0;
	int i = n;
	while(n>0){
		double tmp = Math.sqrt(i);
		int i_tmp = (int)tmp;
		if(tmp==i_tmp){
			count++;
			n-=i;
		}else{
			i--;
		}
	}
	return count;
}

//移动零
public void func(int[] nums){
	int index = 0;
	for(int i=0;i<nums.length;i++){
		if(nums[i]!=0){
			nums[index++]=nums[i];
		}
	}
	for(int i=index;i<nums.length;i++){
		nums[i]=0;
	}
}

//寻找数组中只有一个重复的元素
public int func(int[] nums){
	int len = nums.length;
	int begin = 0,end = len-1;
	for(int i=0;i<len;i++){
		end = len-1;
		while(i<end){
			if(nums[i]==nums[end]){
				return nums[i];
			}else{
				end--;
			}
		}
	}
	return 0;
}

//买入股票的最佳时机的最大收益（含冷冻期）
public int func(int[] prices){
	int len = prices.length;
	if(len==0)
		return 0;
	int[][] dp = new int[len][3];
	//dp[i][0]      //持有一支股票
	//dp[i][1]      //不持有股票，处于冷冻期
	//dp[i][2]      //不持有股票，不处于冷冻期
	dp[0][0]=-prices[0];
	for(int i=1;i<len;i++){
        dp[i][0]=Math.max(dp[i-1][0],dp[i-1][2]-prices[i]);
        dp[i][1]=dp[i-1][0]+prices[i];
        dp[i][2]=Math.max(dp[i-1][1],dp[i-1][2]);
    }
    return Math.max(dp[len-1][1],dp[len-1][2]);
}

//硬币的最少数量
public int func(int[] coins,int amount){
		int len = coins.length;
		int[] dp=new int[len+1];
		Arrays.fill(dp,amount+1);
		dp[0]=0;
		for(int i=1;i<amount;i++){
			for(int j=0;j<len;j++){
				dp[i]=Math.min(dp[i],dp[i-coins[j]]+1);
			}
		}
		return dp[amount]>amount?-1:dp[amount];
	}
}

public int dfs(int[] coins,int amount,int index){
	if(index>=coins.length){
		return 0;
	}
	if(amount<=0){
		return 0;
	}
	amount-=coins[index];
	dfs(coins,amount,index)+1;
	dfs(coins,amount,index+1)+1;
}

//根据身高重新组建队列


//分割等和子集    [1,5,11,5]
public boolean func(int[] nums){
	Arrays.sort(nums);
	int lsum=0,rsum=0;
	int i=0,j=nums.length-1;
	while(i<=j){
		if(i==j){
			if(lsum==rsum){
				return true;
			}else{
				return false;
			}
		}else if(lsum==rsum){
			i++;
			j--;
		}
		while(i<j&&lsum<rsum){
			lsum+=nums[i];
			i++;
		}
		while(i<j&&lsum>rsum){
			rsum+=nums[j];
			j--;
		}
	}
	return false;
}


public boolean func(int[] nums){
	int max = 0,sum = 0;
	for(int a:nums){
		max=Math.max(a,max);
		sum+=a;
	}
	if(sum%2!=0){
		return false;
	}
	int target = sum/2;
	int len = nums.length;
	if(max>target){
		return false;
	}
	boolean[][] dp=new boolean[len][target+1];
	//边界初始化
	for(int i=0;i<len;i++){
		dp[i][0]=true;
	}
	dp[0][nums[0]]=true;
	for(int i=1;i<len;i++){
		for(int j=1;j<target+1;j++){
			if(j>=nums[i]){
				dp[i][j]=dp[i-1][j]|dp[i-1][j-nums[i]];
			}else{
				dp[i][j]=dp[i-1][j];
			}
		}
	}
	return dp[len-1][target];
}

//路径总和
public int pathSum(TreeNode root,int sum){
	//前缀和算法
	if(root==null){
		return 0;
	}
	Map<Integer,Integer> presixSum = new HashMap<Integer,Integer>();
	presixSum.put(0,1);     //当前和为零的一条路径
	return func(root,presixSum,sum,0);

}
public int func(TreeNode node,Map<Integer,Integer> presixSum,int target,int curSum){
	if(node==null){
		return 0;
	}
	int res = 0;
	curSum+=node.val;
	res+=presixSum.getOrDefault(curSum-target,0);
	presixSum.put(curSum,presixSum.getOrDefault(curSum,0)+1);

	//进入下一层
	res+=func(node.left,presixSum,target,curSum);
	res+=func(node.right,presixSum,target,curSum);
	//回到本层，恢复状态
	presixSum.put(curSum,presixSum.get(curSum)-1);
	return res;
}

//找到字符串中所有异位词
List<String> list = new ArrayList<String>();
public List<Integer> findAnagrams(String s, String p) {
      List<Integer> res = new ArrayList<Integer>();
        if(s.length()<p.length()){
            return res;
        }
        int plen = p.length();
        char[] c = p.toCharArray();
        getAllStr(c,0);
        // System.out.println(list.toString());
       
        for(int i=0,j=i+plen-1;j<s.length();i++,j++){
            String tmp = s.substring(i,j+1);
            boolean has = list.contains(tmp);
            if(has){
                res.add(i);
            }
        }
        return res;
    }

    public void getAllStr(char[] c,int begin){
        if(begin==c.length-1){
            list.add(String.valueOf(c));
        }

        for(int i=begin;i<c.length;i++){
            swap(c,i,begin);
            getAllStr(c,begin+1);
            swap(c,i,begin);
        }
    }

    public void swap(char[] c,int i,int j){

        char tmp = c[i];
        c[i] = c[j];
        c[j] = tmp;
    }

 //二叉树的直径
 int res;
 public int diameterOfBinaryTree(TreeNode root){
 	if(root==null){
 		return 0;
 	}
 	func(root);
 	return res;
 	
 }

 public int func(TreeNode node){
 	if(node==null){
 		return 0;
 	}
 	int L=func(node.left);
 	int R=func(node.right);
 	res=Math.max(res,L+R);
 	return Math.max(L,R)+1;
 }

 //和为k的子数组的个数(前缀和法)
 public int func(int[] nums,int k){
 	Map<Integer,Integer> map = new HashMap<Integer,Integer>();
 	map.put(0,1);
 	int pre=0,count=0;
 	for(int i=0;i<nums.length;i++){
 		pre+=nums[i];
 		if(map.containsKey(pre-k)){
 			count+=map.get(pre-k);
 		}
 		map.put(pre,map.getOrDefault(pre,0)+1);
 	}
 	return count;

 }

 //最短无序连续子数组
 //方法一
 public int func(int[] nums){
 	int len = nums.length;
 	if(len==0)
 		return 0;
 	int l=nums.length-1,r=0;
 	for(int i=0;i<len-1;i++){
 		for(int j=i+1;j<len;j++){
 			if(nums[j]<nums[i]){
 				l=Math.min(l,i);
 				r=Math.max(r,j);
 			}
 		}
 	}
 	return r-l<=0?0:r-l+1;
 }
//方法二：（排序）
//方法三：（栈）
public int func(int[] nums){
	int len=nums.length;
	int l=len-1,r=0;
	Stack<Integer> stack = new Stack<Integer>();
	for(int i=0;i<len;i++){
		while(!stack.isEmpty()&&nums[stack.peek()]>nums[i]){
			l=Math.min(l,stack.pop());
		}
		stack.push(i);
	}
	stack.clear();
	for(int i=len-1;i>=0;i--){
		while(!stack.isEmpty()&&nums[stack.peek()]<nums[i]){
			r=Math.max(r,stack.pop());
		}
		stack.push(i);
	}
	return r-l<=0?0:r-l+1;
}

//合并二叉树
public TreeNode func(TreeNode t1,TreeNode t2){
	if(t1==null){
		return t2;
	}
	if(t2==null){
		return t1;
	}
	TreeNode mergeNode= new TreeNode(t1.val+t2.val);
	mergeNode.left=func(t1.left,t2.left);
	mergeNode.right=func(t1.right,t2.right);
	return mergeNode;
}

//回文子串
public int func(String s){
	int len = s.length();
	if(len==0)
		return 0;
	int cnt = len;
	char[] c=s.toCharArray();
	for(int i=1;i<len;i++){
		for(int j=0;j<i;j++){
			boolean is = isContactStr(c,j,i);
			if(is){
				cnt+=1;
			}
		}
	}
	return cnt;
}
/**判断是否是回文字符串*/
public boolean isContactStr(char[] c,int begin,int end){
	while(begin<end){
		if(c[begin]!=c[end]){
			return false;
		}
		begin++;
		end--;
	}
	return true;
}

//739，每日温度
public int[] func(int[] T){
	int len = T.length;
	int[] res = new int[len];
	int cnt=0;
	for(int i=0;i<len;i++){
		cnt=0;
		for(int j=i+1;j<len;j++){
			cnt++;
			if(T[j]>T[i]){
				res[i]=cnt;
				break;
			}
		}
	}
	return res;
}
//方法二：也可以用栈  [73, 74, 75, 71, 69, 72, 76, 73]
public int[] func2(int[] T){
	int len = T.length;
	int[] res = new int[len];
	Stack<Integer> stack = new Stack<Integer>();
	for(int i=0;i<len;i++){
		while(!stack.isEmpty()&&T[stack.peek()]<T[i]){
			int preIndex = stack.pop();   //会弹出所有已经算过的元素
			res[preIndex]=i-preIndex;
		}
		stack.push(i);
	}
	return res;
}

//滑动窗口最大值（模拟最大递减队列）
public int[]  func(int[] nums,int k){
	int len = nums.length;
	int[] res=new int[len-k+1];
	LinkedList<Integer> list = new LinkedList<Integer>();
	for(int i=0;i<len;i++){
		//构造递减队列
		while(!list.isEmpty()&&list.peekLast()<nums[i]){
			list.pollLast();
		}
		list.addLast(nums[i]);
		//如果头元素等于窗口第一个元素，将其移除
		if(i>=k&&list.peekFrist()==nums[i-k]){
			list.pollFrist();
		}
		//写入当前最大值 
		if(i>=k-1){
			res[i-k+1]=list.peekFrist();
		}
	}
	return res;
}

//3.无重复字符的最长子串  pwwkew
public int func(String s){
	char[] c=s.toCharArray();
	int len=c.length;
	if(len==0)
		return 0;
	int[] mark=new int[26];
	int maxLen=-Integer.MAX_VALUE;
	int curLen=0;
	for(int i=0,j=0;i<len&&j<len;j++){
		if(mark[c[j]-96]==0){
			curLen++;
			mark[c[j]-96]++;
		}else{
			mark[c[j]-96]++;
			if(curLen>maxLen){
				maxLen=curLen;
			}
			while(i<j){
				if(mark[c[i]-96]>1){
					mark[c[i]-96]=1;
					i++;
					break;
				}
				i++;
			}
			curLen=i-j+1;
		}
	}
	return maxLen;
}

首先将所有字符串都置为a
从后往前，依次将第i位置成相应的字母，
使用 （k - n）/ 25 计算出需要置为z的个数
然后通过 （k - n ) % 25 计算出z之前的那一位应该置为多少
超时是因为当n太大时，计算z的位数的过程中如果是按位遍历，计算次数太多。其实一步就能完成，（k - n）/ 25
//1663，具有给定数值的最小字符串
public String func(int n,int k){
	StringBuffer buffer=new StringBuffer();
	for(int i=n;i>=1;i--){
		int bound=k-26*(i-1);
		if(bound>0){
			buffer.append((char)('a'+bound-1));
			k-=bound;
		}else{
			ans+='a';
			k-=1;
		}
	}
	return buffer.toString();
}



```
