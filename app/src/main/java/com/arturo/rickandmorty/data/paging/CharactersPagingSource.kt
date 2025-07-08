package com.arturo.rickandmorty.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arturo.rickandmorty.data.dto.toCharacter
import com.arturo.rickandmorty.data.network.RickAndMortyApi
import com.arturo.rickandmorty.domain.model.Character
import retrofit2.HttpException
import java.io.IOException

class CharactersPagingSource(
    private val api: RickAndMortyApi,
    private val characterIds: List<String>? = null,
    private val nameQuery: String? = null,
    private val status: String? = null,
    private val species: String? = null
) : PagingSource<Int, Character>() {

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            if (characterIds != null) {
                // Carga por IDs específicos (para filtrado por ubicación)
                if (characterIds.isEmpty()) {
                    return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
                }

                val response = api.getMultipleCharacters(characterIds.joinToString(","))
                val characters = response.body()?.map { it.toCharacter() } ?: emptyList()
                LoadResult.Page(characters, prevKey = null, nextKey = null)
            } else {
                val page = params.key ?: 1
                val response = when {
                    !nameQuery.isNullOrEmpty() -> api.searchCharacters(nameQuery, page)
                    status != null || species != null -> api.getCharacters(
                        page = page,
                        status = status,
                        species = species
                    )
                    else -> api.getCharacters(page)
                }

                val characters = response.body()?.results?.map { it!!.toCharacter() } ?: emptyList()
                val nextKey = if (response.body()?.info?.next == null) null else page + 1

                LoadResult.Page(
                    data = characters,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = nextKey
                )
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}