package XML_handin3;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class TXTScan {
private Scanner scan;
private String[] arr, nyArr;
 public TXTScan(String filePath) {
  try {
   scan = new Scanner(new File(filePath)).useDelimiter("[\\n]");
  } catch (FileNotFoundException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }
 
  public String[] scanning(int tal){
  nyArr = scan.next().split(",");
  while(nyArr.length != tal){nyArr = scan.next().split(","); System.out.println(nyArr[0]);}
  nyArr[nyArr.length -1] = nyArr[nyArr.length -1].trim();
  return nyArr;
 }

 public boolean hasNext() {
   return scan.hasNext();
 }
}
