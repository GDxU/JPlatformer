package com.sh.jplatformer.ui.editor.sidebar;

import java.io.File;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.*;
import com.sh.jplatformer.ui.stages.EditorStage;
import com.sh.jplatformer.util.FileUtils;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.WorldFile;

/**
 * The {@code FileMenu} provides a tabbed menu structure for creating, saving and loading world files.
 * @author Stefan Hösemann
 */

public class FileMenu extends Table
{
	// Fields
	//=======
	private WorldController worldController;
	private Skin skin;
	
	private TabPane pne_tabs;
	private Actor mnu_createWorld;
	private Actor mnu_saveWorld;
	private Actor mnu_loadWorld;

	private TextField txt_fileName;
	private FormList<String> lst_worldFiles_load;
	private FormList<String> lst_worldFiles_save;
	private String[] worldPaths;
	private String[] worldNames;

	// Constructor
	//============
	/**
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public FileMenu( WorldController newWorldController )
	{
		// Setup
		//======
		worldController = newWorldController;
		skin = Resources.UI.skin;
		
		// Create sub menus
		//=================
		mnu_createWorld = new SidebarScrollPane( this.createCreateMenu(), skin, "transparent" );
		mnu_saveWorld   = new SidebarScrollPane( this.createSaveMenu(),   skin, "transparent" );
		mnu_loadWorld   = new SidebarScrollPane( this.createLoadMenu(),   skin, "transparent" );
		
		// Create tab pane
		//================
		pne_tabs = new TabPane();
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_file_new, skin, "toggle" ),
		                 mnu_createWorld );
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_file_save, skin, "toggle" ),
		                 mnu_saveWorld );
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_file_load, skin, "toggle" ),
		                 mnu_loadWorld );
		
		// Click listener
		//===============
		pne_tabs.addListener( new ClickListener()
		{
			@Override
			public void clicked( InputEvent event, float x, float y )
			{
				Actor content = pne_tabs.getVisibleContent();
				
				if ( content == mnu_saveWorld ) updateWorldFilesList();
				if ( content == mnu_loadWorld ) updateWorldFilesList();
			}
		} );
		
		// Wrap in form pane
		//==================
		this.add( new FormPane( pne_tabs, skin ) ).top().expand().fill();
		
		// Update file list
		//=================
		this.updateWorldFilesList();
		txt_fileName.setText( lst_worldFiles_save.getSelected() );
	}
	
	// createCreateMenu
	//=================
	private Actor createCreateMenu()
	{
		// Text fields
		//============
		TextField txt_title  = new TextField( Lang.txt( "editor_newWorld" ), skin );
		TextField txt_width  = new TextField( "10", skin );
		TextField txt_height = new TextField( "10", skin );
		
		txt_width .setTextFieldFilter( new TextFieldFilter.DigitsOnlyFilter() );
		txt_height.setTextFieldFilter( new TextFieldFilter.DigitsOnlyFilter() );
		
		txt_title .setMaxLength( 30 );
		txt_width .setMaxLength( 3 );
		txt_height.setMaxLength( 3 );
		
		// Button: Create
		//===============
		FormButton btn_create = new FormButton( Lang.txt( "editor_file_create" ), skin );
		btn_create.addListener( new ChangeListener()
		{
			// Create new world
			//=================
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				// Limit size input
				//=================
				if ( Integer.parseInt( txt_width .getText() ) <   4 ) txt_width .setText(   "4" );
				if ( Integer.parseInt( txt_height.getText() ) <   4 ) txt_height.setText(   "4" );
				if ( Integer.parseInt( txt_width .getText() ) > 500 ) txt_width .setText( "500" );
				if ( Integer.parseInt( txt_height.getText() ) > 500 ) txt_height.setText( "500" );
				
				// Create new world
				//=================
				worldController.createWorld( Integer.parseInt( txt_width.getText() ),
				                             Integer.parseInt( txt_height.getText() ) );

				// Set map properties
				//===================
				worldController.getMap().setTitle( txt_title.getText() );
				worldController.getMap().setBackgroundFile( Resources.WORLD.availableBackgroundTextures[0] );
				worldController.getMap().setForegroundFile( Resources.WORLD.availableForegroundTextures[0] );
				
				// Pause game + update sidebar
				//============================
				JPlatformerGame.get().setPaused( true );
				( (EditorStage) getStage() ).update();
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		tbl_main.columnDefaults( 0 ).width( Value.percentWidth( 0.00f, tbl_main ) );
		tbl_main.columnDefaults( 1 ).width( Value.percentWidth( 0.40f, tbl_main ) );
		
		// Heading
		//========
		tbl_main.add( new Label(Lang.txt( "editor_file_createWorld" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();
		
		// Title
		//======
		tbl_main.add( new Label( Lang.txt( "editor_file_worldName" ), skin ) ).left();
		tbl_main.add( txt_title ).right();
		tbl_main.row();
		
		// Width
		//======
		tbl_main.add( new Label( Lang.txt( "editor_file_width" ), skin ) ).left();
		tbl_main.add( txt_width ).right();
		tbl_main.row();
		
		// Height
		//=======
		tbl_main.add( new Label( Lang.txt( "editor_file_height" ), skin ) ).left();
		tbl_main.add( txt_height ).right();
		tbl_main.row();
		
		// Create button
		//==============
		tbl_main.add();
		tbl_main.add( btn_create ).right().width( btn_create.getPrefWidth() );
		
		return ( tbl_main );
	}
	
	// createSaveMenu
	//===============
	private Actor createSaveMenu()
	{
		// Text field: File name
		//======================
		txt_fileName = new TextField( "", skin );
		
		// Create files list
		//==================
		lst_worldFiles_save = new FormList<String>( skin );
		lst_worldFiles_save.addListener( new ClickListener()
		{
			// Update text field string
			//=========================
			@Override
			public void clicked( InputEvent e, float x, float y )
			{
				if ( lst_worldFiles_save.getSelected() != null )
				{
					txt_fileName.setText( lst_worldFiles_save.getSelected() );
				}
			}
		} );

		// Button: Save
		//=============
		FormButton btn_save = new FormButton( Lang.txt( "editor_file_save" ), skin );
		btn_save.addListener( new ChangeListener()
		{
			// Save to file
			//=============
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				if ( txt_fileName.getText().length() > 0 )
				{
					// Save file
					//==========
					String file = FileUtils.getRoot() + WorldFile.FILE_DIR + txt_fileName.getText();
					String ext  = WorldFile.FILE_EXTENSION;
					String path = FileUtils.appendExtension( file, ext );
					
					WorldFile.saveWorld( path, worldController );
					
					updateWorldFilesList();
					
					// Select saved file
					//==================
					lst_worldFiles_save.setSelected( FileUtils.appendExtension( txt_fileName.getText(), ext ));
					
					// Update sidebar
					//===============
					( (EditorStage) getStage() ).update();
				}
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_file_saveWorld" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();
		
		// File list
		//==========
		SidebarScrollPane filesPane = new SidebarScrollPane( lst_worldFiles_save, skin, "dark" );
		
		tbl_main.add( filesPane ).top().expand().fill().colspan( 2 );
		tbl_main.row();

		// Text field and save button
		//===========================
		tbl_main.add( txt_fileName ).expandX().fill();
		tbl_main.add( btn_save ).expand( false, false );

		return ( tbl_main );
	}
	
	// createLoadMenu
	//===============
	private Actor createLoadMenu()
	{
		// Create file list
		//=================
		lst_worldFiles_load = new FormList<String>( skin );

		// Button: Load
		//=============
		FormButton btn_load = new FormButton( Lang.txt( "editor_file_load" ), skin );
		btn_load.addListener( new ChangeListener()
		{
			// Load world
			//===========
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				// Open world file
				//================
				if ( lst_worldFiles_load.getSelected() != null )
				{
					WorldFile.loadWorld( worldPaths[lst_worldFiles_load.getSelectedIndex()],
					                     worldController );
					JPlatformerGame.get().setPaused( true );
				}
				worldController.setLive( false );
				
				// Update sidebar
				//===============
				( (EditorStage) getStage() ).update();
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_file_loadWorld" ), skin, "heading" ) ).left();
		tbl_main.row();
		
		// File list
		//==========
		SidebarScrollPane pne_filesList = new SidebarScrollPane( lst_worldFiles_load, skin, "dark" );
		
		tbl_main.add( pne_filesList ).top().expand().fill();
		tbl_main.row();
		
		// Load button
		//============
		tbl_main.add( btn_load ).right();

		return ( tbl_main );
	}
	
	// updateWorldFilesList
	//=====================
	/**
	 * Updates the world file lists in the load and save sub menus.
	 */
	public void updateWorldFilesList()
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
		int idx_load = lst_worldFiles_load.getSelectedIndex();
		int idx_save = lst_worldFiles_save.getSelectedIndex();
		
		if ( idx_load < 0 ) idx_load = 0;
		if ( idx_save < 0 ) idx_save = 0;
		
		// Update load list
		//=================
		lst_worldFiles_load.clearItems();
		lst_worldFiles_load.setItems( worldNames );
		lst_worldFiles_load.setSelectedIndex( lst_worldFiles_load.getItems().size - 1 );
		
		if ( lst_worldFiles_load.getItems().size > idx_load )
		{
			lst_worldFiles_load.setSelectedIndex( idx_load );
		}

		// Update save list
		//=================
		lst_worldFiles_save.clearItems();
		lst_worldFiles_save.setItems( worldNames );
		lst_worldFiles_save.setSelectedIndex( lst_worldFiles_save.getItems().size - 1 );
		
		if ( lst_worldFiles_save.getItems().size > idx_save )
		{
			lst_worldFiles_save.setSelectedIndex( idx_save );
		}
	}
	
	// setVisible
	//===========
	@Override
	public void setVisible( boolean visible )
	{
		super.setVisible( visible );
		this.updateWorldFilesList();
	}
}