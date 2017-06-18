package my.minesweeper.game;

/**
 * Created by iulyus on 05.06.2017.
 */
public class Info {
    public static final int hudHeight = 50;
    public static final int Width = 400;
    public static final int Height = 400;
    static int mineSize = 40;
    static int gridWidth = Width / mineSize;
    static int gridHeight = Height / mineSize;
    static int minesNr = 15;
    public static int difficulty = 0;
}
