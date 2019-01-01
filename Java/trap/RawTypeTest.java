package cn.just.shinelon.trap;

import java.util.ArrayList;
import java.util.List;
/**
 * 赋值给List<Integer>时JVM会把集合里盛装的所有元素都当做Integer来处理。上面程序遍历List<Integer>集合时，只是简单
 * 地输出每一个集合元素，并未涉及集合元素的类型，因此程序并没有异常；否则会出现ClassCastException异常
 * @author shinelon
 *
 */
public class RawTypeTest {
	
	public static void main(String[] args) {
		List list = new ArrayList();
		list.add("java字符串");
		list.add("疯狂java");
		List<Integer> intList = list;
		for(int i=0;i<intList.size();i++){
			System.out.println(intList.get(i));
			//抛出异常ClassCastException
//			Integer in = intList.get(i);
//			System.out.println(in);
		}
	}

}
