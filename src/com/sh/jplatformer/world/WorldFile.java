package com.sh.jplatformer.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapCell;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.objects.characters.Player;

/**
 * The {@code WorldFile} is an import/export tool for worlds.
 * @author Stefan Hösemann
 */

public class WorldFile
{
	// File constants
	//===============
	public static final String FILE_EXTENSION = "worldfile";
	public static final String FILE_DIR = "resources/worlds/";
	
	// Last file
	//==========
	private static String lastFile;
	
	// saveWorld
	//==========
	/**
	 * Saves the specified world to a file.
	 * @param path the path of the file relative to the root directory of the application.
	 * @param worldController the world to save.
	 */
	public static void saveWorld( String path, WorldController worldController )
	{
		try ( OutputStream fos = new FileOutputStream( new File( path ) ); )
		{
			// Create stream
			//==============
			ObjectOutputStream out = new ObjectOutputStream( fos );
			
			// Map header data
			//================
			Map map = worldController.getMap();
			
			out.writeInt   ( map.getColumns()              );
			out.writeInt   ( map.getRows()                 );
			out.writeObject( map.getTitle()                );
			out.writeObject( map.getCreator()              );
			out.writeInt   ( map.getDifficultyId()         );
			out.writeInt   ( map.getHighScore()            );
			out.writeLong  ( map.getBestTime()             );
			out.writeLong  ( map.getCountdownTime()        );
			out.writeObject( map.getBackgroundFile()       );
			out.writeObject( map.getForegroundFile()       );
			out.writeObject( map.getEnvironmentSoundFile() );
			out.writeInt   ( map.getWaterId()              );
			out.writeInt   ( map.getWaterSpeedId()         );
			out.writeFloat ( map.getWaterHeight()          );
			out.writeObject( map.getStartArea()            );
			out.writeObject( map.getFinishArea()           );
			
			// Get occupied cells
			//===================
			ArrayList<MapCell> tmp_cells = new ArrayList<MapCell>();
			
			for ( int x = 0; x < map.getColumns(); x++ )
			{
				for ( int y = 0; y < map.getRows(); y++ )
				{
					// Add to temporary array
					//=======================
					if ( map.getCellAt( x, y ).tileSetId >= 0 )
					{
						tmp_cells.add( map.getCellAt( x, y ) );
					}
				}
			}
			
			// Write occupied cells
			//=====================
			out.writeInt( tmp_cells.size() );
			
			for ( MapCell c : tmp_cells )
			{
				out.writeObject( c );
			}
			
			// Map object data
			//================
			out.writeObject( worldController.getMapObjects() );
			out.writeObject( worldController.getPlayer() );
			out.writeObject( Integer.valueOf( MapObject.idCount ) );
		}
		catch ( Exception e )
		{
			FileHandle mapFile = Gdx.files.local( path );
			System.err.println( "Error writing world file: " + mapFile.file().getName() + "!" );
		}
	}
	
	// loadWorld
	//==========
	/**
	 * Loads a world from a world file into the specified {@code WorldController}. If an error
	 * occurred, the world is reset.
	 * @param path the absolute path of the file.
	 * @param worldController the {@code WorldController} to load the world into.
	 * @return {@code false} if an error occurred. 
	 */
	@SuppressWarnings("unchecked")
	public static boolean loadWorld( String path, WorldController worldController )
	{
		// Reset
		//======
		worldController.createWorld( 0, 0 );
		lastFile = path;
		
		// Read world data
		//================
		try ( InputStream fis = new FileInputStream( new File( path ) ); )
		{
			// Create stream
			//==============
			ObjectInputStream in = new ObjectInputStream( fis );
			
			// Read dimensions and reset map
			//==============================
			Map map = worldController.getMap();
			map.reset( in.readInt(), in.readInt() );
			
			// Map header data
			//================
			map.setTitle               ( ( String ) in.readObject() );
			map.setCreator             ( ( String ) in.readObject() );
			map.setDifficultyId        ( in.readInt() );
			map.setHighScore           ( in.readInt() );
			map.setBestTime            ( in.readLong() );
			map.setCountdownTime       ( in.readLong() );
			map.setBackgroundFile      ( ( String ) in.readObject() );
			map.setForegroundFile      ( ( String ) in.readObject() );
			map.setEnvironmentSoundFile( ( String ) in.readObject() );
			map.setWaterId             ( in.readInt() );
			map.setWaterSpeedId        ( in.readInt());
			map.setWaterHeight         ( in.readFloat() );
			map.setStartArea           ( ( Rectangle ) in.readObject() );
			map.setFinishArea          ( ( Rectangle ) in.readObject() );
			
			// Read occupied cells
			//====================
			MapCell cell = null;
			int cells    = in.readInt();
			
			// Transfer cell attributes
			//=========================
			for ( int i = 0; i < cells; i++ )
			{
				cell = ( ( MapCell ) in.readObject() );
				map.getCellAt( cell.x, cell.y ).tileId = cell.tileId;
				map.getCellAt( cell.x, cell.y ).tileSetId = cell.tileSetId;
			}
			
			// Map object data
			//================
			worldController.setMapObjects( ( ArrayList<MapObject> ) in.readObject() );
			MapObject player = ( ( MapObject ) in.readObject() );
			MapObject.idCount = ( ( Integer ) in.readObject() ).intValue();
			
			// Update world controller references
			//===================================
			for ( MapObject o : worldController.getMapObjects() )
			{
				o.initFrames();
				o.initAgility();
				o.setWorldController( worldController );
			} 
			
			// Reset player position
			//======================
			if ( player == null )
			{
				// Create new player
				//==================
				player = new Player( worldController );
				worldController.addMapObject( player,
				                              worldController.getMap().getStartArea().x,
				                              worldController.getMap().getStartArea().y,
				                              false );
				worldController.setPlayer( player );
			}
			else
			{
				// Set player coordinates
				//=======================
				worldController.setPlayer( player );
				worldController.getPlayer().setPosition( worldController.getMap().getStartArea().x,
				                                         worldController.getMap().getStartArea().y,
				                                         false );
			}

			// Updates
			//========
			worldController.updateTiles();
			worldController.resetTimer();
			
			return ( true );
		}
		catch ( Exception e )
		{
			System.err.println( "Error reading map file: " + Gdx.files.internal( path ).file().getName() + "!" );
			worldController.createWorld( 0, 0 );
			return ( false );
		}
	}
	
	// loadWorldMetaInformation
	//=========================
	/**
	 * Opens a world file and reads only meta information (creator, title, difficulty, ...).
	 * @param path the path of the file relative to the root directory of the application.
	 * @return a {@code Map} containing meta information.
	 */
	public static Map loadWorldMetaInformation( String path )
	{
		// Read world data
		//================
		try ( InputStream fis = new FileInputStream( new File( path ) ); )
		{
			// Create stream
			//==============
			ObjectInputStream in = new ObjectInputStream( fis );
			
			// Read map meta data
			//===================
			Map map = new Map( in.readInt(), in.readInt() );
			map.setTitle        ( ( String ) in.readObject() );
			map.setCreator      ( ( String ) in.readObject() );
			map.setDifficultyId ( in.readInt() );
			map.setHighScore    ( in.readInt() );
			map.setBestTime     ( in.readLong() );
			map.setCountdownTime( in.readLong() );
			
			// Return map
			//===========
			return ( map );
		}
		catch ( Exception e )
		{
			System.err.println( "Error reading map file: " + Gdx.files.internal( path ).file().getName() + "!" );
			return ( new Map( 0, 0 ) );
		}
	}
	
	// reloadWorld
	//============
	/**
	 * Reloads the world file that has been loaded recently by calling the {@code loadWorld} method.
	 * If no world has been loaded before, this method will do nothing.
	 * @param worldController the {@code WorldController} to load the world into.
	 */
	public static void reloadWorld( WorldController worldController )
	{
		loadWorld( lastFile, worldController );
	}
	
	// overwriteWorld
	//===============
	/**
	 * Overwrites the world file that has been loaded recently by calling the {@code loadWorld}
	 * method. If no world has been loaded before, this method will do nothing.
	 * @param worldController the {@code WorldController} that stores the world to save.
	 */
	public static void overwriteWorld( WorldController worldController )
	{
		saveWorld( lastFile, worldController );
	}
	
	// saveHighscores
	//===============
	public static void saveHighscores( WorldController worldController )
	{
		// Get values
		//===========
		boolean newRecords = false;
		int highScore      = worldController.getMap().getHighScore();
		long bestTime      = worldController.getMap().getBestTime();		
		long currentTime   = worldController.getElapsedTime();
		
		// Check high score
		//=================
		if ( worldController.getScore() > highScore )
		{
			newRecords = true;
			highScore = worldController.getScore();
		}
		
		// Calculate time if countdown
		//============================
		if ( worldController.getMap().getCountdownTime() != Map.COUNTDOWN_DISABLED )
		{
			currentTime = worldController.getMap().getCountdownTime() - currentTime;
		}
		
		// Check best time
		//================
		if ( currentTime < bestTime || bestTime <= 0L )
		{
			newRecords = true;
			bestTime = currentTime;
		}
		
		// Save records
		//=============
		if ( newRecords == true )
		{
			WorldFile.reloadWorld( worldController );
			worldController.getMap().setHighScore( highScore );
			worldController.getMap().setBestTime ( bestTime );
			WorldFile.overwriteWorld( worldController );
		}
	}
}
