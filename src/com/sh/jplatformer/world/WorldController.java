package com.sh.jplatformer.world;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapCell;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.MapPopup;

/**
 * The {@code WorldController} represents the game world. It bundles and processes all world
 * components (i.e. map, objects, time, score) and the game logic. These components might change,
 * so they should always be accessed by the accessor methods and not cached. This class also offers
 * a variety of methods to modify the world.
 * @author Stefan Hösemann
 */

public class WorldController implements Serializable
{
	// Constants
	//==========
	private static final long serialVersionUID = 1L;
	
	// States
	//=======
	public static final int STATE_PLAYING      = 0;
	public static final int STATE_VIEW_STATS   = 1;
	public static final int STATE_BACK_TO_MENU = 2;
	public static final int STATE_PLAYER_DIES  = 3;
	public static final int STATE_RESTART      = 4;
	
	// Game
	//=====
	private int worldState;
	private int score;
	private long gameStartTime;
	private long gameElapsedTime;
	private long pauseStartTime;
	private boolean isLive;
	
	// World
	//======
	private Map map;
	private WorldCamera worldCamera;
	private transient WorldAudio worldAudio;
	private ArrayList<MapCell> visibleMapCells;
	private ArrayList<MapPopup> popups;
	
	// Player
	//=======
	private MapObject player;
	
	// Objects
	//========
	private ArrayList<MapObject> mapObjects;
	private ArrayList<MapObject> deadMapObjects;
	private ArrayList<MapObject> markedMapObjects;
	private ArrayList<MapObject> tmp_objectsInArea;
	private MapObject hoveredMapObject;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code WorldController} with an empty world.
	 */
	public WorldController()
	{
		// Initialize objects
		//===================
		map               = new Map( 0, 0 );
		worldCamera       = new WorldCamera();
		worldAudio        = new WorldAudio( this );
		visibleMapCells   = new ArrayList<MapCell>();
		popups            = new ArrayList<MapPopup>();
		mapObjects        = new ArrayList<MapObject>();
		deadMapObjects    = new ArrayList<MapObject>();
		markedMapObjects  = new ArrayList<MapObject>();
		tmp_objectsInArea = new ArrayList<MapObject>();
		isLive            = true;
		
		// Create new world
		//=================
		this.createWorld( 0, 0 );
	}
	
	// getDelta
	//=========
	/**
	 * @param amountPerSecond the desired amount per second.
	 * @return the amount per frame.
	 */
	public static float getDelta( float amountPerSecond )
	{
		return ( amountPerSecond * JPlatformerGame.get().getAverageDelta() );
	}
	
	// getDeltaRatio60
	//================
	public static float getDeltaRatio60( float amountPerSecond )
	{
		float ratio = 60f / (float) Gdx.graphics.getFramesPerSecond();
		return ( getDelta( amountPerSecond ) * ratio );
	}
	
	// createWorld
	//============
	/**
	 * Resets the world and creates a new one.
	 * @param columns the new number of columns of the {@code Map}.
	 * @param rows the new number of rows of the {@code Map}.
	 */
	public void createWorld( int columns, int rows )
	{
		// Game
		//=====
		worldState     = STATE_PLAYING;	
		score          = 0;
		gameStartTime  = System.currentTimeMillis();
		pauseStartTime = 0L;
		
		// Camera
		//=======
		worldCamera.resetZoom();
		worldCamera.position.set( 0.1f, 0.1f, 0f );
		
		// Map and environment
		//====================
		map.reset( columns, rows );
		updateTiles();
		
		if ( columns > 0 && rows > 0 )
		{
			map.setBackgroundFile      ( Resources.WORLD.availableBackgroundTextures[0] );
			map.setForegroundFile      ( Resources.WORLD.availableForegroundTextures[0] );
			map.setEnvironmentSoundFile( Resources.WORLD.availableEnvironmentSounds [0] );
		}
		
		// Objects
		//========
		mapObjects.clear();
		markedMapObjects.clear();
		player = null;
		MapObject.idCount = 0;
	}
	
	// update
	//=======
	/**
	 * Processes and updates all world components.
	 */
	public void update()
	{
		// Timers and world
		//=================
		updateTimers();
		
		if ( JPlatformerGame.get().isPaused() == false )
		{
			updateMapObjects();
			updatePopups();
			updateWorldState();
		}
		
		// Camera
		//=======
		updateCamera();
	}
	
	// updateTiles
	//============
	/**
	 * Updates all {@code MapCells} and their {@code tileId} property.
	 */
	public void updateTiles()
	{
		// Iteration
		//==========
		for ( MapCell cell : map.getCells() )
		{
			// Set center tile
			//================
			cell.tileId = 4;
			int tileSetId = cell.tileSetId;
			
			// Rectangular (right)
			//====================
			if ( compareTiles( tileSetId, cell.x + 1, cell.y ) == -1 )
			{
				if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) == -1 ) cell.tileId = 8; // Top Right Corner
				if ( compareTiles( tileSetId, cell.x, cell.y + 1 ) == -1 ) cell.tileId = 2; // Bottom Right Corner
				if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) >= +0 &&
				     compareTiles( tileSetId, cell.x, cell.y + 1 ) >= +0 ) cell.tileId = 5; // Right Side Center
			}

			// Rectangular (left)
			//===================
			if ( compareTiles( tileSetId, cell.x - 1, cell.y ) == -1 )
			{
				if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) == -1 ) cell.tileId = 6; // Top Left Corner
				if ( compareTiles( tileSetId, cell.x, cell.y + 1 ) == -1 ) cell.tileId = 0; // Bottom Left Corner
				if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) >= +0 &&
				     compareTiles( tileSetId, cell.x, cell.y + 1 ) >= +0 ) cell.tileId = 3; // Left Side Center
			}
				
			// Rectangular center (top, bottom)
			//=================================
			if ( compareTiles( tileSetId, cell.x - 1, cell.y ) >= +0 &&
			     compareTiles( tileSetId, cell.x + 1, cell.y ) >= +0 )
			{
				if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) == -1 ) cell.tileId = 7; // Top Center
				if ( compareTiles( tileSetId, cell.x, cell.y + 1 ) == -1 ) cell.tileId = 1; // Bottom Center
			}
				
			// Single tiles (horizontal)
			//==========================
			if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) == -1 &&
			     compareTiles( tileSetId, cell.x, cell.y + 1 ) == -1 )
			{
				if ( compareTiles( tileSetId, cell.x + 1, cell.y ) == -1 ) cell.tileId = 11; // Single Left
				if ( compareTiles( tileSetId, cell.x - 1, cell.y ) == -1 ) cell.tileId = 9;  // Single Right
				if ( compareTiles( tileSetId, cell.x + 1, cell.y ) >= +0 &&
				     compareTiles( tileSetId, cell.x - 1, cell.y ) >= +0 ) cell.tileId = 10; // Single Horizontal Center
			}

			// Single tiles (vertical)
			//========================
			if ( compareTiles( tileSetId, cell.x + 1, cell.y ) == -1 &&
			     compareTiles( tileSetId, cell.x - 1, cell.y ) == -1 )
			{
				if ( compareTiles( tileSetId, cell.x, cell.y + 1 ) == -1 ) cell.tileId = 12; // Single Bottom
				if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) == -1 ) cell.tileId = 14; // Single Top
				
				if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) >= +0 &&
				     compareTiles( tileSetId, cell.x, cell.y + 1 ) >= +0 ) cell.tileId = 13; // Single Vertical Center
				     
				if ( compareTiles( tileSetId, cell.x, cell.y - 1 ) == -1 &&
				     compareTiles( tileSetId, cell.x, cell.y + 1 ) == -1 ) cell.tileId = 15; // Single Tile
			}
		}
	}
	
	// updateMapObjects
	//=================
	private void updateMapObjects()
	{
		// Update all objects
		//===================
		for ( int i = 0; i < mapObjects.size(); i++ )
		{
			MapObject o = mapObjects.get( i );
			
			if ( o.isAlive() )
			{
				o.setSurroundingObjects( getMapObjects( o.getScanArea() ) );
				o.update();
			}
			if ( !o.isAlive() && o.getAlpha() == 0f )
			{
				deadMapObjects.add( o );
			}
		};
		
		// Remove dead objects
		//====================
		for ( MapObject d : deadMapObjects )
		{
			// Remove from lists
			//==================
			mapObjects.remove( d );
			markedMapObjects.remove( d );
			
			// Remove player
			//==============
			if ( player == d )
			{
				player = null;
			}
		}
		deadMapObjects.clear();
	}
	
	// updatePopups
	//=============
	/**
	 * Updates all popups. Popups will be moved up while fading out. Once a popup is invisible it
	 * is removed from the popup list.
	 */
	private void updatePopups()
	{
		// Update all popups
		//==================
		for ( MapPopup p : popups )
		{
			if ( p.getAlpha() > 0f )
			{
				p.getPosition().y += getDelta( 150f );
				p.setAlpha( p.getAlpha() - getDelta( 1f ) / 1f );
			}
		}
		
		// Remove dead popups
		//===================
		popups.removeIf( o -> o.getAlpha() <= 0f );
	}
	
	// updateCamera
	//=============
	private void updateCamera()
	{
		// Target + limits
		//================
		if ( isLive && player != null )
		{
			// Set target
			//===========
			worldCamera.setTarget( player );
			worldCamera.update();
			
			// Get objects
			//============
			Vector3 pos  = worldCamera.position;
			float w      = worldCamera.zoom * worldCamera.viewportWidth;
			float h      = worldCamera.zoom * worldCamera.viewportHeight;
			float worldW = map.getMapBounds().width;
			float worldH = map.getMapBounds().height;

			// Limit position
			//===============
			if ( pos.x < +w / 2f )          { pos.x = +w / 2f;          worldCamera.setPosition( pos ); }
			if ( pos.y < +h / 2f )          { pos.y = +h / 2f;          worldCamera.setPosition( pos ); }
			if ( pos.x > -w / 2f + worldW ) { pos.x = -w / 2f + worldW; worldCamera.setPosition( pos ); }
			if ( pos.y > -h / 2f + worldH ) { pos.y = -h / 2f + worldH; worldCamera.setPosition( pos ); }
		}
		worldCamera.update();
	}
	
	// updateTimers
	//=============
	private void updateTimers()
	{
		// Game is paused
		//===============
		if ( JPlatformerGame.get().isPaused() == true )
		{
			if ( pauseStartTime == 0L )
			{
				pauseStartTime = System.currentTimeMillis();
			}
		}
		// Game is running
		//================
		else
		{
			// Add pause delay
			//================
			if ( pauseStartTime != 0L )
			{
				// Get delay
				//==========
				long delay = System.currentTimeMillis() - pauseStartTime;
				pauseStartTime = 0L;
				
				// Object routine times
				//=====================
				for ( MapObject o : mapObjects )
				{
					o.setRoutineTimer( o.getRoutineTimer() + delay );
				}
				
				// Game time
				//==========
				gameStartTime += delay;
			}
			
			// Update game time
			//=================
			if ( map.getCountdownTime() != Map.COUNTDOWN_DISABLED )
			{
				gameElapsedTime = map.getCountdownTime() - ( System.currentTimeMillis() - gameStartTime );
				if ( gameElapsedTime < 0L )
				{
					gameElapsedTime = 0L;
				}
			}
			else
			{
				gameElapsedTime = System.currentTimeMillis() - gameStartTime;
			}
		}
	}
	
	// updateWorldState
	//=================
	/**
	 * Checks the game conditions and updates the world state.
	 */
	private void updateWorldState()
	{
		// Return if editor
		//=================
		if ( isLive == false )
		{
			return;
		}
		
		// Time out
		//=========
		if ( map.getCountdownTime() != Map.COUNTDOWN_DISABLED && gameElapsedTime <= 0L )
		{
			worldState = STATE_RESTART;
		}
		
		// Player state
		//=============
		if ( player != null )
		{
			// Player is dead
			//===============
			if ( player.isAlive() == false )
			{
				worldState = STATE_RESTART;
			}
			
			// Finish area
			//============
			if ( player.getBounds().overlaps( map.getFinishArea() ) )
			{
				worldState = STATE_VIEW_STATS;
			}
		}
	}
	
	// setWorldState
	//==============
	public void setWorldState( int worldState )
	{
		this.worldState = worldState;
	}
	
	// getWorldState
	//==============
	public int getWorldState()
	{
		return ( worldState );
	}
	
	// setLive
	//========
	/**
	 * Sets the {@code isLive} flag to the specified value. The flag is read by the sub components
	 * of this controller (objects, logic, ...) in order to disable item collection or to prevent
	 * the player from dying.
	 */
	public void setLive( boolean isLive )
	{
		this.isLive = isLive;
	}
	
	// isLive
	//=======
	/**
	 * @see com.sh.jplatformer.world.WorldController#setLive(boolean)
	 * @return the {@code isLive} flag.
	 */
	public boolean isLive()
	{
		return ( isLive );
	}
	
	// addScore
	//=========
	public void addScore( int value )
	{
		score += value;
	}
	
	// getScore
	//=========
	public int getScore()
	{
		return ( score );
	}
	
	// resetTimer
	//===========
	public void resetTimer()
	{
		gameStartTime = System.currentTimeMillis();
	}
	
	// getElapsedTime
	//===============
	public long getElapsedTime()
	{
		return ( gameElapsedTime );
	}
	
	// addPopup
	//=========
	/**
	 * @param text the displayed text.
	 * @param x the x-position (center).
	 * @param y the y-position (center).
	 */
	public void addPopup( String text, float x, float y )
	{
		popups.add( new MapPopup( text, x, y ) );
	}
	
	// getPopups
	//==========
	/**
	 * @return all active popups.
	 */
	public ArrayList<MapPopup> getPopups()
	{
		return ( popups );
	}
	
	// compareTiles
	//=============
	/**
	 * This method checks the specified {@code MapCell} and returns its {@code tileSetId}, but only
	 * if it is equal to the {@code tileSetId} argument of this method. Otherwise, an invalid tile
	 * set ID is returned ({@code -1}).
	 * @param tileSetId the tile set ID to compare against.
	 * @param col the column of the {@code MapCell} to check.
	 * @param row the row of the {@code MapCell} to check.
	 * @return see description.
	 */
	public int compareTiles( int tileSetId, int col, int row )
	{
		// Limit column + row
		//===================
		if ( col < 0 ) col = 0;
		if ( row < 0 ) row = 0;
		if ( col > map.getColumns() - 1 ) col = map.getColumns() - 1;
		if ( row > map.getRows()    - 1 ) row = map.getRows() - 1;
		
		// Compare tileSetId
		//==================
		if ( map.getCellAt( col, row ).tileSetId == tileSetId )
		{
			return ( map.getCellAt( col, row ).tileSetId );
		}
		return ( -1 );
	}
	
	// setCells
	//=========
	/**
	 * Sets the {@code tileSetId} field of all {@code MapCells} within an area to a specified value.
	 * @param area the {@code Map} area that contains the cells to edit.
	 * @param value the new value.
	 */
	public void setCells( Rectangle area, int value )
	{
		// Temporary object
		//=================
		Rectangle cell = new Rectangle();
			
		// Iteration
		//==========
		for ( int y = 0; y < map.getRows(); y++ )
		{
			for ( int x = 0; x < map.getColumns(); x++ )
			{
				// Calculate bounds
				//=================
				cell.x      = Map.CELL_SIZE * x;
				cell.y      = Map.CELL_SIZE * y;
				cell.width  = Map.CELL_SIZE;
				cell.height = Map.CELL_SIZE;

				// Set tileSetId
				//==============
				if ( area.overlaps( cell ) )
				{
					map.getCellAt( x, y ).tileSetId = value;
				}
			}
		}
		
		// Update tiles
		//=============
		this.updateTiles();
	}
	
	// getVisibleCells
	//================
	/**
	 * @return an array of all visible {@code MapCells} based on the applied {@code WorldCamera}.
	 */
	public ArrayList<MapCell> getVisibleCells()
	{
		// Clear cell list
		//================
		visibleMapCells.clear();
		
		// Visible cells start
		//====================
		int x1 = (int) ( worldCamera.getOffset().x / Map.CELL_SIZE );
		int y1 = (int) ( worldCamera.getOffset().y / Map.CELL_SIZE );
		
		if ( x1 < 0 ) x1 = 0;
		if ( y1 < 0 ) y1 = 0;
		
		// Visible cells end
		//==================
		int x2 = (int) ( worldCamera.viewportWidth  * worldCamera.zoom / Map.CELL_SIZE ) + x1 + 2;
		int y2 = (int) ( worldCamera.viewportHeight * worldCamera.zoom / Map.CELL_SIZE ) + y1 + 2;

		if ( x2 > map.getColumns() ) x2 = map.getColumns();
		if ( y2 > map.getRows() )    y2 = map.getRows();

		// Iteration
		//==========
		for ( int y = y1; y < y2; y++ )
		{
			// Check row visibility
			//=====================
			if ( Map.CELL_SIZE * y > worldCamera.getOffset().y + worldCamera.viewportHeight * worldCamera.zoom )
			{
				break;
			}
			
			// Check column visibility
			//========================
			for ( int x = x1; x < x2; x++ )
			{
				if ( Map.CELL_SIZE * x > worldCamera.getOffset().x + worldCamera.viewportWidth * worldCamera.zoom )
				{
					break;
				}
				visibleMapCells.add( map.getCellAt( x, y ) );
			}
		}
		return ( visibleMapCells );
	}
	
	// getMap
	//=======
	/**
	 * @return the {@code Map} model processed by this controller.
	 */
	public Map getMap()
	{
		return ( map );
	}
	
	// setMapObjects
	//==============
	public void setMapObjects( ArrayList<MapObject> mapObjects )
	{
		this.mapObjects = mapObjects;
	}
	
	// addMapObject
	//=============
	/**
	 * Adds a new {@code MapObject} to the world.
	 * @param newMapObject the {@code MapObject} to add.
	 * @param x the initial x-position on the {@code Map}.
	 * @param y the initial y-position on the {@code Map}.
	 * @param center if this value is {@code true}, the object will be centered at the given point.
	 */
	public void addMapObject( MapObject newMapObject, float x, float y, boolean center )
	{
		newMapObject.setPosition( x, y, center );
		mapObjects.add( newMapObject );
	}
		
	// removeMapObject
	//================
	/**
	 * Removes a specified {@code MapObject} from the world.
	 * @param mapObject the {@code MapObject} to remove.
	 */
	public void removeMapObject( MapObject mapObject )
	{
		// Reset player
		//=============
		if ( mapObject == player )
		{
			player = null;
		}
		
		// Remove object
		//==============
		mapObjects.remove( mapObject );
		markedMapObjects.remove( mapObject );
	}
	
	// removeMapObjects
	//=================
	/**
	 * Removes all {@code MapObjects} that overlap with the specified area from the world.
	 * @param area the {@code Map} area to remove all objects from.
	 */
	public void removeMapObjects( Rectangle area )
	{
		// Helper array
		//=============
		ArrayList<MapObject> objectsToRemove = new ArrayList<MapObject>();
		
		// Find objects
		//=============
		for ( MapObject object : mapObjects )
		{
			if ( area.overlaps( object.getBounds() ) == true )
			{
				objectsToRemove.add( object );
			}
		}

		// Remove objects
		//===============
		for ( MapObject object : objectsToRemove )
		{
			this.removeMapObject( object );
		}
	}
	
	// resetMapObjectRoutines
	//=======================
	/**
	 * Resets the routine timers of all {@code MapObjects} in order to restart their behavior
	 * routines.
	 */
	public void resetMapObjectRoutines()
	{
		// Reset timer
		//============
		for ( MapObject object : mapObjects )
		{
			object.resetRoutineTimer();
		}
	}
	
	// getMapObjects
	//==============
	public ArrayList<MapObject> getMapObjects()
	{
		return ( mapObjects );
	}
	
	// getMapObjects
	//==============
	/**
	 * @param area the area on the {@code Map} to scan for {@code MapObjects}.
	 * @return an {@code ArrayList} of all overlapping {@code MapObjects}.
	 */
	public ArrayList<MapObject> getMapObjects( Rectangle area )
	{
		// Clear helper array
		//===================
		tmp_objectsInArea.clear();
		
		// Find overlapping objects
		//=========================
		for ( int i = 0; i < mapObjects.size(); i++ )
		{
			if ( mapObjects.get( i ).getBounds().overlaps( area ) )
			{
				tmp_objectsInArea.add( mapObjects.get( i ) );
			}
		}
		return ( tmp_objectsInArea );
	}
	
	// setHoveredMapObject
	//====================
	public void setHoveredMapObject( MapObject hoveredMapObject )
	{
		this.hoveredMapObject = hoveredMapObject;
	}
	
	// getHoveredMapObject
	//====================
	public MapObject getHoveredMapObject()
	{
		return ( hoveredMapObject );
	}
	
	// addMarkedMapObject
	//===================
	/**
	 * Adds the specified {@code MapObject} to the array of currently marked objects. If the object
	 * is already marked, it will be unmarked. If the specified object is {@code null}, all objects
	 * will be unmarked.
	 */
	public void addMarkedMapObject( MapObject mapObject )
	{
		// Clear if null
		//==============
		if ( mapObject == null )
		{
			markedMapObjects.clear();
			return;
		}
		
		// Unmark if already marked
		//=========================
		for ( MapObject o : markedMapObjects )
		{
			if ( o == mapObject )
			{
				markedMapObjects.remove( o );
				return;
			}
		}
		
		// Add to marked list
		//===================
		markedMapObjects.add( mapObject );
	}
	
	// getMarkedMapObjects
	//====================
	public ArrayList<MapObject> getMarkedMapObjects()
	{
		return ( markedMapObjects );
	}
	
	// setPlayer
	//==========
	/**
	 * Sets the specified {@code MapObject} as new player in this {@code WorldController}.
	 * @param player the {@code MapObject} that represents the player. Any previous player
	 * is removed from the world.
	 */
	public void setPlayer( MapObject player )
	{	
		// Set new player
		//===============
		this.player = player;
		mapObjects.remove( player );
		mapObjects.add( player );
		
		// Update camera
		//==============
		this.updateCamera();
	}
	
	// getPlayer
	//==========
	/**
	 * @return the {@code MapObject} that represents the player.
	 */
	public MapObject getPlayer()
	{
		return ( player );
	}
	
	// getWorldCamera
	//===============
	public WorldCamera getWorldCamera()
	{
		return ( worldCamera );
	}
	
	// getMapBounds
	//=============
	public Rectangle getMapBounds()
	{
		return ( map.getMapBounds() );
	}
	
	// getWorldAudio
	//==============
	public WorldAudio getWorldAudio()
	{
		return ( worldAudio );
	}
}
