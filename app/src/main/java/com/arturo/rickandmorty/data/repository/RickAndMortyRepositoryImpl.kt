package com.arturo.rickandmorty.data.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arturo.rickandmorty.data.dto.CharacterResponse
import com.arturo.rickandmorty.data.dto.EpisodeResponse
import com.arturo.rickandmorty.data.dto.RickAndMortyResponse
import com.arturo.rickandmorty.data.network.RickAndMortyApi
import com.arturo.rickandmorty.data.paging.CharactersPagingSource
import com.arturo.rickandmorty.data.paging.LocationsPagingSource
import com.arturo.rickandmorty.domain.mapper.CharacterMapper
import com.arturo.rickandmorty.domain.mapper.LocationMapper
import com.arturo.rickandmorty.domain.model.Character
import com.arturo.rickandmorty.domain.model.Episode
import com.arturo.rickandmorty.domain.model.Location
import com.arturo.rickandmorty.util.NetworkResponse
import com.arturo.rickandmorty.util.SafeApiCall.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class RickAndMortyRepositoryImpl @Inject constructor(
    private val rickAndMortyApi: RickAndMortyApi,
    private val characterMapper: CharacterMapper,
    private val locationMapper: LocationMapper
) : RickAndMortyRepository {

    override fun getCharactersPaging(): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CharactersPagingSource(
                    api = rickAndMortyApi,
                    characterIds = null
                )
            }
        ).flow
    }

    override fun getLocations(): Flow<PagingData<Location>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                LocationsPagingSource(
                    api = rickAndMortyApi,
                    mapper = locationMapper
                )
            }
        ).flow
    }

    override fun getCharactersByLocation(characterIds: List<String>): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = characterIds.size.coerceAtLeast(1),
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CharactersPagingSource(
                    api = rickAndMortyApi,
                    characterIds = characterIds
                )
            }
        ).flow
    }

    override fun searchCharacters(name: String): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CharactersPagingSource(
                    api = rickAndMortyApi,
                    characterIds = null,
                    nameQuery = name
                )
            }
        ).flow
    }

    override fun getCharacter(id: String): Flow<NetworkResponse<Character>> {
        return flow {
            emit(NetworkResponse.Loading)

            when (val result = safeApiCall { rickAndMortyApi.getCharacter(id) }) {
                is NetworkResponse.Error -> emit(NetworkResponse.Error(result.errorMessage))
                is NetworkResponse.Success -> {
                    emit(NetworkResponse.Success(characterMapper.mapToDomainModel(result.data)))
                }

                else -> Unit
            }
        }
    }

    override fun getCharactersWithFilters(
        status: String?,
        species: String?
    ): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                CharactersPagingSource(
                    api = rickAndMortyApi,
                    status = status,
                    species = species
                )
            }
        ).flow
    }

    override fun getAllCharacters(): Flow<NetworkResponse<List<Character>>> {
        return flow {
            emit(NetworkResponse.Loading)

            val allCharacters = mutableListOf<Character>()
            var currentPage = 1
            var hasMorePages = true

            while (hasMorePages) {
                when (val result = safeApiCall { rickAndMortyApi.getCharacters(currentPage) }) {
                    is NetworkResponse.Success -> {
                        result.data?.results?.let { characters ->
                            allCharacters.addAll(characters.map { characterMapper.mapToDomainModel(it) })
                            hasMorePages = characters.isNotEmpty() &&
                                    (currentPage * DEFAULT_PAGE_SIZE) < (result.data.info?.count ?: 0)
                            currentPage++
                        } ?: run { hasMorePages = false }
                    }
                    is NetworkResponse.Error -> {
                        emit(NetworkResponse.Error(result.errorMessage))
                        return@flow
                    }
                    else -> Unit
                }
            }

            emit(NetworkResponse.Success(allCharacters))
        }

    }

    override fun getEpisodes(episodeIds: List<String>): Flow<NetworkResponse<List<Episode>>> = flow {
        try {
            emit(NetworkResponse.Loading)

            // Validación para evitar cargar demasiados episodios a la vez
            val idsToLoad = if (episodeIds.size > 20) {
                println("Advertencia: Demasiados episodios solicitados. Limitando a 20")
                episodeIds.take(20)
            } else {
                episodeIds
            }

            val idsString = idsToLoad.joinToString(",")
            val response = rickAndMortyApi.getEpisodes(idsString)

            if (response.isSuccessful) {
                val episodes = response.body()?.map { episodeResponse ->
                    mapToEpisode(episodeResponse)
                } ?: emptyList()

                emit(NetworkResponse.Success(episodes))
            } else {
                val errorMsg = "Error ${response.code()}: ${response.errorBody()?.string()}"
                emit(NetworkResponse.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Error("Excepción: ${e.localizedMessage}"))
        }
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

    private fun mapToEpisode(response: EpisodeResponse): Episode {
        return Episode(
            id = response.id.toString(),  // Convertimos Int a String
            name = response.name,
            airDate = response.airDate,
            episodeCode = response.episode,
            characterIds = response.characters.map { url ->
                url.substringAfterLast("/")
            }
        )
    }
}