package com.example.watch_together.movieCards

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.AsyncImage
import com.example.watch_together.R
import com.example.watch_together.models.MediaItem
import com.example.watch_together.models.MediaType
import com.example.watch_together.ui.theme.Black
import com.example.watch_together.ui.theme.DarkGray
import com.example.watch_together.ui.theme.Red
import java.util.Locale
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
fun RatingStars(
    rating: Double,
    modifier: Modifier = Modifier,
    maxStars: Int = 5
) {
    val roundedStars = (round(rating / 2 * 2) / 2).coerceIn(0.0, maxStars.toDouble())
    val fullStars = roundedStars.toInt()
    val hasHalfStar = (roundedStars - fullStars) == 0.5
    val emptyStars = maxOf(0, maxStars - fullStars - if (hasHalfStar) 1 else 0)

    Row(modifier = modifier) {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star",
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
        }

        if (hasHalfStar) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = "Half Star",
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
        }

        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Default.StarOutline,
                contentDescription = "Empty Star",
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun MediaColumnItem(
    modifier: Modifier = Modifier,
    maxSwipeWidth: Dp = 170.dp,
    mediaItem: MediaItem,
    isFavorite: Boolean,
    isGroupFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleGroupFavorite: () -> Unit,
) {
    val swipeableState = rememberSwipeableState(0)

    val maxSwipePx = with(LocalDensity.current) { maxSwipeWidth.toPx() }
    val anchors = mapOf(0f to 0, maxSwipePx to 1)


    var shouldClose by remember { mutableStateOf(false) }

    LaunchedEffect(shouldClose) {
        if (shouldClose) {
            swipeableState.animateTo(0)
            shouldClose = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal,
                reverseDirection = true
            )
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(maxSwipeWidth)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(DarkGray)
                    .clickable {
                        onToggleFavorite()
                        shouldClose = true
                    },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Избранное",
                        tint = Red,
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = if (isFavorite) "Удалить из избранного" else "Добавить\nв избранное",
                        color = Red,
                        modifier = Modifier
                            .padding(6.dp),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

            }
            VerticalDivider(
                color = Color.Gray,
                thickness = 1.dp
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(DarkGray)
                    .clickable {
                        onToggleGroupFavorite()
                        shouldClose = true
                    },
                contentAlignment = Alignment.Center,

            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Избранное",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = if (isGroupFavorite) "Удалить\nиз группы" else "Добавить\nв группу",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(6.dp),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(-swipeableState.offset.value.roundToInt(), 0) }
                .fillMaxSize()
                .background(Black)
        ) {
            CardContent(
                modifier = Modifier.fillMaxSize(),
                mediaItem = mediaItem,
                isFavorite = isFavorite,
                isGroupFavorite = isGroupFavorite,
                onClick = onClick,
                onToggleFavorite = onToggleFavorite,
                onToggleGroupFavorite = onToggleGroupFavorite
            )
        }
    }
}

@Composable
private fun CardContent(
    modifier: Modifier = Modifier,
    mediaItem: MediaItem,
    isFavorite: Boolean,
    isGroupFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleGroupFavorite: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clickable { onClick() }
    ) {
        Row {

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(155.dp)
                    .background(Color.Transparent)
            ){

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .width(100.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Transparent)
                ) {

                    if (mediaItem.fullPosterPath == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkGray)
                                .border(2.dp, Color.Gray, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (mediaItem.mediaType) {
                                    MediaType.MOVIE -> Icons.Outlined.Movie
                                    MediaType.TV -> Icons.Filled.Tv
                                },
                                contentDescription = "Нет изображения",
                                tint = Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    } else {
                        AsyncImage(
                            model = mediaItem.fullPosterPath,
                            contentDescription = mediaItem.titleText,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                if (isFavorite){
                    Icon(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .offset(x = (-8).dp, y = (-5).dp)
                        ,
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Добавлено в избранное",
                        tint = Red
                    )
                }
                if (isGroupFavorite){
                    Icon(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .offset(x = (-8).dp, y = (-5).dp),

                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Добавлено в группу",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when (mediaItem.mediaType) {
                            MediaType.MOVIE -> Icons.Outlined.Movie
                            MediaType.TV -> Icons.Filled.Tv
                        },
                        contentDescription = "Media Type",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    mediaItem.releaseDate?.take(4)?.let { year ->
                        Text(
                            text = "($year)",
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    mediaItem.ageCertification?.let { certification ->
                        Text(
                            text = certification,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (mediaItem.voteAverage != null){
                        Row (
                            modifier = Modifier
                                .weight(1f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = String.format(Locale.US, "%.1f", mediaItem.voteAverage),
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Star",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = mediaItem.titleText,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = mediaItem.genre ?: "",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = mediaItem.productionCountries ?: "",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

//        Row(modifier = Modifier.align(Alignment.TopEnd)) {
//            IconButton(onClick = onToggleFavorite) {
//                Icon(
//                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
//                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
//                    tint = if (isFavorite) Color.Red else Color.Gray
//                )
//            }
//
//            IconButton(onClick = onToggleGroupFavorite) {
//                Icon(
//                    imageVector = Icons.Default.PushPin,
//                    contentDescription = if (isGroupFavorite) "Удалить из группового избранного" else "Добавить в групповое избранное",
//                    tint = if (isGroupFavorite) MaterialTheme.colorScheme.primary else Color.Gray
//                )
//            }
//        }
    }
}
