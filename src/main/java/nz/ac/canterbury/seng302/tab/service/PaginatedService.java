package nz.ac.canterbury.seng302.tab.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

/**
 * Base abstract class for providing pagination functionality in service classes
 *
 * @param <T> the type of object being paginated
 */
public interface PaginatedService<T> {

    /**
     * Searches the linked repository of this service for all items that match
     * the given query
     *
     * @param query The query to search by
     * @return Returns the list of items that match the query
     */
    List<T> search(String query);

    /**
     * Filters the linked repository of this service for all items that match
     * @param query The query to filter by
     * @return Returns the list of items that match the query
     */
    List<T> filterSports(List<T> items, String query);
    
    /**
     * Filters the linked repository of this service for all items that match
     * @param query The query to filter by
     * @return Returns the list of items that match the query
     */
    List<T> filterCities(List<T> items, String query);


    /**
     * Abstract method to be implemented by subclasses to return a paginated list of objects.
     *
     * @param pageable the Pageable object containing the pagination parameters
     * @return a Page object containing the paginated list of objects
     */
    default Page<T> findPaginated(Pageable pageable, String nameFilter, String sportsFilter, String cityFilter) {
        return paginateResult(pageable, this.filterCities(this.filterSports(this.search(nameFilter), sportsFilter), cityFilter));
    }

    /**
     * Splits a list of items into a paginate page
     *
     * @param pageable the Pageable object containing the pagination parameters
     * @param items    The items to paginate
     * @return a Page object containing the paginated list of items
     */
    private static <T> Page<T> paginateResult(Pageable pageable, List<T> items) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<T> itemsList;
        if (items.size() < startItem) {
            itemsList = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, items.size());
            itemsList = items.subList(startItem, toIndex);
        }

        return new PageImpl<>(itemsList, PageRequest.of(currentPage, pageSize), items.size());
    }
}
