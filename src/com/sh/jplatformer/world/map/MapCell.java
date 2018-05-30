package com.sh.jplatformer.world.map;

import java.io.Serializable;

/**
 * The {@code MapCell} class provides a model for cells in a {@code Map}.
 * @author Stefan Hösemann
 */

public class MapCell implements Serializable
{
	// Fields
	//=======
	private static final long serialVersionUID = 1L;
	public int tileSetId;
	public int tileId;
	public int x;
	public int y;
	
	// Constructor
	//============
	public MapCell()
	{
		this.clear();
	}
	
	// clear
	//======
	public void clear()
	{
		tileSetId = -1;
		tileId = 0;
		x = 0;
		y = 0;
	}
}