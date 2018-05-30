package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Spider extends MapObject
{
	// Serial version
	//===============
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private enum Routine
	{
		CLIMBING, HANGING_ON_ROOF, FALLING, HANGING_DOWN
	}
	private Routine routine;
	private float fallOriginY;
	private float fallHeight;
	
	// Constructor
	//============
	public Spider( WorldController worldController )
	{
		// Super constructor
		//==================
		super( worldController );
		
		// Properties
		//===========
		name            = "Spider";
		bounds.width    = 42f;
		bounds.height   = 60f;
		isBlockingSpace = false;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_TOP;
		
		// Routine
		//========
		routine = Routine.CLIMBING;
		fallHeight = Map.CELL_SIZE * 3f;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Init routine
		//=============
		if ( routine == null )
		{
			routine = Routine.CLIMBING;
		}
		
		// While climbing
		//===============
		if ( routine == Routine.CLIMBING )
		{
			// Check roof collision
			//=====================
			Map map = worldController.getMap();
			
			if ( map.isBlocked( bounds.x + bounds.width / 2f, bounds.y + bounds.height + 1f ) )
			{
				routineTimer = System.currentTimeMillis() + 5000L;
				routine = Routine.HANGING_ON_ROOF;
			}
			
			// Move up when not docked
			//========================
			ignoreGravity = true;
			agility.jump();
		}
		
		// While hanging on roof tile
		//===========================
		if ( routine == Routine.HANGING_ON_ROOF )
		{
			// Dock to roof
			//=============
			ignoreGravity = true;
			
			// Initiate fall
			//==============
			if ( routineTimer < System.currentTimeMillis() )
			{
				routine = Routine.FALLING;
				fallOriginY = bounds.y;
				ignoreGravity = false;
				
				worldController.getWorldAudio().addSound( Resources.WORLD.sound_character_spider, this );
			}
		}
		
		// While falling
		//==============
		if ( routine == Routine.FALLING )
		{
			// Limit fall + Switch state
			//==========================
			if ( bounds.y <= fallOriginY - fallHeight || isOnGround() )
			{
				// Update routine
				//===============
				routineTimer = System.currentTimeMillis() + 5000L;
				routine = Routine.HANGING_DOWN;
				agility.resetJump();
				
				// Update position
				//================
				if ( bounds.y <= fallOriginY - fallHeight )
				{
					bounds.y = fallOriginY - fallHeight;
				}
			}
		}
		
		// While hanging
		//==============
		if ( routine == Routine.HANGING_DOWN )
		{
			// Defy gravity
			//=============
			ignoreGravity = true;
			
			// Initiate moving up
			//===================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routine = Routine.CLIMBING;
			}
		}
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		worldController.getPlayer().setAlive( false );
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// While climbing
		//===============
		if ( routine == Routine.CLIMBING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				frameTimer = System.currentTimeMillis() + 80L;
				currentFrame++;
				
				if ( currentFrame > 4 )
				{
					currentFrame = 0;
				}
			}
		}
		
		// While hanging
		//==============
		else
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = Randomizer.getInt( 5, 7 );
				frameTimer = System.currentTimeMillis() + 300L;
			}
		}
	}
	
	// initAgility
	//============
	@Override
	public void initAgility()
	{
		// Super
		//======
		super.initAgility();
		
		// Jumping
		//========
		agility.setMinJumpHeight( 8f );
	}
}
