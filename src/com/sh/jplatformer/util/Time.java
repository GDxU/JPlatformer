package com.sh.jplatformer.util;

/**
 * The {@code Time} class provides static time-related utility methods.
 * @author Stefan Hösemann
 */

public class Time
{
	// toString
	//=========
	/**
	 * @param ms the time to transform in milliseconds.
	 * @return the time {@code String} in the format of {@code HH:MM:SS}, or
	 * {@code MM:SS}, if the hours value is 0.
	 */
	public static String toString( long ms )
	{
		// Calculate units
		//================
		long sec = ms  / 1000L;
		long min = sec / 60L;
		long hrs = min / 60L;
		
		sec = sec - ( min * 60L );
		min = min - ( hrs * 60L );

		// String builders
		//================
		StringBuilder sb_sec = new StringBuilder( sec +  "" );
		StringBuilder sb_min = new StringBuilder( min + ":" );
		StringBuilder sb_hrs = new StringBuilder( hrs + ":" );
		
		// Insert zero
		//============
		if ( sec < 10L ) sb_sec.insert( 0, "0" );
		if ( min < 10L ) sb_min.insert( 0, "0" );
		if ( hrs < 10L ) sb_hrs.insert( 0, "0" );

		// Return string
		//==============
		if ( hrs > 0L )
		{
			return ( sb_hrs.toString() + sb_min.toString() + sb_sec.toString() );
		}
		return ( sb_min.toString() + sb_sec.toString() );
	}
}
