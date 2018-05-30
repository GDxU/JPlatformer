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

public class Block extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Audio
	//======
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_block1,
	                                                        Resources.WORLD.sound_character_block2 };
	
	// Constructor
	//============
	public Block( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name          = "Block";
		bounds.width  = 64f;
		bounds.height = 64f;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		if ( agility.isFalling() )
		{
			worldController.getPlayer().setAlive( false );
		}
	}
		
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// While player on top
		//====================
		for ( MapObject o : surroundingObjects )
		{
			if ( o == worldController.getPlayer() && o.isOnGround() )
			{
				if ( o.getBounds().y == bounds.y + bounds.height &&
				     o.getBounds().x  < bounds.x + bounds.width &&
				     o.getBounds().x + o.getBounds().width > bounds.x )
				{
					// Play sound
					//===========
					if ( currentFrame != 4 )
					{
						worldController.getWorldAudio().addSound( sounds[Randomizer.getInt( 0, sounds.length - 1 )], this );
					}
					
					// Set frame
					//==========
					currentFrame = 4;
					
					return;
				}
			}
		}
		
		// Update frames
		//==============
		if ( frameTimer < System.currentTimeMillis() )
		{
			// Look around
			//============
			frameTimer = System.currentTimeMillis() + Randomizer.getLong( 1500L, 3000L );
			currentFrame = Randomizer.getInt( 0, 3 );
			
			// Blink frame
			//============
			if ( currentFrame == 3 )
			{
				frameTimer = System.currentTimeMillis() + 50L;
			}
		}
		
		// While falling / landing
		//========================
		if ( isOnGround() == false )
		{
			currentFrame = 5;
		}
		else if ( currentFrame == 5 )
		{
			// Reset timer
			//============
			currentFrame = 0;
			frameTimer = System.currentTimeMillis() + Randomizer.getLong( 1500L, 3000L );
		}
	}
}
