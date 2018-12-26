package cn.just.shinelon.reference;

import java.lang.ref.WeakReference;
/**
 * 弱引用
 * @author shinelon
 *
 */
public class WeakReferenceTest {
	
	public static void main(String[] args){
		String str = new String("疯狂java讲义");
		WeakReference<String> wr = new WeakReference<String>(str);
		str=null;
		System.out.println(wr.get());		//疯狂java讲义
		System.gc();
		System.runFinalization();
		System.out.println(wr.get());		//null
	}
}
