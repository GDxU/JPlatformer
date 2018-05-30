package com.sh.jplatformer.world.map.agility;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.collision.CollisionHelper;

/**
 * The {@code Agility} class represents horizontal and vertical motion of an object.
 * @author Stefan Hösemann
 */

public class Agility implements Serializable
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;

	// Constants
	//==========
	public enum Direction
	{
		NONE,
		POSITIVE,
		NEGATIVE
	}
	
	// Velocity
	//=========
	private AgilityValue velocity;
	private Direction direction;
	private Vector2 helperVector2;
	
	// Jumps / gravity
	//================
	private float minJumpHeight;
	private float maxJumpHeight;
	private float jumpHeight;
	private float jumpTarget;
	private float jumpDelta;
	private boolean gravityEnabled;
	
	// Repulsions
	//===========
	private ArrayList<Repulsion> repulsions;
	private Vector2 repulsionSum;
		
	// Constructor
	//============
	public Agility()
	{
		velocity = new AgilityValue();
		repulsions = new ArrayList<Repulsion>();
		repulsionSum = new Vector2();
	}
	
	// update
	//=======
	/**
	 * Performs all invoked actions and resets all invocations.
	 */
	public void update()
	{
		// Velocity and jumps
		//===================
		updateVelocity();
		updateJump();
		
		// Repulsion
		//==========
		for ( Repulsion r : repulsions )
		{
			r.update();
		}
		repulsions.removeIf( r -> r.getForce() <= 0f );
	}
	
	// updateVelocity
	//===============
	private void updateVelocity()
	{	
		// When moving
		//============
		if ( direction != Direction.NONE )
		{
			// While moving forward
			//=====================
			if ( direction == Direction.POSITIVE )
			{
				velocity.acceleratePositive();
				
				if ( velocity.getNegative() > 0f )
				{
					velocity.decelerateNegative();
				}
			}
			
			// While moving reverse
			//=====================
			if ( direction == Direction.NEGATIVE )
			{
				velocity.accelerateNegative();
				
				if ( velocity.getPositive() > 0f )
				{
					velocity.deceleratePositive();
				}
			}
		}
		
		// When not moving
		//================
		else
		{
			velocity.deceleratePositive();
			velocity.decelerateNegative();
		}
		
		// Reset direction
		//================
		direction = Direction.NONE;
	}
	
	// updateJump
	//===========
	private void updateJump()
	{
		// While rising
		//=============
		if ( jumpHeight < jumpTarget )
		{
			// Calculate delta
			//================
			jumpDelta = ( jumpHeight - jumpTarget ) * -8f;
			
			if ( jumpDelta < 150f ) jumpDelta = 150f;
			if ( jumpDelta > 450f ) jumpDelta = 450f;
			
			// Transform to world delta + clamp
			//=================================
			jumpDelta = WorldController.getDelta( jumpDelta );
			jumpDelta = CollisionHelper.limitDelta( jumpDelta );
			
			// Increase jump height
			//=====================
			jumpHeight += jumpDelta;
			
			// Reset at reverse point
			//=======================
			if ( jumpHeight >= jumpTarget )
			{
				resetJump();
			}
		}
		
		// While falling
		//==============
		else if ( gravityEnabled )
		{
			jumpDelta -= WorldController.getDeltaRatio60( 32f );
		}
	}
	
	// accelerate
	//===========
	/**
	 * Invokes a forward acceleration.
	 * @param negative if {@code true}, acceleration will be performed in a reverse direction.
	 */
	public void accelerate( boolean negative )
	{
		if ( negative == false )
		{
			direction = Direction.POSITIVE;
		}
		else
		{
			direction = Direction.NEGATIVE;
		}
	}
	
	// jump
	//=====
	/**
	 * Invokes a jump.
	 */
	public void jump()
	{
		resetJump();
		jumpTarget = minJumpHeight;
	}
	
	// jump
	//=====
	/**
	 * Invokes a jump.
	 * @param newJumpHeight the jump height.
	 * @param overrideMaxJumpHeight if this is {@code true}, the jump height will not
	 * be limited by {@code maxJumpHeight}.
	 */
	public void extendJump( float newJumpHeight, boolean overrideMaxJumpHeight )
	{
		if ( overrideMaxJumpHeight )
		{
			jumpTarget = MathUtils.clamp( newJumpHeight, minJumpHeight, 9999f );
		}
		else
		{
			jumpTarget = MathUtils.clamp( newJumpHeight, minJumpHeight, maxJumpHeight );
		}
		
	}
	
	// resetJump
	//==========
	public void resetJump()
	{
		jumpDelta = 0f;
		jumpHeight = 0f;
		jumpTarget = 0f;
	}
	
	// setMinJumpHeight
	//=================
	public void setMinJumpHeight( float minJumpHeight )
	{
		this.minJumpHeight = minJumpHeight;
	}
	
	// setMaxJumpHeight
	//=================
	public void setMaxJumpHeight( float maxJumpHeight )
	{
		this.maxJumpHeight = maxJumpHeight;
	}
	
	// getVelocity
	//============
	public AgilityValue getVelocity()
	{
		return ( velocity );
	}
	
	// getDelta
	//=========
	/**
	 * @return the total amount of movement on all axes.
	 */
	public Vector2 getDelta()
	{
		// Create helper
		//==============
		if ( helperVector2 == null )
		{
			helperVector2 = new Vector2();
		}
		
		// Calculate delta on all axes
		//============================
		float x = getRepulsionVector().x + velocity.getSum();
		float y = getRepulsionVector().y + jumpDelta;
		
		return ( helperVector2.set( x, y ).add( getRepulsionVector() ) );
	}
	
	// getDirection
	//=============
	public Direction getDirection()
	{
		return ( direction );
	}
	
	// clearRepulsions
	//================
	public void clearRepulsions()
	{
		repulsions.clear();
	}
	
	// addRepulsion
	//=============
	public void addRepulsion( float force, float angle, float attenuation )
	{
		repulsions.add( new Repulsion( force, angle, attenuation ) );
	}
	
	// getRepulsionVector
	//===================
	public Vector2 getRepulsionVector()
	{
		// Reset
		//======
		repulsionSum.set( 0f, 0f );
		
		// Add repulsions
		//===============
		for ( Repulsion r : repulsions )
		{
			repulsionSum.add( r.getForceVector() );
		}
		
		// Multiply by world delta
		//========================
		repulsionSum.x = WorldController.getDeltaRatio60( repulsionSum.x );
		repulsionSum.y = WorldController.getDeltaRatio60( repulsionSum.y );
		
		return ( repulsionSum );
	}
	
	// isMoving
	//=========
	/**
	 * @return {@code true} if the {@code Agility} is moving horizontally from acceleration,
	 * not repulsion.
	 */
	public boolean isMoving()
	{
		return ( velocity.getSum() != 0f );
	}
	
	// isJumping
	//==========
	public boolean isJumping()
	{
		return ( jumpHeight < jumpTarget );
	}
	
	// isFalling
	//==========
	public boolean isFalling()
	{
		return ( jumpDelta < 0f && jumpHeight >= jumpTarget );
	}
	
	// setGravityEnabled
	//==================
	public void setGravityEnabled( boolean gravityEnabled )
	{
		this.gravityEnabled = gravityEnabled;
	}
	
	// createVelocity
	//===============
	public void createVelocity()
	{
		if ( velocity == null )
		{
			velocity = new AgilityValue();
		}
	}
}
