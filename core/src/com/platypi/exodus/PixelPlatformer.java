package com.platypi.exodus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import static com.platypi.exodus.PixelMenu.sounds;

public class PixelPlatformer extends Game {

	static AdService adService;

	public PixelPlatformer(AdService adService) {
		this.adService = adService;
	}

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
