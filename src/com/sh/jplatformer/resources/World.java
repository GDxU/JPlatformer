package com.sh.jplatformer.resources;

import java.util.TreeMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.sh.jplatformer.util.FileUtils;
import com.sh.jplatformer.world.map.Map;

/**
 * The {@code World} class provides all game world related resources.
 * @author Stefan Hösemann
 */

public class World
{
	// Constants
	//==========
	private static final String BACKGROUNDS_PATH = FileUtils.getRoot() + "resources/images/world/background/";
	private static final String FOREGROUNDS_PATH = FileUtils.getRoot() + "resources/images/world/foreground/";
	private static final String ENVR_SOUNDS_PATH = FileUtils.getRoot() + "resources/audio/environment/";
	
	// Images: Map + objects
	//======================
	public Texture texture_water;
	public Texture texture_tiles;
	public TextureAtlas atlas_objects;
	public Sprite[] water;
	public Sprite[][] tiles;
	public TreeMap<String, Sprite> objectSprites;
	
	// Images: Back- and foregrounds
	//==============================
	private String  currentBackgroundPath;
	private String  currentForegroundPath;
	public String[] availableBackgroundTextures;
	public String[] availableForegroundTextures;
	public Texture  texture_background;
	public Texture  texture_foreground;
	public Sprite   background;
	public Sprite   foreground;
	
	// Audio: Environment
	//===================
	private String  currentEnvironmentSoundPath;
	public String[] availableEnvironmentSounds;
	public Music    sound_environment;
	public Music    sound_water;
	
	// Audio: Objects
	//===============
	public Sound sound_character_block1;
	public Sound sound_character_block2;
	public Sound sound_character_cartonGuy1;
	public Sound sound_character_cartonGuy2;
	public Sound sound_character_cartonGuy3;
	public Sound sound_character_cartonGuy4;
	public Sound sound_character_ghost1;
	public Sound sound_character_ghost2;
	public Sound sound_character_ghost3;
	public Sound sound_character_gland1;
	public Sound sound_character_gland2;
	public Sound sound_character_gland3;
	public Sound sound_character_gland4;
	public Sound sound_character_player_step1;
	public Sound sound_character_player_step2;
	public Sound sound_character_player_step3;
	public Sound sound_character_player_step4;
	public Sound sound_character_player_step5;
	public Sound sound_character_player_step6;
	public Sound sound_character_spider;
	public Sound sound_character_spikehead1;
	public Sound sound_character_spikehead2;
	public Sound sound_character_spikehead3;
	public Sound sound_character_spikehead4;
	public Sound sound_character_spitter1;
	public Sound sound_character_spitter2;
	public Sound sound_character_spitter3;
	public Sound sound_character_springboard;
	
	public Sound sound_machine_electricTrap1;
	public Sound sound_machine_electricTrap2;
	public Sound sound_machine_electricTrap3;
	public Sound sound_machine_springboard;
	public Sound sound_machine_switch_on;
	public Sound sound_machine_switch_off;
	
	public Sound sound_item_collect;
	
	// Constructor
	//============
	public World()
	{
		// Init sprites
		//=============
		this.initWater();
		this.initTiles();
		this.initObjectSprites();
		
		// Init audio
		//===========
		this.initSoundEffects();
		
		// Update available environment files
		//===================================
		this.updateAvailableBackgroundTextures();
		this.updateAvailableForegroundTextures();
		this.updateAvailableEnvironmentSounds();
	}
	
	// initWater
	//==========
	private void initWater()
	{
		// Load textures
		//==============
		texture_water = new Texture( Gdx.files.internal( "resources/images/world/world_water.png" ) );
		texture_water.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		
		// Initialize water
		//=================
		int size = Map.CELL_SIZE;
		int max  = texture_water.getHeight() / ( size + 10 ) + 1;
		water = new Sprite[max];
		
		// Create array
		//=============
		for ( int i = 0; i < max; i++ )
		{
			water[i] = new Sprite( texture_water, 0, i * ( size + 10 ), size, size );
		}
	}
	
	// initTiles
	//==========
	private void initTiles()
	{
		// Load textures
		//==============
		texture_tiles = new Texture( Gdx.files.internal( "resources/images/world/world_tiles.png" ) );
		texture_tiles.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		
		// Initialize tiles
		//=================
		int size = Map.CELL_SIZE;
		int max  = texture_tiles.getHeight() / ( size + 10 ) + 1;
		tiles    = new Sprite[max][20];
		
		// Create array
		//=============
		for ( int i = 0; i < max; i++ )
		{
			for ( int j = 0; j < 20; j++ )
			{
				tiles[i][j] = new Sprite( texture_tiles, j * ( size + 10 ), i * ( size + 10 ) + 2, size, size );
			}
		}
	}
	
	// initObjectSprites
	//==================
	private void initObjectSprites()
	{
		// Read atlas
		//===========
		atlas_objects = new TextureAtlas( Gdx.files.internal( "resources/images/world/world_objects.atlas" ) );
		
		// Read regions
		//=============
		objectSprites = new TreeMap<String, Sprite>(); 
		for ( AtlasRegion r : atlas_objects.getRegions() )
		{
			objectSprites.put( r.name, atlas_objects.createSprite( r.name ) );
		}
	}
	
	// initSoundEffects
	//=================
	private void initSoundEffects()
	{
		// Water
		//======
		sound_water = Gdx.audio.newMusic( Gdx.files.internal( "resources/audio/environment/water/water.wav" ) );
		
		// Objects
		//========
		String p = "resources/audio/objects/";
		
		sound_character_block1       = Gdx.audio.newSound( Gdx.files.internal( p + "character_block1.wav"       ) );
		sound_character_block2       = Gdx.audio.newSound( Gdx.files.internal( p + "character_block2.wav"       ) );
		sound_character_cartonGuy1   = Gdx.audio.newSound( Gdx.files.internal( p + "character_cartonGuy1.wav"   ) );
		sound_character_cartonGuy2   = Gdx.audio.newSound( Gdx.files.internal( p + "character_cartonGuy2.wav"   ) );
		sound_character_cartonGuy3   = Gdx.audio.newSound( Gdx.files.internal( p + "character_cartonGuy3.wav"   ) );
		sound_character_cartonGuy4   = Gdx.audio.newSound( Gdx.files.internal( p + "character_cartonGuy4.wav"   ) );
		sound_character_ghost1       = Gdx.audio.newSound( Gdx.files.internal( p + "character_ghost1.wav"       ) );
		sound_character_ghost2       = Gdx.audio.newSound( Gdx.files.internal( p + "character_ghost2.wav"       ) );
		sound_character_ghost3       = Gdx.audio.newSound( Gdx.files.internal( p + "character_ghost3.wav"       ) );
		sound_character_gland1       = Gdx.audio.newSound( Gdx.files.internal( p + "character_gland1.wav"       ) );
		sound_character_gland2       = Gdx.audio.newSound( Gdx.files.internal( p + "character_gland2.wav"       ) );
		sound_character_gland3       = Gdx.audio.newSound( Gdx.files.internal( p + "character_gland3.wav"       ) );
		sound_character_gland4       = Gdx.audio.newSound( Gdx.files.internal( p + "character_gland4.wav"       ) );
		sound_character_player_step1 = Gdx.audio.newSound( Gdx.files.internal( p + "character_player_step1.wav" ) );
		sound_character_player_step2 = Gdx.audio.newSound( Gdx.files.internal( p + "character_player_step2.wav" ) );
		sound_character_player_step3 = Gdx.audio.newSound( Gdx.files.internal( p + "character_player_step3.wav" ) );
		sound_character_player_step4 = Gdx.audio.newSound( Gdx.files.internal( p + "character_player_step4.wav" ) );
		sound_character_player_step5 = Gdx.audio.newSound( Gdx.files.internal( p + "character_player_step5.wav" ) );
		sound_character_player_step6 = Gdx.audio.newSound( Gdx.files.internal( p + "character_player_step6.wav" ) );
		sound_character_spider       = Gdx.audio.newSound( Gdx.files.internal( p + "character_spider.wav"       ) );
		sound_character_spikehead1   = Gdx.audio.newSound( Gdx.files.internal( p + "character_spikehead1.wav"   ) );
		sound_character_spikehead2   = Gdx.audio.newSound( Gdx.files.internal( p + "character_spikehead2.wav"   ) );
		sound_character_spikehead3   = Gdx.audio.newSound( Gdx.files.internal( p + "character_spikehead3.wav"   ) );
		sound_character_spikehead4   = Gdx.audio.newSound( Gdx.files.internal( p + "character_spikehead4.wav"   ) );
		sound_character_spitter1     = Gdx.audio.newSound( Gdx.files.internal( p + "character_spitter1.wav"     ) );
		sound_character_spitter2     = Gdx.audio.newSound( Gdx.files.internal( p + "character_spitter2.wav"     ) );
		sound_character_spitter3     = Gdx.audio.newSound( Gdx.files.internal( p + "character_spitter3.wav"     ) );
		sound_character_springboard  = Gdx.audio.newSound( Gdx.files.internal( p + "character_springboard.wav"  ) );
		
		sound_machine_electricTrap1  = Gdx.audio.newSound( Gdx.files.internal( p + "machine_electricTrap1.wav"  ) );
		sound_machine_electricTrap2  = Gdx.audio.newSound( Gdx.files.internal( p + "machine_electricTrap2.wav"  ) );
		sound_machine_electricTrap3  = Gdx.audio.newSound( Gdx.files.internal( p + "machine_electricTrap3.wav"  ) );
		sound_machine_springboard    = Gdx.audio.newSound( Gdx.files.internal( p + "machine_springboard.wav"    ) );
		sound_machine_switch_on      = Gdx.audio.newSound( Gdx.files.internal( p + "machine_switch_on.wav"      ) );
		sound_machine_switch_off     = Gdx.audio.newSound( Gdx.files.internal( p + "machine_switch_off.wav"     ) );
		
		sound_item_collect           = Gdx.audio.newSound( Gdx.files.internal( p + "item_collect.wav"           ) );
	}
	
	// updateAvailableBackgroundTextures
	//==================================
	private void updateAvailableBackgroundTextures()
	{
		// Read directory
		//===============
		FileHandle[] files = Gdx.files.absolute( BACKGROUNDS_PATH ).list( ".png" );
		
		// Add placeholder if empty
		//=========================
		if ( files.length > 0 )
		{
			availableBackgroundTextures = new String[files.length];
		}
		else
		{
			availableBackgroundTextures = new String[] { "" };
		}
		
		// Add files names
		//================
		for ( int i = 0; i < files.length; i++ )
		{
			availableBackgroundTextures[i] = files[i].name();
		}
	}
	
	// updateAvailableForegroundTextures
	//==================================
	private void updateAvailableForegroundTextures()
	{
		// Read directory
		//===============
		FileHandle[] files = Gdx.files.absolute( FOREGROUNDS_PATH ).list( ".png" );
		
		// Add placeholder if empty
		//=========================
		if ( files.length > 0 )
		{
			availableForegroundTextures = new String[files.length];
		}
		else
		{
			availableForegroundTextures = new String[] { "" };
		}
		
		// Add files names
		//================
		for ( int i = 0; i < files.length; i++ )
		{
			availableForegroundTextures[i] = files[i].name();
		}
	}
	
	// updateAvailableEnvironmentSounds
	//=================================
	private void updateAvailableEnvironmentSounds()
	{
		// Read directory
		//===============
		FileHandle[] files = Gdx.files.absolute( ENVR_SOUNDS_PATH ).list( ".wav" );
		
		// Add placeholder if empty
		//=========================
		if ( files.length > 0 )
		{
			availableEnvironmentSounds = new String[files.length];
		}
		else
		{
			availableEnvironmentSounds = new String[] { "" };
		}
		
		// Add files names
		//================
		for ( int i = 0; i < files.length; i++ )
		{
			availableEnvironmentSounds[i] = files[i].name();
		}
	}
	
	// loadBackground
	//===============
	public void loadBackground( String file )
	{
		// Check if already loaded
		//========================
		if ( file != null && file.equals( currentBackgroundPath ) )
		{
			return;
		}
		
		// Dispose old texture
		//====================
		if ( texture_background != null )
		{
			texture_background.dispose();
			texture_background = null;
		}
		
		// Return if null
		//===============
		if ( file == null )
		{
			currentBackgroundPath = null;
			return;
		}
		
		// Load texture + sprite
		//======================
		try
		{
			texture_background    = new Texture( Gdx.files.absolute( BACKGROUNDS_PATH + file ) );
			background            = new Sprite( texture_background );
			currentBackgroundPath = file;
			
			texture_background.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		}
		catch ( Exception e )
		{
			if ( currentBackgroundPath != file )
			{
				currentBackgroundPath = file;
				texture_background    = null;
				background            = null;
				System.err.println( "Error reading background texture: " + file );
			}
		}
	}
	
	// loadForeground
	//===============
	public void loadForeground( String file )
	{
		// Check if already loaded
		//========================
		if ( file != null && file.equals( currentForegroundPath ) )
		{
			return;
		}
		
		// Dispose old texture
		//====================
		if ( texture_foreground != null )
		{
			texture_foreground.dispose();
			texture_foreground = null;
		}
		
		// Return if null
		//===============
		if ( file == null )
		{
			currentForegroundPath = null;
			return;
		}
		
		// Load texture + sprite
		//======================
		try
		{
			texture_foreground    = new Texture( Gdx.files.absolute( FOREGROUNDS_PATH + file ) );
			foreground            = new Sprite( texture_foreground );
			currentForegroundPath = file;
			
			texture_foreground.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		}
		catch ( Exception e )
		{
			if ( currentForegroundPath != file )
			{
				currentForegroundPath = file;
				texture_foreground    = null;
				foreground            = null;
				System.err.println( "Error reading foreground texture: " + file );
			}
		}
	}
	
	// loadEnvironmentSound
	//=====================
	public void loadEnvironmentSound( String file )
	{
		// Check if already loaded
		//========================
		if ( file != null && file.equals( currentEnvironmentSoundPath ) )
		{
			return;
		}
		
		// Dispose old sound
		//==================
		if ( sound_environment != null )
		{
			sound_environment.dispose();
			sound_environment = null;
		}

		// Return if null
		//===============
		if ( file == null )
		{
			currentEnvironmentSoundPath = null;
			return;
		}
		
		// Load environment sound
		//=======================
		try
		{
			sound_environment = Gdx.audio.newMusic( Gdx.files.absolute( ENVR_SOUNDS_PATH + file ) );
			currentEnvironmentSoundPath = file;
		}
		catch ( Exception e )
		{
			if ( currentEnvironmentSoundPath != file )
			{
				currentEnvironmentSoundPath = file;
				sound_environment = null;
				System.err.println( "Error reading environment sound: " + file );
			}
		}
	}
	
	// dispose
	//========
	public void dispose()
	{
		// Back- and foreground
		//=====================
		loadBackground( null );
		loadForeground( null );
		loadEnvironmentSound( null );
		
		// Map textures + objects
		//=======================
		texture_water.dispose();
		texture_tiles.dispose();
		atlas_objects.dispose();
		
		// Dispose audio
		//==============
		sound_character_block1      .dispose();
		sound_character_block2      .dispose();
		sound_character_cartonGuy1  .dispose();
		sound_character_cartonGuy2  .dispose();
		sound_character_cartonGuy3  .dispose();
		sound_character_cartonGuy4  .dispose();
		sound_character_ghost1      .dispose();
		sound_character_ghost2      .dispose();
		sound_character_ghost3      .dispose();
		sound_character_gland1      .dispose();
		sound_character_gland2      .dispose();
		sound_character_gland3      .dispose();
		sound_character_gland4      .dispose();
		sound_character_player_step1.dispose();
		sound_character_player_step2.dispose();
		sound_character_player_step3.dispose();
		sound_character_player_step4.dispose();
		sound_character_player_step5.dispose();
		sound_character_player_step6.dispose();
		sound_character_spider      .dispose();
		sound_character_spikehead1  .dispose();
		sound_character_spikehead2  .dispose();
		sound_character_spikehead3  .dispose();
		sound_character_spikehead4  .dispose();
		sound_character_spitter1    .dispose();
		sound_character_spitter2    .dispose();
		sound_character_spitter3    .dispose();
		sound_character_springboard .dispose();
		sound_machine_electricTrap1 .dispose();
		sound_machine_electricTrap2 .dispose();
		sound_machine_electricTrap3 .dispose();
		sound_machine_springboard   .dispose();
		sound_machine_switch_on     .dispose();
		sound_machine_switch_off    .dispose();
		sound_item_collect          .dispose();
	}
}