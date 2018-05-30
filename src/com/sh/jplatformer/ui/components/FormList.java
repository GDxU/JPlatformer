package com.sh.jplatformer.ui.components;

import java.util.ArrayList;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sh.jplatformer.resources.Resources;

/**
* The {@code FormList} extends the original libGDX {@code List}. It offers a variety of additional
* methods for item modification. If a list item is selected, a click sound will be played.
* @author Stefan Hösemann
 */

public class FormList<T> extends List<T>
{
	// Constructor
	//============
	/**
	 * Constructs a new {@code FormList} with the "default" list style.
	 * @param skin the {@code Skin} of the list.
	 */
	public FormList( Skin skin )
	{
		this( skin, "default" );
	}
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code FormList} with the "default" list style.
	 * @param skin the {@code Skin} of the list.
	 * @param style the list style to apply.
	 */
	public FormList( Skin skin, String style )
	{
		// Call super
		//===========
		super( skin, style );
		
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
	
	// addItem
	//========
	/**
	 * @param items the new items for this list.
	 */
	public void setItems( ArrayList<T> items )
	{
		// Clear and set items
		//====================
		int selected = this.getSelectedIndex();
		
		this.clearItems();
		
		for ( T item : items )
		{
			this.addItem( item );
		}
		
		// Reset selection
		//================
		this.setSelectedIndex( selected );
	}
	
	// addItem
	//========
	/**
	 * Adds an item to the end of the list and selects it.
	 * @param item the item to add.
	 */
	public void addItem( T item )
	{
		this.getItems().add( item );
		this.setSelectedIndex( this.getItems().size-1 );
	}
	
	// removeItem
	//===========
	/**
	 * Removes the selected item and keeps the selection at its current position.
	 */
	public void removeItem()
	{
		if ( this.getItems().size > 0 )
		{
			this.removeItem( this.getSelectedIndex() );
		}
	}
	
	// removeItem
	//===========
	/**
	 * Removes the specified item and keeps the selection at its current position.
	 * @param index the index of the item to be removed. If the index is invalid, no changes are
	 * made.
	 */
	public void removeItem( int index )
	{
		// Removes selected item
		//======================
		if ( index >= 0 && index < this.getItems().size )
		{
			this.getItems().removeIndex( index );
		}
		
		// Reset selection
		//================
		int size = this.getItems().size;
		
		if ( index >= size )
		{
			this.setSelectedIndex( size - 1 );
		}
		else if ( size != 0 )
		{
			this.setSelectedIndex( index );
		}
	}
	
	// setSelectedIndex
	//=================
	/**
	 * Sets the specified list item selected. If the index is out of range, it will be limited to
	 * the highest or smallest valid value.
	 * @param index the item to set selected.
	 */
	@Override
	public void setSelectedIndex( int index )
	{
		// Limit value
		//============
		if ( index > this.getItems().size - 1 )
		{
			index = this.getItems().size - 1;
		}
		else if ( index < -1 )
		{
			index = -1;
		}
		
		// Call super method
		//==================
		super.setSelectedIndex( index );
	}
}
