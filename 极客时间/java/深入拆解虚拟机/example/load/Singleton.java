public class Singleton {
	private Singleton(){}
	
	private static class LazyHolder{
		static final Singleton singleton = new Singleton();
		static {
			System.out.println("lazyholder.<clinit>");
		}
	}
	
	public static Object getInstance(boolean flag) {
		if(flag) return new LazyHolder[2];
		return LazyHolder.singleton;
	}
	
	public static void main(String[]args) {
		getInstance(true);
		System.out.println("--------------");
		getInstance(false);
	}
}