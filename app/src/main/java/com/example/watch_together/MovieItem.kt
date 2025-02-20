package com.example.watch_together

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter


@Composable
fun MovieItem(moviesList: Movie) {
    var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    Row (modifier = Modifier
        .fillMaxWidth(1f)
        .height(200.dp)
        .background(color = Color.LightGray))
    {
        Box(modifier = Modifier
            .fillMaxHeight(1f)
            .width(140.dp)
            .background(color = Color.DarkGray)
            .align(Alignment.CenterVertically)
        ) {
            AsyncImage(
                model = moviesList.fullPosterPath,
                contentDescription = moviesList.title,
                modifier = Modifier
                    .align(Center)
                    .fillMaxSize(),
                contentScale = ContentScale.Fit,
                onState = { state ->
                    imageState = state
                }
            )
            when (val state = imageState) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Center))
                }
                is AsyncImagePainter.State.Error -> {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Перезагрузить постер")
                }
                else -> {

                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = moviesList.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

    }

}

//@Preview(showBackground = true)
//@Composable
//fun MovieItemPreview() {
//    Watch_TogetherTheme {
//        MovieItem(movie = Movie("Inception", "https://image.tmdb.org/t/p/w500/zo8CIjJ2nfNOevqNajwMRO6Hwka.jpg"))
//    }
//}
