package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
* The {@code SidebarTable} extends the original libGDX {@code Table} and comes with
* predefined properties adapted for the editor {@code SidebarWindow} UI.
* @author Stefan Hösemann
 */

public class SidebarTable extends Table
{
	// Constructor
	//============
	public SidebarTable()
	{
		// Call super
		//===========
		super();
		
		// Properties
		//===========
		this.top()
		    .left()
		    .pad( UiConstants.BORDER_SMALL )
		    .padTop( 0f );
		this.defaults()
		    .space( UiConstants.BORDER_SMALL )
		    .expandX()
		    .fillX();
	}
}
