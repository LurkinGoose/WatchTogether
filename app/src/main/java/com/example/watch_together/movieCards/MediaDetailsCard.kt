package com.example.watch_together.movieCards

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.watch_together.models.MediaItem
import com.example.watch_together.models.MediaType
import com.example.watch_together.models.formatRuntime
import com.example.watch_together.models.formatVoteCount
import com.example.watch_together.models.getRatingColor
import com.example.watch_together.ui.theme.AlphaBlack
import com.example.watch_together.ui.theme.Black
import com.example.watch_together.ui.theme.DarkGray
import java.util.Locale

@Composable
fun MediaDetailsCard(
    modifier: Modifier = Modifier,
    mediaItem: MediaItem,
    isFavorite: Boolean,
    isGroupFavorite: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleGroupFavorite: () -> Unit,
) {

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    if (mediaItem.backdropPath == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (mediaItem.mediaType) {
                                    MediaType.MOVIE -> Icons.Outlined.Movie
                                    MediaType.TV -> Icons.Filled.Tv
                                },
                                contentDescription = "Нет обложки",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    } else {
                        AsyncImage(
                            model = mediaItem.fullBackdropPath,
                            contentDescription = "Обложка ${mediaItem.titleText}",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Black)
                                )
                            )
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = mediaItem.titleText,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 28.sp,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(4f, 4f),
                            )
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        buildAnnotatedString {
                            appendInlineContent("icon", "[icon] ")
                            append(" ")
                            append(
                                when (mediaItem.mediaType) {
                                    MediaType.MOVIE -> "Фильм:"
                                    MediaType.TV -> "Сериал:"
                                }
                            )
                            append(" ")
                            append(mediaItem.originalTitle?.takeIf { it.isNotBlank() } ?: mediaItem.titleText)
                        },
                        inlineContent = mapOf(
                            "icon" to InlineTextContent(
                                Placeholder(
                                    width = 24.sp,
                                    height = 24.sp,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                )
                            ) {
                                Icon(
                                    imageVector = when (mediaItem.mediaType) {
                                        MediaType.MOVIE -> Icons.Outlined.Movie
                                        MediaType.TV -> Icons.Filled.Tv
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        ),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val detailsList1 = listOfNotNull(
                        mediaItem.releaseDate?.take(4)?.let {
                            when (mediaItem.mediaType) {
                                MediaType.MOVIE -> it
                                MediaType.TV -> "с $it"
                            }
                        },
                        mediaItem.genre?.lowercase()
                    )

                    if (detailsList1.isNotEmpty()) {
                        Text(
                            text = detailsList1.joinToString(", "),
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val formattedRuntime = mediaItem.runTime?.let { formatRuntime(it) }

                    val detailsList2 = listOf(
                        mediaItem.productionCountries,
                        formattedRuntime,
                        mediaItem.ageCertification
                    ).filter { !it.isNullOrBlank() }

                    if (detailsList2.isNotEmpty()) {
                        Text(
                            text = detailsList2.joinToString(", "),
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (mediaItem.voteAverage != null || mediaItem.voteCount != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        mediaItem.voteAverage?.let {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Рейтинг",
                                tint = getRatingColor(it),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Text(
                                text = String.format(Locale.US, "%.1f", it),
                                color = getRatingColor(it),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        mediaItem.voteCount?.let {
                            if (mediaItem.voteAverage != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Text(
                                text = "(${formatVoteCount(it)})",
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

//        Image(
//            painter = painterResource(id = R.drawable.tmdb_logo),
//            contentDescription = "TMDB",
//            modifier = Modifier.height(12.dp)
//        )
                        Text(
                            text = "TMDB",
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Кнопка "Оценить"
                    Column(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) { /* TODO: логика оценки */ },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.StarOutline,
                            contentDescription = "Оценить",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Оценить",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }


                    // Кнопка "Избранное"
                    Column(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ){ onToggleFavorite() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Добавить в избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "В избранное",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }


                    // Кнопка "В группу"
                    Column(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                        ) { onToggleGroupFavorite() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isGroupFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Добавить в группу",
                            tint = if (isGroupFavorite) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "В группу",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }

                    // Кнопка "Просмотрен"
                    Column(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ){ /* TODO: логика просмотрен */ },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RemoveRedEye,
                            contentDescription = "Просмотрен",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Просмотрен",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    var isExpanded by remember { mutableStateOf(false) }
                    ExpandableText(
                        text = mediaItem.overView ?: "Описание не доступно",
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.White,
                        ),
                        isExpanded = isExpanded,
                        onToggleExpand = { isExpanded = !isExpanded }
                    )

                    if (!isExpanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Black)
                                    )
                                )
                        )
                    }
                }

                val castWithPhoto = mediaItem.castMember
                    ?.filter { it.profile_path != null }
                    ?: emptyList()
                if (castWithPhoto.isNotEmpty()) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp,start = 16.dp)
                    ) {
                        Text(
                            text = "Актеры",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(castWithPhoto) {
                                Column(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(190.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Box {
                                        AsyncImage(
                                            model = "https://image.tmdb.org/t/p/w185${it.profile_path}",
                                            contentDescription = it.name,
                                            modifier = Modifier
                                                .width(100.dp)
                                                .height(150.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )

                                        Text(
                                            text = it.name,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = TextStyle(
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                                color = Color.White,
                                                shadow = Shadow(
                                                    color = Color.Black,
                                                    offset = Offset(4f, 4f),
                                                )
                                            ),
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(start = 4.dp, bottom = 8.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    it.character?.let {
                                        Text(
                                            text = it,
                                            style = TextStyle(
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                val importantJobs = listOf("Director", "Executive Producer", "Co-Executive Producer", "Producer", "Writer", "Screenplay", "Editor","Cinematographer", "Original Music Composer")
                val sortedCrewWithPhoto = mediaItem.crewMember
                    ?.filter { it.profile_path != null }
                    ?.distinctBy { it.name }
                    ?.sortedBy { crew ->
                        val index = importantJobs.indexOf(crew.job)
                        if (index != -1) index else Int.MAX_VALUE
                    }
                    ?: emptyList()

                if (sortedCrewWithPhoto.isNotEmpty()) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp,start = 16.dp)
                    ) {
                        Text(
                            text = "Съёмочная группа",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(sortedCrewWithPhoto) {
                                Column(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(190.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Box {

                                        AsyncImage(
                                            model = "https://image.tmdb.org/t/p/w185${it.profile_path}",
                                            contentDescription = it.name,
                                            modifier = Modifier
                                                .width(100.dp)
                                                .height(150.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )

                                        Text(
                                            text = it.name,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            style = TextStyle(
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                                color = Color.White,
                                                shadow = Shadow(
                                                    color = Color.Black,
                                                    offset = Offset(4f, 4f),
                                                )
                                            ),
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(start = 4.dp, bottom = 8.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    it.job?.let {
                                        Text(
                                            text = it,
                                            style = TextStyle(
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (!mediaItem.backdropsList.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 40.dp)
                    ) {

                        Text(
                            text = "Изображения",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Log.d("BACKDROPS", "Backdrops size: ${mediaItem.backdropsList?.size}")

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val image = mediaItem.backdropsList ?: emptyList()
                            items(image) {
                                AsyncImage(
                                    model = "https://image.tmdb.org/t/p/original${it.file_path}",
                                    contentDescription = "Фоновые изображения",
                                    modifier = Modifier
                                        .width(300.dp)
                                        .height(169.dp),
                                    contentScale = ContentScale.FillBounds
                                    // .clip(RoundedCornerShape(10.dp))
                                )
                            }
                        }
                    }
                }

                val recommendationsWithPosters = mediaItem.recommendationsList
                    ?.filter { it.poster_path != null }
                    .orEmpty()

                if (recommendationsWithPosters.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, start = 16.dp)
                    ) {
                        Text(
                            text = "Редомендации",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recommendationsWithPosters) { item ->
                                Column(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .clickable {

                                        }
                                ) {
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w342${item.poster_path}",
                                        contentDescription = item.name ?: item.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(150.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(color = AlphaBlack, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Назад",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 4,
    textStyle: TextStyle = TextStyle.Default,
    collapsedTextColor: Color = Color.White,
    expandedTextColor: Color = Color.White,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Text(
        text = text,
        maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
        color = if (isExpanded) expandedTextColor else collapsedTextColor,
        style = textStyle,
        modifier = modifier
            .animateContentSize()
            .clickable(
                interactionSource = null,
                indication = null
            ) { onToggleExpand() }
    )
}


