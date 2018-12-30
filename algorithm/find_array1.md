**题目描述**

在一个二维数组中（每个一维数组的长度相同），
每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，
输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。


、、、
public class Solution {
    public boolean Find(int target, int [][] array) {
        for（int i=0,j=0;i<array.length,j<array[].length;）{
               while(array[i][j]<=target){
                if(array[i][j]==target){
                    return true;
                }
                j++;
            }
            while(array[i][j-1]<=target){
                if(array[i][j]==target){
                    return true;
                }
                i++;
            }
            while(array[i][j-1]>=target){
                  if(array[i][j]==target){
                    return true;
                }
                j--;
            }
        }
        return false;
    }
    
    public sttaic void main(String[] args){
        int[][] array={
            {1,2,3},
            {2,5,6},
            {4,6,8}
        };
        int target=6;
        boolean find=Find(target,array);
        System.out.println(find);
    }
}
、、、
