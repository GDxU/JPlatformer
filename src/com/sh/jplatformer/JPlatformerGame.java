package com.sh.jplatformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.screens.EditorScreen;
import com.sh.jplatformer.screens.GameScreen;
import com.sh.jplatformer.screens.MenuScreen;
import com.sh.jplatformer.util.Task;
import com.sh.jplatformer.util.Transition;

/**
 * This is the main class of the game.
 * @author Stefan Hösemann
 */

public class JPlatformerGame extends Game
{
	// Game meta data
	//===============
	public static final String TITLE   = "JPlatformer";
	public static final String AUTHOR  = "Stefan Hösemann";
	public static final String VERSION = "1.37";
	public static final String YEARS   = "2016-2018";
	
	// Game
	//=====
	private static JPlatformerGame instance;
	private boolean paused;
	
	// Screens
	//========
	public final MenuScreen menuScreen;
	public final EditorScreen editorScreen;
	public final GameScreen gameScreen;
	
	// Rendering
	//==========
	private SpriteBatch batch;
	private Transition transition;
	private float[] deltaTimes;
	private int deltaIndex;
	private float averageDelta;
	
	// Constructor
	//============
	public JPlatformerGame()
	{
		menuScreen   = new MenuScreen();
		editorScreen = new EditorScreen();
		gameScreen   = new GameScreen();
		
		deltaTimes = new float[16];
	}
	
	// get
	//====
	/**
	 * Creates a single instance of this class (if there is none yet) and returns it.
	 */
	public static JPlatformerGame get()
	{
		if ( instance == null )
		{
			instance = new JPlatformerGame();
		}
		return ( instance );
	}
	
	// render
	//=======
	@Override
	public void render()
	{
		// Update delta times
		//===================
		updateDelta();
		
		// Render game and transition
		//===========================
		Gdx.gl20.glClearColor( 0.07f, 0.075f, 0.08f, 1f );
		Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		super.render();
		transition.update();
		transition.draw();
	}
	
	// create
	//=======
	@Override
	public void create()
	{
		// Set title
		//==========
		Gdx.graphics.setTitle( TITLE + " v" + VERSION );
		
		// Load config
		//============
		Config.get().initDefault();
		Config.get().load();
		Config.get().applyDisplay();
		
		// Batch and screen
		//=================
		batch = new SpriteBatch();
		transition = new Transition();
		transition.transition( new Task()
		{
			@Override
			public void perform()
			{
				JPlatformerGame.this.setScreen( menuScreen );
			}
		} );
	}
	
	// dispose
	//========
	@Override
	public void dispose()
	{
		// Save settings
		//==============
		Config.get().save();
		
		// Dispose resources
		//==================
		menuScreen.dispose();
		editorScreen.dispose();
		gameScreen.dispose();
		batch.dispose();
		Resources.dispose();
	}
	
	// updateDelta
	//============
	private void updateDelta()
	{
		// Save previous frame times
		//==========================
		deltaIndex++;
		if ( deltaIndex > deltaTimes.length - 1 )
		{
			deltaIndex = 0;
		}
		deltaTimes[deltaIndex] = Gdx.graphics.getDeltaTime();
		
		// Calculate average delta
		//========================
		float sum = 0f;
		int index;
		
		for ( index = 0; index < deltaTimes.length; index++ )
		{
			sum += deltaTimes[index];
		}
		averageDelta = ( sum / index );
	}
	
	// resetDelta
	//===========
	public void resetDelta()
	{
		deltaTimes = new float[16];
		deltaIndex = 0;
		averageDelta = 0f;
	}
	
	// getAverageDelta
	//================
	public float getAverageDelta()
	{
		// Return actual delta during fade in
		//===================================
		if ( transition.getState() == Transition.State.FADE_IN )
		{
			averageDelta = Gdx.graphics.getDeltaTime();
		}
		return ( averageDelta );
	}
	
	// getBatch
	//=========
	public SpriteBatch getBatch()
	{
		return ( batch );
	}
	
	// getTransition
	//==============
	public Transition getTransition()
	{
		return ( transition );
	}
	
	// setPaused
	//==========
	public void setPaused( boolean paused )
	{
		this.paused = paused;
	}
	
	// isPaused
	//=========
	public boolean isPaused()
	{
		return ( paused );
	}
	
	// resize
	//=======
	@Override
	public void resize( int width, int height )
	{
		super.resize( width, height );
		transition.resize( width, height );
	}
}