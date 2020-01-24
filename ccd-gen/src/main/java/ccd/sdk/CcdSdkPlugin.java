/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ccd.sdk;

import com.google.common.collect.Lists;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.net.URLClassLoader;

/**
 * A simple 'hello world' plugin.
 */
public class CcdSdkPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class);
        URLClassLoader l = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        SourceSetContainer ssc = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet source = ssc.getByName("main");
        FileCollection deps = source.getRuntimeClasspath().plus(project.files((Object)l.getURLs()));

        JavaExec generate = project.getTasks().create("generateCCDConfig", JavaExec.class);
        generate.setGroup("CCD tasks");
        generate.setClasspath(deps);
        generate.setMain(Main.class.getName());
        generate.dependsOn(project.getTasksByName("compileJava", true));
        CCDConfig config = project.getExtensions().create("ccd", CCDConfig.class);
        config.configDir = project.getBuildDir();
        generate.doFirst(x -> generate.setArgs(Lists.newArrayList(
                config.configDir.getAbsolutePath(),
                config.rootPackage,
                config.caseType
        )));

        project.getDependencies().add("compile", "ccd-sdk:ccd-sdk-types:0.1.8");
        project.getRepositories().mavenCentral();
        project.getRepositories().maven(x -> x.setUrl("https://raw.githubusercontent.com/banderous/ccd/master"));
    }

    static class CCDConfig {
        public File configDir;
        public String rootPackage = "uk.gov.hmcts";
        public String caseType = "";

        public CCDConfig() {
        }
    }
}
