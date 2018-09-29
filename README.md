# BCommons
Provides reusable and maintainable Spigot components.

### BPlugin

The `JavaPlugin` is extended by the [BPlugin](https://github.com/BradleySteele/BCommons/blob/master/src/main/java/me/bradleysteele/commons/BPlugin.java) 
class and handles pre and post processing of loading, enabling and disabling. An example of how to use the BPlugin class 
can be found below. 
```java
public class ExamplePlugin extends BPlugin {
    
    @Override
    public void enable() {
        this.register(ExampleWorker.class, ExampleCommand.class, TestCommand.class);
    }
}
```

### BCommand
The [BCommand](https://github.com/BradleySteele/BCommons/blob/master/src/main/java/me/bradleysteele/commons/register/command/BCommand.java) 
class provides simple methods for easily creating command executors. It is important to note when using the BCommand class, you do not
have to register commands in your `plugin.yml`. 

```java
public class ExampleCommand extends BCommand {
    
    public ExampleCommand() {
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
