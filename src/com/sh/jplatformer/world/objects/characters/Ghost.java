package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.agility.Agility.Direction;
import com.sh.jplatformer.world.map.collision.CollisionHelper;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Ghost extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private Direction direction;
	
	// Audio
	//======
	private long audioTimer;
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_ghost1,
	                                                        Resources.WORLD.sound_character_ghost2,
	                                                        Resources.WORLD.sound_character_ghost3 };
	
	// Constructor
	//============
	public Ghost( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Ghost";
		bounds.width    = 56f;
		bounds.height   = 60f;
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
		// Play sounds randomly
		//=====================
		if ( audioTimer < System.currentTimeMillis() )
		{
			audioTimer = System.currentTimeMillis() + Randomizer.getLong( 7500L, 15000L );
			worldController.getWorldAudio().addSound( sounds[Randomizer.getInt( 0, sounds.length - 1 )], this );
		}
		
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
		agility.getVelocity().setMaxPositive( 120f );
		agility.getVelocity().setMaxNegative( 120f );
	}
}
