package com.colin.novelanalysis.infrastructure.parser;

import com.colin.novelanalysis.domain.model.Novel;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * EPUB 小说解析器
 */
@Component
public class EpubParser extends AbstractNovelParser {

    @Override
    public boolean supports(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".epub");
    }

    @Override
    public List<ChapterContent> parse(Novel novel, InputStream inputStream) {
        try {
            Book book = new EpubReader().readEpub(inputStream);
            List<ChapterContent> chapters = new ArrayList<>();
            List<Resource> contents = book.getTableOfContents().getAllUniqueResources();
            if (contents.isEmpty()) {
                contents = book.getSpine().getSpineReferences().stream()
                        .map(r -> r.getResource()).toList();
            }

            int chapterNo = 1;
            long startPos = 0;
            for (Resource resource : contents) {
                String html = new String(resource.getData(), resource.getInputEncoding());
                Document doc = Jsoup.parse(html);
                String text = doc.text();
                if (StringUtils.isBlank(text)) {
                    continue;
                }
                String title = resource.getTitle();
                if (StringUtils.isBlank(title)) {
                    title = "第" + chapterNo + "章";
                }
                long endPos = startPos + text.length();
                chapters.add(new ChapterContent(chapterNo, title, text.trim(), startPos, endPos));
                chapterNo++;
                startPos = endPos;
            }
            return chapters;
        } catch (Exception e) {
            throw new RuntimeException("EPUB 解析失败", e);
        }
    }
}
