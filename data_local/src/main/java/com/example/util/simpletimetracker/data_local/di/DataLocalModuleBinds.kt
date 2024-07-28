package com.example.util.simpletimetracker.data_local.di

import com.example.util.simpletimetracker.core.repo.DataEditRepo
import com.example.util.simpletimetracker.data_local.repo.ActivityFilterRepoImpl
import com.example.util.simpletimetracker.data_local.repo.CategoryRepoImpl
import com.example.util.simpletimetracker.data_local.repo.ComplexRuleRepoImpl
import com.example.util.simpletimetracker.data_local.repo.DataEditRepoImpl
import com.example.util.simpletimetracker.data_local.repo.FavouriteCommentRepoImpl
import com.example.util.simpletimetracker.data_local.repo.FavouriteIconRepoImpl
import com.example.util.simpletimetracker.data_local.repo.PrefsRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RecordRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RecordTagRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RecordToRecordTagRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RecordTypeCategoryRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RecordTypeGoalRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RecordTypeRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RecordTypeToDefaultTagRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RecordTypeToTagRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RunningRecordRepoImpl
import com.example.util.simpletimetracker.data_local.repo.RunningRecordToRecordTagRepoImpl
import com.example.util.simpletimetracker.data_local.resolver.BackupRepoImpl
import com.example.util.simpletimetracker.data_local.resolver.CsvRepoImpl
import com.example.util.simpletimetracker.data_local.resolver.IcsRepoImpl
import com.example.util.simpletimetracker.data_local.resolver.SharingRepoImpl
import com.example.util.simpletimetracker.domain.repo.ActivityFilterRepo
import com.example.util.simpletimetracker.domain.repo.CategoryRepo
import com.example.util.simpletimetracker.domain.repo.ComplexRuleRepo
import com.example.util.simpletimetracker.domain.repo.FavouriteCommentRepo
import com.example.util.simpletimetracker.domain.repo.FavouriteIconRepo
import com.example.util.simpletimetracker.domain.repo.PrefsRepo
import com.example.util.simpletimetracker.domain.repo.RecordRepo
import com.example.util.simpletimetracker.domain.repo.RecordTagRepo
import com.example.util.simpletimetracker.domain.repo.RecordToRecordTagRepo
import com.example.util.simpletimetracker.domain.repo.RecordTypeCategoryRepo
import com.example.util.simpletimetracker.domain.repo.RecordTypeGoalRepo
import com.example.util.simpletimetracker.domain.repo.RecordTypeRepo
import com.example.util.simpletimetracker.domain.repo.RecordTypeToDefaultTagRepo
import com.example.util.simpletimetracker.domain.repo.RecordTypeToTagRepo
import com.example.util.simpletimetracker.domain.repo.RunningRecordRepo
import com.example.util.simpletimetracker.domain.repo.RunningRecordToRecordTagRepo
import com.example.util.simpletimetracker.domain.resolver.BackupRepo
import com.example.util.simpletimetracker.domain.resolver.CsvRepo
import com.example.util.simpletimetracker.domain.resolver.IcsRepo
import com.example.util.simpletimetracker.domain.resolver.SharingRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataLocalModuleBinds {

    @Binds
    @Singleton
    fun RecordRepoImpl.bindRecordRepo(): RecordRepo

    @Binds
    @Singleton
    fun RecordTypeRepoImpl.bindRecordTypeRepo(): RecordTypeRepo

    @Binds
    @Singleton
    fun RunningRecordRepoImpl.bindRunningRecordRepo(): RunningRecordRepo

    @Binds
    @Singleton
    fun PrefsRepoImpl.bindPrefsRepo(): PrefsRepo

    @Binds
    @Singleton
    fun BackupRepoImpl.bindBackupRepo(): BackupRepo

    @Binds
    @Singleton
    fun CsvRepoImpl.bindCsvRepo(): CsvRepo

    @Binds
    @Singleton
    fun IcsRepoImpl.bindIcsRepo(): IcsRepo

    @Binds
    @Singleton
    fun SharingRepoImpl.bindSharingRepo(): SharingRepo

    @Binds
    @Singleton
    fun CategoryRepoImpl.bindCategoryRepo(): CategoryRepo

    @Binds
    @Singleton
    fun RecordTagRepoImpl.bindRecordTagRepo(): RecordTagRepo

    @Binds
    @Singleton
    fun RecordTypeCategoryRepoImpl.bindRecordTypeCategoryRepo(): RecordTypeCategoryRepo

    @Binds
    @Singleton
    fun RecordTypeToTagRepoImpl.bindRecordTypeToTagRepo(): RecordTypeToTagRepo

    @Binds
    @Singleton
    fun RecordTypeToDefaultTagRepoImpl.bindRecordTypeToDefaultTagRepo(): RecordTypeToDefaultTagRepo

    @Binds
    @Singleton
    fun RecordToRecordTagRepoImpl.bindRecordToRecordTagRepo(): RecordToRecordTagRepo

    @Binds
    @Singleton
    fun RunningRecordToRecordTagRepoImpl.bindRunningRecordToRecordTagRepo(): RunningRecordToRecordTagRepo

    @Binds
    @Singleton
    fun ActivityFilterRepoImpl.bindActivityFilterRepo(): ActivityFilterRepo

    @Binds
    @Singleton
    fun FavouriteCommentRepoImpl.bindFavouriteCommentRepo(): FavouriteCommentRepo

    @Binds
    @Singleton
    fun RecordTypeGoalRepoImpl.bindRecordTypeGoalRepo(): RecordTypeGoalRepo

    @Binds
    @Singleton
    fun DataEditRepoImpl.bindDataEditRepo(): DataEditRepo

    @Binds
    @Singleton
    fun FavouriteIconRepoImpl.bindFavouriteIconRepo(): FavouriteIconRepo

    @Binds
    @Singleton
    fun ComplexRuleRepoImpl.bindComplexRuleRepo(): ComplexRuleRepo
}