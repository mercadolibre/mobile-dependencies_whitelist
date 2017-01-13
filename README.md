# Whitelist dependencies

**NOTE**: _This repo isnt versioned. `master` branch is consumed by default, so every time `master` branch changes, all repositories will immediatly start consuming the new changes_

### Android

Android whitelist dependencies consist of a set of dependencies that are available for front-ends and high-level repositories to consume from the Mercadolibre-mobile group.

This set of dependencies is parsed in the form of a JSON text. The root level property should be called `whitelist`.

Each of the dependencies is a single String that will be matched against each of the dependencies in the repository. The repository dependencies will be a string formed as `group:name:version`. The whitelist string SUPPORTS regex expression, so you can form match cases for groups in single strings.

Example:
```
{
    "whitelist": [
        # This will match all names of the group 'com.mercadolibre.my_repo' with the version '3.+' (The repo must have 3.+, not 3.9)
        "com\\.mercadolibre\\.my_repo:.*:3\\.\\+", 
        # This will match against all the dependencies that are from the group 'com.another.[whatever]'. It doesnt matter which version or name it has!
        "com\\.another\\." 
    ]
}
```

