package com.diva.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.diva.ui.navigation.arguments.ForgotAction
import com.diva.ui.navigation.arguments.PlayerArgs
import com.diva.ui.navigation.arguments.VerificationAction
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    sealed interface OnboardingGraph : Destination {
        @Serializable
        data object Splash : Destination

        @Serializable
        data object Onboarding : Destination
    }

    @Serializable
    sealed interface AuthGraph : Destination {
        @Serializable
        data object SignIn : AuthGraph

        @Serializable
        data object SignUp : AuthGraph

        @Serializable
        data class Forgot(val action: ForgotAction) : AuthGraph

        @Serializable
        data class Verification(val action: VerificationAction) : AuthGraph
    }

    @Serializable
    sealed interface HomeGraph : Destination {
        @Serializable
        data object Home : HomeGraph

        @Serializable
        data object Feed : HomeGraph

        @Serializable
        data object Creation : HomeGraph

        @Serializable
        data object Library : HomeGraph

        @Serializable
        data object Profile : HomeGraph
    }

    @Serializable
    data object Search : Destination

    @Serializable
    data class Post(val postId: String) : Destination

    @Serializable
    data class Player(val playerArgs: PlayerArgs) : Destination

    @Serializable
    data object Notifications : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    sealed interface ChatGraph : Destination {
        @Serializable
        data object Chats : ChatGraph

        @Serializable
        data class Chat(val id: String) : ChatGraph
    }
}

typealias SplashDestination = Destination.OnboardingGraph.Splash
typealias OnboardingDestination = Destination.OnboardingGraph.Onboarding
typealias SignInDestination = Destination.AuthGraph.SignIn
typealias SignUpDestination = Destination.AuthGraph.SignUp
typealias ForgotDestination = Destination.AuthGraph.Forgot
typealias VerificationDestination = Destination.AuthGraph.Verification
typealias HomeDestination = Destination.HomeGraph.Home
typealias SearchDestination = Destination.Search
typealias FeedDestination = Destination.HomeGraph.Feed
typealias CreationDestination = Destination.HomeGraph.Creation
typealias LibraryDestination = Destination.HomeGraph.Library
typealias ProfileDestination = Destination.HomeGraph.Profile
typealias PostDestination = Destination.Post
typealias PlayerDestination = Destination.Player
typealias NotificationsDestination = Destination.Notifications
typealias SettingsDestination = Destination.Settings
typealias ChatsDestination = Destination.ChatGraph.Chats
typealias ChatDestination = Destination.ChatGraph.Chat
