package com.sh.jplatformer.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * The {@code Ui} class provides all user interface related resources.
 * @author Stefan Hösemann
 */

public class Ui
{
	// Skin objects
	//=============
	public Skin skin;
	public TextureAtlas atlas_ui;
	
	// Fonts
	//======
	public BitmapFont font_small;
	public BitmapFont font_medium;
	public BitmapFont font_big;
	
	// Menu textures
	//==============
	public Texture texture_logo_game_small;
	public Texture texture_logo_game_large;
	public Texture texture_menu_background;
	public Texture texture_menu_foreground;
	
	// Menu sprites
	//=============
	public Sprite logo_game_small;
	public Sprite logo_game_large;
	
	public Sprite menu_background;
	public Sprite menu_foreground;
	
	public Sprite menu_icon_play;
	public Sprite menu_icon_editor;
	public Sprite menu_icon_settings;
	public Sprite menu_icon_quit;
	public Sprite menu_icon_highlight_small;
	public Sprite menu_icon_highlight_large;
	
	// Skin sprites
	//=============
	public TiledDrawable skin_shadow;
	
	// Editor sprites: Rectangles
	//===========================
	public Sprite editor_rect_green;
	public Sprite editor_rect_red;
	public Sprite editor_rect_white;
	public Sprite editor_rect_yellow;
	
	// Editor sprites: Icons
	//======================
	public Sprite editor_icon_scrollCenter;
	public Sprite editor_icon_markObject;
	public Sprite editor_icon_leftMouse;
	public Sprite editor_icon_rightMouse;
	
	// Editor sprites: Toolbars
	//=========================
	public Sprite editor_toolbar_file;
	public Sprite editor_toolbar_map;
	public Sprite editor_toolbar_objects;
	public Sprite editor_toolbar_system;
	
	public Sprite editor_toolbar_file_new;
	public Sprite editor_toolbar_file_save;
	public Sprite editor_toolbar_file_load;
	
	public Sprite editor_toolbar_map_environment;
	public Sprite editor_toolbar_map_tiles;
	public Sprite editor_toolbar_map_conditions;
	
	public Sprite editor_toolbar_obj_place;
	public Sprite editor_toolbar_obj_attributes;
	
	public Sprite editor_toolbar_sys_settings;
	public Sprite editor_toolbar_sys_menu;
	
	// Game sprites
	//=============
	public Sprite[] game_scoreBoard;
	
	// Audio: UI + game
	//=================
	public Sound sound_click1;
	public Sound sound_click2;
	public Sound sound_editor_addTile;
	public Sound sound_editor_addObject;
	public Sound sound_editor_remove;
	public Sound sound_game_pause;
	public Sound sound_game_win;
	public Sound sound_game_lose;
	public Music sound_game_menu;
	
	// Constructor
	//============
	public Ui()
	{
		// Load atlas files
		//=================
		atlas_ui = new TextureAtlas( Gdx.files.internal( "resources/images/ui/ui.atlas" ) );
		
		// Initialize components
		//======================
		this.initFonts();
		this.initSkin();
		this.initMenuUi();
		this.initEditorUi();
		this.initGameUi();
		
		// Init audio
		//===========
		this.initSoundEffects();
	}
	
	// initFonts
	//==========
	private void initFonts()
	{
		// Default font parameters
		//========================
		FileHandle fontFile = Gdx.files.internal( "resources/fonts/font.ttf" );
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator( fontFile );
		{
			// Font setups
			//============
			parameter.genMipMaps = true;
			parameter.spaceY = 2;
			
			parameter.size = 18; font_small    = generator.generateFont( parameter );
			parameter.size = 32; font_medium   = generator.generateFont( parameter );
			parameter.size = 38; font_big      = generator.generateFont( parameter );
		}
		generator.dispose();
		
		// Use integer positions
		//======================
		font_small .setUseIntegerPositions( true );
		font_medium.setUseIntegerPositions( true );
		font_big   .setUseIntegerPositions( true );
	}
	
	// initSkin
	//=========
	private void initSkin()
	{		
		// Skin setup
		//===========
		skin = new Skin();
		skin.addRegions( atlas_ui );

		// Insert fonts
		//=============
		skin.add( "font_small",  font_small );
		skin.add( "font_medium", font_medium );
		skin.add( "font_big",    font_big );
		
		// Read JSON
		//==========
		skin.load( Gdx.files.internal( "resources/images/ui/ui.json" ) );
		
		// Separate sprites
		//=================
		skin_shadow = new TiledDrawable( atlas_ui.createSprite( "sidebar_shadow" ) );
	}

	// initMenuUi
	//===========
	private void initMenuUi()
	{
		// Menu back- and foreground
		//==========================
		texture_menu_background = new Texture( Gdx.files.internal( "resources/images/ui/ui_menu_background.png" ) );
		texture_menu_foreground = new Texture( Gdx.files.internal( "resources/images/ui/ui_menu_foreground.png" ) );
		
		texture_menu_background.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		texture_menu_foreground.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		
		menu_background = new Sprite( texture_menu_background );
		menu_foreground = new Sprite( texture_menu_foreground );
		
		// Game logos
		//===========
		texture_logo_game_small = new Texture( Gdx.files.internal( "resources/images/ui/logo_game_small.png" ) );
		texture_logo_game_large = new Texture( Gdx.files.internal( "resources/images/ui/logo_game_large.png" ) );

		texture_logo_game_small.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		texture_logo_game_large.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		
		logo_game_small = new Sprite( texture_logo_game_small );
		logo_game_large = new Sprite( texture_logo_game_large );
		
		// Menu icons
		//===========
		menu_icon_play            = atlas_ui.createSprite( "icon_play"            );
		menu_icon_editor          = atlas_ui.createSprite( "icon_editor"          );
		menu_icon_settings        = atlas_ui.createSprite( "icon_settings"        );
		menu_icon_quit            = atlas_ui.createSprite( "icon_quit"            );
		menu_icon_highlight_small = atlas_ui.createSprite( "icon_highlight_small" );
		menu_icon_highlight_large = atlas_ui.createSprite( "icon_highlight_large" );
	}
	
	// initEditorUi
	//=============
	private void initEditorUi()
	{
		// Color rectangles
		//=================
		editor_rect_green              = atlas_ui.createSprite( "rect_green"  );
		editor_rect_red                = atlas_ui.createSprite( "rect_red"    );
		editor_rect_white              = atlas_ui.createSprite( "rect_white"  );
		editor_rect_yellow             = atlas_ui.createSprite( "rect_yellow" );
		
		// Icons
		//======
		editor_icon_scrollCenter       = atlas_ui.createSprite( "icon_scrollCenter" );
		editor_icon_markObject         = atlas_ui.createSprite( "icon_markObject"   );
		editor_icon_leftMouse          = atlas_ui.createSprite( "icon_leftMouse"    );
		editor_icon_rightMouse         = atlas_ui.createSprite( "icon_rightMouse"   );
		
		// Toolbar: main
		//==============
		editor_toolbar_file            = atlas_ui.createSprite( "toolbar_file"    );
		editor_toolbar_map             = atlas_ui.createSprite( "toolbar_map"     );
		editor_toolbar_objects         = atlas_ui.createSprite( "toolbar_objects" );
		editor_toolbar_system          = atlas_ui.createSprite( "toolbar_system"  );
		
		// Toolbar: file
		//==============
		editor_toolbar_file_new        = atlas_ui.createSprite( "toolbar_file_new"  );
		editor_toolbar_file_save       = atlas_ui.createSprite( "toolbar_file_save" );
		editor_toolbar_file_load       = atlas_ui.createSprite( "toolbar_file_load" );
		
		// Toolbar: world
		//===============
		editor_toolbar_map_environment = atlas_ui.createSprite( "toolbar_map_environment" );
		editor_toolbar_map_tiles       = atlas_ui.createSprite( "toolbar_map_tiles"       );
		editor_toolbar_map_conditions  = atlas_ui.createSprite( "toolbar_map_conditions"  );
		
		// Toolbar: object
		//================
		editor_toolbar_obj_place       = atlas_ui.createSprite( "toolbar_obj_place"      );
		editor_toolbar_obj_attributes  = atlas_ui.createSprite( "toolbar_obj_attributes" );
		
		// Toolbar: system
		//================
		editor_toolbar_sys_menu        = atlas_ui.createSprite( "toolbar_sys_menu"     );
		editor_toolbar_sys_settings    = atlas_ui.createSprite( "toolbar_sys_settings" );
	}
	
	// initGameUi
	//===========
	private void initGameUi()
	{
		// Prepare values
		//===============
		TextureRegion tmp = atlas_ui.findRegion( "scoreBoard" );
		game_scoreBoard   = new Sprite[10];
		
		// Create array
		//=============
		for ( int i = 0; i < 10; i++ )
		{
			game_scoreBoard[i] = new Sprite( tmp,
			                                 (int) ( i * ( tmp.getRegionWidth() / 10 ) ),
			                                 0,
			                                 ( tmp.getRegionWidth() / 10 ),
			                                 ( tmp.getRegionHeight() ) );
		}
	}
	
	// initSoundEffects
	//=================
	private void initSoundEffects()
	{
		String p = "resources/audio/ui/";
		
		sound_click1           = Gdx.audio.newSound( Gdx.files.internal( p + "ui_click1.wav"           ) );
		sound_click2           = Gdx.audio.newSound( Gdx.files.internal( p + "ui_click2.wav"           ) );
		sound_editor_addTile   = Gdx.audio.newSound( Gdx.files.internal( p + "ui_editor_addTile.wav"   ) );
		sound_editor_addObject = Gdx.audio.newSound( Gdx.files.internal( p + "ui_editor_addObject.wav" ) );
		sound_editor_remove    = Gdx.audio.newSound( Gdx.files.internal( p + "ui_editor_remove.wav"    ) );
		sound_game_pause       = Gdx.audio.newSound( Gdx.files.internal( p + "ui_game_pause.wav"       ) );
		sound_game_win         = Gdx.audio.newSound( Gdx.files.internal( p + "ui_game_win.wav"         ) );
		sound_game_lose        = Gdx.audio.newSound( Gdx.files.internal( p + "ui_game_lose.wav"        ) );
		sound_game_menu        = Gdx.audio.newMusic( Gdx.files.internal( p + "ui_game_menu.wav"        ) );
	}
	
	// dispose
	//========
	public void dispose()
	{
		// Skin
		//=====
		skin.dispose();
		atlas_ui.dispose();
		
		// Textures
		//=========
		texture_logo_game_small.dispose();
		texture_logo_game_large.dispose();
		texture_menu_background.dispose();
		texture_menu_foreground.dispose();
		
		// Audio
		//======
		sound_click1          .dispose();
		sound_click2          .dispose();
		sound_editor_addTile  .dispose();
		sound_editor_addObject.dispose();
		sound_editor_remove   .dispose();
		sound_game_pause      .dispose();
		sound_game_win        .dispose();
		sound_game_lose       .dispose();
		sound_game_menu       .dispose();
	}
}