# Whitelist dependencies

**NOTE**: _This repo isnt versioned. `master` branch is consumed by default, so every time `master` branch changes, all repositories will immediatly start consuming the new changes_

### Android

Android whitelist dependencies consist of a set of dependencies that are available for front-ends and high-level repositories to consume from the Mercadolibre-mobile group.

This set of dependencies is parsed in the form of a JSON text. The root level property should be called `whitelist`.

Each of the dependencies is a JSON Object that will be matched against each of the resolved dependencies of the repository. The repository dependencies will be a string formed as `group:name:version`. The whitelist fields SUPPORTS regex expressions, so you can form match cases for groups in single strings.

**NOTE1:** Remember that this are regexes, so if you want to declare `com.example` it should be `com\\.example`
**NOTE2**: The repository will validate against resolved dependencies. Thus, if declaring as version `4\\.\\+` it **will** match against a dependency `4.2.1` (it wont be explicitly the string `4.+`)
**NOTE3**: You can expirable dependencies (by adding the `expires` field). If no field added, the dependency is considered as non-expirable

Example of the json:
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

- If no group / name / version is provided, they will default to `.*` (anystring)
- If no expires field is provided, the dependency is considered non-expirable.

### iOS
iOS whitelist dependencies consist of a set of dependencies that are available for front-ends and high-level repositories to consume from the Mercadolibre-mobile group.

This set of dependencies is parsed in the form of a JSON text. The root level property should be called `whitelist`.

Each of the dependencies is an object with two properties `name` and `version` which will be matched against each of the dependencies in the podspec. The `version` string SUPPORTS regex expression.

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
