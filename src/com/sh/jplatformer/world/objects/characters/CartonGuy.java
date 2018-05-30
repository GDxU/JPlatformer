package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class CartonGuy extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Audio
	//======
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_cartonGuy1,
	                                                        Resources.WORLD.sound_character_cartonGuy2,
	                                                        Resources.WORLD.sound_character_cartonGuy3,
	                                                        Resources.WORLD.sound_character_cartonGuy4 };
	
	// Constructor
	//============
	public CartonGuy( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name          = "Carton Guy";
		bounds.width  = 54f;
		bounds.height = 63f;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Init first jump
		//================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + Randomizer.getLong( 4500L, 6000L );
		}
		
		// Jump from time to time
		//=======================
		if ( routineTimer < System.currentTimeMillis() && isOnGround() == true )
		{
			// Jump
			//=====
			agility.jump();
			routineTimer = System.currentTimeMillis() + Randomizer.getLong( 4500L, 6000L );
			
			// Play sound
			//===========
			worldController.getWorldAudio().addSound( sounds[Randomizer.getInt( 0, sounds.length - 1 )], this );
		}
		
		// Toggle blocking behavior
		//=========================
		if ( worldController.getPlayer() != null )
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
		if ( isOnGround() )
		{
			// Looking around
			//===============
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = Randomizer.getInt( 0, 2 );
				frameTimer = System.currentTimeMillis() + 400L;
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
		if ( agility.isJumping() ) { currentFrame = 4; frameTimer = 0; };
		if ( agility.isFalling() ) { currentFrame = 5; frameTimer = 0; };
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
