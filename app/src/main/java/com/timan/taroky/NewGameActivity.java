package com.timan.taroky;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NewGameActivity extends FragmentActivity implements NewGameAlert.NoticeDialogListener{

	private static final int NUM_PLAYERS = 4;
	private static final int JEW_INDEX = 4;

	private static final int FIRST = 0;
	private static final int SECOND_MANAGED = 1;
	private static final int SECOND_FAILED = 2;

	private String[] players = new String[NUM_PLAYERS];
	private ArrayList<Integer> total_scores = new ArrayList<Integer>();

    //preventing from accidentally killing the app
	@Override
	public void onBackPressed() {
		//do nothing
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		setContentView(R.layout.new_game_activity);
		if (extras != null) {

			String data = extras.getString("loaded_game");
			String[] parts = data.split("#");
			for(int i = 0; i < 4; i++) {
				players[i] = parts[i];
			}
			for(int i = 4; i < parts.length; i++) {
				total_scores.add(Integer.parseInt(parts[i]));
			}
			hideKeyboard();
			scoreView();
		} else {

			for(int i = 0; i < 5; i++) {
				total_scores.add(0);
			}
		}
	}

    /**
     * Start game with loaded data
     * @param view	View	data loaded from a file
     */
    public void loadGameAnyway(View view)
    {
        String filename = "taroky.txt";
        FileInputStream fin;

        try {
            fin = openFileInput(filename);

            int c;
            String data="";
            while((c = fin.read()) != -1){
                data = data + Character.toString((char)c);
            }
            //string temp contains all the data of the file.
            fin.close();

            Intent intent = new Intent(this, NewGameActivity.class);
            intent.putExtra("loaded_game", data);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void restart(View view)
	{
		DialogFragment dialog = new NewGameAlert();
		dialog.show(getFragmentManager(), "NewGameAlert");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		total_scores.clear();
		setContentView(R.layout.new_game_activity);
		for(int i = 0; i < 5; i++)
		{
			total_scores.add(0);
		}
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		//do nothing
	}

	private void setScores(int[] new_score)
	{
		for(int i = 0; i < 5; i++)
		{
			total_scores.add(new_score[i] + total_scores.get(total_scores.size() - 5));
		}
	}

	private void resetScores()
	{
		for(int i = 0; i < 5; i++)
		{
			if(total_scores.size() > 5)total_scores.remove(total_scores.size() - 1);
		}
	}


	private void hideKeyboard()
	{
		InputMethodManager inputManager = (InputMethodManager)
				getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void checkboxDesc()
	{
		CheckBox first_player = (CheckBox)findViewById(R.id.checkbox_first);
		first_player.setText(players[0]);
		CheckBox second_player = (CheckBox)findViewById(R.id.checkbox_second);
		second_player.setText(players[1]);
		CheckBox third_player = (CheckBox)findViewById(R.id.checkbox_third);
		third_player.setText(players[2]);
		CheckBox fourth_player = (CheckBox)findViewById(R.id.checkbox_fourth);
		fourth_player.setText(players[3]);
	}

	private void radiobuttonDesc()
	{
		RadioButton first_player = (RadioButton)findViewById(R.id.checkbox_first);
		first_player.setText(players[0]);
		RadioButton second_player = (RadioButton)findViewById(R.id.checkbox_second);
		second_player.setText(players[1]);
		RadioButton third_player = (RadioButton)findViewById(R.id.checkbox_third);
		third_player.setText(players[2]);
		RadioButton fourth_player = (RadioButton)findViewById(R.id.checkbox_fourth);
		fourth_player.setText(players[3]);
	}

	private int[] firstAndSecond(int second)
	{
		ArrayList<CheckBox> players = new ArrayList<CheckBox>();
		EditText new_points = (EditText)findViewById(R.id.new_points);
		TextView error = (TextView)findViewById(R.id.error);

		int[] new_scores = {0,0,0,0,0};
		boolean[] coop = {false, false, false, false};
		int coop_players = 0;

		players.add((CheckBox) findViewById(R.id.checkbox_first));
		players.add((CheckBox) findViewById(R.id.checkbox_second));
		players.add((CheckBox) findViewById(R.id.checkbox_third));
		players.add((CheckBox) findViewById(R.id.checkbox_fourth));


		for (short i = 0; i < NUM_PLAYERS; i++) {
			if(players.get(i).isChecked()){
				coop[i] = true;
				coop_players++;
			}
		}

		if(new_points.getText().toString().matches("")) {
			error.setText(getResources().getString(R.string.wrong_points));
		} else if(coop_players > 2) {
			error.setText(getResources().getString(R.string.too_many_players));
		} else if(coop_players < 1) {
			error.setText(getResources().getString(R.string.at_least_one_player));
		} else {
			error.setText("");

			//First duty, 2 against 2
			if(coop_players == 2) {
				for(int i = 0; i < NUM_PLAYERS; i++) {
					if(coop[i]){
						new_scores[i] += Integer.parseInt(new_points.getText().toString());
					} else {
						new_scores[i] -= Integer.parseInt(new_points.getText().toString());
					}
				}
			//Fist duty, 1 against 3
			} else {
				for(int i = 0; i < NUM_PLAYERS; i++) {
					if(coop[i]) {
						new_scores[i] += 3 * Integer.parseInt(new_points.getText().toString());
					} else {
						new_scores[i] -= Integer.parseInt(new_points.getText().toString());
					}
				}
			}
			//Second duty, declarante managed to get pagat
			if(second == SECOND_MANAGED) {
				//2 against 2
				if(coop_players == 2) {
					for(int i = 0; i < NUM_PLAYERS; i++) {
						if(coop[i]){
							new_scores[i] += total_scores.get(total_scores.size() - 1) / 2;
						}
					}
				//1 against 3
				} else {
					for(int i = 0; i < NUM_PLAYERS; i++) {
						if(coop[i]) {
							new_scores[i] +=  total_scores.get(total_scores.size() - 1);
							break;
						}
					}
				}

				new_scores[JEW_INDEX] = -total_scores.get(total_scores.size() - 1);
				//Second duty, declarante failed to get pagat
			} else if(second == SECOND_FAILED) {
				//2 against 2
				if(coop_players == 2) {
					for(int i = 0; i < NUM_PLAYERS; i++) {
						if(!coop[i]) {
							new_scores[i] += total_scores.get(total_scores.size() - 1) / 2;
						}
					}
					new_scores[JEW_INDEX] = -total_scores.get(total_scores.size() - 1);
				//1 against 3
				} else {
					int jew = total_scores.get(total_scores.size() - 1) - total_scores.get(total_scores.size() - 1)%3;
					for(int i = 0; i < NUM_PLAYERS; i++) {
						if(!coop[i]) {
							new_scores[i] += jew/3;
						}
					}
					new_scores[JEW_INDEX] =  -jew;
				}
			}
		}

		return new_scores;
	}

	public void startFirst(View view)
	{
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.game);
		gameLayout.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();
		gameLayout.addView(inflater.inflate(R.layout.player_checkbox_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.first_second_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.back, null));
		gameLayout.addView(inflater.inflate(R.layout.error_layout, null));
		Button go_back = (Button)findViewById(R.id.back);
		checkboxDesc();
		Button count_points = (Button)findViewById(R.id.count_points);
		count_points.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				int[] new_scores;
				new_scores = firstAndSecond(FIRST);
				TextView error = (TextView)findViewById(R.id.error);
				if(error.getText().toString().length() == 0)
				{
					hideKeyboard();
					setScores(new_scores);
					scoreView();
				}
			}
		});

		go_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				hideKeyboard();
				continueGame();
			}
		});
	}

	public void startSecond(View view)
	{
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.game);
		gameLayout.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();
		gameLayout.addView(inflater.inflate(R.layout.player_checkbox_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.pagat, null));
		gameLayout.addView(inflater.inflate(R.layout.first_second_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.back, null));
		gameLayout.addView(inflater.inflate(R.layout.error_layout, null));
		Button go_back = (Button)findViewById(R.id.back);
		checkboxDesc();
		Button count_points = (Button)findViewById(R.id.count_points);
		count_points.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				int[] new_scores;
				CheckBox pagat = (CheckBox)findViewById(R.id.pagat);
				if(pagat.isChecked()) {
					new_scores = firstAndSecond(SECOND_MANAGED);
				} else {
					new_scores = firstAndSecond(SECOND_FAILED);
				}
				TextView error = (TextView)findViewById(R.id.error);
				if(error.getText().toString().length() == 0)
				{
					hideKeyboard();
					setScores(new_scores);
					scoreView();
				}
			}
		});

		go_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				hideKeyboard();
				continueGame();
			}
		});
	}

	public void startThird(View view)
	{
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.game);
		gameLayout.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();
		gameLayout.addView(inflater.inflate(R.layout.player_radiobutton_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.third_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.back, null));
		gameLayout.addView(inflater.inflate(R.layout.error_layout, null));
		Button go_back = (Button)findViewById(R.id.back);
		radiobuttonDesc();
		Button count_points = (Button)findViewById(R.id.count_points);
		count_points.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				int[] new_scores = {0,0,0,0,0};

				RadioButton first_player = (RadioButton)findViewById(R.id.checkbox_first);
				RadioButton second_player = (RadioButton)findViewById(R.id.checkbox_second);
				RadioButton third_player = (RadioButton)findViewById(R.id.checkbox_third);
				RadioButton fourth_player = (RadioButton)findViewById(R.id.checkbox_fourth);
				EditText new_points = (EditText)findViewById(R.id.new_points);
				TextView error = (TextView)findViewById(R.id.error);

				int checked = -1;

				if(first_player.isChecked()) {
					checked = 0;
				} else if(second_player.isChecked()) {
					checked = 1;
				} else if(third_player.isChecked()) {
					checked = 2;
				} else if (fourth_player.isChecked()) {
					checked = 3;
				}

				if(new_points.getText().toString().matches("")) {
					error.setText(getResources().getString(R.string.wrong_points));
				} else if(checked == -1) {
					error.setText(getResources().getString(R.string.exactly_one_player));
				} else {
					error.setText("");

					for(int i = 0; i < NUM_PLAYERS; i++) {
						if(i == checked) {
							new_scores[i] = 3 * Integer.parseInt(new_points.getText().toString());
						} else {
							new_scores[i] = -1 * Integer.parseInt(new_points.getText().toString());
						}
					}
				}

				if(error.getText().toString().length() == 0)
				{
					hideKeyboard();
					setScores(new_scores);
					scoreView();
				}
			}
		});


		go_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				hideKeyboard();
				continueGame();
			}
		});
	}

	public void startWarszaw(View view)
	{
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.game);
		gameLayout.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();
		gameLayout.addView(inflater.inflate(R.layout.warszaw_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.back, null));
		gameLayout.addView(inflater.inflate(R.layout.error_layout, null));
		Button go_back = (Button)findViewById(R.id.back);
		final EditText first_player = (EditText)findViewById(R.id.p1);
		first_player.setHint(players[0]);
		EditText second_player = (EditText)findViewById(R.id.p2);
		second_player.setHint(players[1]);
		EditText third_player = (EditText)findViewById(R.id.p3);
		third_player.setHint(players[2]);
		EditText fourth_player = (EditText)findViewById(R.id.p4);
		fourth_player.setHint(players[3]);
		Button count_points = (Button)findViewById(R.id.count_points);
		count_points.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				int[] new_scores = {0,0,0,0,0};
				EditText first_player = (EditText)findViewById(R.id.p1);
				EditText second_player = (EditText)findViewById(R.id.p2);
				EditText third_player = (EditText)findViewById(R.id.p3);
				EditText fourth_player = (EditText)findViewById(R.id.p4);
				TextView error = (TextView)findViewById(R.id.error);
				error.setText("");

				if(first_player.getText().toString().matches("")) new_scores[0] = 0;
				else new_scores[0] -= Integer.parseInt(first_player.getText().toString());
				if(second_player.getText().toString().matches("")) new_scores[1] = 0;
				else new_scores[1] -= Integer.parseInt(second_player.getText().toString());
				if(third_player.getText().toString().matches("")) new_scores[2] = 0;
				else new_scores[2] -= Integer.parseInt(third_player.getText().toString());
				if(fourth_player.getText().toString().matches("")) new_scores[3] = 0;
				else new_scores[3] -= Integer.parseInt(fourth_player.getText().toString());

				for(int i = 0; i < NUM_PLAYERS; i++)
				{
					new_scores[JEW_INDEX] -= new_scores[i];
				}

				if(error.getText().toString().length() == 0)
				{
					hideKeyboard();
					setScores(new_scores);
					scoreView();
				}
			}
		});


		go_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				hideKeyboard();
				continueGame();
			}
		});
	}

	public void feedJew(View view)
	{
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.game);
		gameLayout.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();
		gameLayout.addView(inflater.inflate(R.layout.player_radiobutton_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.fill_jew_layout, null));
		gameLayout.addView(inflater.inflate(R.layout.back, null));
		gameLayout.addView(inflater.inflate(R.layout.error_layout, null));
		TextView desc = (TextView)findViewById(R.id.first_desc);
		desc.setText(getResources().getString(R.string.faulting_player));
		radiobuttonDesc();
		Button go_back = (Button)findViewById(R.id.back);
		Button count_points = (Button)findViewById(R.id.count_points);
		count_points.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				RadioButton first_player = (RadioButton)findViewById(R.id.checkbox_first);
				RadioButton second_player = (RadioButton)findViewById(R.id.checkbox_second);
				RadioButton third_player = (RadioButton)findViewById(R.id.checkbox_third);
				RadioButton fourth_player = (RadioButton)findViewById(R.id.checkbox_fourth);
				EditText new_points = (EditText)findViewById(R.id.new_points);
				TextView error = (TextView)findViewById(R.id.error);

				int checked = -1;

				if(first_player.isChecked()) checked = 0;
				if(second_player.isChecked()) checked = 1;
				if(third_player.isChecked()) checked = 2;
				if(fourth_player.isChecked()) checked = 3;

				if(new_points.getText().toString().matches("")) {
					error.setText(getResources().getString(R.string.wrong_points));
				} else if(checked == -1) {
					error.setText(getResources().getString(R.string.exactly_one_player));
				} else {
					hideKeyboard();

					int[] new_scores = {0,0,0,0,0};
					error.setText("");
					new_scores[checked] -= Integer.parseInt(new_points.getText().toString());
					new_scores[JEW_INDEX] += Integer.parseInt(new_points.getText().toString());
					setScores(new_scores);
					scoreView();
				}
			}
		});


		go_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				hideKeyboard();
				continueGame();
			}
		});
	}

	private void addRow(int i)
	{

		final TableLayout table = (TableLayout) findViewById(R.id.score_layout);
		final TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.score_row_layout, null);

		TextView tv;
		// Fill out our cells
		tv = (TextView) tr.findViewById(R.id.player1_score);
		tv.setText(total_scores.get(i).toString());

		tv = (TextView) tr.findViewById(R.id.player2_score);
		tv.setText(total_scores.get(i+1).toString());

		tv = (TextView) tr.findViewById(R.id.player3_score);
		tv.setText(total_scores.get(i+2).toString());

		tv = (TextView) tr.findViewById(R.id.player4_score);
		tv.setText(total_scores.get(i+3).toString());

		tv = (TextView) tr.findViewById(R.id.jew_score);
		tv.setText(total_scores.get(i+4).toString());

		table.addView(tr);

	}

	private void scoreView()
	{
		save();
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.game);
		gameLayout.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();
		gameLayout.addView(inflater.inflate(R.layout.continue_layout, null));
		Button continue_game = (Button)findViewById(R.id.continue_game);

		Button go_back = (Button)findViewById(R.id.back);
		gameLayout.addView(inflater.inflate(R.layout.score_layout, null));

        for (int i = 0; i < total_scores.size(); i = i + 5) {
			addRow(i);
		}
        TextView shufflingPlayer = (TextView) findViewById(R.id.shufflingPlayer);
        shufflingPlayer.setText(players[((total_scores.size()/5)%4+2)%4] + " " + getResources().getString(R.string.shuffling_player));


		TextView header = (TextView) findViewById(R.id.player1);
		header.setText(players[0]);
		header = (TextView) findViewById(R.id.player2);
		header.setText(players[1]);
		header = (TextView) findViewById(R.id.player3);
		header.setText(players[2]);
		header = (TextView) findViewById(R.id.player4);
		header.setText(players[3]);

		final ScrollView scrollview = ((ScrollView) findViewById(R.id.scroll_score));
		scrollview.post(new Runnable() {
			@Override
			public void run() {
				scrollview.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

		continue_game.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				continueGame();
			}
		});

		go_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				resetScores();
				scoreView();
			}
		});

	}

	public void startGame(View view)
	{
		EditText editText = (EditText) findViewById(R.id.first_player);
		players[0] = editText.getText().toString();
		editText = (EditText) findViewById(R.id.second_player);
		players[1] = editText.getText().toString();
		editText = (EditText) findViewById(R.id.third_player);
		players[2] = editText.getText().toString();
		editText = (EditText) findViewById(R.id.fourth_player);
		players[3] = editText.getText().toString();

		save();
		hideKeyboard();
		continueGame();
	}

	public void continueGame()
	{
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.game);
		gameLayout.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();
		gameLayout.addView(inflater.inflate(R.layout.new_hand_layout, null));
	}

	public void save()
	{
		String filename = "taroky.txt";
		String string = "";

		for(int i = 0; i < NUM_PLAYERS; i++)
		{
			string += players[i] + "#";
		}

		for(int i = 0; i < total_scores.size(); i++)
		{
			string += total_scores.get(i) + "#";
		}


		FileOutputStream outputStream;

		try {
			outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream.write(string.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
