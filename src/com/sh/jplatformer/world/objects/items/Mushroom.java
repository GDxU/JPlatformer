package com.sh.jplatformer.world.objects.items;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} item.
 * @author Stefan Hösemann
 */

public class Mushroom extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	public Mushroom( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Mushroom";
		score           = 500;
		bounds.width    = 44;
		bounds.height   = 40f;
		isBlockingSpace = false;
		ignoreGravity   = true;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_CENTER;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		// Set alive + add score
		//======================
		this.setAlive( false );
		worldController.addScore( score );
		worldController.addPopup( score + "",
		                          bounds.x + bounds.width  / 2f,
		                          bounds.y + bounds.height / 2f );
		
		// Play sound
		//===========
		worldController.getWorldAudio().addCollectSound();
	}
}