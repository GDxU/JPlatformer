package com.sh.jplatformer.ui.stages;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.UiConstants;
import com.sh.jplatformer.ui.menus.PauseMenu;
import com.sh.jplatformer.ui.menus.RecordsMenu;
import com.sh.jplatformer.util.Time;
import com.sh.jplatformer.world.WorldCamera;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.WorldFile;
import com.sh.jplatformer.world.map.MapPopup;

/**
 * The {@code GameStage} provides an interface layer for the game mode.
 * @author Stefan Hösemann
 */

public class GameStage extends Stage
{
	// UI Components
	//==============
	private Table tbl_main;
	private Stack stk_menus;
	private Actor mnu_pause;
	private Actor mnu_records;
	
	// World
	//======
	private WorldController worldController;
	private WorldCamera worldCamera;

	// Popup labels
	//=============
	private Label lbl_popup;
	private Label lbl_time;
	private Vector2 origin;
	
	// Score board
	//============
	private int oldScore;
	private String scoreString;
	
	// Constructor
	//============
	/**
	 * @param newWorldController the {@code WorldController} that contains the world data.
	 */
	public GameStage( WorldController newWorldController )
	{
		// Viewport
		//=========
		super( new ScreenViewport() );

		// Menu setup
		//===========
		mnu_pause   = new PauseMenu  ( this );
		mnu_records = new RecordsMenu( this );
		
		// Add menus to stack
		//===================
		stk_menus = new Stack();
		stk_menus.add( mnu_pause );
		stk_menus.add( mnu_records );
		
		// Table setup
		//============
		tbl_main = new Table();
		tbl_main.setFillParent( true );
		tbl_main.add( stk_menus );

		this.addActor( tbl_main );
		
		// Hide menus
		//===========
		this.hideAllMenus();
		
		// Popup labels
		//=============
		lbl_popup = new Label( "", Resources.UI.skin, "popup" );
		lbl_time  = new Label( "", Resources.UI.skin, "popup_big" );
		origin    = new Vector2();
		
		// World
		//======
		worldController = newWorldController;
		worldCamera     = newWorldController.getWorldCamera();
	}
	
	// draw
	//=====
	@Override
	public void draw()
	{
		this.getBatch().begin();
		{
			// Popup labels
			//=============
			drawPopupLabels();
			
			// Game displays
			//==============
			if ( worldController.isLive() == true )
			{
				drawScoreBoard();
				drawTime();
			}
		}
		this.getBatch().end();
		super.draw();
	}
	
	// drawPopupLabels
	//================
	private void drawPopupLabels()
	{
		for ( MapPopup p : worldController.getPopups() )
		{
			// Set text + pack
			//================
			lbl_popup.setText( p.getText() );
			lbl_popup.pack();
			
			// Project position
			//=================
			origin.set( (int) ( p.getPosition().x - lbl_popup.getWidth() / 2f ),
			            (int) ( p.getPosition().y - lbl_popup.getHeight() / 2f ) );
			worldCamera.project( origin );
			
			// Draw
			//=====
			lbl_popup.setX( origin.x );
			lbl_popup.setY( origin.y );
			lbl_popup.draw( this.getBatch(), p.getAlpha() );
		}
	}
	
	// drawScoreBoard
	//===============
	private void drawScoreBoard()
	{
		// Temporary values
		//=================
		int digits   = 6;
		int newScore = worldController.getScore();
		
		// Apply zero score
		//=================
		if ( oldScore == 0 )
		{
			scoreString = "000000";
		}
		
		// Update score string
		//====================
		if ( oldScore != newScore )
		{
			// Limit score
			//============
			if ( newScore > 999999 )
			{
				newScore = 999999;
			}
			
			// Get objects / values
			//=====================
			scoreString = Integer.toString( newScore );
			oldScore    = newScore;
			int loops   = digits - scoreString.length();
			
			// Add leading zeros
			//==================
			if ( scoreString.length() < digits )
			{
				for ( int i = 0; i < loops; i++ )
				{
					scoreString = "0" + scoreString;
				}
			}
		}
		
		// Draw score board
		//=================
		if ( scoreString.length() < 10 && newScore >= 0 )
		{
			float w = Resources.UI.game_scoreBoard[0].getWidth();
			float h = Resources.UI.game_scoreBoard[0].getHeight();
			
			for ( int i = 0; i < scoreString.length(); i++ )
			{
				int idx = Integer.parseInt( scoreString.substring( i, i + 1 ) );
				Resources.UI.game_scoreBoard[idx].setX( +UiConstants.BORDER_BIG + i * w );
				Resources.UI.game_scoreBoard[idx].setY( -UiConstants.BORDER_BIG - h + getHeight() );
				Resources.UI.game_scoreBoard[idx].draw( this.getBatch() );
			}
		}
	}
	
	// drawTime
	//=========
	private void drawTime()
	{	
		lbl_time.setText( Time.toString( worldController.getElapsedTime() ) );
		lbl_time.pack();
		lbl_time.setX( getWidth()  - lbl_time.getWidth()  * 1f - UiConstants.BORDER_BIG );
		lbl_time.setY( getHeight() - lbl_time.getHeight() * 1f - UiConstants.BORDER_BIG );
		lbl_time.draw( this.getBatch(), 1f );
	}
	
	// hideAllMenus
	//=============
	private void hideAllMenus()
	{
		for ( Actor a : stk_menus.getChildren() )
		{	
			a.setVisible( false );
		}
	}
	
	// showRecordsMenu
	//================
	/**
	 * Sets the records menu visible and pauses the game.
	 */
	public void showRecordsMenu()
	{
		this.hideAllMenus();
		mnu_records.setVisible( true );
		JPlatformerGame.get().setPaused( true );
	}
		
	// showPauseMenu
	//==============
	public void showPauseMenu()
	{
		this.hideAllMenus();
		JPlatformerGame.get().setPaused( true );
		mnu_pause.setVisible( true );
	}
	
	// isPauseMenuVisible
	//===================
	public boolean isPauseMenuVisible()
	{
		return ( mnu_pause.isVisible() );
	}
	
	// isRecordsMenuVisible
	//=====================
	public boolean isRecordsMenuVisible()
	{
		return ( mnu_records.isVisible() );
	}
	
	// resumeGame
	//===========
	public void resumeGame()
	{
		this.hideAllMenus();
		JPlatformerGame.get().setPaused( false );
	}
	
	// restartGame
	//============
	public void restartGame()
	{
		WorldFile.reloadWorld( worldController );
		resumeGame();
	}
	
	// getWorldController
	//===================
	public WorldController getWorldController()
	{
		return ( worldController );
	}
	
	// resize
	//=======
	/**
	 * Updates the viewport on resize.
	 * @param width the new width.
	 * @param height the new height.
	 */
	public void resize( int width, int height )
	{
		this.getViewport().update( width, height, true );
	}
}