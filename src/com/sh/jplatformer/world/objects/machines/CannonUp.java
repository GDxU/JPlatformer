package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class CannonUp extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public CannonUp( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name             = "Cannon (up)";
		bounds.width     = 38f;
		bounds.height    = 40f;
		ignoreGravity    = true;
		isPowerSupported = true;
		
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
			CannonBall cannonBall = new CannonBall( worldController, CannonBall.Routine.UP );
			worldController.addMapObject( cannonBall,
			                              bounds.x + bounds.width / 2f,
			                              bounds.y + bounds.height + cannonBall.getBounds().height / 2f,
			                              true );
		}
	}
}