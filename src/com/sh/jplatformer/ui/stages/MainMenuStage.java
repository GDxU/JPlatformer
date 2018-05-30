package com.sh.jplatformer.ui.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.UiConstants;
import com.sh.jplatformer.ui.menus.SelectWorldMenu;
import com.sh.jplatformer.ui.menus.SettingsMenu;
import com.sh.jplatformer.ui.menus.StartMenu;

/**
 * The {@code MainMenuStage} provides is a stage for all main menu related UI components.
 * @author Stefan Hösemann
 */

public class MainMenuStage extends Stage
{
	// UI components
	//==============
	private Table tbl_main;
	private Stack stk_menus;
	private Actor mnu_startMenu;
	private Actor mnu_selectWorldMenu;
	private Actor mnu_settingsMenu;
	private Label lbl_copyright;
	
	// Back- / foreground
	//===================
	private float foregroundOffset;
	
	// Constructor
	//============
	public MainMenuStage()
	{
		// Viewport
		//=========
		super( new ScreenViewport() );
		
		// Menu setup
		//===========
		mnu_startMenu       = new StartMenu( this );
		mnu_selectWorldMenu = new SelectWorldMenu( this );
		mnu_settingsMenu    = new SettingsMenu( this );
		
		// Add menus to stack
		//===================
		stk_menus = new Stack();
		stk_menus.add( mnu_startMenu );
		stk_menus.add( mnu_selectWorldMenu );
		stk_menus.add( mnu_settingsMenu );
		
		// Table setup
		//============
		tbl_main = new Table();
		tbl_main.setFillParent( true );
		
		// Header space + settings button
		//===============================
		tbl_main.add( createQuitButton() ).height( Value.percentHeight( 0.3f, tbl_main ) )
		                                  .top()
		                                  .right()
		                                  .space( UiConstants.BORDER_BIG );
		tbl_main.row();
		
		// Body space
		//===========
		tbl_main.add( stk_menus ).height( Value.percentHeight( 0.6f, tbl_main )  )
		                         .bottom()
		                         .fill()
		                         .expand();
		this.addActor( tbl_main );
		
		// Initial menu
		//=============
		this.showStartMenu();
	}
	
	// draw
	//=====
	@Override
	public void draw()
	{
		this.getBatch().setColor( Color.WHITE );
		this.getBatch().begin();
		{
			drawBackground();
			drawForeground();
			drawLogo();
			drawCopyrightLabel();
		}
		this.getBatch().end();
		super.draw();
	}
	
	// createQuitButton
	//=================
	private Actor createQuitButton()
	{
		// Highlight button
		//=================
		ImageButton btn_quit = new ImageButton( new SpriteDrawable( Resources.UI.menu_icon_highlight_small ) );
		
		// Click listener
		// -> Highlight button when hovered
		//=================================
		btn_quit.addListener( new ClickListener()
		{
			// enter
			//======
			@Override
			public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor )
			{
				btn_quit.addAction( Actions.alpha( 1f, 0.2f ) );
			}
			
			// exit
			//======
			@Override
			public void exit( InputEvent event, float x, float y, int pointer, Actor fromActor )
			{
				btn_quit.addAction( Actions.alpha( 0f, 0.2f ) );
			}
			
		} );
		
		// Change listener
		// -> Quit game
		//================
		btn_quit.addListener( new ChangeListener()
		{
			// changed
			//========
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				Gdx.app.exit();
			}
		} );
		btn_quit.setColor( 1f, 1f, 1f, 0f );
		
		// Button table
		//=============
		Table icon_quit = new Table();
		icon_quit.background( new SpriteDrawable( Resources.UI.menu_icon_quit ) );
		
		// Create stack + add to new table
		//================================
		Table tbl_main = new Table();
		
		tbl_main.add( new Stack( btn_quit, icon_quit ) );
		tbl_main.top();
		tbl_main.pad( UiConstants.BORDER_BIG * 2f );
		
		return ( tbl_main );
	}
	
	// drawLogo
	//=========
	private void drawLogo()
	{
		// Temporary values
		//=================
		float w = this.getViewport().getScreenWidth();
		float h = this.getViewport().getScreenHeight();
		float x = w / 2f;
		float y = h - ( h / 3f ) / 1.35f;
		
		// Pick sprite size
		//=================
		Sprite sprite = Resources.UI.logo_game_large;
		
		if ( w < sprite.getWidth() || h / 2.5f < sprite.getHeight() )
		{
			sprite = Resources.UI.logo_game_small;
		}
		
		// Set position + draw
		//====================
		sprite.setX( (int) ( x - sprite.getWidth()  / 2f ) );
		sprite.setY( (int) ( y - sprite.getHeight() / 2f ) );
		sprite.draw( this.getBatch() );
	}
	
	// drawCopyrightLabel
	//===================
	private void drawCopyrightLabel()
	{
		// Create label
		//=============
		if ( lbl_copyright == null )
		{
			lbl_copyright = new Label( "", Resources.UI.skin, "popup" );
			lbl_copyright.setText( "(c) " + JPlatformerGame.YEARS + " " + JPlatformerGame.AUTHOR );
			lbl_copyright.setAlignment( Align.center );
			lbl_copyright.pack();
		}
		
		// Calculate position
		//===================
		lbl_copyright.setWidth ( lbl_copyright.getPrefWidth()  + 10f );
		lbl_copyright.setHeight( lbl_copyright.getPrefHeight() + 10f );
		lbl_copyright.setX     ( getCamera().viewportWidth - lbl_copyright.getWidth() - UiConstants.BORDER_BIG );
		lbl_copyright.setY     ( UiConstants.BORDER_BIG );
		
		// Draw label
		//===========
		lbl_copyright.draw( this.getBatch(), 1f );
	}
	
	// drawBackground
	//===============
	private void drawBackground()
	{
		// Update bounds + scale
		//======================
		Camera camera = this.getCamera();
		Sprite sprite = Resources.UI.menu_background;

		float w = camera.viewportWidth;
		float h = sprite.getHeight() * ( w / sprite.getWidth() );
		
		// Adapt size
		//===========
		if ( camera.viewportHeight > h )
		{
			h = camera.viewportHeight;
			w = sprite.getWidth() * ( h / sprite.getHeight() );
		}

		// Draw sprite
		//============
		sprite.setOrigin( 0f, 0f );
		sprite.setBounds( 0f, 0f, w, h );
		sprite.draw     ( this.getBatch() );
	}
	
	// drawForeground
	//===============
	private void drawForeground()
	{
		// Get sprite dimensions
		//======================
		int w = (int) Resources.UI.menu_foreground.getWidth() / 2;
		int h = (int) Resources.UI.menu_foreground.getHeight();
		
		// Update offset
		//==============
		foregroundOffset += Gdx.graphics.getDeltaTime() * 40f;
		if ( foregroundOffset > w  ) foregroundOffset = 0f;
		if ( foregroundOffset < 0f ) foregroundOffset = w;
		
		// Draw foreground
		//================
		for ( int i = 0; i < this.getViewport().getScreenWidth() / w + 1; i++ )
		{
			this.getBatch().draw( Resources.UI.texture_menu_foreground,
			                      i * w, 0f, w, h,
			                      (int) foregroundOffset, 0, w, h,
			                      false, false );
		}
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
	
	// showStartMenu
	//==============
	public void showStartMenu()
	{
		this.hideAllMenus();
		mnu_startMenu.setVisible( true );
	}
	
	// showSelectWorldMenu
	//====================
	public void showSelectWorldMenu()
	{
		this.hideAllMenus();
		mnu_selectWorldMenu.setVisible( true );
	}
	
	// showSettingsMenu
	//=================
	public void showSettingsMenu()
	{
		this.hideAllMenus();
		mnu_settingsMenu.setVisible( true );
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