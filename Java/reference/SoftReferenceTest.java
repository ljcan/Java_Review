package cn.just.shinelon.reference;

import java.lang.ref.SoftReference;
/**
 * 软引用
 * @author shinelon
 *
 */
public class SoftReferenceTest {
	
	public static void main(String[] args){
		SoftReference<Person>[] people = new SoftReference[10000];
		for(int i=0;i<people.length;i++){
			people[i]=new SoftReference<Person>(new Person("名字"+i,(i+1)*4%100));
		}
		System.out.println(people[2].get());
		System.out.println(people[4].get());
		//通知系统进行垃圾回收
		System.gc();
		System.runFinalization();
		//垃圾回收机制运行之后，软引用中的元素保持不变
		System.out.println(people[2].get());
		System.out.println(people[4].get());
	}
	
}

class Person{
	String name;
	int age;
	public Person(String name,int age){
		this.name=name;
		this.age=age;
	}
	
	public String toString(){
		return "Person[name="+name+",age="+age+"]";
	}
}

