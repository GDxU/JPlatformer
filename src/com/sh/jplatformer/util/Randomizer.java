package com.sh.jplatformer.util;

/**
 * The {@code Randomizer} class provides static utility methods to generate random numbers of
 * several types within a specified range.
 * @author Stefan Hösemann
 */

public class Randomizer
{
	// Constructor
	//============
	private Randomizer()
	{
	}
	
	// getInt
	//=======
	/**
	 * Returns a random {@code integer} within the range specified by the parameters min and max.
	 * @param min the minimum value for the random number.
	 * @param max the maximum value for the random number.
	 * @return a random {@code integer} within the given range.
	 */
	public static int getInt( int min, int max )
	{
		return ( int ) ( min + Math.round( ( max - min ) * Math.random() ) );
	}
	
	// getLong
	//========
	/**
	 * Returns a random {@code long} within the range specified by the parameters min and max.
	 * @param min the minimum value for the random number.
	 * @param max the maximum value for the random number.
	 * @return a random {@code long} within the given range.
	 */
	public static long getLong( long min, long max )
	{
		return ( min + Math.round( ( max - min ) * Math.random() ) );
	}
	
	// getFloat
	//=========
	/**
	 * Returns a random {@code float} within the range specified by the parameters min and max.
	 * @param min the minimum value for the random number.
	 * @param max the maximum value for the random number.
	 * @return a random {@code float} within the given range.
	 */
	public static float getFloat( float min, float max )
	{
		return ( float ) ( min + ( max - min ) * Math.random() );
	}
	
	// getDouble
	//==========
	/**
	 * Returns a random {@code double} within the range specified by the parameters min and max.
	 * @param min the minimum value for the random number.
	 * @param max the maximum value for the random number.
	 * @return a random {@code double} within the given range.
	 */
	public static double getDouble( double min, double max )
	{
		return ( min + ( max - min ) * Math.random() );
	}
}
