package com.sh.jplatformer.ui.editor.sidebar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.FormPane;
import com.sh.jplatformer.ui.components.TabPane;
import com.sh.jplatformer.ui.components.TabPaneButton;
import com.sh.jplatformer.ui.stages.EditorStage.EditMode;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code SidebarWindow} provides an user interface to modify a {@code WorldController}.
 * @author Stefan Hösemann
 */

public class SidebarWindow extends Table
{
	// UI components
	//==============
	public static final float WIDTH = 362f;
	
	private TabPane pne_tabs;
	
	private FileMenu   mnu_fileMenu;
	private MapMenu    mnu_mapMenu;
	private ObjectMenu mnu_objectMenu;
	private SystemMenu mnu_systemMenu;
	
	// Constructor
	//============
	/**
	 * @param worldController the {@code WorldController} to interact with.
	 */
	public SidebarWindow( Skin skin, WorldController worldController )
	{
		// Create sub menus
		//=================
		mnu_fileMenu   = new FileMenu( worldController );
		mnu_mapMenu    = new MapMenu( worldController );
		mnu_objectMenu = new ObjectMenu( worldController );
		mnu_systemMenu = new SystemMenu();

		// Create tab pane
		//================
		pne_tabs = new TabPane();

		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_file,    skin, "toggle" ), mnu_fileMenu );
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_map,     skin, "toggle" ), mnu_mapMenu );
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_objects, skin, "toggle" ), mnu_objectMenu );
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_system,  skin, "toggle" ), mnu_systemMenu );
		
		// Add components
		//===============
		this.add( new FormPane( pne_tabs, skin, "dark" ) ).grow();	
		this.pack();
		this.setVisible( true );
	}
	
	// update
	//=======
	public void update()
	{
		mnu_mapMenu.updateComponents();
		mnu_objectMenu.updateComponents();
	}
	
	// draw
	//=====
	@Override
	public void draw( Batch batch, float parentAlpha )
	{
		super.draw( batch, parentAlpha );
		
		batch.setColor( Color.WHITE );
		
		Resources.UI.skin_shadow.draw( batch,
		                               this.getX() + this.getWidth(),
		                               this.getY(),
		                               Resources.UI.skin_shadow.getRegion().getRegionWidth(),
		                               this.getHeight() );
	}
	
	// getEditMode
	//============
	public EditMode getEditMode()
	{
		if ( pne_tabs.getVisibleContent() == mnu_mapMenu )
		{
			return ( mnu_mapMenu.getEditMode() );
		}
		else if ( pne_tabs.getVisibleContent() == mnu_objectMenu )
		{
			return ( mnu_objectMenu.getEditMode() );
		}
		
		return ( EditMode.NONE );
	}
	
	// getSelectedWaterTile
	//=====================
	public int getSelectedWaterTile()
	{
		return ( mnu_mapMenu.getSelectedWaterTile() );
	}
	
	// getSelectedWaterSpeedId
	//========================
	public int getSelectedWaterSpeedId()
	{
		return ( mnu_mapMenu.getSelectedWaterSpeedId() );
	}
	
	// getSelectedMapTile
	//===================
	public int getSelectedMapTile()
	{
		return ( mnu_mapMenu.getSelectedMapTile() );
	}
	
	// getSelectedMapObject
	//=====================
	public MapObject getSelectedMapObject()
	{
		return ( mnu_objectMenu.getSelectedMapObject() );
	}
	
	// resize
	//=======
	public void resize( int width, int height )
	{
		this.setSize( WIDTH, height);
	}
}