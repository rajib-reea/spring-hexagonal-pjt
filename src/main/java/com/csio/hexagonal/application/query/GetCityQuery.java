//package com.csio.hexagonal.application.query;
//import java.util.UUID;
//
///**
// * Read-only query object
// * No validation logic here (simple data carrier)
// */
//public record GetCityQuery(UUID uid) {
//}
package com.csio.hexagonal.application.query;

/**
 * Read-only query object
 * No validation logic here (simple data carrier)
 */
public record GetCityQuery(java.util.UUID uid) {}
