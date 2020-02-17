// -----------------------------------------------------
//The goal of this program is to read .bib files, extract
//certain information from them, and reformat the information
//into 3 output files: IEEE.json, ACM.json, and NJ.json
//The user may also request information written to those
//output files and have them printed on the console.
// -----------------------------------------------------

import java.util.Scanner; //Imports Scanner utility
import java.io.FileInputStream; //Converts file name to an object to be used by Scanner
import java.io.PrintWriter; //To write to a text file
import java.io.FileOutputStream; //Converts file name to an object to be used by PrintWriter
import java.io.FileNotFoundException; //Exception thrown if file not found(reading) / file could not be created(writing)
import java.util.StringTokenizer; //Imports StringTokenizer
import java.io.BufferedReader; //To read from a text file
import java.io.FileReader; //Converts file name to an object to be used by BufferedReader
import java.util.regex.Matcher; //import for pattern matching
import java.util.regex.Pattern; //import for pattern matching
import java.io.File; //to delete files
import java.io.IOException;

public class BibCreator //reads .bib files and returns the whole file in a string
{
	static int valid = 0, invalid = 0; //static variables for the amount of valid and invalid files
	
	//read files using Scanner
	public static String readFile(Scanner sc)
	{
		String s = "";
		while (sc.hasNextLine()) //While the file still has a next line and is not at END OF FILE
		{
			s += sc.nextLine(); //add to the String
		}
		sc.close(); //Close Scanner
		return s; //return the file
	}
	
	public static void readBuffer(BufferedReader br, String fileName) throws IOException
	{
		System.out.println("Here are the contents of the successfully created .JSON file: " + fileName);
		String s = br.readLine();
		
		while (s != null)
		{
			System.out.println(s);
			s = br.readLine();
		}
		br.close();
	}
	
	//delete files wrt Latex(i).bib
	public static void deleteFile(int i)
	{
		File file = null;
		file = new File(/Users/kajanthysubramaniam/COMP249/A3/IEEE"+ i +".json");
		file.delete();
		file = new File("/Users/kajanthysubramaniam/COMP249/A3/ACM" + i + ".json");
		file.delete();
		file = new File("/Users/kajanthysubramaniam/COMP249/A3/NJ" + i + ".json");
		file.delete();
	}
	
	//PART 5: PROCESSING FOR VALIDATION (delete or to write to output files)
	public static void processFilesForValidation(Scanner sc, int i, PrintWriter pw) throws FileInvalidException, FileNotFoundException
	{
		String bibFile = readFile(sc); //turn the .bib file into a String
		String ieee = "", acm = "", nj = "";
		int k = 1; //to be used to increment article numbers (specifically in ACM files)
		
		StringTokenizer articleTokenizer = new StringTokenizer(bibFile, "@"); //separate the .bib file by article
		
		while(articleTokenizer.hasMoreTokens()) //while the StringTokenizer still has tokens (in this case, articles)
		{
			String article = articleTokenizer.nextToken(); //takes current article
			String authors[] = new String[1]; //just to initialize the array
			String title = "", journal = "", volume = "", number = "", pages = "", month = "", year = "", doi = "";
			
			Pattern patternFields = Pattern.compile("([a-zA-Z]*?)=\\{(.*?)\\}"); //Match the field patterns (in this case, it's field={...} )
		    Matcher matcherFields = patternFields.matcher(article); //Match the pattern with each article
		    
		    while(matcherFields.find()) //for loop which separates each field and checks for validity
		    {
		    	String field = matcherFields.group(); //takes each matched field and puts it in the String 'field'
		    	
		    	String[] splitField = field.split("="); //Split the field into two: splitField[0] is the name of the field, splitField[1] is the value of the field with brackets
		    	String splitted = splitField[1].substring(1,splitField[1].length()-1); //takes the value of splitField[1] without the accolades

		    	if (splitField[1].charAt(0) == '{' && splitField[1].charAt(1) == '}') //If there is an empty field (such as ={} ), then throw an exception
		    	{
		    		invalid++;
		    		deleteFile(i); //if it's invalid, delete all the corresponding files 
		    		throw new FileInvalidException("Error: Detected Empty Field!\n"
							  +"============================\n\n"
							  +"Problem detected with input file: Latex" + i + ".bib\n" 
							  + "File is Invalid: Field \"" + splitField[0] + "\" is Empty. Processing stopped at this point. Other empty fields may be present as well!\n");
		    	}
		    	else if(splitField[0].equals("author")) //take the author field
		    		authors = splitted.split(" and ");
		    	else if(splitField[0].equals("title")) //take the title field
		    		title = splitted;
		    	else if(splitField[0].equals("journal")) //take the journal field
		    		journal = splitted;
		    	else if(splitField[0].equals("volume")) //take the volume field
		    		volume = splitted;
		    	else if(splitField[0].equals("number")) //take the number field
		    		number = splitted;
		    	else if(splitField[0].equals("pages")) //take the pages field
		    		pages = splitted;
		    	else if(splitField[0].equals("month")) //take the month field
		    		month = splitted;
		    	else if(splitField[0].equals("year")) //take the year field
		    		year = splitted;
		    	else if(splitField[0].equals("doi")) //take the doi field
		    		doi = splitted;
		    } 
		    
		    //If you reach here, then the file was valid. Proceed to creation of 3 output files

	    	//=============================================================
	    	//Get IEEE information
	    	for (int j = 0; j < authors.length-1; j++) //print the authors
	    		ieee += (authors[j] +", ");
	    	ieee += (authors[authors.length-1] + ". \"" + title + "\", " + journal +", vol. " + volume + ", no. " + number + ", p. " + pages + ", " + month + " " + year + ".\n\n");
	    	
	    	//=============================================================
	    	//Get ACM information
	    	acm += ("[" + k + "]\t" + authors[0] + " et al. " + year + ". " + title + ". " + journal + ". " + volume + ", " + number + " (" + year + "), " + pages + ". DOI:https://doi.org/" + doi +"\n\n");
	    	k++;
	    	
	    	//=============================================================
	    	//Get NJ information
	    	for (int j = 0; j < authors.length-1; j++) //print the authors
	    		nj += (authors[j] +" & ");
	    	nj += (authors[authors.length-1] + ". " + title + ". " + journal +". " + volume + ", " + pages + "(" + year + ").\n\n");
		}
		
		valid++; //increment number of valid files
		
		//=============================================================
    	//CREATE THE IEEE.json file
		pw = new PrintWriter(new FileOutputStream("IEEE"+ i +".json", false));
		pw.println(ieee);
		pw.close();
		
		//=============================================================
    	//CREATE THE  ACM.json file
		pw = new PrintWriter(new FileOutputStream("ACM"+ i +".json", false));
		pw.println(acm);
		pw.close();

		//=============================================================
    	//CREATE THE  NJ.json file
		pw = new PrintWriter(new FileOutputStream("NJ"+ i +".json", false));
		pw.println(nj);
		pw.close();	
	}
	
	public static void main(String[] args) 
	{
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		System.out.println("======================\nWelcome to BibCreator!\n======================\n");
		Scanner kb = new Scanner(System.in);
		Scanner sc = null;
		PrintWriter pw = null;
		
		//PART 3: Attempt to open all 10 .bib files
		for (int i = 1; i <= 10; i++) 
		{
			try
			{
				sc = new Scanner(new FileInputStream("Latex" + i + ".bib"));
				sc.close();
			}
			catch (FileNotFoundException e) //if file does not exist
			{
				sc.close(); //closes all opened files
				System.out.println("Could not open input file Latex" + i + ".bib for reading.\n\nPlease check if file exists! Program will terminate after closing any opened files.");
				System.exit(0);
			}
		}
		
		//PART 4: If you get here, then you are able to open all 10 .bib files.
		//Create 3 output files for each .bib file
		boolean flag = false;
		for (int i = 1; i <= 10; i++)
		{
			int which = 1;
			try
			{
//				if (i == 5) //manual testing
//					throw new FileNotFoundException();
				pw = new PrintWriter(new FileOutputStream("IEEE" + i + ".json"));
				pw.close(); //will only get to this line if file creation was valid
				
				which = 2;
				pw = new PrintWriter(new FileOutputStream("ACM" + i + ".json"));
				pw.close(); //will only get to this line if file creation was valid
				
				which = 3;
				pw = new PrintWriter(new FileOutputStream("NJ" + i + ".json"));
				pw.close(); //will only get to this line if file creation was valid
			}
			catch (FileNotFoundException e)
			{
				if (which == 1)
				{
					System.out.println("Could not create IEEE" + i + ".json for Latex" + i + ".bib.\nClearing directory of all other created output files.");
					pw.close(); //must create another set of closes since it skips when the exception is thrown
				}
				if (which == 2)
				{
					System.out.println("Could not create ACM" + i + ".json for Latex" + i + ".bib.\nClearing directory of all other created output files.");
					pw.close(); //must create another set of closes since it skips when the exception is thrown
				}
				if (which == 3)
				{
					System.out.println("Could not create NJ" + i + ".json for Latex" + i + ".bib.\nClearing directory of all other created output files.");
					pw.close(); //must create another set of closes since it skips when the exception is thrown
				}				
				flag = true; //set the while loop to true and delete all files
				sc.close(); //closes Scanner (input files)
				break; //get out of the for loop
			}
		}
		
		while (flag) //if an output file could not be created, delete the directory
		{
			File file = null;
			for (int i = 1; i <= 10; i++)
			{
				deleteFile(i);
			}
			System.exit(0);
		}
		
		//PART 5: VALIDATE (THEN WRITE OR DELETE)
		for (int i = 1; i<= 10; i++)
		{
			try
			{
				sc = new Scanner(new FileInputStream("Latex" + i + ".bib"));
			}
			catch (FileNotFoundException e) //if file does not exist
			{
				sc.close();
				System.out.println("Could not open input file Latex" + i + ".bib for reading.\n\nPlease check if file exists! Program will terminate after closing any opened files.");
				System.exit(0);
			}
			
			try //validate the file
			{
				processFilesForValidation(sc, i, pw);
			}
			catch (FileInvalidException e)
			{
				String s = e.getMessage();
				System.out.println(s);
			}
			catch (FileNotFoundException e)
			{
				String s = e.getMessage();
				System.out.println(s);
			}
		}
		System.out.println("A total of " + invalid + " files were invalid, and could not be processed. All other " + valid + " files have been created.\n");
		
		//PART 7: USER INPUT TO READ FILES
		BufferedReader br = null;
		int count = 1;
		while (count < 3)
		{
			try
			{
				System.out.print("Please enter the name of one of the files that you need to review: ");
				String fileName = kb.nextLine();
				br = new BufferedReader(new FileReader(fileName));
				readBuffer(br, fileName); //if it gets here, that means the file is valid, which means we can get out of the loop
				break;
			}
			catch (IOException e)//since FileNotFoundException extends IOException, we can use IOException instead
			{
				if (count == 1)
					System.out.println("Could not open input file. File does not exist; possibly it could not be created!\n\nHowever, you will be allowed another chance to enter another file name.");
				else if (count == 2)
				{
					System.out.println("\nCould not open input file again! Either file does not exist, or could not be created.");
					System.out.println("Sorry! I am unable to display your desired files! Program will exit!");
					System.exit(0);
				}
				count++;
			}
		}
		//If you get here, you have succesfully opened a file for reading using BufferedReader
		System.out.print("Goodbye! Hope you enjoyed creating the needed files using BibCreator.");
	}
}
