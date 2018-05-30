package com.sh.jplatformer.ui.menus;

import java.io.File;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.sh.jplatformer.Config;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.*;
import com.sh.jplatformer.ui.stages.MainMenuStage;
import com.sh.jplatformer.util.FileUtils;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.util.Task;
import com.sh.jplatformer.util.Time;
import com.sh.jplatformer.world.WorldFile;
import com.sh.jplatformer.world.map.Map;

/**
 * The {@code SelectWorldMenu} provides a world selection menu.
 * @author Stefan Hösemann
 */

public class SelectWorldMenu extends Table
{
	// UI components
	//==============
	private MainMenuStage mainMenuStage;
	private Skin skin;
	
	private FormList<String> lst_worldFiles;
	private String[] worldPaths;
	private String[] worldNames;
	
	private Label lbl_map_title;
	private Label lbl_map_creator;
	private Label lbl_map_difficulty;
	private Label lbl_map_size;
	private Label lbl_map_time;
	private Label lbl_map_records;
	private Label lbl_map_highScore;
	private Label lbl_map_bestTime;
	
	// Constructor
	//============
	/**
	 * @param mainMenuStage the {@code MainMenuStage} to interact with.
	 */
	public SelectWorldMenu( MainMenuStage mainMenuStage )
	{
		// Assignments
		//============
		this.mainMenuStage = mainMenuStage;
		this.skin = Resources.UI.skin;

		// Create components
		//==================
		Actor pane = new FormPane( createMenu(), skin, "menu" );
		this.add( pane ).center().width( Value.percentWidth ( 0.4f, this ) );
	}
	
	// createMenu
	//===========
	private Actor createMenu()
	{		
		// Table setup
		//============
		Table tbl_main = new Table();
		
		tbl_main.top().pad( UiConstants.BORDER_SMALL );
		tbl_main.defaults().space( UiConstants.BORDER_SMALL );
		
		// Window heading setup
		//=====================
		Label lbl_heading = new Label( Lang.txt( "menu_selectWorld_heading" ), skin, "heading_menu" );
		lbl_heading.setAlignment( Align.center );
		
		// Window heading
		//===============
		tbl_main.add( lbl_heading ).top().expandX().fillX().pad( 0f ).colspan( 2 );
		tbl_main.row();
		
		// Create panes
		///============
		ScrollPane pne_files = createFilesPane();
		ScrollPane pne_info  = createInfoPane();
		
		// Panels
		//=======
		tbl_main.add( pne_files ).fill().height( pne_info.getPrefHeight() );
		tbl_main.add( pne_info  ).fill();
		tbl_main.row();
		
		// Buttons
		//========
		tbl_main.add( createButtonBar() ).bottom().right().space( 0f ).pad( 0f ).colspan( 2 );
		
		return ( tbl_main );
	}
	
	// createFilesPane
	//================
	private ScrollPane createFilesPane()
	{
		// Create files list
		//==================
		lst_worldFiles = new FormList<String>( skin );
		lst_worldFiles.addListener( new ClickListener()
		{
			@Override
			public void clicked( InputEvent event, float x, float y )
			{
				updateInfoPane();
			}
		} );
		
		// Wrap in pane
		//=============
		return ( new SidebarScrollPane( lst_worldFiles, skin, "menu" ) );
	}
	
	// createInfoPane
	//===============
	private ScrollPane createInfoPane()
	{
		// Create components
		//==================
		Table tbl_info = new Table();
		tbl_info.defaults().left();
		tbl_info.top().left().padRight( 100f );
		
		// Create labels
		//==============
		lbl_map_title      = new Label( "", skin, "default_medium");
		lbl_map_creator    = new Label( "", skin, "default" );
		lbl_map_difficulty = new Label( "", skin, "default" );
		lbl_map_size       = new Label( "", skin, "default" );
		lbl_map_time       = new Label( "", skin, "default" );
		lbl_map_records    = new Label( "", skin, "heading_medium" );
		lbl_map_highScore  = new Label( "", skin, "heading");
		lbl_map_bestTime   = new Label( "", skin, "heading");
		
		// Gap labels
		//===========
		Label lbl_gap1 = new Label( "", skin );
		Label lbl_gap2 = new Label( "", skin );
		Label lbl_gap3 = new Label( "", skin );
		
		// Add labels
		//===========
		tbl_info.add( lbl_map_title)      .width( 0f ).row();
		tbl_info.add( lbl_map_creator )   .width( 0f ).row();
		tbl_info.add( lbl_gap1 )          .width( 0f ).row();
		tbl_info.add( lbl_map_difficulty ).width( 0f ).row();
		tbl_info.add( lbl_map_size )      .width( 0f ).row();
		tbl_info.add( lbl_map_time )      .width( 0f ).row();
		tbl_info.add( lbl_gap2 )          .width( 0f ).row();
		tbl_info.add( lbl_map_records )   .width( 0f ).row();
		tbl_info.add( lbl_map_highScore ) .width( 0f ).row();
		tbl_info.add( lbl_map_bestTime )  .width( 0f ).row();
		tbl_info.add( lbl_gap3 )          .width( 0f ).row();
		
		// Wrap in pane
		//=============
		return ( new FormPane( tbl_info, skin, "menu" ) );
	}
	
	// createButtonBar
	//================
	private Actor createButtonBar()
	{
		// Button: Back
		//=============
		FormButton btn_back = new FormButton( Lang.txt( "menu_selectWorld_back" ), skin, "menu"  );
		btn_back.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				mainMenuStage.showStartMenu();
			}
		} );
		
		// Button: Play
		//=============
		FormButton btn_play = new FormButton( Lang.txt( "menu_selectWorld_play" ), skin, "menu" );
		btn_play.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				Config.get().tmp_worldPath = worldPaths[lst_worldFiles.getSelectedIndex()];
				JPlatformerGame.get().getTransition().transition( new Task()
				{
					@Override
					public void perform()
					{
						JPlatformerGame.get().setScreen( JPlatformerGame.get().gameScreen );
					}
				} );
			}
		} );
		
		// Equal size
		//===========
		float w = 0f;
		if ( btn_back.getPrefWidth() > btn_play.getPrefWidth() ) w = btn_back.getPrefWidth();
		if ( btn_play.getPrefWidth() > btn_back.getPrefWidth() ) w = btn_play.getPrefWidth();
		
		// Create table
		//=============
		Table tbl_buttons = new Table();
		
		tbl_buttons.add( btn_back ).padLeft( UiConstants.BORDER_BIG ).minWidth( w );
		tbl_buttons.add( btn_play ).padLeft( UiConstants.BORDER_BIG ).minWidth( w );
		tbl_buttons.pack();
		
		return ( tbl_buttons );
	}
	
	// updateFileList
	//===============
	private void updateFileList()
	{
		// Get files and names
		//====================
		File[] files = FileUtils.listFiles( FileUtils.getRoot() + WorldFile.FILE_DIR,
		                                    WorldFile.FILE_EXTENSION );
		worldNames = new String[files.length];
		worldPaths = new String[files.length];
		
		for ( int i = 0; i < files.length; i++ )
		{
			worldNames[i] = files[i].getName();
			worldPaths[i] = files[i].getAbsolutePath();
		}
		
		// Save selected index
		//====================
		int idx_load = lst_worldFiles.getSelectedIndex();
		if ( idx_load < 0 ) idx_load = 0;
		
		// Update list
		//============
		lst_worldFiles.clearItems();
		lst_worldFiles.setItems( worldNames );
		lst_worldFiles.setSelectedIndex( lst_worldFiles.getItems().size - 1 );
		
		if ( lst_worldFiles.getItems().size > idx_load )
		{
			lst_worldFiles.setSelectedIndex( idx_load );
		}
	}
	
	// updateInfoPane
	//===============
	/**
	 * Updates the world info overview pane based on the world file that is currently selected.
	 */
	private void updateInfoPane()
	{
		// Check selected item
		//====================
		if ( lst_worldFiles.getSelected() != null )
		{
			// Read meta info
			//===============
			Map map = WorldFile.loadWorldMetaInformation( worldPaths[lst_worldFiles.getSelectedIndex()] );
			
			// Get time string
			//================
			String timeString = Lang.txt( "menu_selectWorld_none" );
			
			if ( map.getCountdownTime() != Map.COUNTDOWN_DISABLED )
			{
				timeString = Time.toString( map.getCountdownTime() );
			}
			
			// Prepare strings
			//================
			String title      = map.getTitle();
			String creator    = Lang.txt( "menu_selectWorld_by"         ) + " " + map.getCreator();
			String difficulty = Lang.txt( "menu_selectWorld_difficulty" ) + " " + map.getDifficulty();
			String size       = Lang.txt( "menu_selectWorld_size"       ) + " " + map.getColumns() + " x " + map.getRows();
			String highScore  = Lang.txt( "menu_selectWorld_highScore"  ) + " " + map.getHighScore();
			String bestTime   = Lang.txt( "menu_selectWorld_bestTime"   ) + " " + Time.toString( map.getBestTime() );
			String timeLimit  = Lang.txt( "menu_selectWorld_timeLimit"  ) + " " + timeString;
			
			// Update labels
			//==============
			lbl_map_title     .setText( title );
			lbl_map_creator   .setText( creator );
			lbl_map_difficulty.setText( difficulty );
			lbl_map_size      .setText( size );
			lbl_map_time      .setText( timeLimit );
			lbl_map_records   .setText( Lang.txt( "menu_selectWorld_records" ) );
			lbl_map_highScore .setText( highScore );
			lbl_map_bestTime  .setText( bestTime );
		}
		else
		{
			// Clear all labels
			//=================
			lbl_map_title     .setText( "" );
			lbl_map_creator   .setText( "" );
			lbl_map_difficulty.setText( "" );
			lbl_map_size      .setText( "" );
			lbl_map_time      .setText( "" );
			lbl_map_records   .setText( "" );
			lbl_map_highScore .setText( "" );
			lbl_map_bestTime  .setText( "" );
		}
	}
	
	// setVisible
	//===========
	@Override
	public void setVisible( boolean visible )
	{
		super.setVisible( visible );
		if ( visible )
		{
			this.updateFileList();
			this.updateInfoPane();
		}
	}
}