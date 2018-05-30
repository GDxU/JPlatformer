package com.sh.jplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sh.jplatformer.Config;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.input.GameInput;
import com.sh.jplatformer.ui.input.PlayerInput;
import com.sh.jplatformer.ui.stages.GameStage;
import com.sh.jplatformer.util.Task;
import com.sh.jplatformer.util.Transition;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.WorldFile;
import com.sh.jplatformer.world.WorldRenderer;

/**
 * The {@code GameScreen} displays the play mode.
 * @author Stefan Hösemann
 */

public class GameScreen implements Screen
{
	// World
	//======
	private WorldController worldController; 
	private WorldRenderer worldRenderer;
	
	// UI / input
	//===========
	private GameStage gameStage;
	private GameInput gameInput;
	private PlayerInput playerInput;
	private InputMultiplexer inputMultiplexer;
	
	// render
	//=======
	@Override
	public void render( float delta )
	{
		// Update game
		//============
		worldController.update();
		playerInput.update();
		
		// Process state
		//==============
		processWorldState( worldController.getWorldState() );
		
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
		
		// Game stage
		//===========
		gameStage.act();
		gameStage.draw();
	}
	
	// processWorldState
	//==================
	private void processWorldState( int state )
	{
		// Return if transitioning
		//========================
		if ( JPlatformerGame.get().getTransition().getState() != Transition.State.NONE )
		{
			return;
		}
		
		// Restart game
		//=============
		if ( worldController.getWorldState() == WorldController.STATE_RESTART )
		{
			JPlatformerGame.get().getTransition().transition( new Task()
			{
				@Override
				public void perform()
				{
					gameStage.restartGame();
				}
			} );
		}
		
		// Records menu
		//=============
		if ( worldController.getWorldState() == WorldController.STATE_VIEW_STATS )
		{
			if ( !gameStage.isRecordsMenuVisible() )
			{
				gameStage.showRecordsMenu();
				Resources.UI.sound_game_win.play();
			}
		}
		
		// Save and return
		//================
		if ( worldController.getWorldState() == WorldController.STATE_BACK_TO_MENU )
		{
			JPlatformerGame.get().getTransition().transition( new Task()
			{
				@Override
				public void perform()
				{
					WorldFile.saveHighscores( worldController );
					JPlatformerGame.get().setScreen( JPlatformerGame.get().menuScreen );
				}
			} );
		}
	}
	
	// startGame
	//==========
	private void startGame( String worldPath )
	{
		if ( WorldFile.loadWorld( worldPath, worldController ) == true )
		{
			worldController.setLive( true );
			worldController.resetTimer();
			JPlatformerGame.get().resetDelta();
			JPlatformerGame.get().setPaused( false );
		}
		else
		{
			JPlatformerGame.get().getTransition().transition( new Task()
			{
				@Override
				public void perform()
				{
					JPlatformerGame.get().setScreen( JPlatformerGame.get().menuScreen );
				}
			} );
		}
	}
	
	// resize
	//=======
	@Override
	public void resize( int width, int height )
	{
		worldController.getWorldCamera().resize( width, height );
		gameStage.resize( width, height );
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
		gameStage        = new GameStage( worldController );
		gameInput        = new GameInput( gameStage );
		playerInput      = new PlayerInput( worldController );
		inputMultiplexer = new InputMultiplexer();
		
		// Multiplexer setup
		//==================
		inputMultiplexer.addProcessor( gameStage );
		inputMultiplexer.addProcessor( gameInput );
		inputMultiplexer.addProcessor( playerInput );
		
		Gdx.input.setInputProcessor( inputMultiplexer );
		
		// Start game
		//===========
		this.startGame( Config.get().tmp_worldPath );
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
			gameStage.dispose();
		}
		catch ( Exception e ) {}
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
}
