package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
* The {@code SidebarScrollPane} extends the original libGDX {@code ScrollPane} and comes with
* predefined properties adapted for the editor {@code SidebarWindow} UI.
* @author Stefan Hösemann
 */

public class SidebarScrollPane extends ScrollPane
{
	// Constructor
	//============
	public SidebarScrollPane( Actor content, Skin skin )
	{
		this( content, skin, "default" );
	}
	
	// Constructor
	//============
	public SidebarScrollPane( Actor content, Skin skin, String styleName )
	{
		super( content, skin, styleName );
		
		this.setScrollbarsOnTop( true );
		this.setFadeScrollBars( false );
		this.setFlickScroll( false );
		this.setScrollingDisabled( true, false );
	}
}
