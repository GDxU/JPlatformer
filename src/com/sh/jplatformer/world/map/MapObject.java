package com.sh.jplatformer.world.map;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.agility.Agility;
import com.sh.jplatformer.world.map.collision.Collision;
import com.sh.jplatformer.world.map.collision.Collision.CollisionType;
import com.sh.jplatformer.world.map.collision.CollisionHelper;

/**
 * The {@code MapObject} class is the base class for all objects in the world.
 * @author Stefan Hösemann
 */

public abstract class MapObject implements Serializable
{
	// Object management
	//==================
	private static final long serialVersionUID = 1L;
	public static int idCount;
	protected int id;
	
	// Alignments
	//===========
	public static final int ALIGN_RIGHT  = 0;
	public static final int ALIGN_LEFT   = 1;
	public static final int ALIGN_TOP    = 2;
	public static final int ALIGN_BOTTOM = 3;
	public static final int ALIGN_CENTER = 4;
	
	// States 
	//=======
	protected String name;
	protected boolean isAlive;
	protected long routineTimer;
	protected int score;
	
	// World
	//======
	protected WorldController worldController;
	protected ArrayList<MapObject> surroundingObjects;
	
	// Power
	//======
	protected int powerId;
	protected boolean isPowerSupported;
	protected boolean isPowerOn;	
	
	// Movement and collision
	//=======================
	protected Agility agility;
	protected Collision lastCollision;
	protected Rectangle bounds;
	protected Rectangle scanArea;
	protected boolean isBlockingSpace;
	protected boolean ignoreGravity;
	
	// Alignments
	//===========
	protected int horizontalAlignment;
	protected int verticalAlignment;
	
	// Rendering
	//==========
	protected transient Sprite[] frames;
	protected long frameTimer;
	protected int frameSize;
	protected int currentFrame;
	protected float alpha;
	
	// Audio
	//======
	protected Sound soundFile;
	protected long soundId;
	
	// Constructor
	//============
	/**
	 * @param worldController the {@code WorldController} to interact with.
	 */
	public MapObject( WorldController worldController )
	{
		// General
		//========
		if ( worldController != null )
		{
			// ID counter
			//===========
			idCount ++;
			id = idCount;
			
			// Setup
			//======
			this.worldController    = worldController;
			this.surroundingObjects = new ArrayList<MapObject>();
			this.isAlive            = true;
		}
		
		// Movement and collision
		//=======================
		agility         = new Agility();
		lastCollision   = new Collision();
		bounds          = new Rectangle( 0f, 0f, Map.CELL_SIZE, Map.CELL_SIZE );
		scanArea        = new Rectangle( bounds );
		isBlockingSpace = true;
		
		// Init default agility
		//=====================
		initAgility();
		
		// Positioning and rendering
		//==========================
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_BOTTOM;
		frameSize = 64;
		alpha = 0f;
	}
	
	// createFromClassName
	//====================
	/**
	 * Creates an instance of a {@code MapObject} at runtime.
	 * @param className the class name of the {@code MapObject} to be created.
	 * @param worldController the {@code WorldController} to interact with.
	 * @return the new {@code MapObject}. This method returns {@code null} if an error occurred.
	 */
	public static MapObject createFromClassName( String className, WorldController worldController )
	{
		try
		{
			Class<?> objectClass = Class.forName( className );
			Constructor<?> ctor  = objectClass.getConstructor( WorldController.class );
			Object object        = ctor.newInstance( new Object[] { worldController } );

			return ( ( MapObject ) object );
		}
		catch ( Exception e )
		{
			System.err.println( "Error creating map object: " + className + "!" );
			return ( null );
		}
	}
	
	// draw
	//=====
	/**
	 * Draws the {@code currentFrame} of this {@code MapObject}. 
	 * @param batch the {@code SpriteBatch} to render.
	 */
	public void draw( SpriteBatch batch )
	{
		// Calculate coordinates
		//======================
		float x = bounds.x + bounds.width / 2f - frameSize / 2f;
		float y = bounds.y;
		
		// Draw frame
		//===========
		updateAlpha();
		frames[currentFrame].setPosition( x, y );
		frames[currentFrame].setSize( frameSize, frameSize );
		frames[currentFrame].setAlpha( alpha );
		frames[currentFrame].draw( batch );
	}
	
	// update
	//=======
	public void update()
	{
		// Updates
		//========
		this.updateAgility();
		this.updateBounds();
		this.act();
		this.updateFrame();
		
		// Perform player collision
		//=========================
		if ( worldController.isLive() )
		{
			if ( worldController.getPlayer() != null )
			{
				if ( worldController.getPlayer().getBounds().overlaps( bounds ) )
				{
					this.onPlayerCollision();
				}
			}
		}
	}
	
	// updateAgility
	//==============
	private void updateAgility()
	{
		agility.setGravityEnabled( !ignoreGravity );
		agility.update();
	}
	
	// updateBounds
	//=============
	/**
	 * Updates the current position of this {@code MapObject} depending on its {@code Agility}.
	 */
	private void updateBounds()
	{
		// Do not update if dead
		//======================
		if ( !isAlive )
		{
			return;
		}
		
		// Reset last collision
		//=====================
		lastCollision.x = CollisionType.NONE;
		lastCollision.y = CollisionType.NONE;
		
		// Move bounds
		//============
		if ( isAlive )
		{
			CollisionHelper.move( this, agility.getDelta().x, agility.getDelta().y );
		}
	}
	
	// updateFrame
	//============
	protected void updateFrame()
	{
	}
	
	// updateAlpha
	//============
	protected void updateAlpha()
	{
		// Update alpha
		//=============
		if ( isAlive == true )
		{
			alpha += WorldController.getDelta( 7f );
			if ( alpha > 1f ) alpha = 1f;
		}
		else
		{
			alpha -= WorldController.getDelta( 7f );
			if ( alpha < 0f ) alpha = 0f;
		}
	}
	
	// initFrames
	//===========
	/**
	 * Initializes the {@code frames} array by searching {@code Resources.WORLD.objectSprites} for
	 * all sprites by a key starting with the {@code name} of this {@code MapObject}.
	 */
	public void initFrames()
	{
		try
		{
			// Find sprites
			//=============
			List<String> frameKeys = new ArrayList<String>();
			Set<String> keys = Resources.WORLD.objectSprites.keySet();
			
			for ( String k : keys )
			{
				if ( k.startsWith( name + "_" ) )
				{
					frameKeys.add( k );
				}
			}
			
			// Init frames
			//============
			frames = new Sprite[frameKeys.size()];
			for ( int i = 0; i < frameKeys.size(); i++ )
			{
				frames[i] = Resources.WORLD.objectSprites.get( frameKeys.get( i ) );
			}
		}
		catch ( Exception e )
		{
			System.err.println( "Error initializing frames for map object: " + name + "!" );
		}
	}
	
	// initAgility
	//============
	public void initAgility()
	{
		// Check nulls (for compatibility)
		//================================
		if ( agility == null )
		{
			agility = new Agility();
		}
		agility.createVelocity();
		
		if ( lastCollision == null )
		{
			lastCollision = new Collision();
		}
		
		// Velocity
		//=========
		agility.getVelocity().setAcceleration( 10f );
		agility.getVelocity().setDeceleration( 10f );
		agility.getVelocity().setMaxPositive( 300f );
		agility.getVelocity().setMaxNegative( 300f );
		
		// Jumping
		//========
		agility.setMinJumpHeight( 32f );
		agility.setMaxJumpHeight( 32f );
	}
	
	// onPlayerCollision
	//==================
	/**
	 * This method is called if the {@code WorldController} is live and if the bounds of this
	 * {@code MapObject} overlap the bounds of the player object (if existent). 
	 */
	public void onPlayerCollision()
	{
	}
	
	// act
	//====
	/**
	 * Performs the individual routine of this {@code MapObject}
	 */
	public void act()
	{	
	}
	
	// onUse
	//======
	/**
	 * This method is performed when this {@code MapObject} is used.
	 */
	public boolean onUse()
	{	
		return ( true );
	}
	
	// use
	//====
	public void use()
	{
		for ( MapObject o : surroundingObjects )
		{
			if ( bounds.overlaps( o.getBounds() ) )
			{
				o.onUse();
			}
		}
	}
	
	// getFrames
	//==========
	public Sprite[] getFrames()
	{
		return ( frames );
	}
	
	// getAlpha
	//=========
	public float getAlpha()
	{
		return ( alpha );
	}
	
	// setWorldController
	//===================
	public void setWorldController( WorldController worldController )
	{
		this.worldController = worldController;
	}
	
	// getWorldController
	//===================
	public WorldController getWorldController()
	{
		return ( worldController );
	}
	
	// setAlive
	//=========
	public void setAlive( boolean isAlive )
	{
		this.isAlive = isAlive;
	}
	
	// isAlive
	//========
	public boolean isAlive()
	{
		return ( isAlive );
	}
	
	// getName
	//========
	public String getName()
	{
		return ( name );
	}
	
	// getScore
	//=========
	public int getScore()
	{
		return ( score );
	}
	
	// isPowerSupported
	//=================
	public boolean isPowerSupported()
	{
		return ( isPowerSupported );
	}
	
	// setPowerId
	//===========
	public void setPowerId( int powerId )
	{
		this.powerId = powerId;
	}
	
	// getPowerId
	//===========
	public int getPowerId()
	{
		return ( powerId );
	}
	
	// setPowerOn
	//===========
	public void setPowerOn( boolean isPowerOn )
	{
		this.isPowerOn = isPowerOn;
	}
	
	// isPowerOn
	//==========
	public boolean isPowerOn()
	{
		return ( isPowerOn );
	}
	
	// isBlockingSpace
	//================
	public boolean isBlockingSpace()
	{
		return ( isBlockingSpace );
	}
	
	// setRoutineTimer
	//================
	public void setRoutineTimer( long routineTimer )
	{
		this.routineTimer = routineTimer;
	}
	
	// resetRoutineTimer
	//==================
	/**
	 * Resets the routine timer in order to synchronize all objects routines.
	 */
	public void resetRoutineTimer()
	{
		routineTimer = 0L;
	}
	
	// getRoutineTimer
	//================
	public long getRoutineTimer()
	{
		return ( routineTimer );
	}
	
	// setSurroundingObjects
	//======================
	/**
	 * Sets an array of {@code MapObjects} which is used for collision detection and scanned for
	 * usable objects.
	 * @param surroundingObjects an array of {@code MapObjects}.
	 */
	public void setSurroundingObjects( ArrayList<MapObject> surroundingObjects )
	{
		this.surroundingObjects.clear();
		this.surroundingObjects.addAll( surroundingObjects );
	}
	
	// getSurroundingObjects
	//======================
	public ArrayList<MapObject> getSurroundingObjects()
	{
		return ( surroundingObjects );
	}
	
	// setPosition
	//============
	/**
	 * @param position the new position of this {@code MapObject} on the {@code Map}.
	 * @param center if this value is {@code true}, the object will be centered at the given point.
	 */
	public void setPosition( Vector2 position, boolean center )
	{
		this.setPosition( position.x,  position.y, center );
	}
	
	// setPosition
	//============
	/**
	 * @param x the x-position on the {@code Map}.
	 * @param y the y-position on the {@code Map}.
	 * @param center if this value is {@code true}, the object will be centered at the given point.
	 */
	public void setPosition( float x, float y, boolean center )
	{
		// Set position
		//=============
		if ( center == true )
		{
			bounds.x = x - bounds.width  / 2f;
			bounds.y = y - bounds.height / 2f + 1f;
		}
		else
		{
			bounds.x = x;
			bounds.y = y;
		}
		
		// Reset jump
		//===========
		agility.resetJump();
	}
	
	// getFrameSize
	//=============
	public int getFrameSize()
	{
		return ( frameSize );
	}
	
	// getAgility
	//===========
	public Agility getAgility()
	{
		return ( agility );
	}
	
	// getLastCollision
	//=================
	public Collision getLastCollision()
	{
		return ( lastCollision );
	}
	
	// isOnGround
	//===========
	public boolean isOnGround()
	{
		return ( lastCollision.y == CollisionType.BOTTOM );
	}
	
	// getBounds
	//==========
	public Rectangle getBounds()
	{
		return ( bounds );
	}
	
	// getScanArea
	//============
	/**
	 * @return the rectangular area around this {@code MapObject} to scan for other objects.
	 */
	public Rectangle getScanArea()
	{
		int size = Map.CELL_SIZE;
		
		scanArea.x      = bounds.x      - size / 2f;
		scanArea.y      = bounds.y      - size / 2f;
		scanArea.width  = bounds.width  + size;
		scanArea.height = bounds.height + size;
		
		return ( scanArea );
	}
	
	// getHorizontalAlignment
	//=======================
	public int getHorizontalAlignment()
	{
		return ( horizontalAlignment );
	}
	
	// getVerticalAlignment
	//=====================
	public int getVerticalAlignment()
	{
		return ( verticalAlignment );
	}
}
