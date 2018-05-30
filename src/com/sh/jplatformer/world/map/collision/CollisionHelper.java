package com.sh.jplatformer.world.map.collision;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.map.collision.Collision.CollisionType;

/**
 * The {@code CollisionHelper} class provides utility methods for collision detection.
 * @author Stefan Hösemann
 */

public class CollisionHelper
{
	// Helpers
	//========
	private static Vector2   v1 = new Vector2( 0f, 0f );
	private static Vector2   v2 = new Vector2( 0f, 0f );
	private static Vector2   v3 = new Vector2( 0f, 0f );
	private static Rectangle r1 = new Rectangle();
	
	// move
	//=====
	/**
	 * Attempts to translate the bounds of the specified {@code MapObject}. The movement is blocked
	 * by occupied {@code MapCells} and other objects that block space.
	 * @param o the {@code MapObject} that moves.
	 * @param x the amount of movement on the x-axis.
	 * @param y the amount of movement on the y-axis.
	 */
	public static void move( MapObject o, float x, float y )
	{
		// Helper objects
		//===============
		Map map = o.getWorldController().getMap();
		Rectangle r = o.getBounds();
		
		// Limit values
		//=============
		x = limitDelta( x );
		y = limitDelta( y );
		
		// Move up
		//========
		if ( y > 0f )
		{
			// Set collision vectors
			//======================
			v1.set( r.x,                    r.y + r.height + y );
			v2.set( r.x + r.width - 0.001f, r.y + r.height + y );
			v3.set( r.x + r.width / 2f,     r.y + r.height + y );
			r1.set( r.x, r.y + y, r.width, r.height );
			
			// Check tiles
			//============
			if ( !isCellBlocked( map, v1, v2, v3 ) )
			{
				// Check objects
				//==============
				Rectangle area = checkObjectCollision( o, r1 );
				
				if ( area != null )
				{
					r.y = area.y - r.height;
					o.getLastCollision().y = CollisionType.TOP;
					o.getAgility().resetJump();
				}
			}
			else
			{
				r.y = map.getCellAt( v1 ).y * Map.CELL_SIZE - r.height - 0.001f;
				o.getLastCollision().y = CollisionType.TOP;
				o.getAgility().resetJump();
			}
		}
		
		// Move down
		//==========
		if ( y < 0f )
		{
			// Set collision vectors
			//======================
			v1.set( r.x,                    r.y + y );
			v2.set( r.x + r.width - 0.001f, r.y + y );
			v3.set( r.x + r.width / 2f,     r.y + y );
			r1.set( r.x, r.y + y, r.width, r.height );
			
			// Check tiles
			//============
			if ( !isCellBlocked( map, v1, v2, v3 ) )
			{
				// Check objects
				//==============
				Rectangle area = checkObjectCollision( o, r1 );
				
				if ( area != null )
				{
					r.y = area.y + area.height;
					o.getLastCollision().y = CollisionType.BOTTOM;
					o.getAgility().resetJump();
				}
			}
			else
			{
				r.y = map.getCellAt( v1 ).y * Map.CELL_SIZE + Map.CELL_SIZE;
				o.getLastCollision().y = CollisionType.BOTTOM;
				o.getAgility().resetJump();
			}
		}

		// Move east
		//==========
		if ( x > 0f )
		{
			// Set collision vectors
			//======================
			v1.set( r.x + r.width + x, r.y );
			v2.set( r.x + r.width + x, r.y + r.height - 0.001f );
			v3.set( r.x + r.width + x, r.y + r.height / 2f );
			r1.set( r.x + x, r.y, r.width, r.height );
			
			// Check tiles
			//============
			if ( !isCellBlocked( map, v1, v2, v3 ) )
			{
				// Check objects
				//==============
				Rectangle area = checkObjectCollision( o, r1 );
				
				if ( area != null )
				{
					r.x = area.x - r.width;
					o.getLastCollision().x = CollisionType.EAST;
					o.getAgility().getVelocity().reset();
				}
			}
			else
			{
				r.x = map.getCellAt( v1 ).x * Map.CELL_SIZE - r.width - 0.001f;
				o.getLastCollision().x = CollisionType.EAST;
				o.getAgility().getVelocity().reset();
			}
		}
		
		// Move west
		//==========
		if ( x < 0f )
		{
			// Set collision vectors
			//======================
			v1.set( r.x + x, r.y );
			v2.set( r.x + x, r.y + r.height - 0.001f );
			v3.set( r.x + x, r.y + r.height / 2f );
			r1.set( r.x + x, r.y, r.width, r.height );
			
			// Check tiles
			//============
			if ( !isCellBlocked( map, v1, v2, v3 ) )
			{
				// Check objects
				//==============
				Rectangle area = checkObjectCollision( o, r1 );
				
				if ( area != null )
				{
					r.x = area.x + area.width;
					o.getLastCollision().x = CollisionType.WEST;
					o.getAgility().getVelocity().reset();
				}
			}
			else
			{
				r.x = map.getCellAt( v1 ).x * Map.CELL_SIZE + Map.CELL_SIZE;
				o.getLastCollision().x = CollisionType.WEST;
				o.getAgility().getVelocity().reset();
			}
		}
		
		// Update temporary coordinates
		//=============================
		if ( o.getLastCollision().x == CollisionType.NONE ) { r.x += x; }
		if ( o.getLastCollision().y == CollisionType.NONE ) { r.y += y; }
		
		// Kill when in water
		//===================
		if ( r.y + r.height < map.getWaterHeight() )
		{
			o.setAlive( false );
		}
		
		// Limit western border
		//=====================
		if ( r.x < 0 )
		{
			r.x = 0;
			o.getLastCollision().x = CollisionType.WEST;
		}
				
		// Limit eastern border
		//=====================
		if ( r.x > map.getMapBounds().width - r.width )
		{
			r.x = map.getMapBounds().width - r.width;
			o.getLastCollision().x = CollisionType.EAST;
		}
		
		// Limit bottom border
		//====================
		if ( r.y < -Map.CELL_SIZE * 2f )
		{
			r.y = -Map.CELL_SIZE * 2f;
			o.setAlive( false );
		}
		
		// Limit top border
		//=================
		if ( r.y > map.getMapBounds().height + Map.CELL_SIZE )
		{
			o.setAlive( false );
		}
	}
	
	// isCellBlocked
	//==============
	private static boolean isCellBlocked( Map map, Vector2 v1, Vector2 v2, Vector2 v3 )
	{
		return ( map.isBlocked( v1 ) || map.isBlocked( v2 ) || map.isBlocked( v3 ) );
	}
	
	// checkObjectCollision
	//=====================
	private static Rectangle checkObjectCollision( MapObject movingObject, Rectangle rect )
	{
		for ( MapObject o : movingObject.getSurroundingObjects() )
		{
			if ( o != movingObject && o.isBlockingSpace() && o.getBounds().overlaps( rect ) )
			{
				return ( o.getBounds() );
			}
		}
		return ( null );
	}
	
	// isEastCellPassable
	//===================
	public static boolean isEastCellPassable( MapObject o )
	{
		// Get objects
		//============
		Map map = o.getWorldController().getMap();
		Rectangle b = o.getBounds();
		Collision c = o.getLastCollision();
		
		// Check collision
		//================
		return ( map.isBlocked( b.x + b.width, b.y - Map.CELL_SIZE / 2f ) &&
		         c.x != CollisionType.EAST );
	}
	
	// isWestCellPassable
	//===================
	public static boolean isWestCellPassable( MapObject o )
	{
		// Get objects
		//============
		Map map = o.getWorldController().getMap();
		Rectangle b = o.getBounds();
		Collision c = o.getLastCollision();
		
		// Check collision
		//================
		return ( map.isBlocked( b.x, b.y - Map.CELL_SIZE / 2f ) &&
		         c.x != CollisionType.WEST );
	}
	
	// isOnCell
	//=========
	public static boolean isOnCell( MapObject o )
	{
		// Get objects
		//============
		Map map = o.getWorldController().getMap();
		Rectangle b = o.getBounds();
		
		// Check collision
		//================
		return ( map.isBlocked( b.x + b.width / 2f, b.y - 1f ) );
	}
	
	// limitDelta
	//===========
	public static float limitDelta( float value )
	{
		if ( value > +Map.CELL_SIZE / 2f ) value = +Map.CELL_SIZE / 2f;
		if ( value < -Map.CELL_SIZE / 2f ) value = -Map.CELL_SIZE / 2f;
		
		return ( value );
	}
}
