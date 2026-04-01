package com.chxt.domain.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Pdf {

    private final byte[] data;

    private List<String> lines;
    private List<List<String>> linesWithPage;

    public Pdf(byte[] data) {
        this.data = Objects.requireNonNull(data, "pdf data must not be null");
    }

    public List<String> lines() {
        if (lines != null) {
            return lines;
        }

        try (PDDocument document = PDDocument.load(data)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            lines = Arrays.stream(text.split("\\R"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());
            return lines;
        } catch (IOException e) {
            throw new IllegalStateException("Parse PDF failed", e);
        }
    }

    public List<List<String>> linesWithPage() {
        if (linesWithPage != null) {
            return linesWithPage;
        }

        try (PDDocument document = PDDocument.load(data)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int pageCount = document.getNumberOfPages();
            List<List<String>> result = new ArrayList<>(pageCount);
            for (int page = 1; page <= pageCount; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String text = stripper.getText(document);
                List<String> pageLines = Arrays.stream(text.split("\\R"))
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.toList());
                result.add(pageLines);
            }
            linesWithPage = result;
            return linesWithPage;
        } catch (IOException e) {
            throw new IllegalStateException("Parse PDF failed", e);
        }
    }
}
