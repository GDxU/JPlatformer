package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.agility.Agility.Direction;
import com.sh.jplatformer.world.map.collision.Collision.CollisionType;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Spitter extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		ROAMING, SPITTING
	}
	private Routine routine;
	private Direction direction;
	
	// Audio
	//======
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_spitter1,
	                                                        Resources.WORLD.sound_character_spitter2,
	                                                        Resources.WORLD.sound_character_spitter3 };
	
	// Constructor
	//============
	public Spitter( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Spitter";
		bounds.width    = 60f;
		bounds.height   = 60f;
		isBlockingSpace = false;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_TOP;
		
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
		
		// Init first jump
		//================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + Randomizer.getLong( 3500L, 5000L );
		}
		
		// Get map
		//========
		Map map = worldController.getMap();
		
		// Move underneath platform
		//=========================
		if ( map.isBlocked( bounds.x + bounds.width / 2f,
		                    bounds.y + bounds.height + 1f ) == true )
		{
			// Check timer + update mode
			//==========================
			if ( routineTimer < System.currentTimeMillis() )
			{
				// Turn to spitting mode
				//======================
				if ( routine == Routine.ROAMING )
				{
					// Spit acid
					//==========
					routine = Routine.SPITTING;
					routineTimer = System.currentTimeMillis() + 200L;
					worldController.addMapObject( new AcidDrop( worldController ),
					                              bounds.x + bounds.width / 2f,
					                              bounds.y + bounds.height / 2f,
					                              true );
					// Play sound
					//===========
					worldController.getWorldAudio().addSound( sounds[Randomizer.getInt( 0, sounds.length - 1 )], this );
				}
				
				// Turn to roaming mode
				//=====================
				else if ( routine == Routine.SPITTING )
				{
					routine = Routine.ROAMING;
					routineTimer = System.currentTimeMillis() + Randomizer.getLong( 3500L, 5000L );
				}				
			}
			
			// Move east
			//==========
			if ( direction == Direction.POSITIVE )
			{
				agility.accelerate( false );
				
				if ( !map.isBlocked( bounds.x + bounds.width, bounds.y + bounds.height + Map.CELL_SIZE / 2f ) ||
				     lastCollision.x == CollisionType.EAST )
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
				
				if ( !map.isBlocked( bounds.x, bounds.y + bounds.height + Map.CELL_SIZE / 2f ) ||
				     lastCollision.x == CollisionType.WEST )
				{
					direction = Direction.POSITIVE;
					agility.getVelocity().reset();
				}
			}
		}
		
		// Move up
		//========
		ignoreGravity = true;
		agility.jump();
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
		
		// While spitting
		//===============
		if ( routine == Routine.SPITTING )
		{
			currentFrame = 2;
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
		agility.getVelocity().setAcceleration( 2f );
		agility.getVelocity().setDeceleration( 2f );
		agility.getVelocity().setMaxPositive( 80f );
		agility.getVelocity().setMaxNegative( 80f );
	}
}
