package com.arturo.rickandmorty.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arturo.rickandmorty.data.network.RickAndMortyApi
import com.arturo.rickandmorty.domain.model.Location
import retrofit2.HttpException
import java.io.IOException

class LocationsPagingSource(
    private val api: RickAndMortyApi,
    private val mapper: com.arturo.rickandmorty.domain.mapper.LocationMapper
) : PagingSource<Int, Location>() {

    override fun getRefreshKey(state: PagingState<Int, Location>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Location> {
        return try {
            val page = params.key ?: 1
            val response = api.getLocations(page)
            val locations = response.body()?.results?.map { mapper.mapToDomainModel(it) } ?: emptyList()
            val nextKey = if (response.body()?.info?.next == null) null else page + 1

            LoadResult.Page(
                data = locations,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}