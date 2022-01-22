# Assisted ViewModel Factory Generator
## Problem
- Needs boilerplate codes for `ViewModel` with `AssistedInject`
  - Write factory interface with `@AssistedFactory`
  - Write `ViewModel.Factory` with `AssistedFactory`
- Needs injecting each all factories to Activity/Fragment

## Solutions
- Generate `AssistedFactory` interfaces
- Generate a container with generated `AssistedFactories`
- Only injecting a previous generated container to Activity/Fragment