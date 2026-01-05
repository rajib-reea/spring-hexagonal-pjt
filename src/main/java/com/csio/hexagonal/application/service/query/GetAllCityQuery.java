package com.csio.hexagonal.application.service.query;

public record GetAllCityQuery(int page, int size, String search, String sort) {

    public static GetAllCityQuery fromRequest(int page, int size, String search, String sort) {
        return new GetAllCityQuery(page, size, search, sort);
    }
}
