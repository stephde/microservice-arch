package com.dm.zipkinconsumer.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DependeciesResponse {
    private List<Dependency> dependencies;
}
