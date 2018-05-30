package com.sh.jplatformer.ui.editor.sidebar;

import java.util.ArrayList;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;
import com.sh.jplatformer.world.objects.characters.AcidDrop;
import com.sh.jplatformer.world.objects.characters.AngryBall;
import com.sh.jplatformer.world.objects.characters.Block;
import com.sh.jplatformer.world.objects.characters.CartonGuy;
import com.sh.jplatformer.world.objects.characters.Chaser;
import com.sh.jplatformer.world.objects.characters.Ghost;
import com.sh.jplatformer.world.objects.characters.Gland;
import com.sh.jplatformer.world.objects.characters.Hanger;
import com.sh.jplatformer.world.objects.characters.Jumper;
import com.sh.jplatformer.world.objects.characters.Player;
import com.sh.jplatformer.world.objects.characters.RocketGuy;
import com.sh.jplatformer.world.objects.characters.Skater;
import com.sh.jplatformer.world.objects.characters.Snail;
import com.sh.jplatformer.world.objects.characters.Spider;
import com.sh.jplatformer.world.objects.characters.Spikehead;
import com.sh.jplatformer.world.objects.characters.Spitter;
import com.sh.jplatformer.world.objects.characters.SpringboardMachine;
import com.sh.jplatformer.world.objects.items.ChocolateBar;
import com.sh.jplatformer.world.objects.items.Diamond;
import com.sh.jplatformer.world.objects.items.Donut;
import com.sh.jplatformer.world.objects.items.GoldenCup;
import com.sh.jplatformer.world.objects.items.Meat;
import com.sh.jplatformer.world.objects.items.Mushroom;
import com.sh.jplatformer.world.objects.items.PiggyBank;
import com.sh.jplatformer.world.objects.items.Pizza;
import com.sh.jplatformer.world.objects.items.Popsicle;
import com.sh.jplatformer.world.objects.items.Treasure;
import com.sh.jplatformer.world.objects.items.YellowThing;
import com.sh.jplatformer.world.objects.machines.Box;
import com.sh.jplatformer.world.objects.machines.CannonDown;
import com.sh.jplatformer.world.objects.machines.CannonLeft;
import com.sh.jplatformer.world.objects.machines.CannonRight;
import com.sh.jplatformer.world.objects.machines.CannonUp;
import com.sh.jplatformer.world.objects.machines.ElectricTrap;
import com.sh.jplatformer.world.objects.machines.Platform1;
import com.sh.jplatformer.world.objects.machines.Platform2;
import com.sh.jplatformer.world.objects.machines.SpikesDown;
import com.sh.jplatformer.world.objects.machines.SpikesUp;
import com.sh.jplatformer.world.objects.machines.Springboard;
import com.sh.jplatformer.world.objects.machines.Switch;
import com.sh.jplatformer.world.objects.machines.TreadmillLeft;
import com.sh.jplatformer.world.objects.machines.TreadmillRight;

/**
 * The {@code ObjectMenu} provides a tabbed menu structure for {@code MapObjects} placement and
 * modification.
 * @author Stefan Hösemann
 */

public class ObjectMenu extends Table
{
	// Constants
	//==========
	private static final String OBJECT_TYPE_CHARACTERS = Lang.txt( "editor_objects_characters" );
	private static final String OBJECT_TYPE_MACHINES   = Lang.txt( "editor_objects_machines" );
	private static final String OBJECT_TYPE_ITEMS      = Lang.txt( "editor_objects_items" );
	
	// Fields
	//=======
	private WorldController worldController;
	private Skin skin;
	
	private EditMode editMode;
	private int lastSize;
	private int selectedObject;
	
	// Components
	//===========
	private TabPane pne_tabs;

	private Actor mnu_objects;
	private Actor mnu_attributes;
	private Actor mnu_noObjectMarked;
	
	private Stack stk_attributes;
	private Table tbl_objects;
	
	private FormSelectBox<String> slc_objectType;
	private TextField             txt_attr_powerId;
	private FormCheckBox          chk_attr_powerOn;
	
	// Object types
	//=============
	private String[] availableObjectTypes =
	{
		OBJECT_TYPE_CHARACTERS,
		OBJECT_TYPE_MACHINES,
		OBJECT_TYPE_ITEMS
	};
	
	// Available characters
	//=====================
	private MapObject[] availableCharacters =
	{
		new Player( null ),
		new AcidDrop( null ),
		new AngryBall( null ),
		new Block ( null ),
		new CartonGuy( null ),
		new Chaser( null ),
		new Ghost( null ),
		new Gland( null ),
		new Hanger( null ),
		new Jumper( null ),
		new RocketGuy( null ),
		new Skater( null ),
		new Snail( null ),
		new Spider( null ),
		new Spikehead( null ),
		new Spitter( null ),
		new SpringboardMachine( null ),
	};

	// Available machines
	//===================
	private MapObject[] availableMachines =
	{
		new Box( null ),
		new Springboard( null ),
		new SpikesDown( null ),
		new SpikesUp( null ),
		new Switch( null ),
		new Platform1( null ),
		new Platform2( null ),
		new CannonRight( null ),
		new CannonLeft( null ),
		new CannonDown( null ),
		new CannonUp( null ),
		new ElectricTrap( null ),
		new TreadmillRight( null ),
		new TreadmillLeft( null ),
	};
	
	// Available items
	//================
	private MapObject[] availableItems =
	{
		new Donut( null ),
		new ChocolateBar( null ),
		new Popsicle( null ),
		new Meat( null ),
		new Pizza( null ),
		new Mushroom( null ),
		new YellowThing( null ),
		new GoldenCup( null ),
		new Diamond( null ),
		new PiggyBank( null ),
		new Treasure( null )				
	};
	
	// Constructor
	//============
	/**
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public ObjectMenu( WorldController newWorldController )
	{
		// Setup
		//======
		worldController = newWorldController;
		skin = Resources.UI.skin;
		editMode = EditMode.OBJECTS;
		
		// Create sub menus
		//=================
		mnu_objects        = new SidebarScrollPane( createObjectSelectionMenu(), skin, "transparent" );
		mnu_attributes     = createAttributesMenu();
		mnu_noObjectMarked = createNoObjectMarkedMenu();
		
		stk_attributes = new Stack();
		stk_attributes.add( mnu_attributes );
		stk_attributes.add( mnu_noObjectMarked );

		// Create tab pane
		//================
		pne_tabs = new TabPane();
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_obj_place, skin, "toggle" ),
		                 mnu_objects );
		
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_obj_attributes, skin, "toggle" ),
		                 new SidebarScrollPane( stk_attributes, skin, "transparent" ) );
		
		// Change listener
		//================
		pne_tabs.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent event, Actor actor )
			{				
				Actor content = pne_tabs.getVisibleContent();
				if ( content == mnu_objects ) editMode = EditMode.OBJECTS;
				if ( content == stk_attributes ) editMode = EditMode.ATTRIBUTES;
			}
		} );
		
		// Wrap in form pane
		//==================
		this.add( new FormPane( pne_tabs, skin ) ).top().expand().fill();
	}
	
	// act
	//====
	@Override
	public void act( float delta )
	{
		super.act( delta );
		
		if ( worldController.getMarkedMapObjects().size() != lastSize )
		{
			updateComponents();
		}
	}
	
	// createObjectSelectionMenu
	//==========================
	private Actor createObjectSelectionMenu()
	{
		// Object type select box
		//========================
		slc_objectType = new FormSelectBox<String>( skin );
		slc_objectType.setItems( availableObjectTypes );	
		tbl_objects = new Table();
		
		// Change object types
		//====================
		slc_objectType.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				// Reset values
				//=============
				selectedObject = 0;
				tbl_objects.clear();
				
				// Update objects table
				//=====================
				if ( slc_objectType.getSelected() == OBJECT_TYPE_CHARACTERS ) createCharactersTable();
				if ( slc_objectType.getSelected() == OBJECT_TYPE_MACHINES   ) createMachinesTable();
				if ( slc_objectType.getSelected() == OBJECT_TYPE_ITEMS      ) createItemsTable();
			}
		} );
		
		// Init object type select box
		//============================
		slc_objectType.fire( new ChangeListener.ChangeEvent() );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_objects_placeObjects" ), skin, "heading" ) ).left();
		tbl_main.row();
		
		// Button pane
		//============
		tbl_main.add( slc_objectType ).colspan( 2 ).row();
		tbl_main.add( new SidebarScrollPane( tbl_objects, skin, "dark" ) ).colspan( 2 );
		
		return ( tbl_main );
	}
	
	// createAttributesMenu
	//=====================
	private Actor createAttributesMenu()
	{	
		// Text fields
		//============
		txt_attr_powerId = new TextField( "", skin );
		txt_attr_powerId.setTextFieldFilter( new TextFieldFilter.DigitsOnlyFilter() );
		txt_attr_powerId.setMaxLength( 4 );
		
		// Check box
		//==========
		chk_attr_powerOn = new FormCheckBox ( "", skin );
		
		// Button: Apply
		//==============
		FormButton btn_apply = new FormButton( Lang.txt( "editor_objects_apply" ), skin );
		
		btn_apply.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				// Adjust values
				//==============
				if ( txt_attr_powerId.getText().length() == 0 )
				{
					txt_attr_powerId .setText( "0" );
				}
				
				// Apply attributes
				//=================
				for ( MapObject o : worldController.getMarkedMapObjects() )
				{
					o.setPowerId( Integer.parseInt( txt_attr_powerId.getText() ) );
					o.setPowerOn( chk_attr_powerOn.isChecked() );
				}
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_objects_attributes" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();

		// Power ID
		//=========
		tbl_main.add( new Label( Lang.txt( "editor_objects_powerId" ), skin ) ).left();
		tbl_main.add( txt_attr_powerId ).width( 50f ).right();
		tbl_main.row();
		
		// Power On
		//=========
		tbl_main.add( new Label( Lang.txt( "editor_objects_powerEnabled" ), skin ) ).left();
		tbl_main.add( chk_attr_powerOn ).right();
		tbl_main.row();
		
		// Apply button
		//=============
		tbl_main.add( btn_apply ).colspan( 2 )
		                         .expand( false, false )
		                         .fill( false, false )
		                         .right();
		
		tbl_main.setVisible( false );
		
		return ( tbl_main );
	}
	
	// createNoObjectMarkedMenu
	//=========================
	private Actor createNoObjectMarkedMenu()
	{
		// Table setup
		//============
		Table tbl_main = new SidebarTable();
		
		// Heading
		//========
		tbl_main.add( new Label( Lang.txt( "editor_objects_noObjectMarked" ), skin, "heading" ) ).left();
		tbl_main.row();
		
		// Hint label setup
		//=================
		Label lbl_hint = new Label( Lang.txt( "editor_objects_objectHint" ), skin, "default" );
		lbl_hint.setWrap( true );
		
		// Hint label
		//===========
		tbl_main.add( lbl_hint ).left();
		tbl_main.row();
				
		return ( tbl_main );
	}
	
	// createCharactersTable
	//======================
	private void createCharactersTable()
	{
		// Initialize values
		//==================
		ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>();
		MapObject[] objects = availableCharacters;
		
		// Create buttons list
		//====================
		for ( int i = 0; i < objects.length; i++ )
		{
			// Temporary values
			//=================
			Button btn = new Button( skin, "tabpane" );
			Image  img = new Image( objects[i].getFrames()[0] );
			float imgW = img.getWidth() / ( img.getWidth() / Map.CELL_SIZE );
			float imgH = Map.CELL_SIZE;
			
			// Modify button
			//==============
			btn.pad( 4f );
			btn.add( img ).size( imgW, imgH ).left();
			btn.add( new Label( objects[i].getName(), skin ) ).expandX().left().padLeft( 30f );
			btn.addListener( new ClickListener()
			{
				@Override
				public void clicked( InputEvent event, float x, float y )
				{
					selectedObject = buttonGroup.getCheckedIndex();
					Resources.UI.sound_click2.play();
				}
			} );
			
			// Add to group + table
			//=====================
			buttonGroup.add( btn );
			tbl_objects.add( btn ).expandX().fillX().space( 4f ).row();
		}
	}
	
	// createMachinesTable
	//====================
	private void createMachinesTable()
	{
		// Initialize values
		//==================
		ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>();
		MapObject[] objects = availableMachines;
		
		// Create buttons list
		//====================
		for ( int i = 0; i < objects.length; i++ )
		{
			// Description label
			//==================
			Table lbl = new Table();
			lbl.padLeft( 30f );
			lbl.add( new Label(  objects[i].getName(), skin, "default" ) ).expandX().left();
			lbl.row();
			
			// Add power hint
			//===============
			if ( objects[i].isPowerSupported() )
			{
				lbl.add( new Label( Lang.txt( "editor_objects_powerRequired" ), skin, "heading" ) ).left();
			}
			else
			{
				Label lbl_tmp = new Label( Lang.txt( "editor_objects_noPowerRequired" ), skin, "heading" );
				lbl_tmp.getColor().a = 0.3f;
				lbl.add( lbl_tmp ).left();
			}
			
			// Temporary values
			//=================
			Button btn = new Button( skin, "tabpane" );
			Image  img = new Image( objects[i].getFrames()[0] );
			float imgW = img.getWidth() / ( img.getWidth() / Map.CELL_SIZE );
			float imgH = Map.CELL_SIZE;
			
			// Modify button
			//==============
			btn.pad( 4f );
			btn.add( img ).size( imgW, imgH ).left();
			btn.add( lbl ).expandX().fillX();
			btn.addListener( new ClickListener()
			{
				@Override
				public void clicked( InputEvent event, float x, float y )
				{
					selectedObject = buttonGroup.getCheckedIndex();
					Resources.UI.sound_click2.play();
				}
			} );
			
			// Add to group + table
			//=====================
			buttonGroup.add( btn );
			tbl_objects.add( btn ).expandX().fillX().height( Map.CELL_SIZE + 4f ).space( 4f ).row();
		}
	}
	
	// createItemsTable
	//=================
	private void createItemsTable()
	{
		// Initialize values
		//==================
		ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>();
		MapObject[] objects = availableItems;
		
		// Create buttons list
		//====================
		for ( int i = 0; i < objects.length; i++ )
		{
			// Description label
			//==================
			Table lbl = new Table();
			lbl.padLeft( 30f );
			lbl.add( new Label(  objects[i].getName(), skin, "default" ) ).expandX().left();
			lbl.row();
			
			// Add score hint
			//===============
			Label lbl_tmp = new Label( Lang.txt( "editor_objects_score" ) + " " + objects[i].getScore(), skin, "default" );
			lbl_tmp.getColor().a = 0.75f;
			lbl.add( lbl_tmp ).left();
			
			// Temporary values
			//=================
			Button btn = new Button( skin, "tabpane" );
			Image  img = new Image( objects[i].getFrames()[0] );
			float imgW = img.getWidth() / ( img.getWidth() / Map.CELL_SIZE );
			float imgH = Map.CELL_SIZE;
			
			// Modify button
			//==============
			btn.pad( 4f );
			btn.add( img ).size( imgW, imgH ).left();
			btn.add( lbl ).expandX().fillX();
			btn.addListener( new ClickListener()
			{
				@Override
				public void clicked( InputEvent event, float x, float y )
				{
					selectedObject = buttonGroup.getCheckedIndex();
					Resources.UI.sound_click2.play();
				}
			} );
			
			// Add to group + table
			//=====================
			buttonGroup.add( btn );
			tbl_objects.add( btn ).expandX().fillX().height( Map.CELL_SIZE + 4f ).space( 4f ).row();
		}
	}
	
	// updateComponents
	//=================
	public void updateComponents()
	{
		// Temporary values
		//=================
		ArrayList<MapObject> markedObjects = worldController.getMarkedMapObjects();
		lastSize = markedObjects.size();
		
		// Update attributes components
		//=============================
		if ( lastSize > 0 )
		{
			// Update components
			//==================
			txt_attr_powerId.setText   ( markedObjects.get( lastSize - 1 ).getPowerId() + "" );
			chk_attr_powerOn.setChecked( markedObjects.get( lastSize - 1 ).isPowerOn() );
			
			// Swap content
			//=============
			if ( stk_attributes.getChildren().get( 0 ).isVisible() == false )
			{
				stk_attributes.getChildren().get( 0 ).setVisible( true );
				stk_attributes.getChildren().get( 1 ).setVisible( false );
			}
		}
		else
		{
			// Swap content
			//=============
			stk_attributes.getChildren().get( 0 ).setVisible( false );
			stk_attributes.getChildren().get( 1 ).setVisible( true );	
			return;
		}
	}
	
	// getEditMode
	//============
	public EditMode getEditMode()
	{
		return ( editMode );
	}
	
	// getSelectedMapObject
	//=====================
	public MapObject getSelectedMapObject()
	{
		String selected = slc_objectType.getSelected();
		
		if ( selected == OBJECT_TYPE_CHARACTERS ) return ( availableCharacters[selectedObject] );
		if ( selected == OBJECT_TYPE_MACHINES )   return ( availableMachines  [selectedObject] );
		if ( selected == OBJECT_TYPE_ITEMS )      return ( availableItems     [selectedObject] );
		
		return ( null );
	}
}