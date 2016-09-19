package utility;

public class Snippet {
	public static boolean isNumeric(String str)
		{
		    for (char c : str.toCharArray())
		    {
		        if (!Character.isDigit(c)) return false;
		    }
		    return true;
		}
}

