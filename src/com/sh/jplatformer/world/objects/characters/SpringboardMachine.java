package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.agility.Agility.Direction;
import com.sh.jplatformer.world.map.collision.CollisionHelper;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class SpringboardMachine extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Animation
	//==========
	private enum Routine
	{
		ROAMING, CONTRACTING, EXPANDING;
	}
	
	// Routine
	//========
	private Routine routine;
	private Direction direction;
		
	// Constructor
	//============
	public SpringboardMachine( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Springboard Machine";
		bounds.width    = 64f;
		bounds.height   = 64f;
		isBlockingSpace = false;
		
		// Routine
		//========
		direction = Direction.POSITIVE;
		routine = Routine.ROAMING;
		
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
		// Invoke jumps
		//=============
		for ( MapObject o : surroundingObjects )
		{
			// Check y-position
			//=================
			if ( o.getBounds().y >= bounds.y + bounds.height / 2f &&
			     o.getBounds().y <= bounds.y + bounds.height + 4f )
			{
				// Check x-position
				//=================
				if ( o.getBounds().x + o.getBounds().width > bounds.x  &&
				     o.getBounds().x < bounds.x + bounds.width )
				{
					// Check state
					//============
					if ( o.getAgility().isFalling() )
					{
						// Invoke jump
						//============
						o.getBounds().y = bounds.y + bounds.height;
						o.getAgility().resetJump();
						o.getAgility().jump();
						o.getAgility().extendJump( 240f, true );
						
						routine = Routine.CONTRACTING;
						
						// Play sound
						//===========
						worldController.getWorldAudio().addSound( Resources.WORLD.sound_character_springboard, this );
					}
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
		// While contracting
		//==================
		if ( routine == Routine.CONTRACTING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 14L;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame < 3 ) currentFrame = 3;
				if ( currentFrame > 7 )
				{
					currentFrame = 7;
					routine = Routine.EXPANDING;
				}
			}
		}

		// While expanding
		//================
		if ( routine == Routine.EXPANDING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 17L;
				currentFrame--;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 ) currentFrame = 7;
				if ( currentFrame < 3 ) routine = Routine.ROAMING;
			}
		}
		
		// While roaming
		//==============
		if ( routine == Routine.ROAMING )
		{
			if ( agility.getDirection() == Direction.POSITIVE ) currentFrame = 2;
			if ( agility.getDirection() == Direction.NEGATIVE ) currentFrame = 1;
			if ( agility.isFalling() ) currentFrame = 0;
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
		agility.getVelocity().setAcceleration( 5f );
		agility.getVelocity().setDeceleration( 5f );
		agility.getVelocity().setMaxPositive( 150f );
		agility.getVelocity().setMaxNegative( 150f );
	}
}
