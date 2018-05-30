package com.sh.jplatformer.ui.editor.sidebar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.sh.jplatformer.Config;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.FormButton;
import com.sh.jplatformer.ui.components.FormCheckBox;
import com.sh.jplatformer.ui.components.FormPane;
import com.sh.jplatformer.ui.components.SidebarScrollPane;
import com.sh.jplatformer.ui.components.SidebarTable;
import com.sh.jplatformer.ui.components.TabPane;
import com.sh.jplatformer.ui.components.TabPaneButton;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.util.Task;

/**
 * The {@code SystemMenu} provides a menu for general tasks.
 * @author Stefan Hösemann
 */

public class SystemMenu extends Table
{
	// Fields
	//=======
	private Skin skin;
	
	private TabPane pne_tabs;
	private Actor mnu_main;
	private Actor mnu_settings;
	
	private FormCheckBox chk_showFps;
	private FormCheckBox chk_showPowerInfo;
	
	// Constructor
	//============
	public SystemMenu()
	{
		// Skin
		//=====
		skin = Resources.UI.skin;
		
		// Create sub menus
		//=================
		mnu_main     = new SidebarScrollPane( this.createMainMenu(), skin, "transparent" );
		mnu_settings = new SidebarScrollPane( this.createSettingsMenu(), skin, "transparent" )
		{
			// setVisible
			//===========
			@Override
			public void setVisible( boolean value )
			{
				if ( value == true )
				{
					updateComponents();
				}
				super.setVisible( value );
			}
		};
		
		// Create tab pane
		//================
		pne_tabs = new TabPane();
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_sys_menu, skin, "toggle" ),
		                 mnu_main );
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_sys_settings, skin, "toggle" ),
		                 mnu_settings );
		
		// Wrap in form pane
		//==================
		this.add( new FormPane( pne_tabs, skin ) ).top().expand().fill();
	}
	
	// createMainMenu
	//===============
	private Actor createMainMenu()
	{
		// Button: Exit editor
		//====================
		FormButton btn_exitEditor = new FormButton( Lang.txt( "editor_system_returnToMenu" ), skin );
		btn_exitEditor.addListener( new ChangeListener()
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
		
		// Button: Exit program
		//=====================
		FormButton btn_exitProgram = new FormButton( Lang.txt( "editor_system_quit" ), skin );
		btn_exitProgram.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				Gdx.app.exit();
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_system_menu" ), skin, "heading" ) ).left();
		tbl_main.row();
		
		// Label
		//======
		tbl_main.add( new Label( Lang.txt( "editor_system_subline" ), skin ) ).left();
		tbl_main.row();
		
		// Buttons
		//========
		tbl_main.add( btn_exitEditor ).fillX();
		tbl_main.row();
		tbl_main.add( btn_exitProgram ).fillX();
		tbl_main.row();
		
		// Note label
		//===========
		tbl_main.add( new Label( "\n" + Lang.txt( "editor_system_note" ), skin, "heading" ) ).left();
		tbl_main.row();
		
		// Note label setup
		//=================
		Label lbl_hint = new Label( Lang.txt( "editor_system_noteText" ), skin );
		lbl_hint.setWrap( true );
		
		// Note label
		//===========
		tbl_main.add( lbl_hint ).left();
		tbl_main.row();
		
		return ( tbl_main );
	}
	
	// createSettingsMenu
	//===================
	private Actor createSettingsMenu()
	{
		// Fps check box
		//==============
		chk_showFps = new FormCheckBox( "", skin );
		chk_showFps.setChecked( Config.get().editor_showFps );
		
		// Power info check box
		//=====================
		chk_showPowerInfo = new FormCheckBox( "", skin );
		chk_showPowerInfo.setChecked( Config.get().editor_showPowerInfo );
		
		// Button: Apply
		//==============
		FormButton btn_apply = new FormButton( Lang.txt( "editor_system_apply" ), skin );
		btn_apply.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				Config.get().editor_showFps       = chk_showFps.isChecked();
				Config.get().editor_showPowerInfo = chk_showPowerInfo.isChecked();
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		tbl_main.columnDefaults( 0 ).width( Value.percentWidth( 0.00f, tbl_main ) );
		tbl_main.columnDefaults( 1 ).width( Value.percentWidth( 0.00f, tbl_main ) );
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_system_settings" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();
		
		// Show fps
		//=========
		tbl_main.add( new Label( Lang.txt( "editor_system_showFps" ), skin ) ).left();
		tbl_main.add( chk_showFps ).right();
		tbl_main.row();
		
		// Show power info
		//================
		tbl_main.add( new Label( Lang.txt( "editor_system_showPowerInfo" ), skin ) ).left();
		tbl_main.add( chk_showPowerInfo ).right();
		tbl_main.row();
		
		// Apply button
		//=============
		tbl_main.add( btn_apply ).colspan( 2 ).right().width( btn_apply.getPrefWidth() );
		
		return ( tbl_main );
	}
		
	// updateComponents
	//=================
	private void updateComponents()
	{
		chk_showFps.setChecked( Config.get().editor_showFps );
		chk_showPowerInfo.setChecked( Config.get().editor_showPowerInfo );
	}
}