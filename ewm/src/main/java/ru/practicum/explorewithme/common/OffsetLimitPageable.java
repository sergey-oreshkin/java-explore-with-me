package ru.practicum.explorewithme.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.explorewithme.exception.ValidationException;

import java.util.Optional;

public class OffsetLimitPageable implements Pageable {

    public static final int DEFAULT_PAGE_SIZE = 500;

    public static final Sort DEFAULT_SORT = Sort.unsorted();
    private final int offset;
    private final int limit;
    private final Sort sort;

    protected OffsetLimitPageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public static Pageable of(Integer from, Integer size) {
        return of(from, size, DEFAULT_SORT);
    }

    public static Pageable of(Integer from, Integer size, Sort sort) {
        if (from == null && size == null) {
            from = 0;
            size = DEFAULT_PAGE_SIZE;
        }
        size = Optional.ofNullable(size).orElse(0);
        from = Optional.ofNullable(from).orElse(0);
        if (size < 1 || from < 0) {
            throw new ValidationException("From must be positive and size must be more then 0");
        }
        return new OffsetLimitPageable(from, size, sort);
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitPageable(offset + limit, limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return new OffsetLimitPageable(offset, limit, sort);
    }

    @Override
    public Pageable first() {
        return new OffsetLimitPageable(offset, limit, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetLimitPageable(offset + limit * pageNumber, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
