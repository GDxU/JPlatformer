package com.sh.jplatformer.world.map.agility;

import java.io.Serializable;
import com.badlogic.gdx.math.MathUtils;
import com.sh.jplatformer.world.WorldController;

/**
 * The {@code AgilityValue} stores a pair of floating point values that represent the positive and
 * the negative components of a force. Both can be specified by maximum, acceleration and
 * deceleration values. Agility values are used by the {@code Agility} class to manage velocities.
 * @author Stefan Hösemann
 */

public class AgilityValue implements Serializable
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Fields
	//=======
	private float positive;
	private float negative;
	private float acceleration;
	private float deceleration;
	private float maxPositive;
	private float maxNegative;
	
	// acceleratePositive
	//===================
	public void acceleratePositive()
	{
		positive += WorldController.getDeltaRatio60( acceleration );
		clampValues();
	}
	
	// deceleratePositive
	//===================
	public void deceleratePositive()
	{
		positive -= WorldController.getDeltaRatio60( deceleration );
		clampValues();
	}
	
	// accelerateNegative
	//===================
	public void accelerateNegative()
	{
		negative += WorldController.getDeltaRatio60( acceleration );
		clampValues();
	}
	
	// decelerateNegative
	//===================
	public void decelerateNegative()
	{
		negative -= WorldController.getDeltaRatio60( deceleration );
		clampValues();
	}
	
	// reset
	//======
	public void reset()
	{
		positive = 0f;
		negative = 0f;
	}
	
	// clampValues
	//============
	private void clampValues()
	{
		// Calculate maximum
		//==================
		float maxPosDelta = WorldController.getDelta( maxPositive );
		float maxNegDelta = WorldController.getDelta( maxNegative );
		
		// Clamp
		//======
		positive = MathUtils.clamp( positive, 0f, maxPosDelta );
		negative = MathUtils.clamp( negative, 0f, maxNegDelta );
	}
	
	// setAcceleration
	//================
	public void setAcceleration( float acceleration )
	{
		this.acceleration = acceleration;
	}
	
	// getAcceleration
	//================
	public float getAcceleration()
	{
		return ( acceleration );
	}
	
	// setDeceleration
	//================
	public void setDeceleration( float deceleration )
	{
		this.deceleration = deceleration;
	}
	
	// getDeceleration
	//================
	public float getDeceleration()
	{
		return ( deceleration );
	}
	
	// setMaxPositive
	//===============
	public void setMaxPositive( float maxPositive )
	{
		this.maxPositive = maxPositive;
	}
	
	// getMaxPositive
	//===============
	public float getMaxPositive()
	{
		return ( maxPositive );
	}
	
	// setMaxNegative
	//===============
	public void setMaxNegative( float maxNegative )
	{
		this.maxNegative = maxNegative;
	}
	
	// getMaxNegative
	//===============
	public float getMaxNegative()
	{
		return ( maxNegative );
	}
	
	// setPositive
	//============
	public void setPositive( float positive )
	{
		this.positive = positive;
	}
	
	// getPositive
	//============
	public float getPositive()
	{
		return ( positive );
	}
	
	// setNegative
	//============
	public void setNegative( float negative )
	{
		this.negative = negative;
	}
	
	// getNegative
	//============
	public float getNegative()
	{
		return ( negative );
	}
	
	// getSum
	//=======
	public float getSum()
	{
		return ( positive - negative ) ;
	}
}
