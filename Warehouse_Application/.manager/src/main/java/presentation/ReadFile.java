package presentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class provides the application with a reader
 * that parses the input file to generate instructions
 * for the application.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */
public class ReadFile {

	/**
	 * Instance of the file class. It holds a reference
	 * to the file from which the writer will read the
	 * commands.
	 */
	public File myObj;
	/**
	 * Instance of the scanner class.
	 */
	public Scanner myReader;
	/**
	 * Field that holds the number of lines that were 
	 * read from the input file.
	 */
	private int linesRead = 0;
	/**
	 * Field that holds the data that was read from the
	 * input file.
	 */
	private String[] dataRead;
	
	
	/**
	 * Constructor of this class.
	 * It sets the name of the file from which to read
	 * to the one given as the parameter.
	 * @param fileName : String
	 */
	public ReadFile(String fileName) {
		this.myObj = new File(fileName);
		try {
			this.myReader = new Scanner(myObj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.dataRead = new String[100];
	}
	
	/**
	 * Method used for reading or parsing the input
	 * file.
	 */
	public void read() {
		int i = 0;
		while (myReader.hasNextLine()) {
			if (i < this.dataRead.length) {
				this.dataRead[i] = myReader.nextLine();
				i++;
				this.linesRead++;
			}
			if (i >= this.dataRead.length) {
				break;
			}
		}
		myReader.close();
	}

	/**
	 * @return the data that was read
	 */
	public String[] getDataRead() {
		return dataRead;
	}
	
	/**
	 * Sets the data read from the file.
	 * @param string : String
	 * @param index : int
	 */
	public void setDataRead(String string, int index) {
		this.dataRead[index] = string;
	}

	/**
	 * @return the number of lines read from the file
	 */
	public int getLinesRead() {
		return linesRead;
	}

	/**
	 * Sets the number of lines that are read from the
	 * input file.
	 * @param linesRead : int
	 */
	public void setLinesRead(int linesRead) {
		this.linesRead = linesRead;
	}
}
