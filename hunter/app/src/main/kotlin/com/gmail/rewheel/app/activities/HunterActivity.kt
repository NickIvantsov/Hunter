package com.gmail.rewheel.app.activities

import android.app.Activity
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import timber.log.Timber
import java.util.*
import kotlin.math.sqrt

class HunterActivity : Activity() {

    var numberHorizontalPixels = 0
    var numberVerticalPixels = 0
    var blockSize = 0
    var gridWidth = 40
    var gridHeight = 0
    var horizontalTouched = -100
    var verticalTouched = -100
    var subHorizontalPosition = 0
    var subVerticalPosition = 0
    var hit = false
    var shotsTaken = 0
    var distanceFromSub = 0
    var debugging = false
    lateinit var gameView: ImageView
    lateinit var blankBitmap: Bitmap
    lateinit var canvas: Canvas
    lateinit var paint: Paint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Получение размера экрана
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        // Инициализируем наши переменные на основе размера
        // в зависимости от разрешения экрана
        numberHorizontalPixels = size.x
        numberVerticalPixels = size.y
        blockSize = numberHorizontalPixels / gridWidth
        gridHeight = numberVerticalPixels / blockSize

        //инициализация обьектов для рисования
        blankBitmap = Bitmap.createBitmap(
            numberHorizontalPixels,
            numberVerticalPixels,
            Bitmap.Config.ARGB_8888
        )

        canvas = Canvas(blankBitmap)
        gameView = ImageView(this)
        paint = Paint()

        setContentView(gameView)

        Timber.d("In onCreate")
        newGame()
        draw()
    }

    /*
     * Этот код будет выполняться, когда новыйигра должна быть запущена Так и будетпроизойдет,
     * когда приложение запускается впервые
     * и после того, как игрок выигрывает игру
     */
    private fun newGame() {
        val random = Random()
        subHorizontalPosition = random.nextInt(gridWidth)
        subVerticalPosition = random.nextInt(gridHeight)
        shotsTaken = 0

        Timber.d("In newGame")
    }

    /*Здесь мы будем делать все рисование.Линии сетки, HUD,сенсорный индикатор и«БУМ», когда суб*/
    private fun draw() {
        gameView.setImageBitmap(blankBitmap)

        //очистка экарана белым цветом
        canvas.drawColor(Color.argb(255, 255, 255, 255))

        //изменить цвет рисовки на черный
        paint.color = Color.argb(255, 0, 0, 0)


        with(canvas) {
            //нарисовать вертикальную линию от сетки
            for (i in 0 until gridWidth) {
                drawLine(
                    blockSize * i.toFloat(), 0f,
                    blockSize * i.toFloat(), numberVerticalPixels.toFloat(),
                    paint
                )
            }

            //нарисовать горизонтальную линию от сетки
            for (i in 0 until gridHeight) {
                drawLine(
                    0f, blockSize * i.toFloat(),
                    numberHorizontalPixels.toFloat(), blockSize * i.toFloat(),
                    paint
                )
            }

            drawRect(
                (horizontalTouched * blockSize).toFloat(),
                (verticalTouched * blockSize).toFloat(),
                ((horizontalTouched * blockSize) + blockSize).toFloat(),
                ((verticalTouched * blockSize) + blockSize).toFloat(),
                paint
            )
        }

        // Re-size the text appropriate for the
        // score and distance text
        with(paint) {
            textSize = (blockSize * 2).toFloat()
            color = Color.argb(255, 0, 0, 255)
        }
        canvas.drawText(
            "Shots Taken: " + shotsTaken +
                    " Distance: " + distanceFromSub,
            blockSize.toFloat(), blockSize * 1.75f,
            paint
        )

        Timber.d("In draw")
        if (debugging)
            printDebuggingText()

    }

    /*игроккоснулся экрана*/
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        Timber.d("In onTouchEvent")
        if (motionEvent?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
// Process the player's shot by passing the
// coordinates of the player's finger to takeShot
            takeShot(motionEvent.x, motionEvent.y)
        }

        return true
    }

    /*Код здесь будет выполняться, когдаигрок нажимает
    на экранрассчитать расстояние от суби определить попадание или промах*/
    private fun takeShot(touchX: Float, touchY: Float) {
        // Add one to the shotsTaken variable
        shotsTaken++

        // Convert the float screen coordinates
// into int grid coordinates
        // Convert the float screen coordinates
// into int grid coordinates
        horizontalTouched = touchX.toInt() / blockSize
        verticalTouched = touchY.toInt() / blockSize

        // Did the shot hit the sub?
        hit = horizontalTouched == subHorizontalPosition
                && verticalTouched == subVerticalPosition


        // How far away horizontally and vertically
// was the shot from the sub
        // How far away horizontally and vertically
// was the shot from the sub
        val horizontalGap = horizontalTouched.toInt() -
                subHorizontalPosition
        val verticalGap = verticalTouched.toInt() -
                subVerticalPosition

        // Use Pythagoras's theorem to get the
// distance travelled in a straight line
        // Use Pythagoras's theorem to get the
// distance travelled in a straight line
        distanceFromSub = sqrt(
            (horizontalGap * horizontalGap +
                    verticalGap * verticalGap).toDouble()
        ).toInt()

        // If there is a hit call boom
        if (hit)
            boom()
        // Otherwise call draw as usual
        else draw()

        Timber.d("In takeShot")
    }

    //этот говорит "Boom"
    private fun boom() {
        gameView.setImageBitmap(blankBitmap)
// заличаем экран крассным цветом
        canvas.drawColor(Color.argb(255, 255, 0, 0))
// рисуем огромный белый текст
        paint.color = Color.argb(255, 255, 255, 255)
        paint.textSize = (blockSize * 10).toFloat()
        canvas.drawText(
            "BOOM!", (blockSize * 4).toFloat(),
            (blockSize * 14).toFloat(), paint
        )
// рисуем текст для перезапуска
        paint.textSize = (blockSize * 2).toFloat()
        canvas.drawText(
            "Take a shot to start again",
            (blockSize * 8).toFloat(),
            (blockSize * 18).toFloat(), paint
        )
// Start a new game
        newGame()
    }

    //Печатает тектс отладки
    private fun printDebuggingText() {
        paint.textSize = blockSize.toFloat()
        with(canvas) {
            drawText(
                "numberHorizontalPixels = "
                        + numberHorizontalPixels,
                50F, (blockSize * 3).toFloat(), paint
            )
            drawText(
                "numberVerticalPixels = "
                        + numberVerticalPixels,
                50F, (blockSize * 4).toFloat(), paint
            )
            drawText(
                "gridWidth = $gridWidth",
                50F, (blockSize * 6).toFloat(), paint
            )
            drawText(
                "gridHeight = $gridHeight",
                50F, (blockSize * 7).toFloat(), paint
            )
            drawText(
                "horizontalTouched = " +
                        horizontalTouched, 50F,
                (blockSize * 8).toFloat(), paint
            )

            drawText(
                "verticalTouched = " +
                        verticalTouched, 50F,
                (blockSize * 9).toFloat(), paint
            )

            drawText(
                "subHorizontalPosition = " +
                        subHorizontalPosition, 50F,
                (blockSize * 10).toFloat(), paint
            )

            drawText(
                "subVerticalPosition = " +
                        subVerticalPosition, 50F,
                (blockSize * 11).toFloat(), paint
            )

            drawText(
                "hit = $hit",
                50F, (blockSize * 12).toFloat(), paint
            )
            drawText(
                "shotsTaken = " +
                        shotsTaken,
                50F, (blockSize * 13).toFloat(), paint
            )

            drawText(
                "debugging = $debugging",
                50F, (blockSize * 14).toFloat(), paint
            )
        }
    }
}