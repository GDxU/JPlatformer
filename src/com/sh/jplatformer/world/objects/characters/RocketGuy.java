package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class RocketGuy extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public RocketGuy( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Rocket Guy";
		bounds.width    = 48f;
		bounds.height   = 64f;
		isBlockingSpace = false;
		ignoreGravity   = true;
		
		// Init frames
		//============
		this.initFrames();
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
		// Floating animation
		//===================
		if ( frameTimer < System.currentTimeMillis() )
		{
			frameTimer = System.currentTimeMillis() + 40L;
			currentFrame++;
			
			if ( currentFrame > 7 )
			{
				currentFrame = 0;
			}
		}
	}
}
