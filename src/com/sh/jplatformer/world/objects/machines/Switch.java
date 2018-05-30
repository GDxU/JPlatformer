package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Switch extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		NONE, INCREASING, DECREASING
	}
	private Routine routine;
	private int maxFrame;
	private int minFrame;

	// Constructor
	//============
	public Switch( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name             = "Switch";
		bounds.width     = 42f;
		bounds.height    = 64f;
		isBlockingSpace  = false;
		isPowerSupported = true;
		
		// Routine
		//========
		routine = Routine.NONE;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// use
	//====
	@Override
	public boolean onUse()
	{
		// Switch related power states
		//============================
		for ( MapObject o : worldController.getMapObjects() )
		{
			if ( o.getPowerId() == this.getPowerId() )
			{
				o.setPowerOn( !o.isPowerOn() );
			}
		}
		
		// Popup message
		//==============
		if ( isPowerOn == true )
		{
			worldController.getWorldAudio().addSound( Resources.WORLD.sound_machine_switch_on, this );
			worldController.addPopup( Lang.txt( "game_on" ),
			                          bounds.x + bounds.width  / 2f,
			                          bounds.y + bounds.height / 2f );
		}
		else
		{
			worldController.getWorldAudio().addSound( Resources.WORLD.sound_machine_switch_off, this );
			worldController.addPopup( Lang.txt( "game_off" ),
			                          bounds.x + bounds.width  / 2f,
			                          bounds.y + bounds.height / 2f );			
		}
		return ( true );
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// Init routine
		//=============
		if ( routine == null )
		{
			routine = Routine.INCREASING;
		}
		
		// Increase glow
		//==============
		if ( routine == Routine.INCREASING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 50L;
				currentFrame++;
				
				// Max / min frames
				//=================
				if ( isPowerOn == false )
				{
					maxFrame = 3;
					minFrame = 0;
				}
				else
				{
					maxFrame = 7;
					minFrame = 4;
				}
				
				// Limit frames
				//=============
				if ( currentFrame < minFrame ) currentFrame = minFrame;
				if ( currentFrame > maxFrame )
				{
					currentFrame = maxFrame;
					routine = Routine.DECREASING;
				}
			}
		}

		// Decrease glow
		//==============
		if ( routine == Routine.DECREASING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 50L;
				currentFrame--;
				
				// Max / min frames
				//=================
				if ( isPowerOn == false )
				{
					maxFrame = 3;
					minFrame = 0;
				}
				else
				{
					maxFrame = 7;
					minFrame = 4;
				}
				
				// Limit frames
				//=============
				if ( currentFrame > maxFrame ) currentFrame = maxFrame;
				if ( currentFrame < minFrame )
				{
					currentFrame = minFrame;
					frameTimer = System.currentTimeMillis() + 1000L;
					routine = Routine.NONE;
				}
			}
		}
		
		// Initiate glow animation
		//========================
		if ( routine == Routine.NONE )
		{
			// Set frames
			//===========
			if ( isPowerOn == false ) currentFrame = 0;
			if ( isPowerOn == true )  currentFrame = 4;
			
			// Init glow
			//==========
			if ( frameTimer < System.currentTimeMillis() )
			{
				routine = Routine.INCREASING;
			}
		}
	}
}