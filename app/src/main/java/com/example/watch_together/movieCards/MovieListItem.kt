package com.example.watch_together.movieCards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.watch_together.models.Movie
import com.example.watch_together.viewModels.FavoritesViewModel


@Composable
fun MovieListItem(moviesList: Movie, favoritesViewModel: FavoritesViewModel, onClick: () -> Unit) {
    var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    val favoriteMovies by favoritesViewModel.favoriteMovies.collectAsState()
    val isFavorite = favoriteMovies.any { it.id == moviesList.id }

    Row (modifier = Modifier
        .fillMaxWidth(1f)
        .background(color = Color.White)
        .padding(start = 8.dp),


    ) {
        Box(modifier = Modifier
            .fillMaxHeight(1f)
            .width(130.dp)
            .height(227.dp)
            .background(color = Color.Transparent)
            .padding(top = 16.dp, bottom = 16.dp)



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



        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = moviesList.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f).padding(top = 16.dp)
            )

            IconButton(
                onClick = {
                    if (isFavorite) {
                        favoritesViewModel.removeFromFavorites(moviesList.id)
                    } else {
                        favoritesViewModel.addToFavorites(moviesList.id)
                    }
                }, modifier = Modifier.size(56.dp)

            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}

//@Preview(showBackground = true)
//@Composable
//fun MovieItemPreview() {
//    Watch_TogetherTheme {
//        MovieItem(movie = Movie("Inception", "https://image.tmdb.org/t/p/w500/zo8CIjJ2nfNOevqNajwMRO6Hwka.jpg"))
//    }
//}
