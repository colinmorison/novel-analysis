package com.colin.novelanalysis.infrastructure.parser;

import com.colin.novelanalysis.domain.model.Novel;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Word DOCX 小说解析器
 */
@Component
public class DocxParser extends AbstractNovelParser {

    @Override
    public boolean supports(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".docx");
    }

    @Override
    public List<ChapterContent> parse(Novel novel, InputStream inputStream) {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            String text = document.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .collect(Collectors.joining("\n"));
            return splitChapters(text);
        } catch (Exception e) {
            throw new RuntimeException("DOCX 解析失败", e);
        }
    }
}
