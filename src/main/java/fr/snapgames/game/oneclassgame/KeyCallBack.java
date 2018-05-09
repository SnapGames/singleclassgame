package fr.snapgames.game.oneclassgame;

import java.awt.event.KeyEvent;

import fr.snapgames.game.oneclassgame.Game.GameObject;

public interface KeyCallBack {
	public void KeyPressed(GameObject go, KeyEvent e);

	public void KeyReleased(GameObject go, KeyEvent e);

	public void KeyTyped(GameObject go, KeyEvent e);
}
