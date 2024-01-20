# BoardIt
A simple note taking app in form of boards, organized by categories and tags

Note: This project was created to learn Android Development, And I had nothing more than 15 days of learnings when I started this project

The project was planned ahead in Figma: [Figma Design](https://www.figma.com/file/0DD4oZPlBsTaQDvYj54lC8/BoardIt-App-Design?type=design&node-id=224%3A120&mode=design&t=jb0UwbWuopodZinF-1)

## Features
- Create boards with title and body
- Embed attachments such as images, links, PDFs
- Create boards from shared content(images or links only)
- Organize boards by categories and tags
- Filter boards by selected tags
- Search boards by title and body

## Project Structure
At first the project was intended to sync boards online, but it got unnecessarily complicated so I abandoned the idea and made fully offline.

#### Folder Structure
When started the project, I didn't give much thought to structuring and just to apply many patterns that conflicted with each other. So half through the project things got messy so I decided to half a strict structure like the following:

    .
    ├── data/
    │   ├── local/
    │   │   ├── entities/
    │   │   │   └── AuthorEntity.kt
    │   │   ├── dao/
    │   │   │   └── AuthorDao.kt
    │   │   ├── AppDatabase.kt
    │   │   └── DatabaseModule.kt
    │   ├── remote/
    │   │   ├── models/
    │   │   │   └── AuthorDto.kt
    │   │   └── ApiModule.kt
    │   ├── models/
    │   │   └── Author.kt
    │   └── repository/
    │       └── AuthorRepository.kt
    └── presentation/
        ├── components/
        │   ├── DashedDivider.kt
        │   └── ...
        ├── screens/
        │   ├── login/
        │   │   ├── LoginScreen.kt
        │   │   ├── LoginViewModel.kt
        │   │   └── LoginState.kt
        │   └── ...
        ├── graphs/
        │   ├── AuthGraph.kt
        │   └── AuthorGraph.kt
        ├── theme/
        │   ├── Type.kt
        │   ├── Color.kt
        │   └── Theme.kt
        ├── MainActivity.kt
        ├── MainApplication.kt
        └── Navigation.kt


#### Navigation
For every group of related screens create a graph navigation that holds all those screens

```kotlin
const val AuthGraphPattern = "auth"  
fun NavGraphBuilder.authGraph(
	navController: NavController,
	// ...
){  
	navigation(  
		startDestination = LoginGraphPattern,  
		route = AuthGraphPattern  
	){  
		logInScreen(/* ... */) 
		signUpScreen(/* ... */) 
	}  
}
```

While every screen should have a graph composable, and a navigating function to have type safety

```kotlin
const val UserScreenPattern = "user/{id}"
fun NavGraphBuilder.userScreen() {
	composable(LoginScreenPattern) {  
		val userViewModel: UserViewModel = hiltViewModel()  
		  
		UserScreen(  
			/* ... */
		)  
	}
}

fun NavController.navigateToUserScreen(user: User) {
	this.navigate("user/${user.id}")
}

```

Then combine every graph in `Navigation.kt`

```kotlin
@Composable
fun Navigation(){
	val navController = rememberNavController()
	NavHost(
        navController = navController,
        startDestination = AuthGraphPattern,
        enterTransition = { /* Default Enter Animation */ },
        exitTransition = { /* Default Exit Animation */ },
    ) {
        authGraph(navController, /* ... */)
    }
}
```

#### Conversion between models
Each network model should have an extension to convert to entity model:
```kotlin
fun NetworkAuthor.asEntity() = AuthorEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    twitter = twitter,
    mediumPage = mediumPage,
    bio = bio,
)
```

And each entity model should have an extension to convert to external model(which is the one exposed to the UI Layer):
```kotlin
// To be used in the UI layer
fun AuthorEntity.asExternalModel() = Author(
    id = id,
    name = name,
    imageUrl = imageUrl,
    twitter = twitter,
    mediumPage = mediumPage,
    bio = bio,
)
```

## Screenshots

| Home Screen | Board Screen |
|---|---|
| ![Home Screen](</screenshots/Screenshot 4.jpg>) | ![Board Screen](</screenshots/Screenshot 5.jpg>) |

| Manage Screen | Add Tag Modal |
|---|---|
| ![Manage Screen](</screenshots/Screenshot 8.jpg>) | ![Add Tag Modal](</screenshots/Screenshot 9.jpg>) |

| Filter Tags | Home Screen Sidebar |
|---|---|
| ![Filter Tags](</screenshots/Screenshot 7.jpg>) | ![Home Screen Sidebar](</screenshots/Screenshot 6.jpg>) |

| Edit Screen | Create Screen |
|---|---|
| ![Edit Screen](</screenshots/Screenshot 1.jpg>) | ![Create Screen](</screenshots/Screenshot 10.jpg>) |

| Board Screen(2) | Image Slideshow/Gallery |
|---|---|
| ![Board Screen](</screenshots/Screenshot 2.jpg>) | ![Create Screen](</screenshots/Screenshot 3.jpg>) |
