package com.test.smartapp

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

import ktx.app.KtxGame
import ktx.app.KtxScreen
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random

private const val GAME_DURATION = 30
private const val HIGH_SCORE_KEY = "highScores"

class MainGame : KtxGame<KtxScreen>() {
    private lateinit var batch: SpriteBatch
    private lateinit var targetTexture: Texture
    private lateinit var targetRectangle: Rectangle
    private lateinit var bitmapFont: BitmapFont
    private lateinit var gamePreferences: Preferences

    private var score = 0
    private var highScores = 0
    private var gameStart = false
    private var gameTimeLeft = 0
    private var gameTimer = Timer()
    private var touchPosition = Vector2()
    private var touchRectangle = Rectangle()
    private var randomTargetPosition = Random

    override fun create() {
        batch = SpriteBatch()
        targetTexture = Texture("frog.png")
        bitmapFont = BitmapFont().apply {
            color = Color.WHITE
            data.scale(5f)
        }

        gamePreferences = Gdx.app.getPreferences("SmartApp")
        highScores = getHighScore()

        startTimer()
    }

    override fun render() {
        Gdx.gl.glClearColor(0.416f, 0.549f, 0.667f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()

        if (gameTimeLeft <= 0) {
            gameStart = false
        }

        if (!gameStart) {
            val textHighScores = "High Scores: $highScores"
            val textTabToStart = "TAB TO START GAME"

            bitmapFont.draw(
                batch,
                textHighScores,
                fixBitmapFontPositionWidth(textHighScores) / 2f,
                Gdx.graphics.height - 50f
            )

            if (score == 0) {
                bitmapFont.draw(
                    batch,
                    textTabToStart,
                    fixBitmapFontPositionWidth(textTabToStart) / 2f,
                    fixBitmapFontPositionHeight(textTabToStart) / 2f
                )
            } else {
                val textYourScore = "Your Score: $score"
                val textGameOver = "GAME OVER"

                bitmapFont.draw(
                    batch,
                    textYourScore,
                    fixBitmapFontPositionWidth(textYourScore) / 2f,
                    fixBitmapFontPositionHeight(textYourScore) / 2f + 100f
                )

                bitmapFont.draw(
                    batch,
                    textGameOver,
                    fixBitmapFontPositionWidth(textGameOver) / 2f,
                    fixBitmapFontPositionHeight(textGameOver) / 2f
                )

                if (score > highScores) {
                    highScores = score
                    saveHighScore(score)
                }
            }


            if (Gdx.input.justTouched()) {
                startGame()
            }
        } else {
            val textTimeLeft = "Time left: $gameTimeLeft"
            val textScore = "Score: $score"

            batch.draw(
                targetTexture,
                targetRectangle.x,
                targetRectangle.y,
                targetRectangle.width,
                targetRectangle.height
            )

            bitmapFont.draw(
                batch,
                textTimeLeft,
                50f,
                fixBitmapFontPositionHeight(textTimeLeft)
            )

            bitmapFont.draw(
                batch,
                textScore,
                fixBitmapFontPositionWidth(textScore) - 50f,
                fixBitmapFontPositionHeight(textScore)
            )


            if (Gdx.input.justTouched()) {
                touchPosition =
                    Vector2(Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
                touchRectangle = Rectangle(touchPosition.x, touchPosition.y, 1f, 1f)

                if (Intersector.overlaps(touchRectangle, targetRectangle)) {
                    changeTargetPosition()
                    score++
                }
            }
        }
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }

    private fun changeTargetPosition() {
        targetRectangle.setPosition(
            randomTargetPosition.nextFloat() * (Gdx.graphics.width - targetRectangle.width / 2),
            randomTargetPosition.nextFloat() * (Gdx.graphics.height - targetRectangle.height / 2)
        )
    }

    private fun startTimer() {
        gameTimeLeft = GAME_DURATION
        gameTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (gameStart) gameTimeLeft--
            }
        }, 0, 1000)
    }

    private fun startGame() {
        score = 0
        gameStart = true
        gameTimeLeft = GAME_DURATION

        targetRectangle = Rectangle(
            ((Gdx.graphics.width / 2) - targetTexture.width * 2).toFloat(),
            ((Gdx.graphics.height / 2) - targetTexture.height * 2).toFloat(),
            (targetTexture.width * 2).toFloat(),
            (targetTexture.height * 2).toFloat()
        )
    }

    private fun getHighScore(): Int {
        return gamePreferences.getInteger(HIGH_SCORE_KEY, 20)
    }

    private fun saveHighScore(score: Int) {
        gamePreferences.putInteger(HIGH_SCORE_KEY, score)
        gamePreferences.flush()
    }

    private fun fixBitmapFontPositionWidth(string: String): Float {
        val layout = GlyphLayout(bitmapFont, string)
        return (Gdx.graphics.width - layout.width)
    }

    private fun fixBitmapFontPositionHeight(string: String): Float {
        val layout = GlyphLayout(bitmapFont, string)
        return (Gdx.graphics.height - layout.height)
    }
}