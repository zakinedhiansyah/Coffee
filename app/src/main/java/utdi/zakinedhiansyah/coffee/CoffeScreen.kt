@file:OptIn(ExperimentalMaterial3Api::class)

package utdi.zakinedhiansyah.coffee

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import utdi.zakinedhiansyah.coffee.data.DataSource
import utdi.zakinedhiansyah.coffee.data.OrderUiState
import utdi.zakinedhiansyah.coffee.ui.OrderSummaryScreen
import utdi.zakinedhiansyah.coffee.ui.OrderViewModel
import utdi.zakinedhiansyah.coffee.ui.SelectOptionScreen
import utdi.zakinedhiansyah.coffee.ui.StartOrderScreen


enum class CoffeeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.choose_flavor),
    Pickup(title = R.string.choose_pickup_date),
    Summary(title = R.string.order_summary)
}

/**
 */
@Composable
fun CoffeeAppBar(
    currentScreen: CoffeeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) { /** Untuk menampilkan tombol back pada tampilan atas aplikasi*/
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CoffeeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = CoffeeScreen.valueOf(
        backStackEntry?.destination?.route ?: CoffeeScreen.Start.name
    )

    Scaffold(
        topBar = {
            CoffeeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = CoffeeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            /**
             * untuk menampilkan awal apk
             * DataSource untuk menampikan data berapa banyak kopi yang ingin dipesan
             * [onNextButtonClicked] untuk melanjutkan pesanan ke tampilan pilih rasa
             */
            composable(route = CoffeeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        navController.navigate(CoffeeScreen.Flavor.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            /**
             * untuk menampilkan tanpilan beberapa pilihan rasa kopi
             * [onNextButtonClicked] tombol untuk melanjutkan pesanan dan tampilan akan berpindah ke pickup date
             * [onCancelButtonClicked] tombol untuk membatalkan pesanan dan tampilan akan berpindah ke tampilan awal
             * */
            composable(route = CoffeeScreen.Flavor.name) {
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CoffeeScreen.Pickup.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = DataSource.flavors.map { id -> context.resources.getString(id) },
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            /**
             * untuk menampilkan tanggal pickup
             * [onNextButtonClicked] tombol untuk melanjutkan pesanan dan akan berpindah ke tampilan order summary
             * [onCancelButtonClicked] tombol untuk membatalkan pesanan dan tampilan akan berpindah ke tampilan awal
             */
            composable(route = CoffeeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CoffeeScreen.Summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            /** Menampilkan halaman Order Summary
             * [onCancelButtonCliked] untuk membatalkan pesanan dan tampilan otomatis pindah ke tampilan awal
             * [onSendButtonClicked] untuk mengirim pesanan ke beberapa apk
             */
            composable(route = CoffeeScreen.Summary.name) {
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onSendButtonClicked = { subject: String, summary: String ->
                        shareOrder(context, subject = subject, summary = summary)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

/**
 * Fungsi untuk membatalkan pesanan dan tampilan otomatis perpindah ke tampilan awal aplikasi jika membatalkan pesanan
 */
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CoffeeScreen.Start.name, inclusive = false)
}

/**
 * Creates an intent to share order details
 */
private fun shareOrder(context: Context, subject: String, summary: String) {
    // Create an ACTION_SEND implicit intent with order details in the intent extras
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_coffee_order)
        )
    )
}
