    public ListNode reverseKGroup(ListNode head, int k) {
    	int i=0;
    	ListNode cur = head;
    	ListNode begin = head;
    	ListNode tmpHead = new ListNode(0);
    	ListNode newHead = null;
    	boolean isFrist = true;
    	while(cur!=null){
    		i++;
    		if(i==k){
    			i=0;
    			ListNode tmp = cur.next;
    			if(isFrist){
    				newHead = reverseList(begin,k);
    			}
    			tmpHead.next = reverseList(begin,k);
    			begin.next = tmp;
    			tmpHead = begin;
    			isFrist = false;
    		}
    		cur = cur.next;
    	}
        return newHead; 
     }
    
     public ListNode reverseList(ListNode head,int k){
     	int i = 0;
     	while(i<k){
     		if(head==null||head.next==null){
     		return head;
    	 	}
    	 	ListNode pre = head;
    	 	ListNode cur = head.next;
    	 	ListNode tmp = head.next.next;
    	 	while(cur!=null){
    	 		tmp = cur.next;
    	 		cur.next = pre;
    	 		pre=cur;
    	 		cur=tmp;
    	 	}
    	 	head.next = null;
    	 	return pre;
     	}
     	return head;
     }
    
    //从前序和中序遍历构造二叉树
    static Map<Integer,Integer> indexMap = new HashMap<Integer,Integer>();
    public TreeNode buildTree(int[] preorder, int[] inorder) {
    	int n = inorder.length;
    	if(n==0){
    		return null;
    	}
    	for(int i=0;i<n;i++){
    		indexMap.put(inorder[i],i);
    	}
    	return mybuild(preorder,inorder,0,n-1,0,n-1);
    }
    public TreeNode mybuild(int[] preorder,int[] inorder,int preleft,int preright,int inleft,int inright){
    	if(preleft>preright){
    		return null;
    	}
    	int rootVal = preorder[preleft];
    	TreeNode root = new TreeNode(rootVal);
    	int rootIndex = indexMap.get(rootVal);
    	int offset = rootIndex - inleft;
    	root.left = mybuild(preorder,inorder,preleft+1,preleft+offset,inleft,rootIndex-1);
    	root.right = mybuild(preorder,inorder,preleft+offset+1,preright,rootIndex+1,inright);
    	return root;
    }
    
    //排序链表
     public ListNode sortList(ListNode head) {
     /**     快慢指针在自顶向下递归时用到
     	ListNode fast = head,slow = head;
     	while(slow!=null){
     		fast = fast.next;
     		slow = slow.next;
     		if(fast!=null){
     			fast = fast.next;
     		}
     	}
    */
        int len = 0;
        ListNode tmp = head;
        while(tmp!=null){
        	len++:
        	tmp=tmp.next;
        }
        ListNode tmpHead = new ListNode(0,head);
        for(int sublen = 1;sublen<len;sublen<<=1){
        	ListNode pre = tmpHead,cur = tmpHead.next;
        	while(cur!=null){
    	    	ListNode head1 = cur;
    	    	for(int i=1;i<sublen&&cur.next!=null;i++){
    	    		cur = cur.next;
    	    	}
    	    	ListNode head2 = cur.next;
    	    	cur.next = null;
    	    	cur = head2;
    	    	for(int i=1;i<sublen&&cur!=null&&cur.next!=null;i++){
    	    		cur = cur.next;
    	    	}
    	    	ListNode next = null;
    	    	if(cur!=null){
    	    		next = cur.next;
    	    		cur.next = null;
    	    	}
    	    	ListNode merge = merge(head1,head2);
    	    	pre.next = merge;    //连接子链表
    	    	while(pre.next!=null){
    	    		pre=pre.next;
    	    	}
    	    	cur = next;
        	}
        	
        }
        return tmpHead.next;
     }
    
     public ListNode merge(ListNode head1,ListNode head2){
     	if(head1==null){
     		return head2;
     	}
     	if(head2==null){
     		return head1;
     	}
     	ListNode tmpHead = new ListNode(0);
     	ListNode tmp = tmpHead;
     	ListNode tmp1 = head1,tmp2=head2;
     	while(tmp1!=null&&tmp2!=null){
     		if(tmp1.val<=tmp2.val){
     			tmp.next=tmp1;
     			tmp1 = tmp1.next;
     		}else{
     			tmp.next = tmp2;
     			tmp2 = tmp2.next;
     		}
     		tmp = tmp.next;
     	}
     	if(tmp1!=null){
     		tmp.next=tmp1;
     	}else if(tmp2!=null){
     		tmp.next = tmp2;
     	}
     	return tmpHead.next;
     }
    
     //打家劫舍3
     Map<TreeNode,Integer> fmap = new HashMap<TreeNode,Integer>();   //保存取当前节点值时的最大值
     Map<TreeNode,Integer> gmap = new HashMap<TreeNode,Integer>();   //保存不取当前值时的最大值
     public int rob(TreeNode root){
     	dfs(root);
     	return Math.max(fmap.getOrDefault(root,0),gmap.getOrDefault(root,0));
    
     }
     public void dfs(TreeNode node){
     	if(node==null){
     		return;
     	}
     	dfs(node.left);
     	dfs(node.right);
     	fmap.put(node,node.val+gmap.getOrDefault(node.left,0)+gmap.getOrDefault(node.right,0));
     	gmap.put(node,Math.max(gmap.getOrDefault(node.left,0),fmap.getOrDefault(node.left,0))+Math.max(gmap.getOrDefault(node.right,0),fmap.getOrDefault(node.right,0)));
     }
    
     //二叉搜索树转换为累加树（反序中序遍历）
     int sum=0;
     public TreeNode convertBST(TreeNode root) {
     	if(root!=null){
     		convertBST(root.right);
     		sum+=root.val;
     		root.val=sum;
     		convertBST(root.left);
      	}
      	return root;
     }
    
     //判断是否是平衡二叉树
     public boolean isBalanced(TreeNode root) {
     	if(root==null){
     		return true;
     	}
     	return Math.abs(heigh(root.left)-heigh(root.right))<=1&&isBalanced(root.left)&&isBalanced(root.right);
     }
     public int heigh(TreeNode node){
     	if(node==null){
     		return 0;
     	}
     	return Math.max(heigh(node.left),heigh(node.right))+1;
     }
    
     // 04.03. 特定深度节点链表
     public ListNode[] listOfDepth(TreeNode tree) {
     	if(tree==null){
     		return null;
     	}
     	LinkedList<TreeNode> buffer = new LinkedList<TreeNode>();
     	LinkedList<TreeNode> buffer2 = new LinkedList<TreeNode>();
     	LinkedList<LinkedList<TreeNode>> res = new LinkedList<LinkedList<TreeNode>>();
     	buffer.add(tree);
     	while(buffer.size()>0){
     		LinkedList<TreeNode> subList= new LinkedList<TreeNode>();
     		while(buffer.size()>0){
     			TreeNode node = buffer.removeFirst();
     			subList.add(node);
     			if(node.left!=null){
     				buffer2.add(node.left);
     			}
     			if(node.right!=null){
     				buffer2.add(node.right);
     			}
     		}
     		res.add(subList);
     		LinkedList<TreeNode> tmp = buffer;
     		buffer = buffer2;
     		buffer2 = tmp;
     	}
     	int n = res.size(),i=0;
     	ListNode[] list = new ListNode[n];
     	for(LinkedList<TreeNode> sub:res){
     		ListNode head = new ListNode(0);
     		ListNode cur = head;
     		for(TreeNode node:sub){
     			ListNode tmpnode=new ListNode(node.val);
                cur.next = tmpnode;
                cur = tmpnode;
     		}
     		list[i]=head.next;
     		i++;
     	}
     	return list;
     }
    
     //N叉树的前序遍历
     public List<Integer> preorder(Node root) {
     	List<Integer> res = new ArrayList<Integer>();
     	if(root==null){
     		return res;
     	}
     	LinkedList<Node> stack = new LinkedList<Node>();
     	stack.add(root);
     	while(stack.size()>0){
     		Node node = stack.pollLast();
     		res.add(node.val);
     		List<Node> children = node.children;
     		Collections.reverse(children);
     		for(Node n:children){
     			stack.add(n);
     		}
     	}
     	return res;
     }
    
     //交换一个数给出小于当前数组arr的最大可能排列
     //逆序遍历找出arr[i+1]>arr[i]的第一个数，然后找出其右端比他小的最大数，进行交换
     /**
     	[3,1,1,3]
     */
     public int[] prevPermOpt1(int[] arr) {
     	int n = arr.length;
     	int index = -1;
     	boolean hasResult = false;
     	int max = -Integer.MAX_VALUE;
     	for(int i=n-2;i>=0;i--){
     		if(arr[i+1]<arr[i]){
     			for(int j=i+1;j<n;j++){
     			   if(arr[j]<arr[i]){
     			   		hasResult=true;
     			   		if(arr[j]>max){
    	 					max=arr[j];
    	 					index = j;
    	 				}
     			   }
     			}
     		}
     		if(hasResult){
    	 		int tmp = arr[i];
    	 		arr[i]=arr[index];
    	 		arr[index]=tmp;
    	 		return arr;
    	 	}
     	}
     	return arr;
     }
    
     //行列递增的数组中查找目标数
      public boolean findNumberIn2DArray(int[][] matrix, int target) {
    	  if(matrix.length==0&&matrix[0].length==0){
    	  		return false;
    	  	}
      	//从数组的右上角开始查找
      	int x = matrix.length,y=matrix[0].length;
      	int j=y-1,i=0;
      	while(i<x&&j>=0){
      		if(matrix[i][j]==target){
      			return true;
      		}else if(matrix[i][j]>target){
      			j--
      		}else{
      			i++:
      		}
      	}
      	return false;
      }
    
      //跳跃游戏2
       //1.贪心算法
        public int jump(int[] nums) {
            int n = nums.length;
            int position = n-1,step=0;
            while(position>0){
            	for(int i=0;i<n;i++){
    	        	if(i+nums[i]>=position){
    	        		position=i;
    	        		step++;
    	        		break;
    	        	}
    	        }
            }
            return step;
        }
        //2.正方向查找
        public int jump(int[] nums){
        	int n = nums.length;
        	int end = 0,step=0;
        	int max=0;
        	for(int i=0;i<n-1;i++){
        		max = Math.max(max,i+nums[i]);
        		if(i==end){
        			end=max;
        			step++;
        		}
        	}
        	return step;
        }
    
        481. 不同整数的最少数目
     	public int findLeastNumOfUniqueInts(int[] arr, int k) {
     		int n = arr.length;
     		Arrays.sort(arr);
     		int res = 1,cnt = 1;
     		for(int i=0;i<n-1;i++){
     			if(k==0){
     				break;
     			}
     			if(arr[i]!=arr[i+1]){
     				res++;
     				if(cnt<=k){
     					k--;
     					res--;
     				}
     				cnt=1;
     			}else{
     				cnt++;
     			}
     		}
     		return res;
        }
    
        public int findLeastNumOfUniqueInts(int[] arr, int k) {
        	int n=arr.length;
        	int max = 0;
        	for(int i=0;i<n;i++){
        		if(max<arr[i]){
        			max=arr[i];
        		}
        	}
        	int[] cnt = new int[max+1];
        	int res = 0;
        	for(int i=0;i<n;i++){
        		cnt[arr[i]]++;
        	}
        	for(int i=0;i<max+1;i++){
        		if(cnt[i]>0){
        			res++;
        		}
        	}
        	Arrays.sort(cnt);
        	for(int i=0;i<max+1;i++){
        		if(k<=0){
        			break;
        		}
        		if(cnt[i]!=0&&cnt[i]<=k){
        			k-=cnt[i];
        			res--;
        		}
        	}
        	return res;
        }
    
         public int findLeastNumOfUniqueInts(int[] arr, int k) {
         	int n = arr.length;
         	if(n==0){
         		return n;
         	}
         	List<Integer> list = new ArrayList<Integer>();
         	Arrays.sort(arr);
         	int tmp = 1;
         	for(int i=0;i<n;i++){
         		if(i+1<n&&arr[i]!=arr[i+1]){
         			list.add(tmp);
         			tmp = 1;
         		}else{
         			tmp++;
         		}
         	}
         	if(n-2>=0&&arr[n-1]==arr[n-2]){
         		list.add(tmp);
         	}else{
         		list.add(1);
         	}
         	Collections.sort(list);
         	int res = list.size();
         	if(k==0){
         		return res;
         	}
         	for(int i=0;i<list.size();i++){
         		if(k<=0){
         			break;
         		}
         		if(list.get(i)<=k){
         			k-=list.get(i);
         			res--;
         		}
         	}
         	return res;
         }
    
         //旋转打印数组
         public List<Integer> spiralOrder(int[][] matrix) {
         	List<Integer> list = new ArrayList<Integer>();
         	int m=matrix.length,n=matrix[0].length;
         	int top=0,left=0,bottom=m-1,right=n-1;
         	while(left<=right&&top<=bottom){
         		for(int i=left;i<=right;i++){
         			list.add(matrix[top][i]);
         		}
         		for(int i=top+1;i<=bottom;i++){
         			list.add(matrix[i][right]);
         		}
         		if(left<right&&top<bottom){
         			for(int i=right-1;i>left;i--){
         				list.add(matrix[bottom][i]);
         			}
         			for(int i=bottom;i>top;i--){
         				list.add(matrix[i][left]);
         			}
         		}
         		left++;
         		right--;
         		top++;
         		bottom--;
         	}
         	return list;
        }
    
        // 两个非重叠子数组的最大和  (经典)
        public int maxSumTwoNoOverlap(int[] A, int L, int M) {
        	//使用前缀和+动态规划完成
        	int n=A.length;
        	for(int i=1;i<n;i++){
        		A[i]+=A[i-1];
        	}
        	int res = A[L+M-1],Lmax=A[L-1],Mmax=A[M-1];       
        	for(int i=L+M;i<n;i++){
        		Lmax = Math.max(Lmax,A[i-M]-A[i-M-L]);     //l=i-m-(i-m-l)
        		Mmax = Math.max(Mmax,A[i-L]-A[i-M-L]);     //m=i-l-(i-m-l)
        		res = Math.max(res,Math.max(Lmax+A[i]-A[i-M],Mmax+A[i]-A[i-L]));     //res=max(lmax+i-(i-m),mmax+i-(i-l));
        	}
        	return res;
        }
    
        给你一个二进制字符串数组 strs 和两个整数 m 和 n 。
        请你找出并返回 strs 的最大子集的大小，该子集中 最多 有 m 个 0 和 n 个 1 。
        public int findMaxForm(String[] strs, int m, int n) {
        	int n = strs.length;
        	int[][] dp =new int[m+1][n+1];
        	for(int i=0;i<n;i++){
        		int[] cnt=countZeroOrOne(strs[i]);
        		for(int j=m;j>=cnt[0];j--){
        			for(int k=n;k>=cnt[1];k--){
        				dp[j][k]=Math.max(dp[j-cnt[0]][k-cnt[1]]+1,dp[j][k]);
        			}
        		}
        	}
        	return dp[m][n];
        }
    
        public int[] countZeroOrOne(String str){
        	int[] cnt = new int[2];
        	for(int i=0;i<str.length;i++){
        		cnt[str.charAt(i)-'0']++;
        	}
        	return cnt;
        }
    
    
    	5
    	[[1,2,10],[2,0,7],[1,3,8],[4,0,10],[3,4,2],[4,2,10],[0,3,3],[3,1,6],[2,4,5]]
    	0
    	4
    	1
        //K站中转内最便宜的航班
         public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
         	//dp[i][k]代表最多经过k站，到达i的最小代价
         	int[][] dp = new int[n][K+1];
    
         	 //初始化整个状态数组
         	for(int i=0;i<n;i++){
         		Arrays.fill(dp[i],Integer.MAX_VALUE);
         	}
         	
         	//直接到达的航线
         	for(int[] flight:flights){
         		if(flight[0]==src){
         			dp[flight[1]][0]=flight[2];
         		}
         	}
         	//自己到达自己为0
         	for(int i=0;i<=K;i++){
         		dp[src][i]=0;
         	}
    
         	for(int i=1;i<=K;i++){
         		for(int[] flight:flights){
         			if(dp[flight[0]][i-1]!=Integer.MAX_VALUE)
         				dp[flight[1]][i]=Math.min(dp[flight[1]][i],dp[flight[0]][i-1]+flight[2]);
         		}
         	}
         	return dp[dst][K]==Integer.MAX_VALUE?-1:dp[dst][K];
    
        }
    
        //剪绳子，将长度为n的绳子剪为m段，使得每段的长度的乘积最大
        public int cuttingRope(int n) {     
        	//dp[i][j]代表第i段的长度为j
        	int[][] dp=new int[n][n+1];
        	for(int i=0;i<=n;i++){
        		dp[0][i]=0;
        	}
        	for(int i=1;i<=n;i++){
        		dp[n][i]=1;
        	}
        	dp[1][n]=n;
        	for(int i=1;i<n;i++){
        		for(int j=n;j>=1;j--){
        			dp[i][j]=Math.max(dp[i][j],dp[i-1][j-])
        		}
        		
        	}
        }
    
        public int cuttingRope(int n) {
        	if(n<=3){
        		return n-1;
        	}
        	int res=1;
        	int mod=Integer.MAX_VALUE;
        	while(n>4){
        			res=res*3%mod;
        			n-=3;
        	}
        	return res*n%mod;
        }
    
     给你一个 m * n 的矩阵 mat 和一个整数 K ，请你返回一个矩阵 answer ，其中每个 answer[i][j] 是所有满足下述条件的元素 mat[r][c] 的和： 
    i - K <= r <= i + K, j - K <= c <= j + K 
    (r, c) 在矩阵内。
    	//矩阵区域和
        public int[][] matrixBlockSum(int[][] mat, int K) {
        	//前缀和
        	int m=mat.length,n=mat[0].length;
        	int[][] dp = new int[m+1][n+1];
        	for(int i=1;i<=m;i++){
        		for(int j=1;j<=n;j++){
        			dp[i][j]=dp[i-1][j]+dp[i][j-1]-dp[i-1][j-1]+mat[i-1][j-1];
        		}
        	}
        	int[][] res=new int[m][n];
        	for(int i=0;i<m;i++){
        		for(int j=0;j<n;j++){
        			res[i][j]=get(dp,i+k+1,j+k+1,m,n)-get(dp,i-k,j+k+1,m,n)-get(dp,i+k+1,j-k,m,n)+get(dp,i-k,j-k,m,n);
        		}
        	}
        	return res;
        }
        public int get(int[][] dp,int x,int y,int m,int n){
        	x = Math.max(Math.min(x,m),0);
        	y =	Math.max(Math.min(y,n),0);
        	return dp[x][y];
        }
    
       //买股票的最佳时机，含有手续费 dp[i][0]表示第i天没有股票时的最大利益，dp[i][1]表示第i天手中有股票时的最大利益
       public int maxProfit(int[] prices, int fee) {
       		int n=prices.length;
       		int[][] dp = new int[n][2];
       		dp[0][0]=0;
       		dp[0][1]=-prices[0];
       		for(int i=1;i<n;i++){
       			dp[i][0]=Math.max(dp[i-1][0],dp[i-1][1]+prices[i]-fee);
       			dp[i][1]=Math.max(dp[i-1][1],dp[i-1][0]-prices[i]);
       		}
       		return dp[n-1][0];
        }
        
        //数组中可以被3整除的最大元素和
        public int maxSumDivThree(int[] nums) {
        	int n=nums.length;
        	int sum = 0;
        	for(int num:nums){
        		sum+=num;
        	}
        	int t=Integer.MAX_VALUE,m1=Integer.MAX_VALUE,m2=Integer.MAX_VALUE;
        	if(sum%3==0){
        		return sum;
        	}
        	int mod = sum%3;
        	for(int num:nums){
        		if(mod==num%3){
        			t=Math.min(t,num);
        		}
        		if(num%3!=0){
        			if(num<m1){
    	    			m2=m1;
    	    			m1=num;
    	    		}else if(num<m2){
    	    			m2=num;
    	    		}
        		}
        	}
        	m1=m1==Integer.MAX_VALUE?0:m1;
        	m2=m2==Integer.MAX_VALUE?0:m2;
        	sum-=Math.min(t,m1+m2);
        	return sum;
        }
    
        //分割回文串
            public List<List<String>> partition(String s) {
                int n=s.length();
                boolean[][] dp = new boolean[n][n];
                for(int i=0;i<n;i++){
                    for(int j=0;j<=i;j++){
                        if(s.charAt(j)==s.charAt(i)&&(i-j<=2||dp[j+1][i-1])){
                            dp[j][i]=true;
                        }
                    }
                }
                List<List<String>> res= new ArrayList<>();
                Deque<String> path = new ArrayDeque();
                dfs(s,0,n,path,dp,res);
                return res;
            }
    
            public void dfs(String s,int start,int n,Deque<String> path,boolean[][] dp,List<List<String>> res){
                if(start>=n){
                    res.add(new ArrayList<>(path));
                    return;
                }
                for(int i=start;i<n;i++){
                    if(!dp[start][i]){
                        continue;
                    }
                    path.addLast(s.substring(start,i+1));
                    dfs(s,i+1,n,path,dp,res);
                    path.removeLast();
                }
                
            }
    
        //最后一块石头的重量
        public int lastStoneWeightII(int[] stones) {
        	//看作01背包问题
        	int n=stones.length,sum=0;
        	for(int i:stones){
        		sum+=i;
        	}
        	int target = sum/2;
        	int[] dp=new int[target+1];
        	for(int i=0;i<n;i++){
        		for(int j=target;j>=stones[i];j--){
        			dp[j]=Math.max(dp[j],dp[j-stones[i]]+stones[i]);
        		}
        	}
        	return sum-2*dp[target];
        }
    
        //打家劫舍2 （环形的房屋）
        public int rob(int[] nums) {
        	if (nums == null || nums.length == 0)
            	return 0;
        	int n=nums.length;
        	int[][] dp=new int[n][2];     //dp[0][0] 不抢第一个房子，dp[0][1] 抢第一个房子
        	dp[0][0]=0;
        	dp[0][1]=nums[0];
        	for(int i=1;i<n;i++){
        		dp[i][0]=Math.max(dp[i-1][0],dp[i-1][1]);
        		dp[i][1]=dp[i-1][0]+nums[i];
        	}
        	int tmp1=dp[n-1][0];   //第一家投了就不偷第二家了
    
        	for(int i=0;i<n;i++)
        		Arrays.fill(dp[i],0);
        	//第一家不偷
        	dp[0][1]=0;
        	for(int i=1;i<n;i++){
        		dp[i][0]=Math.max(dp[i-1][0],dp[i-1][1]);
        		dp[i][1]=dp[i-1][0]+nums[i];
        	}
        	int tmp2=Math.max(dp[n-1][0],dp[n-1][1]);
        	return Math.max(tmp1,tmp2);
        }
        //优化版本 dp[i]=Math.max(dp[i+1],dp[i+2]+nums[i]);
        public int rob(int[] nums) {
        	int n=nums.length;
        	return Math.max(myRob(Arrays.copyOfRange(nums,0,n-1)),
        					myRob(Arrays.copyOfRange(nums,1,n)));
        }
        public int myRob(int[] nums){
        	int n=nums.length;
        	int pre=0,cur=0,tmp;
        	for(int num:nums){
        		tmp=cur;
        		cur=Math.max(cur,pre+num);
        		pre=tmp;
        	}
        	return cur;
        }
        //重复k次数组的子数组的最大和
        public int kConcatenationMaxSum(int[] arr, int k) {
    	    if(arr==null||arr.length==0){
    	        return 0;
    	    }
        	int res = -Integer.MAX_VALUE,max=-Integer.MAX_VALUE,sum=arr[0];
        	res=max=arr[0]>0?arr[0]:0;
        	int n=arr.length;
        	for(int i=1;i<Math.min(k,2)*n;i++){
        		max=Math.max(arr[i%n],max+arr[i%n]);    //加上前面的或者只要当前元素
        		res=Math.max(res,max);
        		if(i<n){
        			sum+=arr[i];
        		}
        	}
    
        	while(sum>0&&--k>=2){
        		res=(res+sum)%1000000007;
        	}
        	return (int)res;
        }
        //统计全是1的子矩阵个数
        public int numSubmat(int[][] mat) {
        	int m=mat.length,n=mat[0].length;
        	int[][] dp=new int[m][n];
        	for(int i=0;i<m;i++){
        		for(int j=0;j<n;j++){
        			if(j==0){
        				dp[i][j]=mat[i][j];
        			}else if(mat[i][j]!=0){
        				dp[i][j]=dp[i][j-1]+1;
        			}else{
        				dp[i][j]=0;
        			}
        		}
        	}
        	int res=0;
        	for(int i=0;i<m;i++){
        		for(int j=0;j<n;j++){
        			int min = dp[i][j];
        			for(int k=i;k>=0&&min!=0;k--){
        				int min = Math.min(min,dp[k][j]);
        				res+=min;
        			}
        		}
        	}
        	return res;
        }
    
        // 找两个和为目标值且不重叠的子数组
        //1.前缀和+哈希表的方式
        public int minSumOfLengths(int[] arr, int target) {
        	  int n=arr.length;
        	int[] prefix = new int[n];     //以i元素结尾的子数组和为target时的最小长度
        	int[] subfix = new int[n];     //以i元素开头的子数组和为target时的最小长度
        	Map<Integer,Integer> prefixMap = new HashMap<>();   //前缀和，下标
        	Map<Integer,Integer> subfixMap = new HashMap<>(); 	//后缀和，下标
        	int prefixSum=0,subfixSum = 0;
        	int min1=1001,min2=1001;
        	prefixMap.put(0,-1);
        	subfixMap.put(n-1,-1);
        	for(int i=0;i<n;i++){
        		prefixSum+=arr[i];
        		Integer preindex = prefixMap.get(prefixSum-target);
        		if(preindex!=null){
        			min1 = Math.min(min1,i-preindex);
        		}
        		prefixMap.put(prefixSum,i);
        		prefix[i]=min1;
        	}
        	for(int i=n-1;i>=0;i--){
        		subfixSum+=arr[i];
        		Integer subindex = subfixMap.get(subfixSum-target);
        		if(subindex!=null){
        			min2 = Math.min(min2,subindex-i);
        		}
        		subfixMap.put(subfixSum,i);
        		subfix[i]=min2;
        	}
        	if(min1==1001||min2==1001)
        		return -1;
        	int res=1001;
        	for(int i=0;i<n-1;i++){
        		res=Math.min(res,prefix[i]+subfix[i+1]);
        	}
        	return res;
        }
        //滑动窗口+动态规划
       public int minSumOfLengths(int[] arr, int target) {
        	int n=arr.length;
        	int[] dp=new int[n+1];	//保存子数组的最小长度
        	Arrays.fill(dp,1000000);
        	int l=0,r=n-1;
        	int sum=0,minLen = 1000000;
        	int res=10000000;
        	for(l=r;l>=0;l--){
        		sum+=arr[l];
        		while(sum>target){
        			sum-=arr[r--];   //移动滑动窗口
        		}
        		if(target==sum){
        			int curLen = r-l+1;
        			res=Math.min(res,curLen+dp[r+1]);
        			dp[l]=Math.min(curLen,dp[l+1]);  //更新状态
        		}else{
        			dp[l]=dp[l+1];
        		}
        	}
        	return res>=1000000?-1:res;
        }
        //剑指 Offer 63. 股票的最大利润
        public int maxProfit(int[] prices) {
        	int n=prices.length;
        	int[] dp = new int[n];
        	dp[0]=0;
        	int minPrices=prices[0];
        	for(int i=1;i<n;i++){
        		minPrices=Math.min(minPrices,prices[i]);
        		dp[i]=Math.max(dp[i-1],prices[i]-minPrices);
        	}
        	return dp[n-1];
        }
    
    
        //k个一组链表反转
         public ListNode reverseKGroup(ListNode head, int k) {
         	if(k==1){
         		return head;
         	}
         	ListNode newHead = new ListNode(0,head);
         	ListNode reHead=newHead;
         	ListNode begin=head,end=head;
         	int tmp = k;
         	while(end!=null&&tmp>=0){
         		if(tmp==0){
         			ListNode node = begin;
         			ListNode next = end.next;
         			end.next=null;
         			ListNode[] reverseList = reverse(node.next);
         			reHead.next=reverseList[0];
         			reverseList[1].next = next;
         			begin=next;
         			end=begin;
         			tmp=k;
         		}else{
         			end=end.next;
         			tmp--;
         		}
         	}
         	return newHead.next;
         }
         public ListNode[] reverse(ListNode head){
         	ListNode pre = null;
         	ListNode cur = head;
         	while(cur!=null){
         		ListNode nextNode = cur.next;
         		cur.next=pre;
         		pre=cur;
         		cur=nextNode;
         	}
         	ListNode node = pre;
         	while(node.next!=null){
         		node = node.next;
         	}
         	return new ListNode[]{pre,node};   //返回首尾节点
         }
    
         //奇偶链表
         public ListNode oddEvenList(ListNode head) {
            if(head==null)
                return null;
            ListNode eventHead = head.next;
            ListNode odd = head,event = eventHead;
            while(event!=null&&event.next!=null){
            	odd.next = event.next;
            	odd = odd.next;
            	event.next = odd.next;
            	event = event.next;
            }
            odd.next=eventHead;
            return head;
        }
    
        //生成括号
        List<String> res= new ArrayList<>();
         public List<String> generateParenthesis(int n) {
         	StringBuffer buffer = new StringBuffer();
         	trace(buffer,0,0,n);
         	return res;
        }
        public void trace(StringBuffer buffer,int open,int close,int max){
        	if(buffer.length()==max*2){
        		res.add(buffer);
        		return;
        	}
        	if(open<max){
        		buffer.append("(");
        		trace(buufer,open+1,close,max);
        		buffer.deleteCharAt(buffer.length()-1);
        	}
        	if(close<open){
        		buffer.append(")");
        		trace(buufer,open,close+1,max);
        		buufer.deleteCharAt(buffer.length()-1);
        	}
        }
       /**
       	给你一个二进制字符串数组 strs 和两个整数 m 和 n 。
    	请你找出并返回 strs 的最大子集的大小，该子集中 最多 有 m 个 0 和 n 个 1 。
       */
       public int findMaxForm(String[] strs, int m, int n) {
       		int[][] dp =new int[m+1][n+1];
       		for(int i=0;i<strs.length;i++){
       			int[] cnt = count(strs[i]);
       			for(int zero=m;zero>=cnt[0];zero--){
       				for(int one=n;one>=cnt[1];one--){
       					dp[zero][one] = Math.max(1+dp[zero-cnt[0]][one-cnt[1]],dp[zero][one]);
       				}
       			}
       		}
       		return dp[m][n];
        }
    
        public int[] count(String s){
        	int[] cnt = new int[2];
        	char[] c = s.toCharArray();
        	for(int i=0;i<c.length;i++){
        		cnt[c[i]-'0']++;
        	}
        	return cnt;
        }
    
    


