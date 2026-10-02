package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ostadkar.app.domain.model.Customer
import com.ostadkar.app.domain.model.Project
import com.ostadkar.app.domain.repository.CustomerRepository
import com.ostadkar.app.domain.repository.ProjectRepository
import com.ostadkar.app.domain.repository.ProjectStageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewProjectViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val projectRepository: ProjectRepository,
    private val stageRepository: ProjectStageRepository
) : ViewModel() {

    suspend fun createProject(
        customerName: String,
        mobile: String,
        address: String,
        title: String,
        description: String
    ): Long {
        val customerId = customerRepository.save(
            Customer(
                name = customerName,
                mobile = mobile,
                address = address
            )
        )
        val projectId = projectRepository.save(
            Project(
                customerId = customerId,
                title = title,
                address = address,
                description = description,
                status = "active"
            )
        )
        stageRepository.seedDefaultStages(projectId)
        return projectId
    }
}
