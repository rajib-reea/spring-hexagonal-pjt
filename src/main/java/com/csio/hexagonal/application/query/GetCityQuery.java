package com.csio.hexagonal.application.query;

/**
 * Read-only query object
 * No validation logic here (simple data carrier)
 */
public record GetCityQuery(String uid) {
}
