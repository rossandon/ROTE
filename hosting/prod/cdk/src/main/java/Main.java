import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class Main {
    public static void main(final String[] args) {
        App app = new App();

        new RoteStack(app, "Rote", StackProps.builder().build());

        app.synth();
    }
}
