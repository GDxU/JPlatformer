package com.sh.jplatformer.ui.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.FormButton;
import com.sh.jplatformer.ui.components.FormPane;
import com.sh.jplatformer.ui.components.UiConstants;
import com.sh.jplatformer.ui.stages.GameStage;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.util.Time;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;

/**
 * The {@code RecordsMenu} shows up if the player finishes a world.
 * @author Stefan Hösemann
 */

public class RecordsMenu extends Table
{
	// UI Components
	//==============
	private GameStage gameStage;
	private Skin skin;
	
	private Label lbl_score;
	private Label lbl_time;
	private Label lbl_highScore;
	private Label lbl_bestTime;
	
	// Constructor
	//============
	/**
	 * @param gameStage the {@code GameStage} to interact with.
	 */
	public RecordsMenu( GameStage gameStage )
	{
		// Assignments
		//============
		this.gameStage = gameStage;
		this.skin = Resources.UI.skin;
		
		// Create components
		//==================
		Actor pane = new FormPane( createMenu(), skin, "menu" );
		this.add( pane );
	}
	
	// createMenu
	//===========
	private Actor createMenu()
	{
		// Create labels
		//==============
		lbl_score     = new Label( "", Resources.UI.skin );
		lbl_time      = new Label( "", Resources.UI.skin );
		lbl_highScore = new Label( "", Resources.UI.skin );
		lbl_bestTime  = new Label( "", Resources.UI.skin );
		
		// Button: Continue
		//=================
		FormButton btn_continue = new FormButton( Lang.txt( "menu_records_toMenu" ), skin, "menu" );
		btn_continue.addListener( new ChangeListener()
		{
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				gameStage.getWorldController().setWorldState( WorldController.STATE_BACK_TO_MENU );
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new Table();
		
		tbl_main.top().pad( UiConstants.BORDER_SMALL );
		tbl_main.defaults().space( UiConstants.BORDER_SMALL );
		
		// Window heading setup
		//=====================
		Label lbl_heading = new Label( Lang.txt( "menu_records_heading" ), skin, "heading_menu" )
		{
			@Override
			public float getMinWidth()
			{
				return ( 300f );
			}
		};
		lbl_heading.setAlignment( Align.center );
		
		// Window heading
		//===============
		tbl_main.add( lbl_heading ).colspan( 2 ).top().expandX().fillX().pad( 0f );
		tbl_main.row();

		// Add panes
		//==========
		tbl_main.add( new FormPane( createResultsPane(),    skin, "menu" ) ).fillX().expand().left();
		tbl_main.add( new FormPane( createEvaluationPane(), skin, "menu" ) ).fillX().expand().left();
		tbl_main.row();
		
		// Buttons
		//========
		tbl_main.add( btn_continue ).bottom().right().colspan( 2 ).space( 0f ).pad( 0f );
		
		return ( tbl_main );
	}
	
	// createResultsPane
	//==================
	private Actor createResultsPane()
	{
		// Table setup
		//============
		Table tbl_results = new Table();
		
		tbl_results.left().center().pad( UiConstants.BORDER_TINY );
		tbl_results.defaults().padRight( UiConstants.BORDER_SMALL ).left().expand().fill();
		
		// Heading
		//========
		tbl_results.add( new Label( Lang.txt( "menu_records_results" ), skin, "heading_medium" ) ).colspan( 2 );
		tbl_results.row();
		tbl_results.add( new Label( "", skin ) );
		tbl_results.row();
		
		// Labels
		//=======
		tbl_results.add( new Label( Lang.txt( "menu_records_score" ), skin ) );
		tbl_results.add( lbl_score );
		tbl_results.row();
		tbl_results.add( new Label( Lang.txt( "menu_records_time" ), skin ) );
		tbl_results.add( lbl_time );
		
		return ( tbl_results );
	}
	
	// createEvaluationPane
	//=====================
	private Actor createEvaluationPane()
	{
		// Table setup
		//============
		Table tbl_evaluation = new Table();
		
		tbl_evaluation.left().center().pad( UiConstants.BORDER_TINY );
		tbl_evaluation.defaults().padRight( UiConstants.BORDER_SMALL ).left().expand().fill();
		
		// Heading
		//========
		tbl_evaluation.add( new Label( Lang.txt( "menu_records_records" ), skin, "heading_medium" ) ).colspan( 2 );
		tbl_evaluation.row();
		tbl_evaluation.add( new Label( "", skin ) );
		tbl_evaluation.row();
		
		// Labels
		//=======
		tbl_evaluation.add( new Label( Lang.txt( "menu_records_highScore" ), skin ) );
		tbl_evaluation.add( lbl_highScore );
		tbl_evaluation.row();
		tbl_evaluation.add( new Label( Lang.txt( "menu_records_bestTime" ), skin ) );
		tbl_evaluation.add( lbl_bestTime );
		
		return ( tbl_evaluation );
	}
	
	// updateComponents
	//=================
	private void updateComponents()
	{
		// Get objects / values
		//=====================
		int score = gameStage.getWorldController().getScore();
		long time = gameStage.getWorldController().getElapsedTime();
		Map map   = gameStage.getWorldController().getMap();
		
		// Calculate time if countdown
		//============================
		if ( map.getCountdownTime() != Map.COUNTDOWN_DISABLED )
		{
			time = map.getCountdownTime() - time;
		}
		
		// Update labels
		//==============
		lbl_score    .setText( score + "" );
		lbl_time     .setText( Time.toString( time ) );
		lbl_highScore.setText( map.getHighScore() + "" );
		lbl_bestTime .setText( Time.toString( map.getBestTime() ) );
		
		// Update high score
		//==================
		if ( score > map.getHighScore() )
		{
			lbl_highScore.setText( Lang.txt( "menu_records_new" ) );
		}
		
		// Update best time
		//=================
		if ( time < map.getBestTime() || map.getBestTime() == 0L )
		{
			lbl_bestTime.setText( Lang.txt( "menu_records_new" ) );
		}
	}
	
	// setVisible
	//===========
	@Override
	public void setVisible( boolean visible )
	{
		super.setVisible( visible );
		if ( visible )
		{
			updateComponents();
		}
	}
}