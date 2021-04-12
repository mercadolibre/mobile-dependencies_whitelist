package com.mercadolibre.android.gradle.base.publish

class RepositoryProvider {
    private static final List<Repository> REPOSITORIES = new ArrayList<Repository>() {
        {
            add(new Repository("AndroidPublic", "https://artifacts.mercadolibre.com/repository/maven-releases", new EnvironmentPublishCredentials()))
            add(new Repository("AndroidExperimental", "https://android.artifacts.furycloud.io/repository/releases", new EnvironmentPublishCredentials()))
            add(new Repository("AndroidRelease", "https://android.artifacts.furycloud.io/repository/experimental", new EnvironmentPublishCredentials()))
        }
    }

    static List<Repository> getRepositories() {
        return REPOSITORIES
    }
}
