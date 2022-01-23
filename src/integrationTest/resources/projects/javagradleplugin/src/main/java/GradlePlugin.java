
import org.gradle.api.Project;
import org.gradle.api.Plugin;

public class GradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getTasks().register("greeting", task -> {
            task.doLast(s -> System.out.println("Hello from AppPlugin"));
        });
    }
}
    /*plugins {
    `java-gradle-plugin`
    }*/
