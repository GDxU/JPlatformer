package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
* The {@code FormPane} is a {@code ScrollPane} without the ability to scroll and is used as a 
* container throughout the menu UI.
* @author Stefan Hösemann
 */

public class FormPane extends ScrollPane
{
	// Constructor
	//============
	public FormPane( Actor content, Skin skin )
	{
		super( content, skin );
		this.setScrollingDisabled( true, true );
	}
	
	// Constructor
	//============
	public FormPane( Actor content, Skin skin, String styleName )
	{
		super( content, skin, styleName );
		this.setScrollingDisabled( true, true );
	}
}
