package my.minesweeper.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import my.minesweeper.game.Info;
import my.minesweeper.game.MainClass;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Info.Width;
		config.height = Info.Height + Info.hudHeight;
		config.samples = 10;
		config.title = "Minesweeper";
		config.addIcon("Icon3.png", Files.FileType.Internal);
		config.addIcon("Icon2.png", Files.FileType.Internal);
		config.addIcon("Icon1.png", Files.FileType.Internal);
		new LwjglApplication(new MainClass(), config);
	}
}
