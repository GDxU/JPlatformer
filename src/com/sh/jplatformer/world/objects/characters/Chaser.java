package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.math.Rectangle;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.agility.Agility.Direction;
import com.sh.jplatformer.world.map.collision.CollisionHelper;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Chaser extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		ROAMING, CHASING, HALTING
	}
	private Routine routine;
	private Direction direction;
	
	// Constructor
	//============
	public Chaser( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Chaser";
		bounds.width    = 54f;
		bounds.height   = 54f;
		isBlockingSpace = false;
		
		// Routine
		//========
		routine = Routine.ROAMING;
		direction = Direction.POSITIVE;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Init routine
		//=============
		if ( routine == null )
		{
			routine = Routine.ROAMING;
		}
		
		// Get map
		//========
		Map map = worldController.getMap();
		
		// Follow player
		//==============
		for ( MapObject o : surroundingObjects )
		{
			// Find player
			//============
			if ( o == worldController.getPlayer() && o.isOnGround() )
			{	
				// Check y-position
				//=================
				if ( o.getBounds().y >= bounds.y &&
				     o.getBounds().y < bounds.y + Map.CELL_SIZE )
				{
					// Player is west
					//===============
					if ( o.getBounds().x + o.getBounds().width < bounds.x )
					{		
						if ( map.isBlocked( bounds.x, bounds.y - Map.CELL_SIZE / 2f ) )
						{
							routine = Routine.CHASING;
							agility.accelerate( true );
						}
						return;
					}
					
					// Player is east
					//===============
					if ( o.getBounds().x > bounds.x + bounds.width )
					{
						if ( map.isBlocked( bounds.x + bounds.width, bounds.y - Map.CELL_SIZE / 2f ) )
						{
							routine = Routine.CHASING;
							agility.accelerate( false );
						}
						return;
					}
				}
			}
		}
		
		// Halt when player lost
		//======================
		if ( routine == Routine.CHASING )
		{
			routine = Routine.HALTING;
			routineTimer = System.currentTimeMillis() + 2000L;
		}

		// Return to roaming
		//==================
		if ( routine == Routine.HALTING )
		{
			if ( routineTimer < System.currentTimeMillis() )
			{
				routine = Routine.ROAMING;
			}
		}
		
		// Roam on platform
		//=================
		if ( routine == Routine.ROAMING && isOnGround() == true && CollisionHelper.isOnCell( this ) )
		{
			// Move east
			//==========
			if ( direction == Direction.POSITIVE )
			{
				agility.accelerate( false );
				
				if ( !CollisionHelper.isEastCellPassable( this ) )
				{
					direction = Direction.NEGATIVE;
					agility.getVelocity().reset();
				}
			}
			
			// Move west
			//==========
			else
			{
				agility.accelerate( true );
				
				if ( !CollisionHelper.isWestCellPassable( this ) )
				{
					direction = Direction.POSITIVE;
					agility.getVelocity().reset();
				}
			}
		}
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		worldController.getPlayer().setAlive( false );
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// While roaming
		//==============
		if ( routine == Routine.ROAMING )
		{
			if ( agility.getDirection() == Direction.POSITIVE ) currentFrame = 0;
			if ( agility.getDirection() == Direction.NEGATIVE ) currentFrame = 1;
		}
		
		// While chasing
		//==============
		if ( routine == Routine.CHASING )
		{
			if ( agility.getDirection() == Direction.POSITIVE ) currentFrame = 2;
			if ( agility.getDirection() == Direction.NEGATIVE ) currentFrame = 3;
		}
		
		// While falling
		//==============
		if ( agility.isFalling() ) currentFrame = 4;
	}
	
	// getScanArea
	//============
	/**
	 * @return the rectangular area around this {@code MapObject} to scan for other objects.
	 */
	public Rectangle getScanArea()
	{
		int size = Map.CELL_SIZE * 6;
		
		scanArea.x      = bounds.x      - size / 2f;
		scanArea.y      = bounds.y      - size / 2f;
		scanArea.width  = bounds.width  + size;
		scanArea.height = bounds.height + size;
		
		return ( scanArea );
	}
	
	// initAgility
	//============
	@Override
	public void initAgility()
	{
		// Super
		//======
		super.initAgility();
		
		// Velocity
		//=========
		agility.getVelocity().setAcceleration( 3f );
		agility.getVelocity().setDeceleration( 3f );
		agility.getVelocity().setMaxPositive( 100f );
		agility.getVelocity().setMaxNegative( 100f );
	}
}
