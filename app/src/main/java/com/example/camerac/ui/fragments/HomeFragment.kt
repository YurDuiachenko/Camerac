package com.example.musting.ui.fragments

import com.example.musting.data.store.FileStorage
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musting.data.api.RetrofitClient
import com.example.musting.data.model.CurrencyEntity
import com.example.musting.data.model.CurrencyInfo
import com.example.musting.data.model.CurrencyPrice
import com.example.musting.data.repository.CurrencyRepository
import com.example.musting.data.store.AppDatabase
import com.example.musting.databinding.FragmentHomeBinding
import com.example.musting.ui.MainActivity
import com.example.musting.ui.adapter.CurrentsViewAdapter
import com.example.musting.ui.model.Currency
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: CurrentsViewAdapter
    private val data: MutableList<Currency> = mutableListOf()

    private val repository: CurrencyRepository by lazy {
        CurrencyRepository(
            RetrofitClient.instance,
            AppDatabase.getDatabase(requireContext()).characterDao()
        )
    }
    private val fileStorage: FileStorage by lazy { FileStorage(requireContext()) }

    private var isLoading = false
    private var currentPage = 0
    private val pageSize = 20

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currents.layoutManager = LinearLayoutManager(context)
        adapter = CurrentsViewAdapter(data)
        binding.currents.adapter = adapter

        getDataFromDatabase()

        fetchDataIfDatabaseIsEmpty()

        addScrollListener()

        addNavigateListenerToSettings()

        addRefreshListener()
    }

    private fun addNavigateListenerToSettings() {
        binding.stngBtn.setOnClickListener {
            (activity as MainActivity).navigateHomeToSettings()
        }
    }

    private fun getDataFromDatabase() {
        lifecycleScope.launchWhenStarted {
            repository.getAll().collect { entities ->
                if (entities.isNotEmpty()) {
                    data.clear()
                    data.addAll(entities.map { mapToCurrency(it) })
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun addScrollListener() {
        binding.currents.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                    currentPage++
                    fetchAllSymbols()
                }
            }
        })
    }

    private fun addRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            currentPage = 0
            data.clear()
            adapter.notifyDataSetChanged()

            fetchAllSymbols()

            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun fetchDataIfDatabaseIsEmpty() {
        lifecycleScope.launch {
            val saved = repository.getAll()
            if (saved.toList().isEmpty()) {
                fetchAllSymbols()
            }
        }
    }

    private fun fetchAllSymbols() {
        isLoading = true
        lifecycleScope.launch {
            repository.deleteAll()
            repository.fetchAll().enqueue(object : Callback<List<CurrencyPrice>> {
                override fun onResponse(
                    call: Call<List<CurrencyPrice>>,
                    response: Response<List<CurrencyPrice>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { currencyPrices ->
                            filterCurrenciesAndTakePage(currencyPrices)
                                .forEach { fetchCurrencyData(it) }
                        }
                    }

                    saveCurrencyToFile(data) { result ->
                        handleSavinFile(result)
                    }

                    saveCurrencyToDatabase(data)

                    isLoading = false
                }

                override fun onFailure(call: Call<List<CurrencyPrice>>, t: Throwable) {
                    showErrorDataLoadingToast()
                    isLoading = false
                }
            })
        }
    }

    private fun fetchCurrencyData(symbol: String) {
        repository.fetchCurrencyBySymbol(symbol).enqueue(object : Callback<CurrencyInfo> {
            override fun onResponse(
                call: Call<CurrencyInfo>,
                response: Response<CurrencyInfo>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        data.add(mapToCurrency(it))
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<CurrencyInfo>, t: Throwable) {
                showErrorDataLoadingToast()
            }
        })
    }

    private fun mapToCurrency(source: CurrencyInfo): Currency {
        return Currency(
            shortName = source.symbol.substring(0, source.symbol.length - 4),
            fullName = source.symbol,
            cost = source.lastPrice.toDouble(),
            grow = (round(source.priceChangePercent.toDouble() * 100) / 100)
        )
    }

    private fun filterCurrenciesAndTakePage(currencies: List<CurrencyPrice>): List<String> {
        return currencies.map { it.symbol }
            .filter { it.endsWith("USDT") }
            .drop(currentPage * pageSize)
            .take(pageSize)
    }

    private fun saveCurrencyToFile(data: List<Currency>, onResult: (Boolean) -> Unit) {
        Log.d("MY-TAG", "Saving to file")
        val dataToSave = mutableListOf<Currency>()
        dataToSave.addAll(data)
        lifecycleScope.launch {
            var isSaved = false
            withContext(Dispatchers.IO) {

                isSaved = fileStorage.writeFile(
                    fileStorage.publicFile(Environment.DIRECTORY_DOCUMENTS, "currencies.txt"),
                    dataToSave.map { Gson().toJson(it) }
                )
            }

            onResult(isSaved)
        }
    }

    private fun saveCurrencyToDatabase(data: List<Currency>) {
        Log.d("MY-TAG", "Saving to database")
        lifecycleScope.launch {
            val dataToSave = data.map { mapToCurrencyEntity(it) }

            withContext(Dispatchers.IO) {
                repository.insertAll(dataToSave)
            }
        }
    }

    private fun showErrorDataLoadingToast() {
        Toast.makeText(context, "Error while loading data", Toast.LENGTH_SHORT).show()
    }

    private fun handleSavinFile(isSaved: Boolean) {
        if (isSaved) Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "Error saving file", Toast.LENGTH_SHORT).show()
    }

    private fun mapToCurrencyEntity(source: Currency): CurrencyEntity {
        return source.let {
            CurrencyEntity(
                null,
                it.shortName,
                it.fullName,
                it.cost,
                it.grow
            )
        }
    }

    private fun mapToCurrency(source: CurrencyEntity): Currency {
        return source.let {
            Currency(
                it.shortName,
                it.fullName,
                it.cost,
                it.grow
            )
        }
    }
}