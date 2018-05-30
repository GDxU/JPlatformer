package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class CannonLeft extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public CannonLeft( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name             = "Cannon (left)";
		bounds.width     = 40f;
		bounds.height    = 38f;
		ignoreGravity    = true;
		isPowerSupported = true;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_RIGHT;
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
		// Fire cannon ball
		//=================
		if ( isPowerOn == true && routineTimer < System.currentTimeMillis() )
		{
			// Reset timer
			//============
			routineTimer = System.currentTimeMillis() + 4000L;
			
			// Create cannon ball
			//===================
			CannonBall cannonBall = new CannonBall( worldController, CannonBall.Routine.WEST );
			worldController.addMapObject( cannonBall,
			                              bounds.x - cannonBall.getBounds().width / 2f,
			                              bounds.y + bounds.height / 2f,
			                              true );
		}
	}
}