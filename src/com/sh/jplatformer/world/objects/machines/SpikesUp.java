package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class SpikesUp extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;

	// Constructor
	//============
	public SpikesUp( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name          = "Spikes (up)";
		bounds.width  = 50f;
		bounds.height = 31f;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
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
}