package com.csio.hexagonal.infrastructure.store.persistence.adapter;

/**
 * A small record pairing a StoredProcedureParam (enum constant) with its runtime value.
 * Usage:
 *   new ParamValue(ExampleProcParam.IN_SEARCH, "foo")
 */
public record ParamValue(StoredProcedureParam param, Object value) { }
