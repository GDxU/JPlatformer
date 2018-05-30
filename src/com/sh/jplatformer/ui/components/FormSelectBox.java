package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sh.jplatformer.resources.Resources;

/**
* The {@code FormSelectBox} extends the original libGDX {@code SelectBox} and extends the internal
* padding around the text {@code Label}. If an item is selected, a click sound will be played.
* @author Stefan Hösemann
 */

public class FormSelectBox<T> extends SelectBox<T>
{
	// Fields
	//=======
	float height = new FormButton( "", Resources.UI.skin ).getPrefHeight();
	
	// Constructor
	//============
	public FormSelectBox( Skin skin )
	{
		this( skin, "default" );
	}
	
	// Constructor
	//============
	public FormSelectBox( Skin skin, String style )
	{
		// Call super
		//===========
		super( skin, style );
		
		// Play sound on click
		//====================
		this.getList().addListener( new ClickListener()
		{
			@Override
			public void clicked( InputEvent e, float x, float y )
			{
				Resources.UI.sound_click2.play();
			}
		} );
	}
	
	// getPrefHeight
	//==============
	@Override
	public float getPrefHeight()
	{
		return ( height );
	}	
}