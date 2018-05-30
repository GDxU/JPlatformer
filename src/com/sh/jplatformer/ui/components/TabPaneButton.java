package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

/**
* The {@code TabPaneButton} extends the original libGDX {@code ImageButton}.
* @author Stefan Hösemann
 */

public class TabPaneButton extends ImageButton
{
	// Constructor
	//============
	public TabPaneButton( Sprite sprite, Skin skin, String styleName )
	{
		super( skin, "tabpane" );	
		this.add( new Image( new TextureRegionDrawable( sprite ) ) );
	}
}
