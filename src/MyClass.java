import java.io.*;
import java.util.Scanner;

public class MyClass implements Serializable {
	private int a[];
	private String info;
	private int length;
	
	MyClass(String s){
		String split[]=s.split("\\s+");
		int slength=split.length;
		length=slength-1;
		info=split[0];
		a=new int[length];
		for(int i=0;i<length;i++){
			a[i]=Integer.parseInt(split[i+1]);
		}
	}
	
	public byte[] write(){
		byte[] x=null;
		try{
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		x=fos.toByteArray();
		oos.close();
		}catch(Exception e){e.printStackTrace();}
		return x;
	}
	
	public static MyClass read(byte[] x){
		try{
		ByteArrayInputStream fis=new ByteArrayInputStream(x);
		ObjectInputStream ois= new ObjectInputStream(fis);
		MyClass c= (MyClass) ois.readObject();
		ois.close();
		return c;
		}catch(Exception e){e.printStackTrace();}
		return null;
		
		
	}
	
	public String toString(){
		String s;
		s=info;
		for(int i=0;i<length;i++){
			s=s+" "+a[i];
		}
		return s;
	}
	
	
	public static void main(String[] args){
		byte x[];
		MyClass c=new MyClass("PrvaSerijalizovanaKlasa 2 32 4 6\n");
		System.out.print("kreirani objekat: "+c+"\n");
		System.out.print("Serijalizujem...\n");
		x=c.write();
		MyClass serC=MyClass.read(x);
		System.out.print("Ponovo formirani objekat: "+serC);
		
		c=new MyClass("DrugaSerijalizovanaKlasa 9 102 9999 1\n");
		System.out.print("kreirani objekat: "+c+"\n");
		System.out.print("Serijalizujem...\n");
		x=c.write();
		serC=MyClass.read(x);
		System.out.print("Ponovo formirani objekat: "+serC);
	}
	
}
