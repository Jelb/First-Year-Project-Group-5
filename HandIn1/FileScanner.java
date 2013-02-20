package HandIn1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileScanner   {

private static FileInputStream file;
private static DataInputStream in;
private static BufferedReader br;
	
	public FileScanner() throws FileNotFoundException{
		file = new FileInputStream("src/road_names.txt");
		in = new DataInputStream(file);
		br = new BufferedReader(new InputStreamReader(in));

	}
	
	// Returns empty string if nothing is found
	public String scanning(String input) {
		String strLine;
		String currentMatch = "";
		
		try {
			while((strLine = br.readLine()) != null) {
				if (input.contains(strLine.toLowerCase()) && strLine.length() > currentMatch.length()) {
					currentMatch = strLine;
					System.out.println("strLine: " + strLine);
					System.out.println("currentMatch: " + currentMatch);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentMatch;
	}
}
