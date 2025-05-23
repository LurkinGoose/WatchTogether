package com.example.watch_together.models

import java.util.Locale

interface MediaItem {
    val titleText: String
    val originalTitle: String?
    val posterPath: String?
    val backdropPath: String?
    val itemId: Int
    val overView: String?
    val voteAverage: Double?
    val voteCount: Int?
    val ageCertification: String?
    val releaseDate: String?
    val productionCountries: String?
    val productionCompanies: String?
    val genre: String?
    val runTime: Int?
    val castMember: List<CastMember>?
    val crewMember: List<CrewMember>?
    val backdropsList: List<ImageData>?
    val recommendationsList: List<RecommendationItem>?

    val mediaType: MediaType

    val fullBackdropPath: String?
        get() = backdropPath?.let { "https://image.tmdb.org/t/p/original$it" }

    val fullPosterPath: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w185$it" }

    val fullLogoPath: List<String>?
        get() = null
}

data class CreditsResponse(
    val id: Int,
    val cast: List<CastMember>?,
    val crew: List<CrewMember>?
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String?,
    val profile_path: String?
)

data class CrewMember(
    val id: Int,
    val name: String,
    val job: String?,
    val profile_path: String?
)

data class Genre(
    val id: Int,
    val name: String
)

data class ProductionCountry(
    val iso_3166_1: String,
    val name: String
)

data class ProductionCompanies(
    val id: Int,
    val logo_path: String?,
    val name: String,
    val origin_country: String
)

data class ImagesResponse(
    val backdrops: List<ImageData>
)

data class ImageData(
    val file_path: String,
    val width: Int,
    val height: Int,
    val vote_average: Float
)

data class RecommendationResponse(
    val page: Int,
    val results: List<RecommendationItem>,
    val total_pages: Int,
    val total_results: Int
)

data class RecommendationItem(
    val id: Int,
    val title: String?,
    val name: String?,
    val poster_path: String?
)

// Функция для получения локализованного названия страны
private fun getLocalizedCountryName(isoCode: String, locale: Locale = Locale("ru")): String {
    return if (isoCode == "US") {
        "США"
    } else {
        Locale("", isoCode).getDisplayCountry(locale)
    }
}


// Персоны
data class PersonDetailsResponse(
    val id: Int,
    val name: String,
    val biography: String?,
    val birthday: String?,
    val deathday: String?,
    val place_of_birth: String?,
    val profile_path: String?,
    val known_for_department: String?,
    val also_known_as: List<String>?,
    val imdb_id: String?
)

data class CombinedCreditsResponse(
    val cast: List<PersonMediaCredit>,
    val crew: List<PersonMediaCredit>
)

data class PersonMediaCredit(
    val id: Int,
    val media_type: String, // "movie" или "tv"
    val title: String?,     // для фильмов
    val name: String?,      // для сериалов
    val character: String?, // если актёр
    val job: String?,       // если член съёмочной группы
    val poster_path: String?,
    val release_date: String?,
    val first_air_date: String?
)

// Фильмы
data class MovieList(val results: List<Movie>)
data class Movie(
    val title: String,
    val original_title: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val id: Int,
    val overview: String,
    val production_countries: List<ProductionCountry>?,
    val production_companies: List<ProductionCompanies>?,
    val release_date: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    val runtime: Int?,
    val genres: List<Genre>?,
    val adult: Boolean?,
    val tagline: String?,
    val certification: String?,
    val cast: List<CastMember>? = null,
    val crew: List<CrewMember>? = null,
    val backdrops: List<ImageData>? = null,
    val recommendations: List<RecommendationItem>? = null
) : MediaItem {
    override val titleText: String get() = title
    override val originalTitle: String?
        get() = original_title?.takeIf { it != title }
    override val posterPath: String? get() = poster_path
    override val backdropPath: String? get() = backdrop_path
    override val itemId: Int get() = id
    override val overView: String get() = overview
    override val productionCountries: String? get() = production_countries?.joinToString(", ") { getLocalizedCountryName(it.iso_3166_1) }
    override val productionCompanies: String? get() = production_companies?.joinToString(", ") { it.name }

    override val fullLogoPath: List<String>?
        get() = production_companies
            ?.mapNotNull { it.logo_path }
            ?.map { "https://image.tmdb.org/t/p/original$it" }
            ?.takeIf { it.isNotEmpty() }

    override val voteAverage: Double? get() = vote_average?.takeIf { it > 0.0 }
    override val voteCount: Int? get() = vote_count?.takeIf { it > 1000 }
    override val runTime: Int? get() = runtime?.takeIf { it > 0 }
    override val genre: String? get() = genres?.joinToString(", ") { it.name }
    override val ageCertification: String? get() = certification?.takeIf { it.isNotBlank() }
    override val releaseDate: String? get() = release_date?.takeIf { it.isNotBlank() }
    override val castMember: List<CastMember>? get() = cast
    override val crewMember: List<CrewMember>? get() = crew
    override val backdropsList: List<ImageData>? get() = backdrops?.take(10)
    override val recommendationsList: List<RecommendationItem>? get() = recommendations

    override val mediaType: MediaType get() = MediaType.MOVIE
}

data class SeriesList(val results: List<Series>)

data class Series(
    val name: String,
    val original_name: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val id: Int,
    val overview: String,
    val production_countries: List<ProductionCountry>?,
    val production_companies: List<ProductionCompanies>?,
    val first_air_date: String?,
    val last_air_date: String?,
    val number_of_seasons: Int?,
    val vote_average: Double?,
    val vote_count: Int?,
    val genres: List<Genre>?,
    val tagline: String?,
    val rating: String?,
    val cast: List<CastMember>? = null,
    val crew: List<CrewMember>? = null,
    val backdrops: List<ImageData>? = null,
    val recommendations: List<RecommendationItem>? = null
) : MediaItem {
    override val titleText: String get() = name
    override val originalTitle: String?
        get() = original_name?.takeIf { it != name }
    override val posterPath: String? get() = poster_path
    override val backdropPath: String? get() = backdrop_path
    override val itemId: Int get() = id
    override val overView: String get() = overview
    override val productionCountries: String? get() = production_countries?.joinToString(", ") { getLocalizedCountryName(it.iso_3166_1) }
    override val productionCompanies: String? get() = production_companies?.joinToString(", ") { it.name }

    override val fullLogoPath: List<String>?
        get() = production_companies
            ?.mapNotNull { it.logo_path }
            ?.map { "https://image.tmdb.org/t/p/original$it" }
            ?.takeIf { it.isNotEmpty() }

    override val voteAverage: Double? get() = vote_average?.takeIf { it > 0.0 }
    override val voteCount: Int? get() = vote_count?.takeIf { it > 1000 }
    override val genre: String? get() = genres?.joinToString(", ") { it.name }
    override val runTime: Int? get() = null
    override val ageCertification: String? get() = rating?.takeIf { it.isNotBlank() }
    override val releaseDate: String? get() = first_air_date?.takeIf { it.isNotBlank() }
    override val castMember: List<CastMember>? get() = cast
    override val crewMember: List<CrewMember>? get() = crew
    override val backdropsList: List<ImageData>? get() = backdrops?.take(10)
    override val recommendationsList: List<RecommendationItem>? get() = recommendations

    override val mediaType: MediaType get() = MediaType.TV
}

data class ReleaseDatesResponse(
    val results: List<ReleaseDatesResult>
)

data class ReleaseDatesResult(
    val iso_3166_1: String,
    val release_dates: List<ReleaseDate>
)

data class ReleaseDate(
    val certification: String,
    val iso_639_1: String?,
    val note: String?,
    val release_date: String
)

data class ContentRatingsResponse(
    val results: List<ContentRating>
)

data class ContentRating(
    val iso_3166_1: String,
    val rating: String
)
