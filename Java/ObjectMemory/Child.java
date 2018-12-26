package cn.just.shinelon.objectMemory;


/**
 * java在继承父类的变量和方法时是有区别的，继承方法时它编译时会将方法转移到子类中，因此
 * 访问其方法取决于实际引用的java对象的类型，但是变量不会转移到子类中，因此变量的访问取决于声明该变量时的类型
 * @author shinelon
 *
 */
public class Child extends Parent{
	private int i=1;
	
	public static void main(String[] args) {
		new Child();
	}
	
}
class Parent{
	private int i=11;
	public void print(){
		System.out.println("print");
	}
}
