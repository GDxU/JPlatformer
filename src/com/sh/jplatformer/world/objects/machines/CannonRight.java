package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class CannonRight extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public CannonRight( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name             = "Cannon (right)";
		bounds.width     = 40f;
		bounds.height    = 38f;
		ignoreGravity    = true;
		isPowerSupported = true;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_LEFT;
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
			CannonBall cannonBall = new CannonBall( worldController, CannonBall.Routine.EAST );
			worldController.addMapObject( cannonBall,
			                              bounds.x + bounds.width + cannonBall.getBounds().width / 2f,
			                              bounds.y + bounds.height / 2f,
			                              true );
		}
	}
}