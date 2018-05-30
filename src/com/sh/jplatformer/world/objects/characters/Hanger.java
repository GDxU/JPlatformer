package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Hanger extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		ON_GROUND, HANGING, FALLING
	}
	private Routine routine;
	
	// Audio
	//======
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_cartonGuy1,
	                                                        Resources.WORLD.sound_character_cartonGuy2,
	                                                        Resources.WORLD.sound_character_cartonGuy3,
	                                                        Resources.WORLD.sound_character_cartonGuy4 };
	
	// Constructor
	//============
	public Hanger( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name          = "Hanger";
		bounds.width  = 58f;
		bounds.height = 64f;
		
		// Routine
		//========
		routine = Routine.ON_GROUND;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Get map
		//========
		Map map = worldController.getMap();
		
		// Init first jump
		//================
		if ( routineTimer == 0L || routine == null )
		{
			routine = Routine.ON_GROUND;
			routineTimer = System.currentTimeMillis() + Randomizer.getLong( 4500L, 6000L );
		}
		
		// Toggle blocking behavior
		//=========================
		if ( isOnGround() || routine == Routine.HANGING )
		{
			isBlockingSpace = true;
		}
		else if ( worldController.getPlayer() != null )  
		{
			// Set player
			//===========
			MapObject p = worldController.getPlayer();
			
			// Unblock if x overlaps
			//======================
			if ( p.getBounds().overlaps( this.scanArea ) )
			{
				if ( p.getBounds().x + p.getBounds().width <= bounds.x ||
				     p.getBounds().x >= bounds.x + bounds.width )
				{
					isBlockingSpace = true;
				}
				else
				{
					isBlockingSpace = false;
				}
			}
		}
		
		// While on ground / jumping
		//==========================
		if ( routine == Routine.ON_GROUND )
		{
			// Init jump when ready
			//=====================
			if ( routineTimer < System.currentTimeMillis() )
			{
				if ( isOnGround() )
				{
					// Jump
					//=====
					agility.jump();
					routineTimer = System.currentTimeMillis() + Randomizer.getLong( 4500L, 6000L );
					
					// Play sound
					//===========
					worldController.getWorldAudio().addSound( sounds[Randomizer.getInt( 0, sounds.length - 1 )], this );
				}
			}
			
			// Check roof collision
			//=====================
			if ( map.isBlocked( bounds.x + bounds.width / 2f, bounds.y + bounds.height + 1f ) &&
			     isOnGround() == false )
			{
				routine = Routine.HANGING;
				routineTimer = System.currentTimeMillis() + 5000L;
			}
		}
		
		// While hanging
		//==============
		if ( routine == Routine.HANGING )
		{
			// Initialize fall
			//================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routine = Routine.FALLING;
				ignoreGravity = false;
				worldController.getWorldAudio().addSound( sounds[Randomizer.getInt( 0, sounds.length - 1 )], this );
			}
			else
			{
				// Check roof tile
				//================
				if ( map.isBlocked( bounds.x + bounds.width / 2f,
				                    bounds.y + bounds.height + 1f ) == false )
				{
					routineTimer = 0L;
				}
				else
				{
					// Stick to roof tile
					//===================
					ignoreGravity = true;
					agility.jump();
				}
			}
		}
		
		// While falling
		//==============
		if ( routine == Routine.FALLING )
		{
			if ( isOnGround() == true )
			{	
				routine = Routine.ON_GROUND;
				routineTimer = System.currentTimeMillis() + Randomizer.getLong( 4500L, 6000L );
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
		// While on ground
		//================
		if ( routine == Routine.ON_GROUND )
		{
			// Looking around
			//===============
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = Randomizer.getInt( 0, 2 );
				frameTimer = System.currentTimeMillis() + 500L;
			}
			
			// Before jumping
			//===============
			if ( routineTimer < System.currentTimeMillis() + 1000L )
			{
				currentFrame = 3;
			}
		}
		
		// While not on ground
		//====================
		if ( agility.isJumping() ) { currentFrame = 6; frameTimer = 0; };
		if ( agility.isFalling() ) { currentFrame = 7; frameTimer = 0; };
		
		// While hanging
		//==============
		if ( routine == Routine.HANGING )
		{
			// Looking around
			//===============
			currentFrame = 4;
			
			// Before falling
			//===============
			if ( routineTimer < System.currentTimeMillis() + 1000L )
			{
				currentFrame = 5;
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
		
		// Jumping
		//========
		agility.setMinJumpHeight( 220f );
		agility.setMaxJumpHeight( 220f );
	}
}
