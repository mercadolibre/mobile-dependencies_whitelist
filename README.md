# Allowlist dependencies

**NOTE**: _This repo isnt versioned. `master` branch is consumed by default, so every time `master` branch changes, all
repositories will immediatly start consuming the new changes_

**If you need to add or update a library**
,visit [Wiki.](https://sites.google.com/mercadolibre.com/mobile/arquitectura/allowlist)

### Android

Android allowlist dependencies consist of a set of dependencies that are available for front-ends and high-level
repositories to consume from the Mercadolibre-mobile group.

This set of dependencies is parsed in the form of a JSON text. The root level property should be called `whitelist`.

Each of the dependencies is a JSON Object that will be matched against each of the unresolved dependencies of the
repository. The repository dependencies will be a string formed as `group:name:version`. The allowlist fields SUPPORTS
regex expressions, so you can form match cases for groups in single strings.

**NOTE1:** Remember that this are regexes, so if you want to declare `com.example` it should be `com\\.example`
**NOTE2**: The repository will validate against unresolved dependencies. Thus, if declaring as version `4\\.\\+` it **
will** match against a dependency `4.+` (it wont be for example the string `4.2.3`)
**NOTE3**: You can have expirable dependencies by adding the `expires` field. If no field is added, the dependency is
considered as non-expirable
**NOTE4**: If no group / name / version is provided, they will default to `.*` (any string)

JSON Schema:

```
{
  "whitelist": [
    {
      "description": "(optional) description",
      "expires": "yyyy-MM-dd",
      "group": "group_regex",
      "name": "name_regex",
      "version": "version_regex"
    },
    ...
  ]
}
```

**NOTE5**: If you want to try if its working correctly from your fork, just add this line to the build.gradle:

```
ext["allowlistURL"] = "https://raw.githubusercontent.com/YOUR_GITHUB_USER/mobile-dependencies_whitelist/YOUR_GIT_BRANCH/android-whitelist.json"
```

### iOS

iOS allowlist dependencies consist of a set of dependencies that are available for front-ends and high-level
repositories to consume from the Mercadolibre-mobile group.

This set of dependencies is parsed in the form of a JSON text. The root level property should be called `whitelist`.

Each of the dependencies is an object with the following properties:

- `name`: Dependency Podname
- `source`: keyword that indicates the source where the dependency spec should be downloaded. (`public` || `private`)
- `target`: Indicates if it is a test or productive dependency. (`test` || `productive`)
- `version`: Which will be matched against each of the dependencies in the podspec. The `version` string SUPPORTS regex
  expression.

#### Optional

- `expires`: You can have expirable dependencies by adding the `expires` field. If no field is added, the dependency is
  considered as non-expirable
- `description`: (optional) some relevant description

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

## Support for Granular Dependencies:

This functionality provides a more precise management of the scope of dependencies, giving us the ability to select specific consumers for each of them.

To activate the granularity feature, it is necessary to introduce a new block within the dependency definition, specifying which Mercado Libre projects will have access to it. This should be done as follows:


### Android Platform
```
{
  "whitelist": [
    {
      "allows_granular_projects": [ 
            "group_meli_project",
            "com.mercadolibre.android.commons"  # Example of a Mercado Libre Dependency Group.
      ]
      "description": "(optional) description",
      "expires": "yyyy-MM-dd",
      "group": "group_regex",
      "name": "name_regex",
      "version": "version_regex"
    },
    ...
  ]
}
```

### iOS Platform
```
{
  "whitelist": [
    {
      "name": "MeliSDK",
      "version": "^~>5.[0-9]+$"
      "allows_granular_projects": [ 
            "name_meli_lib",
            "MLRecommendations"  # Example of a Mercado Libre Dependency Lib Name .
      ]
    },
    ...
  ]
}
```



# Contexts Allowlist [DEPRECATED]

For more information consult
the [new context allowlist](https://furydocs.io/mobile-apps/v1.5.2/guide/#/lang-en/metrics/02_crash-rate?id=contexts)
