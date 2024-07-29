# Configuration ![GitHub Issues or Pull Requests](https://img.shields.io/github/issues/Toma1O6/Configuration?style=for-the-badge&color=red) ![CurseForge Downloads](https://img.shields.io/curseforge/dt/444699?style=for-the-badge&color=blue) ![Discord](https://img.shields.io/discord/799354846125096960?style=for-the-badge&link=https%3A%2F%2Fdiscord.gg%2FWEFYxwS8E3&label=Discord)
Library mod which aims to simplify config creation for mod developers while also being
easy to use for general users.

For developers this is achieved by collection of comprehensive set of annotations to be used on your config fields.
Allows you to specify which values can be entered, automatically synchronizes values to clients when desired while also
automatically reloads changes from disk, manages edit permissions and more. All of these are completely optional and
can be enabled for desired fields.

For users config UI is automatically generated with simple controls and user-friendly UX.

## User installation
Installation is simple as any other mod - download `.jar` file from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/configuration/files/)
for correct Minecraft version and drop the `.jar` file into `mods` directory.

## For developers
### Project setup
<span style="color:#AA0000">**This setup has been made for 3.+ versions, for older releases you must use curseforge maven repository**</span><br>
In the following examples replace the `project.minecraft_version` and `project.configuration_version` by your used properties.

- ![1.21](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepsy.io%2Fmvn%2Ftoma%2Fpublic%2Fdev%2Ftoma%2Fconfiguration%2Fconfiguration-1.21%2Fmaven-metadata.xml&versionSuffix=-common&label=1.21)


First add maven repository into your root project `build.gradle` and into all subprojects
```groovy
repositories {
    maven {
        name 'Configuration'
        url 'https://repsy.io/mvn/toma/public/'
    }
}
```

**(This step is relevant only for multiloader setups)** Now add relevant `compileOnly` dependency for each subproject, starting with `common` module - `/common/build.gradle`
```groovy
dependencies {
    // Configuration library
    compileOnly "dev.toma.configuration:configuration-${project.minecraft_version}:${project.configuration_version}-common"
}
```
And now for each subproject (we will use Neoforge in this example, but the same applies for the remaning mod loaders) -
`neoforge/build.gradle`
```groovy
dependencies {
    // Configuration library
    implementation "dev.toma.configuration:configuration-${project.minecraft_version}:${project.configuration_version}-neoforge"
}
```
Then repeat the same process for each subproject, but do not forget to change the version suffix to relevant mod loader.

### Config registration
Declare your config class and annotate it with `@Config` annotation
```java
@Config(id = "my_config_id")
public final class MyConfig {}
```
Now you need to register the config - inside your mod constructor call the `registerConfig` method
```java
public class MyMod {
    
    public static MyConfig config;
    
    public MyMod() {
        // You can also use JSON/PROPERTIES config formats. More types may be supported in the future if needed
        ConfigHolder<MyConfig> configHolder = Configuration.registerConfig(MyConfig.class, ConfigFormats.YAML);
        config = configHolder.getConfigInstance();
        // You can use the config holder to declare custom value descriptions etc.
    }
}
```
And now we can start adding configurable parameters
```java
@Config(id = "my_config_id")
public final class MyConfig {

    // All values have to be non-final instance fields!

    @Configurable
    @Configurable.Comment(value = "Random boolean value", localize = true)
    public boolean randomBool = false; // The assigned value is treated as default value

    @Configurable
    @Configurable.Comment(value = "This value will be synchronized to all clients", localize = true)
    @Configurable.Synchronized // sent to all clients on log in, value cannot be modified by player while playing on server
    @Configurable.Gui.Slider // render value as slider in GUI
    public int synchronizedIntValue = 132;
    
    @Configurable
    @Configurable.Comment(value = "This value will be updated only after game restart", localize = true)
    @Configurable.UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    public String text = "Text";
    
    @Configurable
    public SubCategory subCategory = new SubCategory();
    
    public static class SubCategory {
        
        // The key attribute allows you to specify how translation key will be generated for this field.
        // SHORT - Only the field name will be used for translation key => config.my_config_id.option.subcategoryNumber
        // FULL - All parent field names will be used as prefix => config.my_config_id.option.subSategory.subcategoryNumber
        // So it makes sense to only use this on nested config fields, otherwise the behaviour will be the same
        // However in most cases using the default SHORT value should be sufficient
        @Configurable(key = Configurable.LocalizationKey.FULL)
        @Configurable.Range(min = 0, max = 9) // Only values between 0-9 (inclusive) are allowed
        @Configurable.Comment("This value is within subcategory. Use this for config value grouping and organization") // Non-translated comment
        public int subcategoryNumber = 1;
    }
}
```
For more advanced topics and full annotation list visit the [wiki](https://github.com/Toma1O6/Configuration/wiki)