package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.UiConstants;

/**
 * The {@code TabPane} provides a tabbed pane structure where each tab relates to an actor which
 * is only displayed if the tab button is toggled.
 * @author Stefan Hösemann
 */

public class TabPane extends Table
{
	// Components
	//===========
	private ButtonGroup<Button> buttonGroup;
	private Stack contentStack;
	private Table tbl_tabs;
	
	// Constructor
	//============
	public TabPane()
	{
		buttonGroup  = new ButtonGroup<Button>();
		contentStack = new Stack();
		tbl_tabs     = new Table();
	}
	
	// addTab
	//=======
	/**
	 * Adds a new tab to the pane.
	 * @param button the tab {@code Button}.
	 * @param content the {@code Actor} to be displayed when this tab is active.
	 */
	public void addTab( Button button, Actor content )
	{
		// Set click action
		//=================
		button.addListener( new ClickListener()
		{
			@Override
			public void clicked( InputEvent e, float x, float y )
			{
				// Update active tab
				//==================
				setActiveTab( button );		
				TabPane.this.fire( new ChangeListener.ChangeEvent() );
				
				// Play sound
				//===========
				Resources.UI.sound_click2.play();
			}
		} );
		button.pad( 0f );
		
		// Add to components
		//==================
		buttonGroup .add( button );
		contentStack.add( content );
		tbl_tabs    .add( button ).prefWidth( 999f )
		                          .height( UiConstants.TOOLBAR_SIZE )
		                          .pad( UiConstants.BORDER_TINY );
		
		// Recreate table
		//===============
		this.clear();
		this.add( tbl_tabs ).padBottom( UiConstants.BORDER_SMALL ).left().row();
		this.add( contentStack ).pad( 0f,
		                              UiConstants.BORDER_TINY,
		                              UiConstants.BORDER_TINY,
		                              UiConstants.BORDER_TINY ).expand().fill().width( 0f );
		
		this.setActiveTab( 0 );
	}
	
	// removeTab
	//==========
	/**
	 * Removes the specified content and its tab from the {@code TabPane}.
	 * @param content the content to remove.
	 */
	public void removeTab( Actor content )
	{
		for ( int i = 0; i < contentStack.getChildren().size; i++ )
		{
			// Check equality
			//===============
			if ( contentStack.getChildren().get( i ).equals( content ) )
			{
				// Remove tab button
				//==================
				Button btn = buttonGroup.getButtons().get( i );
				
				btn.remove();
				buttonGroup.remove( btn );	
				
				// Remove content
				//===============
				 contentStack.getChildren().get( i ).remove();
				 break;
			}
		}
	}
	
	// setActiveTab
	//=============
	public void setActiveTab( int index )
	{	
		this.setActiveTab( buttonGroup.getButtons().get( index ) );
	}
	
	// setActiveTab
	//=============
	/**
	 * @param tab the tab {@code Button} to be activated.
	 */
	private void setActiveTab( Button tab )
	{
		// Check tab
		//==========
		int contentId = 0;
		
		for ( int i = 0; i < buttonGroup.getButtons().size; i++ )
		{
			if ( buttonGroup.getButtons().get( i ) == tab )
			{
				buttonGroup.getButtons().get( i ).setChecked( true );
				contentId = i; 
			}
		}
		
		// Show content
		//=============
		for ( int i = 0; i < contentStack.getChildren().size; i++ )
		{	
			contentStack.getChildren().get( i ).setVisible( false );
			
			if ( i == contentId )
			{
				contentStack.getChildren().get( i ).setVisible( true );
			}
		}
	}
	
	// getVisibleContent
	//==================
	/**
	 * @return the visible {@code Actor}.
	 */
	public Actor getVisibleContent()
	{
		// Check stack
		//============
		for ( Actor actor : contentStack.getChildren() )
		{	
			if ( actor.isVisible() )
			{
				return ( actor );
			}
		}
		return ( null );
	}
	
	// getPrefWidth
	//=============
	@Override 
	public float getPrefWidth()
	{
		return ( tbl_tabs.getWidth() );
	}
}