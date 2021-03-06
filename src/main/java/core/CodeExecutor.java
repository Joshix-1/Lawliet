package core;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CodeExecutor {

    private final String CODE_BASE = "import commands.*;\n" +
            "import constants.*;\n" +
            "import core.*;\n" +
            "import modules.*;\n" +
            "import mysql.*;\n" +
            "import websockets.*;\n" +
            "import org.javacord.api.*;\n" +
            "public class CodeRuntime {\n" +
            "    public static int run() {\n" +
            "        %s\n" +
            "        return 0;\n" +
            "    }\n" +
            "}";

    private String readCode(String sourcePath) throws FileNotFoundException {
        InputStream stream = new FileInputStream(sourcePath);
        String separator = System.getProperty("line.separator");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().collect(Collectors.joining(separator));
    }

    private Path saveSource(String source) throws IOException {
        String tmpProperty = System.getProperty("java.io.tmpdir");
        Path sourcePath = Paths.get(tmpProperty, "CodeRuntime.java");
        Files.write(sourcePath, source.getBytes(UTF_8));
        return sourcePath;
    }

    private Path compileSource(Path javaFile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, javaFile.toFile().getAbsolutePath());
        return javaFile.getParent().resolve("CodeRuntime.class");
    }

    private int runClass(Path javaClass)
            throws MalformedURLException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        URL classUrl = javaClass.getParent().toFile().toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
        Class<?> clazz = Class.forName("CodeRuntime", true, classLoader);
        return (int)clazz.getMethod("run").invoke(null);
    }

    public int evalFile(String filename) throws Exception {
        String source = readCode(filename);
        Path javaFile = saveSource(source);
        Path classFile = compileSource(javaFile);
        return runClass(classFile);
    }

    public int eval(String code) throws Exception {
        String source = String.format(CODE_BASE, code);
        Path javaFile = saveSource(source);
        Path classFile = compileSource(javaFile);
        return runClass(classFile);
    }

}

