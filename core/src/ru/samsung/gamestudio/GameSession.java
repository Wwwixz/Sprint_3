package ru.samsung.gamestudio;
import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.managers.MemoryManager;

import java.util.ArrayList;
import java.util.Random;


public class GameSession {

    public GameState state;
    long nextTrashSpawnTime;
    long nextBonusSpawnTime;
    long sessionStartTime;
    long pauseStartTime;
    private int score;
    int destructedTrashNumber;

    public GameSession() {
    }

    public void startGame() {
        state = GameState.PLAYING;
        score = 0;
        destructedTrashNumber = 0;
        sessionStartTime = TimeUtils.millis();
        nextTrashSpawnTime = sessionStartTime + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
                * getTrashPeriodCoolDown());
        nextBonusSpawnTime = sessionStartTime + 5000 + (new Random()).nextInt(10000);
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        pauseStartTime = TimeUtils.millis();
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        sessionStartTime += TimeUtils.millis() - pauseStartTime;
        nextBonusSpawnTime += TimeUtils.millis() - pauseStartTime;
    }

    public void endGame() {
        updateScore();
        state = GameState.ENDED;
        ArrayList<Integer> recordsTable = MemoryManager.loadRecordsTable();
        if (recordsTable == null) {
            recordsTable = new ArrayList<>();
        }
        int foundIdx = 0;
        for (; foundIdx < recordsTable.size(); foundIdx++) {
            if (recordsTable.get(foundIdx) < getScore()) break;
        }
        recordsTable.add(foundIdx, getScore());
        MemoryManager.saveTableOfRecords(recordsTable);
    }

    public void destructionRegistration() {
        destructedTrashNumber += 1;
    }

    public void updateScore() {
        score = (int) (TimeUtils.millis() - sessionStartTime) / 100 + destructedTrashNumber * 100;
    }

    public int getScore() {
        return score;
    }

    public boolean shouldSpawnTrash() {
        if (nextTrashSpawnTime <= TimeUtils.millis()) {
            nextTrashSpawnTime = TimeUtils.millis() + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
                    * getTrashPeriodCoolDown());
            return true;
        }
        return false;
    }

    public boolean shouldSpawnBonus() {
        if (nextBonusSpawnTime <= TimeUtils.millis()) {
            nextBonusSpawnTime = TimeUtils.millis() + 10000 + (new Random()).nextInt(15000);
            return true;
        }
        return false;
    }

    public float getCurrentTrashVelocity() {
        float timeInSeconds = (TimeUtils.millis() - sessionStartTime) / 1000f;
        // Скорость растет от начальной до +100% за 5 минут
        return GameSettings.TRASH_VELOCITY * (1 + Math.min(timeInSeconds / 300f, 1.0f));
    }

    public int getNewTrashLives() {
        float timeInSeconds = (TimeUtils.millis() - sessionStartTime) / 1000f;
        // После 1 минуты есть шанс 20%, после 3 минут - 50% появления крепкого мусора
        float chance = Math.min(timeInSeconds / 360f, 0.5f);
        return (new Random().nextFloat() < chance) ? 2 : 1;
    }

    private float getTrashPeriodCoolDown() {
        return (float) Math.exp(-0.003 * (TimeUtils.millis() - sessionStartTime + 1) / 1000);
    }
}
