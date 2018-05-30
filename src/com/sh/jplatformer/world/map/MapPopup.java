package com.sh.jplatformer.world.map;

import java.io.Serializable;
import com.badlogic.gdx.math.Vector2;

/**
 * The {@code MapPopup} class represents messages that pop up in the game world.
 * @author Stefan Hösemann
 */

public class MapPopup implements Serializable
{
	// Constants
	//==========
	private static final long serialVersionUID = 1L;
	
	// Fields
	//=======
	private String text;
	private Vector2 position;
	private float alpha;
	
	// Constructor
	//============
	/**
	 * @param newText the displayed text.
	 * @param x the x-position (center).
	 * @param y the y-position (center).
	 */
	public MapPopup( String newText, float x, float y )
	{
		text = newText;
		position = new Vector2( x, y );
		alpha = 1f;
	}
	
	// getText
	//========
	public String getText()
	{
		return ( text );
	}
	
	// getPosition
	//============
	/**
	 * @return the current position of the popup message. The x-position represents the center, the
	 * y-position represents the bottom of the message.
	 */
	public Vector2 getPosition()
	{
		return ( position );
	}
	
	// setAlpha
	//=========
	public void setAlpha( float alpha )
	{
		this.alpha = alpha;
	}
	
	// getAlpha
	//=========
	public float getAlpha()
	{
		return ( alpha );
	}
}
