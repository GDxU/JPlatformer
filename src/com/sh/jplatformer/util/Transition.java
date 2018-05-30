package com.sh.jplatformer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * The {@code Transition} represents a fade in/fade out transition effect.
 * @author Stefan Hösemann
 */

public class Transition
{
	// State
	//======
	public enum State
	{
		NONE, FADE_IN, FADE_OUT, SWITCH;
	}
	
	// Task and state
	//===============
	private Task task;
	private State state;
	
	// Rendering
	//==========
	private ShapeRenderer renderer;
	private OrthographicCamera camera;
	private float alpha;
	private int frameCounter;
	
	// Constructor
	//============
	public Transition()
	{
		this.state = State.NONE;
		this.renderer = new ShapeRenderer();
		this.camera = new OrthographicCamera();
	}
	
	// update
	//=======
	/**
	 * Updates the transition effect and performs the specified task.
	 */
	public void update()
	{
		// Fade out
		//=========
		if ( state == State.FADE_OUT )
		{
			alpha += Gdx.graphics.getDeltaTime() * 3f;
			if ( alpha >= 1f )
			{
				state = State.SWITCH;
			}
		}
		
		// Switch and perform task
		//========================
		else if ( state == State.SWITCH )
		{
			if ( task != null )
			{
				task.perform();
			}
			state = State.FADE_IN;
		}
		
		// Fade in
		//========
		else if ( state == State.FADE_IN )
		{
			if ( frameCounter >= 2 )
			{
				alpha -= Gdx.graphics.getDeltaTime() * 3f;
				if ( alpha <= 0f )
				{
					state = State.NONE;
				}
			}
			else
			{
				frameCounter++;
			}
		}
	}
	
	// draw
	//=====
	/**
	 * Draws the fade effect using a {@code ShapeRenderer}.
	 */
	public void draw()
	{
		if ( alpha >= 0f )
		{
			// Enable blending
			//================
			Gdx.gl.glEnable( GL20.GL_BLEND );

			// Render black overlay
			//=====================
			renderer.begin( ShapeType.Filled );
			renderer.setColor( 0f, 0f, 0f, alpha );
			renderer.rect( 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
			renderer.end();
			
			// Disable blending
			//=================
			Gdx.gl.glDisable( GL20.GL_BLEND );
		}
	}
	
	// transition
	//===========
	/**
	 * Initializes a new transition effect if there is no transition in action.
	 * @param nextTask the {@code Task} to perform during the transition.
	 */
	public void transition( Task nextTask )
	{
		if ( state == State.NONE )
		{
			state        = State.FADE_OUT;
			task         = nextTask;
			frameCounter = 0;
			alpha        = 0f;
		}
	}
	
	// getState
	//=========
	public State getState()
	{
		return ( state );
	}
	
	// resize
	//=======
	public void resize( int width, int height )
	{
		camera.position.x     = width  / 2f;
		camera.position.y     = height / 2f;
		camera.viewportWidth  = width;
		camera.viewportHeight = height;
		camera.update();
		
		renderer.setProjectionMatrix( camera.combined );
	}
}