Architecture
Overview
Neurix follows Clean Architecture with MVI (Model-View-Intent) presentation pattern, organized into multiple Gradle modules for maximum scalability and separation of concerns.

Module Dependency Graph
app
 ├── core (aggregator)
 │    ├── core-common
 │    ├── core-design
 │    ├── core-ui
 │    ├── core-navigation
 │    ├── core-network
 │    ├── core-database
 │    ├── core-ai
 │    ├── core-speech
 │    ├── core-actions
 │    └── core-service
 ├── feature-home
 ├── feature-chat
 │    ├── core-ai
 │    ├── core-speech
 │    └── core-actions
 ├── feature-settings
 └── feature-assistant
      ├── core-ai
      ├── core-speech
      ├── core-actions
      └── core-service

Layer Responsibilities
app - Entry point, DI setup, splash, navigation
core-common - BaseViewModel, Result<T>, MVI contracts
core-design - Theme, colors, typography, dimensions
core-ui - Reusable composables (FadeInView, PlaceholderScreen)
core-navigation - Screen route definitions
core-network - OpenRouter API client (Retrofit + OkHttp)
core-database - Room DB (Conversations, Messages)
core-ai - AiRepository, ChatRepository, UseCases
core-speech - SpeechRecognizerEngine, TextToSpeechEngine, SpeechManager
core-actions - ActionEngine, ActionRegistry, 6 action executors
core-service - WakeWordService, OverlayService, VoiceInteractionService

Feature Modules
feature-home - Home screen with voice button
feature-chat - AI-powered chat with MVI
feature-assistant - Floating glass overlay, voice-first assistant flow
feature-settings - Settings with placeholder detail pages