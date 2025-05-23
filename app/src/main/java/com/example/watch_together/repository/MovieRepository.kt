package com.example.watch_together.repository

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.example.watch_together.auth.UserPreferences
import com.example.watch_together.group.Group
import com.example.watch_together.group.MediaAddedBy
import com.example.watch_together.models.CastMember
import com.example.watch_together.models.CombinedCreditsResponse
import com.example.watch_together.models.CrewMember
import com.example.watch_together.models.FavoriteMovieDao
import com.example.watch_together.models.FavoriteMovieEntity
import com.example.watch_together.models.MediaItem
import com.example.watch_together.models.MediaType
import com.example.watch_together.models.Movie
import com.example.watch_together.models.PersonDetailsResponse
import com.example.watch_together.models.Series
import com.example.watch_together.movieApiService.MovieApiService
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.iosParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.shortLinkAsync
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: MovieApiService,
    private val favoriteMovieDao: FavoriteMovieDao,
    private val userPreferences: UserPreferences
) {

    private val database = FirebaseDatabase.getInstance().reference

    fun getCurrentUserId(): String {
        val userId = userPreferences.requireUserId()
        Log.d("MovieRepository", "📦 Получен userId из UserPreferences: $userId")
        return userId
    }

    // --- API ---

    suspend fun getPersonDetails(personId: Int): PersonDetailsResponse {
        return apiService.getPersonById(personId)
    }

    suspend fun getPersonCredits(personId: Int): CombinedCreditsResponse {
        return apiService.getPersonCombinedCredits(personId)
    }

    private suspend fun getCastAndCrew(mediaId: Int, type: MediaType): Pair<List<CastMember>, List<CrewMember>> {
        return try {
            val response = when (type) {
                MediaType.MOVIE -> apiService.getMovieCredits(mediaId)
                MediaType.TV -> apiService.getTvCredits(mediaId)
            }
            response.cast.orEmpty() to response.crew.orEmpty()
        } catch (e: Exception) {
            emptyList<CastMember>() to emptyList()
        }
    }

    private suspend fun fetchMovieWithExtras(movieId: Int): Movie {
        val movie = apiService.getMovieById(movieId)
        val certification = try {
            val releaseDatesResponse = apiService.getMovieReleaseDates(movieId)
            releaseDatesResponse.results
                .firstOrNull { it.iso_3166_1 == "RU" }
                ?.release_dates
                ?.firstOrNull()
                ?.certification
        } catch (e: Exception) {
            null
        }
        val (cast, crew) = getCastAndCrew(movieId, MediaType.MOVIE)
        val (backdrops) = apiService.getMovieImages(movieId)
//        val (similar) = apiService.getSimilarMovies(movieId)
        val recommendationsResponse = apiService.getMovieRecommendations(movieId)
        val recommendations = recommendationsResponse.results
        return movie.copy(certification = certification, cast = cast, crew = crew, backdrops = backdrops, recommendations = recommendations)
    }

    private suspend fun fetchSeriesWithExtras(seriesId: Int): Series {
        val series = apiService.getTvShowsById(seriesId)
        val rating = try {
            val contentRatings = apiService.getTvContentRatings(seriesId)
            contentRatings.results
                .firstOrNull { it.iso_3166_1 == "RU" }
                ?.rating
        } catch (e: Exception) {
            null
        }
        val (cast, crew) = getCastAndCrew(seriesId, MediaType.TV)
        val (backdrops) = apiService.getTvImages(seriesId)
//        val (results) = apiService.getSimilarTvShows(seriesId)
        val recommendationsResponse = apiService.getTvRecommendations(seriesId)
        val recommendations = recommendationsResponse.results
        return series.copy(rating = rating, cast = cast, crew = crew, backdrops = backdrops, recommendations = recommendations)
    }

    suspend fun getMediaItems(type: MediaType, category: String): List<MediaItem> {
        return when (type) {
            MediaType.MOVIE -> when (category) {
                "popular" -> apiService.getPopularMovies().results
                "top_rated" -> apiService.getTopRatedMovies().results
                "now_playing" -> apiService.getNowPlayingMovies().results
                "upcoming" -> apiService.getUpcomingMovies().results
                "trending" -> apiService.getTrendingMovies().results
                else -> emptyList()
            }
            MediaType.TV -> when (category) {
                "popular" -> apiService.getPopularTvShows().results
                "top_rated" -> apiService.getTopRatedTvShows().results
                "on_the_air" -> apiService.getOnTheAirTvShows().results
                "trending" -> apiService.getTrendingTvShows().results
                else -> emptyList()
            }
        }
    }

    suspend fun getMediaDetailsById(id: Int, type: MediaType): MediaItem {
        return when (type) {
            MediaType.MOVIE -> fetchMovieWithExtras(id)
            MediaType.TV -> fetchSeriesWithExtras(id)
        }
    }

    suspend fun getMediaById(id: Int, type: MediaType): MediaItem {
        return when (type) {
            MediaType.MOVIE -> apiService.getMovieById(id)
            MediaType.TV -> apiService.getMovieById(id)
        }
    }

    suspend fun searchMedia(query: String, type: MediaType): List<MediaItem> = coroutineScope {
        val baseResults = when (type) {
            MediaType.MOVIE -> apiService.searchMovies(query).results
            MediaType.TV -> apiService.searchTvShows(query).results
        }
        baseResults.map { item ->
            async {
                when (type) {
                    MediaType.MOVIE -> fetchMovieWithExtras(item.itemId)
                    MediaType.TV -> fetchSeriesWithExtras(item.itemId)
                }
            }
        }.awaitAll()
    }

    // --- Firebase ---

    suspend fun addMediaToGroupFavorites(groupId: String, mediaId: String, mediaType: MediaType) {
        if (groupId.isBlank()) {
            Log.e("MovieRepository", "groupId пуст при добавлении медиа!")
            return
        }
        val addedBy = getCurrentUserId()
        val key = "${mediaType.name.lowercase()}_$mediaId"
        val mediaData = mapOf(
            "addedBy" to addedBy,
            "mediaType" to mediaType.name.lowercase()
        )

        withContext(Dispatchers.IO) {
            try {
                database.child("groups")
                    .child(groupId)
                    .child("groupMedia")
                    .child(key)
                    .setValue(mediaData)
                    .await()
                Log.d("MovieRepository", "Медиа $key добавлено в группу $groupId")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Ошибка при добавлении медиа: ${e.message}")
            }
        }
    }

    suspend fun removeMediaFromGroupFavorites(groupId: String, mediaId: String, mediaType: MediaType) {
        val key = "${mediaType.name.lowercase()}_$mediaId"
        withContext(Dispatchers.IO) {
            try {
                database.child("groups")
                    .child(groupId)
                    .child("groupMedia")
                    .child(key)
                    .removeValue()
                    .await()
                Log.d("MovieRepository", "Медиа $key удалено из группы $groupId")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Ошибка при удалении медиа: ${e.message}")
            }
        }
    }

    suspend fun createGroup(groupName: String): String {
        val userId = getCurrentUserId()
        val userName = userPreferences.requireUserName()

        val groupId = database.child("groups").push().key ?: throw Exception("Не удалось создать группу")

        Log.d("MovieRepository", "➡️ Создание группы: $groupName (ID: $groupId), создатель: $userId")

        val groupData = mapOf(
            "groupName" to groupName,
            "createdBy" to userId,
            "groupMembers" to mapOf(userId to true),
            "groupMedia" to emptyMap<String, Map<String, String>>() // Инициализация пустого поля groupMedia
        )

        try {
            database.child("groups").child(groupId).setValue(groupData).await()
            Log.d("MovieRepository", "✅ Группа записана в /groups/$groupId")

            database.child("users").child(userId).child("groups").child(groupId).setValue(true).await()
            Log.d("MovieRepository", "✅ Связь user→group записана в /users/$userId/groups/$groupId")

            database.child("users").child(userId).child("userName").setValue(userName).await()
            Log.d("MovieRepository", "📛 UserName '$userName' записан в /users/$userId/userName")

        } catch (e: Exception) {
            Log.e("MovieRepository", "❌ Ошибка при записи: ${e.message}", e)
        }

        return groupId
    }

    suspend fun getUsernameByUserId(userId: String): String? {
        return try {
            val snapshot = database.child("users").child(userId).child("userName").get().await()
            snapshot.getValue(String::class.java)
        } catch (e: Exception) {
            Log.e("MovieRepository", "Ошибка при получении username: ${e.message}")
            null
        }
    }

    private suspend fun fetchUserGroupsFromFirebase(userId: String): List<String> {
        val snapshot = database.child("users").child(userId).child("groups").get().await()
        return snapshot.children.mapNotNull { it.key }.toList()
    }

    suspend fun getUserGroupsWithMedia(): List<Group> {
        val userId = getCurrentUserId()
        val groupIds = fetchUserGroupsFromFirebase(userId)
        return getGroupsInfoWithMedia(groupIds)
    }

    private suspend fun getGroupsInfoWithMedia(groupIds: List<String>): List<Group> {
        return groupIds.mapNotNull outer@{ groupId ->
            val groupSnapshot = database.child("groups").child(groupId).get().await()

            val groupName = groupSnapshot.child("groupName").getValue(String::class.java) ?: return@outer null
            val createdBy = groupSnapshot.child("createdBy").getValue(String::class.java) ?: return@outer null
            val groupMembers = groupSnapshot.child("groupMembers").getValue<Map<String, Boolean>>() ?: emptyMap()

            val groupMediaMap: Map<String, MediaAddedBy> = groupSnapshot.child("groupMedia").children.mapNotNull { mediaSnapshot ->
                val key = mediaSnapshot.key ?: return@mapNotNull null
                val addedBy = mediaSnapshot.child("addedBy").getValue(String::class.java) ?: return@mapNotNull null
                val mediaType = mediaSnapshot.child("mediaType").getValue(String::class.java) ?: return@mapNotNull null
                key to MediaAddedBy(addedBy, mediaType)
            }.toMap()

            Group(
                groupId = groupId,
                groupName = groupName,
                createdBy = createdBy,
                groupMembers = groupMembers,
                groupMedia = groupMediaMap
            )
        }
    }

    @SuppressLint("UseKtx")
    @Suppress("DEPRECATION")
    suspend fun createInviteLink(groupId: String): Uri {
        val link = "https://watchtogether/invite?groupId=$groupId".toUri()

        val shortDynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(link)
            .setDomainUriPrefix("https://watchtogether1.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setMinimumVersion(1)
                    .build()
            )
            .setIosParameters(
                DynamicLink.IosParameters.Builder("com.your.bundle.id")
                    .setAppStoreId("123456789")
                    .build()
            )
            .buildShortDynamicLink()
            .await()

        return shortDynamicLink.shortLink ?: link
    }


    suspend fun inviteMemberToGroup(groupId: String, userIdToInvite: String) {
        database.child("groups")
            .child(groupId)
            .child("groupMembers")
            .child(userIdToInvite)
            .setValue(true)
            .await()

        database.child("users")
            .child(userIdToInvite)
            .child("groups")
            .child(groupId)
            .setValue(true)
            .await()
    }

    suspend fun removeMemberFromGroup(groupId: String, userId: String) {
        database.child("groups")
            .child(groupId)
            .child("groupMembers")
            .child(userId)
            .removeValue()
            .await()

        database.child("users")
            .child(userId)
            .child("groups")
            .child(groupId)
            .removeValue()
            .await()
    }

    suspend fun deleteGroup(groupId: String) {
        val groupSnapshot = database.child("groups").child(groupId).get().await()
        val groupMembers = groupSnapshot.child("groupMembers")
            .getValue<Map<String, Boolean>>() ?: emptyMap()

        groupMembers.keys.forEach { userId ->
            database.child("users")
                .child(userId)
                .child("groups")
                .child(groupId)
                .removeValue()
                .await()
        }

        database.child("groups").child(groupId).removeValue().await()
    }

    // --- Room & Local ---

    fun setHasNewFavorites(value: Boolean) {
        Log.d("MovieRepository", "setHasNewFavorites: $value")
        userPreferences.setHasNewFavorites(value)
    }

    fun getHasNewFavorites(): Boolean {
        val value = userPreferences.getHasNewFavorites()
        Log.d("MovieRepository", "getHasNewFavorites: $value")
        return value
    }

    suspend fun addToFavorites(mediaId: Int, type: MediaType) {
        favoriteMovieDao.addFavorite(
            FavoriteMovieEntity(
                mediaId = mediaId,
                mediaType = type.name.lowercase()
            )
        )
    }

    @SuppressLint("ImplicitSamInstance")
    suspend fun removeFromFavorites(mediaId: Int, type: MediaType) {
        favoriteMovieDao.removeFavorite(
            FavoriteMovieEntity(
                mediaId = mediaId,
                mediaType = type.name.lowercase()
            )
        )
    }

    // in-memory cache: Pair<id, type> → MediaItem
    private val cache = mutableMapOf<Pair<Int, MediaType>, MediaItem>()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllFavorites(): Flow<List<MediaItem>> =
        favoriteMovieDao.getAllFavorites()
            .map { entities ->
                entities.map { it.mediaId to MediaType.valueOf(it.mediaType.uppercase()) }
            }
            .distinctUntilChanged()
            .mapLatest { idTypePairs ->
                coroutineScope {
                    val deferredList = idTypePairs.map { (id, type) ->
                        cache[id to type]?.let {
                            async(start = CoroutineStart.LAZY) { it }
                        } ?: async(Dispatchers.IO) {
                            getMediaById(id, type).also { media ->
                                cache[id to type] = media
                            }
                        }
                    }
                    deferredList.awaitAll()
                }
            }
            .flowOn(Dispatchers.IO)

}
