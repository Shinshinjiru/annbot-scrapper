package com.shinshinjiru.annbotscrapper.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

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
public class NewsItem implements Serializable {
    private String thumbnail;
    private String title;
    private String preview;
    private int id;
}
