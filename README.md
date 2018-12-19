# BCommons
Provides reusable and maintainable Spigot components.
Latest compiled version available [here](https://cdn.bradleysteele.me/BCommons-0.1.8b3-RELEASE).

[![](https://img.shields.io/maven-central/v/me.bradleysteele/commons.svg?label=maven-central)](https://search.maven.org/artifact/me.bradleysteele/commons/) [![](https://img.shields.io/github/license/BradleySteele/BCommons.svg)](https://github.com/BradleySteele/BCommons/blob/master/LICENSE) 


### Dependency
BCommons is hosted on maven's central repository, below are examples of how to include BCommons into your project:

#### Maven
```xml
<dependency>
  <groupId>me.bradleysteele</groupId>
  <artifactId>commons</artifactId>
  <version>VERSION/version>
</dependency>
```

#### Gradle
```
implementation 'me.bradleysteele:commons:VERSION'
```

### BPlugin

The `JavaPlugin` is extended by the [BPlugin](https://github.com/BradleySteele/BCommons/blob/master/src/main/java/me/bradleysteele/commons/BPlugin.java) 
class and handles pre and post processing of loading, enabling and disabling. An example of how to use the BPlugin class 
can be found below. 
```java
public class ExamplePlugin extends BPlugin {
    
    @Override
    public void enable() {
        this.register(
                WorkerExample.class, 
            
                CmdExample.class
        );
    }
}
```

## Registrable

All [Registrable](https://github.com/BradleySteele/BCommons/blob/master/src/main/java/me/bradleysteele/commons/register/Registrable.java) classes must be enabled through [`BPlugin#register`](https://github.com/BradleySteele/BCommons/blob/master/src/main/java/me/bradleysteele/commons/BPlugin.java#L132). Singletons must have a static `get()` or `getInstance()` method returning an instance of the class being registered, otherwise a new instance will be created and registered. Some examples of built-in registrables can be found below. 

### BCommand
The [BCommand](https://github.com/BradleySteele/BCommons/blob/master/src/main/java/me/bradleysteele/commons/register/command/BCommand.java) 
registrable provides simple methods for easily creating command executors. It is important to note when using the BCommand class, you do not
have to register commands in your `plugin.yml`. 

```java
public class CmdExample extends BCommand {
    
    public CmdExample() {
        // Note: the "main" command executor must be in this list too.
        this.setAliases("example", "exmpl", "test", "tst");
        
        // The information when shown when typing '/minecraft:help /example'.
        this.setDescription("Some information about the command.");
        
        // Default: false, allows the command to be ran by the console.
        this.setAllowConsole(true);
        
        this.setPermission("example.permission.node");
        this.setPermissionDenyMessage("&cYou do not have permission.");
    }
    
    // The method executed when the command is ran. Note that this is not fired 
    // if cancelled by the PlayerCommandPreprocessEvent.
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Hello!");
    }
}

```

### BWorker

The [BWorker](https://github.com/BradleySteele/BCommons/blob/master/src/main/java/me/bradleysteele/commons/register/worker/BWorker.java) registrable should be used for event handling and running **repeating** tasks.

```java
public class WorkerExample extends BWorker {

    private boolean canFly;

    public WorkerExample() {
        // Initial delay
        this.setDelay(20);
        
        // Every second (20 ticks)
        this.setPeriod(20);
        
        // Asynchronous
        this.setSync(false); 
    }
    
    @Override
    public void run() {
        canFly = ThreadLocalRandom.current().nextBoolean();
    }
    
    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        event.setCancelled(event.isFlying() && !canFly);
    }
}
```
