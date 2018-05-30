package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.agility.Agility.Direction;
import com.sh.jplatformer.world.map.collision.CollisionHelper;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Skater extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		ROAMING, RESTING
	}
	private Routine routine;
	private Direction direction;
	
	// Constructor
	//============
	public Skater( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Skater";
		bounds.width    = 48f;
		bounds.height   = 64f;
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
		// Move on platform
		//=================
		if ( routine == Routine.ROAMING )
		{
			// Check timer + update mode
			//==========================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routine = Routine.RESTING;
				routineTimer = System.currentTimeMillis() + 4000L;
				agility.getVelocity().reset();
			}
			
			// Move while on ground
			//=====================
			if ( isOnGround() == true && CollisionHelper.isOnCell( this ) )
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
		else
		{
			// Check timer + update rest mode
			//===============================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routine = Routine.ROAMING;
				routineTimer = System.currentTimeMillis() + Randomizer.getLong( 4000L, 6000L );
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
		
		// While resting
		//==============
		if ( routine == Routine.RESTING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = Randomizer.getInt( 3, 5 );
				frameTimer = System.currentTimeMillis() + 300L;
			}
		}
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
		agility.getVelocity().setAcceleration( 4f );
		agility.getVelocity().setDeceleration( 4f );
		agility.getVelocity().setMaxPositive( 120f );
		agility.getVelocity().setMaxNegative( 120f );
	}
}
