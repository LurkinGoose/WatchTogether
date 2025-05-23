package com.example.watch_together.movieCards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.watch_together.models.MediaItem
import com.example.watch_together.models.getRatingColor
import com.example.watch_together.ui.theme.Green
import java.util.Locale


@Composable
fun MediaRowItem(
    modifier: Modifier = Modifier,
    mediaItem: MediaItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .height(285.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = modifier
                .height(240.dp)
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            AsyncImage(
                model = mediaItem.fullPosterPath,
                contentDescription = mediaItem.titleText,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            mediaItem.voteAverage?.takeIf { it > 0.0 }?.let { rating ->
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(40.dp)
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 8.dp)
                        .background(getRatingColor(rating))
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = String.format(Locale.US, "%.1f", rating),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }

        }

        Text(
            text = mediaItem.titleText,
            style = TextStyle(
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}



