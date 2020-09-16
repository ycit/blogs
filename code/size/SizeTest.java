public class SizeTest{
	
	private static Object[] objs = new Object[1];
	
	private static int i = 4;
	
	public static void main(String[]args) {
	    int j = 3;
		System.out.println("static int  size is:" + SizeUtil.sizeof(i));
		System.out.println("inner i  size is:" + SizeUtil.sizeof(j));
	    System.out.println("object  size is:" + SizeUtil.sizeof(new Object()));
	    System.out.println("object  size is:" + SizeUtil.sizeof(new Object()));
	    System.out.println("empty object array with  size is:" + SizeUtil.sizeof(new Object[]{}));
		System.out.println("object array with 1 object size is:" + SizeUtil.sizeof(objs));
	}
	
}