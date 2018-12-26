package cn.just.shinelon.objectMemory;

/**
 * @author shinelon
 *
 */
public class Derived extends Base{
	private int i=22;
	public Derived(){
		System.out.println(this.getClass());
		i=222;
	}
	public void display(){
		System.out.println(i);
//		FileInputStream
	}
	
	public static void main(String[] args){
		new Derived();
	}

}

class Base{
	private int i=2;
	public Base(){
		//但是此时this虽然是正在初始化的java对象，但是它在父类构造器中，因此this的编译时类型为父类Base，所以结果为2
		System.out.println(this.i);
		//this代表正在初始化的java对象
		//此时会调用子类中重写的方法，但是由于子类此时还没有初始化完成，因此值为0
		this.display();
	}
	
	public void display(){
		System.out.println(i);
	}
}
