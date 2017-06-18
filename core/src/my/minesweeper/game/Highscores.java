package my.minesweeper.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

/**
 * Created by iulyus on 17.06.2017.
 */
public class Highscores implements Screen {

    private Highscores highscores;

    private MainClass main;

    private Stage stage;

    private Table table;

    private TextButton.TextButtonStyle backButtonStyle;
    private TextButton backButton;
    private ButtonGroup<TextButton> buttonGroup;

    private BitmapFont font;

    private FreeTypeFontGenerator generator;

    private Label label;

    private Texture background;


    public Highscores(final MainClass game) {
        main = game;
        highscores = this;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();

        font = new BitmapFont();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("Orbitron Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        font = generator.generateFont(parameter);
        generator.dispose();

        Label easyText = new Label("Easy", new Label.LabelStyle(font, Color.WHITE));
        Label mediumText = new Label("Medium", new Label.LabelStyle(font, Color.WHITE));
        Label hardText = new Label("Hard", new Label.LabelStyle(font, Color.WHITE));
        easyText.setAlignment(Align.center);
        mediumText.setAlignment(Align.center);
        hardText.setAlignment(Align.center);
        String easyScore = "-";
        String mediumScore = "-";
        String hardScore = "-";
        if (Gdx.app.getPreferences("easyHighscore").getInteger("easyHighscore") != 0)
            easyScore = Integer.toString(Gdx.app.getPreferences("easyHighscore").getInteger("easyHighscore"));
        if (Gdx.app.getPreferences("mediumHighscore").getInteger("mediumHighscore") != 0)
            mediumScore = Integer.toString(Gdx.app.getPreferences("mediumHighscore").getInteger("mediumHighscore"));
        if (Gdx.app.getPreferences("hardHighscore").getInteger("hardHighscore") != 0)
            hardScore = Integer.toString(Gdx.app.getPreferences("hardHighscore").getInteger("hardHighscore"));

        Label easyHighscoreLabel = new Label(easyScore, new Label.LabelStyle(font, Color.WHITE));
        Label mediumHighscoreLabel = new Label(mediumScore, new Label.LabelStyle(font, Color.WHITE));
        Label hardHighscoreLabel = new Label(hardScore, new Label.LabelStyle(font, Color.WHITE));
        easyHighscoreLabel.setAlignment(Align.center);
        mediumHighscoreLabel.setAlignment(Align.center);
        hardHighscoreLabel.setAlignment(Align.center);

        background = new Texture(Gdx.files.internal("MenuBackground.png"));

        backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.font = font;
        backButtonStyle.fontColor = Color.WHITE;
        backButtonStyle.overFontColor = Color.GRAY;
        backButtonStyle.downFontColor = Color.BLACK;
        backButton = new TextButton("Back", backButtonStyle);

        label = new Label("HIGHSCORES", new Label.LabelStyle(font, Color.WHITE));

        table.setBounds(0, 0, Info.Width, Info.Height);
        table.row();
        table.add(label).expandX().colspan(3);
        table.row().padTop(20).width(Info.Width);
        table.add(easyText).width(Info.Width / 3).expandX();
        table.add(mediumText).width(Info.Width / 3).expandX();
        table.add(hardText).width(Info.Width / 3).expandX();
        table.row().padTop(10);
        table.add(easyHighscoreLabel).width(Info.Width / 3);
        table.add(mediumHighscoreLabel).width(Info.Width / 3);
        table.add(hardHighscoreLabel).width(Info.Width / 3);
        table.row().padTop(50);
        table.add(backButton).expandX().colspan(3);

        stage.addActor(table);

        backButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                highscores.dispose();
                game.setScreen(new MainMenu(game));
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.3f, 0.25f, 0.3f, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0);
        stage.getBatch().end();

        stage.act(delta);
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

    }
}
