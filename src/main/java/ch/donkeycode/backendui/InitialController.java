package ch.donkeycode.backendui;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class InitialController {

    private final List<Resource> resources;

    @SneakyThrows
    public InitialController() {

        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        this.resources = List.of(resolver.getResources("classpath*:static/*"));

        LOG.info("Found the following ressources: " + resources.stream()
                .map(Resource::getFilename)
                .collect(Collectors.joining("\n")));
    }

    @SneakyThrows
    @GetMapping("*")
    public String getHtml(HttpServletRequest request) {
        LOG.info("HTML requested over path {}", request.getRequestURI());

        val filename = Optional.ofNullable(request.getRequestURI())
                .map(s -> s.replace("/", ""))
                .filter(s -> !s.isBlank())
                .orElseGet(() -> {
                    LOG.info("No file defined in request, returned index.html as default.");
                    return "index.html";
                });

        val file = resources.stream()
                .filter(resource -> resource.getFilename().equals(filename))
                .findAny();

        return file.flatMap(resource -> {
                    try (InputStream is = resource.getInputStream()) {
                        return Optional.of(new String(is.readAllBytes()));
                    }
                    catch (IOException e) {
                        LOG.error("Failed to read resource {}", resource.getFilename());
                        return Optional.empty();
                    }
                })
                .orElseThrow(() -> {
                    LOG.error("No file for request URI {} found. Used file name: {} Available ressources are: {}",
                            request.getRequestURI(),
                            filename,
                            resources.stream()
                                    .map(Resource::getFilename)
                                    .collect(Collectors.joining(", "))
                    );
                    return new IllegalArgumentException();
                });
    }
}
