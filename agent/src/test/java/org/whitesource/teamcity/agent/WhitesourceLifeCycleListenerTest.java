package org.whitesource.teamcity.agent;

import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.ServiceNotFoundException;
import jetbrains.buildServer.TeamCityExtension;
import jetbrains.buildServer.agent.impl.AgentEventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

public class WhitesourceLifeCycleListenerTest {

    @Ignore
    @Test
    public void shouldExtractProperties(){
        WhitesourceLifeCycleListener whitesourceLifeCycleListener = new WhitesourceLifeCycleListener(new AgentEventDispatcher(), new ExtensionHolder() {
            @Override
            public <T extends TeamCityExtension> void registerExtension(@NotNull Class<T> aClass, @NotNull String s, @NotNull T t) {

            }

            @Override
            public <T extends TeamCityExtension> void unregisterExtension(@NotNull Class<T> aClass, @NotNull String s) {

            }

            @NotNull
            @Override
            public <T extends TeamCityExtension> Collection<T> getExtensions(@NotNull Class<T> aClass) {
                return null;
            }

            @NotNull
            @Override
            public <T extends TeamCityExtension> Collection<String> getExtensionSources(@NotNull Class<T> aClass) {
                return null;
            }

            @Nullable
            @Override
            public <T extends TeamCityExtension> T getExtension(@NotNull Class<T> aClass, @NotNull String s) {
                return null;
            }

            @Override
            public <T extends TeamCityExtension> void foreachExtension(@NotNull Class<T> aClass, @NotNull ExtensionAction<T> extensionAction) {

            }

            @NotNull
            @Override
            public <T> T getSingletonService(@NotNull Class<T> aClass) throws ServiceNotFoundException {
                return null;
            }

            @Nullable
            @Override
            public <T> T findSingletonService(@NotNull Class<T> aClass) {
                return null;
            }

            @NotNull
            @Override
            public <T> Collection<T> getServices(@NotNull Class<T> aClass) {
                return null;
            }
        });

    }
}
