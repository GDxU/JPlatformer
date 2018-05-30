package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Jumper extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public Jumper( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Jumper";
		bounds.width    = 50f;
		bounds.height   = 50f;
		isBlockingSpace = false;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{		
		// Jump
		//=====
		if ( isOnGround() == true )
		{
			agility.jump();
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
		if ( agility.isJumping() ) currentFrame = 0;
		if ( agility.isFalling() ) currentFrame = 1;
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
