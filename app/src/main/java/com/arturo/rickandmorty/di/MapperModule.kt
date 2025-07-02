package com.arturo.rickandmorty.di

import com.arturo.rickandmorty.data.dto.CharacterResponse
import com.arturo.rickandmorty.data.dto.LocationResponse
import com.arturo.rickandmorty.domain.mapper.CharacterMapper
import com.arturo.rickandmorty.domain.mapper.DomainMapper
import com.arturo.rickandmorty.domain.mapper.LocationMapper
import com.arturo.rickandmorty.domain.model.Character
import com.arturo.rickandmorty.domain.model.Location
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class MapperModule {
    @Binds
    @ViewModelScoped
    abstract fun bindCharacterMapper(characterMapper: CharacterMapper): DomainMapper<CharacterResponse, Character>

    @Binds
    @ViewModelScoped
    abstract fun bindLocationMapper(locationMapper: LocationMapper): DomainMapper<LocationResponse, Location>
}