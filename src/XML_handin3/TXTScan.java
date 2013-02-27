package XML_handin3;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class TXTScan {
	private Scanner scan;
	private String[] nyArr;
	
	public TXTScan(String filePath) {
		try {
			scan = new Scanner(new File(filePath)).useDelimiter("[\\n]");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	public String[] scanning(int tal){
		String[] temp = scan.next().split(",(?! |[a-zA-ZæÆøØåÅ])");
		temp[temp.length -1] = temp[temp.length-1].trim();
		if(temp.length == 33){
			nyArr = new String[]{
					temp[0], temp[1], temp[2], temp[6], temp[7], 
					temp[8], temp[9], temp[10], temp[11], temp[12], 
					temp[13], temp[14],temp[17], temp[18], temp[26]};
		} else {
			nyArr = temp;
		}
		return nyArr;
	}

	public String[] titleScanning(int tal){
		String[] temp = scan.next().split("( |,)");
		temp[temp.length -1] = temp[temp.length-1].trim();
		if(temp.length == 33){
			nyArr = new String[]{
					temp[0], temp[1], temp[2], temp[6], temp[7], 
					temp[8], temp[9], temp[10], temp[11], temp[12], 
					temp[13], temp[14],temp[17], temp[18], temp[26]};
		} else {
			nyArr = temp;
		}
		return nyArr;
	}
	
	public boolean hasNext() {
		return scan.hasNext();
	}
	
	public void closeFile() {
		scan.close();
	}
}
