package com.doctoror.splittor.di

import androidx.lifecycle.SavedStateHandle
import com.doctoror.splittor.domain.groups.InsertGroupUseCase
import com.doctoror.splittor.domain.groups.ValidateAddGroupInputFieldsUseCase
import com.doctoror.splittor.domain.numberformat.StripCurrencyAndGroupingSeparatorsUseCase
import com.doctoror.splittor.presentation.addgroup.AddGroupPresenter
import com.doctoror.splittor.presentation.addgroup.AddGroupViewModel
import com.doctoror.splittor.presentation.addgroup.AddGroupViewModelUpdater
import com.doctoror.splittor.presentation.addgroup.ContactDetailsViewModelMapper
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun provideAddGroupModule() = module {

    viewModel { (handle: SavedStateHandle) -> AddGroupViewModel(handle) }

    viewModel { parameters ->
        val stripCurrencyAndGroupingSeparatorsUseCase: StripCurrencyAndGroupingSeparatorsUseCase =
            get()
        AddGroupPresenter(
            dispatcherIo = Dispatchers.IO,
            getContactDetailsUseCase = get(),
            insertGroupUseCase = InsertGroupUseCase(groupsRepository = get()),
            stripCurrencyAndGroupingSeparatorsUseCase = stripCurrencyAndGroupingSeparatorsUseCase,
            validateAddGroupInputFieldsUseCase = ValidateAddGroupInputFieldsUseCase(
                stripCurrencyAndGroupingSeparatorsUseCase
            ),
            viewModel = parameters.get(),
            viewModelUpdater = AddGroupViewModelUpdater(
                contactDetailsViewModelMapper = ContactDetailsViewModelMapper()
            )
        )
    }
}