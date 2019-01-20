package com.dm.zipkinconsumer.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Dependency {

    private String parent;
    private String child;
    private Integer callCount;
    private Integer errorCount;
}
