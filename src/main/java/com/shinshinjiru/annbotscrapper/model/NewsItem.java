package com.shinshinjiru.annbotscrapper.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * News Item model.
 * ================
 *
 * Represents an item from ANN's homepage.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Data
@Builder
public class NewsItem {
    private String thumbnail;
    private String title;
    private String preview;
    private int id;
}
