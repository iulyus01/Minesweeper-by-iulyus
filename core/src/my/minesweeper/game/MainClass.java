package my.minesweeper.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainClass extends Game {

	private Music backgroundMusic;

	@Override
	public void create () {
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Birth of a New Day Full Album HD2.mp3"));
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
		setScreen(new MainMenu(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
	}
}
