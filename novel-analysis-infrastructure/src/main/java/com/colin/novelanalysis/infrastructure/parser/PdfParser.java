package com.colin.novelanalysis.infrastructure.parser;

import com.colin.novelanalysis.domain.model.Novel;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * PDF 小说解析器
 */
@Component
public class PdfParser extends AbstractNovelParser {

    @Override
    public boolean supports(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }

    @Override
    public List<ChapterContent> parse(Novel novel, InputStream inputStream) {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return splitChapters(text);
        } catch (Exception e) {
            throw new RuntimeException("PDF 解析失败", e);
        }
    }
}
