package com.sh.jplatformer.ui.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.agility.Agility.Direction;
import com.sh.jplatformer.world.objects.characters.Player;

/**
 * The {@code PlayerInput} class handles player related input events during gameplay.
 * @author Stefan Hösemann
 */

public class PlayerInput extends InputAdapter
{
	// World
	//======
	private WorldController worldController;
	private Player player;
	
	// Controls
	//=========
	private boolean jumpInitiated;
	private boolean jumpKeyDown;
	private long jumpTimer;
	private boolean useKeyDown;
	
	// Constructor
	//============
	/**
	 * @param worldController the {@code WorldController} to interact with.
	 */
	public PlayerInput( WorldController worldController )
	{
		this.worldController = worldController;
	}
	
	// update
	//=======
	public void update()
	{
		player = (Player) worldController.getPlayer();
		keyPressed();
		mousePressed();
	}
	
	// keyPressed
	//===========
	private void keyPressed()
	{
		// Check for player
		//=================
		if ( !isPlayerInputAllowed() )
		{
			return;
		}
		
		// Reset direction
		//================
		player.setRunningAnimDirection( Direction.NONE );
			
		// Accelerate right
		//=================
		if ( Gdx.input.isKeyPressed( Keys.RIGHT ) )
		{
			player.getAgility().accelerate( false );
			player.setRunningAnimDirection( Direction.POSITIVE );
		}
		
		// Accelerate left
		//================
		if ( Gdx.input.isKeyPressed( Keys.LEFT ) )
		{
			player.getAgility().accelerate( true );
			player.setRunningAnimDirection( Direction.NEGATIVE );
		}
		
		// Initialize jump
		//================
		if ( Gdx.input.isKeyJustPressed( Keys.SPACE ) )
		{
			jumpInitiated = true;
		}
		
		// Perform initial jump
		//=====================
		if ( jumpInitiated )
		{
			if ( !player.getAgility().isJumping() && player.isOnGround() )
			{
				jumpInitiated = false;
				jumpKeyDown = true;
				jumpTimer = System.currentTimeMillis();
				player.getAgility().jump();
			}
		}
		
		// Extend jump
		//============
		if ( jumpKeyDown && player.getAgility().isJumping() )
		{
			player.getAgility().extendJump( System.currentTimeMillis() - jumpTimer, false );
		}
		else if ( player.getAgility().isFalling() )
		{
			jumpKeyDown = false;
		}
	}
	
	// mousePressed
	//=============
	private void mousePressed()
	{
		// Check for player
		//=================
		if ( !isPlayerInputAllowed() )
		{
			return;
		}
	}
	
	// keyUp
	//======
	@Override
	public boolean keyUp( int key )
	{
		// Release jump
		//=============
		if ( key == Keys.SPACE )
		{
			jumpInitiated = false;
			jumpKeyDown = false;
		}
		
		// Release use
		//============
		if ( key == Keys.UP )
		{
			useKeyDown = false;
		}
		return ( false );
	}
	
	// keyDown
	//========
	@Override
	public boolean keyDown( int key )
	{
		// Check for player
		//=================
		if ( !isPlayerInputAllowed() )
		{
			return ( false );
		}
		
		// Use
		//====
		if ( !useKeyDown && Gdx.input.isKeyPressed( Keys.UP ) )
		{
			useKeyDown = true;
			player.use();
		}
		return ( false );
	}
	
	// isPlayerInputAllowed
	//=====================
	private boolean isPlayerInputAllowed()
	{
		return ( player != null && !player.isDeathSequencePlaying() );
	}
}
