package com.kubernetesmonitor.models;

import com.kubernetesmonitor.watcher.AbstractWatcher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatcherDTO {
    private AbstractWatcher.WATCHER_TYPE type;
    private boolean isActive;
}
