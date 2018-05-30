package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Platform2 extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public Platform2( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name             = "Platform #2";
		bounds.width     = 64f;
		bounds.height    = 52f;
		ignoreGravity    = true;
		isPowerSupported = true;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_TOP;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Set collision
		//==============
		isBlockingSpace = isPowerOn;
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// While on
		//=========
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
				if ( currentFrame > 6 ) currentFrame = 0;
			}
		}
		// When off
		//=========
		else
		{
			currentFrame = 7;
		}
	}
}