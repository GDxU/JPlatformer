package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.sh.jplatformer.resources.Resources;

/**
* The {@code FormButton} extends the original libGDX {@code TextButton}. It removes external
* padding around the button and extends the internal padding around the text {@code Label}. In order
* to enable a uniform button look, button widths are adjusted in steps of 35 pixels so even buttons
* with short labels have the specified width. If the {@code FormButton} is clicked, a click sound
* will be played.
* @author Stefan Hösemann
 */

public class FormButton extends TextButton
{
	// Constructor
	//============
	public FormButton( String text, Skin skin )
	{
		this( text, skin, "default" );
	}
	
	// Constructor
	//============
	public FormButton( String text, Skin skin, String styleName )
	{
		// Call super
		//===========
		super( text, skin, styleName );
		
		// Customize
		//==========
		this.pad( 0f );
		
		// Play sound on click
		//====================
		this.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				Resources.UI.sound_click1.play();
			}
		} );
	}
	
	// getPrefWidth
	//=============
	@Override
	public float getPrefWidth()
	{
		// Temporary values
		//=================
		float oldWidth = super.getPrefWidth() + 14f;
		float newWidth = 0f;
		float step = 35f;
		
		// Adjust size
		//============
		for ( int i = 1; i < 999; i++ )
		{
			if ( oldWidth < step * i )
			{
				newWidth = step * i;
				break;
			}
		}
		return ( newWidth );
	}
	
	// getPrefHeight
	//==============
	@Override
	public float getPrefHeight()
	{
		float height = super.getPrefHeight() + 6f;
		return ( height );
	}	
}
