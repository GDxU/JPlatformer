package com.sh.jplatformer;

import com.badlogic.gdx.Gdx;
import com.sh.jplatformer.util.FileUtils;
import com.sh.jplatformer.util.Lang;
import java.io.*;
import java.util.Locale;
import java.util.Properties;

/**
 * The {@code Config} class provides access to global values and application settings.
 * @author Stefan Hösemann
 */

public class Config
{
	// File and instance
	//==================
	public static final String filePath = FileUtils.getRoot() + "resources/settings.ini";
	private static Config instance;
	
	// Settings
	//=========
	public Locale  locale;
	public boolean enableFullscreen;
	public boolean enableVSync;
	public boolean enableFixedViewport;
	public boolean editor_showFps;
	public boolean editor_showPowerInfo;
	public String  tmp_worldPath;
	
	// get
	//====
	/**
	 * Creates a single instance of this class (if there is none yet) and returns it.
	 */
	public static synchronized Config get()
	{
		if ( instance == null )
		{
			instance = new Config();
		}
		return ( instance );
	}
	
	// initDefault
	//============
	/**
	 * Initializes all settings with their default values.
	 */
	public void initDefault()
	{
		locale               = Locale.getDefault();
		enableFullscreen     = true;
		enableVSync          = true;
		enableFixedViewport  = true;
		editor_showFps       = true;
		editor_showPowerInfo = true;
		
		Lang.get().init( FileUtils.getRoot() + "resources/lang", "lang" );
	}
	
	// save
	//=====
	/**
	 * Attempts to save all settings to file.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean save()
	{
		// Create directory
		//=================
		FileUtils.createDir( filePath );
		
		// Write to file
		//==============
		try ( PrintStream out = new PrintStream( filePath ) )
		{
			out.println( "# Language" );
			out.println( "language = " + locale.getLanguage() );
			out.println( "country = "  + locale.getCountry() );
			out.println();

			out.println( "# Display" );
			out.println( "enableFullscreen = "    + enableFullscreen );
			out.println( "enableVSync = "         + enableVSync );
			out.println( "enableFixedViewport = " + enableFixedViewport );
			out.println();

			out.println( "# Editor" );
			out.println( "editor_showFps = "       + editor_showFps );
			out.println( "editor_showPowerInfo = " + editor_showPowerInfo );
		}
		catch ( Exception e )
		{
			System.err.println( "Error reading configuration file!" );
			return ( false );
		}
		return ( true );
	}
	
	// load
	//=====
	/**
	 * Attempts to load the settings from a user specific file. If an error occurred, a new settings
	 * file with the default settings is created.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean load()
	{
		try ( BufferedReader in = new BufferedReader( new FileReader( filePath ) ) )
		{
			// Init properties
			//================
			Properties p = new Properties();
			p.load( in );
			
			// Read properties
			//================
			String lang          = p.getProperty( "language" );
			String ctry          = p.getProperty( "country" );
			enableFullscreen     = Boolean.parseBoolean( p.getProperty( "enableFullscreen" ) );
			enableVSync          = Boolean.parseBoolean( p.getProperty( "enableVSync" ) );
			enableFixedViewport  = Boolean.parseBoolean( p.getProperty( "enableFixedViewport" ) );
			editor_showFps       = Boolean.parseBoolean( p.getProperty( "editor_showFps" ) );
			editor_showPowerInfo = Boolean.parseBoolean( p.getProperty( "editor_showPowerInfo" ) );
			
			// Apply locale
			//=============
			if ( lang == null ) lang = "";
			if ( ctry == null ) ctry = "";
			
			Lang.get().setLocale( new Locale( lang, ctry ) );
			Locale.setDefault( Lang.get().getLocale() );
			locale = Locale.getDefault();
		}
		catch ( Exception e )
		{
			// Write default settings
			//=======================
			System.err.println( "Error reading configuration file!" );
			initDefault();
			save();
			return ( false );
		}
		return ( true );
	}
	
	// applyDisplay
	//=============
	/**
	 * Applies the current display configuration.
	 */
	public void applyDisplay()
	{
		// Fullscreen
		//===========
		if ( enableFullscreen == true  )
		{
			Gdx.graphics.setFullscreenMode( Gdx.graphics.getDisplayMode() );
		}
		else
		{
			// Calculate window size
			//======================
			float w = Gdx.graphics.getDisplayMode().width  * 0.8f;
			float h = Gdx.graphics.getDisplayMode().height * 0.8f;
			
			if ( w < 600f ) w = 600f;
			if ( h < 600f ) h = 600f;
			
			// Set window mode
			//================
			if ( Gdx.graphics.isFullscreen() )
			{
				Gdx.graphics.setFullscreenMode( Gdx.graphics.getDisplayModes()[0] );
			}
			Gdx.graphics.setWindowedMode( (int) w, (int) h );
		}
		
		// VSync
		//======
		Gdx.graphics.setVSync( enableVSync );
	}
}