package org.timowa.megabazar.mapper;

public interface Mapper<F, T> {
    T map(F fromObject);
}
