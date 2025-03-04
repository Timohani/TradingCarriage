package org.timowa.megabazar.dto.mapper;

public interface Mapper<F, T> {
    T map(F fromObject);
}
