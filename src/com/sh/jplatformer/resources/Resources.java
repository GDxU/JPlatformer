package com.sh.jplatformer.resources;

/**
 * The {@code Resources} class grants access to external resources.
 * @author Stefan Hösemann
 */

public class Resources
{
	// Components
	//===========
	public static final World WORLD = new World();
	public static final Ui UI = new Ui();
	
	// Constructor
	//============
	private Resources()
	{
	}
	
	// dispose
	//========
	public static void dispose()
	{
		UI.dispose();
		WORLD.dispose();
	}
}