package com.sh.jplatformer.ui.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sh.jplatformer.Config;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.UiConstants;
import com.sh.jplatformer.ui.editor.sidebar.SidebarWindow;
import com.sh.jplatformer.ui.input.EditorInput;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.world.WorldCamera;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapCell;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code EditorStage} provides a stage for the editor UI.
 * @author Stefan Hösemann
 */

public class EditorStage extends Stage
{
	// Enums
	//======
	public enum DragMode
	{
		NONE, PLACE, REMOVE, HOVER, TRIGGER
	}
	
	public enum EditMode
	{
		NONE, ENVIRONMENT, CELLS, TRIGGERS, OBJECTS, ATTRIBUTES
	}

	// World
	//======
	private WorldController worldController;
	private WorldCamera worldCamera;
	
	// UI components
	//==============
	private SidebarWindow sidebarWindow;
	private Label lbl_info;
	private StringBuilder infoText;
	
	// Input
	//======
	private Vector2 scrollCenter;
	private Rectangle mouseArea;
	private Rectangle dragArea;
	private Rectangle drawArea;
	private DragMode dragMode;
	private boolean ctrlPressed;
	private boolean altPressed;
	
	// Helpers
	//========
	private Vector3 helperVector3;
	private Rectangle helperRectangle;

	// Constructor
	//============
	/**
	 * @param newWorldController the {@code WorldController} to modify.
	 */
	public EditorStage( WorldController newWorldController )
	{
		// Viewport / camera
		//==================
		super( new ScreenViewport() );
		worldCamera = newWorldController.getWorldCamera();
		
		// World controller
		//=================
		worldController = newWorldController;
		worldController.setLive( false );
		
		// Mouse drag
		//===========
		mouseArea = new Rectangle();
		dragArea = new Rectangle();
		drawArea = new Rectangle();
		dragMode = DragMode.NONE;
		
		// Helpers
		//========
		helperVector3 = new Vector3();
		helperRectangle = new Rectangle();
		
		// Sidebar setup
		//==============
		lbl_info       = new Label( "", Resources.UI.skin, "popup" );
		infoText       = new StringBuilder( "" );
		sidebarWindow  = new SidebarWindow( Resources.UI.skin, worldController );
		
		this.addActor( sidebarWindow );		

		// Unselect text fields when mouse clicks
		//=======================================
		this.getRoot().addCaptureListener( new InputListener()
		{
			public boolean touchDown ( InputEvent event, float x, float y, int pointer, int button )
			{
				// Release text field
				//===================
				if ( !( event.getTarget() instanceof TextField ) )
				{
					setKeyboardFocus(null);
				}
				
				// Release scroll pane
				//====================
				if ( !( event.getTarget() instanceof ScrollPane ) )
				{
					if ( getScrollFocus() != null )
					{
						unfocus( getScrollFocus() );
					}
				}
				return ( false );
			}
		} );
	}
	
	// resize
	//=======
	/**
	 * Updates viewport and sidebar on resize.
	 * @param width the new width.
	 * @param height the new height.
	 */
	public void resize( int width, int height )
	{
		this.getViewport().update( width, height, true );
		sidebarWindow.resize( width, height );
	}
	
	// update
	//=======
	public void update()
	{
		sidebarWindow.update();
	}
	
	// draw
	//=====
	@Override
	public void draw()
	{
		// Begin batch
		//============
		this.getBatch().begin();
		{
			// Info displays
			//==============
			this.drawInfoLabel();
			
			// Draw UI if world size > 0x0
			//============================
			if ( worldController.getMap().getColumns() > 0 ||
			     worldController.getMap().getRows()    > 0 )
			{
				// Editing displays
				//=================
				this.drawMapCellDisplays();
				this.drawMapObjectDisplays();
				this.drawTriggerAreas();
				this.drawScrollCenter();
				this.drawZoomLabel();
			}	
		}
		this.getBatch().end();
		
		// Super draw
		//===========
		super.draw();
	}
	
	// drawInfoLabel
	//==============
	private void drawInfoLabel()
	{
		// Clear string
		//=============
		infoText.delete( 0, infoText.length() );
		
		// Pause text
		//===========
		if ( JPlatformerGame.get().isPaused() )
		{
			infoText.append( "Pause" );	
		}
		
		// FPS text
		//=========
		if ( Config.get().editor_showFps == true )
		{
			if ( JPlatformerGame.get().isPaused() )
			{
				infoText.insert( 0, Gdx.graphics.getFramesPerSecond() + " FPS" + "\n" );
			}
			else
			{
				infoText.insert( 0, Gdx.graphics.getFramesPerSecond() + " FPS" );
			}
		}
		
		// Draw label
		//===========
		if ( infoText.length() > 0 )
		{
			lbl_info.setText( infoText.toString() );
			lbl_info.pack();
			lbl_info.setX( getWidth()  - lbl_info.getWidth()  * 1f - UiConstants.BORDER_BIG );
			lbl_info.setY( getHeight() - lbl_info.getHeight() * 1f - UiConstants.BORDER_BIG );
			lbl_info.setAlignment( Align.right );
			lbl_info.draw( this.getBatch(), 1f );
		}
	}
	
	// drawZoomLabel
	//==============
	private void drawZoomLabel()
	{
		int value = (int) ( ( 1f / worldCamera.zoom ) * 100f );
		
		lbl_info.setText( Lang.txt( "editor_zoom" ) + " " + value + "%" );
		lbl_info.pack();
		lbl_info.setX( getWidth() - lbl_info.getWidth() * 1f - UiConstants.BORDER_BIG );
		lbl_info.setY( UiConstants.BORDER_BIG );
		lbl_info.draw( this.getBatch(), 1f );
	}
	
	// drawMapCellDisplays
	//====================
	/**
	 * Draws all {@code Map} and {@code MapCell} related highlighting and editing graphics.
	 */
	private void drawMapCellDisplays()
	{
		// Get objects
		//============
		Vector2 mouse = EditorInput.getMousePosInWorld();
		Map map = worldController.getMap();
		
		// Map tile highlighting
		//======================
		if ( sidebarWindow.getEditMode() == EditMode.CELLS )
		{
			// Actual drag area
			//=================
			if ( dragMode == DragMode.PLACE || dragMode == DragMode.REMOVE )
			{
				this.drawDragArea( dragArea, dragMode );
			}
			
			// Tile-based highlighting
			//========================
			if ( ctrlPressed == false )
			{
				for ( MapCell cell : worldController.getVisibleCells() )
				{
					// Get hovered cell position
					//==========================
					mouseArea.x      = Map.CELL_SIZE * cell.x;
					mouseArea.y      = Map.CELL_SIZE * cell.y;
					mouseArea.width  = Map.CELL_SIZE;
					mouseArea.height = Map.CELL_SIZE;
					
					// Hover highlight
					//================
					if ( dragMode == DragMode.NONE && mouseArea.contains( mouse ) )
					{
						this.drawDragArea( mouseArea, DragMode.HOVER );
					}
					
					// Drag highlights
					//================
					if ( ( dragMode == DragMode.PLACE ||
					       dragMode == DragMode.REMOVE ) && dragArea.overlaps( mouseArea ) )
					{
						// Placement highlight
						//====================
						if ( dragMode == DragMode.PLACE )
						{
							this.drawDragArea( mouseArea, dragMode );
						}
						
						// Removement highlight
						//=====================
						if ( dragMode == DragMode.REMOVE )
						{
							if ( cell.tileSetId > -1 )
							{
								this.drawDragArea( mouseArea, dragMode );
							}
						}
					}	
				}

				// Mouse label setup
				//==================
				if ( dragMode != DragMode.NONE )
				{
					int x1 = map.getCellAt( dragArea.x, dragArea.y ).x;
					int y1 = map.getCellAt( dragArea.x, dragArea.y ).y;
					
					int x2 = map.getCellAt( mouse.x, mouse.y ).x;
					int y2 = map.getCellAt( mouse.x, mouse.y ).y;
			
					if ( x2 == x1 ) x2 = map.getCellAt( dragArea.x + dragArea.width, dragArea.y + dragArea.height ).x;
					if ( y2 == y1 ) y2 = map.getCellAt( dragArea.x + dragArea.width, dragArea.y + dragArea.height ).y;
					
					int w = -( x1 - x2 ) + 1;
					int h = -( y1 - y2 ) + 1;
					
					// Check if inside map
					//====================
					if ( dragArea.overlaps( worldController.getMapBounds() ) )
					{
						lbl_info.setText( w + " x " + h );
						lbl_info.setPosition( EditorInput.getMousePos().x, EditorInput.getMousePos().y );
						lbl_info.pack();
						lbl_info.draw( this.getBatch(), 1f );
					}
				}
			}
		}
	}
	
	// drawMapObjectDisplays
	//======================
	/**
	 * Draws all {@code MapObject} related highlighting and editing graphics.
	 */
	private void drawMapObjectDisplays()
	{
		// Reset hover
		//============
		worldController.setHoveredMapObject( null );
		
		// Return condition
		//=================
		if ( sidebarWindow.getEditMode() != EditMode.OBJECTS &&
		     sidebarWindow.getEditMode() != EditMode.ATTRIBUTES )
		{
			return;
		}
		
		// Actual removement drag area
		//============================
		if ( dragMode == DragMode.REMOVE )
		{
			this.drawDragArea( dragArea, dragMode );
		}
		
		// Map objects
		//============
		for ( MapObject o : worldController.getMapObjects() )
		{	
			// Removement highlight
			//=====================
			if ( dragMode == DragMode.REMOVE && dragArea.overlaps( o.getBounds() ) )
			{
				this.drawDragArea( o.getBounds(), dragMode );
			}
			
			// Mouse hover highlight
			//======================
			if ( dragMode == DragMode.NONE && ctrlPressed == true )
			{
				if ( o.getBounds().contains( EditorInput.getMousePosInWorld() ) )
				{
					worldController.setHoveredMapObject( o );
					this.drawDragArea( o.getBounds(), DragMode.HOVER );
				}
			}
			
			// Highlight marked objects
			//=========================
			if ( worldController.getMarkedMapObjects().contains( o ) )
			{	
				this.drawDragArea( o.getBounds(), DragMode.HOVER );
			}
			
			// Draw object power label
			//========================
			if ( Config.get().editor_showPowerInfo )
			{
				if ( o.isPowerSupported() == true )
				{
					// Init text
					//==========
					Rectangle pos = projectRectangle( o.getBounds() );
					
					infoText.setLength( 0 );
					infoText.append( "ID: " + o.getPowerId() + "\n" );
					
					if ( o.isPowerOn() == true  ) infoText.append( Lang.txt( "game_on" ) );
					if ( o.isPowerOn() == false ) infoText.append( Lang.txt( "game_off" ) );
					
					// Label setup
					//============
					lbl_info.setText( infoText.toString() );
					lbl_info.setX( pos.x );
					lbl_info.setY( pos.y + pos.height );
					lbl_info.setAlignment( Align.left );
					lbl_info.pack();
					lbl_info.draw( this.getBatch(), 1f );
				}
			}
		}
		
		// Draw cursor ghost
		//==================
		if ( sidebarWindow.getEditMode() != EditMode.ATTRIBUTES && !ctrlPressed )
		{
			if ( dragMode != DragMode.REMOVE )
			{
				// Get objects + temporary values
				//===============================
				MapObject obj  = sidebarWindow.getSelectedMapObject();
				Vector2 tmpPos = EditorInput.getMousePosInWorld();
				float tmpSize  = obj.getFrameSize() / worldCamera.zoom;
				
				// Draw ghost
				//===========
				if ( worldController.getMapBounds().contains( tmpPos ) && obj != null )
				{
					// Set ghost coordinates
					//======================
					if ( altPressed == false )
					{
						// Align within tile
						//==================
						float cellX = (int) ( tmpPos.x / Map.CELL_SIZE ) * Map.CELL_SIZE;
						float cellY = (int) ( tmpPos.y / Map.CELL_SIZE ) * Map.CELL_SIZE;
						
						// Horizontal alignment
						//=====================
						if ( obj.getHorizontalAlignment() == MapObject.ALIGN_LEFT )
						{
							obj.getBounds().x = cellX;
						}
						else if ( obj.getHorizontalAlignment() == MapObject.ALIGN_CENTER )
						{
							obj.getBounds().x = cellX + Map.CELL_SIZE / 2f - obj.getBounds().width / 2f;
						}
						else if ( obj.getHorizontalAlignment() == MapObject.ALIGN_RIGHT )
						{
							obj.getBounds().x = cellX + Map.CELL_SIZE - obj.getBounds().width;
						}
						
						// Vertical alignment
						//===================
						if ( obj.getVerticalAlignment() == MapObject.ALIGN_TOP )
						{
							obj.getBounds().y = cellY + Map.CELL_SIZE - obj.getBounds().height;
						}
						else if ( obj.getVerticalAlignment() == MapObject.ALIGN_CENTER )
						{
							obj.getBounds().y = cellY + Map.CELL_SIZE / 2f - obj.getBounds().height / 2f;
						}
						else if ( obj.getVerticalAlignment() == MapObject.ALIGN_BOTTOM )
						{
							obj.getBounds().y = cellY;
						}
						
						// Adjust coords
						//==============
						obj.getBounds().x -= ( obj.getFrameSize() - obj.getBounds().width ) / 2f;
					}
					else
					{
						// Direct mouse coords
						//====================
						obj.getBounds().x = tmpPos.x - ( tmpSize / 2f ) * worldCamera.zoom;
						obj.getBounds().y = tmpPos.y - ( tmpSize / 2f ) * worldCamera.zoom;
					}
					
					// Convert size
					//=============
					tmpSize = worldCamera.toUnits( tmpSize );
					
					// Draw ghost
					//===========
					obj.getFrames()[0].setAlpha( 0.2f );
					obj.getFrames()[0].setX    ( projectRectangle( obj.getBounds() ).x );
					obj.getFrames()[0].setY    ( projectRectangle( obj.getBounds() ).y );
					obj.getFrames()[0].setSize ( tmpSize, tmpSize );	
					obj.getFrames()[0].draw    ( this.getBatch() );
				}
			}
		}
		
		// Mark objects cursor
		//====================
		if ( ctrlPressed )
		{
			// Draw icon
			//==========
			Vector2 pos = EditorInput.getMousePos();
			
			Resources.UI.editor_icon_markObject.setX( pos.x );
			Resources.UI.editor_icon_markObject.setY( pos.y );
			Resources.UI.editor_icon_markObject.draw( this.getBatch() );
			
			// Draw number label
			//==================
			if ( worldController.getMarkedMapObjects().size() > 0 )
			{
				lbl_info.setText( worldController.getMarkedMapObjects().size() + "" );
				lbl_info.setPosition( pos.x, pos.y );
				lbl_info.pack();
				lbl_info.draw( this.getBatch(), 1f );
			}
		}
	}

	// drawTriggerAreas
	//=================
	/**
	 * Draws all trigger areas (start and finish area).
	 */
	private void drawTriggerAreas()
	{
		// Temporary objects
		//==================
		Rectangle start  = worldController.getMap().getStartArea();
		Rectangle finish = worldController.getMap().getFinishArea();
		
		// Start area
		//===========
		this.drawDragArea( start, DragMode.TRIGGER );
		
		lbl_info.setText( Lang.txt( "editor_start" ) );
		lbl_info.pack   ();
		lbl_info.setX   ( drawArea.x + 5f );
		lbl_info.setY   ( drawArea.y - 5f + drawArea.height - lbl_info.getHeight() );
		lbl_info.draw   ( this.getBatch(), 1f );

		// Finish area
		//============
		this.drawDragArea( finish, DragMode.TRIGGER );
		
		lbl_info.setText( Lang.txt( "editor_finish" ) );
		lbl_info.pack();
		lbl_info.setX( drawArea.x + 5f );
		lbl_info.setY( drawArea.y - 5f + drawArea.height - lbl_info.getHeight() );
		lbl_info.draw( this.getBatch(), 1f );
	}
	
	// drawScrollCenter
	//=================
	private void drawScrollCenter()
	{
		if ( scrollCenter != null )
		{
			int x = (int) ( scrollCenter.x - Resources.UI.editor_icon_scrollCenter.getWidth()  / 2f );
			int y = (int) ( scrollCenter.y - Resources.UI.editor_icon_scrollCenter.getHeight() / 2f );
			
			Resources.UI.editor_icon_scrollCenter.setPosition( x, y );
			Resources.UI.editor_icon_scrollCenter.draw( this.getBatch() ) ;
		}
	}
	
	// drawDragArea
	//=============
	/**
	 * Draws the drag area rectangle.
	 * @param pos the area in world coordinates.
	 * @param mode the {@code DragMode}.
	 */
	private void drawDragArea( Rectangle pos, DragMode mode )
	{
		// Get sprite
		//===========
		Sprite tmp_sprite = null;
		
		if ( mode == DragMode.PLACE   ) tmp_sprite = Resources.UI.editor_rect_green;
		if ( mode == DragMode.REMOVE  ) tmp_sprite = Resources.UI.editor_rect_red;
		if ( mode == DragMode.HOVER   ) tmp_sprite = Resources.UI.editor_rect_white;
		if ( mode == DragMode.TRIGGER ) tmp_sprite = Resources.UI.editor_rect_yellow;
		
		// Draw sprite
		//============
		if ( tmp_sprite != null )
		{
			// Project area
			//=============
			drawArea.set( pos );
			drawArea.width  = worldCamera.toUnits( drawArea.width );
			drawArea.height = worldCamera.toUnits( drawArea.height );
			drawArea = projectRectangle( drawArea );
			
			// Draw sprite
			//============
			tmp_sprite.setPosition( drawArea.x, drawArea.y );
			tmp_sprite.setSize    ( drawArea.width, drawArea.height);
			tmp_sprite.draw       ( this.getBatch() );
		}
	}
	
	// projectRectangle
	//=================
	private Rectangle projectRectangle( Rectangle area )
	{
		// Project rectangle origin
		//=========================
		Rectangle rect = helperRectangle.set( area );
		worldCamera.project( helperVector3.set( rect.x, rect.y, 0f ) );
		rect.x = helperVector3.x;
		rect.y = helperVector3.y;
		
		// Convert dimensions
		//===================
		rect.width  = +rect.width / worldCamera.zoom;
		rect.height = -rect.height / worldCamera.zoom;
		
		// Handle negative dimensions
		//===========================
		if ( rect.width < 0f )
		{
			rect.width = -rect.width;
			rect.x = rect.x - rect.width;
		}
		if ( rect.height < 0f )
		{
			rect.height = -rect.height;
		}
		return ( rect );
	}
	
	// touchDown
	//==========
	@Override
	public boolean touchDown( int x, int y, int pointer, int button )
	{
		// Call super
		//===========
		super.touchDown( x, y, pointer, button);
		
		// Catch mouse clicks on sidebar
		//==============================
		if ( sidebarWindow.isVisible() )
		{
			helperRectangle.x      = sidebarWindow.getX();
			helperRectangle.y      = sidebarWindow.getY();
			helperRectangle.width  = sidebarWindow.getWidth();
			helperRectangle.height = sidebarWindow.getHeight();
		}
		return ( helperRectangle.contains( EditorInput.getMousePos() ) );
	}
	
	// setSidebarVisible
	//==================
	public void setSidebarVisible( boolean visible )
	{
		sidebarWindow.setVisible( visible );
	}
	
	// isSidebarVisible
	//=================
	public boolean isSidebarVisible()
	{
		return ( sidebarWindow.isVisible() );
	}
	
	// setScrollCenter
	//================
	public void setScrollCenter( Vector2 scrollCenter )
	{	
		this.scrollCenter = scrollCenter;
	}
	
	// setDragArea
	//============
	public void setDragArea( DragMode dragMode, Rectangle dragArea )
	{
		this.dragMode = dragMode;
		this.dragArea = dragArea;
	}
	
	// getDragArea
	//============
	public Rectangle getDragArea()
	{
		return ( dragArea );
	}
	
	// setCtrlPressed
	//===============
	public void setCtrlPressed( boolean ctrlPressed )
	{
		this.ctrlPressed = ctrlPressed;
	}
	
	// setAltPressed
	//==============
	public void setAltPressed( boolean altPressed )
	{
		this.altPressed = altPressed;
	}
	
	// getWorldController
	//===================
	public WorldController getWorldController()
	{
		return ( worldController );
	}

	// getSidebarWindow
	//=================
	public SidebarWindow getSidebarWindow()
	{
		return ( sidebarWindow );
	}
	
	// getEditMode
	//============
	public EditMode getEditMode()
	{
		return ( sidebarWindow.getEditMode() );
	}
}