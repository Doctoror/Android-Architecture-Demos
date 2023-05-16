package com.doctoror.splittor.presentation.groupsoverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.doctoror.splittor.BR
import com.doctoror.splittor.R
import com.doctoror.splittor.databinding.FragmentGroupsOverviewBinding
import com.doctoror.splittor.databinding.ItemGroupBinding
import com.doctoror.splittor.platform.recyclerview.BindingRecyclerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GroupsOverviewFragment : Fragment() {

    private val adapter by lazy {
        BindingRecyclerAdapter<ItemGroupBinding, GroupItemViewModel>(
            layoutId = R.layout.item_group,
            layoutInflater = layoutInflater,
            modelId = BR.model,
            onItemClickListener = {
                findNavController().navigate(
                    GroupsOverviewFragmentDirections.actionGroupsOverviewToGroupDetails(it.id)
                )
            }
        )
    }

    private var binding: FragmentGroupsOverviewBinding? = null

    private val viewModel: GroupsOverviewViewModel by viewModel()

    private val presenter: GroupsOverviewPresenter by viewModel {
        parametersOf(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(presenter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupsOverviewBinding.inflate(layoutInflater, container, false)
        return requireBinding().root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding().fab.setOnClickListener {
            navigateToAddGroup()
        }

        requireBinding().fragmentGroupsEmpty.addFirstGroup.setOnClickListener {
            navigateToAddGroup()
        }

        requireBinding().fragmentGroupsContent.recycler.adapter = adapter

        requireBinding().model = viewModel
    }

    private fun navigateToAddGroup() {
        findNavController().navigate(GroupsOverviewFragmentDirections.actionGroupsToAddGroup())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(presenter)
    }

    private fun requireBinding() = binding!!
}
