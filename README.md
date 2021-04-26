# Whitelist dependencies

**NOTE**: _This repo isnt versioned. `master` branch is consumed by default, so every time `master` branch changes, all repositories will immediatly start consuming the new changes_

### Android

Android whitelist dependencies consist of a set of dependencies that are available for front-ends and high-level repositories to consume from the Mercadolibre-mobile group.

This set of dependencies is parsed in the form of a JSON text. The root level property should be called `whitelist`.

Each of the dependencies is a JSON Object that will be matched against each of the unresolved dependencies of the repository. The repository dependencies will be a string formed as `group:name:version`. The whitelist fields SUPPORTS regex expressions, so you can form match cases for groups in single strings.

**NOTE1:** Remember that this are regexes, so if you want to declare `com.example` it should be `com\\.example`
**NOTE2**: The repository will validate against unresolved dependencies. Thus, if declaring as version `4\\.\\+` it **will** match against a dependency `4.+` (it wont be for example the string `4.2.3`)
**NOTE3**: You can have expirable dependencies by adding the `expires` field. If no field is added, the dependency is considered as non-expirable
**NOTE4**: If no group / name / version is provided, they will default to `.*` (any string)

JSON Schema:
```
{
  "whitelist": [
    {
      "group": "group_regex",
      "name": "name_regex",
      "version": "version_regex",
      "expires": "yyyy-MM-dd"
    },
    ...
  ]
}
```

**NOTE5**: If you want to try if its working correctly from your fork, just add this line to de build.gradle:
```
lintGradle {
    dependencyWhitelistUrl = "https://raw.githubusercontent.com/YOUR_GITHUB_USER/mobile-dependencies_whitelist/master/android-whitelist.json"
}
```

### iOS
iOS whitelist dependencies consist of a set of dependencies that are available for front-ends and high-level repositories to consume from the Mercadolibre-mobile group.

This set of dependencies is parsed in the form of a JSON text. The root level property should be called `whitelist`.

Each of the dependencies is an object with the following properties:
 - `name`: Dependency Podname
 - `source`: keyword that indicates the source where the dependency spec should be downloaded. (`public` || `private`)
 - `target`: Indicates if it is a test or productive dependency. (`test` || `productive`)
 - `version`: Which will be matched against each of the dependencies in the podspec. The `version` string SUPPORTS regex expression.

Example:
```
{
	"whitelist": [
     # This will match with 'MeliSDK' and version '~>5.+' (version must have ~>5.x)
    {
		"name": "MeliSDK",
		"version": "^~>5.[0-9]+$"
	}, 
     # This will match with 'MLRecommendations' for any version
    {
		"name": "MLRecommendations",
		"version": null
	}]
}
```

# Contexts Whitelist

This json represents the contexts for our apps, both for iOS and Android. Which will be consumed by Kelli, in the "Deploy" job, and will be pushed into the apps, in their respective branches. 
The apps will use it to create the Initiative map, and will use, the "key" to looks for the context of the errors, to pass it on to Bugsnag.

This set of context is parsed in the form of a JSON text. The root level property should be called “context”.

Each context is an object with three properties:

- **“name”**: which represents the context.
- "iOS" and "Android" objects, which have two properties:
    - **“key”**: which represents, the "package" in Android and the "Module" in iOS, and it will be used to match and set the context for each error occurs in the apps.

Example:
```
{
    "context": [
        {
            "name": "ab_user_onboarding",
            "iOS": {
                "key": "ABUserOnboarding"
            },
            "Android": {
                "key": "com.mercadopago.android.useronboarding"
            }
        },
        …
    ]
}
```