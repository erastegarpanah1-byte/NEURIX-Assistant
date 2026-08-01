Project Structure
neurix/
│
├── app/
│   ├── src/main/
│   │   ├── java/com/neurix/app/
│   │   │   ├── MainActivity.kt
│   │   │   ├── NeurixApplication.kt
│   │   │   └── navigation/
│   │   │       └── NeurixNavHost.kt
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   ├── drawable-anydpi-v26/
│   │   │   └── values/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── core/
│   └── build.gradle.kts
│
├── core-common/
│   ├── src/main/java/com/neurix/core/common/
│   │   ├── BaseViewModel.kt
│   │   ├── Constants.kt
│   │   ├── MviState.kt / MviIntent.kt / MviEffect.kt
│   │   └── Result.kt
│   └── build.gradle.kts
│
├── core-design/
│   └── NeurixColors, NeurixDimens, NeurixTheme, ...
│
├── core-ui/
│   └── composables/NeurixComponents.kt
│
├── core-navigation/
│   └── Screen.kt
│
├── core-network/          ← NEW
│   ├── OpenRouterApi.kt
│   ├── OpenRouterDataSource.kt
│   ├── OpenRouterAuthProvider.kt
│   ├── model/OpenRouterModels.kt
│   └── di/NetworkModule.kt
│
├── core-database/         ← NEW
│   ├── NeurixDatabase.kt
│   ├── entity/ConversationEntity.kt, MessageEntity.kt
│   ├── dao/ConversationDao.kt, MessageDao.kt
│   └── di/DatabaseModule.kt
│
├── core-ai/               ← NEW
│   ├── domain/model/DomainModels.kt
│   ├── domain/UseCases.kt
│   └── data/AiRepository.kt, ChatRepository.kt
│
├── core-speech/           ← NEW
│   ├── SpeechManager.kt
│   ├── SpeechRecognizerEngine.kt
│   ├── TextToSpeechEngine.kt
│   ├── AndroidSpeechRecognizer.kt
│   └── AndroidTextToSpeech.kt
│
├── core-actions/          ← NEW
│   ├── ActionModels.kt
│   ├── ActionExecutor.kt
│   ├── ActionRegistry.kt
│   ├── ActionEngine.kt
│   └── executors/ActionExecutors.kt
│
├── core-service/          ← NEW
│   ├── WakeWordService.kt
│   ├── OverlayService.kt
│   ├── NeurixVoiceInteractionService.kt
│   └── wakeword/WakeWordEngine.kt, PlaceholderWakeWordEngine.kt
│
├── feature-home/
│
├── feature-chat/
│   └── presentation/ (updated: real AI + actions)
│
├── feature-assistant/     ← NEW
│   ├── presentation/
│   │   ├── AssistantContract.kt
│   │   ├── AssistantViewModel.kt
│   │   └── FloatingAssistantOverlay.kt
│   ├── domain/
│   └── data/
│
├── feature-settings/
│
├── gradle/
│
├── build.gradle.kts
├── settings.gradle.kts
├── Architecture.md
├── ProjectStructure.md
├── Roadmap.md
└── README.md
Module Count: 15 modules (9 original + 6 new)