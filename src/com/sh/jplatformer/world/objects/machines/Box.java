package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Box extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;

	// Constructor
	//============
	public Box( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name          = "Box";
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
}