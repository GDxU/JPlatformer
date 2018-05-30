package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.agility.Agility.Direction;
import com.sh.jplatformer.world.map.collision.CollisionHelper;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Snail extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private Direction direction;
	
	// Constructor
	//============
	public Snail( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Snail";
		bounds.width    = 62f;
		bounds.height   = 54f;
		isBlockingSpace = false;
		
		// Routine
		//========
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
		// Update frames
		//==============
		if ( agility.getDirection() == Direction.POSITIVE ) currentFrame = 0;
		if ( agility.getDirection() == Direction.NEGATIVE ) currentFrame = 1;
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
