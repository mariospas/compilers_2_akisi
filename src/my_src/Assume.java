package my_src;


public class Assume
{
	public static void assumeTrue(boolean b) throws Exception, SemError
	{
		if (b == true)
		{
			throw new SemError();
		}
	}
}