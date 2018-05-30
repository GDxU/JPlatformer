package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Springboard extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		NONE, CONTRACTING, EXPANDING
	}
	private Routine routine;
	
	// Constructor
	//============
	public Springboard( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Springboard";
		bounds.width    = 64f;
		bounds.height   = 64f;
		isBlockingSpace = false;
		
		// Routine
		//========
		routine = Routine.NONE;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Invoke jumps
		//=============
		for ( MapObject o : surroundingObjects )
		{
			// Check y-position
			//=================
			if ( o.getBounds().y >= bounds.y + bounds.height / 2f &&
			     o.getBounds().y <= bounds.y + bounds.height + 4f )
			{
				// Check x-position
				//=================
				if ( o.getBounds().x + o.getBounds().width > bounds.x  &&
				     o.getBounds().x < bounds.x + bounds.width )
				{
					// Check state
					//============
					if ( o.getAgility().isFalling() )
					{
						// Invoke jump
						//============
						o.getBounds().y = bounds.y + bounds.height;
						o.getAgility().resetJump();
						o.getAgility().jump();
						o.getAgility().extendJump( 400f, true );
						routine = Routine.CONTRACTING;
						
						// Play sound
						//===========
						worldController.getWorldAudio().addSound( Resources.WORLD.sound_machine_springboard, this );
					}
				}
			}
		}
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// While contracting
		//==================
		if ( routine == Routine.CONTRACTING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 14L;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 )
				{
					currentFrame = 7;
					routine = Routine.EXPANDING;
				}
			}
		}

		// While expanding
		//================
		if ( routine == Routine.EXPANDING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 14L;
				currentFrame--;
				
				// Limit frames
				//=============
				if ( currentFrame < 0 ) currentFrame = 0;
				if ( currentFrame < 1 ) routine = Routine.NONE;
			}
		}
	}
}