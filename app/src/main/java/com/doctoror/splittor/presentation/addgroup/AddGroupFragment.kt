package com.doctoror.splittor.presentation.addgroup

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.doctoror.splittor.BR
import com.doctoror.splittor.R
import com.doctoror.splittor.databinding.FragmentGroupAddBinding
import com.doctoror.splittor.databinding.ItemContactBinding
import com.doctoror.splittor.platform.recyclerview.BindingRecyclerAdapter
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddGroupFragment : Fragment() {

    private val contactPickedEvents = PublishSubject.create<Uri>()

    private val activityResultContractPickContact = ActivityResultContracts.PickContact()

    private val activityResultLauncherPickContact = registerForActivityResult(
        activityResultContractPickContact,
        contactPickedEvents::onNext
    )

    private var binding: FragmentGroupAddBinding? = null

    private val disposables = CompositeDisposable()

    private val inputFieldsMonitor: AddGroupInputFieldsMonitor by viewModel()

    private val presenter: AddGroupPresenter by viewModel {
        parametersOf(contactPickedEvents, inputFieldsMonitor, viewModel)
    }

    private val viewModel: AddGroupViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        disposables.add(
            presenter
                .groupInsertedEvents
                .subscribe { findNavController().navigate(R.id.actionAddGroupToGroups) }
        )

        lifecycle.addObserver(presenter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_group, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.create -> presenter.createGroup().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupAddBinding.inflate(inflater, container, false)
        return requireBinding().root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding().recycler.adapter = BindingRecyclerAdapter<ItemContactBinding>(
            layoutId = R.layout.item_contact,
            layoutInflater = layoutInflater,
            modelId = BR.model,
        )
        requireBinding().addContact.setOnClickListener { activityResultLauncherPickContact.launch() }
        requireBinding().model = viewModel
        requireBinding().monitor = inputFieldsMonitor
    }

    override fun onStart() {
        super.onStart()
        viewModel.errorMessage.addOnPropertyChangedCallback(errorMessagePropertyChangeCallback)
    }

    override fun onStop() {
        super.onStop()
        viewModel.errorMessage.removeOnPropertyChangedCallback(errorMessagePropertyChangeCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
        lifecycle.removeObserver(presenter)
    }

    private fun requireBinding() = binding!!

    private val errorMessagePropertyChangeCallback =
        object : Observable.OnPropertyChangedCallback() {

            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                sender as ObservableInt

                val message = sender.get()
                if (message != 0) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    sender.set(0)
                }
            }
        }
}