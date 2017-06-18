package my.minesweeper.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by iulyus on 05.06.2017.
 */
public class Game implements Screen {

    private Game mainGame;
    private MainClass game;

    private Stage stage;

    private boolean firstClick = false;
    private boolean checking = false;
    private boolean checks[] = new boolean[10];
    private boolean clickAgain = false;
    private boolean discovered = false;
    private boolean discoveredCheck = false;

    private FreeTypeFontGenerator generator;
    private BitmapFont textFont;
    private Label timeLabel;
    private Label flagsLabel;

    private float time;
    private float markingDelay;
    private float maxMarkingDelay = 0.1f;

    private Skin skin;
    private Button resetButton;
    private Button backButton;
    private TextureAtlas resetButtonAtlas;
    private TextureAtlas backButtonAtlas;
    private TextureAtlas squareForms;

    private boolean dragged = false;
    private boolean dragging = false;

    private int W = Info.Width;
    private int H = Info.Height;
    private int GW = Info.gridWidth;
    private int GH = Info.gridHeight;
    private int grid[][];
    private int logicalGrid[][];
    private int flagNr = Info.minesNr;
    private int endGame = 0;
    private int mouseI = 0;
    private int mouseJ = 0;
    private int oldI = 1;
    private int oldJ = 1;

    private ParticleEffect particleBackground;

    private ParticleEffect particlesWin;
    private ParticleEffect particlesWin2;

    private Table table;

    private Preferences easyHighscore;
    private Preferences mediumHighscore;
    private Preferences hardHighscore;

    Game(final MainClass game) {
        this.game = game;
        mainGame = this;
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        for (int i = 0; i < 10; i ++) checks[i] = false;
        grid = new int[GH][GW];
        logicalGrid = new int[GH][GW];
        for (int i = 0; i < GH; i ++) {
            for (int j = 0; j < GW; j ++) {
                grid[i][j] = - 1;
                logicalGrid[i][j] = 0;
            }
        }

        maxMarkingDelay = 0.2f;

        squareForms = new TextureAtlas(Gdx.files.internal("SquareForm.atlas"));

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Orbitron Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 22;
        generator.generateData(parameter);
        textFont = generator.generateFont(parameter);
        textFont.setColor(Color.WHITE);
        skin = new Skin();

        Label.LabelStyle labelStyle = new Label.LabelStyle(textFont, Color.WHITE);
        timeLabel = new Label(Integer.toString((int) time), labelStyle);
        Label.LabelStyle minesLabelStyle = new Label.LabelStyle(textFont, Color.WHITE);
        flagsLabel = new Label(" " + Integer.toString(flagNr), minesLabelStyle);

        resetButtonAtlas = new TextureAtlas(Gdx.files.internal("ResetButton.atlas"));
        skin = new Skin(resetButtonAtlas);
        Button.ButtonStyle resetButtonStyle = new Button.ButtonStyle();
        resetButtonStyle.up = skin.getDrawable("ResetButton");
        resetButtonStyle.down = skin.getDrawable("ResetButtonPressed");
        resetButton = new Button(resetButtonStyle);
        resetButton.setSize(30, 30);
        resetButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if (!dragged) {
                    mainGame.dispose();
                    game.setScreen(new Game(game));
                }
                dragging = false;
            }
        });
        Button.ButtonStyle backButtonStyle = new Button.ButtonStyle();
        backButtonAtlas = new TextureAtlas(Gdx.files.internal("BackButton.atlas"));
        skin = new Skin(backButtonAtlas);
        backButtonStyle.up = skin.getDrawable("BackButton");
        backButtonStyle.down = skin.getDrawable("BackButtonPressed");
        backButton = new Button(backButtonStyle);
        backButton.setSize(30, 30);
        backButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                mainGame.dispose();
                game.setScreen(new MainMenu(game));
            }
        });

        particleBackground = new ParticleEffect();
        particleBackground.load(Gdx.files.internal("ParticleBackground2"), Gdx.files.internal(""));
        particleBackground.getEmitters().first().setPosition(W / 2, H / 2);

        particlesWin = new ParticleEffect();
        particlesWin2 = new ParticleEffect();
        particlesWin.load(Gdx.files.internal("ParticlesWinning"), Gdx.files.internal(""));
        particlesWin2.load(Gdx.files.internal("ParticlesWinning"), Gdx.files.internal(""));
        particlesWin.setPosition(W / 2 + (float) (Math.random() * 100 - 50), H / 2 + (float) (Math.random() * 100 - 50));
        particlesWin2.setPosition(W / 2 + (float) (Math.random() * 100 - 50), H / 2 + (float) (Math.random() * 100 - 50));

        table = new Table();
        table.setBounds(0, H, W, Info.hudHeight);
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("HudBackground.png")))));
        table.add(backButton).size(W / 3 / 2 + 2, Info.hudHeight).expandX().left().fill();
        table.add(flagsLabel).expandX().width(W / 3 / 2);
        table.add().expandX().align(Align.center).maxWidth(W / 3).minWidth(W / 3);
        table.add(timeLabel).expandX().width(W / 3 / 2);
        table.add(resetButton).size(W / 3 / 2 + 2, Info.hudHeight).expandX().right().fill().padRight(- 1);

        stage.addActor(table);


        easyHighscore = Gdx.app.getPreferences("easyHighscore");
        mediumHighscore = Gdx.app.getPreferences("mediumHighscore");
        hardHighscore = Gdx.app.getPreferences("hardHighscore");


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        timeLabel.setText("  " + Integer.toString((int) time));

        if (firstClick) {
            time += delta;
        }
        stage.act();
        particleBackground.update(delta);
        particlesWin.update(delta);
        particlesWin2.update(delta);
        if (particleBackground.isComplete()) {
            particleBackground.reset();
        }

        // <====================================================================================================>
        // <============================================= ON CLICK =============================================>
        // <====================================================================================================>

        mouseJ = (int) (Gdx.input.getX() / (W / (float) GW));
        mouseI = (int) ((H - Gdx.input.getY() + Info.hudHeight) / (H / (float) GH));
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&
                Gdx.input.getY() > Info.hudHeight &&
                endGame == 0 &&
                Gdx.input.getY() < H + Info.hudHeight &&
                Gdx.input.getX() > 0 && Gdx.input.getX() < W
        ) {
            if (oldI != mouseI || oldJ != mouseJ) {
                ReturnFromChecking(oldI, oldJ);
                discoveredCheck = false;
            }
            if (!firstClick) {
                grid[mouseI][mouseJ] = 0;
                if (mouseI >= 1) grid[mouseI - 1][mouseJ] = 0;
                if (mouseI <= GH - 2) grid[mouseI + 1][mouseJ] = 0;
                if (mouseJ >= 1) grid[mouseI][mouseJ - 1] = 0;
                if (mouseJ <= GW - 2) grid[mouseI][mouseJ + 1] = 0;
                InitSquares();
                Set();
                Clicked(mouseI, mouseJ, 1);

                Clicked(mouseI - 1, mouseJ, 1);
                Clicked(mouseI + 1, mouseJ, 1);
                Clicked(mouseI, mouseJ - 1, 1);
                Clicked(mouseI, mouseJ + 1, 1);
                firstClick = true;
            } else {
                Clicked(mouseI, mouseJ, 1);
            }
            oldI = mouseI;
            oldJ = mouseJ;
        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) &&
                Gdx.input.getY() > Info.hudHeight && endGame == 0 &&
                Gdx.input.getY() < H + Info.hudHeight &&
                Gdx.input.getX() > 0 &&
                Gdx.input.getX() < W
        ) {
            if (markingDelay < 0) {
                int i, j;
                j = (int) (Gdx.input.getX() / (W / (float) GW));
                i = (int) ((H - Gdx.input.getY() + Info.hudHeight) / (H / (float) GH));
                Clicked(i, j, 2);
                markingDelay = maxMarkingDelay;
            }
            oldI = mouseI;
            oldJ = mouseJ;

        } else {
            checking = false;
            ReturnFromChecking(mouseI, mouseJ);
            for (int i = 0; i < 10; i ++) {
                checks[i] = false;
            }
            discoveredCheck = false;
        }
        // <====================================================================================================>
        // <====================================================================================================>

        if (markingDelay >= 0) markingDelay -= delta;

        if (flagNr == 0 && endGame == 0) {
            int nr = 0;
            for (int i = 0; i < GH; i ++) {
                for (int j = 0; j < GW; j ++) {
                    if (grid[i][j] == - 2 && logicalGrid[i][j] == - 1) {
                        nr ++;
                    }
                }
            }
            if (nr == Info.minesNr) {
                firstClick = false;
                Won();
            }
        }
        if (endGame == 0) {
            int nr = 0;
            for (int i = 0; i < GH; i++) {
                for (int j = 0; j < GW; j++) {
                    if (grid[i][j] == - 1 || grid[i][j] == - 2) nr++;
                }
            }
            if (nr == Info.minesNr) {
                Won();
            }
        }


        Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        stage.getBatch().begin();
        particleBackground.draw(stage.getBatch());
        particlesWin.draw(stage.getBatch());
        particlesWin2.draw(stage.getBatch());
        DrawGrid();
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
        generator.dispose();
        textFont.dispose();
        skin.dispose();
        resetButtonAtlas.dispose();
        particleBackground.dispose();
        particlesWin.dispose();
        particlesWin2.dispose();
    }

    private void InitSquares() {

        // 1 ... 8  - -> 1Bomb ... 8Bombs
        // 0  - - - -> Empty
        // - 1  - - -> Undiscovered
        // - 2  - - -> Marked
        // - 3  - - -> Bomb
        // - 4  - - -> WrongMarked
        // - 5  - - -> ClickedBomb

        int minesNr = Info.minesNr;
        while(minesNr > 0) {
            int i = (int) (Math.random() * GH);
            int j = (int) (Math.random() * GW);
            if (grid[i][j] == - 1 && logicalGrid[i][j] == 0) {
                logicalGrid[i][j] = - 1;
                minesNr --;
            }
        }
    }

    private void DrawGrid() {
        for (int i = 0; i < GH; i ++) {
            for (int j = 0; j < GW; j ++) {
                if (grid[i][j] >= 1) {
                    stage.getBatch().draw(
                            squareForms.findRegion(Integer.toString(grid[i][j]) + "Bomb" + ((grid[i][j] != 1) ? "s" : "")),
                            j * W / GW,
                            i * H / GH,
                            0,
                            0,
                            400,
                            400,
                            Info.mineSize / 400f,
                            Info.mineSize / 400f,
                            0
                    );
                } else {
                    switch (grid[i][j]) {
                        case 0:
                            stage.getBatch().draw(
                                    squareForms.findRegion("Empty"),
                                    j * W / GW,
                                    i * H / GH,
                                    0,
                                    0,
                                    400,
                                    400,
                                    Info.mineSize / 400f,
                                    Info.mineSize / 400f,
                                    0
                            );
                            break;
                        case - 1:
                            stage.getBatch().draw(
                                    squareForms.findRegion("Undiscovered"),
                                    j * W / GW,
                                    i * H / GH,
                                    0,
                                    0,
                                    400,
                                    400,
                                    Info.mineSize / 400f,
                                    Info.mineSize / 400f,
                                    0
                            );
                            break;
                        case - 2:
                            stage.getBatch().draw(
                                    squareForms.findRegion("Marked"),
                                    j * W / GW,
                                    i * H / GH,
                                    0,
                                    0,
                                    400,
                                    400,
                                    Info.mineSize / 400f,
                                    Info.mineSize / 400f,
                                    0
                            );
                            break;
                        case - 3:
                            stage.getBatch().draw(
                                    squareForms.findRegion("Bomb"),
                                    j * W / GW,
                                    i * H / GH,
                                    0,
                                    0,
                                    400,
                                    400,
                                    Info.mineSize / 400f,
                                    Info.mineSize / 400f,
                                    0
                            );
                            break;
                        case - 4:
                            stage.getBatch().draw(
                                    squareForms.findRegion("WrongMarked"),
                                    j * W / GW,
                                    i * H / GH,
                                    0,
                                    0,
                                    400,
                                    400,
                                    Info.mineSize / 400f,
                                    Info.mineSize / 400f,
                                    0
                            );
                            break;
                        case - 5:
                            stage.getBatch().draw(
                                    squareForms.findRegion("ClickedBomb"),
                                    j * W / GW,
                                    i * H / GH,
                                    0,
                                    0,
                                    400,
                                    400,
                                    Info.mineSize / 400f,
                                    Info.mineSize / 400f,
                                    0
                            );
                            break;
                        case - 6:
                            stage.getBatch().draw(
                                    squareForms.findRegion("Unknown"),
                                    j * W / GW,
                                    i * H / GH,
                                    0,
                                    0,
                                    400,
                                    400,
                                    Info.mineSize / 400f,
                                    Info.mineSize / 400f,
                                    0
                            );
                            break;
                    }
                }
            }
        }
    }

    private void Set() {
        int nearMines = 0;
        for (int i = 1; i < GH - 1; i ++) {
            for (int j = 1; j < GW - 1; j ++) {
                if (logicalGrid[i][j] != - 1) {
                    if (logicalGrid[i - 1][j - 1] == - 1) nearMines ++;
                    if (logicalGrid[i - 1][j] == - 1) nearMines ++;
                    if (logicalGrid[i - 1][j + 1] == - 1) nearMines ++;
                    if (logicalGrid[i][j - 1] == - 1) nearMines ++;
                    if (logicalGrid[i][j + 1] == - 1) nearMines ++;
                    if (logicalGrid[i + 1][j - 1] == - 1) nearMines ++;
                    if (logicalGrid[i + 1][j] == - 1) nearMines ++;
                    if (logicalGrid[i + 1][j + 1] == - 1) nearMines ++;
                    logicalGrid[i][j] = nearMines;
                }
                nearMines = 0;
            }
        }
        for (int i = 1; i < GH - 1; i ++) {
            if (logicalGrid[i][0] != - 1) {
                if (logicalGrid[i - 1][0] == - 1) nearMines ++;
                if (logicalGrid[i - 1][1] == - 1) nearMines ++;
                if (logicalGrid[i][1] == - 1) nearMines ++;
                if (logicalGrid[i + 1][1] == - 1) nearMines ++;
                if (logicalGrid[i + 1][0] == - 1) nearMines ++;
                logicalGrid[i][0] = nearMines;
            }
            nearMines = 0;
            if (logicalGrid[i][GW - 1] != - 1) {
                if (logicalGrid[i - 1][GW - 1] == - 1) nearMines ++;
                if (logicalGrid[i - 1][GW - 2] == - 1) nearMines ++;
                if (logicalGrid[i][GW - 2] == - 1) nearMines ++;
                if (logicalGrid[i + 1][GW - 2] == - 1) nearMines ++;
                if (logicalGrid[i + 1][GW - 1] == - 1) nearMines ++;
                logicalGrid[i][GW - 1] = nearMines;
            }
            nearMines = 0;
        }
        for (int i = 1; i < GW - 1; i ++) {
            if (logicalGrid[0][i] != - 1) {
                if (logicalGrid[0][i - 1] == - 1) nearMines ++;
                if (logicalGrid[1][i - 1] == - 1) nearMines ++;
                if (logicalGrid[1][i] == - 1) nearMines ++;
                if (logicalGrid[1][i + 1] == - 1) nearMines ++;
                if (logicalGrid[0][i + 1] == - 1) nearMines ++;
                logicalGrid[0][i] = nearMines;
            }
            nearMines = 0;
            if (logicalGrid[GH - 1][i] != - 1) {
                if (logicalGrid[GH - 1][i - 1] == - 1) nearMines ++;
                if (logicalGrid[GH - 2][i - 1] == - 1) nearMines ++;
                if (logicalGrid[GH - 2][i] == - 1) nearMines ++;
                if (logicalGrid[GH - 2][i + 1] == - 1) nearMines ++;
                if (logicalGrid[GH - 1][i + 1] == - 1) nearMines ++;
                logicalGrid[GH - 1][i] = nearMines;
            }
            nearMines = 0;
        }
        if (logicalGrid[0][0] != - 1) {
            if (logicalGrid[0][1] == - 1) nearMines ++;
            if (logicalGrid[1][0] == - 1) nearMines ++;
            if (logicalGrid[1][1] == - 1) nearMines ++;
            logicalGrid[0][0] = nearMines;
        }
        nearMines = 0;
        if (logicalGrid[GH - 1][0] != - 1) {
            if (logicalGrid[GH - 1][1] == - 1) nearMines ++;
            if (logicalGrid[GH - 2][0] == - 1) nearMines ++;
            if (logicalGrid[GH - 2][1] == - 1) nearMines ++;
            logicalGrid[GH - 1][0] = nearMines;
        }
        nearMines = 0;
        if (logicalGrid[0][GW - 1] != - 1) {
            if (logicalGrid[0][GW - 2] == - 1) nearMines ++;
            if (logicalGrid[1][GW - 1] == - 1) nearMines ++;
            if (logicalGrid[1][GW - 2] == - 1) nearMines ++;
            logicalGrid[0][GW - 1] = nearMines;
        }
        nearMines = 0;
        if (logicalGrid[GH - 1][GW - 1] != - 1) {
            if (logicalGrid[GH - 1][GW - 2] == - 1) nearMines ++;
            if (logicalGrid[GH - 2][GW - 1] == - 1) nearMines ++;
            if (logicalGrid[GH - 2][GW - 2] == - 1) nearMines ++;
            logicalGrid[GH - 1][GW - 1] = nearMines;
        }
    }

    private void Clicked(int i, int j, int button) {
        if (grid[i][j] >= 1 && !discoveredCheck) {
            discovered = true;
            discoveredCheck = true;
        } else if (!discoveredCheck) {
            discovered = false;
            discoveredCheck = true;
        }
        if (button == 1 && grid[i][j] !=  - 2) {
            if (logicalGrid[i][j] >= 1) {
                grid[i][j] = logicalGrid[i][j];
            } else if (logicalGrid[i][j] == -1) {
                Lost();
                grid[i][j] = - 5;
            } else if (logicalGrid[i][j] == 0) {
                SearchAndDiscover(i, j);
            }
            if (grid[i][j] >= 1) {
                CloseDiscover(i, j, discovered);
            }
        } else if (button == 2) {
            if (grid[i][j] == - 1 && flagNr > 0) {
                grid[i][j] = - 2;
                flagNr --;
            }
            else if (grid[i][j] == - 2) {
                grid[i][j] = - 1;
                flagNr ++;
            }
            flagsLabel.setText(" " + Integer.toString(flagNr));
        }
    }

    private void CloseClicked(int i, int j) {
        if (grid[i][j] != - 2) {
            if (logicalGrid[i][j] >= 0 && grid[i][j] == - 1) {
                grid[i][j] = logicalGrid[i][j];
                SearchAndDiscover(i, j);
            } else if (logicalGrid[i][j] == - 1) {
                grid[i][j] = - 5;
                Lost();
            }
        }
    }

    private void SearchAndDiscover(int i, int j) {
        if (logicalGrid[i][j] >= 0) {
            grid[i][j] = logicalGrid[i][j];
        }
        if (j - 1 >= 0)         if (logicalGrid[i][j - 1] >= 0 && grid[i][j] == 0 && grid[i][j - 1] == - 1) SearchAndDiscover(i, j - 1);
        if (j + 1 <= GW - 1)    if (logicalGrid[i][j + 1] >= 0 && grid[i][j] == 0 && grid[i][j + 1] == - 1) SearchAndDiscover(i, j + 1);
        if (i - 1 >= 0)         if (logicalGrid[i - 1][j] >= 0 && grid[i][j] == 0 && grid[i - 1][j] == - 1) SearchAndDiscover(i - 1, j);
        if (i + 1 <= GH - 1)    if (logicalGrid[i + 1][j] >= 0 && grid[i][j] == 0 && grid[i + 1][j] == - 1) SearchAndDiscover(i + 1, j);

        if (i - 1 >= 0 && j - 1 >= 0)           if (logicalGrid[i - 1][j - 1] >= 0 && grid[i][j] == 0 && grid[i - 1][j - 1] == - 1) SearchAndDiscover(i - 1, j - 1);
        if (i - 1 >= 0 && j + 1 <= GW - 1)      if (logicalGrid[i - 1][j + 1] >= 0 && grid[i][j] == 0 && grid[i - 1][j + 1] == - 1) SearchAndDiscover(i - 1, j + 1);
        if (i + 1 <= GH - 1 && j - 1 >= 0)      if (logicalGrid[i + 1][j - 1] >= 0 && grid[i][j] == 0 && grid[i + 1][j - 1] == - 1) SearchAndDiscover(i + 1, j - 1);
        if (i + 1 <= GH - 1 && j + 1 <= GW - 1) if (logicalGrid[i + 1][j + 1] >= 0 && grid[i][j] == 0 && grid[i + 1][j + 1] == - 1) SearchAndDiscover(i + 1, j + 1);
    }

    private void CloseDiscover(int i, int j, boolean discovered) {
        int nr = 0; //number of marks
        if (i >= 1) {
            if (j >= 1) if (grid[i - 1][j - 1] == - 2) nr ++;
            if (j <= GW - 2) if (grid[i - 1][j + 1] == - 2) nr++;
            if (grid[i - 1][j] == - 2) nr ++;
        }
        if (i <= GH - 2) {
            if (j >= 1) if (grid[i + 1][j - 1] == - 2) nr ++;
            if (j <= GW - 2) if (grid[i + 1][j + 1] == - 2) nr ++;
            if (grid[i + 1][j] == - 2) nr ++;
        }
        if (j >= 1) if (grid[i][j - 1] == - 2) nr ++;
        if (j <= GW - 2) if (grid[i][j + 1] == - 2) nr ++;


        if (nr == grid[i][j]) { // discover close squares
            if (i >= 1) {
                if (j >= 1) CloseClicked(i - 1, j - 1);
                if (j <= GW - 2) CloseClicked(i - 1, j + 1);
                CloseClicked(i - 1, j);
            }
            if (i <= GH - 2) {
                if (j >= 1) CloseClicked(i + 1, j - 1);
                if (j <= GW - 2) CloseClicked(i + 1, j + 1);
                CloseClicked(i + 1, j);
            }
            if (j >= 1) CloseClicked(i, j - 1);
            if (j <= GW - 2) CloseClicked(i, j + 1);
        } else if (discovered) { // check which squares are close
            checking = true;
            if (i >= 1) {
                if (j >= 1 && grid[i - 1][j - 1] == - 1) {
                    grid[i - 1][j - 1] = - 6;
                    checks[1] = true;
                }
                if (j <= GW - 2 && grid[i - 1][j + 1] == - 1) {
                    grid[i - 1][j + 1] = - 6;
                    checks[3] = true;
                }
                if (grid[i - 1][j] == - 1) {
                    grid[i - 1][j] = - 6;
                    checks[2] = true;
                }
            }
            if (i <= GH - 2) {
                if (j >= 1 && grid[i + 1][j - 1] == - 1) {
                    grid[i + 1][j - 1] = - 6;
                    checks[7] = true;
                }
                if (j <= GW - 2 && grid[i + 1][j + 1] == - 1) {
                    grid[i + 1][j + 1] = - 6;
                    checks[9] = true;
                }
                if (grid[i + 1][j] == - 1) {
                    grid[i + 1][j] = - 6;
                    checks[8] = true;
                }
            }
            if (j >= 1 && grid[i][j - 1] == - 1) {
                grid[i][j - 1] = - 6;
                checks[4] = true;
            }
            if (j <= GW - 2 && grid[i][j + 1] == - 1) {
                grid[i][j + 1] = - 6;
                checks[6] = true;
            }
        }

    }

    private void ReturnFromChecking(int i, int j) {
        if (Gdx.input.getY() > Info.hudHeight)
        if (i >= 1) {
            if (j >= 1 && grid[i - 1][j - 1] == - 6 && checks[1]) grid[i - 1][j - 1] = - 1;
            if (j <= GW - 2 && grid[i - 1][j + 1] == - 6 && checks[3]) grid[i - 1][j + 1] = - 1;
            if (grid[i - 1][j] == - 6 && checks[2]) grid[i - 1][j] = - 1;
        }
        if (i <= GH - 2) {
            if (j >= 1 && grid[i + 1][j - 1] == - 6 && checks[7]) grid[i + 1][j - 1] = - 1;
            if (j <= GW - 2 && grid[i + 1][j + 1] == - 6 && checks[9]) grid[i + 1][j + 1] = - 1;
            if (grid[i + 1][j] == - 6 && checks[8]) grid[i + 1][j] = - 1;
        }
        if (Gdx.input.getY() > Info.hudHeight) {
            if (j >= 1 && grid[i][j - 1] == -6 && checks[4]) grid[i][j - 1] = -1;
            if (j <= GW - 2 && grid[i][j + 1] == -6 && checks[6]) grid[i][j + 1] = -1;
        }
    }

    private void Lost() {
        for (int i = 0; i < GH; i ++) {
            for (int j = 0; j < GW; j ++) {
                if (logicalGrid[i][j] == -1 && grid[i][j] != - 2) grid[i][j] = - 3;
                if (grid[i][j] == - 2 && logicalGrid[i][j] != - 1) grid[i][j] = - 4;
            }
        }
        endGame = 1;
        firstClick = false;
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("HudBackgroundLose.png")))));
    }

    private void Won() {
        switch (Info.difficulty) {
            case 0:
                if (time < Gdx.app.getPreferences("easyHighscore").getInteger("easyHighscore") || Gdx.app.getPreferences("easyHighscore").getInteger("easyHighscore") == 0) {
                    easyHighscore.putInteger("easyHighscore", (int) time);
                    easyHighscore.flush();
                }
                break;
            case 1:
                if (time < Gdx.app.getPreferences("mediumHighscore").getInteger("mediumHighscore") || Gdx.app.getPreferences("mediumHighscore").getInteger("mediumHighscore") == 0) {
                    mediumHighscore.putInteger("mediumHighscore", (int) time);
                    mediumHighscore.flush();
                }
                break;
            case 2:
                if (time < Gdx.app.getPreferences("hardHighscore").getInteger("hardHighscore") || Gdx.app.getPreferences("hardHighscore").getInteger("hardHighscore") == 0) {
                    hardHighscore.putInteger("hardHighscore", (int) time);
                    hardHighscore.flush();
                }
                break;
        }
        firstClick = false;
        endGame = 2;
        for (int i = 0; i < GH; i ++) {
            for (int j = 0; j < GW; j ++) {
                if (grid[i][j] == - 1) {
                    grid[i][j] = logicalGrid[i][j];
                }
            }
        }
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("HudBackgroundWin.png")))));
        particlesWin.start();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                particlesWin2.start();
            }
        }, 1000);
    }
}
