package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.collision.Collision.CollisionType;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class AcidDrop extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public AcidDrop( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Acid Drop";
		bounds.width    = 14f;
		bounds.height   = 22f;
		isBlockingSpace = false;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_CENTER;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		if ( lastCollision.y == CollisionType.BOTTOM )
		{
			this.setAlive( false );
		}
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		worldController.getPlayer().setAlive( false );
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
		agility.setMinJumpHeight( 8f );
		agility.setMaxJumpHeight( 8f );
	}
}