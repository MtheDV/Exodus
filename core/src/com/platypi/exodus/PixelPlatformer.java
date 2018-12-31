package com.platypi.exodus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PixelPlatformer extends Game {
	@Override
	public void create () {
		super.setScreen(new PixelSplash(this));
	}

	@Override
	public void render () {

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
			Gdx.app.exit();

		super.render();
	}

	@Override
	public void dispose () {
		super.dispose();
	}
}
