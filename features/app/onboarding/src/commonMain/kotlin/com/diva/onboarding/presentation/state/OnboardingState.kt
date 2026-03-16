package com.diva.onboarding.presentation.state

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.onboarding_chat_description
import com.diva.core.ui.resources.onboarding_chat_title
import com.diva.core.ui.resources.onboarding_get_started_description
import com.diva.core.ui.resources.onboarding_get_started_title
import com.diva.core.ui.resources.onboarding_personalize_description
import com.diva.core.ui.resources.onboarding_personalize_title
import com.diva.core.ui.resources.onboarding_social_description
import com.diva.core.ui.resources.onboarding_social_title
import org.jetbrains.compose.resources.StringResource

data class OnboardingState(
    val page: Int = 0,
    val pages: List<OnboardingPage> = defaultPages
) {
    val currentPage: OnboardingPage get() = pages[page]
    val previousEnabled: Boolean get() = page > 0
    val nextEnabled: Boolean get() = page < pages.lastIndex
    val showBackButton: Boolean get() = page == pages.lastIndex
    val showBottomRow: Boolean get() = page != pages.lastIndex
    val showNavigateToAuth: Boolean get() = page == pages.lastIndex

    companion object {
        private val defaultPages: List<OnboardingPage> = listOf(
            OnboardingPage(
                title = Res.string.onboarding_social_title,
                description = Res.string.onboarding_social_description
            ),
            OnboardingPage(
                title = Res.string.onboarding_personalize_title,
                description = Res.string.onboarding_personalize_description
            ),
            OnboardingPage(
                title = Res.string.onboarding_chat_title,
                description = Res.string.onboarding_chat_description
            ),
            OnboardingPage(
                title = Res.string.onboarding_get_started_title,
                description = Res.string.onboarding_get_started_description
            )
        )
    }
}

data class OnboardingPage(
    val title: StringResource,
    val description: StringResource
)
