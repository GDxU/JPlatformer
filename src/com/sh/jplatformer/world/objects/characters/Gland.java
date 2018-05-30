package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Gland extends MapObject
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
	
	// Audio
	//======
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_gland1,
	                                                        Resources.WORLD.sound_character_gland2,
	                                                        Resources.WORLD.sound_character_gland3,
	                                                        Resources.WORLD.sound_character_gland4 };
	
	// Constructor
	//============
	public Gland( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Gland";
		bounds.width    = 32f;
		bounds.height   = 31f;
		isBlockingSpace = false;
		ignoreGravity   = true;
		
		// Routine
		//========
		routine = Routine.NONE;
		
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
		// Get map
		//========
		Map map = worldController.getMap();
		
		// Init first acid spit
		//=====================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + Randomizer.getLong( 4000L, 5500L );
		}
		
		// Check timer + spit acid
		//========================
		if ( map.isBlocked( bounds.x + bounds.width / 2f,
		                    bounds.y + bounds.height + 1f ) == true &&
		                    routineTimer < System.currentTimeMillis() )
		{
			// Add acid drop
			//==============
			routine = Routine.CONTRACTING;
			routineTimer = System.currentTimeMillis() + Randomizer.getLong( 4000L, 5500L );
			worldController.addMapObject( new AcidDrop( worldController ),
			                              bounds.x + bounds.width / 2f,
			                              bounds.y + bounds.height / 2f,
			                              true );
			
			// Play sound
			//===========
			worldController.getWorldAudio().addSound( sounds[Randomizer.getInt( 0, sounds.length - 1 )], this );
		}
		
		// Move up when not docked
		//========================
		ignoreGravity = true;
		agility.jump();
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		worldController.getPlayer().setAlive( false );
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
				frameTimer = System.currentTimeMillis() + 20L;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame < 0 ) currentFrame = 0;
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
				frameTimer = System.currentTimeMillis() + 20L;
				currentFrame--;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 ) currentFrame = 7;
				if ( currentFrame < 0 ) routine = Routine.NONE;
			}
		}
		
		// While resting
		//==============
		if ( routine == Routine.NONE )
		{
			currentFrame = 0;
		}
	}
}
