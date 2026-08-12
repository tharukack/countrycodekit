package io.github.tharukack.countrycodekit.tools;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

public final class FlagRasterizer {
    private static final int WIDTH = 128;
    private static final int HEIGHT = 96;
    private static final Pattern DIMENSIONLESS_RECT = Pattern.compile(
        "<rect\\b(?=[^>]*?/\\s*>)(?!(?:[^>]*\\s)?width\\s*=)(?!(?:[^>]*\\s)?height\\s*=)[^>]*/\\s*>"
    );
    private static final Pattern ID_ATTRIBUTE = Pattern.compile("\\bid\\s*=\\s*[\"']([^\"']+)[\"']");
    private static final Pattern LOCAL_REFERENCE = Pattern.compile(
        "\\b(fill|stroke|mask|clip-path|filter)\\s*=\\s*[\"']url\\(#([^)]+)\\)[\"']"
    );

    private FlagRasterizer() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: FlagRasterizer INPUT_SVG_DIR OUTPUT_PNG_DIR");
        }

        Path inputDirectory = Path.of(args[0]);
        Path outputDirectory = Path.of(args[1]);
        Files.createDirectories(outputDirectory);

        List<Path> inputs;
        try (var paths = Files.list(inputDirectory)) {
            inputs = paths
                .filter(path -> path.getFileName().toString().endsWith(".svg"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .toList();
        }
        if (inputs.isEmpty()) {
            throw new IllegalStateException("No SVG files found in " + inputDirectory);
        }

        for (Path input : inputs) {
            String outputName = input.getFileName().toString().replaceFirst("\\.svg$", ".png");
            Path output = outputDirectory.resolve(outputName);
            transcode(input, output);
            validate(output);
        }

        System.out.println("Rendered " + inputs.size() + " flags as " + WIDTH + "x" + HEIGHT + " PNG files.");
    }

    private static void transcode(Path input, Path output) throws IOException, TranscoderException {
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) WIDTH);
        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) HEIGHT);
        String svg = Files.readString(input);
        String browserCompatibleSvg = sanitize(svg);
        TranscoderInput source = new TranscoderInput(new StringReader(browserCompatibleSvg));
        source.setURI(input.toUri().toString());
        try (OutputStream stream = Files.newOutputStream(output)) {
            transcoder.transcode(source, new TranscoderOutput(stream));
        }
    }

    private static String sanitize(String svg) {
        String sanitized = DIMENSIONLESS_RECT.matcher(svg).replaceAll("");
        Set<String> ids = new HashSet<>();
        Matcher idMatcher = ID_ATTRIBUTE.matcher(sanitized);
        while (idMatcher.find()) {
            ids.add(idMatcher.group(1));
        }

        Matcher paintMatcher = LOCAL_REFERENCE.matcher(sanitized);
        StringBuffer result = new StringBuffer();
        while (paintMatcher.find()) {
            String replacement = ids.contains(paintMatcher.group(2))
                ? paintMatcher.group()
                : paintMatcher.group(1) + "=\"none\"";
            paintMatcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        paintMatcher.appendTail(result);
        return result.toString();
    }

    private static void validate(Path output) throws IOException {
        BufferedImage image = ImageIO.read(output.toFile());
        if (image == null || image.getWidth() != WIDTH || image.getHeight() != HEIGHT) {
            throw new IllegalStateException("Invalid rendered flag: " + output);
        }
    }
}
