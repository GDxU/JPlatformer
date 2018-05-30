package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class TreadmillRight extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public TreadmillRight( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name             = "Treadmill (right)";
		bounds.width     = 64f;
		bounds.height    = 64f;
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
		// Move objects on top
		//====================
		if ( isPowerOn == true )
		{
			for ( MapObject o : surroundingObjects )
			{
				// Check bounds
				//=============
				if ( o.isOnGround() == true && o.getBounds().y == bounds.y + bounds.height )
				{
					o.getAgility().addRepulsion( 3f, 0f, 7f );
				}
			}
		}
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// When on
		//========
		if ( isPowerOn == true )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 50L;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 ) currentFrame = 2;
			}
		}
		
		// When off
		//=========
		else
		{
			currentFrame = 1;
		}
	}
}