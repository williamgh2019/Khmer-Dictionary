package com.sovathna.khmerdictionary.data

import com.sovathna.khmerdictionary.data.interactor.WordListInteractorImpl
import com.sovathna.khmerdictionary.domain.interactor.WordListInteractor
import dagger.Binds
import dagger.Module

@Module
abstract class InteractorModule {

  @Binds
  abstract fun wordListInteractor(impl: WordListInteractorImpl): WordListInteractor

}