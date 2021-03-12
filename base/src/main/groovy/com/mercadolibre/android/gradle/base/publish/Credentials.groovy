package com.mercadolibre.android.gradle.base.publish

import javax.annotation.Nullable

interface Credentials {

    @Nullable
    String getUsername()

    @Nullable
    String getPassword()
}

class EnvironmentPublishCredentials implements Credentials {
    private static final String REPOSITORY_USER_ENV = "REPOSITORY_USER"
    private static final String REPOSITORY_PASSWORD_ENV = "REPOSITORY_PASSWORD"
    private static final String CI = "CI"

    @Override
    String getUsername() {
        return System.getenv(CI) ? System.getenv(REPOSITORY_USER_ENV) : ""
    }

    @Override
    String getPassword() {
        return System.getenv(CI) ? System.getenv(REPOSITORY_PASSWORD_ENV) : ""
    }
}
