public class ChoiceSort {
	
	public static void sort(int[] a){
		for(int i=0;i<a.length-1;i++){
			int index=i;
			for(int j=i+1;j<a.length;j++){
				if(a[index]>a[j]){
					index=j;
				}
			}
			if(index!=i){
				int temp=a[i];
				a[i]=a[index];
				a[index]=temp;
			}
		}
	}
	
	public static void main(String[] args) {
		int[] a=new int[]{
			12,4,5,78,12,5,23,6	
		};
		sort(a);
		for(int i=0;i<a.length;i++){
			System.out.println(a[i]);
		}
	}

}
