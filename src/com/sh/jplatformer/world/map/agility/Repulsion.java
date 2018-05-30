package com.sh.jplatformer.world.map.agility;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;
import com.sh.jplatformer.world.WorldController;

/**
 * The {@code Repulsion} class represents a force effecting an {@code Agility}.
 * @author Stefan Hösemann
 */

public class Repulsion implements Serializable
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Fields
	//=======
	private float force;
	private float angle;
	private float attenuation;
	private Vector2 helperVector2;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code Repulsion}. All values will be multiplied by the world delta time.
	 * @param force the initial force of the repulsion.
	 * @param angle the angle to repulse to.
	 * @param attenuation the attenuation of this repulsion per update.
	 */
	public Repulsion( float force, float angle, float attenuation )
	{
		this.force = force;
		this.angle = angle;
		this.attenuation = attenuation;
		this.helperVector2 = new Vector2();
	}
	
	// update
	//=======
	/**
	 * Decreases the current {@code force} of this {@code Repulsion} by its {@code attenuation}.
	 */
	public void update()
	{
		force -= WorldController.getDelta( attenuation );
	}
	
	// getForce
	//=========
	/**
	 * @return the current {@code force} multiplied by the world delta time.
	 */
	public float getForce()
	{
		return ( WorldController.getDelta( force ) );
	}
	
	// getForceVector
	//===============
	public Vector2 getForceVector()
	{
		helperVector2.set( force, 0f );
		helperVector2.setAngle( 0f );
		helperVector2.rotate( angle );
		
		return ( helperVector2 );
	}
}
