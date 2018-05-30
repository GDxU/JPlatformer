package com.sh.jplatformer.util;

import java.io.*;
import java.util.*;

/**
 * The {@code Lang} class provides ability to read {@code Locale}-specific text data from a
 * properties file in order to implement internationalization and localization.
 * @author Stefan Hösemann
 */

public class Lang
{
	// Fields
	//=======
	private static Lang instance;
	private File currentDir;
	private String baseName;
	private Locale currentLocale;
	private Properties langProperties;
	
	// Constructor
	//============
	private Lang()
	{
	}
	
	// get
	//====
	/**
	 * Creates a single instance of this class (if there is none yet) and returns it.
	 */
	public static synchronized Lang get()
	{
		if ( instance == null )
		{
			instance = new Lang();
		}
		return ( instance );
	}
	
	// txt
	//====
	/**
	 * This is a short form method for {@code Lang.get().getText(key)}.
	 * @param key the key referring to the requested property value.
	 * @return the {@code String} value related to the specified key. If the key was not found,
	 * it is returned instead.
	 */
	public static String txt( String key )
	{
		return ( get().getText( key ) );
	}
	
	// init
	//=====
	/**
	 * Initializes this {@code Lang} with a directory containing all {@code Locale}-specific
	 * properties files. A valid properties file consists of {@code newBaseName} appended by an
	 * underscore and the ISO 639 language code (and, optionally, the ISO 639 country code), i.e.
	 * "{@code example_de_ch.properties}". This method initially attempts to load the file for the
	 * current default {@code Locale}. If this fails, the fallback properties file is loaded, with
	 * a filename only consisting of {@code newBaseName.properties}.
	 * @param path the directory containing the properties files.
	 * @param newBaseName the base name of the properties files.
	 */
	public void init( String path, String newBaseName )
	{
		// Set file values
		//================
		baseName = newBaseName;
		currentDir = new File( path );
			
		// Check dir and apply language file
		//==================================
		if ( currentDir.isFile() == true )
		{
			currentDir = currentDir.getParentFile();
		}
		this.setLocale( Locale.getDefault() );
	}
	
	// getText
	//========
	/**
	 * Returns the {@code String} for the given key from the properties file representing the
	 * {@code Locale} set for this {@code Lang}.
	 * @param key the key referring to the requested property value.
	 * @return the {@code String} value related to the specified key. If the key was not found, it
	 * is returned instead.
	 */
	public String getText( String key )
	{
		try
		{
			return ( langProperties.getProperty( key ) );
		}
		catch ( Exception e )
		{
			return ( key );
		}
	}
	
	// setLocale
	//==========
	/**
	 * Applies the specified {@code Locale} and attempts to read the properties file representing
	 * it. If the file for the specified language and country is not found, this method ignores the
	 * country specification and attempts to load the properties file that represents the specified
	 * language only. If no file has been found, the default properties file is read instead.
	 * @param newLocale the {@code Locale} to be applied.
	 */
	public void setLocale( Locale newLocale )
	{
		// Build path string
		//==================
		currentLocale = newLocale;
		String path   = currentDir.getAbsolutePath() + "/" + baseName + "_" +
		                currentLocale.getLanguage().toLowerCase();
		
		// Append country string
		//======================
		if ( currentLocale.getCountry().equals( "" ) == false )
		{
			path = path + "_" + currentLocale.getCountry().toLowerCase();
		}
		path = new File( path + ".properties" ).getAbsolutePath();
		
		// Fallback #1: Ignore country
		//============================
		if ( this.loadLanguageFile( path ) == false )
		{
			path = currentDir.getAbsolutePath() + "/" + baseName + "_" +
			       currentLocale.getLanguage().toLowerCase();
			path = new File( path + ".properties" ).getAbsolutePath();
		}
		else
		{
			return;
		}
		
		// Fallback #2: Default file
		//==========================
		if ( this.loadLanguageFile( path ) == false )
		{
			path = currentDir.getAbsolutePath() + "/" + baseName;
			path = new File( path + ".properties" ).getAbsolutePath();
		}
		else
		{
			return;
		}
		
		// Error message
		//==============
		if ( this.loadLanguageFile( path ) == false )
		{
			System.err.println( "Error reading language file: " + path + "!" );
		}
	}
	
	// setLocale
	//==========
	/**
	 * @return the {@code Locale} that is currently set for the {@code Lang} class.
	 */
	public Locale getLocale()
	{
		return ( currentLocale );
	}
	
	// loadLanguageFile
	//=================
	/**
	 * Attempts to read a specified properties file containing language data.
	 * @param path the path to the file.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	private boolean loadLanguageFile( String path )
	{
		try
		{
			File file = new File( path );
			FileInputStream input = new FileInputStream( file );
			langProperties = new Properties();
			langProperties.load( input );
			input.close();
			return ( true );
		}
		catch ( Exception e2 )
		{
			return ( false );
		}
	}

	// getAvailableLocales
	//====================
	/**
	 * @return An array containing all {@code Locales} for which there is a resource file in the
	 * resource directory.
	 */
	public Locale[] getAvailableLocales()
	{
		// Temporary values
		//=================
		String f;
		String language;
		String country;
		ArrayList<Locale> locales = new ArrayList<Locale>();
		
		// List and filter files
		//======================
		File[] allFiles = currentDir.listFiles( new FilenameFilter()
		{
			@Override
			public boolean accept( File dir, String name )
			{
				if ( name.toLowerCase().startsWith( baseName.toLowerCase() + "_" ) == true )
				{
					if ( name.toLowerCase().endsWith( ".properties" ) == true )
					{
						return ( true );
					}
				}
				return ( false );
			}		
		} );
		
		// Determine Locale codes
		//=======================
		for ( int i = 0; i < allFiles.length; i++ )
		{
			// Get filename ISO code segment
			//==============================
			f = allFiles[i].getName().toUpperCase();
			f = f.substring( baseName.length() + 1, f.length() - ".properties".length() );
			
			// Ignore empty string
			//====================
			if ( f.length() > 0 )
			{
				// Read codes
				//===========
				if ( f.contains( "_" ) == true )
				{
					language = f.substring( 0, f.indexOf( '_') );
					country  = f.substring( f.indexOf( '_' ) + 1 );
				}
				else
				{
					language = f.substring( 0 );
					country  = "";
				}
				locales.add( new Locale( language, country ) );
			}
		}
		return ( locales.toArray( new Locale[locales.size()] ) );
	}
}
