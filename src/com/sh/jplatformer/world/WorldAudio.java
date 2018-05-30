package com.sh.jplatformer.world;

import java.io.Serializable;
import java.util.ArrayList;
import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code WorldAudio} class is responsible for the sound output of the world.
 * @author Stefan Hösemann
 */

public class WorldAudio implements Serializable
{
	// Constants
	//==========
	private static final long serialVersionUID = 1L;
	
	// Fields
	//=======
	private WorldController worldController;
	private ArrayList<WorldSound> objectSounds;
	private boolean playCollectSound;
	
	// Constructor
	//============
	public WorldAudio( WorldController worldController )
	{
		this.worldController = worldController;
		this.objectSounds = new ArrayList<WorldSound>();
	}
	
	// play
	//=====
	/**
	 * Plays all world sounds.
	 */
	public void play()
	{
		playEnvironmentSound();
		playWaterSound();
		playObjectSounds();
	}
	
	// playEnvironmentSound
	//=====================
	private void playEnvironmentSound()
	{
		// Load sound file (if not loaded yet)
		//====================================
		Resources.WORLD.loadEnvironmentSound( worldController.getMap().getEnvironmentSoundFile() );
		
		// Play environment sound
		//=======================
		if ( Resources.WORLD.sound_environment != null )
		{
			if ( Resources.WORLD.sound_environment.isPlaying() == false )
			{
				Resources.WORLD.sound_environment.play();
			}
		}
	}
	
	// playWaterSound
	//===============
	private void playWaterSound()
	{
		// Temporary values
		//=================
		WorldCamera camera = worldController.getWorldCamera();
		float waterHeight  = worldController.getMap().getWaterHeight();
		float cameraY      = camera.position.y - camera.viewportHeight / 2f * camera.zoom;
		float volume       = ( 100f - ( cameraY - waterHeight ) ) / 100f;
		
		// Limit and set volume
		//=====================
		if ( volume      > 1.0f ) volume = 1.0f;
		if ( volume      < 0.0f ) volume = 0.0f;
		if ( waterHeight < 1.0f ) volume = 0.0f;
		Resources.WORLD.sound_water.setVolume( volume );
		
		// Play water sound
		//=================
		if ( Resources.WORLD.sound_water.isPlaying() == false )
		{
			Resources.WORLD.sound_water.play();
		}
	}
	
	// playObjectSounds
	//=================
	private void playObjectSounds()
	{
		// Play all added sounds
		//======================
		for ( WorldSound s : objectSounds )
		{
			// Calculate values
			//=================
			float p = calculatePan( s.mapObject );
			float v = calculateVolume( s.mapObject );
			
			// Play sound
			//===========
			if ( v > 0f )
			{
				long id = s.sound.play();
				
				s.sound.setPan( id, p, v );
			}
		}
		
		// Clear sound array
		//==================
		objectSounds.clear();
		
		// Play collect sound
		//===================
		if ( playCollectSound )
		{
			Resources.WORLD.sound_item_collect.play();
			playCollectSound = false;
		}
	}
	
	// pause
	//======
	/**
	 * Pauses all sounds.
	 */
	public void pause()
	{
		// Pause environment sound
		//========================
		if ( Resources.WORLD.sound_environment != null )
		{
			Resources.WORLD.sound_environment.pause();
		}
		
		// Pause water sound
		//==================
		Resources.WORLD.sound_water.pause();
	}
	
	// addSound
	//=========
	/**
	 * @param soundFile the sound to play.
	 * @param source the {@code MapObject} that is the sound source. It must have a valid
	 * {@code WorldController}.
	 */
	public void addSound( Sound soundFile, MapObject source )
	{
		objectSounds.add( new WorldSound( soundFile, source ) );
	}
	
	// addCollectSound
	//================
	/**
	 * Initiates a single "collect" sound playback for the current frame.
	 */
	public void addCollectSound()
	{
		playCollectSound = true;
	}
	
	// calculateVolume
	//================
	private float calculateVolume( MapObject source )
	{
		// Temporary values
		//=================
		float x            = source.getBounds().x + source.getBounds().width  / 2f;
		float y            = source.getBounds().y + source.getBounds().height / 2f;
		WorldCamera camera = source.getWorldController().getWorldCamera();
		
		// Calculate volumes
		//==================
		float vX = 1f - Math.abs( ( camera.position.x - x ) / ( camera.viewportWidth  / 2.0f * camera.zoom ) );
		float vY = 1f - Math.abs( ( camera.position.y - y ) / ( camera.viewportHeight / 1.5f * camera.zoom ) );
		
		if ( vX < 0f ) vX = 0f;
		if ( vY < 0f ) vY = 0f;
		
		// Mix and return volume
		//======================
		float volume = vX * vY * 2f * ( 1f / ( camera.zoom * camera.zoom ) );
		
		if ( volume > 1f ) volume = 1f;
		if ( volume < 0f ) volume = 0f;

		return ( volume );
	}
	
	// calculatePan
	//=============
	private float calculatePan( MapObject source )
	{
		// Temporary values
		//=================
		float x            = source.getBounds().x + source.getBounds().width / 2f;
		WorldCamera camera = source.getWorldController().getWorldCamera();
		
		// calculatePan
		//=============
		return ( ( camera.position.x - x ) / ( camera.viewportWidth / 2f * camera.zoom ) * - 1f );
	}
}

class WorldSound
{
	// Fields
	//=======
	public Sound sound;
	public MapObject mapObject;
	
	// Constructor
	//============
	public WorldSound( Sound soundFile, MapObject source )
	{
		sound = soundFile;
		mapObject = source;
	}
}
