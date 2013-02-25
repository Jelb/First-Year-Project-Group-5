package xml_jonas;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class TXTScan {
private Scanner scan;
private String[] arr;
 public TXTScan(String filePath) {
  try {
	  File file = new File(filePath);
	  boolean b = file.canRead();
   scan = new Scanner(file).useDelimiter("[,\\n]");
  } catch (FileNotFoundException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }
 
 public String[] scanning(int size) throws NoSuchElementException{
  arr = new String[size];
  for(int i = 0; i < size; i++){
   arr[i] = scan.next().trim();
  }
  return arr;
 }
 
 public boolean hasNext() {
   return scan.hasNext();
 }
}
