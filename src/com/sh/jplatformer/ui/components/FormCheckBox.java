package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.sh.jplatformer.resources.Resources;

/**
* The {@code FormCheckBox} extends the original libGDX {@code CheckBox} and adapts the component
* width when no text is applied. If the {@code FormCheckBox} is clicked, a click sound will be
* played.
* @author Stefan Hösemann
 */

public class FormCheckBox extends CheckBox
{
	// Constructor
	//============
	public FormCheckBox( String text, Skin skin )
	{
		// Call super
		//===========
		super( text, skin );
		
		// Customize
		//==========
		this.align( Align.right );
		
		// Play sound on click
		//====================
		this.addListener( new ClickListener()
		{
			@Override
			public void clicked( InputEvent e, float x, float y )
			{
				Resources.UI.sound_click2.play();
			}
		} );
	}
}
