import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class Data implements Serializable {
	private String data;
	
	public Data(String s) {
		data = s;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String s) {
		data = s;
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
	
	public static Data read(byte[] x){
		try{
			ByteArrayInputStream fis=new ByteArrayInputStream(x);
			ObjectInputStream ois= new ObjectInputStream(fis);
			Data c= (Data) ois.readObject();
			ois.close();
			return c;
		}catch(Exception e){e.printStackTrace();}
		return null;
		
		
	}
	
	public String toString(){
		return data;
	}
}
