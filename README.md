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

## Example

### ViewModels

```kotlin
class AssistedViewModel @AssistedInject constructor(
    repository: ExampleRepository,
    @Assisted val exampleId: String,
    @Assisted val names: List<String>
) : ViewModel() 
```

```kotlin
class AssistedViewModel2 @AssistedInject constructor(
    repository: ExampleRepository,
    @Assisted val id: Long,
) : ViewModel()
```

### Generated factories

```kotlin
@AssistedFactory
public interface AssistedViewModelFactory {
    public fun create(exampleId: String, names: List<String>): AssistedViewModel
}

@AssistedFactory
public interface AssistedViewModel2Factory {
    public fun create(id: Long): AssistedViewModel2
}

@HiltViewModel
public class AssistedViewModelFactoryViewModel @Inject constructor(
    private val assistedViewModelFactory: AssistedViewModelFactory,
    private val assistedViewModel2Factory: AssistedViewModel2Factory
) : ViewModel() {
    public fun createAssistedViewModel(exampleId: String, names: List<String>):
            ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        public override fun <T : ViewModel> create(modelClass: Class<T>): T =
            assistedViewModelFactory.create(exampleId, names) as T
    }

    public fun createAssistedViewModel2(id: Long): ViewModelProvider.Factory = object :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        public override fun <T : ViewModel> create(modelClass: Class<T>): T =
            assistedViewModel2Factory.create(id) as T
    }
}
```

### Use

```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val factories: AssistedViewModelFactoryViewModel by viewModels()

    private val viewModel: AssistedViewModel by viewModels {
        factories.createAssistedViewModel("1", listOf("a", "b", "c"))
    }

    private val viewModel2: AssistedViewModel2 by viewModels {
        factories.createAssistedViewModel2(7L)
    }
}
```