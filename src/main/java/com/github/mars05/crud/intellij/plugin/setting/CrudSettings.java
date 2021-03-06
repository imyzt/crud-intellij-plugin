package com.github.mars05.crud.intellij.plugin.setting;

import com.github.mars05.crud.intellij.plugin.setting.path.PackagePath;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoyu
 */
@State(name = "CrudSettings", storages = @Storage("crud-plugin.xml"))
public class CrudSettings implements PersistentStateComponent<CrudState> {
    private CrudState myState = new CrudState();

    public static CrudSettings getInstance() {
        return ServiceManager.getService(CrudSettings.class);
    }

    @Nullable
    @Override
    public CrudState getState() {
        return myState;
    }

    @Override
    public void loadState(CrudState state) {
        myState = state;
    }

    public List<Conn> getConns() {
        return myState.getConns();
    }

    public Map<String, PackagePath> getPckConfigs() {
        return myState.getPcgConfigs();
    }
}
