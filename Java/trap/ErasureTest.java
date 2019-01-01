package cn.just.shinelon.trap;

import java.util.ArrayList;
import java.util.List;

/**
 * 当把一个具有泛型信息的对象赋给另一个没有泛型信息的变量时，所有尖括号里的类型信息将被丢弃。
 * 比如，将一个List<String>类型的对象转型为List，则该List对集合元素的类型检查变成了类型变量的上限（即Object）。
 * JDK虽然支持泛型，但是不允许创建泛型数组
 * @author shinelon
 */
public class ErasureTest {
	
	public static void main(String[] args) {
		Apple<Integer> a = new Apple<Integer>(6);
		Integer as = a.getSize();
		//会丢失尖括号里的类型信息
		Apple b = a;
		//泛型擦除，返回类型为泛型的上限即Number
		Number size1 = b.getSize();
		//引起编译错误
//		Integer size2 = b.getSize();
		
		//编译错误，泛型擦除的时候会擦除这个尖括号中所有信息
//		for(String apple:b.getApples()){
//			System.out.println(apple);
//		}
		
	}
}
class Apple<T extends Number>{
	T size;
	public Apple(){
	}
	public Apple(T size){
		this.size=size;
	}
	public T getSize() {
		return size;
	}
	public void setSize(T size) {
		this.size = size;
	}
	
	public List<String> getApples(){
		List<String> list = new ArrayList<String>();
		for(int i=0;i<3;i++){
			list.add(new Apple<Integer>(10*i).toString());
		}
		return list;
	}
	public String toString(){
		return "Apple[size=]"+size+"]";
	}
	
}
