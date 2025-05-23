package com.example.watch_together.movieCards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.watch_together.models.MediaItem
import com.example.watch_together.models.Movie
import com.example.watch_together.models.Series
import com.example.watch_together.viewModels.MainViewModel

@Composable
fun MediaCategorySection(
    title: String,
    items: List<MediaItem>,
    rowState: LazyListState,
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.TopStart),
                text = title,
                style = TextStyle(color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )

            Text(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp),
                text = "Все",
                style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
        }

        LazyRow(
            state = rowState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.itemId }) { mediaItem ->
                MediaRowItem(
                    mediaItem = mediaItem,
                    onClick = {
                        when (mediaItem) {
                            is Movie -> {
                                navController.navigate("details/${mediaItem.id}/movie")
                            }
                            is Series -> {
                                navController.navigate("details/${mediaItem.id}/tv")
                            }
                        }
                    }
                )
            }
        }
    }
}
