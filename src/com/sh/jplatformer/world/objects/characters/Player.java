package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.agility.Agility.Direction;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Player extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		ALIVE, DEATH_INIT, DEATH
	}
	private Routine routine;
	private Direction direction;
	private boolean justLanded;
	
	// Death animation
	//================
	private Vector2 deathAnimPos;
	private float deathAnimForce;
	private long deathAnimTimer;
	
	// Audio
	//======
	private long audioTimer;
	private int lastIdx;
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_player_step1,
	                                                        Resources.WORLD.sound_character_player_step2,
	                                                        Resources.WORLD.sound_character_player_step3,
	                                                        Resources.WORLD.sound_character_player_step4,
	                                                        Resources.WORLD.sound_character_player_step5,
	                                                        Resources.WORLD.sound_character_player_step6 };
	
	// Constructor
	//============
	public Player( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Player";
		bounds.width    = 42f;
		bounds.height   = 84f;
		isBlockingSpace = false;
		
		// Routine
		//========
		routine = Routine.ALIVE;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_BOTTOM;
		
		// Init frames
		//============
		frameSize = 96;
		this.initFrames();
	}
	
	// draw
	//=====
	@Override
	public void draw( SpriteBatch batch )
	{
		if ( routine == Routine.DEATH )
		{
			frames[currentFrame].setPosition( deathAnimPos.x, deathAnimPos.y );
			frames[currentFrame].draw( batch );
		}
		else
		{
			super.draw( batch );
		}
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
			routine = Routine.ALIVE;
		}
		
		// Handle death sequence
		//======================
		if ( routine == Routine.DEATH_INIT )
		{
			initDeathSequence();
		}
		else if ( routine == Routine.DEATH )
		{
			playDeathSequence();
		}
		
		// Play step sounds
		//=================
		this.playStepSounds();
	}
	
	// initDeathSequence
	//==================
	private void initDeathSequence()
	{
		// Set values
		//===========
		deathAnimTimer = System.currentTimeMillis() + 1500L;
		ignoreGravity  = true;
		routine        = Routine.DEATH;
		
		// Animation position
		//===================
		deathAnimForce = 0f; 
		deathAnimPos   = new Vector2( frames[currentFrame].getX(),
		                              frames[currentFrame].getY() );
		
		// Play sound
		//===========
		Resources.UI.sound_game_lose.play();
	}
	
	// playDeathSequence
	//==================
	private void playDeathSequence()
	{
		// Stop player movement
		//=====================
		agility.resetJump();
		agility.getVelocity().reset();
		
		// Play sequence
		//==============
		if ( deathAnimTimer <= System.currentTimeMillis() )
		{
			// Move sprite
			//============
			deathAnimForce += WorldController.getDelta( 10f );
			deathAnimPos.y -= deathAnimForce;
			
			// Kill player
			//============
			if ( deathAnimTimer <= System.currentTimeMillis() - 1500L )
			{
				super.setAlive( false );
			}
		}
	}
	
	// playStepSounds
	//===============
	private void playStepSounds()
	{
		// Reset landed flag
		//==================
		if ( !isOnGround() )
		{
			justLanded = false;
		}
		
		// Step sound condition
		//=====================
		boolean playSound = audioTimer < System.currentTimeMillis() &&
		                    direction != Direction.NONE &&
		                    isOnGround();
		
		// Play step sounds
		//=================
		if ( playSound || ( !justLanded && isOnGround() ) )
		{
			// Resets
			//=======
			justLanded = true;
			audioTimer = System.currentTimeMillis() + 200L;
			
			// Pick and play audio file
			//=========================
			int newIdx = lastIdx;
			while ( newIdx == lastIdx )
			{
				newIdx = Randomizer.getInt( 0, sounds.length - 1 );
			}
			worldController.getWorldAudio().addSound( sounds[newIdx], this );
			lastIdx = newIdx;
		}
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// While dying
		//============
		if ( routine == Routine.DEATH || routine == Routine.DEATH_INIT )
		{
			currentFrame = 3;
			return;
		}
		
		// While on ground
		//================
		if ( isOnGround() )
		{
			// Running
			//========
			if ( direction != Direction.NONE )
			{
				// Update timer + frame
				//=====================
				if ( frameTimer < System.currentTimeMillis() )
				{
					frameTimer = System.currentTimeMillis() + 30L;
					currentFrame++;
				}
				
				// Limit east frames
				//==================
				if ( direction == Direction.POSITIVE )
				{
					if ( currentFrame > 21 || currentFrame < 8 )
					{
						frameTimer = System.currentTimeMillis() + 30L;
						currentFrame = 8;
					}
				}

				// Limit west frames
				//==================
				else if ( direction == Direction.NEGATIVE )
				{
					if ( currentFrame > 35 || currentFrame < 22 )
					{
						frameTimer = System.currentTimeMillis() + 30L;
						currentFrame = 22;
					}
				}
			}
			
			// Resting
			//========
			else
			{
				// Check current frame
				//====================
				if ( currentFrame > 2 )
				{
					currentFrame = 0;
				}
				
				// Update frame
				//=============
				if ( frameTimer < System.currentTimeMillis() )
				{
					currentFrame = Randomizer.getInt( 0, 2 );
					frameTimer = System.currentTimeMillis() + Randomizer.getLong( 250L, 1500L );
				}
			}
		}
		
		// While in air
		//=============
		else
		{
			// Falling
			//========
			if ( agility.isFalling() )
			{
				if ( direction == Direction.NEGATIVE )
				{
					currentFrame = 7;
				}
				else
				{
					currentFrame = 5;
				}
			}
			
			// Jumping
			//========
			if ( agility.isJumping() )
			{
				if ( direction == Direction.NEGATIVE )
				{
					currentFrame = 6;
				}
				else
				{
					currentFrame = 4;
				}
			}
		}
	}
	
	// setAlive
	//=========
	/**
	 * Overrides the super method in order to initiate a death sequence if {@code isAlive} is set
	 * {@code false}.
	 */
	@Override
	public void setAlive( boolean isAlive )
	{
		// Init dead sequence when live
		//=============================
		if ( worldController.isLive() && routine == Routine.ALIVE )
		{
			routine = Routine.DEATH_INIT;
			worldController.setWorldState( WorldController.STATE_PLAYER_DIES );
		}
		
		// Skip sequence in editor
		//========================
		else if ( !worldController.isLive() )
		{
			super.setAlive( isAlive );
		}
	}
	
	// initAgility
	//============
	public void initAgility()
	{
		// Super
		//======
		super.initAgility();
		
		// Velocity
		//=========
		agility.getVelocity().setAcceleration( 8f );
		agility.getVelocity().setDeceleration( 10f );
		agility.getVelocity().setMaxPositive( 260f );
		agility.getVelocity().setMaxNegative( 260f );
		
		// Jumping
		//========
		agility.setMinJumpHeight( 76f );
		agility.setMaxJumpHeight( 160f );
	}
	
	// setRunningAnimDirection
	//========================
	public void setRunningAnimDirection( Direction direction )
	{
		if ( isOnGround() )
		{
			this.direction = direction;
		}
	}
	
	// isDeathSequencePlaying
	//=======================
	public boolean isDeathSequencePlaying()
	{
		return ( routine == Routine.DEATH_INIT || routine == Routine.DEATH );
	}
}
