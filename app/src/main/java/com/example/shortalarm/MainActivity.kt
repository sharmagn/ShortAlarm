package com.example.shortalarm

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.shortalarm.ui.theme.ShortAlarmTheme
import kotlinx.coroutines.delay
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.concurrent.TimeUnit

var mediaPlayer: MediaPlayer? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShortAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShortAlarmScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortAlarmScreen() {
    Scaffold(
        topBar = {
                 ShowTopBar()
        },
        content = {padding ->
            var playEnabled = remember { mutableStateOf(true) }
            var stopEnabled = remember { mutableStateOf(false) }
            var remainingTime = remember { mutableStateOf("00:00") }
            Column(modifier = Modifier.fillMaxHeight().padding(padding)) {
                ShowText(remainingTime)
                Spacer(modifier = Modifier.height(40.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    ShowPlayButton(playEnabled,stopEnabled,remainingTime)
                    ShowStopButton(playEnabled, stopEnabled,remainingTime)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTopBar() {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.app_name),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp)
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Cyan,
            titleContentColor = Color.Blue
        )
    )
}

@Composable
fun ShowText(timeRemaining: MutableState<String>) {
    Text(
        text = timeRemaining.value,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 90.dp, bottom = 50.dp)
    )
}

@Composable
fun ShowPlayButton(playEnabledState: MutableState<Boolean>,
                   stopEnabledState: MutableState<Boolean>,
                   balanceTime: MutableState<String>) {
    val context = LocalContext.current
    Button(enabled = playEnabledState.value, onClick = {
        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = String.format("%02d:%02d",
                    millisUntilFinished/60000,
                    ((millisUntilFinished%60000)/1000)
                )
                balanceTime.value = time
                Log.e("MainActivity", "Time remaining: "+time)
                Log.e("MainActivity","Code inside ShowPlayButton() onClick executed")
            }

            override fun onFinish() {
                playMusic(context)
            }
        }.start()
        playEnabledState.value = false
        stopEnabledState.value = true
    },
        modifier = Modifier.wrapContentWidth(Alignment.Start).width(150.dp)) {
        Text(text = stringResource(R.string.start_text))
    }
}

@Composable
fun ShowStopButton(playEnabledState: MutableState<Boolean>,
                   stopEnabledState: MutableState<Boolean>,
                   balanceTime: MutableState<String>) {
    Button(enabled = stopEnabledState.value, onClick = {
        balanceTime.value = "00:00"
        stopMusic()
        stopEnabledState.value = false
        playEnabledState.value = true
        Log.e("MainActivity","Code inside ShowStopButton() onClick executed")
        },
        modifier = Modifier.wrapContentWidth(Alignment.End).width(150.dp)) {
        Text(text = stringResource(R.string.stop_text))
    }
}

fun playMusic(context: Context) {
    if (mediaPlayer == null) {
        mediaPlayer = MediaPlayer.create(context, R.raw.water)
        mediaPlayer!!.isLooping = true
        mediaPlayer!!.start()
    } else mediaPlayer!!.start()
}

fun stopMusic() {
    if (mediaPlayer != null) {
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = null
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShortAlarmTheme {
        ShortAlarmScreen()
    }
}