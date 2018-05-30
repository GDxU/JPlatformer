package com.sh.jplatformer.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * The {@code FileUtils} class provides static utility methods for file and I/O operations.
 * @author Stefan Hösemann
 */

public class FileUtils
{
	// Fields
	//=======
	private static String rootPath;
	
	// Constructor
	//============
	private FileUtils()
	{
	}
	
	// getRoot
	//========
	/**
	 * @return the path of the directory the executable file of this application is located in.
	 * If an error occurred, this method will return the working directory specified by the key
	 * "{@code user.dir}" in the {@code System} properties.
	 */
	public static String getRoot()
	{
		// Determine root dir
		//===================
		if ( rootPath == null )
		{
			try
			{
				// Get root path
				//==============
				File rootDir = new File(ClassLoader.getSystemClassLoader().getResource(".").toURI().getPath() );
				rootPath = rootDir.getAbsolutePath();
				
				// Jump higher if not in root dir
				//===============================
				if ( rootPath.endsWith( "bin" ) )
				{
					rootPath = rootPath.substring( 0, rootPath.length() - "bin".length() );
				}
				else if ( rootPath.endsWith( "target" + File.separator + "classes" ) )
				{
					rootPath = rootPath.substring( 0, rootPath.length() - ( "target" + File.separator + "classes" ).length() );
				}
				else
				{
					rootPath = rootPath + File.separator;
				}
			}
			catch ( Exception e )
			{
				rootPath = System.getProperty( "user.dir" );
			}
		}
		
		// Add separator
		//==============
		if ( !rootPath.endsWith( File.separator ) )
		{
			rootPath += File.separator;
		}
		
		return ( rootPath );
	}
	
	// createDir
	//==========
	/**
	 * Checks if the directory of the given path exists. If it does not, it will be created. This
	 * method does only consider the folder structure and ignores files.
	 * @param path the path to check.
	 * @return {@code true} if the directory exists or was created, {@code false} otherwise.
	 */
	public static boolean createDir( String path )
	{
		File dir = new File( path );
		if ( dir.getParentFile().exists() == false )
		{
			if ( dir.getParentFile().mkdirs() == false )
			{
				return ( false );
			}
		}
		return ( true );
	}
	
	// appendExtension
	//================
	/**
	 * Checks if the given file path for a specified extension, appends it if necessary and returns
	 * the processed {@code String}. This method is case insensitive.
	 * @param file the file path to check.
	 * @param extension the extension to append.
	 * @return the processed path {@code String}.
	 */
	public static String appendExtension( String file, String extension )
	{
		if ( !file.toLowerCase().endsWith( "." + extension.toLowerCase() ) )
		{
			file = file + "." + extension;
		}
		return ( file );
	}
	
	// listFiles
	//==========
	/**
	 * Creates an array of all files of a certain type in a specified directory.
	 * @param dir the path of the directory to search. If this is not a directory, this method
	 * will return {@code null}.
	 * @param filetype the file type without period. This is case insensitive.
	 * @return the {@code File} array.
	 */
	public static File[] listFiles( String dir, String filetype )
	{
		// Check path
		//===========
		if ( new File( dir ).isDirectory() == false )
		{
			return ( null );
		}
		
		// List all files
		//===============
		File[] allFiles = new File( dir ).listFiles( new FilenameFilter()
		{
			@Override
			public boolean accept( File dir, String name )
			{
				if ( name.toLowerCase().endsWith( "." + filetype.toLowerCase() ) == true )
				{
					return ( true );
				}
				return ( false );
			}		
		} );
		return ( allFiles );
	}
}
