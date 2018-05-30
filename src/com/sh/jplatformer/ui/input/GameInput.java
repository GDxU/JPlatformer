package com.sh.jplatformer.ui.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.stages.GameStage;
import com.sh.jplatformer.world.WorldController;

/**
 * The {@code GameInput} class handles game-play related input events.
 * @author Stefan Hösemann
 */

public class GameInput extends InputAdapter
{
	// Fields
	//=======
	private GameStage gameStage;
	
	// Constructor
	//============
	/**
	 * @param gameStage the {@code GameStage} to interact with.
	 */
	public GameInput( GameStage gameStage )
	{
		this.gameStage = gameStage;
	}
	
	// keyDown
	//========
	@Override
	public boolean keyDown( int key )
	{
		// Open pause menu
		//================
		if ( key == Keys.ESCAPE || key == Keys.P )
		{
			// Ignore if world is finished
			//=============================
			if ( gameStage.isRecordsMenuVisible() == false )
			{
				// Open / close menu
				//==================
				if ( JPlatformerGame.get().isPaused() )
				{
					gameStage.resumeGame();
					Resources.UI.sound_game_pause.play();
				}
				else if ( gameStage.getWorldController().getWorldState() == WorldController.STATE_PLAYING )
				{
					gameStage.showPauseMenu();
					Resources.UI.sound_game_pause.play();
				}
			}
		}
		
		// Zoom in
		//========
		if ( key == Keys.PLUS )
		{
			gameStage.getWorldController().getWorldCamera().zoomIn();
		}
		
		// Zoom out
		//=========
		if ( key == Keys.MINUS )
		{
			gameStage.getWorldController().getWorldCamera().zoomOut();
		}
		
		// Return
		//=======
		return ( false );
	}
}
