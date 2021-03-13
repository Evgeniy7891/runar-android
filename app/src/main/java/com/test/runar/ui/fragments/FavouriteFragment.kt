package com.test.runar.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.runar.R
import com.test.runar.presentation.viewmodel.FavouriteViewModel
import com.test.runar.presentation.viewmodel.LibraryViewModel
import dev.chrisbanes.accompanist.coil.CoilImage

class FavouriteFragment : Fragment() {
    val viewModel: FavouriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.getUserLayoutsFromDB()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext()).apply {
            setContent {
                Bars()
            }
        }
        view.isFocusableInTouchMode = true
        view.requestFocus()

        view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //viewModel.goBackInMenu()
                }
            }
            true
        }
        return view
    }
}

@Composable
private fun Bars() {
    val viewModel: FavouriteViewModel = viewModel()
    val fontSize by viewModel.fontSize.observeAsState()
    //val header by viewModel.lastMenuHeader.observeAsState()
    val favData by viewModel.favData.observeAsState()

    var barColor = colorResource(id = R.color.library_top_bar_header)
    var barFont = FontFamily(Font(R.font.roboto_medium))
    var barFontSize = with(LocalDensity.current) { ((fontSize!! * 1.35).toFloat()).toSp() }
    var navIcon: @Composable() (() -> Unit)? = null

    /*if (header != stringResource(id = R.string.library_top_bar_header)) {
        barColor = colorResource(id = R.color.library_top_bar_header_2)
        barFont = FontFamily(Font(R.font.roboto_medium))
        barFontSize = with(LocalDensity.current) { fontSize!!.toSp() }
        navIcon = { TopBarIcon() }
    }*/
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Избранное",
                        color = barColor,
                        fontFamily = barFont,
                        style = TextStyle(fontSize = barFontSize)
                    )
                },
                backgroundColor = colorResource(id = R.color.library_top_bar),
                navigationIcon = navIcon
            )
        },
        backgroundColor = Color(0x73000000)
    ) {
        val scrollState = rememberScrollState()
        Column(Modifier.verticalScroll(state = scrollState, enabled = true)) {
            if (favData != null) {
                for (item in favData!!) {
                    FavItem(
                        fontSize = fontSize!!,
                        header = item.header!!,
                        text = item.content!!,
                        imgId = 12,
                        clickAction = { })
                }
            }
            Box(modifier = Modifier.aspectRatio(15f, true))
        }
    }
}

@Composable
private fun TopBarIcon() {
    val viewModel: LibraryViewModel = viewModel()
    IconButton(onClick = { viewModel.goBackInMenu() }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_library_back_arrow_2),
            tint = colorResource(id = R.color.library_top_bar_fav),
            contentDescription = "arrow"
        )
    }
}

@Composable
private fun FavItem(
    fontSize: Float,
    header: String,
    text: String,
    imgId: Int,
    clickAction: () -> Unit
) {
    Row(
        Modifier
            .aspectRatio(3.8f, true)
            .clickable(onClick = clickAction)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .weight(16f)
        )
        Column(
            Modifier
                .fillMaxSize()
                .weight(398f)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(20f)
            )
            Row(
                Modifier
                    .fillMaxSize()
                    .weight(62f), verticalAlignment = Alignment.CenterVertically
            ) {
                CoilImage(
                    data = R.drawable.test_rune,
                    contentDescription = null,
                    modifier = Modifier
                        .background(Color(0x00000000))
                        .padding(top = 5.dp, bottom = 5.dp)
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .weight(277f)
                        .padding(start = 15.dp)
                ) {
                    Text(
                        text = header,
                        color = colorResource(id = R.color.library_item_header),
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        style = TextStyle(fontSize = with(LocalDensity.current) { ((fontSize * 0.8).toFloat()).toSp() }),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = text,
                        color = colorResource(id = R.color.library_item_text),
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        style = TextStyle(fontSize = with(LocalDensity.current) { ((fontSize * 0.8).toFloat()).toSp() })
                    )
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(17f)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_right),
                    contentDescription = null,
                    modifier = Modifier
                        .background(Color(0x00000000))
                        .weight(10f)
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(16f)
                )
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(20f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_divider),
                contentDescription = null
            )
        }
    }
}
