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

public class AngryBall extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		ROAMING, JUMPING
	}
	private Routine routine;
	private Direction direction;
	
	// Constructor
	//============
	public AngryBall( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Angry Ball";
		bounds.width    = 52f;
		bounds.height   = 52f;
		isBlockingSpace = false;
		
		// Routine
		//========
		routine = Routine.JUMPING;
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
			routine = Routine.JUMPING;
		}
		
		// Move on platform
		//=================
		if ( routine == Routine.ROAMING )
		{
			// Update timer + update mode
			//===========================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routine = Routine.JUMPING;
				routineTimer = System.currentTimeMillis() + 4000L;
				agility.getVelocity().reset();
			}
			
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
		
		// While jumping
		//==============
		if ( routine == Routine.JUMPING )
		{
			// Update timer + update mode
			//===========================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routine = Routine.ROAMING;
				routineTimer = System.currentTimeMillis() + Randomizer.getLong( 3000L, 5000L );
			}
			
			// Jump
			//=====
			if ( isOnGround() == true )
			{
				agility.jump();
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
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 20L;
				
				if ( agility.getDirection() == Direction.POSITIVE ) currentFrame++;
				if ( agility.getDirection() == Direction.NEGATIVE ) currentFrame--;
				
				// Limit frames
				//=============
				if ( currentFrame < 2 ) currentFrame = 7;
				if ( currentFrame > 7 ) currentFrame = 2;
			}
		}

		// While jumping
		//==============
		if ( routine == Routine.JUMPING )
		{
			if ( agility.isJumping() ) currentFrame = 0;
			if ( agility.isFalling() ) currentFrame = 1;
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
		
		// Jumping
		//========
		agility.setMinJumpHeight( 220f );
		agility.setMaxJumpHeight( 220f );
	}
}
