package com.doctoror.splittor.domain.groups

import io.reactivex.rxjava3.core.Observable

interface GroupsRepository {

    fun observeGroups(): Observable<List<Group>>
}
