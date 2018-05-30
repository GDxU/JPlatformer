package com.sh.jplatformer.ui.editor.sidebar;

import java.io.File;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.FormButton;
import com.sh.jplatformer.ui.components.FormCheckBox;
import com.sh.jplatformer.ui.components.FormPane;
import com.sh.jplatformer.ui.components.FormSelectBox;
import com.sh.jplatformer.ui.components.SidebarScrollPane;
import com.sh.jplatformer.ui.components.SidebarTable;
import com.sh.jplatformer.ui.components.TabPane;
import com.sh.jplatformer.ui.components.TabPaneButton;
import com.sh.jplatformer.ui.stages.EditorStage.EditMode;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.util.Time;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;

/**
 * The {@code MapMenu} provides a tabbed menu structure for modifying the {@code Map}.
 * @author Stefan Hösemann
 */

public class MapMenu extends Table
{
	// Fields
	//=======
	private WorldController worldController;
	private EditMode editMode;
	private int selectedWaterTile;
	private int selectedWaterSpeedId;
	private int selectedMapTile;
	
	// UI components
	//==============
	private Skin skin;
	
	private TabPane pne_tabs;
	
	private Actor mnu_environment;
	private Actor mnu_tileSelection;
	private Actor mnu_conditions;
	
	private FormSelectBox<String> slc_background;
	private FormSelectBox<String> slc_foreground;
	private FormSelectBox<String> slc_waterSpeed;
	private FormSelectBox<String> slc_environmentSound;
	private FormSelectBox<String> slc_difficulty;
	
	private CheckBox chk_countdown;
	
	private TextField txt_min;
	private TextField txt_sec;
	private TextField txt_title;
	private TextField txt_creator;
	
	private Label lbl_highScore;
	private Label lbl_bestTime;
	
	private ButtonGroup<Button> btn_grp_waterButtons;
	
	// Water speeds
	//=============
	private String[] waterSpeeds =
		{
			"-2",
			"-1",
			"0",
			"+1",
			"+2"
		};
	
	// Constructor
	//============
	/**
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public MapMenu( WorldController newWorldController )
	{
		// Setup
		//======
		worldController = newWorldController;
		skin = Resources.UI.skin;
		editMode = EditMode.NONE;
		
		// Create sub menus
		//=================
		mnu_environment   = new SidebarScrollPane( this.createEnvironmentMenu(), skin, "transparent" );
		mnu_tileSelection = new SidebarScrollPane( this.createTileMenu(),        skin, "transparent" );
		mnu_conditions    = new SidebarScrollPane( this.createConditionsMenu(),  skin, "transparent" );
		
		// Create tab pane
		//================
		pne_tabs = new TabPane();
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_map_environment, skin, "toggle" ),
		                 mnu_environment );
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_map_tiles, skin, "toggle" ),
		                 mnu_tileSelection );
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_map_conditions, skin, "toggle" ),
		                 mnu_conditions );
		
		// Change listener
		//================
		pne_tabs.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				// Update edit mode
				//=================
				Actor content = pne_tabs.getVisibleContent();
				
				if ( content == mnu_environment )   editMode = EditMode.ENVIRONMENT;
				if ( content == mnu_tileSelection ) editMode = EditMode.CELLS;
				if ( content == mnu_conditions )    editMode = EditMode.TRIGGERS;
			}
		} );
		
		// Wrap in form pane
		//==================
		this.add( new FormPane( pne_tabs, skin ) ).top().expand().fill();
		
		// Update components
		//==================
		this.updateComponents();
	}
	
	// createEnvironmentMenu
	//======================
	private Actor createEnvironmentMenu()
	{
		// Title text field
		//=================
		txt_title = new TextField( "", skin );
		txt_title.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				worldController.getMap().setTitle( txt_title.getText() );
			}
		} );
		
		// Creator text field
		//===================
		txt_creator = new TextField( "", skin );
		txt_creator.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				worldController.getMap().setCreator( txt_creator.getText() );
			}
		} );
		
		// Difficulty select box
		//======================
		slc_difficulty = new FormSelectBox<String>( skin );
		slc_difficulty.setItems( Map.DIFFICULTIES );
		slc_difficulty.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				worldController.getMap().setDifficultyId( slc_difficulty.getSelectedIndex() );
			}
		} );
		
		// Background select box
		//======================
		slc_background = new FormSelectBox<String>( skin );
		slc_background.setItems( Resources.WORLD.availableBackgroundTextures );
		slc_background.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				int idx = slc_background.getSelectedIndex();
				String file = new File( Resources.WORLD.availableBackgroundTextures[idx] ).getName();
				worldController.getMap().setBackgroundFile( file );
			}
		} );
		
		// Foreground select box
		//======================
		slc_foreground = new FormSelectBox<String>( skin );
		slc_foreground.setItems( Resources.WORLD.availableForegroundTextures );
		slc_foreground.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				int idx = slc_foreground.getSelectedIndex();
				String file = new File( Resources.WORLD.availableForegroundTextures[idx] ).getName();
				worldController.getMap().setForegroundFile( file );
			}
		} );
		
		// Water speed select box
		//=======================
		slc_waterSpeed = new FormSelectBox<String>( skin );
		slc_waterSpeed.setItems( waterSpeeds );
		slc_waterSpeed.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				int slc = slc_waterSpeed.getSelectedIndex();
				
				if ( slc >= 0 && slc < Map.WATER_SPEEDS.length )
				{
					selectedWaterSpeedId = slc;
					worldController.getMap().setWaterSpeedId( slc );
				}
			}
		} );
		
		// Environment sound select box
		//=============================
		slc_environmentSound = new FormSelectBox<String>( skin );
		slc_environmentSound.setItems( Resources.WORLD.availableEnvironmentSounds );
		slc_environmentSound.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				int idx = slc_environmentSound.getSelectedIndex();
				String file = new File( Resources.WORLD.availableEnvironmentSounds[idx] ).getName();
				worldController.getMap().setEnvironmentSoundFile( file );
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		tbl_main.columnDefaults( 0 ).width( Value.percentWidth( 0.00f, tbl_main ) );
		tbl_main.columnDefaults( 1 ).width( Value.percentWidth( 0.40f, tbl_main ) );
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_map_description" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();
		
		// Title
		//======
		tbl_main.add( new Label( Lang.txt( "editor_map_worldName" ), skin ) ).left();
		tbl_main.add( txt_title ).right();
		tbl_main.row();
		
		// Creator
		//========
		tbl_main.add( new Label( Lang.txt( "editor_map_creator" ), skin ) ).left();
		tbl_main.add( txt_creator ).right();
		tbl_main.row();
		
		// Difficulty select box
		//======================
		tbl_main.add( new Label( Lang.txt( "editor_map_difficulty" ), skin ) ).left();
		tbl_main.add( slc_difficulty ).right();
		tbl_main.row();
		
		// Heading
		//========
		tbl_main.add( new Label( "\n" + Lang.txt( "editor_map_environment" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();

		// Background selection
		//=====================
		tbl_main.add( new Label( Lang.txt( "editor_map_background" ), skin ) ).left();
		tbl_main.add( slc_background ).right();
		tbl_main.row();
		
		// Foreground selection
		//=====================
		tbl_main.add( new Label( Lang.txt( "editor_map_foreground" ), skin ) ).left();
		tbl_main.add( slc_foreground ).right();
		tbl_main.row();
		
		// Environment sound
		//==================
		tbl_main.add( new Label( Lang.txt( "editor_map_environmentSound" ), skin ) ).left();
		tbl_main.add( slc_environmentSound ).right();
		tbl_main.row();
		
		// Heading
		//========
		tbl_main.add( new Label( "\n" + Lang.txt( "editor_map_waterProperties" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();

		// Water selection
		//================
		tbl_main.add( new Label( Lang.txt( "editor_map_waterSpeed" ), skin ) ).left();
		tbl_main.row();
		tbl_main.add( slc_waterSpeed ).colspan( 2 ).left();
		tbl_main.row();

		// Button pane
		//============
		tbl_main.add( new Label( Lang.txt( "editor_map_waterSet" ), skin ) ).colspan( 2 ).left();
		tbl_main.row();
		tbl_main.add( new SidebarScrollPane( this.createWaterTilesTable().left(), skin, "dark" ) ).colspan( 2 );
		tbl_main.row();
		
		// Label setup
		//============
		Label lbl_hint1 = new Label( Lang.txt( "editor_map_waterHint" ), skin, "default" );
		lbl_hint1.setWrap( true );
		
		// Hint label
		//===========
		tbl_main.add( lbl_hint1 ).colspan( 2 ).left();
		tbl_main.row();
		
		return ( tbl_main );
	}
	
	// createTileMenu
	//===============
	private Actor createTileMenu()
	{
		// Table setup
		//============
		Table tbl_main = new SidebarTable();

		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_map_editTiles" ), skin, "heading" ) ).left();
		tbl_main.row();
		
		// Label setup
		//============
		Label lbl_hint1 = new Label( Lang.txt( "editor_map_tilesHint" ), skin, "default" );
		lbl_hint1.setWrap( true );
		
		// Button pane
		//============
		tbl_main.add( lbl_hint1 ).left();
		tbl_main.row();
		tbl_main.add( new SidebarScrollPane( this.createMapTilesTable().left(), skin, "dark" ) );
		
		return ( tbl_main );
	}
	
	// createConditionsMenu
	//=====================
	private Actor createConditionsMenu()
	{
		// Text fields
		//============
		txt_min = new TextField( "", skin );
		txt_sec = new TextField( "", skin );
		
		txt_min.setTextFieldFilter( new TextFieldFilter.DigitsOnlyFilter() );
		txt_sec.setTextFieldFilter( new TextFieldFilter.DigitsOnlyFilter() );
		
		txt_min .setMaxLength( 2 );
		txt_sec .setMaxLength( 2 );
		
		// Countdown check box
		//====================
		chk_countdown = new FormCheckBox( "", skin );
		chk_countdown.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				applyCountdownTime();
			}
		} );
		chk_countdown.fire( new ChangeListener.ChangeEvent() );
		
		// Records labels
		//===============
		lbl_highScore = new Label( "", skin );
		lbl_highScore.setAlignment( Align.right );
		
		lbl_bestTime = new Label( "", skin );
		lbl_bestTime.setAlignment( Align.right );
		
		// Reset button
		//=============
		FormButton btn_reset = new FormButton( Lang.txt( "editor_map_resetRecords" ), skin );
		btn_reset.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{
				// Reset + update
				//===============
				worldController.getMap().setHighScore( 0 );
				worldController.getMap().setBestTime( 0L );
				
				updateComponents();
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		tbl_main.columnDefaults( 1 ).expand( false, false );
		tbl_main.columnDefaults( 2 ).expand( false, false );
		
		// Limit "seconds" value in text field
		//====================================
		tbl_main.addListener( new InputListener()
		{
			@Override
			public boolean keyTyped( InputEvent event, char keycode )
			{
				try
				{
					if ( Integer.parseInt( txt_sec.getText() ) > 59 )
					{
						txt_sec.setText( "59" );
						txt_sec.setCursorPosition( "59".length() );
					}
					applyCountdownTime();
				}
				catch ( Exception e ) {}
				return ( false );
			}
		} );
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_map_setTimeLimit" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();
		
		// Show object IDs
		//================
		tbl_main.add( new Label( Lang.txt( "editor_map_enableCountdown" ), skin ) ).colspan( 2 ).left();
		tbl_main.add( chk_countdown ).right();
		tbl_main.row();
		
		// Countdown text fields
		//======================
		tbl_main.add( new Label( Lang.txt( "editor_map_timeLimit" ), skin ) ).left();
		tbl_main.add( txt_min ).width( 40f ).right();
		tbl_main.add( txt_sec ).width( 40f ).right();
		tbl_main.row();
		
		// Heading
		//========
		tbl_main.add( new Label( "\n" + Lang.txt( "editor_map_triggers" ), skin, "heading" ) ).left();
		tbl_main.row();
		
		// Labels setup
		//=============
		Label lbl_hint1 = new Label( Lang.txt( "editor_map_startHint" ),  skin, "default" );
		Label lbl_hint2 = new Label( Lang.txt( "editor_map_finishHint" ), skin, "default" );

		lbl_hint1.setWrap( true );
		lbl_hint2.setWrap( true );
		
		// Hints: Create tables
		//=====================
		Table tbl_startTrigger  = new Table();
		Table tbl_finishTrigger = new Table();
		Table tbl_mouseLeft     = new Table();
		Table tbl_mouseRight    = new Table();
		
		tbl_mouseLeft .setBackground( new SpriteDrawable( Resources.UI.editor_icon_leftMouse ) );
		tbl_mouseRight.setBackground( new SpriteDrawable( Resources.UI.editor_icon_rightMouse ) );
		
		// Hints: Tables setup
		//====================
		tbl_startTrigger .add( tbl_mouseLeft );
		tbl_startTrigger .add( lbl_hint1 ).expand().fill().spaceLeft( 20f );
		tbl_finishTrigger.add( tbl_mouseRight );
		tbl_finishTrigger.add( lbl_hint2 ).expand().fill().spaceLeft( 20f );
		
		// Hints: Start area
		//==================
		tbl_main.add( tbl_startTrigger ).colspan( 3 );
		tbl_main.row();
		
		// Hints: Finish area
		//===================
		tbl_main.add( tbl_finishTrigger ).colspan( 3 );
		tbl_main.row();
		
		// Heading
		//========
		tbl_main.add( new Label( "\n" + Lang.txt( "editor_map_records" ), skin, "heading" ) ).colspan( 3 ).left();
		tbl_main.row();
		
		// High score
		//===========
		tbl_main.add( new Label( Lang.txt( "editor_map_highScore" ), skin ) ).left();
		tbl_main.add( lbl_highScore ).colspan( 2 ).right();
		tbl_main.row();
		
		// Best time
		//==========
		tbl_main.add( new Label( Lang.txt( "editor_map_bestTime" ), skin ) ).left();
		tbl_main.add( lbl_bestTime ).colspan( 2 ).right();
		tbl_main.row();
		
		// Reset button
		//=============
		tbl_main.add( btn_reset ).colspan( 3 ).right().width( btn_reset.getPrefWidth() );
		
		return ( tbl_main );
	}
		
	// createMapTilesTable
	//====================
	private Table createMapTilesTable()
	{
		// Temporary values
		//=================
		Table tbl_buttons               = new Table();
		ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>();
		int cols                        = 4;
		int size                        = ( int ) ( SidebarWindow.WIDTH * 0.83f ) / cols;
		
		// Create table
		//=============
		for ( int i = 0; i < Resources.WORLD.tiles.length; i++ )
		{
			// Button setup
			//=============
			Button btn = new Button( skin, "tabpane" );
			Image  img = new Image( Resources.WORLD.tiles[i][0] );
			
			btn.pad( 6f );
			btn.add( img ).left();

			// Add listener
			//=============
			btn.addListener( new ClickListener()
			{
				@Override
				public void clicked( InputEvent event, float x, float y )
				{
					selectedMapTile = buttonGroup.getCheckedIndex();
					Resources.UI.sound_click2.play();
				}
			} );
			
			// Add to group + table
			//=====================
			buttonGroup.add( btn );	
			tbl_buttons.add( btn ).size( size );
			
			// Set next row
			//=============
			if ( ( i + 1 ) % cols == 0 ) tbl_buttons.row();
		}
		return ( tbl_buttons );
	}
	
	// createWaterTilesTable
	//======================
	private Table createWaterTilesTable()
	{
		// Temporary values
		//=================
		Table tbl_buttons    = new Table();
		btn_grp_waterButtons = new ButtonGroup<Button>();
		int cols             = 4;
		int size             = ( int ) ( SidebarWindow.WIDTH * 0.83f ) / cols;
		
		// Create table
		//=============
		for ( int i = 0; i < Resources.WORLD.water.length; i++ )
		{
			// Button setup
			//=============
			Button btn = new Button( skin, "tabpane" );
			Image  img = new Image( Resources.WORLD.water[i] );
			
			btn.pad( 6f );
			btn.add( img ).left();

			// Add listener
			//=============
			btn.addListener( new ClickListener()
			{
				@Override
				public void clicked( InputEvent event, float x, float y )
				{
					selectedWaterTile = btn_grp_waterButtons.getCheckedIndex();
					worldController.getMap().setWaterId( btn_grp_waterButtons.getCheckedIndex() );
					Resources.UI.sound_click2.play();
				}
			} );
			
			// Add to group + table
			//=====================
			btn_grp_waterButtons.add( btn );	
			tbl_buttons         .add( btn ).size( size );
			
			// Set next row
			//=============
			if ( ( i + 1 ) % cols == 0 ) tbl_buttons.row();
		}
		return ( tbl_buttons );
	}
	
	// applyCountdownTime
	//===================
	private void applyCountdownTime()
	{
		// Countdown is enabled
		//=====================
		if ( chk_countdown.isChecked() == true )
		{
			// Disable text fields
			//====================
			txt_min.setDisabled( false );
			txt_sec.setDisabled( false );
			
			// Catch invalid string
			//=====================
			if ( txt_min.getText().length() == 0 ) txt_min.setText( "0" );
			if ( txt_sec.getText().length() == 0 ) txt_sec.setText( "0" );
			
			// Convert time
			//=============
			long min = Integer.parseInt( txt_min.getText() ) * 1000L * 60L;
			long sec = Integer.parseInt( txt_sec.getText() ) * 1000L;
			
			worldController.getMap().setCountdownTime( min + sec );
		}
		else
		{
			// Enable text fields
			//===================
			txt_min.setDisabled( true );
			txt_sec.setDisabled( true );
			
			worldController.getMap().setCountdownTime( Map.COUNTDOWN_DISABLED );
		}
	}
	
	// updateComponents
	//=================
	public void updateComponents()
	{
		// Temporary values
		//=================
		Map map = worldController.getMap();
		long sec = map.getCountdownTime() / 1000L;
		long min = sec / 60L;
		sec = sec - (min * 60L);

		// Backgrounds UI
		//===============
		slc_background.setSelected( map.getBackgroundFile() );
		slc_foreground.setSelected( map.getForegroundFile() );
		
		// Water UI
		//=========
		slc_waterSpeed.setSelected( waterSpeeds[map.getWaterSpeedId()] );
		btn_grp_waterButtons.getButtons().get( map.getWaterId() ).setChecked( true );
		
		// Environment sound UI
		//=====================
		slc_environmentSound.setSelected( map.getEnvironmentSoundFile() );
		
		// Countdown UI: Check box
		//========================
		chk_countdown.setChecked( map.getCountdownTime() != Map.COUNTDOWN_DISABLED );
		chk_countdown.fire( new ChangeListener.ChangeEvent() );
		
		// Countdown UI: Text fields
		//==========================
		if ( chk_countdown.isChecked() )
		{
			txt_min.setText( min + "" );
			txt_sec.setText( sec + "" );
		}
		else
		{
			txt_min.setText( "0" );
			txt_sec.setText( "0" );		
		}
		this.applyCountdownTime();
		
		// Map info UI
		//============
		txt_title     .setText         ( worldController.getMap().getTitle() );
		txt_creator   .setText         ( worldController.getMap().getCreator() );
		slc_difficulty.setSelectedIndex( worldController.getMap().getDifficultyId() );
		lbl_highScore .setText         ( worldController.getMap().getHighScore() + "" );
		lbl_bestTime  .setText         ( Time.toString( worldController.getMap().getBestTime() ) );
	}
	
	// getEditMode
	//============
	public EditMode getEditMode()
	{
		return ( editMode );
	}
	
	// getSelectedWaterTile
	//=====================
	public int getSelectedWaterTile()
	{
		return ( selectedWaterTile );
	}
	
	// getSelectedWaterSpeedId
	//========================
	public int getSelectedWaterSpeedId()
	{
		return ( selectedWaterSpeedId );
	}
	
	// getSelectedMapTile
	//===================
	public int getSelectedMapTile()
	{
		return ( selectedMapTile );
	}
}