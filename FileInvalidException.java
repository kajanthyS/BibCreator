//PART 2: Creating of FileInvalidException
public class FileInvalidException extends Exception
{
	
	public FileInvalidException()
	{
		super("Error: Input file cannot be parsed due to missing information (i.e. month={}, title={}, etc.)");
	}
	
	public FileInvalidException(String message)
	{
		super(message);
	}
	
	public String getMesage()
	{
		return super.getMessage();
	}
	
}
