package dev.azdanov.pagination.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationUtilsTest {

    @Test
    void getPageItemsWithEmptyPage() {
        PagedModel<Integer> pageModel = createPagedModel(10, 0, 0);
        List<PageItem> result = PaginationUtils.getPageItems(pageModel.getMetadata());

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getPageItemsWithoutEllipsisTestCases")
    void getPageItemsWithoutEllipsis(int pageSize, int pageNumber, int totalElements, List<Integer> expectedPageNumbers) {
        PagedModel<Integer> pageModel = createPagedModel(pageSize, pageNumber, totalElements);
        List<PageItem> result = PaginationUtils.getPageItems(pageModel.getMetadata());

        assertThat(result).hasSize(expectedPageNumbers.size())
            .extracting(PageItem::pageNumber)
            .containsExactly(expectedPageNumbers.toArray(Integer[]::new));
        assertThat(result).extracting(PageItem::isEllipsis)
            .containsOnly(false);
    }

    private static Stream<Arguments> getPageItemsWithoutEllipsisTestCases() {
        return Stream.of(
            Arguments.of(5, 0, 5, List.of(1)),
            Arguments.of(5, 0, 10, List.of(1, 2)),
            Arguments.of(5, 0, 15, List.of(1, 2, 3)),
            Arguments.of(5, 0, 20, List.of(1, 2, 3, 4)),
            Arguments.of(5, 0, 25, List.of(1, 2, 3, 4, 5)),
            Arguments.of(5, 0, 30, List.of(1, 2, 3, 4, 5, 6)),
            Arguments.of(5, 0, 35, List.of(1, 2, 3, 4, 5, 6, 7))
        );
    }

    @Test
    void getPageItemsWithRightEllipsis() {
        PagedModel<Integer> pageModel = createPagedModel(10, 0, 100);
        List<PageItem> result = PaginationUtils.getPageItems(pageModel.getMetadata());

        int[] expectedPageNumbers = new int[]{1, 2, 3, 4, 5, 6, 10};
        assertThat(result).hasSize(expectedPageNumbers.length)
            .extracting(PageItem::pageNumber)
            .containsExactly(IntStream.of(expectedPageNumbers).boxed().toArray(Integer[]::new));
        assertThat(result).extracting(PageItem::isEllipsis)
            .containsExactly(false, false, false, false, false, true, false);
    }

    @Test
    void getPageItemsWithBothEllipses() {
        PagedModel<Integer> pageModel = createPagedModel(10, 4, 100);
        List<PageItem> result = PaginationUtils.getPageItems(pageModel.getMetadata());

        int[] expectedPageNumbers = new int[]{1, 3, 4, 5, 6, 7, 10};
        assertThat(result).hasSize(expectedPageNumbers.length)
            .extracting(PageItem::pageNumber)
            .containsExactly(IntStream.of(expectedPageNumbers).boxed().toArray(Integer[]::new));
        assertThat(result).extracting(PageItem::isEllipsis)
            .containsExactly(false, true, false, false, false, true, false);
    }

    @Test
    void getPageItemsWithLeftEllipsis() {
        PagedModel<Integer> pageModel = createPagedModel(10, 7, 100);
        List<PageItem> result = PaginationUtils.getPageItems(pageModel.getMetadata());

        int[] expectedPageNumbers = new int[]{1, 5, 6, 7, 8, 9, 10};
        assertThat(result).hasSize(expectedPageNumbers.length)
            .extracting(PageItem::pageNumber)
            .containsExactly(IntStream.of(expectedPageNumbers).boxed().toArray(Integer[]::new));
        assertThat(result).extracting(PageItem::isEllipsis)
            .containsExactly(false, true, false, false, false, false, false);
    }

    private static PagedModel<Integer> createPagedModel(int pageSize, int pageNumber, int totalElements) {
        List<Integer> items = IntStream.range(0, totalElements).boxed().toList();
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        Page<Integer> page = new PageImpl<>(items, pageable, items.size());
        return new PagedModel(page);
    }
}
