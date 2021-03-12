package com.mercadolibre.android.gradle.base.publish

class RepositoryProvider {
    private static final List<Repository> REPOSITORIES = new ArrayList<Repository>() {
        {
            add(new Repository("AndroidPublic", "https://android-test.artifacts.furycloud.io/repository/internal", new EnvironmentPublishCredentials()))
            add(new Repository("AndroidExperimental", "https://android-test.artifacts.furycloud.io/repository/internal", new EnvironmentPublishCredentials()))
            add(new Repository("AndroidReleases", "https://android-test.artifacts.furycloud.io/repository/internal", new EnvironmentPublishCredentials()))
        }
    }

    static List<Repository> getRepositories() {
        return REPOSITORIES
    }
}
