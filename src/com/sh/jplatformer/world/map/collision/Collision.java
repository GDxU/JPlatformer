package com.sh.jplatformer.world.map.collision;

import java.io.Serializable;

public class Collision implements Serializable
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Types
	//======
	public enum CollisionType
	{
		NONE, WEST, EAST, TOP, BOTTOM
	}
	
	// Fields
	//=======
	public CollisionType x;
	public CollisionType y;
	
	// Constructor
	//============
	public Collision()
	{
		x = CollisionType.NONE;
		y = CollisionType.NONE;
	}
}
