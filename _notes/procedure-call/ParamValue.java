package com.csio.hexagonal.infrastructure.store.persistence.adapter;

/**
 * Small record pairing a procedure parameter definition (enum) with a value for the call.
 * Example usage:
 *   new ParamValue(ExampleProcParam.IN_PARAM, "foo")
 */
public record ParamValue(ExampleProcParam param, Object value) { }
