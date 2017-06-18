package my.minesweeper.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by iulyus on 05.06.2017.
 */
public class MainMenu implements Screen {

    private MainClass game;
    private MainMenu mainMenu;

    private Stage stage;

    private Table table;

    private BitmapFont titleFont;
    private BitmapFont textFont;
    private BitmapFont othersFont;

    private Texture background;

    private TextButton.TextButtonStyle buttonStyle;

    private TextButton playButton;
    private TextButton settingsButton;
    private TextButton highscoresButton;
    private TextButton exitButton;

    private String title = "Minesweeper";
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private GlyphLayout layout;

    private ParticleEffect particleMenuBackground;

    private float scale = 0.0005f;
    private int xBg = 0;
    private int yBg = 0;

    public MainMenu(final MainClass game) {
        this.game = game;
        mainMenu = this;

        stage = new Stage();
        table = new Table();

        Gdx.input.setInputProcessor(stage);

        background = new Texture(Gdx.files.internal("MenuBackground.png"));

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Orbitron Bold.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        generator.generateData(parameter);
        titleFont = generator.generateFont(parameter);
        generator.dispose();
        layout = new GlyphLayout(titleFont, title);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Orbitron Light.ttf"));
        parameter.size = 30;
        generator.generateData(parameter);
        textFont = generator.generateFont(parameter);
        generator.dispose();
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = textFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.GRAY;
        buttonStyle.downFontColor = Color.BLACK;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Orbitron Light.ttf"));
        parameter.size = 15;
        generator.generateData(parameter);
        othersFont = generator.generateFont(parameter);
        generator.dispose();
        playButton = new TextButton("Play", buttonStyle);
        settingsButton = new TextButton("Settings", buttonStyle);
        highscoresButton = new TextButton("Highscores", buttonStyle);
        exitButton = new TextButton("Exit", buttonStyle);

        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.add(playButton).padBottom(20).padTop(Info.Height / 4);
        table.row();
        table.add(settingsButton).padBottom(20);
        table.row();
        table.add(highscoresButton).padBottom(20);
        table.row();
        table.add(exitButton);

        stage.addActor(table);

        playButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                mainMenu.dispose();
                game.setScreen(new Game(game));
            }
        });
        settingsButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                mainMenu.dispose();
                game.setScreen(new Settings(game));
            }
        });
        highscoresButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                mainMenu.dispose();
                game.setScreen(new Highscores(game));
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Gdx.app.exit();
            }
        });


        particleMenuBackground = new ParticleEffect();
        particleMenuBackground.load(Gdx.files.internal("ParticleMenuBackground"), Gdx.files.internal(""));
        particleMenuBackground.getEmitters().first().setPosition(Info.Width / 2, Info.Height / 2);
        particleMenuBackground.start();


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        table.setDebug(true);
        xBg ++;
        yBg += 4;

        particleMenuBackground.getEmitters().first().setPosition(Gdx.input.getX(), Info.Height - Gdx.input.getY());
        particleMenuBackground.update(delta);
        if (particleMenuBackground.isComplete()) {
            particleMenuBackground.reset();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);


        stage.act(delta);

        stage.getBatch().begin();
        stage.getBatch().draw(background, 0 - (float) ((SimplexNoise.noise(xBg * scale, yBg * scale) + 1) / 2 * (background.getWidth() - Info.Width)), 0 - (float) ((SimplexNoise.noise(xBg * scale, yBg * scale) + 1) / 2 * (background.getHeight() - Info.Height)));
        particleMenuBackground.draw(stage.getBatch());
        titleFont.draw(stage.getBatch(), layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 8);
        stage.getBatch().end();

        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        titleFont.dispose();
        textFont.dispose();
        othersFont.dispose();
        background.dispose();
//        generator.dispose(); // disposed
        particleMenuBackground.dispose();
    }

}

