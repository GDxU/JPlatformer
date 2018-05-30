package com.sh.jplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.input.EditorInput;
import com.sh.jplatformer.ui.input.PlayerInput;
import com.sh.jplatformer.ui.stages.EditorStage;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.WorldRenderer;

/**
 * The {@code EditorScreen} displays the world editor.
 * @author Stefan Hösemann
 */

public class EditorScreen implements Screen
{
	// World
	//======
	private WorldController worldController; 
	private WorldRenderer worldRenderer;
	
	// UI / input
	//===========
	private EditorStage editorStage;
	private EditorInput editorInput;
	private PlayerInput playerInput;
	private InputMultiplexer inputMultiplexer;
	
	// render
	//=======
	@Override
	public void render( float delta )
	{
		// Update world
		//=============
		worldController.update();
		playerInput.update();
		
		// Render world
		//=============
		SpriteBatch batch = JPlatformerGame.get().getBatch();
		batch.begin();
		{
			batch.setProjectionMatrix( worldController.getWorldCamera().combined );
			worldRenderer.render( batch );
		}
		batch.end();
		
		// Play world audio
		//=================
		if ( !JPlatformerGame.get().isPaused() )
		{
			worldController.getWorldAudio().play();
		}
		else
		{
			worldController.getWorldAudio().pause();
		}
		
		// Editor stage
		//=============
		editorInput.update();
		editorStage.act();
		editorStage.draw();
	}
	
	// resize
	//=======
	@Override
	public void resize( int width, int height )
	{
		worldController.getWorldCamera().resize( width, height );
		editorStage.resize( width, height );
	}
	
	// pause
	//======
	@Override
	public void pause()
	{
	}
	
	// resume
	//=======
	@Override
	public void resume()
	{
	}
	
	// show
	//=====
	@Override
	public void show()
	{
		// World
		//======
		worldController = new WorldController();
		worldRenderer = new WorldRenderer( worldController );
		
		// UI / input
		//===========
		editorStage      = new EditorStage( worldController );
		editorInput      = new EditorInput( editorStage );
		playerInput      = new PlayerInput( worldController );
		inputMultiplexer = new InputMultiplexer();
		
		// Multiplexer setup
		//==================
		inputMultiplexer.addProcessor( editorStage );
		inputMultiplexer.addProcessor( editorInput );
		inputMultiplexer.addProcessor( playerInput );
		
		Gdx.input.setInputProcessor( inputMultiplexer );
		
		// Pause game
		//===========
		JPlatformerGame.get().setPaused( true );
	}
	
	// hide
	//=====
	@Override
	public void hide()
	{
		this.dispose();
	}
	
	// dispose
	//========
	@Override
	public void dispose()
	{
		try
		{
			// Pause audio
			//============
			worldController.getWorldAudio().pause();
			
			// Environment
			//============
			Resources.WORLD.loadBackground( null );
			Resources.WORLD.loadForeground( null );
			Resources.WORLD.loadEnvironmentSound( null );
			
			// UI / input
			//===========
			editorStage.dispose();
			inputMultiplexer.clear();
		}
		catch ( Exception e ) {}
	}
}