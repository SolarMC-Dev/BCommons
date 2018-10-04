/*
 * Copyright 2018 Bradley Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.bradleysteele.commons;

import com.google.common.collect.Sets;
import me.bradleysteele.commons.register.Registrable;
import me.bradleysteele.commons.resource.DefaultResourceProvider;
import me.bradleysteele.commons.resource.ResourceProvider;
import me.bradleysteele.commons.resource.handler.YamlResourceHandler;
import me.bradleysteele.commons.util.logging.ConsoleLog;
import me.bradleysteele.commons.util.logging.StaticLog;
import me.bradleysteele.commons.util.reflect.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The {@link BPlugin} is responsible for loading and enabling components
 * within the library.
 *
 * @author Bradley Steele
 */
public class BPlugin extends JavaPlugin {

    /**
     * A set containing all of the registered objects within the plugin.
     */
    private final Set<Registrable> registers = Sets.newHashSet();

    protected PluginDescriptionFile description;
    protected ResourceProvider resourceProvider;
    protected final ConsoleLog console = new ConsoleLog();

    @Override
    public final void onLoad() {
        description = getDescription();
        resourceProvider = new DefaultResourceProvider(this);
        resourceProvider.addResourceHandler(new YamlResourceHandler());

        console.setFormat("[&6" + (description.getPrefix() != null ? description.getPrefix() : description.getName())
                + "&r] [{bcommons_log_level}]: {bcommons_log_message}");

        execute(new StateExecutor(StateType.LOAD) {

            @Override
            public void execute() {
                load();
            }
        });
    }

    @Override
    public final void onEnable() {
        execute(new StateExecutor(StateType.ENABLE) {

            @Override
            public void execute() {
                enable();
            }
        });
    }

    @Override
    public final void onDisable() {
        execute(new StateExecutor(StateType.DISABLE) {

            @Override
            public void execute() {
                // Unregister registrables before disabling.
                registers.forEach(Registrable::onUnregister);

                disable();
            }
        });
    }

    private void execute(StateExecutor executor) {
        try {
            executor.execute();
        } catch (Exception e) {
            StaticLog.error("Failed to execute plugin in state &c" + executor.type.name() + "&r, exception was thrown:");
            StaticLog.exception(e);

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    // Semi abstract

    public void load() {}

    public void enable() {}

    public void disable() {}

    /**
     * @param registrable the registrable object to register.
     */
    public void register(Registrable registrable) {
        if (registrable == null) {
            console.error("Failed to register registrable object: &cnull&r.");
            return;
        }

        // Inject the plugin.
        Reflection.setFieldValue("plugin", registrable, this);

        try {
            registrable.register();
        } catch (Exception e) {
            console.error("Failed to register registrable object: &c" + registrable.getClass().getSimpleName() + "&r.");
            console.exception(e);
            return;
        }

        // We assume that the register has successfully registered as
        // no exception was thrown.
        registers.add(registrable);

        // Finally, call the overridable onRegister method.
        registrable.onRegister();
    }

    /**
     * @param clazz the registrable class to register.
     */
    public void register(Class<? extends Registrable> clazz) {
        Registrable registrable;

        if (Reflection.isSingleton(clazz)) {
            registrable = Reflection.getSingleton(clazz);
        } else {
            registrable = Reflection.newInstance(clazz);
        }

        if (registrable != null) {
            register(registrable);
        } else {
            console.error("Failed to register registrable class: &ccould not create an instance&r.");
        }
    }

    /**
     * The provided object can either be an implementation of {@link Registrable}
     * or a class. In the case of it being a class, a new instance will be created
     * unless a singleton.
     *
     * @param object the object to register.
     *
     * @see Reflection#isSingleton(Class)
     */
    @SuppressWarnings("unchecked")
    public void register(Object object) {
        if (Registrable.class.isInstance(object)) {
            register((Registrable) object);
        } else if (object instanceof Class<?>) {
            Class<?> clazz = (Class<?>) object;

            if (Registrable.class.isAssignableFrom(clazz)) {
                register((Class<? extends Registrable>) clazz);
            } else {
                console.error("Failed to register &c" + clazz.getSimpleName() + " &ras it does not implement &eRegistrable&r.");
            }
        } else {
            console.error("Failed to register &c" + object.getClass().getSimpleName() +"&r: unknown object.");
        }
    }

    /**
     * @param objects iterable of objects to register.
     */
    public void register(Iterable<Object> objects) {
        objects.forEach(this::register);
    }

    /**
     * @param objects array of objects to register.
     */
    public void register(Object... objects) {
        Stream.of(objects).forEach(this::register);
    }

    /**
     * @return an unmodifiable set containing all of the registered registers.
     */
    public Set<Registrable> getRegisters() {
        return Collections.unmodifiableSet(registers);
    }

    /**
     * @return the plugin's resource provider.
     */
    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    /**
     * @return the plugin's console logger.
     */
    public ConsoleLog getConsole() {
        return console;
    }

    /**
     * @param resourceProvider the plugin's resource provider.
     */
    public void setResourceProvider(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    // State Execution

    private enum StateType {
        LOAD,
        ENABLE,
        DISABLE
    }

    private abstract class StateExecutor {

        private final StateType type;

        StateExecutor(StateType type) {
            this.type = type;
        }

        /**
         * Executes code while catching any exceptions.
         *
         * @throws Exception
         */
         abstract void execute() throws Exception;

    }
}