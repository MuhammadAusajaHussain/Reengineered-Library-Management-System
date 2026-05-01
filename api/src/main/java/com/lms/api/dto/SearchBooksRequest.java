package com.lms.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class SearchBooksRequest {
    @Pattern(regexp = "title|author|subject", message = "searchBy must be: title, author, or subject")
    private String searchBy;

    @NotBlank(message = "query is required")
    private String query;

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
