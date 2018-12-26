package cn.just.shinelon.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
/**
 * 虚引用
 * @author shinelon
 *
 */
public class PhantomReferenceTest {
	
	public static void main(String[] args) {
		String str = new String("疯狂java讲义");
		ReferenceQueue<String> rq = new ReferenceQueue<String>();
		//虚应用不能单独使用，必须和引用队列联合使用
		PhantomReference<String> pr = new PhantomReference<String>(str, rq);
		str=null;
		System.out.println(pr.get());			//null
		System.gc();
		System.runFinalization();
		System.out.println(rq.poll()==pr);		//true
		
	}

}
