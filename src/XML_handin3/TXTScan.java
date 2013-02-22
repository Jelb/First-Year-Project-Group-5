package XML_handin3;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class TXTScan {
private Scanner scan;
private String[] arr;
	public TXTScan(String fileName) {
		try {
			scan = new Scanner(new File(fileName)).useDelimiter("[,\\n]");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] scanning(int size) {
		arr = new String[size];
		for(int i = 0; i < size; i++){
			arr[i] = scan.next();
		}
		return arr;
	}
	
	public boolean hasNext() {
			return scan.hasNext();
	}
	
	
	public void test(){
		scan.useDelimiter(",");
		for(int i = 0; i < 100; i++){
			
			System.out.println(scan.next());
		}
	}
	
	public void close() {scan.close();}
}
