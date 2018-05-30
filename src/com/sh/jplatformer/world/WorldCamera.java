package com.sh.jplatformer.world;

import java.io.Serializable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sh.jplatformer.Config;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code WorldCamera} class extends {@code OrthographicCamera} and provides some utility
 * methods for camera movement and zoom.
 * @author Stefan Hösemann
 */

public class WorldCamera extends OrthographicCamera implements Serializable
{
	// Constants
	//==========
	private static final long serialVersionUID = 1L;
	public static final float VIEWPORT_WIDTH = 1920f;
	private static final float zoomSteps[] =
	{
		0.5f,
		0.66666f,
		1f,
		1.25f,
		1.66666f,
		2.5f,
		3.33333f,
		5f,
		10f
	};
	
	// Fields
	//=======
	private MapObject target;
	private int currentZoomStep;
	private Vector2 helperVector2;
	private Vector3 helperVector3;
	
	// Constructor
	//============
	public WorldCamera()
	{
		this.helperVector2 = new Vector2();
		this.helperVector3 = new Vector3();
		this.resize( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
		this.resetZoom();
	}
	
	// update
	//=======
	@Override
	public void update()
	{
		// Update zoom
		//============
		this.updateZoom();
		
		// Follow target
		//==============
		if ( target != null )
		{
			this.position.x = target.getBounds().x + target.getBounds().width  / 2f;
			this.position.y = target.getBounds().y + target.getBounds().height / 2f;
		}
		
		// Update camera
		//==============
		super.update();
	}
	
	// resize
	//=======
	/**
	 * Updates the camera viewport.
	 * @param width the new width.
	 * @param height the new height.
	 */
	public void resize( int width, int height )
	{
		if ( Config.get().enableFixedViewport )
		{
			super.viewportWidth = VIEWPORT_WIDTH;
			super.viewportHeight = VIEWPORT_WIDTH * ( (float) height / (float) width );
		}
		else
		{
			super.viewportWidth = width;
			super.viewportHeight = width * ( (float) height / (float) width );
		}
		this.update();
	}
	
	// updateZoom
	//===========
	private void updateZoom()
	{
		// Zoom in
		//========
		if ( zoom > zoomSteps[currentZoomStep] )
		{
			zoom -= Gdx.graphics.getDeltaTime() * ( zoom * 3.5f );
			if ( zoom < zoomSteps[currentZoomStep] )
			{
				zoom = zoomSteps[currentZoomStep];
			}
		}
		
		// Zoom out
		//=========
		if ( zoom < zoomSteps[currentZoomStep] )
		{
			zoom += Gdx.graphics.getDeltaTime() * ( zoom * 3.5f );
			if ( zoom > zoomSteps[currentZoomStep] )
			{
				zoom = zoomSteps[currentZoomStep];
			}
		}
	}
	
	// resetZoom
	//==========
	/**
	 * Resets the zoom to 100%.
	 */
	public void resetZoom()
	{
		currentZoomStep = 2;
		zoom = zoomSteps[currentZoomStep];
	}
	
	// zoomIn
	//=======
	/**
	 * Sets the next zoom step from an array of predefined zoom steps.
	 */
	public void zoomIn()
	{
		if ( currentZoomStep - 1 < 0 )
		{
			return;
		}
		currentZoomStep--;
	}
	
	// zoomOut
	//========
	/**
	 * Sets the previous zoom step from an array of predefined zoom steps.
	 */
	public void zoomOut()
	{
		if ( currentZoomStep + 1 >= zoomSteps.length )
		{
			return;
		}
		currentZoomStep++;
	}
	
	// getOffset
	//==========
	/**
	 * @return the position of the camera with an origin in the bottom left corner of the screen.
	 */
	public Vector2 getOffset()
	{
		helperVector2.x = this.position.x - ( this.viewportWidth  * zoom / 2f );
		helperVector2.y = this.position.y - ( this.viewportHeight * zoom / 2f );
		
		return ( helperVector2 );
	}
	
	// setPosition
	//============
	/**
	 * Sets the camera position and removes the current target.
	 * @param position the new position.
	 */
	public void setPosition( Vector3 position )
	{
		target = null;

		this.position.x = position.x;
		this.position.y = position.y;
		this.position.z = position.z;
	}
	
	// getPosition
	//============
	public Vector3 getPosition()
	{
		return ( position );
	}
	
	// setTarget
	//==========
	public void setTarget( MapObject target )
	{
		this.target = target;
	}
	
	// getTarget
	//==========
	public MapObject getTarget()
	{
		return ( target );
	}
	
	// translate
	//==========
	@Override
	public void translate( float x, float y )
	{
		target = null;
		super.translate( x, y );
	}
	
	// translate
	//==========
	@Override
	public void translate( Vector2 vector )
	{
		target = null;
		super.translate( vector );
	}
	
	// project
	//========
	public Vector2 project( Vector2 vector )
	{
		super.project( helperVector3.set( vector.x, vector.y, 0f ) );
		return ( vector.set( helperVector3.x, helperVector3.y ) );
	}
	
	// toUnits
	//========
	/**
	 * Converts the given amount of pixels to world units.
	 */
	public float toUnits( float pixels )
	{
		return ( pixels * ( Gdx.graphics.getWidth() / viewportWidth ) );
	}
}
