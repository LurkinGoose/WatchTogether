package com.example.watch_together.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.watch_together.group.GroupViewModel
import com.example.watch_together.movieCards.MediaCategorySection
import com.example.watch_together.viewModels.MainViewModel


@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    groupViewModel: GroupViewModel,
) {
    val mainState by mainViewModel.mainState.collectAsState()
    val groupState by groupViewModel.groupState.collectAsState()

    val homeColumnState = mainViewModel.homeColumnState

    val snackbarHostState = remember { SnackbarHostState() }

    // Обработка ошибок
    LaunchedEffect(mainState.error) {
        mainState.error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            mainViewModel.clearError()
        }
    }

    LaunchedEffect(groupState.error) {
        groupState.error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            groupViewModel.clearError()
        }
    }

    // Автоскролл к началу при необходимости
    LaunchedEffect(mainState.scrollToTop) {
        if (mainState.scrollToTop) {
            homeColumnState.animateScrollToItem(0)
            mainViewModel.clearScrollToTop()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (mainState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {

                LazyColumn(
                    state = homeColumnState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {

                    if (mainState.movies.topRated.isNotEmpty()) {
                        item {
                            MediaCategorySection(
                                title = "Топ рейтинга TMDB (Фильмы)",
                                items = mainState.movies.topRated,
                                rowState = mainViewModel.moviesTopRatedState,
                                navController = navController
                            )
                        }
                    }

                    if (mainState.movies.popular.isNotEmpty()) {
                        item {
                            MediaCategorySection(
                                title = "Популярные фильмы",
                                items = mainState.movies.popular,
                                rowState = mainViewModel.moviesPopularState,
                                navController = navController
                            )
                        }
                    }

                    if (mainState.movies.upcoming.isNotEmpty()) {
                        item {
                            MediaCategorySection(
                                title = "Скоро в прокате",
                                items = mainState.movies.upcoming,
                                rowState = mainViewModel.moviesUpcomingState,
                                navController = navController
                            )
                        }
                    }

                    if (mainState.movies.trending.isNotEmpty()) {
                        item {
                            MediaCategorySection(
                                title = "В тренде (Фильмы)",
                                items = mainState.movies.trending,
                                rowState = mainViewModel.moviesTrendingState,
                                navController = navController
                            )
                        }
                    }

                    if (mainState.series.topRated.isNotEmpty()) {
                        item {
                            MediaCategorySection(
                                title = "Топ рейтинга TMDB (Сериалы)",
                                items = mainState.series.topRated,
                                rowState = mainViewModel.seriesTopRatedState,
                                navController = navController
                            )
                        }
                    }

                    if (mainState.series.popular.isNotEmpty()) {
                        item {
                            MediaCategorySection(
                                title = "Популярные сериалы",
                                items = mainState.series.popular,
                                rowState = mainViewModel.seriesPopularState,
                                navController = navController
                            )
                        }
                    }

                    if (mainState.series.nowPlayingOrOnAir.isNotEmpty()) {
                        item {
                            MediaCategorySection(
                                title = "Сейчас в эфире",
                                items = mainState.series.nowPlayingOrOnAir,
                                rowState = mainViewModel.seriesOnTheAirState,
                                navController = navController
                            )
                        }
                    }

                    if (mainState.series.trending.isNotEmpty()) {
                        item {
                            MediaCategorySection(
                                title = "В тренде (Сериалы)",
                                items = mainState.series.trending,
                                rowState = mainViewModel.seriesTrendingState,
                                navController = navController
                            )
                        }
                    }

                }
            }
        }
    }
}
