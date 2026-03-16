package com.diva.onboarding.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.get_started
import com.diva.core.ui.resources.go_back
import com.diva.core.ui.resources.ic_arrow_left
import com.diva.core.ui.resources.ic_chevron_left
import com.diva.core.ui.resources.ic_chevron_right
import com.diva.core.ui.resources.next
import com.diva.core.ui.resources.previous
import com.diva.core.ui.resources.puerro
import com.diva.core.ui.resources.skip
import com.diva.onboarding.presentation.events.OnboardingEvents
import com.diva.onboarding.presentation.state.OnboardingState
import com.diva.onboarding.presentation.viewmodel.OnboardingViewModel
import io.github.juevigrace.diva.ui.components.layout.Screen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val state: OnboardingState by viewModel.state.collectAsStateWithLifecycle()
    val pagerState: PagerState = rememberPagerState(pageCount = { state.pages.size })

    LaunchedEffect(state.page) {
        pagerState.animateScrollToPage(state.page)
    }

    Screen(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = if (state.showBackButton) {
                    {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(OnboardingEvents.OnPreviousPage)
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_arrow_left),
                                contentDescription = stringResource(Res.string.go_back)
                            )
                        }
                    }
                } else {
                    {}
                },
                title = {
                    Text(text = stringResource(state.currentPage.title))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { _ ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(state.currentPage.description),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Image(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        painter = painterResource(Res.drawable.puerro),
                        contentDescription = null
                    )
                    if (state.showNavigateToAuth) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.onEvent(OnboardingEvents.OnNavigateToSignIn) }
                        ) {
                            Text(
                                text = stringResource(Res.string.get_started),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            if (state.showBottomRow) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(OnboardingEvents.OnPreviousPage)
                            },
                            enabled = state.previousEnabled
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chevron_left),
                                contentDescription = stringResource(Res.string.previous)
                            )
                        }
                        Text(
                            text = "${pagerState.currentPage + 1} / ${state.pages.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(
                            onClick = {
                                viewModel.onEvent(OnboardingEvents.OnNextPage)
                            },
                            enabled = state.nextEnabled
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chevron_right),
                                contentDescription = stringResource(Res.string.next)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.onEvent(OnboardingEvents.OnSkip) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            text = stringResource(Res.string.skip),
                        )
                    }
                }
            }
        }
    }
}
