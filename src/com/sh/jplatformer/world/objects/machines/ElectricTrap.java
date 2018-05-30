package com.sh.jplatformer.world.objects.machines;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class ElectricTrap extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		OFF, ON
	}
	private Routine routine;
	
	// Audio
	//======
	private long audioTimer;
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_machine_electricTrap1,
	                                                        Resources.WORLD.sound_machine_electricTrap2,
	                                                        Resources.WORLD.sound_machine_electricTrap3 };
	
	// Constructor
	//============
	public ElectricTrap( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name             = "Electric Trap";
		bounds.width     = 60f;
		bounds.height    = 54f;
		isBlockingSpace  = false;
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
		// Init first energy
		//==================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + 4000L;
		}
		
		// Play sounds randomly
		//=====================
		if ( isPowerOn == true && routine == Routine.ON )
		{
			if ( audioTimer < System.currentTimeMillis() )
			{
				audioTimer = System.currentTimeMillis() + Randomizer.getLong( 2000L, 8000L );
				worldController.getWorldAudio().addSound( sounds[Randomizer.getInt( 0, sounds.length - 1 )], this );
			}
		}
		
		// Switch routine mode
		//====================
		if ( isPowerOn == true && routineTimer < System.currentTimeMillis() )
		{
			// Reset timer
			//============
			routineTimer = System.currentTimeMillis() + 4000L;
			
			// Set routine mode
			//=================
			if ( routine == Routine.ON  )
			{
				routine = Routine.OFF;
			}
			else
			{
				routine = Routine.ON;
			}
		}
		
		// Turn off
		//=========
		if ( isPowerOn == false )
		{
			routine = Routine.OFF;
		}
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		if ( routine == Routine.ON )
		{
			worldController.getPlayer().setAlive( false );
		}
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// When on
		//========
		if ( routine == Routine.ON )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 50L;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 ) currentFrame = 1;
			}
		}
		
		// When off
		//=========
		if ( routine == Routine.OFF )
		{
			currentFrame = 0;
		}
	}
}