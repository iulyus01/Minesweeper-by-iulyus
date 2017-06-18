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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

/**
 * Created by iulyus on 11.06.2017.
 */
public class Settings implements Screen {

    private Settings settings;

    private MainClass main;

    private Stage stage;

    private Table table;

    private TextButton.TextButtonStyle difficultyButtonStyle;
    private ArrayList<TextButton> difficultyButton;
    private ButtonGroup<TextButton> buttonGroup;

    private BitmapFont font;

    private FreeTypeFontGenerator generator;

    private Label label;

    private Texture background;


    public Settings(final MainClass game) {
        main = game;
        settings = this;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();

        font = new BitmapFont();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("Orbitron Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        font = generator.generateFont(parameter);
        generator.dispose();

        background = new Texture(Gdx.files.internal("MenuBackground.png"));

        difficultyButtonStyle = new TextButton.TextButtonStyle();
        difficultyButtonStyle.font = font;
        difficultyButtonStyle.fontColor = Color.WHITE;
        difficultyButtonStyle.overFontColor = Color.GRAY;
        difficultyButtonStyle.downFontColor = Color.BLACK;
        difficultyButtonStyle.checkedFontColor = Color.BROWN;
        difficultyButton = new ArrayList<TextButton>();
        difficultyButton.add(new TextButton("Easy", difficultyButtonStyle));
        difficultyButton.add(new TextButton("Medium", difficultyButtonStyle));
        difficultyButton.add(new TextButton("Hard", difficultyButtonStyle));
        difficultyButton.add(new TextButton("Back", difficultyButtonStyle));

        buttonGroup = new ButtonGroup<TextButton>();
        buttonGroup.add(difficultyButton.get(0));
        buttonGroup.add(difficultyButton.get(1));
        buttonGroup.add(difficultyButton.get(2));
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);

        label = new Label("DIFFICULTIES", new Label.LabelStyle(font, Color.WHITE));

        table.setBounds(0, 0, Info.Width, Info.Height);
//        table.row().width(Info.Width);
//        table.add();
//        table.add(label).expandX().spaceLeft(label.getWidth() / 2 + Info.Width / 6);
//        table.add();
//        table.row().padTop(20).width(Info.Width);
//        table.add(difficultyButton.get(0)).width(Info.Width / 3);
//        table.add(difficultyButton.get(1)).width(Info.Width / 3);
//        table.add(difficultyButton.get(2)).width(Info.Width / 3);
        table.row();
        table.add(label).expandX().colspan(3);
        table.row().padTop(20).width(Info.Width);
        table.add(difficultyButton.get(0)).width(Info.Width / 3);
        table.add(difficultyButton.get(1)).width(Info.Width / 3);
        table.add(difficultyButton.get(2)).width(Info.Width / 3);
        table.row().padTop(50);
        table.add(difficultyButton.get(3)).expandX().colspan(3);

        stage.addActor(table);

        difficultyButton.get(0).addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Info.minesNr = 15;
                Info.mineSize = 40;
                Info.difficulty = 0;
                difficultyButton.get(1);
            }
        });
        difficultyButton.get(1).addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Info.minesNr = 20;
                Info.mineSize = 40;
                Info.difficulty = 1;
            }
        });
        difficultyButton.get(2).addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Info.minesNr = 25;
                Info.mineSize = 40;
                Info.difficulty = 2;
            }
        });
        difficultyButton.get(3).addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                settings.dispose();
                game.setScreen(new MainMenu(game));
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

//        table.setDebug(true);

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
