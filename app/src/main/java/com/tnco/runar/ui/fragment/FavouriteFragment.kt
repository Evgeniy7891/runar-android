package com.tnco.runar.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tnco.runar.R
import com.tnco.runar.analytics.AnalyticsHelper
import com.tnco.runar.enums.AnalyticsEvent
import com.tnco.runar.ui.Navigator
import com.tnco.runar.ui.component.dialog.SavedLayoutsDialog
import com.tnco.runar.ui.viewmodel.FavouriteViewModel
import com.tnco.runar.util.AnalyticsConstants
import com.tnco.runar.util.AnalyticsUtils

class FavouriteFragment : Fragment() {
    val viewModel: FavouriteViewModel by viewModels()
    private var navigator: Navigator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.getUserLayoutsFromDB()
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigator = context as Navigator
    }

    override fun onDetach() {
        navigator = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext()).apply {
            setContent {
                Bars(navigator)
            }
        }
        AnalyticsHelper.sendEvent(AnalyticsEvent.FAVOURITE_OPENED)
        view.isFocusableInTouchMode = true
        view.requestFocus()
        return view
    }
}

@Composable
private fun Bars(navigator: Navigator?) {
    val viewModel: FavouriteViewModel = viewModel()
    val fontSize by viewModel.fontSize.observeAsState()
    val favData by viewModel.favData.observeAsState()
    val existSelected by viewModel.haveSelectedItem.observeAsState()

    val barColor = colorResource(id = R.color.library_top_bar_header)
    val barFont = FontFamily(Font(R.font.roboto_medium))
    val barFontSize = with(LocalDensity.current) { ((fontSize!! * 1.35f)).toSp() }
    var barText = stringResource(id = R.string.library_bar_fav)

    var navIcon: @Composable (() -> Unit)? = null
    var navActions: @Composable RowScope.() -> Unit = {}

    val checkedState = remember { mutableStateOf(false) }

    if (existSelected!! >= 1) {
        barText = ""
        navIcon = {
            TopBarIcon(clickAction = {
                viewModel.changeAll(false)
                checkedState.value = false
            })
        }
        navActions = {
            TopBarActions(fontSize!!,
                clickAction = {
                    viewModel.removeSelectedLayouts()
                    viewModel.changeAll(false)
                    checkedState.value = false
                })
        }
        if (existSelected == 2) checkedState.value = false
        else if (existSelected == 3) checkedState.value = true
    } else checkedState.value = false

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = barText,
                        color = barColor,
                        fontFamily = barFont,
                        style = TextStyle(fontSize = barFontSize)
                    )
                },
                backgroundColor = colorResource(id = R.color.library_top_bar),
                navigationIcon = navIcon,
                actions = navActions
            )
        },
        backgroundColor = colorResource(id = R.color.library_top_bar_2)
    ) {
        val scrollState = rememberScrollState()

        Column(Modifier.verticalScroll(state = scrollState, enabled = true)) {
            if (favData != null) {
                if (favData!!.isNotEmpty()) {
                    CheckboxItem(
                        state = checkedState.value,
                        checkAction = {
                            checkedState.value = it
                            viewModel.changeAll(it)
                        },
                        fontSize = fontSize!!
                    )
                }
                for (item in favData!!) {
                    if (item.id != 666999) {
                        FavItem(
                            fontSize = fontSize!!,
                            time = item.time!!,
                            text = item.content!!,
                            header = item.header!!,
                            clickAction = {
                                val layoutName =
                                    AnalyticsUtils.convertLayoutIdToName(item.layoutId!!)
                                AnalyticsHelper.sendEvent(
                                    AnalyticsEvent.FAVOURITE_DRAWS_OPENED,
                                    Pair(AnalyticsConstants.DRAW_RUNE_LAYOUT, layoutName)
                                )
                                navigator?.navigateToFavInterpretationFragment(
                                    layoutId = item.layoutId!!,
                                    userLayout = item.userData!!,
                                    affirmId = item.affirmId!!
                                )
                            },
                            state = item.selected!!,
                            checkAction = {
                                viewModel.changeSelection(item.id!!)
                            })
                    }
                }
            }
            Box(modifier = Modifier.aspectRatio(15f, true))
        }
    }
}

@Composable
private fun TopBarIcon(clickAction: () -> Unit) {
    IconButton(onClick = clickAction) {
        Icon(
            painter = painterResource(id = R.drawable.ic_library_back_arrow_2),
            tint = colorResource(id = R.color.library_top_bar_fav),
            contentDescription = "arrow"
        )
    }
}

@Composable
private fun TopBarActions(fontSize: Float, clickAction: () -> Unit) {
    val context = LocalContext.current
    IconButton(onClick = {
        SavedLayoutsDialog(context, fontSize, clickAction).showDialog()
    }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            tint = colorResource(id = R.color.fav_top_bar_delete),
            contentDescription = "trash"
        )
    }
}

@Composable
private fun CheckboxItem(
    state: Boolean,
    checkAction: (Boolean) -> Unit,
    fontSize: Float
) {
    Box(
        Modifier
            .aspectRatio(10f, true)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(258f)
            )
            Text(
                modifier = Modifier
                    .weight(100f),
                text = stringResource(id = R.string.fav_checkbox_text),
                textAlign = TextAlign.End,
                color = colorResource(id = R.color.fav_checkbox_text),
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                style = TextStyle(
                    fontSize = with(LocalDensity.current) {
                        ((fontSize * 0.7f)).toSp()
                    }
                )
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(21f)
            )
            Checkbox(
                checked = state,
                onCheckedChange = checkAction,
                modifier = Modifier
                    .weight(18f),
                colors = CheckboxDefaults.colors(
                    uncheckedColor = colorResource(id = R.color.fav_checkbox_border),
                    checkedColor = colorResource(id = R.color.fav_checkbox_selected),
                    checkmarkColor = colorResource(id = R.color.fav_checkbox_checkmark)
                )

            )
            //end space
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(25f)
            )
        }
    }
}

@Composable
private fun FavItem(
    fontSize: Float,
    time: String,
    text: String,
    header: String,
    clickAction: () -> Unit,
    state: Boolean,
    checkAction: (Boolean) -> Unit
) {

    Row(
        Modifier
            .aspectRatio(4.25f, true)
            .clickable(onClick = clickAction)
    ) {
        //start space
        Box(
            Modifier
                .fillMaxSize()
                .weight(16f)
        )
        //data space
        Column(
            Modifier
                .fillMaxSize()
                .weight(398f)
        ) {
            //top space
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(10f)
            )
            //data
            Row(
                Modifier
                    .fillMaxSize()
                    .weight(66f),

                verticalAlignment = Alignment.CenterVertically
            ) {
                //first img
                //space between first img and text
                Column(
                    Modifier
                        .fillMaxSize()
                        .weight(318f)
                        .padding(end = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = header,
                            color = colorResource(id = R.color.fav_header_text),
                            fontFamily = FontFamily(Font(R.font.roboto_regular)),
                            style = TextStyle(
                                fontSize = with(LocalDensity.current) {
                                    fontSize.toSp()
                                }
                            ),
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .weight(10f),
                        )
                        Text(
                            text = time,
                            color = colorResource(id = R.color.fav_time_text),
                            fontFamily = FontFamily(Font(R.font.roboto_regular)),
                            style = TextStyle(
                                fontSize = with(LocalDensity.current) {
                                    ((fontSize * 0.7f)).toSp()
                                }
                            ),
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .weight(7f),
                            textAlign = TextAlign.End
                        )
                    }
                    Text(
                        text = text,
                        color = colorResource(id = R.color.fav_inter_text),
                        fontFamily = FontFamily(Font(R.font.roboto_light)),
                        style = TextStyle(
                            fontSize = with(LocalDensity.current) {
                                ((fontSize * 0.8f)).toSp()
                            }
                        )
                    )
                }
                //space between text and end img
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(15f)
                )
                //end img
                Checkbox(
                    checked = state,
                    onCheckedChange = checkAction,
                    modifier = Modifier
                        .weight(18f)
                        .padding(top = 6.dp),
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = colorResource(id = R.color.fav_checkbox_border),
                        checkedColor = colorResource(id = R.color.fav_checkbox_selected),
                        checkmarkColor = colorResource(id = R.color.fav_checkbox_checkmark)
                    )

                )
                //end space
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(23f)
                )
            }
            //bottom space
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(16f)
            )
            //bottom divider
            Divider(
                color = colorResource(id = R.color.divider)
            )
        }
    }
}
