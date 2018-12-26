package cn.just.shinelon.objectMemory;

/**
 * 当初始化一个子类的时候，它首先会初始化父类，隐式调用父类无参构造器，如果子类重写了父类的方法它会调用子类的方法
 * 然后初始化子类变量，执行非静态代码块（和代码的编写顺序一致），调用子类构造器
 * @author shinelon
 */
public class Wolf extends Animal{
	private String name="wolf";
	private Double weight;
	{
		System.out.println(this.name);
	}
	public Wolf(String name,Double weight){
		System.out.println("子类有参构造器");
		this.name=name;
		this.weight=weight;
	}
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		System.out.println("子类重写方法");           //①
		return "name: "+name+" weight: "+weight;		//④
	}
	
	public static void main(String[] args){
		System.out.println(new Wolf("灰太狼",52.0));		//③
	}
}
class Animal{
	private String desc;
	public Animal(){
		//this在构造函数中的时候this代表正在初始化的java对象
		//这里会调用子类中被重新的方法
		this.desc=getDesc();
		System.out.println("父类无参构造器");			//②
	}
	public String getDesc(){
		return "Animal";
	}
	public String toString(){
		return desc;
	}
	
}