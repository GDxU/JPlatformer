package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Platform1 extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		OFF, ON, TRANSITION
	}
	private Routine routine;
	
	// Constructor
	//============
	public Platform1( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name             = "Platform #1";
		bounds.width     = 64f;
		bounds.height    = 64f;
		ignoreGravity    = true;
		isPowerSupported = true;
		
		// Routine
		//========
		routine = Routine.OFF;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Init routine
		//=============
		if ( routine == null )
		{
			routine = Routine.OFF;
		}
		
		// Act when enabled
		//=================
		if ( isPowerOn == true )
		{
			// Update modes
			//=============
			if ( routineTimer < System.currentTimeMillis() )
			{
				switch ( routine )
				{
					case OFF:
					{
						// Turn to "on"
						//=============
						routine = Routine.ON;
						routineTimer = System.currentTimeMillis() + 5000L;
						break;
					}
					case ON:
					{
						// Turn to "transition"
						//=====================
						routine = Routine.TRANSITION;
						routineTimer = System.currentTimeMillis() + 3000L;
						break;
					}
					case TRANSITION:
					{
						// Turn to "off"
						//==============
						routine = Routine.OFF;
						routineTimer = System.currentTimeMillis() + 5000L;
						break;
					}
				}
			}
		}
		else
		{
			routine = Routine.OFF;
		}
		
		// Update space blocking
		//======================
		isBlockingSpace = !( routine == Routine.OFF );
	}
	
	// updateFrame
	//============
	@Override
	protected void updateAlpha()
	{
		// When on
		//========
		if ( routine == Routine.ON )
		{
			alpha += WorldController.getDelta( 2f );
			if ( alpha > 1f ) alpha = 1f;
		}
		
		// While transitioning
		//====================
		if ( routine == Routine.TRANSITION )
		{
			alpha -= WorldController.getDelta( 1f );
			if ( alpha < 0.75f ) alpha = 0.75f;
		}
		
		// When off
		//=========
		if ( routine == Routine.OFF )
		{
			alpha -= WorldController.getDelta( 1f );
			if ( alpha < 0.25f ) alpha = 0.25f;
		}
		
		// When out of power
		//==================
		if ( isPowerOn == false )
		{
			alpha = 0.04f;
		}
	}
}