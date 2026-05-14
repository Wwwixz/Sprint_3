package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;

import java.util.Random;

public class TrashObject extends GameObject {

    private static final int paddingHorizontal = 30;

    private int livesLeft;

    public TrashObject(int width, int height, String texturePath, World world, float velocity, int lives) {
        super(
                texturePath,
                width / 2 + paddingHorizontal + (new Random()).nextInt((GameSettings.SCREEN_WIDTH - 2 * paddingHorizontal - width)),
                GameSettings.SCREEN_HEIGHT + height / 2,
                width, height,
                GameSettings.TRASH_BIT,
                world
        );

        body.setLinearVelocity(new Vector2(0, -velocity));
        livesLeft = lives;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (livesLeft > 1) {
            batch.setColor(Color.ORANGE); // Крепкий мусор подсвечивается оранжевым
        }
        super.draw(batch);
        if (livesLeft > 1) {
            batch.setColor(Color.WHITE); // Возвращаем обычный цвет
        }
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }

    public boolean isInFrame() {
        return getY() + height / 2 > 0;
    }

    @Override
    public void hit() {
        livesLeft -= 1;
    }
}
