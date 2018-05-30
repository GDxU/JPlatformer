package com.sh.jplatformer.world.objects.machines;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.collision.Collision.CollisionType;
import com.sh.jplatformer.world.map.collision.CollisionHelper;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class CannonBall extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	public enum Routine
	{
		EAST, WEST, UP, DOWN
	}
	private Routine routine;
	
	// Constructor
	//============
	public CannonBall( WorldController worldController, Routine routine )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Cannon Ball";
		bounds.width    = 32f;
		bounds.height   = 32f;
		isBlockingSpace = false;
		ignoreGravity   = true;
		
		// Routine
		//========
		this.routine = routine;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// draw
	//=====
	@Override
	public void draw( SpriteBatch batch )
	{
		// Update alpha
		//=============
		updateAlpha();
		
		// Frame setup
		//============
		frames[currentFrame].setPosition( bounds.x + bounds.width / 2f - bounds.height,
		                                  bounds.y - bounds.height / 2f );
		frames[currentFrame].setColor( 1f, 1f, 1f, alpha );
		frames[currentFrame].draw( batch );
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Move in direction
		//==================
		ignoreGravity = true;
		
		float delta = WorldController.getDelta( agility.getVelocity().getMaxPositive() );
		
		if ( routine == Routine.EAST ) CollisionHelper.move( this, +delta, 0f );
		if ( routine == Routine.WEST ) CollisionHelper.move( this, -delta, 0f );
		if ( routine == Routine.UP   ) CollisionHelper.move( this, 0f, +delta );
		if ( routine == Routine.DOWN ) CollisionHelper.move( this, 0f, -delta );
		
		// Destroy if blocked
		//===================
		if ( lastCollision.x != CollisionType.NONE ||
		     lastCollision.y != CollisionType.NONE )
		{
			this.setAlive( false );
		}
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
		if ( routine == Routine.EAST ) currentFrame = 0;
		if ( routine == Routine.WEST ) currentFrame = 1;
		if ( routine == Routine.UP   ) currentFrame = 3;
		if ( routine == Routine.DOWN ) currentFrame = 2;
	}
	
	// initAgility
	//============
	@Override
	public void initAgility()
	{
		// Super
		//======
		super.initAgility();
		
		// Velocity
		//==========
		agility.getVelocity().setMaxPositive( 180f );
	}
	
	// setPosition
	//============
	@Override
	public void setPosition( float x, float y, boolean center )
	{
		if ( center == true )
		{
			bounds.x = x - bounds.width / 2f;
			bounds.y = y - bounds.height / 2f + 1f;
		}
		else
		{
			bounds.x = x;
			bounds.y = y;
		}
	}
}