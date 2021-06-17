import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;


public class OSMClientArgumentProvider implements ArgumentsProvider {


    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(new pt.av.it.SimpleDriver.OSMClient("https://192.168.100.2:9999","admin", "admin","admin","emu-vim1")).map(Arguments::of);
    }
}
