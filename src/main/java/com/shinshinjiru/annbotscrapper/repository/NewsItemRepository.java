package com.shinshinjiru.annbotscrapper.repository;

import com.shinshinjiru.annbotscrapper.model.NewsItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * News Item repository.
 * =====================
 *
 * Repository for the NewsItem value in redis.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Repository
public interface NewsItemRepository extends CrudRepository<NewsItem, Integer> {
}
