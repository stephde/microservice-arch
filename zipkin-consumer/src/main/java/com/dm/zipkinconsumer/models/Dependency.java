package com.dm.zipkinconsumer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dependency {
    private String parent;
    private String child;
    private Integer callCount;
    private Integer errorCount;
}
