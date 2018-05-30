package com.sh.jplatformer.ui.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.FormButton;
import com.sh.jplatformer.ui.components.FormPane;
import com.sh.jplatformer.ui.components.UiConstants;
import com.sh.jplatformer.ui.stages.GameStage;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.util.Task;
import com.sh.jplatformer.world.WorldController;

/**
 * The {@code PauseMenu} shows up if the game is paused in the {@code GameScreen}.
 * @author Stefan Hösemann
 */

public class PauseMenu extends Table
{
	// UI Components
	//==============
	private GameStage gameStage;
	private Skin skin;
	
	// Constructor
	//============
	/**
	 * @param gameStage the {@code GameStage} to interact with.
	 */
	public PauseMenu( GameStage gameStage )
	{
		// Assignments
		//============
		this.gameStage = gameStage;
		this.skin = Resources.UI.skin;
		
		// Create components
		//==================
		Actor pane = new FormPane( createMenu(), skin, "menu" );
		this.add( pane );
	}
	
	// createMenu
	//===========
	private Actor createMenu()
	{
		// Button: Resume
		//===============
		FormButton btn_resume = new FormButton( Lang.txt( "menu_pause_resume" ), skin, "menu" );
		btn_resume.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				gameStage.resumeGame();
			}
		} );
		
		// Button: Restart
		//================
		FormButton btn_restart = new FormButton( Lang.txt( "menu_pause_restart" ), skin, "menu" );
		btn_restart.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				gameStage.getWorldController().setWorldState( WorldController.STATE_RESTART );
			}
		} );
		
		// Button: Leave
		//==============
		FormButton btn_leave = new FormButton( Lang.txt( "menu_pause_backToMenu" ), skin, "menu" );
		btn_leave.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
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
		} );
		
		// Table setup
		//============
		Table tbl_main = new Table();
		
		tbl_main.top().pad( UiConstants.BORDER_SMALL );
		tbl_main.defaults().space( UiConstants.BORDER_SMALL );
		
		// Window heading setup
		//=====================
		Label lbl_heading = new Label( Lang.txt( "menu_pause_heading" ), skin, "heading_menu" )
		{
			@Override
			public float getMinWidth()
			{
				return ( 300f );
			}
		};
		lbl_heading.setAlignment( Align.center );
		
		// Window heading
		//===============
		tbl_main.add( lbl_heading ).top().expandX().fillX();
		tbl_main.row();
		
		// Buttons
		//========
		tbl_main.add( btn_resume  ).expand().fill().height( btn_resume .getPrefHeight() + 8f );
		tbl_main.row();
		tbl_main.add( btn_restart ).expand().fill().height( btn_restart.getPrefHeight() + 8f );
		tbl_main.row();
		tbl_main.add( btn_leave   ).expand().fill().height( btn_leave  .getPrefHeight() + 8f );
		tbl_main.row();
		
		return ( tbl_main );
	}
}