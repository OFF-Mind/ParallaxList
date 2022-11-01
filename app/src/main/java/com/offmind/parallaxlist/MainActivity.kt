package com.offmind.parallaxlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offmind.parallaxlist.ui.theme.ParallaxListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParallaxListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        ParallaxList(contentItems = provideSampleListItems())
                    }
                }
            }
        }
    }
}

@Composable
fun ParallaxList(
    modifier: Modifier = Modifier,
    contentItems: List<ParallaxListItem>
) {
    val verticalItemBias = remember { mutableStateListOf<Float>() }
    val displayContent = remember { mutableStateOf(false) }
    val lazyListState = LazyListState()

    repeat(contentItems.size) {
        verticalItemBias.add(-1f)
    }

    val itemHeight = (LocalConfiguration.current.screenWidthDp * (3f / 2f)) * 0.7f
    var totalHeight = 0f

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val layoutInfo = lazyListState.layoutInfo

            val firstVisibleItem = lazyListState.firstVisibleItemIndex

            repeat(layoutInfo.visibleItemsInfo.size) {
                val offset = layoutInfo.visibleItemsInfo[it].offset
                val imageBias = offset / totalHeight
                verticalItemBias[firstVisibleItem + it] = imageBias.coerceIn(-1f, 1f)
            }

            return Offset.Zero
        }
    }

    LazyColumn(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .onGloballyPositioned {
                //TODO think how to avoid this call
                totalHeight = it.size.height.toFloat()
                val layoutInfo = lazyListState.layoutInfo

                val firstVisibleItem = lazyListState.firstVisibleItemIndex

                repeat(layoutInfo.visibleItemsInfo.size) {
                    val offset = layoutInfo.visibleItemsInfo[it].offset
                    val imageBias = offset / totalHeight
                    verticalItemBias[firstVisibleItem + it] = imageBias.coerceIn(-1f, 1f)
                }

                displayContent.value = true
            },
        state = lazyListState
    ) {

        repeat(contentItems.size) {
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .height(itemHeight.dp)
                ) {
                    if (displayContent.value) {
                        ParallaxLayoutItem(
                            contentItem = contentItems[it],
                            verticalItemBias = verticalItemBias[it]
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParallaxLayoutItem(
    contentItem: ParallaxListItem,
    verticalItemBias: Float
) {

    Image(
        modifier = Modifier.fillMaxWidth(),
        painter = painterResource(id = contentItem.imageRecource),
        contentScale = ContentScale.Crop,
        alignment = BiasAbsoluteAlignment(0f, verticalItemBias),
        contentDescription = "background",
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            modifier = Modifier
                .background(
                    color = Color(0f, 0f, 0f, 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(10.dp),
            text = contentItem.title,
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .background(
                    color = Color(0f, 0f, 0f, 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = contentItem.temperature,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )
            Text(
                text = contentItem.humidity,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ParallaxListTheme {
        Greeting("Android")
    }
}


/**
 * Provide fake items for testing
 */
fun provideSampleListItems(): List<ParallaxListItem> {
    val listItems: MutableList<ParallaxListItem> = mutableListOf()
    listItems.add(
        ParallaxListItem(
            R.drawable.image_01,
            "Germany",
            "weather: 20°C",
            "humidity: 80%"
        )
    )
    listItems.add(
        ParallaxListItem(
            R.drawable.image_02,
            "Poland",
            "weather: 19°C",
            "humidity: 82%"
        )
    )
    listItems.add(
        ParallaxListItem(
            R.drawable.image_03,
            "Britain",
            "weather: 22°C",
            "humidity: 85%"
        )
    )
    listItems.add(
        ParallaxListItem(
            R.drawable.image_04,
            "France",
            "weather: 15°C",
            "humidity: 79%"
        )
    )
    listItems.add(
        ParallaxListItem(
            R.drawable.image_05,
            "Belarus",
            "weather: 12°C",
            "humidity: 76%"
        )
    )
    return listItems
}

data class ParallaxListItem(
    val imageRecource: Int,
    val title: String,
    val temperature: String,
    val humidity: String)
